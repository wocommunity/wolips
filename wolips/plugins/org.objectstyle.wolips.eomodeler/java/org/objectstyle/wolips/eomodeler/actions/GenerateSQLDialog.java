package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.IEOClassLoaderFactory;
import org.objectstyle.wolips.eomodeler.core.sql.IEOSQLGenerator;
import org.objectstyle.wolips.eomodeler.core.sql.IEOSQLGeneratorFactory;

public class GenerateSQLDialog extends Dialog {
	private Button _dropDatabaseButton;

	private Button _dropTablesButton;

	private Button _dropPrimaryKeySupportButton;

	private Button _dropIndexesButton;

	private Button _createSelectedEntitiesButton;

	private Button _createTablesButton;

	private Button _createPrimaryKeySupportButton;

	private Button _createPrimaryKeyConstraintsButton;

	private Button _createForeignKeyConstraintsButton;

	private Button _createDatabaseButton;

	private Button _createIndexesButton;

	private Button _runInEntityModelerButton;

	private Text _sqlText;

	private EOModel _model;

	private List<String> _entityNames;

	private Set<EODatabaseConfig> _databaseConfigs;

	private ComboViewer _databaseConfigComboViewer;

	private ClassLoader _eoModelClassLoader;

	private FlagChangedHandler _flagChangeHander;

	private boolean _cancel;

	private Cursor _waitCursor;

	private Button _executeSqlButton;

	private boolean _createOnlySelectedEntities;

