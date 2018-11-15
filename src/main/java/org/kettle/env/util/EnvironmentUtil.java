package org.kettle.env.util;

import org.apache.commons.lang.StringUtils;
import org.kettle.env.Environment;
import org.kettle.env.EnvironmentVariable;
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
    if ( StringUtils.isNotEmpty(environment.getHomeFolder())) {
      System.setProperty( "KETTLE_HOME", environment.getHomeFolder());
    }
    if (StringUtils.isNotEmpty(environment.getMetaStoreBaseFolder())) {
      System.setProperty( Const.PENTAHO_METASTORE_FOLDER, environment.getMetaStoreBaseFolder());
    }

    for ( EnvironmentVariable environmentVariable : environment.getVariables()) {
      if (environmentVariable.getName()!=null) {
        System.setProperty( environmentVariable.getName(), environmentVariable.getValue() );
      }
    }

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
