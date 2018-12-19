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
import org.pentaho.di.core.gui.BasePainter;
import org.pentaho.di.core.gui.GCInterface;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.gui.PrimitiveGCInterface;
import org.pentaho.di.core.gui.PrimitiveGCInterface.EColor;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.TransPainter;
import org.pentaho.di.ui.spoon.SWTGC;

@ExtensionPoint(
  id = "DrawEnvironmentTransExtensionPoint",
  description = "Draw the name of the environment in the top left corner of the transformation canvas",
  extensionPointId = "TransPainterStart" )
public class DrawEnvironmentTransExtensionPoint implements ExtensionPointInterface {

  @Override
  public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {
    if ( !( object instanceof TransPainter ) ) {
      return;
    }

    TransPainter painter = (TransPainter) object;

    String activeEnvironment = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
    if ( StringUtils.isNotEmpty( activeEnvironment ) ) {
      drawActiveEnvironment( painter, activeEnvironment );
    }
  }

  public static void drawActiveEnvironment( BasePainter<?, ?> painter, String activeEnvironment ) {

    GCInterface defaultGc = painter.getGc();
    if ( defaultGc instanceof SWTGC ) {
      SWTGC gc = (SWTGC) defaultGc;

      gc.setFont( "Courier", 8, true, false);
      gc.setForeground( 180, 180, 180 );

      Point textSize = gc.textExtent( activeEnvironment );

      Point areaSize = painter.getArea();

      int x = areaSize.x - Math.round( (float) textSize.x * painter.getMagnification() ) - 20;
      int y = 10;

      // Take zoom into account
      //
      x = Math.round( (float) x / painter.getMagnification() );
      y = Math.round( (float) y / painter.getMagnification() );


      // System.out.println( "Active environment : "+activeEnvironment +", drawing on ("+x+","+y+") : "+textSize.x+"x"+textSize.y+", magnification="+painter.getMagnification());

      gc.drawText( activeEnvironment, x, y, true );

      // Let the world know where the name of the environment is.
      // Maybe later we can click on it and edit the properties
      //
      painter.getAreaOwners().add( new AreaOwner( AreaType.CUSTOM, x, y, textSize.x, textSize.y, painter.getOffset(),
        Defaults.AREA_DRAWN_ENVIRONMENT_NAME, activeEnvironment ) );
    }
  }

}
