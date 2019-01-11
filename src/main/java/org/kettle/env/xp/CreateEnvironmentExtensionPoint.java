package org.kettle.env.xp;

import org.apache.commons.lang.StringUtils;
import org.kettle.env.config.EnvironmentConfigSingleton;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentSingleton;
import org.kettle.env.environment.EnvironmentVariable;
import org.kettle.env.util.Defaults;
import org.kettle.env.util.EnvironmentUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.net.URLDecoder;
import java.util.Map;

@ExtensionPoint(
  id = "CreateEnvironmentExtensionPoint",
  description = "Create an environment with a given name and base folder",
  extensionPointId = "CreateEnvironment"
)
/**
 * Called only by Maitre for now but others may play as well.
 */
public class CreateEnvironmentExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {

    if (!(object instanceof Object[])) {
      return;
    }

    Object[] objects = (Object[]) object;

    String environmentName = (String) objects[0];
    String baseFolder = (String) objects[1];
    Map<String, String> variablesToAdd = (Map<String, String>) objects[2];

    try {
      if ( StringUtils.isNotEmpty( environmentName ) ) {
        log.logBasic("Creating environment '"+environmentName+"'");
        EnvironmentSingleton.initialize( Defaults.ENVIRONMENT_METASTORE_FOLDER );
        EnvironmentConfigSingleton.initialize( EnvironmentSingleton.getEnvironmentMetaStore() );

        // Only move forward if the environment system is enabled...
        //
        if (EnvironmentConfigSingleton.getConfig().isEnabled()) {

          MetaStoreFactory<Environment> factory = EnvironmentSingleton.getEnvironmentFactory();

          // Create a new default environment...
          //
          Environment environment = new Environment();

          // No base folder: default
          //
          if (StringUtils.isEmpty( baseFolder )) {
            environment.applyKettleDefaultSettings();
            log.logBasic( "Created default environment '"+environmentName+"'" );
          } else {
            environment.applySuggestedSettings();
            environment.setEnvironmentHomeFolder( baseFolder );
            log.logBasic( "Created environment '"+environmentName+"' for base folder '"+baseFolder+"'" );
          }
          environment.setName( environmentName );

          // Apply variables...
          //
          if (variablesToAdd!=null) {
            for (String variableName : variablesToAdd.keySet()) {
              String valueDescription = variablesToAdd.get( variableName );
              String variableValue=null;
              String variableDescription=null;
              if (StringUtils.isNotEmpty( valueDescription) ) {
                String[] split = valueDescription.split( ":" );
                if (split.length>0) {
                  variableValue = URLDecoder.decode( split[0], "UTF-8" );
                }
                if (split.length>1) {
                  variableValue = URLDecoder.decode( split[1], "UTF-8" );
                }
              }
              environment.getVariables().add(new EnvironmentVariable( variableName, variableValue, variableDescription ) );
              log.logBasic( "Added variable: '"+variableName+"', value: '"+Const.NVL(variableValue, "")+"', description: '"+Const.NVL(variableDescription,"")+"'" );
            }
          }


          // Save it in the metastore
          //
          factory.saveElement( environment );
          log.logBasic( "Saved environment '"+environmentName+"'" );

          // Also apply it...
          //
          EnvironmentUtil.enableEnvironment( environment, null );
          log.logBasic( "Enabled environment '"+environmentName+"'" );

        } else {
          throw new KettleException("Unable to create the environment since the system is disabled.");
        }
      }
    } catch(Exception e) {
      log.logError("Error creating environment '"+environmentName+"'");
      log.logError( Const.getStackTracker(e));
    }
  }



}
