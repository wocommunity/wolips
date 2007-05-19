package org.objectstyle.wolips.eomodeler.editors;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EOModelErrorDialog extends Dialog {
	public static final int DELETE_ANYWAY_ID = 100;
	
	private boolean _showDeleteAnywayButton;

	private Set _failures;

	private ListViewer _failureListViewer;

	public EOModelErrorDialog(Shell parentShell, Set failures) {
		this(parentShell, failures, false);
	}

	public EOModelErrorDialog(Shell parentShell, Set failures, boolean showDeleteAnywayButton) {
		super(parentShell);
		_failures = failures;
		_showDeleteAnywayButton = showDeleteAnywayButton;
	}

	protected void configureShell(Shell _newShell) {
		super.configureShell(_newShell);
		_newShell.setText("EOModel Verification Failures");
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		_failureListViewer = new ListViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL);
		_failureListViewer.setContentProvider(new FailureContentProvider());
		_failureListViewer.setLabelProvider(new FailureLabelProvider());
		_failureListViewer.setSorter(new ViewerSorter());
		_failureListViewer.setInput(_failures);
		GridData failuresGridData = new GridData(GridData.FILL_BOTH);
		failuresGridData.widthHint = 800;
		failuresGridData.heightHint = 400;
		_failureListViewer.getList().setLayoutData(failuresGridData);
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		if (_showDeleteAnywayButton) {
			createButton(parent, EOModelErrorDialog.DELETE_ANYWAY_ID, "Delete Anyway", false);
		}
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == EOModelErrorDialog.DELETE_ANYWAY_ID) {
			setReturnCode(EOModelErrorDialog.DELETE_ANYWAY_ID);
			close();
		}
		else {
			super.buttonPressed(buttonId);
		}
	}

	protected static class FailureLabelProvider implements ILabelProvider {
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
			EOModelVerificationFailure failure = (EOModelVerificationFailure) element;
			return failure.getMessage();
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
			// DO NOTHING
		}
	}

	protected static class FailureContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			Set failures = (Set) inputElement;
			return failures.toArray();
		}

		public void dispose() {
			// DO NOTHING
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// DO NOTHING
		}
	}
}
