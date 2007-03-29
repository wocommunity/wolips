package org.objectstyle.wolips.eomodeler.actions;

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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.sql.SQLUtils;
import org.objectstyle.wolips.eomodeler.utils.ClasspathUtils;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

public class GenerateSQLDialog extends Dialog {
	private Button myDropDatabaseButton;

	private Button myDropTablesButton;

	private Button myDropPrimaryKeySupportButton;

	private Button myCreateSelectedEntitiesButton;

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

	private FlagChangedHandler myFlagChangeHander;

	private boolean myCancel;

	private Cursor myWaitCursor;

	private Button myExecuteSqlButton;
	
	private boolean myCreateOnlySelectedEntities;

	public GenerateSQLDialog(Shell _parentShell, EOModel _model, List _entityNames) {
		super(_parentShell);
		myModel = _model;
		myEntityNames = _entityNames;
		myFlagChangeHander = new FlagChangedHandler();
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

		myDatabaseConfigs = myModel.getDatabaseConfigs();
		if (myDatabaseConfigs.size() > 1) {
			myDatabaseConfigComboViewer = new ComboViewer(control, SWT.READ_ONLY);
			GridData extraInfoData = new GridData(GridData.FILL_HORIZONTAL);
			extraInfoData.horizontalSpan = 2;
			myDatabaseConfigComboViewer.setContentProvider(new DatabaseConfigContentProvider());
			myDatabaseConfigComboViewer.setLabelProvider(new DatabaseConfigLabelProvider());
			myDatabaseConfigComboViewer.setInput(myDatabaseConfigs);
			myDatabaseConfigComboViewer.getCombo().setLayoutData(extraInfoData);
			EODatabaseConfig activeDatabaseConfig = myModel.getActiveDatabaseConfig();
			if (activeDatabaseConfig != null) {
				myDatabaseConfigComboViewer.setSelection(new StructuredSelection(activeDatabaseConfig));
			} else {
				myDatabaseConfigComboViewer.setSelection(new StructuredSelection(myDatabaseConfigs.iterator().next()));
			}
			myDatabaseConfigComboViewer.addSelectionChangedListener(myFlagChangeHander);
		}

		myDropDatabaseButton = new Button(control, SWT.CHECK);
		myDropDatabaseButton.setText("Drop Database");
		myDropDatabaseButton.addSelectionListener(myFlagChangeHander);
		myCreateDatabaseButton = new Button(control, SWT.CHECK);
		myCreateDatabaseButton.setText("Create Database");
		myCreateDatabaseButton.addSelectionListener(myFlagChangeHander);

		myDropTablesButton = new Button(control, SWT.CHECK);
		myDropTablesButton.setText("Drop Tables");
		myDropTablesButton.setSelection(true);
		myDropTablesButton.addSelectionListener(myFlagChangeHander);
		myCreateTablesButton = new Button(control, SWT.CHECK);
		myCreateTablesButton.setText("Create Tables");
		myCreateTablesButton.setSelection(true);
		myCreateTablesButton.addSelectionListener(myFlagChangeHander);

		myDropPrimaryKeySupportButton = new Button(control, SWT.CHECK);
		myDropPrimaryKeySupportButton.setText("Drop Primary Keys");
		myDropPrimaryKeySupportButton.setSelection(true);
		myDropPrimaryKeySupportButton.addSelectionListener(myFlagChangeHander);
		myCreatePrimaryKeySupportButton = new Button(control, SWT.CHECK);
		myCreatePrimaryKeySupportButton.setText("Create Primary Key Support");
		myCreatePrimaryKeySupportButton.setSelection(true);
		myCreatePrimaryKeySupportButton.addSelectionListener(myFlagChangeHander);

		int entityCount = (myEntityNames != null) ? myEntityNames.size() : 0;
		myCreateSelectedEntitiesButton = new Button(control, SWT.CHECK);
		myCreateSelectedEntitiesButton.setText("Create Only Selected Entities");
		myCreateSelectedEntitiesButton.setSelection(entityCount > 0);
		myCreateSelectedEntitiesButton.addSelectionListener(myFlagChangeHander);
		myCreateSelectedEntitiesButton.setEnabled(entityCount > 0);
		myCreateOnlySelectedEntities = (entityCount > 0);
		myCreatePrimaryKeyConstraintsButton = new Button(control, SWT.CHECK);
		myCreatePrimaryKeyConstraintsButton.setText("Primary Key Constraints");
		myCreatePrimaryKeyConstraintsButton.setSelection(true);
		myCreatePrimaryKeyConstraintsButton.addSelectionListener(myFlagChangeHander);

		new Label(control, SWT.NONE);
		myCreateForeignKeyConstraintsButton = new Button(control, SWT.CHECK);
		myCreateForeignKeyConstraintsButton.setText("Foreign Key Constraints");
		myCreateForeignKeyConstraintsButton.setSelection(true);
		myCreateForeignKeyConstraintsButton.addSelectionListener(myFlagChangeHander);

		mySqlText = new Text(control, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData sqlTextData = new GridData(GridData.FILL_HORIZONTAL);
		sqlTextData.heightHint = 300;
		sqlTextData.widthHint = 500;
		sqlTextData.verticalIndent = 10;
		sqlTextData.horizontalSpan = 2;
		mySqlText.setLayoutData(sqlTextData);
		mySqlText.setText("Generating SQL. Please Wait ...");

		myWaitCursor = new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT);
		generateSqlInThread();

		return control;
	}

