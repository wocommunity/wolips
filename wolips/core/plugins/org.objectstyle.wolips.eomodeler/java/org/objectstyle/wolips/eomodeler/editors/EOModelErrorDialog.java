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
	private Set myFailures;

	private ListViewer myFailureListViewer;

	public EOModelErrorDialog(Shell _parentShell, Set _failures) {
		super(_parentShell);
		myFailures = _failures;
	}

	protected void configureShell(Shell _newShell) {
		super.configureShell(_newShell);
		_newShell.setText("EOModel Verification Failures");
	}

	protected Control createDialogArea(Composite _parent) {
		Composite composite = (Composite) super.createDialogArea(_parent);
		myFailureListViewer = new ListViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL);
		myFailureListViewer.setContentProvider(new FailureContentProvider());
		myFailureListViewer.setLabelProvider(new FailureLabelProvider());
		myFailureListViewer.setSorter(new ViewerSorter());
		myFailureListViewer.setInput(myFailures);
		GridData failuresGridData = new GridData(GridData.FILL_BOTH);
		failuresGridData.widthHint = 800;
		failuresGridData.heightHint = 400;
		myFailureListViewer.getList().setLayoutData(failuresGridData);
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	protected static class FailureLabelProvider implements ILabelProvider {
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
			EOModelVerificationFailure failure = (EOModelVerificationFailure) _element;
			return failure.getMessage();
		}

		public boolean isLabelProperty(Object _element, String _property) {
			return true;
		}

		public void removeListener(ILabelProviderListener _listener) {
			// DO NOTHING
		}
	}

	protected static class FailureContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object _inputElement) {
			Set failures = (Set) _inputElement;
			return failures.toArray();
		}

		public void dispose() {
			// DO NOTHING
		}

		public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
			// DO NOTHING
		}
	}
}
