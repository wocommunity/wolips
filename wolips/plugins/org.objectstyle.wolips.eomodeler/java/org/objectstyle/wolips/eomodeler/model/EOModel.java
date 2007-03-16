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
package org.objectstyle.wolips.eomodeler.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.URLUtils;
import org.objectstyle.wolips.eomodeler.wocompat.PropertyListSerialization;

public class EOModel extends UserInfoableEOModelObject implements IUserInfoable, ISortableEOModelObject {
	public static final String ENTITY_MODELER_KEY = "_EntityModeler";

	public static final String DIRTY = "dirty";

	public static final String ENTITY = "entity";

	public static final String ADAPTOR_NAME = "adaptorName";

	public static final String VERSION = "version";

	public static final String NAME = "name";

	public static final String ENTITIES = "entities";

	public static final String STORED_PROCEDURE = "storedProcedure";

	public static final String STORED_PROCEDURES = "storedProcedures";

	public static final String ACTIVE_DATABASE_CONFIG = "activeDatabaseConfig";

	public static final String DATABASE_CONFIG = "databaseConfig";

	public static final String DATABASE_CONFIGS = "databaseConfigs";

	private EOModelGroup myModelGroup;

	private String myName;

	private String myVersion;

	private Set myEntities;

	private Set myDatabaseConfigs;

	private EODatabaseConfig myActiveDatabaseConfig;

	private Set myStoredProcedures;

	private Set myDeletedEntityNamesInObjectStore;

	private Set myDeletedEntityNames;

	private Set myDeletedStoredProcedureNames;

	private EOModelMap myModelMap;

	private boolean myDirty;

	private URL myModelURL;

	private Set myPrototypeAttributeCache;

	private IProject _project;

	public EOModel(String _name, IProject project) {
		myName = _name;
		myEntities = new PropertyListSet();
		myStoredProcedures = new PropertyListSet();
		myDeletedEntityNamesInObjectStore = new PropertyListSet();
		myDeletedEntityNames = new PropertyListSet();
		myDeletedStoredProcedureNames = new PropertyListSet();
		myDatabaseConfigs = new PropertyListSet();
		myVersion = "2.1";
		myModelMap = new EOModelMap();
		_project = project;
	}

	public IProject getProject() {
		return _project;
	}

	protected void _storedProcedureChanged(EOStoredProcedure _storedProcedure, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(EOModel.STORED_PROCEDURE, null, _storedProcedure);
	}

	public void _setModelGroup(EOModelGroup _modelGroup) {
		myModelGroup = _modelGroup;
	}

	public Set getReferenceFailures() {
		return new HashSet();
	}

	public String guessPackageName() {
		return guessPackageName(getEntities());
	}

