package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

public class NewManyToManyRelationshipOperation extends AbstractOperation {
	private EOEntity _sourceEntity;

	private String _name;

	private boolean _createRelationship;

	private EOEntity _destinationEntity;

	private String _inverseName;

	private boolean _createInverseRelationship;

	private String _joinEntityName;

	private boolean _flatten;
	
	private EOEntity _joinEntity;

	public NewManyToManyRelationshipOperation(EOEntity sourceEntity, EOEntity destinationEntity, boolean createRelationship, String name, boolean createInverseRelationship, String inverseName, String joinEntityName, boolean flatten) {
		super("Add Relationship");
		_sourceEntity = sourceEntity;
		_destinationEntity = destinationEntity;
		_createRelationship = createRelationship;
		_name = name;
		_createInverseRelationship = createInverseRelationship;
		_inverseName = inverseName;
		_joinEntityName = joinEntityName;
		_flatten = flatten;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			_joinEntity = _sourceEntity.joinInManyToManyWith(_destinationEntity, _createRelationship, _name, _createInverseRelationship, _inverseName, _joinEntityName, _flatten);
			return Status.OK_STATUS;
		} catch (EOModelException e) {
			throw new ExecutionException("Failed to add new object.", e);
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		HashSet<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		Set<EORelationship> referencingRelationships = _joinEntity.getReferencingRelationships();
		for (EORelationship relationship : referencingRelationships) {
			if (!relationship.isFlattened()) {
				if (_flatten) {
					for (EORelationship flattenedRelationship : relationship.getReferencingFlattenedRelationships()) {
						flattenedRelationship._removeFromModelParent(failures);
					}
				}
				relationship._removeFromModelParent(failures);
			}
		}
		_joinEntity._removeFromModelParent(failures);
		return Status.OK_STATUS;
	}

}
