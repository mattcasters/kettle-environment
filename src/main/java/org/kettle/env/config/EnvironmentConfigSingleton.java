package org.kettle.env.config;

import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.util.PentahoDefaults;

public class EnvironmentConfigSingleton {

  private static EnvironmentConfigSingleton configSingleton;

  private EnvironmentConfig config;
  private IMetaStore environmentMetaStore;
  private MetaStoreFactory<EnvironmentConfig> configFactory;

  private EnvironmentConfigSingleton( IMetaStore metaStore ) throws MetaStoreException {
    this.environmentMetaStore = metaStore;
    configFactory = new MetaStoreFactory<>( EnvironmentConfig.class, this.environmentMetaStore, PentahoDefaults.NAMESPACE );

    config = configFactory.loadElement( EnvironmentConfig.SYSTEM_CONFIG_NAME );
    if (config ==null) {
      config = new EnvironmentConfig();
      // Save a default if none exists.
      configFactory.saveElement( config );
    }
  }

  public static void initialize( IMetaStore environmentMetaStore ) throws MetaStoreException {
    if ( configSingleton == null ) {
      configSingleton = new EnvironmentConfigSingleton( environmentMetaStore );
    } else {
      throw new MetaStoreException( "Configuration singleton is already initialized" );
    }
  }

  public static void saveConfig() throws MetaStoreException {
    configSingleton.configFactory.saveElement( configSingleton.config );
  }

  public static EnvironmentConfig getConfig() {
    return configSingleton.config;
  }

  /**
   * Gets environmentMetaStore
   *
   * @return value of environmentMetaStore
   */
  public static IMetaStore getEnvironmentMetaStore() {
    return configSingleton.environmentMetaStore;
  }

  /**
   * Gets configFactory
   *
   * @return value of configFactory
   */
  public static MetaStoreFactory<EnvironmentConfig> getConfigFactory() {
    return configSingleton.configFactory;
  }
}
