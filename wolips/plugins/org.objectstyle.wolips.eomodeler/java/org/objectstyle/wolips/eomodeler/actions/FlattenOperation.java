package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModelException;
import org.objectstyle.wolips.eomodeler.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.model.IEOAttribute;

public class FlattenOperation extends AbstractOperation {
	private AbstractEOAttributePath _attributePath;

	private IEOAttribute _newAttribute;

	public FlattenOperation(AbstractEOAttributePath attributePath) {
		super("Flatten " + attributePath.toKeyPath());
		_attributePath = attributePath;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			EOEntity rootEntity = _attributePath.getRootEntity();
			_newAttribute = rootEntity.addBlankIEOAttribute(_attributePath);
			return Status.OK_STATUS;
		} catch (DuplicateNameException e) {
			throw new ExecutionException("Failed to flatten.", e);
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
			_newAttribute._removeFromModelParent(failures);
			return Status.OK_STATUS;
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to remove flattened object.", e);
		}
	}

}
