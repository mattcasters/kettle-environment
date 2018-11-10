package org.kettle.env;

import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;

import java.util.ArrayList;
import java.util.List;

@MetaStoreElementType(
  name = "Kettle Environment",
  description = "An environment to tie together all sorts of configuration options"
)
public class Environment {

  // Information about the environment itself
  //
  private String name;

  @MetaStoreAttribute
  private String description;

  @MetaStoreAttribute
  private String version;

  // Environment metadata (nice to know)
  //
  @MetaStoreAttribute
  private String company;
  @MetaStoreAttribute
  private String department;
  @MetaStoreAttribute
  private String project;

  // Technical information
  //
  @MetaStoreAttribute
  private String homeFolder;
  @MetaStoreAttribute
  private String metaStoreBaseFolder;

  // Git information
  //
  @MetaStoreAttribute
  private String spoonGitProject;

  // Variables
  //
  @MetaStoreAttribute
  private List<EnvironmentVariable> variables;


  public Environment() {
    variables = new ArrayList<>();
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
   * Gets company
   *
   * @return value of company
   */
  public String getCompany() {
    return company;
  }

  /**
   * @param company The company to set
   */
  public void setCompany( String company ) {
    this.company = company;
  }

  /**
   * Gets department
   *
   * @return value of department
   */
  public String getDepartment() {
    return department;
  }

  /**
   * @param department The department to set
   */
  public void setDepartment( String department ) {
    this.department = department;
  }

  /**
   * Gets homeFolder
   *
   * @return value of homeFolder
   */
  public String getHomeFolder() {
    return homeFolder;
  }

  /**
   * @param homeFolder The homeFolder to set
   */
  public void setHomeFolder( String homeFolder ) {
    this.homeFolder = homeFolder;
  }

  /**
   * Gets spoonGitProject
   *
   * @return value of spoonGitProject
   */
  public String getSpoonGitProject() {
    return spoonGitProject;
  }

  /**
   * @param spoonGitProject The spoonGitProject to set
   */
  public void setSpoonGitProject( String spoonGitProject ) {
    this.spoonGitProject = spoonGitProject;
  }

  /**
   * Gets metaStoreBaseFolder
   *
   * @return value of metaStoreBaseFolder
   */
  public String getMetaStoreBaseFolder() {
    return metaStoreBaseFolder;
  }

  /**
   * @param metaStoreBaseFolder The metaStoreBaseFolder to set
   */
  public void setMetaStoreBaseFolder( String metaStoreBaseFolder ) {
    this.metaStoreBaseFolder = metaStoreBaseFolder;
  }

  /**
   * Gets project
   *
   * @return value of project
   */
  public String getProject() {
    return project;
  }

  /**
   * @param project The project to set
   */
  public void setProject( String project ) {
    this.project = project;
  }

  /**
   * Gets version
   *
   * @return value of version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version The version to set
   */
  public void setVersion( String version ) {
    this.version = version;
  }

  /**
   * Gets variables
   *
   * @return value of variables
   */
  public List<EnvironmentVariable> getVariables() {
    return variables;
  }

  /**
   * @param variables The variables to set
   */
  public void setVariables( List<EnvironmentVariable> variables ) {
    this.variables = variables;
  }
}