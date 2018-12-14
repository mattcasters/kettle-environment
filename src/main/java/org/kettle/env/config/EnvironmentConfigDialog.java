package org.kettle.env.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.kettle.env.environment.Environment;
import org.kettle.env.environment.EnvironmentSingleton;
import org.kettle.env.environment.EnvironmentVariable;
import org.kettle.env.environment.EnvironmentsDialog;
import org.kettle.env.util.EnvironmentUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.metastore.persist.MetaStoreFactory;

public class EnvironmentConfigDialog extends Dialog {
  private static Class<?> PKG = EnvironmentsDialog.class; // for i18n purposes, needed by Translator2!!
  
  private final EnvironmentConfig config;

  private boolean ok;

  private Shell shell;
  private final PropsUI props;

  private Text wName;
  private Text wLastUsedEnvironment;
  private Button wEnabled;

  private int margin;
  private int middle;

  public EnvironmentConfigDialog( Shell parent, EnvironmentConfig config ) {
    super( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );

    this.config = config;

    props = PropsUI.getInstance();
  }

  public boolean open() {

    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );
    props.setLook( shell );

    margin = Const.MARGIN + 2;
    middle = Const.MIDDLE_PCT;

    FormLayout formLayout = new FormLayout();

    shell.setLayout( formLayout );
    shell.setText( "Environment Configuration" );

    Label wlName = new Label( shell, SWT.RIGHT );
    props.setLook( wlName );
    wlName.setText( "Name " );
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, 0 );
    fdlName.top = new FormAttachment( 0, 0 );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    wName.setEditable( false );
    props.setLook( wName );
    FormData fdName = new FormData();
    fdName.left = new FormAttachment( middle, margin );
    fdName.right = new FormAttachment( 100, 0 );
    fdName.top = new FormAttachment( wlName, 0, SWT.CENTER );
    wName.setLayoutData( fdName );
    Control lastControl = wName;

    Label wlLastUsedEnvironment = new Label( shell, SWT.RIGHT );
    props.setLook( wlLastUsedEnvironment );
    wlLastUsedEnvironment.setText( "Last used environment " );
    FormData fdlLastUsedEnvironment = new FormData();
    fdlLastUsedEnvironment.left = new FormAttachment( 0, 0 );
    fdlLastUsedEnvironment.right = new FormAttachment( middle, 0 );
    fdlLastUsedEnvironment.top = new FormAttachment( lastControl, margin );
    wlLastUsedEnvironment.setLayoutData( fdlLastUsedEnvironment );
    wLastUsedEnvironment = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wLastUsedEnvironment );
    FormData fdLastUsedEnvironment = new FormData();
    fdLastUsedEnvironment.left = new FormAttachment( middle, margin );
    fdLastUsedEnvironment.right = new FormAttachment( 100, 0 );
    fdLastUsedEnvironment.top = new FormAttachment( wlLastUsedEnvironment, 0, SWT.CENTER );
    wLastUsedEnvironment.setLayoutData( fdLastUsedEnvironment );
    lastControl = wLastUsedEnvironment;

    Label wlCompany = new Label( shell, SWT.RIGHT );
    props.setLook( wlCompany );
    wlCompany.setText( "Enable Kettle Environment? " );
    FormData fdlCompany = new FormData();
    fdlCompany.left = new FormAttachment( 0, 0 );
    fdlCompany.right = new FormAttachment( middle, 0 );
    fdlCompany.top = new FormAttachment( lastControl, margin );
    wlCompany.setLayoutData( fdlCompany );
    wEnabled = new Button( shell, SWT.CHECK | SWT.LEFT );
    props.setLook( wEnabled );
    FormData fdCompany = new FormData();
    fdCompany.left = new FormAttachment( middle, margin );
    fdCompany.right = new FormAttachment( 100, 0 );
    fdCompany.top = new FormAttachment( wlCompany, 0, SWT.CENTER );
    wEnabled.setLayoutData( fdCompany );
    lastControl = wEnabled;

    Button wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOK.addListener( SWT.Selection, event -> ok() );
    Button wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, event -> cancel() );

    // Buttons go at the bottom of the dialog
    //
    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin * 3, lastControl );



    // When enter is hit, close the dialog
    //
    wName.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wLastUsedEnvironment.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wEnabled.addListener( SWT.DefaultSelection, ( e ) -> ok() );

    // Set the shell size, based upon previous time...
    BaseStepDialog.setSize( shell );

    getData();

    shell.open();

    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

    return ok;
  }

  private void ok() {
    getInfo( config );
    ok = true;

    dispose();
  }

  private void cancel() {
    ok = false;

    dispose();
  }

  public void dispose() {
    props.setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }

  private void getData() {
    wName.setText( Const.NVL( config.getName(), "" ) );
    wLastUsedEnvironment.setText( Const.NVL( config.getLastUsedEnvironment(), "" ) );
    wEnabled.setSelection( config.isEnabled() );
  }

  private void getInfo( EnvironmentConfig conf ) {
    conf.setName( wName.getText() );
    conf.setLastUsedEnvironment( wLastUsedEnvironment.getText() );
    conf.setEnabled( wEnabled.getSelection() );
  }
}
