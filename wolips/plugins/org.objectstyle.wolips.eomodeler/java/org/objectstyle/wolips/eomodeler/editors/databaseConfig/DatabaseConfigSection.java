package org.objectstyle.wolips.eomodeler.editors.databaseConfig;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.entity.EOPrototypeEntityListContentProvider;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.StringLabelProvider;

public class DatabaseConfigSection extends AbstractPropertySection {
	private DataBindingContext _bindingContext;

	private Text _nameText;

	private Text _priorityText;

	private Text _deploymentProfileText;

	private ComboViewer _prototypeComboViewer;

	private ComboViewerBinding _prototypeBinding;

	private ComboViewer _adaptorNameComboViewer;

	private ComboViewerBinding _adaptorNameBinding;

	private Button _makeActiveButton;

	private ActiveDatabaseConfigHandler _activeDatabaseConfigHandler;

	private AdaptorNameHandler _adaptorNameHandler;

	private EODatabaseConfig _databaseConfig;

	private Composite _connectionDictionaryContainer;

	private IConnectionDictionarySection _connectionDictionarySection;

	private String _lastAdaptorName;

	private Composite _form;

	public DatabaseConfigSection() {
		_activeDatabaseConfigHandler = new ActiveDatabaseConfigHandler();
		_adaptorNameHandler = new AdaptorNameHandler();
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		_form = getWidgetFactory().createFlatFormComposite(parent);
		FormLayout formLayout = new FormLayout();
		_form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), _form);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EODatabaseConfig." + EODatabaseConfig.NAME), SWT.NONE);
		_nameText = new Text(topForm, SWT.BORDER);
		_nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EODatabaseConfig." + EODatabaseConfig.DEPLOYMENT_PROFILE), SWT.NONE);
		_deploymentProfileText = new Text(topForm, SWT.BORDER);
		_deploymentProfileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EODatabaseConfig." + EODatabaseConfig.PRIORITY), SWT.NONE);
		_priorityText = new Text(topForm, SWT.BORDER);
		_priorityText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EODatabaseConfig." + EODatabaseConfig.PROTOTYPE), SWT.NONE);
		_prototypeComboViewer = new ComboViewer(topForm, SWT.READ_ONLY);
		_prototypeComboViewer.setContentProvider(new EOPrototypeEntityListContentProvider(true));
		_prototypeComboViewer.setLabelProvider(new EOEntityLabelProvider());
		_prototypeComboViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		getWidgetFactory().createCLabel(topForm, Messages.getString("EODatabaseConfig." + EODatabaseConfig.ADAPTOR_NAME), SWT.NONE);
		_adaptorNameComboViewer = new ComboViewer(topForm, SWT.READ_ONLY);
		_adaptorNameComboViewer.setContentProvider(new AdaptorNameContentProvider());
		_adaptorNameComboViewer.setLabelProvider(new StringLabelProvider());
		_adaptorNameComboViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_connectionDictionaryContainer = new Composite(topForm, SWT.NONE);
		_connectionDictionaryContainer.setBackground(topForm.getBackground());
		GridLayout connectionDictionaryLayout = new GridLayout();
		connectionDictionaryLayout.marginLeft = 0;
		connectionDictionaryLayout.marginWidth = 0;
		connectionDictionaryLayout.marginRight = 0;
		connectionDictionaryLayout.marginHeight = 0;
		connectionDictionaryLayout.marginTop = 0;
		connectionDictionaryLayout.marginBottom = 0;
		_connectionDictionaryContainer.setLayout(connectionDictionaryLayout);

		GridData connectionDictionaryData = new GridData(GridData.FILL_HORIZONTAL);
		connectionDictionaryData.horizontalSpan = 2;
		connectionDictionaryData.grabExcessHorizontalSpace = true;
		_connectionDictionaryContainer.setLayoutData(connectionDictionaryData);

		// add entries

		_makeActiveButton = getWidgetFactory().createButton(topForm, Messages.getString("EODatabaseConfig.makeActiveLabel"), SWT.PUSH);
		GridData makeActiveData = new GridData();
		makeActiveData.horizontalSpan = 2;
		_makeActiveButton.setLayoutData(makeActiveData);
		_makeActiveButton.addSelectionListener(new MakeActiveHandler());
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		disposeBindings();

		super.setInput(part, selection);

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_databaseConfig = (EODatabaseConfig) selectedObject;

		if (_databaseConfig != null) {
			_bindingContext = new DataBindingContext();
			addBindings();
		}

		adaptorNameChanged();
	}

	protected void adaptorNameChanged() {
		if (_databaseConfig != null) {
			String adaptorName = _databaseConfig.getAdaptorName();
			if (adaptorName == null || !adaptorName.equals(_lastAdaptorName)) {
				if (_connectionDictionarySection != null) {
					_connectionDictionarySection.dispose();
					_connectionDictionarySection = null;
				}

				IConnectionDictionarySection connectionDictionarySection;
				if (EODatabaseConfig.JDBC_ADAPTOR_NAME.equals(adaptorName)) {
					connectionDictionarySection = new JDBCConnectionDictionarySection(_connectionDictionaryContainer, SWT.NONE, getWidgetFactory());
				} else if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(adaptorName)) {
					connectionDictionarySection = new JNDIConnectionDictionarySection(_connectionDictionaryContainer, SWT.NONE, getWidgetFactory());
				} else if (EODatabaseConfig.REST_ADAPTOR_NAME.equals(adaptorName)) {
					connectionDictionarySection = new RESTConnectionDictionarySection(_connectionDictionaryContainer, SWT.NONE, getWidgetFactory());
				} else {
					connectionDictionarySection = new BlankConnectionDictionarySection(_connectionDictionaryContainer, SWT.NONE);
				}
				GridData connectionDictionaryData = new GridData(GridData.FILL_HORIZONTAL);
				connectionDictionaryData.grabExcessHorizontalSpace = true;
				((Composite) connectionDictionarySection).setLayoutData(connectionDictionaryData);
				_connectionDictionarySection = connectionDictionarySection;
			}
			_lastAdaptorName = adaptorName;
			if (_connectionDictionarySection != null) {
				_connectionDictionarySection.setInput(_databaseConfig);
			}
		} else {
			if (_connectionDictionarySection != null) {
				_connectionDictionarySection.dispose();
				_connectionDictionarySection = null;
			}
			_lastAdaptorName = null;
		}

		_form.layout();
	}

	public EODatabaseConfig getDatabaseConfig() {
		return _databaseConfig;
	}

	public void dispose() {
		disposeBindings();
		if (_connectionDictionarySection != null) {
			_connectionDictionarySection.dispose();
		}
		super.dispose();
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
		if (_prototypeBinding != null) {
			_prototypeBinding.dispose();
		}
		if (_adaptorNameBinding != null) {
			// _adaptorNameComboViewer.getCombo().removeModifyListener(_adaptorNameBinding);
			_adaptorNameBinding.dispose();
		}
		EODatabaseConfig databaseConfig = getDatabaseConfig();
		if (databaseConfig != null) {
			EOModel model = databaseConfig.getModel();
			if (model != null) {
				model.removePropertyChangeListener(EOModel.ACTIVE_DATABASE_CONFIG, _activeDatabaseConfigHandler);
			}
			databaseConfig.removePropertyChangeListener(EODatabaseConfig.ADAPTOR_NAME, _adaptorNameHandler);
		}
	}

	protected void addBindings() {
		_bindingContext.bindValue(SWTObservables.observeText(_nameText, SWT.Modify), BeansObservables.observeValue(getDatabaseConfig(), EODatabaseConfig.NAME), null, null);
		_bindingContext.bindValue(SWTObservables.observeText(_priorityText, SWT.Modify), BeansObservables.observeValue(getDatabaseConfig(), EODatabaseConfig.PRIORITY), null, null);
		_bindingContext.bindValue(SWTObservables.observeText(_deploymentProfileText, SWT.Modify), BeansObservables.observeValue(getDatabaseConfig(), EODatabaseConfig.DEPLOYMENT_PROFILE), null, null);
		_prototypeComboViewer.setInput(getDatabaseConfig());
		_prototypeBinding = new ComboViewerBinding(_prototypeComboViewer, getDatabaseConfig(), EODatabaseConfig.PROTOTYPE, null, null, EOPrototypeEntityListContentProvider.BLANK_ENTITY);
		_adaptorNameComboViewer.setInput(getDatabaseConfig());
		_adaptorNameBinding = new ComboViewerBinding(_adaptorNameComboViewer, getDatabaseConfig(), EODatabaseConfig.ADAPTOR_NAME, null, null, null);
		// _adaptorNameComboViewer.getCombo().addModifyListener(_adaptorNameBinding);

		EODatabaseConfig databaseConfig = getDatabaseConfig();
		if (databaseConfig != null) {
			EOModel model = databaseConfig.getModel();
			if (model != null) {
				model.addPropertyChangeListener(EOModel.ACTIVE_DATABASE_CONFIG, _activeDatabaseConfigHandler);
			}
			databaseConfig.addPropertyChangeListener(EODatabaseConfig.ADAPTOR_NAME, _adaptorNameHandler);
		}
		activeDatabaseConfigChanged();
	}

	protected void makeActive() {
		EODatabaseConfig databaseConfig = getDatabaseConfig();
		if (databaseConfig != null) {
			databaseConfig.setActive();
		}
	}

	protected void activeDatabaseConfigChanged() {
		EODatabaseConfig databaseConfig = getDatabaseConfig();
		if (databaseConfig != null) {
			_makeActiveButton.setEnabled(!databaseConfig.isActive());
		}
	}

	protected class ActiveDatabaseConfigHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			DatabaseConfigSection.this.activeDatabaseConfigChanged();
		}
	}

	protected class AdaptorNameHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			DatabaseConfigSection.this.adaptorNameChanged();
		}
	}

	protected class MakeActiveHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			DatabaseConfigSection.this.makeActive();
		}
	}
}
