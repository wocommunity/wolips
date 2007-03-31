package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.model.EOModelException;
import org.objectstyle.wolips.eomodeler.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.model.EOModelVerificationFailure;

public class AddOperation extends AbstractOperation {
	private EOModelObject _parent;

	private EOModelObject _child;

	public AddOperation(EOModelObject parent, EOModelObject child) {
		super("Add " + child.getName());
		_parent = parent;
		_child = child;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			((EOModelObject) _child)._addToModelParent(_parent, true, failures);
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to add object.", e);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			((EOModelObject) _child)._removeFromModelParent(failures);
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to add object.", e);
		}
		return Status.OK_STATUS;

	}

}
