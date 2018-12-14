package org.kettle.env.xp;

import org.apache.commons.lang.StringUtils;
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
        EnvironmentConfigSingleton.initialize( EnvironmentSingleton.getEnvironmentMetaStore() );

        // Only move forward if the environment system is enabled...
        //
        if (EnvironmentConfigSingleton.getConfig().isEnabled()) {
          Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( environmentName );
          EnvironmentUtil.enableEnvironment( environment, null );
        }
      }
    } catch(Exception e) {
      log.logError("Error switching to environment '"+environmentName+"'");
      log.logError( Const.getStackTracker(e));
    }
  }



}
