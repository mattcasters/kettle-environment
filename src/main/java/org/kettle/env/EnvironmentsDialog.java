package org.kettle.env;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.Collections;

public class EnvironmentsDialog extends Dialog {

  private static Class<?> PKG = EnvironmentsDialog.class; // for i18n purposes, needed by Translator2!!

  public static final String LAST_USED_ENVIRONMENT = "LAST_USED_ENVIRONMENT";

  private String lastEnvironment;
  private String selectedEnvironment;

  private Shell shell;
  private final PropsUI props;

  private List wEnvironments;

  private int margin;
  private int middle;
  private final MetaStoreFactory<Environment> environmentFactory;

  public EnvironmentsDialog( Shell parent, IMetaStore metaStore ) {
    super( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );

    props = PropsUI.getInstance();

    environmentFactory = EnvironmentSingleton.getEnvironmentFactory();

    lastEnvironment = PropsUI.getInstance().getProperty( LAST_USED_ENVIRONMENT );
    selectedEnvironment = null;
  }

  public String open() {

    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );
    props.setLook( shell );

    margin = Const.MARGIN + 2;
    middle = Const.MIDDLE_PCT;

    FormLayout formLayout = new FormLayout();

    shell.setLayout( formLayout );
    shell.setText( "Graph Model Editor" );