	public GenerateSQLDialog(Shell parentShell, EOModel model, List<String> entityNames) {
		super(parentShell);
		_model = model;
		_entityNames = entityNames;
		_flagChangeHander = new FlagChangedHandler();
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("SQL Generation");
	}

	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, true);
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.numColumns = 2;
		control.setLayout(layout);

		_databaseConfigs = _model.getDatabaseConfigs();
		if (_databaseConfigs.size() > 1) {
			_databaseConfigComboViewer = new ComboViewer(control, SWT.READ_ONLY);
			GridData extraInfoData = new GridData(GridData.FILL_HORIZONTAL);
			extraInfoData.horizontalSpan = 2;
			_databaseConfigComboViewer.setContentProvider(new DatabaseConfigContentProvider());
			_databaseConfigComboViewer.setLabelProvider(new DatabaseConfigLabelProvider());
			_databaseConfigComboViewer.setInput(_databaseConfigs);
			_databaseConfigComboViewer.getCombo().setLayoutData(extraInfoData);
			EODatabaseConfig activeDatabaseConfig = _model.getActiveDatabaseConfig();
			if (activeDatabaseConfig != null) {
				_databaseConfigComboViewer.setSelection(new StructuredSelection(activeDatabaseConfig));
			} else {
				_databaseConfigComboViewer.setSelection(new StructuredSelection(_databaseConfigs.iterator().next()));
			}
			_databaseConfigComboViewer.addSelectionChangedListener(_flagChangeHander);
		}

		_dropDatabaseButton = new Button(control, SWT.CHECK);
		_dropDatabaseButton.setText("Drop Database");
		_dropDatabaseButton.addSelectionListener(_flagChangeHander);
		_createDatabaseButton = new Button(control, SWT.CHECK);
		_createDatabaseButton.setText("Create Database");
		_createDatabaseButton.addSelectionListener(_flagChangeHander);

		_dropTablesButton = new Button(control, SWT.CHECK);
		_dropTablesButton.setText("Drop Tables");
		_dropTablesButton.setSelection(true);
		_dropTablesButton.addSelectionListener(_flagChangeHander);
		_createTablesButton = new Button(control, SWT.CHECK);
		_createTablesButton.setText("Create Tables");
		_createTablesButton.setSelection(true);
		_createTablesButton.addSelectionListener(_flagChangeHander);

		_dropPrimaryKeySupportButton = new Button(control, SWT.CHECK);
		_dropPrimaryKeySupportButton.setText("Drop Primary Keys");
		_dropPrimaryKeySupportButton.setSelection(true);
		_dropPrimaryKeySupportButton.addSelectionListener(_flagChangeHander);
		_createPrimaryKeySupportButton = new Button(control, SWT.CHECK);
		_createPrimaryKeySupportButton.setText("Create Primary Key Support");
		_createPrimaryKeySupportButton.setSelection(true);
		_createPrimaryKeySupportButton.addSelectionListener(_flagChangeHander);

		_dropIndexesButton = new Button(control, SWT.CHECK);
		_dropIndexesButton.setText("Drop Indexes");
		_dropIndexesButton.setSelection(true);
		_dropIndexesButton.addSelectionListener(_flagChangeHander);
		_createIndexesButton = new Button(control, SWT.CHECK);
		_createIndexesButton.setText("Create Indexes");
		_createIndexesButton.setSelection(true);
		_createIndexesButton.addSelectionListener(_flagChangeHander);

		int entityCount = (_entityNames != null) ? _entityNames.size() : 0;
		_createSelectedEntitiesButton = new Button(control, SWT.CHECK);
		_createSelectedEntitiesButton.setText("Create Only Selected Entities");
		_createSelectedEntitiesButton.setSelection(entityCount > 0);
		_createSelectedEntitiesButton.addSelectionListener(_flagChangeHander);
		_createSelectedEntitiesButton.setEnabled(entityCount > 0);
		_createOnlySelectedEntities = (entityCount > 0);
		_createPrimaryKeyConstraintsButton = new Button(control, SWT.CHECK);
		_createPrimaryKeyConstraintsButton.setText("Primary Key Constraints");
		_createPrimaryKeyConstraintsButton.setSelection(true);
		_createPrimaryKeyConstraintsButton.addSelectionListener(_flagChangeHander);

		_runInEntityModelerButton = new Button(control, SWT.CHECK);
		_runInEntityModelerButton.setText("Single Transaction Compatible");
		_runInEntityModelerButton.setSelection(true);
		_runInEntityModelerButton.addSelectionListener(_flagChangeHander);

		_createForeignKeyConstraintsButton = new Button(control, SWT.CHECK);
		_createForeignKeyConstraintsButton.setText("Foreign Key Constraints");
		_createForeignKeyConstraintsButton.setSelection(true);
		_createForeignKeyConstraintsButton.addSelectionListener(_flagChangeHander);

		new Label(control, SWT.NONE);

		_sqlText = new Text(control, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData sqlTextData = new GridData(GridData.FILL_HORIZONTAL);
		sqlTextData.heightHint = 300;
		sqlTextData.widthHint = 500;
		sqlTextData.verticalIndent = 10;
		sqlTextData.horizontalSpan = 2;
		_sqlText.setLayoutData(sqlTextData);
		_sqlText.setText("Generating SQL. Please Wait ...");

		_waitCursor = new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT);
		generateSqlInThread();

		return control;
	}

	public boolean close() {
		boolean close = super.close();
		if (_waitCursor != null) {
			_waitCursor.dispose();
			_waitCursor = null;
		}
		return close;
	}

	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
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
		composite.setFont(parent.getFont());
		Button closeButton = new Button(composite, SWT.PUSH);
		closeButton.setText("Close");
		closeButton.addSelectionListener(new CloseHandler());
		_executeSqlButton = new Button(composite, SWT.PUSH);
		_executeSqlButton.setText("Execute SQL");
		_executeSqlButton.addSelectionListener(new ExecuteSqlHandler());
		getShell().setDefaultButton(closeButton);
		return composite;
	}

	protected String yesNo(Button button) {
		return (button.getSelection()) ? "YES" : "NO";
	}

	protected Text getSqlText() {
		return _sqlText;
	}

	protected EODatabaseConfig getSelectedDatabaseConfig() {
		EODatabaseConfig selectedDatabaseConfig = null;
		if (_databaseConfigComboViewer != null) {
			IStructuredSelection selection = (IStructuredSelection) _databaseConfigComboViewer.getSelection();
			selectedDatabaseConfig = (EODatabaseConfig) selection.getFirstElement();
		} else {
			selectedDatabaseConfig = _model.getActiveDatabaseConfig();
		}
		return selectedDatabaseConfig;
	}

	protected ClassLoader getEOModelClassLoader() throws Exception {
		if (_eoModelClassLoader == null) {
			_eoModelClassLoader = IEOClassLoaderFactory.Utility.createClassLoader(_model);
		}
		return _eoModelClassLoader;
	}

	public void generateSqlInThread() {
		_createOnlySelectedEntities = _createSelectedEntitiesButton.getSelection();
		final Map<String, String> flags = new HashMap<String, String>();
		flags.put("dropTables", yesNo(_dropTablesButton));
		flags.put("dropPrimaryKeySupport", yesNo(_dropPrimaryKeySupportButton));
		flags.put("createTables", yesNo(_createTablesButton));
		flags.put("createPrimaryKeySupport", yesNo(_createPrimaryKeySupportButton));
		flags.put("primaryKeyConstraints", yesNo(_createPrimaryKeyConstraintsButton));
		flags.put("foreignKeyConstraints", yesNo(_createForeignKeyConstraintsButton));
		flags.put("createDatabase", yesNo(_createDatabaseButton));
		flags.put("dropDatabase", yesNo(_dropDatabaseButton));
		flags.put("createIndexes", yesNo(_createIndexesButton));
		flags.put("dropIndexes", yesNo(_dropIndexesButton));
		final EODatabaseConfig selectedDatabaseConfig = getSelectedDatabaseConfig();
		final boolean runInEntityModeler = _runInEntityModelerButton.getSelection();
		Thread generateSqlThread = new Thread(new Runnable() {
			public void run() {
				generateSql(flags, selectedDatabaseConfig, runInEntityModeler);
			}
		}, "Generate SQL");
		generateSqlThread.start();
	}

	protected Button getExecuteSqlButton() {
		return _executeSqlButton;
	}

	protected synchronized void generateSql(Map flags, EODatabaseConfig selectedDatabaseConfig, boolean runInEntityModeler) {
		try {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getShell().setCursor(getWaitCursor());
					getExecuteSqlButton().setEnabled(false);
				}
			});
			IEOSQLGenerator sqlGenerator = IEOSQLGeneratorFactory.Utility.sqlGeneratorFactory().sqlGenerator(_model, getEntityNames(), selectedDatabaseConfig, getEOModelClassLoader(), runInEntityModeler);
			final String sqlScript = sqlGenerator.generateSchemaCreationScript(flags);
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
		return _sqlText.getText();
	}

	public void executeSqlInThread() {
		_createOnlySelectedEntities = _createSelectedEntitiesButton.getSelection();
		boolean confirmed = MessageDialog.openConfirm(getShell(), "Execute SQL", "Are you sure you want to execute this SQL?");
		if (confirmed) {
			final String sqlString = getSqlString();
			final EODatabaseConfig selectedDatabaseConfig = getSelectedDatabaseConfig();
			final boolean runInEntityModeler = _runInEntityModelerButton.getSelection();
			Thread executeSqlThread = new Thread(new Runnable() {
				public void run() {
					executeSql(sqlString, selectedDatabaseConfig, runInEntityModeler);
				}
			}, "Execute SQL");
			executeSqlThread.start();
		}
	}

	protected void setCancel(boolean cancel) {
		_cancel = cancel;
	}

	protected Cursor getWaitCursor() {
		return _waitCursor;
	}

	protected List<String> getEntityNames() {
		return (_createOnlySelectedEntities) ? _entityNames : null;
	}

	protected synchronized void executeSql(String allSql, EODatabaseConfig selectedDatabaseConfig, boolean runInEntityModeler) {
		try {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getShell().setCursor(getWaitCursor());
				}
			});
			IEOSQLGenerator sqlGenerator = IEOSQLGeneratorFactory.Utility.sqlGeneratorFactory().sqlGenerator(_model, getEntityNames(), selectedDatabaseConfig, getEOModelClassLoader(), runInEntityModeler);
			String[] statements = allSql.split("[;/]");
			setCancel(false);
			for (int statementsNum = 0; !_cancel && statementsNum < statements.length; statementsNum++) {
				String statement = statements[statementsNum];
				statement = statement.trim().replaceAll("[\n\r]", " ");
				if (statement.length() > 0) {
					try {
						sqlGenerator.executeSQL(statement);
					} catch (final Throwable t) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								MessageDialog dialog = new MessageDialog(getShell(), "Error", null, StringUtils.getErrorMessage(t) + "\n\nThere was an error, do you want to cancel the rest of the script?", MessageDialog.QUESTION, new String[] { "Cancel", "Continue" }, 0);
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
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			GenerateSQLDialog.this.generateSqlInThread();
		}

		public void selectionChanged(SelectionChangedEvent event) {
			GenerateSQLDialog.this.generateSqlInThread();
		}
	}

	public class ExecuteSqlHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			GenerateSQLDialog.this.executeSqlInThread();
		}
	}

	public class CloseHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			GenerateSQLDialog.this.close();
		}
	}

	protected class DatabaseConfigLabelProvider implements ILabelProvider {
		public void addListener(ILabelProviderListener listener) {
			// DO NOTHING
		}

		public void dispose() {
			// DO NOTHING
		}

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			EODatabaseConfig config = (EODatabaseConfig) element;
			StringBuffer text = new StringBuffer();
			text.append(config.getName());
			text.append(" (");
			text.append(config.getUsername());
			text.append(" @ ");
			text.append(config.getURL());
			text.append(")");
			return text.toString();
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
			// DO NOTHING
		}

	}

	protected class DatabaseConfigContentProvider implements IStructuredContentProvider {
		public void dispose() {
			// DO NOTHING
		}

		public Object[] getElements(Object inputElement) {
			return ((Set) inputElement).toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// DO NOTHING
		}

	}
}
