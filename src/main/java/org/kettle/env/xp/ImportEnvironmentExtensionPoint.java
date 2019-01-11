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
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@ExtensionPoint(
  id = "CreateEnvironmentExtensionPoint",
  description = "Create an environment with a given name and base folder",
  extensionPointId = "ImportEnvironment"
)
/**
 * Called only by Maitre for now but others may play as well.
 */
public class ImportEnvironmentExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {

    if (!(object instanceof Object[])) {
      return;
    }

    Object[] objects = (Object[]) object;

    String environmentJsonFilename = (String) objects[0];

    try {
      if ( StringUtils.isNotEmpty( environmentJsonFilename ) ) {
        log.logBasic("Importing environment from JSON file '"+environmentJsonFilename+"'");

        EnvironmentSingleton.initialize( Defaults.ENVIRONMENT_METASTORE_FOLDER );
        EnvironmentConfigSingleton.initialize( EnvironmentSingleton.getEnvironmentMetaStore() );

        // Only move forward if the environment system is enabled...
        //
        if (EnvironmentConfigSingleton.getConfig().isEnabled()) {

          MetaStoreFactory<Environment> factory = EnvironmentSingleton.getEnvironmentFactory();

          // Inflate the Environment from the JSON file
          //
          File environmentJsonFile = new File(environmentJsonFilename);
          String environmentJsonString = new String(Files.readAllBytes( environmentJsonFile.toPath() ), "UTF-8");

          Environment environment = Environment.fromJsonString( environmentJsonString );

          // Save it in the metastore
          //
          factory.saveElement( environment );
          log.logBasic( "Saved environment '"+environment.getName()+"'" );

          // Also apply it...
          //
          EnvironmentUtil.enableEnvironment( environment, null );
          log.logBasic( "Enabled environment '"+environment.getName()+"'" );

        } else {
          throw new KettleException("Unable to import the environment since the system is disabled.");
        }
      }
    } catch(Exception e) {
      log.logError("Error importing environment from file '"+environmentJsonFilename+"'");
      log.logError( Const.getStackTracker(e));
    }
  }



}
