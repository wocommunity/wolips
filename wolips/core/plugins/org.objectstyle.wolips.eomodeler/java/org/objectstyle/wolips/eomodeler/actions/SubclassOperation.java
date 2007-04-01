package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModelException;
import org.objectstyle.wolips.eomodeler.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.model.InheritanceType;

public class SubclassOperation extends AbstractOperation {
	private EOEntity _parentEntity;

	private EOEntity _subclassEntity;

	private InheritanceType _inheritanceType;

	private String _entityName;

	private String _restrictingQualifier;

	public SubclassOperation(EOEntity parentEntity, InheritanceType inheritanceType, String entityName, String restrictingQualifier) {
		super("Subclass " + parentEntity.getName());
		_parentEntity = parentEntity;
		_inheritanceType = inheritanceType;
		_entityName = entityName;
		_restrictingQualifier = restrictingQualifier;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			_subclassEntity = _parentEntity.subclass(_entityName, _inheritanceType);
			_subclassEntity.setRestrictingQualifier(_restrictingQualifier);
			_parentEntity.getModel().addEntity(_subclassEntity);
			return Status.OK_STATUS;
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to subclass entity.", e);
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) {
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		_subclassEntity._removeFromModelParent(failures);
		return Status.OK_STATUS;
	}
}