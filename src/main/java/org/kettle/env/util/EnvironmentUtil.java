package org.kettle.env.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentSingleton;
import org.kettle.env.session.EnvironmentSession;
import org.kettle.env.session.EnvironmentSessionUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPerspectiveManager;
import org.pentaho.di.ui.spoon.SpoonPluginType;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;
import org.pentaho.metastore.util.PentahoDefaults;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    // Variable system variables but also apply them to variables
    // We'll use those to change the loaded variables in Spoon
    //
    VariableSpace variables = new Variables();
    environment.modifyVariableSpace( variables, true );

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
        int index = delegatingMetaStore.getMetaStoreList().indexOf( metaStore );
        metaStore = MetaStoreConst.openLocalPentahoMetaStore();
        delegatingMetaStore.getMetaStoreList().set( index, metaStore );
        delegatingMetaStore.setActiveMetaStoreName( metaStore.getName() );
      }
    }

    // See if we need to restore the default Spoon session for this environment
    // The name of the session is the name of the environment
    // This will cause the least amount of issues.
    //
    Spoon spoon = Spoon.getInstance();
    if ( spoon != null ) {

      loadSpoonGitRepository( environment );

      // Clear last used, fill it with something useful.
      //
      spoon.variables = new RowMetaAndData();
      for ( String variable : variables.listVariables() ) {
        String value = variables.getVariable( variable );
        if ( !variable.startsWith( Const.INTERNAL_VARIABLE_PREFIX ) ) {
          spoon.variables.addValue( new ValueMetaString( variable ), value );
        }
      }

      if ( environment.isAutoRestoringSpoonSession() ) {
        MetaStoreFactory<EnvironmentSession> sessionFactory = new MetaStoreFactory<>( EnvironmentSession.class, delegatingMetaStore, PentahoDefaults.NAMESPACE );
        EnvironmentSession environmentSession = sessionFactory.loadElement( environment.getName() );
        if ( environmentSession != null ) {
          // Load this one in Spoon
          //
          EnvironmentSessionUtil.restoreSessionInSpoon( environmentSession );
        }
      }


    }
  }

  /**
   * Method copied from Spoon.java because it's private.
   *
   * @param spoon
   * @param vars
   */
  private static void fillVariables( Spoon spoon, RowMetaAndData vars ) {
    TransMeta[] transMetas = spoon.getLoadedTransformations();
    JobMeta[] jobMetas = spoon.getLoadedJobs();
    if ( ( transMetas == null || transMetas.length == 0 ) && ( jobMetas == null || jobMetas.length == 0 ) ) {
      return;
    }

    Properties sp = new Properties();
    sp.putAll( System.getProperties() );

    VariableSpace space = Variables.getADefaultVariableSpace();
    String[] keys = space.listVariables();
    for ( String key : keys ) {
      sp.put( key, space.getVariable( key ) );
    }

    for ( TransMeta transMeta : transMetas ) {
      List<String> list = transMeta.getUsedVariables();
      for ( String varName : list ) {
        String varValue = sp.getProperty( varName, "" );
        if ( vars.getRowMeta().indexOfValue( varName ) < 0 && !varName.startsWith( Const.INTERNAL_VARIABLE_PREFIX ) ) {
          vars.addValue( new ValueMetaString( varName ), varValue );
        }
      }
    }

    for ( JobMeta jobMeta : jobMetas ) {
      List<String> list = jobMeta.getUsedVariables();
      for ( String varName : list ) {
        String varValue = sp.getProperty( varName, "" );
        if ( vars.getRowMeta().indexOfValue( varName ) < 0 && !varName.startsWith( Const.INTERNAL_VARIABLE_PREFIX ) ) {
          vars.addValue( new ValueMetaString( varName ), varValue );
        }
      }
    }
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

    VariableSpace space = Variables.getADefaultVariableSpace();

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
      LogChannel.UI.logError( "Unable to open GitSpoon project '"+realRepoName+"'", e);
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
