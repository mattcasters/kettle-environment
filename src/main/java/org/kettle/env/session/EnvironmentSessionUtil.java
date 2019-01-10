package org.kettle.env.session;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentSingleton;
import org.kettle.env.util.Defaults;
import org.pentaho.di.core.LastUsedFile;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.TabMapEntry;
import org.pentaho.di.ui.spoon.delegates.SpoonTabsDelegate;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;
import org.pentaho.metastore.util.PentahoDefaults;
import org.pentaho.xul.swt.tab.TabItem;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentSessionUtil {

  public static final EnvironmentSession buildCurrentActiveSpoonSession() {
    Spoon spoon = Spoon.getInstance();

    EnvironmentSession session = new EnvironmentSession();

    SpoonTabsDelegate tabsDelegate = spoon.delegates.tabs;

    // Copy the list of tabs because we're going to sort them using the list of last used files...
    //
    List<TabMapEntry> entries = tabsDelegate.getTabs();
    if ( !entries.isEmpty() ) {
      CTabItem selectedCTabItem = entries.get( 0 ).getTabItem().getSwtTabItem().getParent().getSelection();
      if ( selectedCTabItem != null ) {
        for ( TabMapEntry entry : entries ) {
          if ( entry.getTabItem().getSwtTabItem() == selectedCTabItem ) {
            session.setLastActiveFile( getLastUsedFileFromTabEntry( entry, spoon.getRepository(), spoon.getRepositoryName() ) );
          }
        }
      }
    }

    List<TabMapEntry> sortedEntries = new ArrayList<>();
    List<TabMapEntry> unsortedEntries = new ArrayList<>();

    List<LastUsedFile> lastUsedFiles = PropsUI.getInstance().getLastUsedFiles();
    for ( LastUsedFile lastUsedFile : lastUsedFiles ) {
      // Find the corresponding tab entry...
      //
      if ( StringUtils.isNotEmpty( lastUsedFile.getFilename() ) ) {
        boolean found = false;
        for ( TabMapEntry entry : entries ) {
          if ( lastUsedFile.getFilename().equals( entry.getFilename() ) ) {
            found = sortedEntries.add( entry );
          }
        }
      }
    }

    for ( TabMapEntry entry : entries ) {
      if ( !sortedEntries.contains( entry ) ) {
        unsortedEntries.add( entry );
      }
    }
    sortedEntries.addAll( unsortedEntries );

    // Add all the files, sorted by last used ones first.
    //
    for ( TabMapEntry entry : sortedEntries ) {

      EnvironmentLastUsedFile environmentLastUsedFile = getLastUsedFileFromTabEntry( entry, spoon.getRepository(), spoon.getRepositoryName() );
      if ( environmentLastUsedFile != null ) {
        session.getSessionFiles().add( environmentLastUsedFile );
      }
    }

    return session;
  }

  private static EnvironmentLastUsedFile getLastUsedFileFromTabEntry( TabMapEntry entry, Repository repository, String repositoryName ) {
    String objectType;
    switch ( entry.getObjectType() ) {
      case TRANSFORMATION_GRAPH:
        objectType = LastUsedFile.FILE_TYPE_TRANSFORMATION; break;
      case JOB_GRAPH:
        objectType = LastUsedFile.FILE_TYPE_JOB; break;
      default:
        return null;
    }

    boolean sourceRepository = repository != null && entry.getObjectName() != null && entry.getFilename() == null;

    // Only transformation and job for now
    //
    LastUsedFile lastUsedFile = new LastUsedFile( objectType, entry.getFilename(), entry.getRepositoryDirectory().getPath(), sourceRepository, repositoryName, true, 0 );
    EnvironmentLastUsedFile environmentLastUsedFile = new EnvironmentLastUsedFile( lastUsedFile );

    return environmentLastUsedFile;
  }

  public static final void restoreSessionInSpoon( EnvironmentSession session ) {

    Spoon spoon = Spoon.getInstance();

    // Close all open files
    //
    spoon.closeAllFiles();

    // Open back to front ending up with the last one used
    //
    for ( int i = session.getSessionFiles().size() - 1; i >= 0; i-- ) {
      EnvironmentLastUsedFile lastUsedFile = session.getSessionFiles().get( i );
      if (lastUsedFile!=null) {
        // Only load existing files...
        //
        boolean load = true;
        if (!lastUsedFile.isSourceRepository()) {
          String filename = lastUsedFile.getFilename();
          if (StringUtils.isEmpty( filename )) {
            load = false;
          } else {
            try {
              FileObject fileObject = KettleVFS.getFileObject( filename );
              if ( !fileObject.exists() ) {
                load = false;
                LogChannel.GENERAL.logError("File '"+filename+"' doesn't exist: not loading.");
              }
              if ( !fileObject.isReadable()) {
                load = false;
                LogChannel.GENERAL.logError("File '"+filename+"' is not readable : not loading.");
              }
            } catch(Exception e) {
              load = false;
              LogChannel.GENERAL.logError("Error checking state of file '"+filename+"', not loading.", e);
            }
          }
        }
        if (load) {
          spoon.lastFileSelect( lastUsedFile.createLastUsedFile() );
        }
      }
    }

    // TODO: Select the last active file
    //
    EnvironmentLastUsedFile lastActive = session.getLastActiveFile();
    if ( lastActive != null ) {
      SpoonTabsDelegate tabs = spoon.delegates.tabs;
      for ( TabMapEntry entry : tabs.getTabs() ) {
        if ( entry.getFilename() != null && lastActive.getFilename() != null && entry.getFilename().equals( lastActive.getFilename() ) ) {
          TabItem tabItem = entry.getTabItem();
          CTabFolder swtFolder = tabItem.getSwtTabItem().getParent();
          CTabItem swtItem = tabItem.getSwtTabItem();

          swtFolder.setSelection( swtItem );
          spoon.getTabSet().setSelected( tabItem );

          if ( tabs.getActiveMeta() != null ) {

            tabs.getActiveMeta().setInternalKettleVariables();
            spoon.refreshCoreObjects();
            spoon.refreshTree();
          }

          break;
        }
      }
    }
  }

  public static void saveActiveEnvironmentSession( boolean forceSave ) throws MetaStoreException {
    Spoon spoon = Spoon.getInstance();
    if ( spoon != null ) {
      String activeEnvironmentName = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
      if ( !StringUtils.isEmpty( activeEnvironmentName ) ) {
        Environment activeEnvironment = EnvironmentSingleton.getEnvironmentFactory().loadElement( activeEnvironmentName );
        if ( activeEnvironment != null && ( activeEnvironment.isAutoSavingSpoonSession() || forceSave ) ) {
          EnvironmentSession activeEnvironmentSession = EnvironmentSessionUtil.buildCurrentActiveSpoonSession();
          activeEnvironmentSession.setName( activeEnvironmentName );
          activeEnvironmentSession.setDescription( "This session was automatically saved by the Kettle Environment plugin" );
          MetaStoreFactory<EnvironmentSession> sessionFactory = new MetaStoreFactory<>( EnvironmentSession.class, spoon.getMetaStore(), PentahoDefaults.NAMESPACE );
          sessionFactory.saveElement( activeEnvironmentSession );
        }
      }
    }
  }

  public static void restoreActiveEnvironmentSession( boolean forceLoad ) throws MetaStoreException {
    Spoon spoon = Spoon.getInstance();
    if ( spoon != null ) {
      String activeEnvironmentName = System.getProperty( Defaults.VARIABLE_ACTIVE_ENVIRONMENT );
      if ( !StringUtils.isEmpty( activeEnvironmentName ) ) {
        Environment activeEnvironment = EnvironmentSingleton.getEnvironmentFactory().loadElement( activeEnvironmentName );
        if ( activeEnvironment != null && ( activeEnvironment.isAutoRestoringSpoonSession() || forceLoad ) ) {
          MetaStoreFactory<EnvironmentSession> sessionFactory = new MetaStoreFactory<>( EnvironmentSession.class, spoon.getMetaStore(), PentahoDefaults.NAMESPACE );
          EnvironmentSession activeEnvironmentSession = sessionFactory.loadElement( activeEnvironmentName );
          if ( activeEnvironmentSession != null ) {
            restoreSessionInSpoon( activeEnvironmentSession );
          }
        }
      }
    }
  }
}
