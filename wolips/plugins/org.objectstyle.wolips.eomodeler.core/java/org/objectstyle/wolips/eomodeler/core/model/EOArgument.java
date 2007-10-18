package org.objectstyle.wolips.eomodeler.core.model;

import java.util.HashSet;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.Messages;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;

public class EOArgument extends AbstractEOArgument<EOStoredProcedure> {
	public static final String DIRECTION = "direction";

	private EOStoredProcedure myStoredProcedure;

	private EOArgumentDirection myDirection;

	public EOArgument() {
		myDirection = EOArgumentDirection.VOID;
	}

	public EOArgument(String _name) {
		super(_name);
		myDirection = EOArgumentDirection.VOID;
	}

	public EOArgument(String _name, String _definition) {
		super(_name, _definition);
		myDirection = EOArgumentDirection.VOID;
	}

	public int hashCode() {
		return ((myStoredProcedure == null) ? 1 : myStoredProcedure.hashCode()) * super.hashCode();
	}

	public boolean equals(Object _obj) {
		boolean equals = false;
		if (_obj instanceof EOArgument) {
			EOArgument argument = (EOArgument) _obj;
			equals = (argument == this) || (ComparisonUtils.equals(argument.myStoredProcedure, myStoredProcedure) && ComparisonUtils.equals(argument.getName(), getName()));
		}
		return equals;
	}

	public EOStoredProcedure getStoredProcedure() {
		return myStoredProcedure;
	}

	public void _setStoredProcedure(EOStoredProcedure _storedProcedure) {
		myStoredProcedure = _storedProcedure;
	}

	protected AbstractEOArgument _createArgument(String _name) {
		return new EOArgument(_name);
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (myStoredProcedure != null) {
			myStoredProcedure._argumentChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		if (_name == null) {
			throw new NullPointerException(Messages.getString("EOArgument.noBlankArgumentNames"));
		}
		if (myStoredProcedure != null) {
			myStoredProcedure._checkForDuplicateArgumentName(this, _name, null);
		}
		super.setName(_name, _fireEvents);
	}

	public void setDirection(EOArgumentDirection _direction) {
		EOArgumentDirection oldDirection = myDirection;
		myDirection = _direction;
		firePropertyChange(EOArgument.DIRECTION, oldDirection, myDirection);
	}

	public EOArgumentDirection getDirection() {
		return myDirection;
	}

	public void loadFromMap(EOModelMap _argumentMap, Set<EOModelVerificationFailure> _failures) {
		super.loadFromMap(_argumentMap, _failures);
		Integer argumentDirectionNum = _argumentMap.getInteger("parameterDirection");
		if (argumentDirectionNum == null) {
			argumentDirectionNum = new Integer(0);
		}
		myDirection = EOArgumentDirection.getArgumentDirectionByID(argumentDirectionNum.intValue());
	}

	public EOModelMap toMap() {
		EOModelMap argumentMap = super.toMap();
		if (myDirection == null || myDirection == EOArgumentDirection.VOID) {
			argumentMap.remove("parameterDirection");
		} else {
			argumentMap.setInteger("parameterDirection", new Integer(myDirection.getID()));
		}
		return argumentMap;
	}
	
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

  @SuppressWarnings("unused")
	public void resolve(Set<EOModelVerificationFailure> _failures) {
		// DO NOTHING
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		String name = getName();
		if (name == null || name.trim().length() == 0) {
			_failures.add(new EOModelVerificationFailure(myStoredProcedure.getModel(), this, "The argument " + getName() + " has an empty name.", false));
		} else {
			if (name.indexOf(' ') != -1) {
				_failures.add(new EOModelVerificationFailure(myStoredProcedure.getModel(), this, "The argument " + getName() + "'s name has a space in it.", false));
			}
		}
		// if (!isFlattened()) {
		// String columnName = getColumnName();
		// if (columnName == null || columnName.trim().length() == 0) {
		// _failures.add(new
		// EOModelVerificationFailure(myStoredProcedure.getModel().getName() +
		// "/" + myStoredProcedure.getName() + "/" + name + " does not have a
		// column name set."));
		// }
		// else if (columnName.indexOf(' ') != -1) {
		// _failures.add(new
		// EOModelVerificationFailure(myStoredProcedure.getModel().getName() +
		// "/" + myStoredProcedure.getName() + "/" + name + "'s column name '" +
		// columnName + "' has a space in it."));
		// }
		// }
		if (myDirection == null) {
			_failures.add(new EOModelVerificationFailure(myStoredProcedure.getModel(), this, "The argument " + getName() + " has no direction specified.", false));
		}
	}

	public String getFullyQualifiedName() {
		return ((myStoredProcedure == null) ? "?" : myStoredProcedure.getFullyQualifiedName()) + "/arg: " + getName();
	}

	@Override
	public EOArgument _cloneModelObject() {
		EOArgument argument = (EOArgument) _cloneArgument();
		argument.myDirection = myDirection;
		return argument;
	}
	
	@Override
	public Class<EOStoredProcedure> _getModelParentType() {
		return EOStoredProcedure.class;
	}
	
	public EOStoredProcedure _getModelParent() {
		return getStoredProcedure();
	}
	
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getStoredProcedure().removeArgument(this);
	}
	
	public void _addToModelParent(EOStoredProcedure modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedArgumentName(getName()));
		}
		modelParent.addArgument(this);
	}

	public String toString() {
		return "[EOArgument: name = " + getName() + "]";
	}
}
