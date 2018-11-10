package org.kettle.xp;

import org.apache.commons.lang.StringUtils;
import org.kettle.env.Environment;
import org.kettle.env.EnvironmentSingleton;
import org.kettle.env.EnvironmentsDialog;
import org.kettle.util.Defaults;
import org.kettle.util.EnvironmentUtil;
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
  id = "SpoonStartEnvironmentPrompt",
  description = "Ask the user for the environment to load",
  extensionPointId = "SpoonStart"
)
/**
 * set the debug level right before the step starts to run
 */
public class SpoonStartEnvironmentPrompt implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {

    Spoon spoon = Spoon.getInstance();
    VariableSpace space = new Variables();
    space.initializeVariablesFrom( null );

    // Where is the metastore for the environment
    //
    String environmentMetastoreLocation = space.getVariable( Defaults.VARIABLE_ENVIRONMENT_METASTORE_FOLDER );
    if ( StringUtils.isEmpty(environmentMetastoreLocation)) {
      environmentMetastoreLocation = Defaults.ENVIRONMENT_METASTORE_FOLDER;
    }
    // Build the metastore for it.
    //
    try {
      EnvironmentSingleton.initialize( environmentMetastoreLocation );

      List<String> environmentNames = EnvironmentSingleton.getEnvironmentFactory().getElementNames();


      EnvironmentsDialog environmentsDialog = new EnvironmentsDialog( spoon.getShell(), spoon.getMetaStore() );
      String selectedEnvironment = environmentsDialog.open();
      if (selectedEnvironment!=null) {

        Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( selectedEnvironment );
        logChannelInterface.logBasic("Setting environment : '"+environment.getName()+"'");

        // Set system variables for KETTLE_HOME, PENTAHO_METASTORE_FOLDER, ...
        //
        EnvironmentUtil.enableEnvironment(environment, spoon.getMetaStore());
      }

    } catch(Exception e) {
      new ErrorDialog(spoon.getShell(), "Error", "Error initializing the Kettle Environment system", e);
    }

  }



}
