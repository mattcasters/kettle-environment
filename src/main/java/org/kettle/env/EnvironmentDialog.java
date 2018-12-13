package org.kettle.env;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

public class EnvironmentDialog extends Dialog {
  private static Class<?> PKG = EnvironmentsDialog.class; // for i18n purposes, needed by Translator2!!

  public static final String LAST_USED_ENVIRONMENT = "LAST_USED_ENVIRONMENT";

  private Environment environment;
  private boolean ok;

  private Shell shell;
  private final PropsUI props;

  private Text wName;
  private Text wDescription;
  private Text wCompany;
  private Text wDepartment;
  private Text wProject;
  private Text wVersion;

  private TextVar wEnvironmentHome;
  private TextVar wKettleHomeFolder;
  private TextVar wMetaStoreBaseFolder;
  private TextVar wSpoonGitProject;
  private TextVar wUnitTestsBasePath;
  private TextVar wDataSetCsvFolder;
  private TableView wVariables;

  private int margin;
  private int middle;
  private final MetaStoreFactory<Environment> environmentFactory;

  private VariableSpace space;

  public EnvironmentDialog( Shell parent, Environment environment ) {
    super( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );

    this.environment = environment;

    props = PropsUI.getInstance();

    environmentFactory = EnvironmentSingleton.getEnvironmentFactory();

    space = new Variables();
    space.initializeVariablesFrom( null );
    environment.modifyVariableSpace( space );

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
    shell.setText( "Environment dialog" );

    Button wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOK.addListener( SWT.Selection, event -> ok() );
    Button wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, event -> cancel() );

