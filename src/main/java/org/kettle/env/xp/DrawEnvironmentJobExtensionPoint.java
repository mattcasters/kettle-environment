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

import org.apache.commons.lang.StringUtils;
import org.kettle.env.util.Defaults;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.gui.AreaOwner;
import org.pentaho.di.core.gui.AreaOwner.AreaType;
import org.pentaho.di.core.gui.GCInterface;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.gui.PrimitiveGCInterface.EColor;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobPainter;
import org.pentaho.di.trans.TransPainter;

@ExtensionPoint(
  id = "DrawEnvironmentJobExtensionPoint",
  description = "Draw the name of the environment in the top left corner of the job canvas",
  extensionPointId = "JobPainterStart" )
public class DrawEnvironmentJobExtensionPoint implements ExtensionPointInterface {

  @Override
  public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {
    if ( !( object instanceof JobPainter ) ) {
      return;
    }

    JobPainter painter = (JobPainter) object;

    String activeEnvironment = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
    if (StringUtils.isNotEmpty( activeEnvironment )) {
      DrawEnvironmentTransExtensionPoint.drawActiveEnvironment( painter, activeEnvironment);
    }
  }


}
