package org.objectstyle.wolips.eomodeler.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.sql.SQLUtils;
import org.objectstyle.wolips.eomodeler.utils.ClasspathUtils;

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

	private Set myDatabaseConfigs;

	private ComboViewer myDatabaseConfigComboViewer;

	private ClassLoader myEOModelClassLoader;

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

		myDatabaseConfigs = myModel.getDatabaseConfigs(true);
		if (myDatabaseConfigs.size() > 1) {
			myDatabaseConfigComboViewer = new ComboViewer(control, SWT.READ_ONLY);
			GridData extraInfoData = new GridData(GridData.FILL_HORIZONTAL);
			extraInfoData.horizontalSpan = 2;
			myDatabaseConfigComboViewer.setContentProvider(new DatabaseConfigContentProvider());
			myDatabaseConfigComboViewer.setLabelProvider(new DatabaseConfigLabelProvider());
			myDatabaseConfigComboViewer.setInput(myDatabaseConfigs);
			myDatabaseConfigComboViewer.getCombo().setLayoutData(extraInfoData);
			myDatabaseConfigComboViewer.setSelection(new StructuredSelection(myDatabaseConfigs.iterator().next()));
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
		layout.numColumns = 3;
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
		Button executeSqlButton = new Button(composite, SWT.PUSH);
		executeSqlButton.setText("Execute SQL");
		executeSqlButton.addSelectionListener(new ExecuteSqlHandler());
		getShell().setDefaultButton(generateSqlButton);
		return composite;
	}

	protected String yesNo(Button _button) {
		return (_button.getSelection()) ? "YES" : "NO";
	}

	public synchronized void generateSql() {
		Text sqlText = mySqlText;
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
			Object eofSQLGenerator = SQLUtils.createEOFSQLGenerator(myModel, myEntityNames, getSelectedDatabaseConfigMap(), getEOModelClassLoader());
			Method getSchemaCreationScriptMethod = eofSQLGenerator.getClass().getMethod("getSchemaCreationScript", new Class[] { Map.class });
			String sqlScript = (String) getSchemaCreationScriptMethod.invoke(eofSQLGenerator, new Object[] { flags });
			sqlText.setText(sqlScript);
		} catch (Throwable t) {
			MessageDialog.openError(getShell(), "Error", getErrorMessage(t));
		}
	}

	protected Map getSelectedDatabaseConfigMap() {
		EODatabaseConfig selectedDatabaseConfig = null;
		if (myDatabaseConfigComboViewer != null) {
			IStructuredSelection selection = (IStructuredSelection) myDatabaseConfigComboViewer.getSelection();
			selectedDatabaseConfig = (EODatabaseConfig) selection.getFirstElement();
		} else {
			selectedDatabaseConfig = myModel.createDefaultDatabaseConfig();
		}
		Map selectedDatabaseConfigMap = null;
		if (selectedDatabaseConfig != null) {
			selectedDatabaseConfigMap = selectedDatabaseConfig.toMap().getBackingMap();
		}
		return selectedDatabaseConfigMap;
	}

	protected ClassLoader getEOModelClassLoader() throws MalformedURLException, JavaModelException {
		if (myEOModelClassLoader == null) {
			myEOModelClassLoader = ClasspathUtils.createEOModelClassLoader(myModel);
		}
		return myEOModelClassLoader;
	}

	public synchronized void executeSql() {
		try {
			generateSql();
			Object eofSQLGenerator = SQLUtils.createEOFSQLGenerator(myModel, myEntityNames, getSelectedDatabaseConfigMap(), getEOModelClassLoader());
			Method executeSQLMethod = eofSQLGenerator.getClass().getMethod("executeSQL", new Class[] { String.class });
			String allSql = mySqlText.getText();
			String[] statements = allSql.split(";");
			boolean cancel = false;
			for (int statementsNum = 0; !cancel && statementsNum < statements.length; statementsNum++) {
				String statement = statements[statementsNum];
				statement = statement.trim().replaceAll("[\n\r]", " ");
				if (statement.length() > 0) {
					try {
						executeSQLMethod.invoke(eofSQLGenerator, new Object[] { statement });
					} catch (Throwable t) {
						cancel = MessageDialog.openQuestion(getShell(), "Error", getErrorMessage(t) + "  Do you want to cancel the rest of the script?");
					}
				}
			}
		} catch (Throwable t) {
			MessageDialog.openError(getShell(), "Error", getErrorMessage(t));
		}
	}

	protected String getErrorMessage(Throwable _t) {
		_t.printStackTrace();
		String message;
		if (_t instanceof InvocationTargetException) {
			Throwable cause = _t.getCause();
			message = cause.getClass().getName() + ": " + cause.getMessage();
		} else {
			message = _t.getClass().getName() + ": " + _t.getMessage();
		}
		return message;
	}

	public class GenerateSqlHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			GenerateSQLDialog.this.generateSql();
		}
	}

	public class ExecuteSqlHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			GenerateSQLDialog.this.executeSql();
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

	protected class DatabaseConfigLabelProvider implements ILabelProvider {
		public void addListener(ILabelProviderListener _listener) {
			// DO NOTHING
		}

		public void dispose() {
			// DO NOTHING
		}

		public Image getImage(Object _element) {
			return null;
		}

		public String getText(Object _element) {
			EODatabaseConfig config = (EODatabaseConfig) _element;
			StringBuffer text = new StringBuffer();
			text.append(config.getName());
			text.append(" (");
			text.append(config.getUsername());
			text.append(" @ ");
			text.append(config.getURL());
			text.append(")");
			return text.toString();
		}

		public boolean isLabelProperty(Object _element, String _property) {
			return true;
		}

		public void removeListener(ILabelProviderListener _listener) {
			// DO NOTHING
		}

	}

	protected class DatabaseConfigContentProvider implements IStructuredContentProvider {
		public void dispose() {
			// DO NOTHING
		}

		public Object[] getElements(Object _inputElement) {
			return ((Set) _inputElement).toArray();
		}

		public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
			// DO NOTHING
		}

	}
}
