/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.utils.StringUtils;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;

public class EOStoredProcedure extends UserInfoableEOModelObject<EOModel> implements ISortableEOModelObject {
	public static final String NAME = "name";

	public static final String EXTERNAL_NAME = "externalName";

	public static final String ARGUMENT = "argument";

	public static final String ARGUMENTS = "arguments";

	private EOModel myModel;

	private String myName;

	private String myExternalName;

	private List<EOArgument> myArguments;

	private EOModelMap myStoredProcedureMap;

	public EOStoredProcedure() {
		myStoredProcedureMap = new EOModelMap();
		myArguments = new LinkedList<EOArgument>();
	}

	public EOStoredProcedure(String _name) {
		this();
		myName = _name;
	}

	public void pasted() {
		for (EOArgument argument : myArguments) {
			argument.pasted();
		}
	}

	public Set<EOModelReferenceFailure> getReferenceFailures() {
		Set<EOModelReferenceFailure> referenceFailures = new HashSet<EOModelReferenceFailure>();
		for (EOArgument argument : myArguments) {
			referenceFailures.addAll(argument.getReferenceFailures());
		}

		if (myModel != null) {
			for (EOEntity entity : myModel.getEntities()) {
				if (entity.getDeleteProcedure() == this) {
					referenceFailures.add(new EOStoredProcedureEntityReferenceFailure(entity, this, EOEntity.DELETE_PROCEDURE));
				}
				if (entity.getInsertProcedure() == this) {
					referenceFailures.add(new EOStoredProcedureEntityReferenceFailure(entity, this, EOEntity.INSERT_PROCEDURE));
				}
				if (entity.getNextPrimaryKeyProcedure() == this) {
					referenceFailures.add(new EOStoredProcedureEntityReferenceFailure(entity, this, EOEntity.NEXT_PRIMARY_KEY_PROCEDURE));
				}
				if (entity.getFetchWithPrimaryKeyProcedure() == this) {
					referenceFailures.add(new EOStoredProcedureEntityReferenceFailure(entity, this, EOEntity.FETCH_WITH_PRIMARY_KEY_PROCEDURE));
				}
				if (entity.getFetchAllProcedure() == this) {
					referenceFailures.add(new EOStoredProcedureEntityReferenceFailure(entity, this, EOEntity.FETCH_ALL_PROCEDURE));
				}

				for (EOFetchSpecification fetchSpec : entity.getFetchSpecs()) {
					if (fetchSpec.getStoredProcedure() == this) {
						referenceFailures.add(new EOStoredProcedureFetchSpecReferenceFailure(fetchSpec, this));
					}
				}
			}
		}

		return referenceFailures;
	}

	public void _setModel(EOModel _model) {
		myModel = _model;
	}

	public EOModel getModel() {
		return myModel;
	}

