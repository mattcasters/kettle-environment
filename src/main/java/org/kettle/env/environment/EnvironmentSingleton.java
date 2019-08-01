package org.kettle.env.environment;

import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.stores.xml.XmlMetaStore;
import org.pentaho.metastore.util.PentahoDefaults;

public class EnvironmentSingleton {

  private static EnvironmentSingleton environmentSingleton;

  private String location;

  private EnvironmentSingleton( String location) throws MetaStoreException {
    this.location = location;
  }

  public static void initialize(String location) throws MetaStoreException {
    environmentSingleton = new EnvironmentSingleton( location );
  }

  public static String getLocation() {
    return environmentSingleton.location;
  }

  public static MetaStoreFactory<Environment> getEnvironmentFactory() throws MetaStoreException {
    return new MetaStoreFactory<>( Environment.class, getEnvironmentMetaStore(), PentahoDefaults.NAMESPACE );
  }

  public static IMetaStore getEnvironmentMetaStore() throws MetaStoreException {
    return new XmlMetaStore( getLocation() );
  }
}
