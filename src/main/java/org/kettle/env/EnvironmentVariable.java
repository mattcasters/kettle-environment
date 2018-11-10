package org.kettle.env;

import org.pentaho.metastore.persist.MetaStoreAttribute;

public class EnvironmentVariable {

  @MetaStoreAttribute
  private String name;

  @MetaStoreAttribute
  private String value;

  public EnvironmentVariable() {
  }

  public EnvironmentVariable( String name, String value ) {
    this.name = name;
    this.value = value;
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
}
