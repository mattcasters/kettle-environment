package org.kettle.env.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentSingleton;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPerspectiveManager;
import org.pentaho.di.ui.spoon.SpoonPluginType;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentUtil {

  public static final String VARIABLE_ENVIRONMENT_HOME = "ENVIRONMENT_HOME";
  public static final String VARIABLE_DATASETS_BASE_PATH = "DATASETS_BASE_PATH";
  public static final String VARIABLE_UNIT_TESTS_BASE_PATH = "UNIT_TESTS_BASE_PATH";


  /**
   * Enable the specified environment
   * Force reload of a number of settings
   *
   * @param environment
   * @param delegatingMetaStore
   * @throws KettleException
   * @throws MetaStoreException
   */
  public static void enableEnvironment( Environment environment, DelegatingMetaStore delegatingMetaStore ) throws KettleException, MetaStoreException {

    environment.modifySystem();

    // Create Kettle home folder in case it doesn't exist
    //
    KettleClientEnvironment.createKettleHome();

    // Force reload of the Kettle environment
    //
    EnvUtil.environmentInit();

    // Initialize the logging back-end.
    //
    KettleLogStore.init();

    // Restart the environment
    //
    KettleEnvironment.shutdown();
    KettleEnvironment.init();

    // Modify local loaded metastore...
    //
    if ( delegatingMetaStore != null ) {
      IMetaStore metaStore = delegatingMetaStore.getMetaStore( Const.PENTAHO_METASTORE_NAME );
      if ( metaStore != null ) {
        System.out.println( "Found metastore '" + metaStore.getName() + "'" );
        int index = delegatingMetaStore.getMetaStoreList().indexOf( metaStore );
        metaStore = MetaStoreConst.openLocalPentahoMetaStore();
        delegatingMetaStore.getMetaStoreList().set( index, metaStore );
        delegatingMetaStore.setActiveMetaStoreName( metaStore.getName() );
      }
    }

    loadSpoonGitRepository( environment );


  }

  /**
   * Finally, if we're running in Spoon and if the
   * GitSpoonPlugin is loaded, load the specified repo
   *
   * @param environment
   */
  public static void loadSpoonGitRepository( Environment environment ) throws KettleException {
    Spoon spoon = Spoon.getInstance();
    if ( spoon == null ) {
      return;
    }

    PluginInterface gitSpoonPlugin = PluginRegistry.getInstance().getPlugin( SpoonPluginType.class, "GitSpoonPlugin" );
    if ( gitSpoonPlugin == null ) {
      return;
    }

    String repoName = environment.getSpoonGitProject();
    if ( StringUtils.isEmpty( repoName ) ) {
      return;
    }

    VariableSpace space = new Variables();
    space.initializeVariablesFrom( null );

    String realRepoName = space.environmentSubstitute( repoName );
    try {
      SpoonPerspectiveManager perspectiveManager = SpoonPerspectiveManager.getInstance();
      for ( SpoonPerspective spoonPerspective : perspectiveManager.getPerspectives() ) {
        if ( spoonPerspective.getId().equals( "010-git" ) ) {

          // This is the one!
          Method loadRepoMethod = spoonPerspective.getClass().getMethod( "openRepository", String.class );
          loadRepoMethod.invoke( spoonPerspective, realRepoName );

          break;
        }
      }
    } catch ( Exception e ) {
      throw new KettleException( "Unable to load git repository", e );
    }

  }

  public static List<String> getGitRepositoryNames() throws KettleException {
    SpoonPerspectiveManager perspectiveManager = SpoonPerspectiveManager.getInstance();
    for ( SpoonPerspective spoonPerspective : perspectiveManager.getPerspectives() ) {
      if ( spoonPerspective.getId().equals( "010-git" ) ) {

        // This is the one!
        try {
          Method getRepoNamesMethod = spoonPerspective.getClass().getMethod( "getRepoNames", new Class<?>[] {} );
          return (List<String>) getRepoNamesMethod.invoke( spoonPerspective, new Object[] {} );
        } catch ( Exception e ) {
          throw new KettleException( "Unable to list Git Repository names", e );
        }
      }
    }
    return new ArrayList<>();
  }

  public static void validateFileInEnvironment( LogChannelInterface log, String transFilename, Environment environment, VariableSpace space ) throws KettleException, FileSystemException {
    if ( StringUtils.isNotEmpty( transFilename ) ) {
      // See that this filename is located under the environment home folder
      //
      String environmentHome = space.environmentSubstitute( environment.getEnvironmentHomeFolder() );
      log.logBasic( "Validation against environment home : " + environmentHome );

      FileObject envHome = KettleVFS.getFileObject( environmentHome );
      FileObject transFile = KettleVFS.getFileObject( transFilename );
      if ( !isInSubDirectory( transFile, envHome ) ) {
        throw new KettleException( "The transformation file '" + transFilename + "' does not live in the configured environment home folder : '" + environmentHome + "'" );
      }
    }
  }

  private static boolean isInSubDirectory( FileObject file, FileObject directory ) throws FileSystemException {

    String filePath = file.getName().getPath();
    String directoryPath = directory.getName().getPath();

    // Same?
    if ( filePath.equals( directoryPath ) ) {
      System.out.println( "Found " + filePath + " in directory " + directoryPath );
      return true;
    }

    FileObject parent = file.getParent();
    if ( parent != null && isInSubDirectory( parent, directory ) ) {
      return true;
    }
    return false;
  }

  public static void validateFileInEnvironment( LogChannelInterface log, String executableFilename, VariableSpace space ) throws KettleException, FileSystemException, MetaStoreException {

    if ( StringUtils.isEmpty( executableFilename ) ) {
      // Repo or remote
      return;
    }

    // What is the active environment?
    //
    String activeEnvironment = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
    if ( StringUtils.isEmpty( activeEnvironment ) ) {
      // Nothing to be done here...
      //
      return;
    }

    log.logBasic( "Validating active environment '" + activeEnvironment + "'" );
    Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( activeEnvironment );
    if ( environment == null ) {
      throw new KettleException( "Active environment '" + activeEnvironment + "' couldn't be found. Fix your setup." );
    }

    if ( environment.isEnforcingExecutionInHome() ) {
      EnvironmentUtil.validateFileInEnvironment( log, executableFilename, environment, space );
    }
  }
}
