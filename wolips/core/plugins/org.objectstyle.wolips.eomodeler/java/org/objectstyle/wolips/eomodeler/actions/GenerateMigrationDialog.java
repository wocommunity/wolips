package org.objectstyle.wolips.eomodeler.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.actions.GenerateSQLDialog.DatabaseConfigContentProvider;
import org.objectstyle.wolips.eomodeler.actions.GenerateSQLDialog.DatabaseConfigLabelProvider;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.sql.MigrationGenerator;

public class GenerateMigrationDialog extends Dialog {
	private Button _createSelectedEntitiesButton;

	private Text _migrationText;

	private EOModel _model;

	private List<EOEntity> _entities;

	private Set<EODatabaseConfig> _databaseConfigs;

	private ComboViewer _databaseConfigComboViewer;

	private FlagChangedHandler _flagChangeHander;

	public GenerateMigrationDialog(Shell parentShell, EOModel model, List<EOEntity> entities) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		_model = model;
		_entities = entities;
		_flagChangeHander = new FlagChangedHandler();
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Migration Generation");
	}

	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, true);
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.numColumns = 1;
		control.setLayout(layout);

		_databaseConfigs = _model.getDatabaseConfigs();
		if (_databaseConfigs.size() > 1) {
			_databaseConfigComboViewer = new ComboViewer(control, SWT.READ_ONLY);
			GridData extraInfoData = new GridData(GridData.FILL_HORIZONTAL);
			// extraInfoData.horizontalSpan = 2;
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

		int entityCount = (_entities != null) ? _entities.size() : 0;
		_createSelectedEntitiesButton = new Button(control, SWT.CHECK);
		_createSelectedEntitiesButton.setText("Create Only Selected Entities");
		_createSelectedEntitiesButton.setSelection(entityCount > 0);
		_createSelectedEntitiesButton.addSelectionListener(_flagChangeHander);
		_createSelectedEntitiesButton.setEnabled(entityCount > 0);

		// new Label(control, SWT.NONE);

		_migrationText = new Text(control, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData sqlTextData = new GridData(GridData.FILL_BOTH);
		sqlTextData.heightHint = 300;
		sqlTextData.widthHint = 700;
		sqlTextData.verticalIndent = 10;
		sqlTextData.horizontalSpan = 2;
		_migrationText.setLayoutData(sqlTextData);
		_migrationText.setText("Generating Migration. Please Wait ...");

		generateMigration();

		return control;
	}

	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
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
		getShell().setDefaultButton(closeButton);
		return composite;
	}

	protected String yesNo(Button button) {
		return (button.getSelection()) ? "YES" : "NO";
	}

	protected Text getMigrationText() {
		return _migrationText;
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

	public void generateMigration() {
		try {
			List<EOEntity> entities = _createSelectedEntitiesButton.getSelection() ? _entities : null;
			String migrationText = MigrationGenerator.generate(_model, entities);
			getMigrationText().setText(migrationText);
		} catch (final Throwable t) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getMigrationText().setText("Migration Failed.");
				}
			});
			ErrorUtils.openErrorDialog(getShell(), t);
		}
	}

	protected String getMigrationString() {
		return _migrationText.getText();
	}

	public class FlagChangedHandler implements SelectionListener, ISelectionChangedListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			GenerateMigrationDialog.this.generateMigration();
		}

		public void selectionChanged(SelectionChangedEvent event) {
			GenerateMigrationDialog.this.generateMigration();
		}
	}

	public class CloseHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			GenerateMigrationDialog.this.close();
		}
	}
}
