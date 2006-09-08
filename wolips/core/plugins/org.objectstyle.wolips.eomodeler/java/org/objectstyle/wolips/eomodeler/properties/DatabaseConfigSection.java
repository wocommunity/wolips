package org.objectstyle.wolips.eomodeler.properties;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.entity.EOPrototypeEntityListContentProvider;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public class DatabaseConfigSection extends ConnectionDictionarySection {
	private Text myNameText;

	private ComboViewer myPrototypeComboViewer;

	private ComboViewerBinding myPrototypeBinding;

	public DatabaseConfigSection() {
		// TODO Auto-generated constructor stub
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
	}

	protected void disposeBindings() {
		if (myPrototypeBinding != null) {
			myPrototypeBinding.dispose();
		}
		super.disposeBindings();
	}

	protected void addBindings(DataBindingContext _context) {
		_context.bind(myNameText, new Property(getConnectionDictionaryOwner(), EODatabaseConfig.NAME), null);
		myPrototypeComboViewer.setInput(getConnectionDictionaryOwner());
		myPrototypeBinding = new ComboViewerBinding(myPrototypeComboViewer, (EODatabaseConfig) getConnectionDictionaryOwner(), EODatabaseConfig.PROTOTYPE, null, null, EOPrototypeEntityListContentProvider.BLANK_ENTITY);
		super.addBindings(_context);
	}
}