    // Buttons go at the bottom of the dialog
    //
    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin * 3, null );


    Label wlName = new Label( shell, SWT.RIGHT );
    props.setLook( wlName );
    wlName.setText( "Name " );
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, 0 );
    fdlName.top = new FormAttachment( 0, 0 );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wName );
    FormData fdName = new FormData();
    fdName.left = new FormAttachment( middle, margin );
    fdName.right = new FormAttachment( 100, 0 );
    fdName.top = new FormAttachment( wlName, 0, SWT.CENTER );
    wName.setLayoutData( fdName );
    Control lastControl = wName;

    Label wlDescription = new Label( shell, SWT.RIGHT );
    props.setLook( wlDescription );
    wlDescription.setText( "Description " );
    FormData fdlDescription = new FormData();
    fdlDescription.left = new FormAttachment( 0, 0 );
    fdlDescription.right = new FormAttachment( middle, 0 );
    fdlDescription.top = new FormAttachment( lastControl, margin );
    wlDescription.setLayoutData( fdlDescription );
    wDescription = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wDescription );
    FormData fdDescription = new FormData();
    fdDescription.left = new FormAttachment( middle, margin );
    fdDescription.right = new FormAttachment( 100, 0 );
    fdDescription.top = new FormAttachment( wlDescription, 0, SWT.CENTER );
    wDescription.setLayoutData( fdDescription );
    lastControl = wDescription;

    Label wlCompany = new Label( shell, SWT.RIGHT );
    props.setLook( wlCompany );
    wlCompany.setText( "Company " );
    FormData fdlCompany = new FormData();
    fdlCompany.left = new FormAttachment( 0, 0 );
    fdlCompany.right = new FormAttachment( middle, 0 );
    fdlCompany.top = new FormAttachment( lastControl, margin );
    wlCompany.setLayoutData( fdlCompany );
    wCompany = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wCompany );
    FormData fdCompany = new FormData();
    fdCompany.left = new FormAttachment( middle, margin );
    fdCompany.right = new FormAttachment( 100, 0 );
    fdCompany.top = new FormAttachment( wlCompany, 0, SWT.CENTER );
    wCompany.setLayoutData( fdCompany );
    lastControl = wCompany;

    Label wlDepartment = new Label( shell, SWT.RIGHT );
    props.setLook( wlDepartment );
    wlDepartment.setText( "Department " );
    FormData fdlDepartment = new FormData();
    fdlDepartment.left = new FormAttachment( 0, 0 );
    fdlDepartment.right = new FormAttachment( middle, 0 );
    fdlDepartment.top = new FormAttachment( lastControl, margin );
    wlDepartment.setLayoutData( fdlDepartment );
    wDepartment = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wDepartment );
    FormData fdDepartment = new FormData();
    fdDepartment.left = new FormAttachment( middle, margin );
    fdDepartment.right = new FormAttachment( 100, 0 );
    fdDepartment.top = new FormAttachment( wlDepartment, 0, SWT.CENTER );
    wDepartment.setLayoutData( fdDepartment );
    lastControl = wDepartment;

    Label wlProject = new Label( shell, SWT.RIGHT );
    props.setLook( wlProject );
    wlProject.setText( "Project " );
    FormData fdlProject = new FormData();
    fdlProject.left = new FormAttachment( 0, 0 );
    fdlProject.right = new FormAttachment( middle, 0 );
    fdlProject.top = new FormAttachment( lastControl, margin );
    wlProject.setLayoutData( fdlProject );
    wProject = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wProject );
    FormData fdProject = new FormData();
    fdProject.left = new FormAttachment( middle, margin );
    fdProject.right = new FormAttachment( 100, 0 );
    fdProject.top = new FormAttachment( wlProject, 0, SWT.CENTER );
    wProject.setLayoutData( fdProject );
    lastControl = wProject;

    Label wlVersion = new Label( shell, SWT.RIGHT );
    props.setLook( wlVersion );
    wlVersion.setText( "Version " );
    FormData fdlVersion = new FormData();
    fdlVersion.left = new FormAttachment( 0, 0 );
    fdlVersion.right = new FormAttachment( middle, 0 );
    fdlVersion.top = new FormAttachment( lastControl, margin );
    wlVersion.setLayoutData( fdlVersion );
    wVersion = new Text( shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wVersion );
    FormData fdVersion = new FormData();
    fdVersion.left = new FormAttachment( middle, margin );
    fdVersion.right = new FormAttachment( 100, 0 );
    fdVersion.top = new FormAttachment( wlVersion, 0, SWT.CENTER );
    wVersion.setLayoutData( fdVersion );
    lastControl = wVersion;

    Label wlEnvironmentHome = new Label( shell, SWT.RIGHT );
    props.setLook( wlEnvironmentHome );
    wlEnvironmentHome.setText( "Environment base folder (" + EnvironmentUtil.VARIABLE_ENVIRONMENT_HOME + ") " );
    FormData fdlEnvironmentHome = new FormData();
    fdlEnvironmentHome.left = new FormAttachment( 0, 0 );
    fdlEnvironmentHome.right = new FormAttachment( middle, 0 );
    fdlEnvironmentHome.top = new FormAttachment( lastControl, margin );
    wlEnvironmentHome.setLayoutData( fdlEnvironmentHome );
    wEnvironmentHome = new TextVar( space, shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wEnvironmentHome );
    FormData fdEnvironmentHome = new FormData();
    fdEnvironmentHome.left = new FormAttachment( middle, margin );
    fdEnvironmentHome.right = new FormAttachment( 100, 0 );
    fdEnvironmentHome.top = new FormAttachment( wlEnvironmentHome, 0, SWT.CENTER );
    wEnvironmentHome.setLayoutData( fdEnvironmentHome );
    wEnvironmentHome.addModifyListener( e->updateVariableSpace());
    lastControl = wEnvironmentHome;

    Label wlKettleHomeFolder = new Label( shell, SWT.RIGHT );
    props.setLook( wlKettleHomeFolder );
    wlKettleHomeFolder.setText( "Kettle Home folder (KETTLE_HOME) " );
    FormData fdlKettleHomeFolder = new FormData();
    fdlKettleHomeFolder.left = new FormAttachment( 0, 0 );
    fdlKettleHomeFolder.right = new FormAttachment( middle, 0 );
    fdlKettleHomeFolder.top = new FormAttachment( lastControl, margin );
    wlKettleHomeFolder.setLayoutData( fdlKettleHomeFolder );
    wKettleHomeFolder = new TextVar( space, shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wKettleHomeFolder );
    FormData fdKettleHomeFolder = new FormData();
    fdKettleHomeFolder.left = new FormAttachment( middle, margin );
    fdKettleHomeFolder.right = new FormAttachment( 100, 0 );
    fdKettleHomeFolder.top = new FormAttachment( wlKettleHomeFolder, 0, SWT.CENTER );
    wKettleHomeFolder.setLayoutData( fdKettleHomeFolder );
    wKettleHomeFolder.addModifyListener( e->updateVariableSpace());
    lastControl = wKettleHomeFolder;

    Label wlMetaStoreBaseFolder = new Label( shell, SWT.RIGHT );
    props.setLook( wlMetaStoreBaseFolder );
    wlMetaStoreBaseFolder.setText( "MetaStore base folder (PENTAHO_METASTORE_FOLDER)" );
    FormData fdlMetaStoreBaseFolder = new FormData();
    fdlMetaStoreBaseFolder.left = new FormAttachment( 0, 0 );
    fdlMetaStoreBaseFolder.right = new FormAttachment( middle, 0 );
    fdlMetaStoreBaseFolder.top = new FormAttachment( lastControl, margin );
    wlMetaStoreBaseFolder.setLayoutData( fdlMetaStoreBaseFolder );
    wMetaStoreBaseFolder = new TextVar( space, shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wMetaStoreBaseFolder );
    FormData fdMetaStoreBaseFolder = new FormData();
    fdMetaStoreBaseFolder.left = new FormAttachment( middle, margin );
    fdMetaStoreBaseFolder.right = new FormAttachment( 100, 0 );
    fdMetaStoreBaseFolder.top = new FormAttachment( wlMetaStoreBaseFolder, 0, SWT.CENTER );
    wMetaStoreBaseFolder.setLayoutData( fdMetaStoreBaseFolder );
    wMetaStoreBaseFolder.addModifyListener( e->updateVariableSpace());
    lastControl = wMetaStoreBaseFolder;

    Label wlSpoonGitProject = new Label( shell, SWT.RIGHT );
    props.setLook( wlSpoonGitProject );
    wlSpoonGitProject.setText( "SpoonGit Project " );
    FormData fdlSpoonGitProject = new FormData();
    fdlSpoonGitProject.left = new FormAttachment( 0, 0 );
    fdlSpoonGitProject.right = new FormAttachment( middle, 0 );
    fdlSpoonGitProject.top = new FormAttachment( lastControl, margin );
    wlSpoonGitProject.setLayoutData( fdlSpoonGitProject );
    wSpoonGitProject = new TextVar( space, shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wSpoonGitProject );
    FormData fdSpoonGitProject = new FormData();
    fdSpoonGitProject.left = new FormAttachment( middle, margin );
    fdSpoonGitProject.right = new FormAttachment( 100, 0 );
    fdSpoonGitProject.top = new FormAttachment( wlSpoonGitProject, 0, SWT.CENTER );
    wSpoonGitProject.setLayoutData( fdSpoonGitProject );
    lastControl = wSpoonGitProject;

    Label wlUnitTestsBasePath = new Label( shell, SWT.RIGHT );
    props.setLook( wlUnitTestsBasePath );
    wlUnitTestsBasePath.setText( "Unit tests base path (UNIT_TESTS_BASE_PATH) " );
    FormData fdlUnitTestsBasePath = new FormData();
    fdlUnitTestsBasePath.left = new FormAttachment( 0, 0 );
    fdlUnitTestsBasePath.right = new FormAttachment( middle, 0 );
    fdlUnitTestsBasePath.top = new FormAttachment( lastControl, margin );
    wlUnitTestsBasePath.setLayoutData( fdlUnitTestsBasePath );
    wUnitTestsBasePath = new TextVar( space, shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wUnitTestsBasePath );
    FormData fdUnitTestsBasePath = new FormData();
    fdUnitTestsBasePath.left = new FormAttachment( middle, margin );
    fdUnitTestsBasePath.right = new FormAttachment( 100, 0 );
    fdUnitTestsBasePath.top = new FormAttachment( wlUnitTestsBasePath, 0, SWT.CENTER );
    wUnitTestsBasePath.setLayoutData( fdUnitTestsBasePath );
    wUnitTestsBasePath.addModifyListener( e->updateVariableSpace());
    lastControl = wUnitTestsBasePath;

    Label wlDataSetCsvFolder = new Label( shell, SWT.RIGHT );
    props.setLook( wlDataSetCsvFolder );
    wlDataSetCsvFolder.setText( "Data Sets CSV Folder (DATASETS_BASE_PATH)" );
    FormData fdlDataSetCsvFolder = new FormData();
    fdlDataSetCsvFolder.left = new FormAttachment( 0, 0 );
    fdlDataSetCsvFolder.right = new FormAttachment( middle, 0 );
    fdlDataSetCsvFolder.top = new FormAttachment( lastControl, margin );
    wlDataSetCsvFolder.setLayoutData( fdlDataSetCsvFolder );
    wDataSetCsvFolder = new TextVar( space, shell, SWT.SINGLE | SWT.BORDER | SWT.LEFT );
    props.setLook( wDataSetCsvFolder );
    FormData fdDataSetCsvFolder = new FormData();
    fdDataSetCsvFolder.left = new FormAttachment( middle, margin );
    fdDataSetCsvFolder.right = new FormAttachment( 100, 0 );
    fdDataSetCsvFolder.top = new FormAttachment( wlDataSetCsvFolder, 0, SWT.CENTER );
    wDataSetCsvFolder.setLayoutData( fdDataSetCsvFolder );
    wDataSetCsvFolder.addModifyListener( e->updateVariableSpace());
    lastControl = wDataSetCsvFolder;

    Label wlVariables = new Label( shell, SWT.RIGHT );
    props.setLook( wlVariables );
    wlVariables.setText( "System variables to set : " );
    FormData fdlVariables = new FormData();
    fdlVariables.left = new FormAttachment( 0, 0 );
    fdlVariables.right = new FormAttachment( middle, 0 );
    fdlVariables.top = new FormAttachment( lastControl, margin );
    wlVariables.setLayoutData( fdlVariables );

    ColumnInfo[] columnInfo = new ColumnInfo[] {
      new ColumnInfo( "Name", ColumnInfo.COLUMN_TYPE_TEXT, false, false ),
      new ColumnInfo( "Value", ColumnInfo.COLUMN_TYPE_TEXT, false, false ),
    };
    columnInfo[ 0 ].setUsingVariables( true );
    columnInfo[ 1 ].setUsingVariables( true );

    wVariables = new TableView( new Variables(), shell, SWT.NONE, columnInfo, environment.getVariables().size(), null, props );
    props.setLook( wVariables );
    FormData fdVariables = new FormData();
    fdVariables.left = new FormAttachment( 0, 0 );
    fdVariables.right = new FormAttachment( 100, 0 );
    fdVariables.top = new FormAttachment( wlVariables, margin );
    fdVariables.bottom = new FormAttachment( wOK, -margin * 2 );
    wVariables.setLayoutData( fdVariables );
    // lastControl = wVariables;

    // When enter is hit, close the dialog
    //
    wName.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wDescription.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wCompany.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wDepartment.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wProject.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wVersion.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wEnvironmentHome.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wKettleHomeFolder.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wMetaStoreBaseFolder.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wSpoonGitProject.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wUnitTestsBasePath.addListener( SWT.DefaultSelection, ( e ) -> ok() );
    wDataSetCsvFolder.addListener( SWT.DefaultSelection, ( e ) -> ok() );

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

  private void updateVariableSpace() {
    Environment env = new Environment();
    getInfo( env );
    env.modifyVariableSpace(space);
  }

  private void ok() {
    getInfo( environment );
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
    wName.setText( Const.NVL( environment.getName(), "" ) );
    wDescription.setText( Const.NVL( environment.getDescription(), "" ) );
    wCompany.setText( Const.NVL( environment.getCompany(), "" ) );
    wDepartment.setText( Const.NVL( environment.getDepartment(), "" ) );
    wProject.setText( Const.NVL( environment.getProject(), "" ) );
    wVersion.setText( Const.NVL( environment.getVersion(), "" ) );
    wEnvironmentHome.setText( Const.NVL( environment.getEnvironmentHomeFolder(), "" ) );
    wKettleHomeFolder.setText( Const.NVL( environment.getKettleHomeFolder(), "" ) );
    wMetaStoreBaseFolder.setText( Const.NVL( environment.getMetaStoreBaseFolder(), "" ) );
    wSpoonGitProject.setText( Const.NVL( environment.getSpoonGitProject(), "" ) );
    wUnitTestsBasePath.setText( Const.NVL( environment.getUnitTestsBasePath(), "" ) );
    wDataSetCsvFolder.setText( Const.NVL( environment.getDataSetsCsvFolder(), "" ) );
    for ( int i = 0; i < environment.getVariables().size(); i++ ) {
      EnvironmentVariable environmentVariable = environment.getVariables().get( i );
      TableItem item = wVariables.table.getItem( i );
      item.setText( 1, Const.NVL( environmentVariable.getName(), "" ) );
      item.setText( 2, Const.NVL( environmentVariable.getValue(), "" ) );
    }
    wVariables.setRowNums();
    wVariables.optWidth( true );
  }

  private void getInfo( Environment env ) {
    env.setName( wName.getText() );
    env.setDescription( wDescription.getText() );
    env.setCompany( wCompany.getText() );
    env.setDepartment( wDepartment.getText() );
    env.setProject( wProject.getText() );
    env.setVersion( wVersion.getText() );
    env.setEnvironmentHomeFolder( wEnvironmentHome.getText() );
    env.setKettleHomeFolder( wKettleHomeFolder.getText() );
    env.setMetaStoreBaseFolder( wMetaStoreBaseFolder.getText() );
    env.setSpoonGitProject( wSpoonGitProject.getText() );
    env.setUnitTestsBasePath( wUnitTestsBasePath.getText() );
    env.setDataSetsCsvFolder( wDataSetCsvFolder.getText() );
    env.getVariables().clear();
    for ( int i = 0; i < wVariables.nrNonEmpty(); i++ ) {
      TableItem item = wVariables.getNonEmpty( i );
      env.getVariables().add( new EnvironmentVariable( item.getText( 1 ), item.getText( 2 ) ) );
    }
  }
}
