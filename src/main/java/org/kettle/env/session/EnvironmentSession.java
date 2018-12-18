package org.kettle.env.session;

import org.pentaho.di.core.LastUsedFile;
import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;

import java.util.ArrayList;
import java.util.List;

@MetaStoreElementType(
  name = "Kettle Environment Spoon Session",
  description = "This describes a set of open transformations and job tabs in Spoon" )
public class EnvironmentSession {

  private String name;

  @MetaStoreAttribute
  private String description;

  @MetaStoreAttribute
  private List<EnvironmentLastUsedFile> sessionFiles;

  @MetaStoreAttribute
  private EnvironmentLastUsedFile lastActiveFile;

  public EnvironmentSession() {
    this.sessionFiles = new ArrayList<>(  );
  }

  public EnvironmentSession( String name, String description, List<EnvironmentLastUsedFile> sessionFiles, EnvironmentLastUsedFile lastActiveFile ) {
    this.name = name;
    this.description = description;
    this.sessionFiles = sessionFiles;
    this.lastActiveFile = lastActiveFile;
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

  /**
   * Gets sessionFiles
   *
   * @return value of sessionFiles
   */
  public List<EnvironmentLastUsedFile> getSessionFiles() {
    return sessionFiles;
  }

  /**
   * @param sessionFiles The sessionFiles to set
   */
  public void setSessionFiles( List<EnvironmentLastUsedFile> sessionFiles ) {
    this.sessionFiles = sessionFiles;
  }

  /**
   * Gets lastActiveFile
   *
   * @return value of lastActiveFile
   */
  public EnvironmentLastUsedFile getLastActiveFile() {
    return lastActiveFile;
  }

  /**
   * @param lastActiveFile The lastActiveFile to set
   */
  public void setLastActiveFile( EnvironmentLastUsedFile lastActiveFile ) {
    this.lastActiveFile = lastActiveFile;
  }
}
