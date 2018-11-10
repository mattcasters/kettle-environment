package org.kettle.xp;

import org.apache.commons.lang.StringUtils;
import org.kettle.env.Environment;
import org.kettle.env.EnvironmentSingleton;
import org.kettle.env.EnvironmentsDialog;
import org.kettle.util.Defaults;
import org.kettle.util.EnvironmentUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.List;

@ExtensionPoint(
  id = "MaitreStartEnvironmentChange",
  description = "Set proper environment at start of Maitre",
  extensionPointId = "MaitreStart"
)
/**
 * set the debug level right before the step starts to run
 */
public class MaitreStartEnvironmentChange implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {

    if (!(object instanceof String)) {
      return;
    }

    String environmentName = (String) object;

    try {
      if ( StringUtils.isNotEmpty( environmentName ) ) {
        log.logBasic("Switching to environment '"+environmentName+"'");
        EnvironmentSingleton.initialize( Defaults.ENVIRONMENT_METASTORE_FOLDER );
        Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( environmentName );
        EnvironmentUtil.enableEnvironment( environment, null );
      }
    } catch(Exception e) {
      log.logError("Error switching to environment '"+environmentName+"'");
      log.logError( Const.getStackTracker(e));
    }
  }



}