	public boolean close() {
		boolean close = super.close();
		if (myWaitCursor != null) {
			myWaitCursor.dispose();
			myWaitCursor = null;
		}
		return close;
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
		myExecuteSqlButton = new Button(composite, SWT.PUSH);
		myExecuteSqlButton.setText("Execute SQL");
		myExecuteSqlButton.addSelectionListener(new ExecuteSqlHandler());
		getShell().setDefaultButton(closeButton);
		return composite;
	}

	protected String yesNo(Button _button) {
		return (_button.getSelection()) ? "YES" : "NO";
	}

	protected Text getSqlText() {
		return mySqlText;
	}

	protected Map getSelectedDatabaseConfigMap() {
		EODatabaseConfig selectedDatabaseConfig = null;
		if (myDatabaseConfigComboViewer != null) {
			IStructuredSelection selection = (IStructuredSelection) myDatabaseConfigComboViewer.getSelection();
			selectedDatabaseConfig = (EODatabaseConfig) selection.getFirstElement();
		} else {
			selectedDatabaseConfig = myModel.getActiveDatabaseConfig();
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

	public void generateSqlInThread() {
		myCreateOnlySelectedEntities = myCreateSelectedEntitiesButton.getSelection();
		final Map<String, String> flags = new HashMap<String, String>();
		flags.put("dropTables", yesNo(myDropTablesButton));
		flags.put("dropPrimaryKeySupport", yesNo(myDropPrimaryKeySupportButton));
		flags.put("createTables", yesNo(myCreateTablesButton));
		flags.put("createPrimaryKeySupport", yesNo(myCreatePrimaryKeySupportButton));
		flags.put("primaryKeyConstraints", yesNo(myCreatePrimaryKeyConstraintsButton));
		flags.put("foreignKeyConstraints", yesNo(myCreateForeignKeyConstraintsButton));
		flags.put("createDatabase", yesNo(myCreateDatabaseButton));
		flags.put("dropDatabase", yesNo(myDropDatabaseButton));
		final Map selectedDatabaseConfigMap = getSelectedDatabaseConfigMap();
		Thread generateSqlThread = new Thread(new Runnable() {
			public void run() {
				generateSql(flags, selectedDatabaseConfigMap);
			}
		}, "Generate SQL");
		generateSqlThread.start();
	}

	protected Button getExecuteSqlButton() {
		return myExecuteSqlButton;
	}

	protected synchronized void generateSql(Map flags, Map selectedDatabaseConfigMap) {
		try {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getShell().setCursor(getWaitCursor());
					getExecuteSqlButton().setEnabled(false);
				}
			});
			Object eofSQLGenerator = SQLUtils.createEOFSQLGenerator(myModel, getEntityNames(), selectedDatabaseConfigMap, getEOModelClassLoader());
			Method getSchemaCreationScriptMethod = eofSQLGenerator.getClass().getMethod("getSchemaCreationScript", new Class[] { Map.class });
			final String sqlScript = (String) getSchemaCreationScriptMethod.invoke(eofSQLGenerator, new Object[] { flags });
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getSqlText().setText(sqlScript);
					getExecuteSqlButton().setEnabled(true);
				}
			});
		} catch (final Throwable t) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getSqlText().setText("Generation Failed.");
				}
			});
			ErrorUtils.openErrorDialog(getShell(), t);
		} finally {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getShell().setCursor(null);
				}
			});
		}
	}

	protected String getSqlString() {
		return mySqlText.getText();
	}

	public void executeSqlInThread() {
		myCreateOnlySelectedEntities = myCreateSelectedEntitiesButton.getSelection();
		boolean confirmed = MessageDialog.openConfirm(getShell(), "Execute SQL", "Are you sure you want to execute this SQL?");
		if (confirmed) {
			final String sqlString = getSqlString();
			final Map selectedDatabaseConfigMap = getSelectedDatabaseConfigMap();
			Thread executeSqlThread = new Thread(new Runnable() {
				public void run() {
					executeSql(sqlString, selectedDatabaseConfigMap);
				}
			}, "Execute SQL");
			executeSqlThread.start();
		}
	}

	protected void setCancel(boolean cancel) {
		myCancel = cancel;
	}

	protected Cursor getWaitCursor() {
		return myWaitCursor;
	}
	
	protected List getEntityNames() {
		return (myCreateOnlySelectedEntities) ? myEntityNames : null;
	}

	protected synchronized void executeSql(String allSql, Map selectedDatabaseConfigMap) {
		try {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getShell().setCursor(getWaitCursor());
				}
			});
			Object eofSQLGenerator = SQLUtils.createEOFSQLGenerator(myModel, getEntityNames(), selectedDatabaseConfigMap, getEOModelClassLoader());
			Method executeSQLMethod = eofSQLGenerator.getClass().getMethod("executeSQL", new Class[] { String.class });
			String[] statements = allSql.split("[;/]");
			setCancel(false);
			for (int statementsNum = 0; !myCancel && statementsNum < statements.length; statementsNum++) {
				String statement = statements[statementsNum];
				statement = statement.trim().replaceAll("[\n\r]", " ");
				if (statement.length() > 0) {
					try {
						executeSQLMethod.invoke(eofSQLGenerator, new Object[] { statement });
					} catch (final Throwable t) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								ErrorUtils.openErrorDialog(getShell(), t);
								MessageDialog dialog = new MessageDialog(getShell(), "Error", null, "There was an error, do you want to cancel the rest of the script?", MessageDialog.QUESTION, new String[] { "Cancel", "Continue" }, 0);
								int results = dialog.open();
								boolean cancel = (results == 0);
								setCancel(cancel);
							}
						});
					}
				}
			}
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(getShell(), "Done", "SQL Execution Complete");
				}
			});
		} catch (final Throwable t) {
			ErrorUtils.openErrorDialog(getShell(), t);
		} finally {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getShell().setCursor(null);
				}
			});
		}
	}

	public class FlagChangedHandler implements SelectionListener, ISelectionChangedListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			GenerateSQLDialog.this.generateSqlInThread();
		}

		public void selectionChanged(SelectionChangedEvent event) {
			GenerateSQLDialog.this.generateSqlInThread();
		}
	}

	public class ExecuteSqlHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			GenerateSQLDialog.this.executeSqlInThread();
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
