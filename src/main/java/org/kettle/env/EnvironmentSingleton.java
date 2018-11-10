package org.kettle.env;

import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.stores.xml.XmlMetaStore;
import org.pentaho.metastore.util.PentahoDefaults;

public class EnvironmentSingleton {

  private static EnvironmentSingleton environmentSingleton;

  private String location;
  private IMetaStore metaStore;
  private MetaStoreFactory<Environment> environmentFactory;

  private EnvironmentSingleton( String location) throws MetaStoreException {
    this.location = location;
    this.metaStore = new XmlMetaStore( location );
    environmentFactory = new MetaStoreFactory<>( Environment.class, this.metaStore, PentahoDefaults.NAMESPACE );
  }

  public static void initialize(String location) throws MetaStoreException {
    environmentSingleton = new EnvironmentSingleton( location );
  }

  public static IMetaStore getMetaStore() {
    return environmentSingleton.metaStore;
  }

  public static String getLocation() {
    return environmentSingleton.location;
  }

  public static MetaStoreFactory<Environment> getEnvironmentFactory() {
    return environmentSingleton.environmentFactory;
  }

}