	public String guessPackageName(Set _entities) {
		String guessPackageName = null;
		Iterator entitiesIter = _entities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			String className = entity.getClassName();
			if (className != null) {
				int packageNameEnd = className.lastIndexOf('.');
				String packageName;
				if (packageNameEnd != -1) {
					packageName = className.substring(0, packageNameEnd);
				} else {
					packageName = "";
				}
				if (guessPackageName == null) {
					guessPackageName = packageName;
				} else if ("".equals(guessPackageName)) {
					// it can't change from ""
				} else if (!guessPackageName.equals(packageName)) {
					if (guessPackageName.startsWith(packageName)) {
						guessPackageName = packageName;
					} else if (packageName.startsWith(guessPackageName)) {
						// leave it as is
					} else {
						int lastMatchingIndex = -1;
						for (int index = 0; index < guessPackageName.length() && index < packageName.length(); index++) {
							if (guessPackageName.charAt(index) == packageName.charAt(index)) {
								lastMatchingIndex = index;
							} else {
								break;
							}
						}
						if (lastMatchingIndex != -1) {
							guessPackageName = guessPackageName.substring(0, lastMatchingIndex);
							if (guessPackageName.endsWith(".")) {
								guessPackageName = guessPackageName.substring(0, guessPackageName.length() - 1);
							}
						} else {
							guessPackageName = "";
						}
					}
				}
			}
		}
		return guessPackageName;
	}

	public EOEntity addBlankEntity(String _name) throws DuplicateNameException {
		String newEntityNameBase = _name;
		String newEntityName = newEntityNameBase;
		int newEntityNum = 0;
		while (getEntityNamed(newEntityName) != null) {
			newEntityNum++;
			newEntityName = newEntityNameBase + newEntityNum;
		}
		EOEntity entity = new EOEntity(newEntityName);
		entity.setExternalName(newEntityName);
		String className = newEntityName;
		String packageName = guessPackageName();
		if (packageName != null && packageName.length() > 0) {
			className = packageName + "." + newEntityName;
		}
		entity.setClassName(className);
		addEntity(entity);

		if (myModelGroup != null && myModelGroup.hasProjectWonder()) {
			EOAttribute pk = new EOAttribute("id");
			pk.setPrototype(getPrototypeAttributeNamed("id"));
			pk.setColumnName("id");
			pk.setPrimaryKey(Boolean.TRUE);
			pk.setClassProperty(Boolean.FALSE);
			pk.setUsedForLocking(Boolean.TRUE);
			entity.addAttribute(pk);
		}

		return entity;
	}

	public boolean isDirty() {
		return myDirty;
	}

	public void setDirty(boolean _dirty) {
		Boolean oldDirty = Boolean.valueOf(myDirty);
		myDirty = _dirty;
		firePropertyChange(EOModel.DIRTY, oldDirty, Boolean.valueOf(myDirty));
	}

	protected void firePropertyChange(String _propertyName, Object _oldValue, Object _newValue) {
		super.firePropertyChange(_propertyName, _oldValue, _newValue);
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (!myDirty && !EOModel.DIRTY.equals(_propertyName)) {
			setDirty(true);
		}
	}

	protected void _entityChanged(EOEntity _entity, String _propertyName, Object _oldValue, Object _newValue) {
		myEntities = new HashSet(myEntities);
		firePropertyChange(EOModel.ENTITY, null, _entity);
	}

	protected void _databaseConfigChanged(EODatabaseConfig _databaseConfig, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(EOModel.DATABASE_CONFIG, null, _databaseConfig);
		if (_databaseConfig == myActiveDatabaseConfig) {
			clearCachedPrototypes(null, false);
		}
	}

	public EODatabaseConfig getDatabaseConfigNamed(String _name) {
		EODatabaseConfig matchingDatabaseConfig = null;
		Iterator databaseConfigsIter = myDatabaseConfigs.iterator();
		while (matchingDatabaseConfig == null && databaseConfigsIter.hasNext()) {
			EODatabaseConfig entity = (EODatabaseConfig) databaseConfigsIter.next();
			if (ComparisonUtils.equals(entity.getName(), _name)) {
				matchingDatabaseConfig = entity;
			}
		}
		return matchingDatabaseConfig;
	}

	public String findUnusedDatabaseConfigName(String _newName) {
		String unusedName = _newName;
		boolean unusedNameFound = (getDatabaseConfigNamed(_newName) == null);
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			EODatabaseConfig renameDatabaseConfig = getDatabaseConfigNamed(unusedName);
			unusedNameFound = (renameDatabaseConfig == null);
		}
		return unusedName;
	}

	public void _checkForDuplicateDatabaseConfigName(EODatabaseConfig _databaseConfig, String _newName, Set _failures) throws DuplicateDatabaseConfigNameException {
		EODatabaseConfig existingDatabaseConfig = getDatabaseConfigNamed(_newName);
		if (existingDatabaseConfig != null && existingDatabaseConfig != _databaseConfig) {
			if (_failures == null) {
				throw new DuplicateDatabaseConfigNameException(_newName, this);
			}
			String unusedName = findUnusedDatabaseConfigName(_newName);
			existingDatabaseConfig.setName(unusedName, true);
			_failures.add(new DuplicateDatabaseConfigFailure(this, _newName, unusedName));
		}
	}

	public EODatabaseConfig addBlankDatabaseConfig(String _name) throws DuplicateNameException {
		String newDatabaseConfigNameBase = _name;
		String newDatabaseConfigName = findUnusedDatabaseConfigName(newDatabaseConfigNameBase);
		EODatabaseConfig databaseConfig = new EODatabaseConfig(newDatabaseConfigName);
		addDatabaseConfig(databaseConfig);
		return databaseConfig;
	}

	public void addDatabaseConfig(EODatabaseConfig _databaseConfig) throws DuplicateNameException {
		addDatabaseConfig(_databaseConfig, true, null);
	}

	public void addDatabaseConfig(EODatabaseConfig _databaseConfig, boolean _fireEvents, Set _failures) throws DuplicateNameException {
		_databaseConfig._setModel(this);
		_checkForDuplicateDatabaseConfigName(_databaseConfig, _databaseConfig.getName(), _failures);
		_databaseConfig.pasted();
		if (_fireEvents) {
			Set oldDatabaseConfigs = null;
			oldDatabaseConfigs = myDatabaseConfigs;
			Set newEntities = new TreeSet(new PropertyListSet());
			newEntities.addAll(myDatabaseConfigs);
			newEntities.add(_databaseConfig);
			myDatabaseConfigs = newEntities;
			firePropertyChange(EOModel.DATABASE_CONFIGS, oldDatabaseConfigs, myDatabaseConfigs);
			if (myActiveDatabaseConfig == null) {
				setActiveDatabaseConfig(_databaseConfig);
			}
		} else {
			myDatabaseConfigs.add(_databaseConfig);
		}
	}

	public void removeDatabaseConfig(EODatabaseConfig _databaseConfig) {
		if (ComparisonUtils.equals(myActiveDatabaseConfig, _databaseConfig)) {
			EODatabaseConfig newActiveDatabaseConfig = null;
			if (!myDatabaseConfigs.isEmpty()) {
				Iterator databaseConfigsIter = myDatabaseConfigs.iterator();
				while (newActiveDatabaseConfig == null && databaseConfigsIter.hasNext()) {
					EODatabaseConfig otherDatabaseConfig = (EODatabaseConfig) databaseConfigsIter.next();
					if (otherDatabaseConfig != _databaseConfig) {
						newActiveDatabaseConfig = otherDatabaseConfig;
					}
				}
			}
			setActiveDatabaseConfig(newActiveDatabaseConfig);
		}
		Set oldDatabaseConfigs = myDatabaseConfigs;
		Set newDatabaseConfigs = new TreeSet(new PropertyListSet());
		newDatabaseConfigs.addAll(myDatabaseConfigs);
		newDatabaseConfigs.remove(_databaseConfig);
		myDatabaseConfigs = newDatabaseConfigs;
		firePropertyChange(EOModel.DATABASE_CONFIGS, oldDatabaseConfigs, myDatabaseConfigs);
		_databaseConfig._setModel(null);
	}

	public void setActiveDatabaseConfig(EODatabaseConfig _activeDatabaseConfig) {
		EODatabaseConfig oldActiveDatabaseConfig = myActiveDatabaseConfig;
		myActiveDatabaseConfig = _activeDatabaseConfig;
		clearCachedPrototypes(null, false);
		firePropertyChange(EOModel.ACTIVE_DATABASE_CONFIG, oldActiveDatabaseConfig, myActiveDatabaseConfig);
	}

	public EODatabaseConfig getActiveDatabaseConfig() {
		return myActiveDatabaseConfig;
	}

	public EODatabaseConfig _createDatabaseConfig(String adaptorName, Map _connectionDictionary) {
		EODatabaseConfig defaultDatabaseConfig = new EODatabaseConfig(findUnusedDatabaseConfigName("Default"));
		defaultDatabaseConfig.setAdaptorName(adaptorName);
		defaultDatabaseConfig.setConnectionDictionary(new HashMap(_connectionDictionary));
		defaultDatabaseConfig.setPrototype(_getPreferredPrototypeEntity(adaptorName, _connectionDictionary));
		defaultDatabaseConfig._setModel(this);
		return defaultDatabaseConfig;
	}

	public Set getDatabaseConfigs() {
		return myDatabaseConfigs;
	}

	public int hashCode() {
		return myName.hashCode();
	}

	public boolean equals(Object _obj) {
		return (_obj instanceof EOModel && ((EOModel) _obj).myName.equals(myName));
	}

	public URL getModelURL() {
		return myModelURL;
	}

	public void setModelURL(URL _modelURL) {
		myModelURL = _modelURL;
	}

	public EOModelGroup getModelGroup() {
		return myModelGroup;
	}

	public String getVersion() {
		return myVersion;
	}

	public void setVersion(String _version) {
		String oldVersion = myVersion;
		myVersion = _version;
		firePropertyChange(EOModel.VERSION, _version, oldVersion);
	}

	public void setName(String _name) {
		String oldName = myName;
		myName = _name;
		firePropertyChange(EOModel.NAME, _name, oldName);
	}

	public String getName() {
		return myName;
	}

	public Set getEntities() {
		return myEntities;
	}

	public String findUnusedEntityName(String _newName) {
		String unusedName = _newName;
		boolean unusedNameFound = (myModelGroup.getEntityNamed(_newName) == null && getEntityNamed(_newName) == null);
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			EOEntity renameEntity = myModelGroup.getEntityNamed(unusedName);
			if (renameEntity == null) {
				renameEntity = getEntityNamed(unusedName);
			}
			unusedNameFound = (renameEntity == null);
		}
		return unusedName;
	}

	public void _checkForDuplicateEntityName(EOEntity _entity, String _newName, Set _failures) throws DuplicateEntityNameException {
		EOEntity existingEntity = null;
		if (myModelGroup != null) {
			existingEntity = myModelGroup.getEntityNamed(_newName);
		}
		// MS: We do this because at load time, the model thinks it's part of
		// the model group, but the model group doesnt'
		// think it contains the model, so we check both
		if (existingEntity == null) {
			existingEntity = getEntityNamed(_newName);
		}
		if (existingEntity != null && existingEntity != _entity) {
			// MS: For most duplicates, we can rename the original. But for
			// entities, they can be
			// in a totally separate model.
			if (_failures == null || _entity.getModel() != existingEntity.getModel()) {
				throw new DuplicateEntityNameException(_newName, this, existingEntity);
			}
			String unusedName = findUnusedEntityName(_newName);
			existingEntity.setName(unusedName, true);
			_failures.add(new DuplicateEntityFailure(this, _newName, unusedName));
		}
	}

	public void _entityNameChanged(String _oldName, String _newName) {
		if (myDeletedEntityNamesInObjectStore == null) {
			myDeletedEntityNamesInObjectStore = new PropertyListSet();
		}
		myDeletedEntityNamesInObjectStore.add(_oldName);
		myDeletedEntityNamesInObjectStore.remove(_newName);
		myDeletedEntityNames.add(_oldName);
		myDeletedEntityNames.remove(_newName);
	}

	public boolean containsEntityNamed(String _entityName) {
		return getEntityNamed(_entityName) != null;
	}

	public Set importEntitiesFromModel(URL sourceModelURL, Set failures) throws EOModelException, IOException {
		EOModelGroup sourceModelGroup = new EOModelGroup();
		EOModel sourceModel = new EOModel("Temp", null);
		sourceModelGroup.addModel(sourceModel);
		sourceModel.loadFromFolder(sourceModelURL, failures);
		sourceModel.resolve(failures);
		return importEntitiesFromModel(sourceModel, failures);
	}
	
	public Set importEntitiesFromModel(EOModel sourceModel, Set failures) throws DuplicateNameException {
		Set clonedEntities = new HashSet();
		Iterator entitiesIter = sourceModel.getEntities().iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			clonedEntities.add(entity.cloneEntity());
		}
		addEntities(clonedEntities, failures);
		Iterator clonedEntitiesIter = clonedEntities.iterator();
		while (clonedEntitiesIter.hasNext()) {
			EOEntity clonedEntity = (EOEntity) clonedEntitiesIter.next();
			//clonedEntity.setName(StringUtils.toUppercaseFirstLetter(clonedEntity.getName().toLowerCase()));
			Iterator clonedAttributesIter = clonedEntity.getAttributes().iterator();
			while (clonedAttributesIter.hasNext()) {
				EOAttribute clonedAttribute = (EOAttribute) clonedAttributesIter.next();
				clonedAttribute.guessPrototype(true);
				//clonedAttribute.setName(clonedAttribute.getName().toLowerCase());
			}
			//Iterator clonedRelationshipsIter = clonedEntity.getRelationships().iterator();
			//while (clonedRelationshipsIter.hasNext()) {
			//	EORelationship clonedRelationship = (EORelationship) clonedRelationshipsIter.next();
			//	clonedRelationship.setName(clonedRelationship.getName().toLowerCase());
			//}
		}
		return clonedEntities;
	}
	
	public void addEntities(Set entities, Set failures) throws DuplicateNameException {
		Set oldEntities = new TreeSet(new PropertyListSet());
		oldEntities.addAll(myEntities);

		Iterator entitiesIter;
		entitiesIter = entities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			addEntity(entity, false, false, failures);
		}
		entitiesIter = entities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			entity.pasted();
		}

		firePropertyChange(EOModel.ENTITIES, oldEntities, myEntities);
	}

	public void addEntity(EOEntity _entity) throws DuplicateNameException {
		addEntity(_entity, true, true, null);
	}

	public void addEntity(EOEntity entity, boolean pasteImmediately, boolean fireEvents, Set failures) throws DuplicateNameException {
		entity._setModel(this);
		_checkForDuplicateEntityName(entity, entity.getName(), failures);
		if (pasteImmediately) {
			entity.pasted();
		}
		myDeletedEntityNames.remove(entity.getName());
		if (fireEvents) {
			Set oldEntities = null;
			oldEntities = myEntities;
			Set newEntities = new TreeSet(new PropertyListSet());
			newEntities.addAll(myEntities);
			newEntities.add(entity);
			myEntities = newEntities;
			firePropertyChange(EOModel.ENTITIES, oldEntities, myEntities);
		} else {
			myEntities.add(entity);
		}
	}

	public void removeEntity(EOEntity _entity) {
		myDeletedEntityNames.add(_entity.getName());
		Set oldEntities = myEntities;
		Set newEntities = new TreeSet(new PropertyListSet());
		newEntities.addAll(myEntities);
		newEntities.remove(_entity);
		myEntities = newEntities;
		firePropertyChange(EOModel.ENTITIES, oldEntities, myEntities);
		_entity._setModel(null);
	}

	public EOEntity getEntityNamed(String _name) {
		EOEntity matchingEntity = null;
		Iterator entitiesIter = myEntities.iterator();
		while (matchingEntity == null && entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			if (ComparisonUtils.equalsIgnoreCase(entity.getName(), _name)) {
				matchingEntity = entity;
			}
		}
		return matchingEntity;
	}

	public void _storedProcedureNameChanged(String _oldName, String _newName) {
		myDeletedStoredProcedureNames.add(_oldName);
		myDeletedStoredProcedureNames.remove(_newName);
	}

	public EOStoredProcedure addBlankStoredProcedure(String _name) throws DuplicateStoredProcedureNameException {
		String newStoredProcedureNameBase = _name;
		String newStoredProcedureName = findUnusedStoredProcedureName(newStoredProcedureNameBase);
		EOStoredProcedure storedProcedure = new EOStoredProcedure(newStoredProcedureName);
		addStoredProcedure(storedProcedure);
		return storedProcedure;
	}

	public void addStoredProcedure(EOStoredProcedure _storedProcedure) throws DuplicateStoredProcedureNameException {
		addStoredProcedure(_storedProcedure, true, null);
	}

	public void addStoredProcedure(EOStoredProcedure _storedProcedure, boolean _fireEvents, Set _failures) throws DuplicateStoredProcedureNameException {
		_storedProcedure._setModel(this);
		_checkForDuplicateStoredProcedureName(_storedProcedure, _storedProcedure.getName(), _failures);
		_storedProcedure.pasted();
		myDeletedStoredProcedureNames.remove(_storedProcedure.getName());
		if (_fireEvents) {
			Set oldStoredProcedures = myStoredProcedures;
			Set newStoredProcedures = new TreeSet(new PropertyListSet());
			newStoredProcedures.addAll(myStoredProcedures);
			newStoredProcedures.add(_storedProcedure);
			myStoredProcedures = newStoredProcedures;
			firePropertyChange(EOModel.STORED_PROCEDURES, oldStoredProcedures, myStoredProcedures);
		} else {
			myStoredProcedures.add(_storedProcedure);
		}
	}

	public void removeStoredProcedure(EOStoredProcedure _storedProcedure) {
		myDeletedStoredProcedureNames.add(_storedProcedure.getName());
		Set oldStoredProcedures = myStoredProcedures;
		Set newStoredProcedures = new TreeSet(new PropertyListSet());
		newStoredProcedures.addAll(myStoredProcedures);
		newStoredProcedures.remove(_storedProcedure);
		myStoredProcedures = newStoredProcedures;
		firePropertyChange(EOModel.STORED_PROCEDURES, oldStoredProcedures, myStoredProcedures);
		_storedProcedure._setModel(null);
	}

	public String findUnusedStoredProcedureName(String _newName) {
		boolean unusedNameFound = (getStoredProcedureNamed(_newName) == null);
		String unusedName = _newName;
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			EOStoredProcedure renameStoredProcedure = getStoredProcedureNamed(unusedName);
			unusedNameFound = (renameStoredProcedure == null);
		}
		return unusedName;
	}

	public Set getStoredProcedures() {
		return myStoredProcedures;
	}

	public EOStoredProcedure getStoredProcedureNamed(String _name) {
		EOStoredProcedure matchingStoredProcedure = null;
		Iterator storedProceduresIter = myStoredProcedures.iterator();
		while (matchingStoredProcedure == null && storedProceduresIter.hasNext()) {
			EOStoredProcedure attribute = (EOStoredProcedure) storedProceduresIter.next();
			if (ComparisonUtils.equals(attribute.getName(), _name)) {
				matchingStoredProcedure = attribute;
			}
		}
		return matchingStoredProcedure;
	}

	public void _checkForDuplicateStoredProcedureName(EOStoredProcedure _storedProcedure, String _newName, Set _failures) throws DuplicateStoredProcedureNameException {
		EOStoredProcedure existingStoredProcedure = getStoredProcedureNamed(_newName);
		if (existingStoredProcedure != null && existingStoredProcedure != _storedProcedure) {
			if (_failures == null) {
				throw new DuplicateStoredProcedureNameException(_newName, this);
			}

			String unusedName = findUnusedStoredProcedureName(_newName);
			existingStoredProcedure.setName(unusedName, true);
			_failures.add(new DuplicateStoredProcedureFailure(this, _newName, unusedName));
		}
	}

	public URL getIndexURL() throws MalformedURLException {
		URL indexURL = new URL(myModelURL, "index.eomodeld");
		return indexURL;
	}

	public void loadFromFolder(URL _modelFolder, Set _failures) throws EOModelException, IOException {
		System.out.println("EOModel.loadFromFolder: " + _modelFolder);
		URL indexURL = new URL(_modelFolder, "index.eomodeld");
		// if (!indexURL.exists()) {
		// throw new EOModelException(indexURL + " does not exist.");
		//		}
		myModelURL = _modelFolder;
		Map rawModelMap = (Map) PropertyListSerialization.propertyListFromURL(indexURL, new EOModelParserDataStructureFactory());
		if (rawModelMap == null) {
			throw new EOModelException(indexURL + " is corrupted.");
		}
		EOModelMap modelMap = new EOModelMap(rawModelMap);
		myModelMap = modelMap;
		Object version = modelMap.get("EOModelVersion");
		if (version instanceof String) {
			myVersion = (String) version;
		} else if (version instanceof Number) {
			myVersion = String.valueOf(((Number) version).floatValue());
		} else {
			throw new IllegalArgumentException("Unknown version format:" + version);
		}
		loadUserInfo(modelMap);

		Set entities = modelMap.getSet("entities");
		if (entities != null) {
			Iterator entitiesIter = entities.iterator();
			while (entitiesIter.hasNext()) {
				EOModelMap entityMap = new EOModelMap((Map) entitiesIter.next());
				String entityName = entityMap.getString("name", true);
				EOEntity entity = new EOEntity();
				URL entityURL = new URL(_modelFolder, entityName + ".plist");
				if (URLUtils.exists(entityURL)) {
					entity.loadFromURL(entityURL, _failures);
					URL fspecURL = new URL(_modelFolder, entityName + ".fspec");
					if (URLUtils.exists(fspecURL)) {
						entity.loadFetchSpecsFromURL(fspecURL, _failures);
					}
					addEntity(entity, true, false, _failures);
				} else {
					_failures.add(new EOModelVerificationFailure(this, "The entity file " + entityURL + " was missing.", false));
				}
			}
		}

		Set storedProcedureNames = modelMap.getSet("storedProcedures");
		if (storedProcedureNames != null) {
			Iterator storedProcedureNamesIter = storedProcedureNames.iterator();
			while (storedProcedureNamesIter.hasNext()) {
				String storedProcedureName = (String) storedProcedureNamesIter.next();
				EOStoredProcedure storedProcedure = new EOStoredProcedure();
				URL storedProcedureURL = new URL(_modelFolder, storedProcedureName + ".storedProcedure");
				if (URLUtils.exists(storedProcedureURL)) {
					storedProcedure.loadFromURL(storedProcedureURL, _failures);
					addStoredProcedure(storedProcedure, false, _failures);
				} else {
					_failures.add(new EOModelVerificationFailure(this, "The stored procedure file " + storedProcedureURL + " was missing.", false));
				}
			}
		}

		Map internalInfoMap = modelMap.getMap("internalInfo");
		if (internalInfoMap != null) {
			Set deletedEntityNamesInObjectStore = modelMap.getSet("_deletedEntityNamesInObjectStore", true);
			if (deletedEntityNamesInObjectStore != null) {
				myDeletedEntityNamesInObjectStore = deletedEntityNamesInObjectStore;
			}
		}

		EOModelMap entityModelerMap = new EOModelMap((Map) getUserInfo().get(EOModel.ENTITY_MODELER_KEY));
		Map databaseConfigs = entityModelerMap.getMap("databaseConfigs");
		if (databaseConfigs != null) {
			Iterator databaseConfigsIter = databaseConfigs.entrySet().iterator();
			while (databaseConfigsIter.hasNext()) {
				Map.Entry databaseConfigEntry = (Map.Entry) databaseConfigsIter.next();
				String name = (String) databaseConfigEntry.getKey();
				EODatabaseConfig databaseConfig = new EODatabaseConfig(name);
				databaseConfig.loadFromMap(new EOModelMap((Map) databaseConfigEntry.getValue()), _failures);
				addDatabaseConfig(databaseConfig, false, _failures);
			}
		}
		EODatabaseConfig activeDatabaseConfig = null;
		String activeDatabaseConfigName = entityModelerMap.getString("activeDatabaseConfigName", false);
		if (activeDatabaseConfigName != null) {
			activeDatabaseConfig = getDatabaseConfigNamed(activeDatabaseConfigName);
		}
		// If there is a connection dictionary, then look for a database config
		// that is equivalent ...
		Map connectionDictionary = modelMap.getMap("connectionDictionary", true);
		if (connectionDictionary != null && !connectionDictionary.isEmpty()) {
			String adaptorName = modelMap.getString("adaptorName", true);
			EODatabaseConfig tempConnectionDictionaryDatabaseConfig = _createDatabaseConfig(adaptorName, connectionDictionary);
			EODatabaseConfig connectionDictionaryDatabaseConfig = null;
			Iterator databaseConfigsIter = myDatabaseConfigs.iterator();
			while (connectionDictionaryDatabaseConfig == null && databaseConfigsIter.hasNext()) {
				EODatabaseConfig databaseConfig = (EODatabaseConfig) databaseConfigsIter.next();
				if (tempConnectionDictionaryDatabaseConfig.isEquivalent(databaseConfig, false)) {
					connectionDictionaryDatabaseConfig = databaseConfig;
				}
			}
			// if one isn't found, then make a new database config based off the
			// connection dictionary
			if (connectionDictionaryDatabaseConfig == null) {
				connectionDictionaryDatabaseConfig = tempConnectionDictionaryDatabaseConfig;
				addDatabaseConfig(connectionDictionaryDatabaseConfig, false, _failures);
			}
			// if the identified active database config isn't the connection
			// dictionary config, then
			// change the active config to the connection dictionary config
			if (activeDatabaseConfig != connectionDictionaryDatabaseConfig) {
				activeDatabaseConfig = connectionDictionaryDatabaseConfig;
			}
		}
		// try to always have at least one active database config
		if (activeDatabaseConfig == null && !myDatabaseConfigs.isEmpty()) {
			activeDatabaseConfig = (EODatabaseConfig) myDatabaseConfigs.iterator().next();
		}
		if (activeDatabaseConfig != null) {
			if (ComparisonUtils.equals(activeDatabaseConfig.getName(), activeDatabaseConfigName)) {
				myActiveDatabaseConfig = activeDatabaseConfig;
			} else {
				setActiveDatabaseConfig(activeDatabaseConfig);
			}
		}
	}

	public EOModelMap toMap() {
		EOModelMap modelMap = myModelMap.cloneModelMap();
		modelMap.setString("EOModelVersion", myVersion, true);
		String adaptorName = EODatabaseConfig.JDBC_ADAPTOR_NAME;
		Map connectionDictionary = null;
		if (myActiveDatabaseConfig != null) {
			connectionDictionary = myActiveDatabaseConfig.getConnectionDictionary();
			adaptorName = myActiveDatabaseConfig.getAdaptorName();
		}
		modelMap.put("connectionDictionary", connectionDictionary);
		modelMap.setString("adaptorName", adaptorName, true);

		Set entities = new PropertyListSet();
		Set entitiesWithSharedObjects = new PropertyListSet();
		Iterator entitiesIter = myEntities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			EOModelMap entityMap = new EOModelMap();
			entityMap.setString("className", entity.getClassName(), true);
			EOEntity parent = entity.getParent();
			String parentName = (parent == null) ? null : parent.getName();
			entityMap.setString("parent", parentName, true);
			entityMap.setString("name", entity.getName(), true);
			entities.add(entityMap);
			if (entity.hasSharedObjects()) {
				entitiesWithSharedObjects.add(entity.getName());
			}
		}
		modelMap.setSet("entities", entities, true);

		modelMap.setSet("entitiesWithSharedObjects", entitiesWithSharedObjects, true);

		Map internalInfoMap = modelMap.getMap("internalInfo");
		if (internalInfoMap == null) {
			internalInfoMap = new HashMap();
		}
		if (myDeletedEntityNamesInObjectStore != null && !myDeletedEntityNamesInObjectStore.isEmpty()) {
			internalInfoMap.put("_deletedEntityNamesInObjectStore", myDeletedEntityNamesInObjectStore);
		} else {
			internalInfoMap.remove("_deletedEntityNamesInObjectStore");
		}
		modelMap.setMap("internalInfo", internalInfoMap, true);

		Set storedProcedures = new PropertyListSet();
		Iterator storedProceduresIter = myStoredProcedures.iterator();
		while (storedProceduresIter.hasNext()) {
			EOStoredProcedure storedProcedure = (EOStoredProcedure) storedProceduresIter.next();
			storedProcedures.add(storedProcedure.getName());
		}
		modelMap.setSet("storedProcedures", storedProcedures, true);

		EOModelMap entityModelerMap = new EOModelMap((Map) getUserInfo().get(EOModel.ENTITY_MODELER_KEY));
		if (myActiveDatabaseConfig == null) {
			entityModelerMap.remove("activeDatabaseConfigName");
		} else {
			entityModelerMap.put("activeDatabaseConfigName", myActiveDatabaseConfig.getName());
		}
		Map databaseConfigs = new PropertyListMap();
		Iterator databaseConfigsIter = myDatabaseConfigs.iterator();
		while (databaseConfigsIter.hasNext()) {
			EODatabaseConfig databaseConfig = (EODatabaseConfig) databaseConfigsIter.next();
			databaseConfigs.put(databaseConfig.getName(), databaseConfig.toMap());
		}
		entityModelerMap.setMap("databaseConfigs", databaseConfigs, true);
		if (entityModelerMap.isEmpty()) {
			getUserInfo().remove(EOModel.ENTITY_MODELER_KEY);
		} else {
			getUserInfo().put(EOModel.ENTITY_MODELER_KEY, entityModelerMap);
		}

		writeUserInfo(modelMap);

		return modelMap;
	}

	public File saveToFolder(File parentFolder) throws IOException {
		File modelFolder;
		if (parentFolder.getName().endsWith(".eomodeld")) {
			modelFolder = parentFolder;
		} else {
			modelFolder = new File(parentFolder, myName + ".eomodeld");
		}
		if (!modelFolder.exists()) {
			if (!modelFolder.mkdirs()) {
				throw new IOException("Failed to create folder '" + modelFolder + "'.");
			}
		}
		myModelURL = modelFolder.toURL();
		File indexFile = new File(modelFolder, "index.eomodeld");
		EOModelMap modelMap = toMap();
		PropertyListSerialization.propertyListToFile(indexFile, modelMap);

		if (myDeletedEntityNames != null) {
			Iterator deletedEntityNameIter = myDeletedEntityNames.iterator();
			while (deletedEntityNameIter.hasNext()) {
				String entityName = (String) deletedEntityNameIter.next();
				File entityFile = new File(modelFolder, entityName + ".plist");
				if (entityFile.exists()) {
					entityFile.delete();
				}
				File fspecFile = new File(modelFolder, entityName + ".fspec");
				if (fspecFile.exists()) {
					fspecFile.delete();
				}
			}
		}

		Iterator entitiesIter = myEntities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			String entityName = entity.getName();
			File entityFile = new File(modelFolder, entityName + ".plist");
			entity.saveToFile(entityFile);
			File fspecFile = new File(modelFolder, entityName + ".fspec");
			entity.saveFetchSpecsToFile(fspecFile);
		}

		if (myDeletedStoredProcedureNames != null) {
			Iterator deletedStoredProcedureNameIter = myDeletedStoredProcedureNames.iterator();
			while (deletedStoredProcedureNameIter.hasNext()) {
				String storedProcedureName = (String) deletedStoredProcedureNameIter.next();
				File storedProcedureFile = new File(modelFolder, storedProcedureName + ".storedProcedure");
				if (storedProcedureFile.exists()) {
					storedProcedureFile.delete();
				}
			}
		}

		Iterator storedProceduresIter = myStoredProcedures.iterator();
		while (storedProceduresIter.hasNext()) {
			EOStoredProcedure storedProcedure = (EOStoredProcedure) storedProceduresIter.next();
			String storedProcedureName = storedProcedure.getName();
			File storedProcedureFile = new File(modelFolder, storedProcedureName + ".storedProcedure");
			storedProcedure.saveToFile(storedProcedureFile);
		}

		return modelFolder;
	}

	public void resolve(Set _failures) {
		Iterator entitiesIter = myEntities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			entity.resolve(_failures);
		}
	}

	public void verify(Set _failures) {
		// TODO

		Iterator entitiesIter = myEntities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			entity.verify(_failures);
		}
	}

	public String getFullyQualifiedName() {
		return myName;
	}

	public String toString() {
		return "[EOModel: name = " + myName + "; entities = " + myEntities + "]";
	}

	/** Begin Prototypes * */
	public synchronized void clearCachedPrototypes(Set _failures, boolean _reload) {
		myPrototypeAttributeCache = null;
		Iterator entitiesIter = myEntities.iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			entity.clearCachedPrototypes(_failures, _reload);
		}
	}

	public synchronized Set getPrototypeAttributeNames() {
		Set prototypeAttributeNames = new PropertyListSet();
		Iterator prototypeAttributesIter = getPrototypeAttributes().iterator();
		while (prototypeAttributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) prototypeAttributesIter.next();
			prototypeAttributeNames.add(attribute.getName());
		}
		return prototypeAttributeNames;
	}

	public String _getDefaultPrototypeEntityName(String name) {
		return "EO" + (name != null ? name : "") + "Prototypes";
	}

	public String getAdaptorName() {
		String adaptorName = EODatabaseConfig.JDBC_ADAPTOR_NAME;
		if (myActiveDatabaseConfig != null) {
			adaptorName = myActiveDatabaseConfig.getAdaptorName();
		}
		return adaptorName;
	}

	public String _getAdaptorPrototypeEntityName(String adaptorName, String name) {
		String adaptorPrototypeEntityName = null;
		if (adaptorName != null) {
			adaptorPrototypeEntityName = "EO" + adaptorName + (name != null ? name : "") + "Prototypes";
		}
		return adaptorPrototypeEntityName;
	}

	public String _getDriverPrototypeEntityName(Map _connectionDictionary, String name) {
		String driverPrototypeEntityName = null;
		String adaptorName = getAdaptorName();
		// MS: Hardcoded JDBC reference hack ...
		if ("JDBC".equals(adaptorName)) {
			if (_connectionDictionary != null) {
				String pluginName = (String) _connectionDictionary.get("plugin");
				if (pluginName == null || pluginName.length() == 0) {
					String jdbcUrl = (String) _connectionDictionary.get("URL");
					if (jdbcUrl != null && jdbcUrl.length() > 0) {
						int firstColon = jdbcUrl.indexOf(':');
						int secondColon = jdbcUrl.indexOf(':', firstColon + 1);
						if (firstColon != -1 && secondColon != -1) {
							pluginName = jdbcUrl.substring(firstColon + 1, secondColon);
						}
					}
				}
				if (pluginName != null) {
					driverPrototypeEntityName = "EOJDBC" + pluginName + (name != null ? name : "") + "Prototypes";
				}
			}
		}
		return driverPrototypeEntityName;
	}

	public EOEntity _getPreferredPrototypeEntity(String adaptorName, Map connectionDictionary) {
		EOEntity prototypeEntity = null;
		String driverPrototypeEntityName = _getDriverPrototypeEntityName(connectionDictionary, null);
		if (driverPrototypeEntityName != null && myModelGroup != null) {
			prototypeEntity = myModelGroup.getEntityNamed(driverPrototypeEntityName);
		}
		if (prototypeEntity == null) {
			String adaptorPrototypeEntityName = _getAdaptorPrototypeEntityName(adaptorName, null);
			if (adaptorPrototypeEntityName != null && myModelGroup != null) {
				prototypeEntity = myModelGroup.getEntityNamed(adaptorPrototypeEntityName);
			}
		}
		if (prototypeEntity == null && myModelGroup != null) {
			String defaultPrototypeEntityName = _getDefaultPrototypeEntityName(null);
			prototypeEntity = myModelGroup.getEntityNamed(defaultPrototypeEntityName);
		}
		return prototypeEntity;
	}

	public synchronized Set getPrototypeAttributes() {
		if (myPrototypeAttributeCache == null) {
			Map prototypeAttributeCache = new HashMap();

			Map connectionDictionary = null;
			EODatabaseConfig activeDatabaseConfig = getActiveDatabaseConfig();
			if (activeDatabaseConfig != null) {
				connectionDictionary = activeDatabaseConfig.getConnectionDictionary();
			}
			addPrototypeAttributes(_getDefaultPrototypeEntityName(null), prototypeAttributeCache);
			addPrototypeAttributes(_getAdaptorPrototypeEntityName(getAdaptorName(), null), prototypeAttributeCache);
			addPrototypeAttributes(_getDriverPrototypeEntityName(connectionDictionary, null), prototypeAttributeCache);

			addPrototypeAttributes(_getDefaultPrototypeEntityName(getName()), prototypeAttributeCache);
			addPrototypeAttributes(_getAdaptorPrototypeEntityName(getAdaptorName(), getName()), prototypeAttributeCache);
			addPrototypeAttributes(_getDriverPrototypeEntityName(connectionDictionary, getName()), prototypeAttributeCache);

			if (activeDatabaseConfig != null) {
				EOEntity prototypeEntity = activeDatabaseConfig.getPrototype();
				if (prototypeEntity != null) {
					addPrototypeAttributes(prototypeEntity.getName(), prototypeAttributeCache);
				}
			}

			// Do we need to support "EOPrototypesToHide" entity?
			myPrototypeAttributeCache = new PropertyListSet();
			myPrototypeAttributeCache.addAll(prototypeAttributeCache.values());
		}
		return myPrototypeAttributeCache;
	}

	protected void addPrototypeAttributes(String _prototypeEntityName, Map _prototypeAttributeCache) {
		if (_prototypeEntityName != null) {
			EOEntity prototypeEntity = myModelGroup.getEntityNamed(_prototypeEntityName);
			if (prototypeEntity != null) {
				Iterator attributeIter = prototypeEntity.getAttributes().iterator();
				while (attributeIter.hasNext()) {
					EOAttribute prototypeAttribute = (EOAttribute) attributeIter.next();
					_prototypeAttributeCache.put(prototypeAttribute.getName(), prototypeAttribute);
				}
			}
		}
	}

	public EOAttribute getPrototypeAttributeNamed(String _name) {
		EOAttribute matchingAttribute = null;
		Set prototypeAttributes = getPrototypeAttributes();
		Iterator attributesIter = prototypeAttributes.iterator();
		while (matchingAttribute == null && attributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributesIter.next();
			if (attribute.getName().equals(_name)) {
				matchingAttribute = attribute;
			}
		}
		return matchingAttribute;
	}

	/** End Prototypes **/ 

	public static void main(String[] args) throws IOException, EOModelException {
		Set failures = new LinkedHashSet();

		EOModelGroup modelGroup = new EOModelGroup();
		modelGroup.addModelsFromFolder(new File("/Library/Frameworks/ERPrototypes.framework/Resources").toURL(), false, failures, false, null);
		modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTask").toURL(), false, failures, false, null);
		modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTAccounting").toURL(), false, failures, false, null);
		modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTCMS").toURL(), false, failures, false, null);
		modelGroup.addModelsFromFolder(new File("/Users/mschrag/Documents/workspace/MDTWOExtensions").toURL(), false, failures, false, null);

		modelGroup.resolve(failures);
		modelGroup.verify(failures);
		Iterator failuresIter = failures.iterator();
		while (failuresIter.hasNext()) {
			EOModelVerificationFailure failure = (EOModelVerificationFailure) failuresIter.next();
			System.out.println("EOModel.main: " + failure);
		}

		File outputPath = new File("/tmp");
		System.out.println("EOModel.main: Saving model to " + outputPath + " ...");
		EOModel mdtaskModel = modelGroup.getModelNamed("MDTask");
		mdtaskModel.saveToFolder(outputPath);
		System.out.println("EOModel.main: Done.");
	}
}
