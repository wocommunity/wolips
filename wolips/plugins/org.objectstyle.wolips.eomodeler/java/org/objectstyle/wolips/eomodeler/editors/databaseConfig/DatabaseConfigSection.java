package org.objectstyle.wolips.eomodeler.editors.databaseConfig;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.entity.EOPrototypeEntityListContentProvider;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public class DatabaseConfigSection extends ConnectionDictionarySection {
	private Text myNameText;

	private ComboViewer myPrototypeComboViewer;

	private ComboViewerBinding myPrototypeBinding;

	private Button myMakeActiveButton;

	private ActiveDatabaseConfigHandler myActiveDatabaseConfigHandler;

	public DatabaseConfigSection() {
		myActiveDatabaseConfigHandler = new ActiveDatabaseConfigHandler();
	}

	protected void addFormEntriesAbove(Composite _form) {
		getWidgetFactory().createCLabel(_form, Messages.getString("EODatabaseConfig." + EODatabaseConfig.NAME), SWT.NONE);
		myNameText = new Text(_form, SWT.BORDER);
		myNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	protected void addFormEntriesBelow(Composite _form) {
		getWidgetFactory().createCLabel(_form, Messages.getString("EODatabaseConfig." + EODatabaseConfig.PROTOTYPE), SWT.NONE);
		myPrototypeComboViewer = new ComboViewer(_form, SWT.READ_ONLY);
		myPrototypeComboViewer.setContentProvider(new EOPrototypeEntityListContentProvider(true));
		myPrototypeComboViewer.setLabelProvider(new EOEntityLabelProvider());
		myPrototypeComboViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		myMakeActiveButton = getWidgetFactory().createButton(_form, Messages.getString("EODatabaseConfig.makeActiveLabel"), SWT.PUSH);
		GridData makeActiveData = new GridData();
		makeActiveData.horizontalSpan = 2;
		myMakeActiveButton.setLayoutData(makeActiveData);
		myMakeActiveButton.addSelectionListener(new MakeActiveHandler());
	}

	protected void disposeBindings() {
		if (myPrototypeBinding != null) {
			myPrototypeBinding.dispose();
		}
		EODatabaseConfig databaseConfig = getDatabaseConfig();
		if (databaseConfig != null) {
			EOModel model = databaseConfig.getModel();
			if (model != null) {
				model.removePropertyChangeListener(EOModel.ACTIVE_DATABASE_CONFIG, myActiveDatabaseConfigHandler);
			}
		}
		super.disposeBindings();
	}

	protected void addBindings(DataBindingContext _context) {
		_context.bind(myNameText, new Property(getDatabaseConfig(), EODatabaseConfig.NAME), null);
		myPrototypeComboViewer.setInput(getDatabaseConfig());
		myPrototypeBinding = new ComboViewerBinding(myPrototypeComboViewer, getDatabaseConfig(), EODatabaseConfig.PROTOTYPE, null, null, EOPrototypeEntityListContentProvider.BLANK_ENTITY);
		EODatabaseConfig databaseConfig = getDatabaseConfig();
		if (databaseConfig != null) {
			EOModel model = databaseConfig.getModel();
			if (model != null) {
				model.addPropertyChangeListener(EOModel.ACTIVE_DATABASE_CONFIG, myActiveDatabaseConfigHandler);
			}
		}
		activeDatabaseConfigChanged();
		super.addBindings(_context);
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
			myMakeActiveButton.setEnabled(!databaseConfig.isActive());
		}
	}

	protected class ActiveDatabaseConfigHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			DatabaseConfigSection.this.activeDatabaseConfigChanged();
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
