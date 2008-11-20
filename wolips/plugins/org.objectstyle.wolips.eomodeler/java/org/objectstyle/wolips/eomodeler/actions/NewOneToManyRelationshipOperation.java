package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

public class NewOneToManyRelationshipOperation extends AbstractOperation {
	private EORelationship _relationship;

	private EOEntity _sourceEntity;

	private String _name;

	private boolean _createRelationship;

	private boolean _toMany;

	private boolean _createFK;

	private EOAttribute _foreignKey;

	private String _fkName;

	private String _fkColumnName;

	private EORelationship _inverseRelationship;

	private EOEntity _destinationEntity;

	private EOAttribute _inverseForeignKey;

	private String _inverseName;

	private boolean _createInverseRelationship;

	private boolean _inverseToMany;

	private boolean _createInverseFK;

	private String _inverseFKName;

	private String _inverseFKColumnName;

	public NewOneToManyRelationshipOperation(EORelationship relationship, EOEntity sourceEntity, String name, boolean createRelationship, boolean toMany, boolean createFK, String fkName, String fkColumnName, EOEntity destinationEntity, String inverseName, boolean createInverseRelationship, boolean inverseToMany, boolean createInverseFK, String inverseFKName, String inverseFKColumnName) {
		super("Add Relationship");
		_relationship = relationship;
		_sourceEntity = sourceEntity;
		_name = name;
		_createRelationship = createRelationship;
		_toMany = toMany;
		_createFK = createFK;
		_fkName = fkName;
		_fkColumnName = fkColumnName;
		_destinationEntity = destinationEntity;
		_inverseName = inverseName;
		_createInverseRelationship = createInverseRelationship;
		_inverseToMany = inverseToMany;
		_createInverseFK = createInverseFK;
		_inverseFKName = inverseFKName;
		_inverseFKColumnName = inverseFKColumnName;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			EOJoin newJoin = null;
			if (_createFK) {
				_foreignKey = _sourceEntity.createForeignKeyTo(_destinationEntity, _fkName, _fkColumnName, false);
				newJoin = new EOJoin();
				newJoin.setSourceAttribute(_foreignKey);
				newJoin.setDestinationAttribute(_destinationEntity.getSinglePrimaryKeyAttribute());
			}
			if (_createInverseFK) {
				_inverseForeignKey = _destinationEntity.createForeignKeyTo(_sourceEntity, _inverseFKName, _inverseFKColumnName, false);
				newJoin = new EOJoin();
				newJoin.setSourceAttribute(_sourceEntity.getSinglePrimaryKeyAttribute());
				newJoin.setDestinationAttribute(_inverseForeignKey);
			}

			if (newJoin != null) {
				_relationship.removeAllJoins();
				_relationship.addJoin(newJoin);
			}

			if (_createRelationship) {
				_relationship.setName(_name);
				_relationship.setToMany(Boolean.valueOf(_toMany));
				_relationship.setMandatoryIfNecessary();
				_sourceEntity.addRelationship(_relationship);
			}
			if (_createInverseRelationship) {
				_inverseRelationship = _relationship.createInverseRelationshipNamed(_inverseName, _inverseToMany);
				_inverseRelationship.setMandatoryIfNecessary();
				_inverseRelationship.getEntity().addRelationship(_inverseRelationship);
			}
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
		if (_createRelationship) {
			_relationship._removeFromModelParent(failures);
		}
		if (_createInverseRelationship) {
			_inverseRelationship._removeFromModelParent(failures);
		}
		if (_createFK) {
			_foreignKey._removeFromModelParent(failures);
		}
		if (_createInverseFK) {
			_inverseForeignKey._removeFromModelParent(failures);
		}
		return Status.OK_STATUS;
	}

}