	@SuppressWarnings("unused")
	protected void _argumentChanged(EOArgument _argument, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(EOStoredProcedure.ARGUMENT, null, _argument);
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (myModel != null) {
			myModel._storedProcedureChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	public int hashCode() {
		return ((myModel == null) ? 1 : myModel.hashCode()) * ((myName == null) ? super.hashCode() : myName.hashCode());
	}

	public boolean equals(Object _obj) {
		boolean equals = false;
		if (_obj instanceof EOStoredProcedure) {
			EOStoredProcedure storedProcedure = (EOStoredProcedure) _obj;
			equals = (storedProcedure == this) || (ComparisonUtils.equals(storedProcedure.myModel, myModel) && ComparisonUtils.equals(storedProcedure.myName, myName));
		}
		return equals;
	}

	public void setName(String _name) throws DuplicateStoredProcedureNameException {
		setName(_name, true);
	}

	public void setName(String _name, boolean _fireEvents) throws DuplicateStoredProcedureNameException {
		if (myModel != null) {
			myModel._checkForDuplicateStoredProcedureName(this, _name, null);
			myModel._storedProcedureNameChanged(myName, _name);
		}
		String oldName = myName;
		myName = _name;
		if (_fireEvents) {
			firePropertyChange(EOStoredProcedure.NAME, oldName, myName);
		}
	}

	public String getName() {
		return myName;
	}

	public String getExternalName() {
		return myExternalName;
	}

	public void setExternalName(String _externalName) {
		String oldExternalName = myExternalName;
		myExternalName = _externalName;
		firePropertyChange(EOStoredProcedure.EXTERNAL_NAME, oldExternalName, myExternalName);
	}

	public EOArgument getArgumentNamed(String _name) {
		EOArgument matchingArgument = null;
		Iterator<EOArgument> argumentsIter = myArguments.iterator();
		while (matchingArgument == null && argumentsIter.hasNext()) {
			EOArgument argument = argumentsIter.next();
			if (ComparisonUtils.equals(argument.getName(), _name)) {
				matchingArgument = argument;
			}
		}
		return matchingArgument;
	}

	public String findUnusedArgumentName(String _newName) {
		return _findUnusedName(_newName, "getArgumentNamed");
	}

	public void _checkForDuplicateArgumentName(EOArgument _argument, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		EOArgument existingArgument = getArgumentNamed(_newName);
		if (existingArgument != null && existingArgument != _argument) {
			if (_failures == null) {
				throw new DuplicateArgumentNameException(_newName, this);
			}

			String unusedName = findUnusedArgumentName(_newName);
			existingArgument.setName(unusedName, true);
			_failures.add(new DuplicateArgumentFailure(this, _newName, unusedName));
		}
	}

	public EOArgument addBlankArgument(String _name) throws DuplicateNameException {
		EOArgument argument = new EOArgument(findUnusedArgumentName(_name));
		argument.setAllowsNull(Boolean.TRUE, false);
		addArgument(argument);
		return argument;
	}

	public void addArgument(EOArgument _argument) throws DuplicateNameException {
		addArgument(_argument, null, true);
	}

	public void addArgument(EOArgument _argument, Set<EOModelVerificationFailure> _failures, boolean _fireEvents) throws DuplicateNameException {
		_argument._setStoredProcedure(this);
		_checkForDuplicateArgumentName(_argument, _argument.getName(), _failures);
		_argument.pasted();
		List<EOArgument> oldArguments = null;
		if (_fireEvents) {
			oldArguments = myArguments;
			List<EOArgument> newArguments = new LinkedList<EOArgument>();
			newArguments.addAll(myArguments);
			newArguments.add(_argument);
			myArguments = newArguments;
			firePropertyChange(EOStoredProcedure.ARGUMENTS, oldArguments, myArguments);
		} else {
			myArguments.add(_argument);
		}
	}

	public void removeArgument(EOArgument _argument) {
		List<EOArgument> oldArguments = myArguments;
		List<EOArgument> newArguments = new LinkedList<EOArgument>();
		newArguments.addAll(myArguments);
		newArguments.remove(_argument);
		myArguments = newArguments;
		firePropertyChange(EOStoredProcedure.ARGUMENTS, oldArguments, newArguments);
		_argument._setStoredProcedure(null);
	}

	public List<EOArgument> getArguments() {
		return myArguments;
	}

	public List<EOArgument> getSortedArguments() {
		return myArguments;
	}

	public void loadFromMap(EOModelMap _map, Set<EOModelVerificationFailure> _failures) throws EOModelException {
		myStoredProcedureMap = _map;
		myName = _map.getString("name", true);
		myExternalName = _map.getString("externalName", true);

		List<Map> argumentsList = _map.getList("arguments", false);
		if (argumentsList != null) {
			for (Map originalArgumentMap : argumentsList) {
				EOModelMap argumentMap = new EOModelMap(originalArgumentMap);
				EOArgument argument = new EOArgument();
				argument.loadFromMap(argumentMap, _failures);
				addArgument(argument, _failures, false);
			}
		}
		loadUserInfo(_map);
	}

	public EOModelMap toMap() {
		EOModelMap fetchSpecMap = myStoredProcedureMap.cloneModelMap();
		fetchSpecMap.setString("name", myName, true);
		fetchSpecMap.setString("externalName", myExternalName, true);

		List<Map> arguments = new LinkedList<Map>();
		for (EOArgument argument : myArguments) {
			EOModelMap argumentMap = argument.toMap();
			arguments.add(argumentMap);
		}
		fetchSpecMap.setList("arguments", arguments, true);
		writeUserInfo(fetchSpecMap);
		return fetchSpecMap;
	}

	public void loadFromURL(URL _storedProcedureURL, Set<EOModelVerificationFailure> _failures) throws EOModelException {
		try {
			EOModelMap entityMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromURL(_storedProcedureURL, new EOModelParserDataStructureFactory()));
			loadFromMap(entityMap, _failures);
		} catch (Throwable e) {
			throw new EOModelException("Failed to load stored procedure from '" + _storedProcedureURL + "'.", e);
		}
	}

	public void saveToFile(File _storedProcedureFile) {
		EOModelMap storedProcedureMap = toMap();
		PropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, _storedProcedureFile, storedProcedureMap);
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		for (EOArgument argument : myArguments) {
			argument.resolve(_failures);
		}
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		String name = getName();
		if (name == null || name.trim().length() == 0) {
			_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + " has an empty name.", false));
		} else {
			if (name.indexOf(' ') != -1) {
				_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s name has a space in it.", false));
			}
			if (!StringUtils.isUppercaseFirstLetter(myName)) {
				_failures.add(new EOModelVerificationFailure(myModel, "Entity names should be capitalized, but " + getFullyQualifiedName() + " is not.", true));
			}
		}

		String externalName = getExternalName();
		if (externalName == null || externalName.trim().length() == 0) {
			_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + " has an empty table name.", false));
		} else if (externalName.indexOf(' ') != -1) {
			_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s table name '" + externalName + "' has a space in it.", false));
		}

		for (EOArgument argument : myArguments) {
			argument.verify(_failures);
		}
	}

	public String getFullyQualifiedName() {
		return ((myModel == null) ? "?" : myModel.getFullyQualifiedName()) + "/proc: " + getName();
	}

	@Override
	public EOModelObject<EOModel> _cloneModelObject() {
		try {
			EOStoredProcedure storedProcedure = new EOStoredProcedure(myName);
			storedProcedure.myName = myName;
			storedProcedure.myExternalName = myExternalName;

			for (EOArgument argument : getArguments()) {
				if (getArgumentNamed(argument.getName()) == null) {
					EOArgument clonedArgument = argument._cloneModelObject();
					clonedArgument.setName(findUnusedArgumentName(clonedArgument.getName()));
					storedProcedure.addArgument(clonedArgument);
				}
			}

			_cloneUserInfoInto(storedProcedure);
			return storedProcedure;
		} catch (DuplicateNameException e) {
			throw new RuntimeException("A duplicate name was found during a clone, which should never happen.", e);
		}
	}

	@Override
	public Class<EOModel> _getModelParentType() {
		return EOModel.class;
	}

	public EOModel _getModelParent() {
		return getModel();
	}

	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getModel().removeStoredProcedure(this);
	}

	public void _addToModelParent(EOModel modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedStoredProcedureName(getName()));
		}
		modelParent.addStoredProcedure(this);
	}

	public String toString() {
		return "[EOStoredProcedure: name = " + myName + "; arguments = " + myArguments + "]";
	}
}
