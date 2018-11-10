package org.kettle.util;

import org.pentaho.di.core.Const;

import java.io.File;

public class Defaults {
  public static final String VARIABLE_ENVIRONMENT_METASTORE_FOLDER = "ENVIRONMENT_METASTORE_FOLDER";

  public static final String ENVIRONMENT_METASTORE_FOLDER = Const.getKettleDirectory()+ File.separator+"environment";

}
