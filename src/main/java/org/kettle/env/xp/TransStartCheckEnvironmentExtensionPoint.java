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
import org.pentaho.di.core.variables.VariableSpace;
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

    String transFilename = trans.getFilename();

    try {
      EnvironmentUtil.validateFileInEnvironment( log, transFilename, (VariableSpace) trans );
    } catch ( Exception e ) {
      throw new KettleException( "Validation error against transformation '" + transFilename + "' in active environment", e );
    }
  }

}