    Button wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOK.addListener( SWT.Selection, event -> ok() );
    Button wAddDefault = new Button( shell, SWT.PUSH );
    wAddDefault.setText( "Add Default" );
    wAddDefault.addListener( SWT.Selection, event -> addDefault() );
    Button wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, event -> cancel() );

    Label wlEnvironments = new Label( shell, SWT.LEFT );
    props.setLook( wlEnvironments );
    wlEnvironments.setText( "Environments: " );
    FormData fdlEnvironments = new FormData();
    fdlEnvironments.left = new FormAttachment( 0, 0 );
    fdlEnvironments.top = new FormAttachment( 0, 0 );
    wlEnvironments.setLayoutData( fdlEnvironments );

    // Add some buttons
    //
    Button wbAdd = new Button( shell, SWT.PUSH );
    wbAdd.setImage( GUIResource.getInstance().getImageAdd() );
    FormData fdbAdd = new FormData();
    fdbAdd.left = new FormAttachment( 0, 0 );
    fdbAdd.top = new FormAttachment( wlEnvironments, margin * 2 );
    wbAdd.setLayoutData( fdbAdd );
    wbAdd.addListener( SWT.Selection, ( e ) -> addEnvironment() );

    Button wbEdit = new Button( shell, SWT.PUSH );
    wbEdit.setImage( GUIResource.getInstance().getImageEdit() );
    FormData fdbEdit = new FormData();
    fdbEdit.left = new FormAttachment( wbAdd, margin );
    fdbEdit.top = new FormAttachment( wlEnvironments, margin * 2 );
    wbEdit.setLayoutData( fdbEdit );
    wbEdit.addListener( SWT.Selection, ( e ) -> editEnvironment() );

    Button wbDelete = new Button( shell, SWT.PUSH );
    wbDelete.setImage( GUIResource.getInstance().getImageDelete() );
    FormData fdbDelete = new FormData();
    fdbDelete.left = new FormAttachment( wbEdit, margin * 2 );
    fdbDelete.top = new FormAttachment( wlEnvironments, margin * 2 );
    wbDelete.setLayoutData( fdbDelete );
    wbDelete.addListener( SWT.Selection, ( e ) -> deleteEnvironment() );

    wEnvironments = new List( shell, SWT.LEFT | SWT.BORDER | SWT.SINGLE );
    props.setLook( wEnvironments );
    FormData fdEnvironments = new FormData();
    fdEnvironments.left = new FormAttachment( 0, 0 );
    fdEnvironments.right = new FormAttachment( 100, 0 );
    fdEnvironments.top = new FormAttachment( wbAdd, margin * 2 );
    fdEnvironments.bottom = new FormAttachment( wOK, -margin * 2 );
    wEnvironments.setLayoutData( fdEnvironments );

    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wAddDefault, wCancel }, margin, null );

    // Double click on an environment : select it
    //
    wEnvironments.addListener( SWT.DefaultSelection, ( e ) -> ok() );

    // Set the shell size, based upon previous time...
    BaseStepDialog.setSize( shell );

    getData();

    shell.open();

    wEnvironments.setFocus();

    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

    return selectedEnvironment;
  }

  private void deleteEnvironment() {
    String selection[] = wEnvironments.getSelection();
    if ( selection.length == 0 ) {
      return;
    }
    String selectedEnvironment = selection[ 0 ];
    MessageBox box = new MessageBox( shell, SWT.ICON_QUESTION | SWT.APPLICATION_MODAL | SWT.YES | SWT.NO );
    box.setText( "Delete environment?" );
    box.setMessage( "Are you sure you want to delete environment '" + selectedEnvironment + "'?" );
    int anwser = box.open();
    if ( ( anwser & SWT.YES ) == 0 ) {
      return;
    }

    try {
      environmentFactory.deleteElement( selectedEnvironment );
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error deleting environment '" + selectedEnvironment + "'", e );
    } finally {
      refreshEnvironmentsList();
    }
  }

  private void editEnvironment() {
    String selection[] = wEnvironments.getSelection();
    if ( selection.length == 0 ) {
      return;
    }
    String selectedEnvironment = selection[ 0 ];
    try {
      Environment environment = environmentFactory.loadElement( selectedEnvironment );
      EnvironmentDialog environmentDialog = new EnvironmentDialog( shell, environment );
      if ( environmentDialog.open() ) {
        environmentFactory.saveElement( environment );
      }
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error editing environment '" + selectedEnvironment + "'", e );
    } finally {
      refreshEnvironmentsList();
    }
  }

  private void addEnvironment() {
    Environment environment = new Environment();
    environment.setName( "Environment #" + wEnvironments.getItemCount() + 1 );
    EnvironmentDialog environmentDialog = new EnvironmentDialog( shell, environment );
    if ( environmentDialog.open() ) {
      try {
        environmentFactory.saveElement( environment );
      } catch ( Exception e ) {
        new ErrorDialog( shell, "Error", "Error adding environment '" + environment.getName() + "'", e );
      } finally {
        refreshEnvironmentsList();
      }
    }
  }

  private void addDefault() {
    try {
      Environment environment = new Environment();
      environment.setName( "Default" );
      environment.setHomeFolder( System.getProperty( "user.home" ) );
      environment.setMetaStoreBaseFolder( MetaStoreConst.getDefaultPentahoMetaStoreLocation() );
      environmentFactory.saveElement( environment );
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error adding Default environment", e );
    } finally {
      refreshEnvironmentsList();
    }
  }

  private void ok() {
    String[] selection = wEnvironments.getSelection();
    if ( selection.length == 0 ) {
      return;
    }
    selectedEnvironment = selection[ 0 ];

    dispose();
  }

  private void cancel() {
    selectedEnvironment = null;

    dispose();
  }

  public void dispose() {
    props.setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }

  private void getData() {
    try {
      refreshEnvironmentsList();
      if ( StringUtils.isNotEmpty( lastEnvironment ) ) {
        wEnvironments.setSelection( new String[] { lastEnvironment } );
      }
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error getting list of environments", e );
    }
  }

  public void refreshEnvironmentsList() {
    try {
      String selected = null;
      if ( wEnvironments.getSelection().length > 0 ) {
        selected = wEnvironments.getSelection()[ 0 ];
      }
      wEnvironments.removeAll();
      java.util.List<String> elementNames = environmentFactory.getElementNames();
      Collections.sort( elementNames );
      for ( String elementName : elementNames ) {
        wEnvironments.add( elementName );
      }
      if ( selected != null ) {
        wEnvironments.setSelection( new String[] { selected } );
      }
    } catch ( Exception e ) {
      new ErrorDialog( shell, "Error", "Error getting list of environments", e );
    }
  }
}