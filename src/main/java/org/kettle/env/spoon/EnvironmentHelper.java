/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.kettle.env.spoon;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.kettle.env.config.EnvironmentConfig;
import org.kettle.env.config.EnvironmentConfigDialog;
import org.kettle.env.config.EnvironmentConfigSingleton;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentDialog;
import org.kettle.env.environment.EnvironmentSingleton;
import org.kettle.env.environment.EnvironmentVariable;
import org.kettle.env.environment.EnvironmentsDialog;
import org.kettle.env.session.EnvironmentSession;
import org.kettle.env.session.EnvironmentSessionUtil;
import org.kettle.env.util.Defaults;
import org.kettle.env.util.EnvironmentUtil;
import org.pentaho.di.core.gui.SpoonFactory;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.ISpoonMenuController;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.util.PentahoDefaults;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

public class EnvironmentHelper extends AbstractXulEventHandler implements ISpoonMenuController {
  protected static Class<?> PKG = EnvironmentHelper.class; // for i18n

  private static EnvironmentHelper instance = null;

  private EnvironmentHelper() {
  }

  public static EnvironmentHelper getInstance() {
    if ( instance == null ) {
      instance = new EnvironmentHelper();
      Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
      spoon.addSpoonMenuController( instance );
    }
    return instance;
  }

  public String getName() {
    return "environmentHelper";
  }

  public void updateMenu( Document doc ) {
    // Nothing so far.
  }

  public void switchEnvironment() {
    Spoon spoon = Spoon.getInstance();
    try {

      EnvironmentsDialog environmentsDialog = new EnvironmentsDialog( spoon.getShell(), spoon.getMetaStore() );
      String selectedEnvironment = environmentsDialog.open();
      if ( selectedEnvironment != null ) {

        // Save the default session if this is enabled (keep optional, don't force load).
        //
        EnvironmentSessionUtil.saveActiveEnvironmentSession( false );

        // Disable the active environment in Spoon...
        //
        disableActiveEnvironmentSpoon();

        // Save the last used environment
        //
        EnvironmentConfigSingleton.getConfig().setLastUsedEnvironment( selectedEnvironment );
        try {
          EnvironmentConfigSingleton.saveConfig();
        } catch ( MetaStoreException e ) {
          LogChannel.GENERAL.logError( "Error saving the environment system config:", e );
        }

        Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( selectedEnvironment );
        spoon.getLog().logBasic( "Setting environment : '" + environment.getName() + "'" );

        // Set system variables for KETTLE_HOME, PENTAHO_METASTORE_FOLDER, ...
        //
        EnvironmentUtil.enableEnvironment( environment, spoon.getMetaStore() );
      }
    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error changing environment", e );
    }
  }

  public void disableActiveEnvironmentSpoon() throws MetaStoreException {
    Spoon spoon = Spoon.getInstance();
    if ( spoon != null ) {
      String activeEnvironmentName = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
      if ( !StringUtils.isEmpty( activeEnvironmentName ) ) {
        Environment activeEnvironment = EnvironmentSingleton.getEnvironmentFactory().loadElement( activeEnvironmentName );
        if ( activeEnvironment != null ) {

          // Clear all defined variables in the active environment...
          // Not all variables are used in all environments...
          //
          for ( EnvironmentVariable environmentVariable : activeEnvironment.getVariables()) {
            if (StringUtils.isNotEmpty( environmentVariable.getName() )) {
              System.clearProperty( environmentVariable.getName() );
            }
          }

        }
      }
    }
  }

  public void configureEnvironment() {
    Spoon spoon = Spoon.getInstance();
    try {

      EnvironmentConfig config = EnvironmentConfigSingleton.getConfig();
      EnvironmentConfigDialog dialog = new EnvironmentConfigDialog( spoon.getShell(), config );
      if ( dialog.open() ) {
        EnvironmentConfigSingleton.getConfigFactory().saveElement( config );
      }

    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error configuring environment", e );
    }
  }

  public void editEnvironment() {
    editActiveEnvironment();
  }

  public static void editActiveEnvironment() {
    Spoon spoon = Spoon.getInstance();
    try {

      String activeEnvironment = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
      if ( StringUtils.isEmpty( activeEnvironment ) ) {
        return;
      }

      Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( activeEnvironment );
      EnvironmentDialog dialog = new EnvironmentDialog( spoon.getShell(), environment );
      if ( dialog.open() ) {
        MetaStoreFactory<Environment> factory = EnvironmentSingleton.getEnvironmentFactory();
        factory.saveElement( environment );
        if (!environment.getName().equals( activeEnvironment )) {
          factory.deleteElement( activeEnvironment );
        }
        EnvironmentUtil.enableEnvironment( environment, spoon.getMetaStore() );
      }

    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error configuring environment", e );
    }
  }

  public void saveDefaultEnvironmentSession() {
    try {
      EnvironmentSessionUtil.saveActiveEnvironmentSession( true );
    } catch ( Exception e ) {
      new ErrorDialog( Spoon.getInstance().getShell(), "Error", "Error saving default environment session", e );
    }
  }

  public void loadDefaultEnvironmentSession() {
    try {
      EnvironmentSessionUtil.restoreActiveEnvironmentSession( true );
    } catch ( Exception e ) {
      new ErrorDialog( Spoon.getInstance().getShell(), "Error", "Error saving default environment session", e );
    }
  }

}
