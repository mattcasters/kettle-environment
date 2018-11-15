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

import org.kettle.env.Environment;
import org.kettle.env.EnvironmentSingleton;
import org.kettle.env.EnvironmentsDialog;
import org.kettle.env.util.EnvironmentUtil;
import org.pentaho.di.core.gui.SpoonFactory;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.ISpoonMenuController;
import org.pentaho.di.ui.spoon.Spoon;
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

        Environment environment = EnvironmentSingleton.getEnvironmentFactory().loadElement( selectedEnvironment );
        spoon.getLog().logBasic( "Setting environment : '" + environment.getName() + "'" );

        // Set system variables for KETTLE_HOME, PENTAHO_METASTORE_FOLDER, ...
        //
        EnvironmentUtil.enableEnvironment( environment, spoon.getMetaStore() );
      }
    } catch(Exception e) {
      new ErrorDialog( spoon.getShell(), "Error", "Error changing environment", e );
    }
  }
}
