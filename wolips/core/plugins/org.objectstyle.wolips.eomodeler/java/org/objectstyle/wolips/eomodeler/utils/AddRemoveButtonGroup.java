package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.Messages;

public class AddRemoveButtonGroup extends Composite {
	private Button myAddButton;

	private Button myRemoveButton;

	public AddRemoveButtonGroup(Composite _parent, SelectionListener _addListener, SelectionListener _removeListener) {
		super(_parent, SWT.NONE);
		setBackgroundMode(SWT.INHERIT_FORCE);
		//setBackground(_parent.getBackground());
		FormLayout layout = new FormLayout();
		setLayout(layout);

		myAddButton = new Button(this, SWT.PUSH);
		myAddButton.setText(Messages.getString("button.add"));
		FormData addButtonData = new FormData();
		addButtonData.right = new FormAttachment(100, 0);
		myAddButton.setLayoutData(addButtonData);
		myAddButton.addSelectionListener(_addListener);

		myRemoveButton = new Button(this, SWT.PUSH);
		myRemoveButton.setText(Messages.getString("button.remove"));
		FormData remoteButtonData = new FormData();
		remoteButtonData.right = new FormAttachment(myAddButton, 0);
		myRemoveButton.setLayoutData(remoteButtonData);
		myRemoveButton.addSelectionListener(_removeListener);
	}

	public void setAddEnabled(boolean _addEnabled) {
		myAddButton.setEnabled(_addEnabled);
	}

	public void setRemoveEnabled(boolean _removeEnabled) {
		myRemoveButton.setEnabled(_removeEnabled);
	}
}
