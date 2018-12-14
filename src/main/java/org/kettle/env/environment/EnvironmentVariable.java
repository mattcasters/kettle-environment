package org.kettle.env.environment;

import org.pentaho.metastore.persist.MetaStoreAttribute;

public class EnvironmentVariable {

  @MetaStoreAttribute
  private String name;

  @MetaStoreAttribute
  private String value;

  @MetaStoreAttribute
  private String description;

  public EnvironmentVariable() {
  }

  public EnvironmentVariable( String name, String value, String description ) {
    this.name = name;
    this.value = value;
    this.description = description;
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
   * Gets value
   *
   * @return value of value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value The value to set
   */
  public void setValue( String value ) {
    this.value = value;
  }

  /**
   * Gets description
   *
   * @return value of description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }
}
