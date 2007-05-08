package org.objectstyle.wolips.eomodeler.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.utils.EOModelUtils;

public class AddOperation extends AbstractOperation {
	private EOModelObject _parent;

	private EOModelObject _child;

	public AddOperation(EOModelObject parent, EOModelObject child) {
		super(EOModelUtils.getOperationLabel("Add", Arrays.asList(new Object[] { child })));
		_parent = parent;
		_child = child;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			_child._addToModelParent(_parent, true, failures);
			return Status.OK_STATUS;
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to add object.", e);
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			_child._removeFromModelParent(failures);
			return Status.OK_STATUS;
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to remove object.", e);
		}
	}

}
