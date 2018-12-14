package org.kettle.env.util;

import org.kettle.env.environment.Environment;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;

public class EnvironmentUtil {

  public static final String VARIABLE_ENVIRONMENT_HOME = "ENVIRONMENT_HOME";
  public static final String VARIABLE_DATASETS_BASE_PATH = "DATASETS_BASE_PATH";
  public static final String VARIABLE_UNIT_TESTS_BASE_PATH = "UNIT_TESTS_BASE_PATH";


  /**
   * Enable the specified environment
   * Force reload of a number of settings
   *
   * @param environment
   * @param delegatingMetaStore
   *
   * @throws KettleException
   * @throws MetaStoreException
   */
  public static void enableEnvironment( Environment environment, DelegatingMetaStore delegatingMetaStore) throws KettleException, MetaStoreException {

    environment.modifySystem();

    // Create Kettle home folder in case it doesn't exist
    //
    KettleClientEnvironment.createKettleHome();

    // Force reload of the Kettle environment
    //
    EnvUtil.environmentInit();

    // Initialize the logging back-end.
    //
    KettleLogStore.init();

    // Restart the environment
    //
    KettleEnvironment.shutdown();
    KettleEnvironment.init();

    // Modify local loaded metastore...
    //
    if (delegatingMetaStore!=null) {
      IMetaStore metaStore = delegatingMetaStore.getMetaStore( Const.PENTAHO_METASTORE_NAME );
      if ( metaStore != null ) {
        System.out.println( "Found metastore '" + metaStore.getName() + "'" );
        int index = delegatingMetaStore.getMetaStoreList().indexOf( metaStore );
        metaStore = MetaStoreConst.openLocalPentahoMetaStore();
        delegatingMetaStore.getMetaStoreList().set( index, metaStore );
        delegatingMetaStore.setActiveMetaStoreName( metaStore.getName() );
      }
    }
  }
}
