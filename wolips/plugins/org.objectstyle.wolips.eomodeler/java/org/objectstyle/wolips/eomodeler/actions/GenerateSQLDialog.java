package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.sql.SQLUtils;

public class GenerateSQLDialog extends Dialog {
  private Button myDropDatabaseButton;
  private Button myDropTablesButton;
  private Button myDropPrimaryKeySupportButton;
  private Button myCreateTablesButton;
  private Button myCreatePrimaryKeySupportButton;
  private Button myCreatePrimaryKeyConstraintsButton;
  private Button myCreateForeignKeyConstraintsButton;
  private Button myCreateDatabaseButton;
  private Text mySqlText;
  private EOModel myModel;
  private List myEntityNames;
  private Map myExtraInfoDictionaries;
  private Combo myExtraInfoCombo;

  public GenerateSQLDialog(Shell _parentShell, EOModel _model, List _entityNames) {
    super(_parentShell);
    myModel = _model;
    myEntityNames = _entityNames;
  }

  protected void configureShell(Shell _newShell) {
    super.configureShell(_newShell);
    _newShell.setText("SQL Generation");
  }

  protected Control createDialogArea(Composite _parent) {
    Composite control = (Composite) super.createDialogArea(_parent);
    GridLayout layout = new GridLayout(1, true);
    layout.marginTop = 10;
    layout.marginLeft = 10;
    layout.marginRight = 10;
    layout.numColumns = 2;
    control.setLayout(layout);

    myExtraInfoDictionaries = myModel.getExtraInfoDictionaries();
    if (myExtraInfoDictionaries.size() > 1) {
      Set extraInfoNamesSet = myExtraInfoDictionaries.keySet();
      String[] extraInfoNames = (String[]) extraInfoNamesSet.toArray(new String[extraInfoNamesSet.size()]);
      myExtraInfoCombo = new Combo(control, SWT.READ_ONLY);
      GridData extraInfoData = new GridData(GridData.FILL_HORIZONTAL);
      extraInfoData.horizontalSpan = 2;
      myExtraInfoCombo.setLayoutData(extraInfoData);
      myExtraInfoCombo.setItems(extraInfoNames);
      myExtraInfoCombo.select(0);
    }

    myDropDatabaseButton = new Button(control, SWT.CHECK);
    myDropDatabaseButton.setText("Drop Database");
    myCreateDatabaseButton = new Button(control, SWT.CHECK);
    myCreateDatabaseButton.setText("Create Database");

    myDropTablesButton = new Button(control, SWT.CHECK);
    myDropTablesButton.setText("Drop Tables");
    myDropTablesButton.setSelection(true);
    myCreateTablesButton = new Button(control, SWT.CHECK);
    myCreateTablesButton.setText("Create Tables");
    myCreateTablesButton.setSelection(true);

    myDropPrimaryKeySupportButton = new Button(control, SWT.CHECK);
    myDropPrimaryKeySupportButton.setText("Drop Primary Keys");
    myDropPrimaryKeySupportButton.setSelection(true);
    myCreatePrimaryKeySupportButton = new Button(control, SWT.CHECK);
    myCreatePrimaryKeySupportButton.setText("Create Primary Key Support");
    myCreatePrimaryKeySupportButton.setSelection(true);

    new Label(control, SWT.NONE);
    myCreatePrimaryKeyConstraintsButton = new Button(control, SWT.CHECK);
    myCreatePrimaryKeyConstraintsButton.setText("Primary Key Constraints");
    myCreatePrimaryKeyConstraintsButton.setSelection(true);

    new Label(control, SWT.NONE);
    myCreateForeignKeyConstraintsButton = new Button(control, SWT.CHECK);
    myCreateForeignKeyConstraintsButton.setText("Foreign Key Constraints");
    myCreateForeignKeyConstraintsButton.setSelection(true);

    mySqlText = new Text(control, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
    GridData sqlTextData = new GridData(GridData.FILL_HORIZONTAL);
    sqlTextData.heightHint = 300;
    sqlTextData.widthHint = 500;
    sqlTextData.verticalIndent = 10;
    sqlTextData.horizontalSpan = 2;
    mySqlText.setLayoutData(sqlTextData);

    return control;
  }

  protected Control createButtonBar(Composite _parent) {
    Composite composite = new Composite(_parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.makeColumnsEqualWidth = true;
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    composite.setLayout(layout);
    GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
    composite.setLayoutData(data);
    composite.setFont(_parent.getFont());
    Button closeButton = new Button(composite, SWT.PUSH);
    closeButton.setText("Close");
    closeButton.addSelectionListener(new CloseHandler());
    Button generateSqlButton = new Button(composite, SWT.PUSH);
    generateSqlButton.setText("View SQL");
    generateSqlButton.addSelectionListener(new GenerateSqlHandler());
    getShell().setDefaultButton(generateSqlButton);
    return composite;
  }

  protected String yesNo(Button _button) {
    return (_button.getSelection()) ? "YES" : "NO";
  }

  public void generateSql() {
    Map flags = new HashMap();
    flags.put("dropTables", yesNo(myDropTablesButton));
    flags.put("dropPrimaryKeySupport", yesNo(myDropPrimaryKeySupportButton));
    flags.put("createTables", yesNo(myCreateTablesButton));
    flags.put("createPrimaryKeySupport", yesNo(myCreatePrimaryKeySupportButton));
    flags.put("primaryKeyConstraints", yesNo(myCreatePrimaryKeyConstraintsButton));
    flags.put("foreignKeyConstraints", yesNo(myCreateForeignKeyConstraintsButton));
    flags.put("createDatabase", yesNo(myCreateDatabaseButton));
    flags.put("dropDatabase", yesNo(myDropDatabaseButton));
    try {
      Map extraInfo = null;
      if (myExtraInfoCombo != null) {
        int selectionIndex = myExtraInfoCombo.getSelectionIndex();
        if (selectionIndex > 0) {
          String extraInfoName = myExtraInfoCombo.getItem(selectionIndex);
          extraInfo = (Map) myExtraInfoDictionaries.get(extraInfoName);
        }
      }
      String sqlScript = SQLUtils.generateSqlScript(myModel, myEntityNames, flags, extraInfo);
      mySqlText.setText(sqlScript);
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public class GenerateSqlHandler implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      GenerateSQLDialog.this.generateSql();
    }
  }

  public class CloseHandler implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      GenerateSQLDialog.this.close();
    }
  }
}
