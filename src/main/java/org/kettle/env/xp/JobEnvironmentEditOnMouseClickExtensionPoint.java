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

package org.kettle.env.xp;

import org.eclipse.swt.events.MouseEvent;
import org.kettle.env.spoon.EnvironmentHelper;
import org.kettle.env.util.Defaults;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.gui.AreaOwner;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraphExtension;

@ExtensionPoint(
  extensionPointId = "JobGraphMouseDown",
  id = "JobEnvironmentEditOnMouseClickExtensionPoint",
  description = "Edit the active environment when we click on its name in a job"
)
public class JobEnvironmentEditOnMouseClickExtensionPoint implements ExtensionPointInterface {

  @Override
  public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {
    if ( !( object instanceof JobGraphExtension ) ) {
      return;
    }

    JobGraphExtension jobGraphExtension = (JobGraphExtension) object;

    Spoon spoon = Spoon.getInstance();
    try {

      // Find the location that was clicked on...
      //
      MouseEvent e = jobGraphExtension.getEvent();
      Point point = jobGraphExtension.getPoint();

      if ( e.button == 1 || e.button == 2 ) {
        AreaOwner areaOwner = jobGraphExtension.getJobGraph().getVisibleAreaOwner( point.x, point.y );
        if ( areaOwner != null && areaOwner.getAreaType() != null ) {
          // Check if this is the environment name...
          //
          if ( Defaults.AREA_DRAWN_ENVIRONMENT_NAME.equals( areaOwner.getParent() ) ) {
            EnvironmentHelper.editActiveEnvironment();
          }
        }
      }
    } catch ( Exception e ) {
      new ErrorDialog( spoon.getShell(), "Error", "Error editing active environment", e );
    }
  }

}
