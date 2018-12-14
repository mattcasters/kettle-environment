package org.kettle.env.config;

import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;

@MetaStoreElementType(
  name = "Kettle Environment Configuration",
  description = "These options allow you to configure the environment system itself"
)
public class EnvironmentConfig {

  public static final String SYSTEM_CONFIG_NAME = "system";

  @MetaStoreAttribute
  private String name;

  @MetaStoreAttribute
  private String lastUsedEnvironment;

  @MetaStoreAttribute
  private boolean enabled;

  public EnvironmentConfig() {
    name = SYSTEM_CONFIG_NAME;
    lastUsedEnvironment = null;
    enabled = true;
  }

  /**
   * Gets name
   *
   * @return value of name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets lastUsedEnvironment
   *
   * @return value of lastUsedEnvironment
   */
  public String getLastUsedEnvironment() {
    return lastUsedEnvironment;
  }

  /**
   * @param lastUsedEnvironment The lastUsedEnvironment to set
   */
  public void setLastUsedEnvironment( String lastUsedEnvironment ) {
    this.lastUsedEnvironment = lastUsedEnvironment;
  }

  /**
   * Gets enabled
   *
   * @return value of enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param enabled The enabled to set
   */
  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }
}
