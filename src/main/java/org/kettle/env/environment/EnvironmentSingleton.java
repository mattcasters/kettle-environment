package org.kettle.env.environment;

import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.stores.xml.XmlMetaStore;
import org.pentaho.metastore.util.PentahoDefaults;

public class EnvironmentSingleton {

  private static EnvironmentSingleton environmentSingleton;

  private String location;
  private IMetaStore environmentMetaStore;
  private MetaStoreFactory<Environment> environmentFactory;

  private EnvironmentSingleton( String location) throws MetaStoreException {
    this.location = location;
    this.environmentMetaStore = new XmlMetaStore( location );
    environmentFactory = new MetaStoreFactory<>( Environment.class, this.environmentMetaStore, PentahoDefaults.NAMESPACE );
  }

  public static void initialize(String location) throws MetaStoreException {
    environmentSingleton = new EnvironmentSingleton( location );
  }

  public static IMetaStore getEnvironmentMetaStore() {
    return environmentSingleton.environmentMetaStore;
  }

  public static String getLocation() {
    return environmentSingleton.location;
  }

  public static MetaStoreFactory<Environment> getEnvironmentFactory() {
    return environmentSingleton.environmentFactory;
  }

}
