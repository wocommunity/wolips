package org.objectstyle.wolips.eomodeler.actions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.objectstyle.wolips.eomodeler.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.utils.EOModelUtils;

public class CutOperation extends SimpleCompositeOperation {
	private List<EOModelObject> _clipboardObjects;

	private ISelection _previousSelection;
	private long _previousSelectionTime;

	public CutOperation(Object[] objects) {
		super(EOModelUtils.getOperationLabel("Cut", Arrays.asList(objects)));
		_clipboardObjects = new LinkedList<EOModelObject>();
		for (Object obj : objects) {
			if (obj instanceof EOModelObject) {
				EOModelObject<?> eoModelObject = (EOModelObject) obj;
				CutItemOperation operation = new CutItemOperation(eoModelObject);
				add(operation);
				_clipboardObjects.add(eoModelObject._cloneModelObject());
			}
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		_previousSelection = LocalSelectionTransfer.getTransfer().getSelection();
		_previousSelectionTime = LocalSelectionTransfer.getTransfer().getSelectionSetTime();
		IStatus status;
		boolean succeeded = false;
		try {
			status = super.execute(monitor, info);
			if (status.getSeverity() == IStatus.OK) {
				succeeded = true;
			}
		} finally {
			if (succeeded) {
				LocalSelectionTransfer.getTransfer().setSelection(new StructuredSelection(_clipboardObjects));
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(System.currentTimeMillis());
			}
		}
		return status;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus status;
		boolean succeeded = false;
		try {
			status = super.undo(monitor, info);
			if (status.getSeverity() == IStatus.OK) {
				succeeded = true;
			}
		} finally {
			if (succeeded) {
				LocalSelectionTransfer.getTransfer().setSelection(_previousSelection);
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(_previousSelectionTime);
			}
		}
		return status;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		_previousSelection = LocalSelectionTransfer.getTransfer().getSelection();
		_previousSelectionTime = LocalSelectionTransfer.getTransfer().getSelectionSetTime();
		IStatus status;
		boolean succeeded = false;
		try {
			status = super.redo(monitor, info);
			if (status.getSeverity() == IStatus.OK) {
				succeeded = true;
			}
		} finally {
			if (succeeded) {
				LocalSelectionTransfer.getTransfer().setSelection(new StructuredSelection(_clipboardObjects));
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(System.currentTimeMillis());
			}
		}
		return status;
	}
}
