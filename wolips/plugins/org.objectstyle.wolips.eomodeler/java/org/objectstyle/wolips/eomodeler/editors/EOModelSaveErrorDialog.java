package org.objectstyle.wolips.eomodeler.editors;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EOModelSaveErrorDialog extends EOModelErrorDialog {
	public EOModelSaveErrorDialog(Shell parentShell, Set<? extends EOModelVerificationFailure> failures) {
		super(parentShell, failures);
	}

	public EOModelSaveErrorDialog(Shell parentShell, Set<? extends EOModelVerificationFailure> failures, EOModelEditor editor) {
		super(parentShell, failures, editor);
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		createButton(parent, IDialogConstants.OK_ID, "Save", true);
	}
}
