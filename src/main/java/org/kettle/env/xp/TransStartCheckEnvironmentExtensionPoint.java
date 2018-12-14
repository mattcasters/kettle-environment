package org.kettle.env.xp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.kettle.env.config.EnvironmentConfigSingleton;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentSingleton;
import org.kettle.env.util.Defaults;
import org.kettle.env.util.EnvironmentUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;

@ExtensionPoint(
  id = "TransStartCheckEnvironmentExtensionPoint",
  description = "At the start of a transformation, verify it lives in the active environment",
  extensionPointId = "TransformationPrepareExecution"
)
/**
 * set the debug level right before the step starts to run
 */
public class TransStartCheckEnvironmentExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {

    if (!(object instanceof Trans )) {
      return;
    }

    Trans trans = (Trans) object;

    // What is the active environment?
    //
    String activeEnvironment = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
    if (StringUtils.isEmpty( activeEnvironment )) {
      // Nothing to be done here...
      //
      return;
    }

    try {
        log.logBasic("Validating active environment '"+activeEnvironment+"'");
      Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( activeEnvironment );
      if (environment==null) {
        throw new KettleException( "Active environment '"+activeEnvironment+"' couldn't be found" );
      }

      if (environment.isEnforcingExecutionInHome()) {

        String transFilename = trans.getTransMeta().getFilename();
        if (StringUtils.isNotEmpty( transFilename )) {
          // See that this filename is located under the environment home folder
          //
          String environmentHome = trans.environmentSubstitute(environment.getEnvironmentHomeFolder());

          FileObject envHome = KettleVFS.getFileObject( environmentHome );
          FileObject transFile = KettleVFS.getFileObject( transFilename );
          if (!isInSubDirectory(transFile, envHome)) {
            throw new KettleException( "The transformation file '"+transFilename+"'does live in the configured environment home folder : '"+environmentHome+"'" );
          }
        }

      }
    } catch(Exception e) {
      log.logError("Error transformation execution in active environment '"+activeEnvironment+"'", e);
    }
  }

  private boolean isInSubDirectory( FileObject file, FileObject directory) throws FileSystemException {

    String filePath = file.getName().getPath();
    String directoryPath = file.getName().getPath();

    // Same?
    if ( filePath.equals( directoryPath )) {
      return true;
    }

    FileObject parent = file.getParent();
    if (isInSubDirectory( parent, directory )) {
      return true;
    }
    return false;
  }


}
