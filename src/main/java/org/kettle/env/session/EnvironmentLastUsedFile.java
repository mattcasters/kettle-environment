package org.kettle.env.session;

import org.pentaho.di.core.LastUsedFile;
import org.pentaho.metastore.persist.MetaStoreAttribute;

import java.util.Date;

public class EnvironmentLastUsedFile {

  @MetaStoreAttribute
  private String fileType;

  @MetaStoreAttribute
  private String filename;

  @MetaStoreAttribute
  private String directory;

  @MetaStoreAttribute
  private boolean sourceRepository;

  @MetaStoreAttribute
  private String repositoryName;


  public EnvironmentLastUsedFile() {
  }

  public EnvironmentLastUsedFile( String fileType, String filename, String directory, boolean sourceRepository, String repositoryName ) {
    this.fileType = fileType;
    this.filename = filename;
    this.directory = directory;
    this.sourceRepository = sourceRepository;
    this.repositoryName = repositoryName;
  }

  public EnvironmentLastUsedFile(LastUsedFile f) {
    this();
    this.fileType = f.getFileType();
    this.filename = f.getFilename();
    this.directory = f.getDirectory();
    this.sourceRepository = f.isSourceRepository();
    this.repositoryName = f.getRepositoryName();
  }

  public LastUsedFile createLastUsedFile() {
    return new LastUsedFile(fileType, filename, directory, sourceRepository, repositoryName, true, 0);
  }

  /**
   * Gets fileType
   *
   * @return value of fileType
   */
  public String getFileType() {
    return fileType;
  }

  /**
   * @param fileType The fileType to set
   */
  public void setFileType( String fileType ) {
    this.fileType = fileType;
  }

  /**
   * Gets filename
   *
   * @return value of filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename The filename to set
   */
  public void setFilename( String filename ) {
    this.filename = filename;
  }

  /**
   * Gets directory
   *
   * @return value of directory
   */
  public String getDirectory() {
    return directory;
  }

  /**
   * @param directory The directory to set
   */
  public void setDirectory( String directory ) {
    this.directory = directory;
  }

  /**
   * Gets sourceRepository
   *
   * @return value of sourceRepository
   */
  public boolean isSourceRepository() {
    return sourceRepository;
  }

  /**
   * @param sourceRepository The sourceRepository to set
   */
  public void setSourceRepository( boolean sourceRepository ) {
    this.sourceRepository = sourceRepository;
  }

  /**
   * Gets repositoryName
   *
   * @return value of repositoryName
   */
  public String getRepositoryName() {
    return repositoryName;
  }

  /**
   * @param repositoryName The repositoryName to set
   */
  public void setRepositoryName( String repositoryName ) {
    this.repositoryName = repositoryName;
  }
}
