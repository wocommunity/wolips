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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.core.model.history.EOEntityAddedEvent;
import org.objectstyle.wolips.eomodeler.core.model.history.EOEntityDeletedEvent;
import org.objectstyle.wolips.eomodeler.core.model.history.ModelEvents;
import org.objectstyle.wolips.eomodeler.core.utils.NamingConvention;

public class EOModel extends UserInfoableEOModelObject<EOModelGroup> implements ISortableEOModelObject {
	public static final String CURRENT_VERSION = "1.0.1";

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

	public static final String ATTRIBUTE_NAMING_CONVENTION = "attributeNamingConvention";

	public static final String ENTITY_NAMING_CONVENTION = "entityNamingConvention";

	public static final String MODEL_SAVING = "modelSaving";

	public static final String REVERSE_ENGINEERED = "reverseEngineered";

	private EOModelGroup myModelGroup;

	private String myName;

	private String myVersion;

	private Set<EOEntity> myEntities;

	private Set<EODatabaseConfig> myDatabaseConfigs;

	private EODatabaseConfig myActiveDatabaseConfig;

	private Set<EOStoredProcedure> myStoredProcedures;

	private Set<String> myDeletedEntityNamesInObjectStore;

	private Set<String> myDeletedEntityNames;

	private Set<String> myDeletedStoredProcedureNames;

	private EOModelMap myModelMap;

	private boolean myDirty;

	private URL myModelURL;

	private Set<EOAttribute> myPrototypeAttributeCache;

	private ModelEvents _modelEvents;

	private NamingConvention _entityNamingConvention;

	private NamingConvention _attributeNamingConvention;
	
	private boolean _reverseEngineered;

	public EOModel(String _name) {
		myName = _name;
		myEntities = new PropertyListSet<EOEntity>();
		myStoredProcedures = new PropertyListSet<EOStoredProcedure>();
		myDeletedEntityNamesInObjectStore = new PropertyListSet<String>();
		myDeletedEntityNames = new PropertyListSet<String>();
		myDeletedStoredProcedureNames = new PropertyListSet<String>();
		myDatabaseConfigs = new PropertyListSet<EODatabaseConfig>();
		myVersion = "2.1";
		myModelMap = new EOModelMap();
		_modelEvents = new ModelEvents();
		_entityNamingConvention = NamingConvention.DEFAULT;
		_attributeNamingConvention = NamingConvention.DEFAULT;
	}

	public EOModel(URL modelURL) throws EOModelException, IOException {
		this(EOModelGroup.getModelNameForURL(modelURL));
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		loadFromURL(modelURL, failures);
		if (failures.size() > 0) {
			throw new EOModelException("Failed to load model from URL '" + modelURL + "': " + failures);
		}
	}

	public EOModel(URL modelURL, Set<EOModelVerificationFailure> failures) throws EOModelException, IOException {
		this(EOModelGroup.getModelNameForURL(modelURL));
		loadFromURL(modelURL, failures);
	}

	protected void _storedProcedureChanged(EOStoredProcedure _storedProcedure, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(EOModel.STORED_PROCEDURE + "." + _propertyName, _oldValue, _newValue);
		firePropertyChange(EOModel.STORED_PROCEDURE, null, _storedProcedure);
	}

	public void _setModelGroup(EOModelGroup _modelGroup) {
		myModelGroup = _modelGroup;
	}

	public void setEntityNamingConvention(NamingConvention entityNamingConvention) {
		NamingConvention oldEntityNamingConvention = _entityNamingConvention;
		_entityNamingConvention = entityNamingConvention;
		firePropertyChange(EOModel.ENTITY_NAMING_CONVENTION, oldEntityNamingConvention, _entityNamingConvention);
	}

	public NamingConvention getEntityNamingConvention() {
		return _entityNamingConvention;
	}

	public void setAttributeNamingConvention(NamingConvention attributeNamingConvention) {
		NamingConvention oldAttributeNamingConvention = _attributeNamingConvention;
		_attributeNamingConvention = attributeNamingConvention;
		firePropertyChange(EOModel.ATTRIBUTE_NAMING_CONVENTION, oldAttributeNamingConvention, _attributeNamingConvention);
	}

	public NamingConvention getAttributeNamingConvention() {
		return _attributeNamingConvention;
	}

	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	public String guessPackageName() {
		return guessPackageName(getEntities());
	}

	public String guessPackageName(Set<EOEntity> _entities) {
		String guessPackageName = null;
		for (EOEntity entity : _entities) {
			String className = entity.getClassName();
			if (className != null) {
				int packageNameEnd = className.lastIndexOf('.');
				String packageName;
				if (packageNameEnd != -1) {
					packageName = className.substring(0, packageNameEnd);
				} else {
					packageName = "";
				}
				// ignore blank package names ...
				if (!"".equals(packageName)) {
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
		}
		return guessPackageName;
	}

	public EOEntity addBlankEntity(String name) throws DuplicateNameException {
		String newEntityName = findUnusedEntityName(name);
		EOEntity entity = new EOEntity(newEntityName);
		entity.guessExternalNameInModel(this);
		entity.guessClassNameInModel(this);
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

	public boolean isEditing() {
		EOModelGroup modelGroup = getModelGroup();
		boolean editing = (modelGroup == null || getName().equals(modelGroup.getEditingModelName()));
		return editing;
	}

	public boolean isReverseEngineered() {
		return _reverseEngineered;
	}

	public void setReverseEngineered(boolean reverseEngineered) {
		Boolean oldReverseEngineered = Boolean.valueOf(_reverseEngineered);
		_reverseEngineered = reverseEngineered;
		firePropertyChange(EOModel.REVERSE_ENGINEERED, oldReverseEngineered, Boolean.valueOf(_reverseEngineered));
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
		if (!myDirty && !EOModel.DIRTY.equals(_propertyName) && !EOModel.MODEL_SAVING.equals(_propertyName)) {
			setDirty(true);
		}
		if (myModelGroup != null) {
			myModelGroup._modelChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	protected void _entityChanged(EOEntity _entity, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(EOModel.ENTITY + "." + _propertyName, _oldValue, _newValue);
		firePropertyChange(EOModel.ENTITY, null, _entity);
	}

	protected void _databaseConfigChanged(EODatabaseConfig _databaseConfig, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(EOModel.DATABASE_CONFIG + "." + _propertyName, _oldValue, _newValue);
		firePropertyChange(EOModel.DATABASE_CONFIG, null, _databaseConfig);
		if (_databaseConfig == myActiveDatabaseConfig) {
			clearCachedPrototypes(null, false);
		}
	}

	public EODatabaseConfig getDatabaseConfigNamed(String _name) {
		EODatabaseConfig matchingDatabaseConfig = null;
		Iterator<EODatabaseConfig> databaseConfigsIter = myDatabaseConfigs.iterator();
		while (matchingDatabaseConfig == null && databaseConfigsIter.hasNext()) {
			EODatabaseConfig entity = databaseConfigsIter.next();
			if (ComparisonUtils.equals(entity.getName(), _name)) {
				matchingDatabaseConfig = entity;
			}
		}
		return matchingDatabaseConfig;
	}

	public String findUnusedDatabaseConfigName(String _newName) {
		return _findUnusedName(_newName, "getDatabaseConfigNamed");
	}

	public void _checkForDuplicateDatabaseConfigName(EODatabaseConfig _databaseConfig, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateDatabaseConfigNameException {
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

	public void addDatabaseConfig(EODatabaseConfig _databaseConfig, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		_databaseConfig._setModel(this);
		_checkForDuplicateDatabaseConfigName(_databaseConfig, _databaseConfig.getName(), _failures);
		_databaseConfig.pasted();
		if (_fireEvents) {
			Set<EODatabaseConfig> oldDatabaseConfigs = null;
			oldDatabaseConfigs = myDatabaseConfigs;
			Set<EODatabaseConfig> newEntities = new TreeSet<EODatabaseConfig>(new PropertyListSet<EODatabaseConfig>());
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
				Iterator<EODatabaseConfig> databaseConfigsIter = myDatabaseConfigs.iterator();
				while (newActiveDatabaseConfig == null && databaseConfigsIter.hasNext()) {
					EODatabaseConfig otherDatabaseConfig = databaseConfigsIter.next();
					if (otherDatabaseConfig != _databaseConfig) {
						newActiveDatabaseConfig = otherDatabaseConfig;
					}
				}
			}
			setActiveDatabaseConfig(newActiveDatabaseConfig);
		}
		Set<EODatabaseConfig> oldDatabaseConfigs = myDatabaseConfigs;
		Set<EODatabaseConfig> newDatabaseConfigs = new TreeSet<EODatabaseConfig>(new PropertyListSet<EODatabaseConfig>());
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

	public EODatabaseConfig _createDatabaseConfig(String adaptorName, Map<Object, Object> _connectionDictionary) {
		EODatabaseConfig defaultDatabaseConfig = new EODatabaseConfig(findUnusedDatabaseConfigName("Default"));
		defaultDatabaseConfig.setAdaptorName(adaptorName);
		defaultDatabaseConfig.setConnectionDictionary(new HashMap<Object, Object>(_connectionDictionary));
		defaultDatabaseConfig.setPrototype(_getPreferredPrototypeEntity(adaptorName, _connectionDictionary));
		defaultDatabaseConfig._setModel(this);
		return defaultDatabaseConfig;
	}

	public Map<EOAttribute, Set<EORelationship>> _createReferencingRelationshipsCache() {
		Map<EOAttribute, Set<EORelationship>> cache = new HashMap<EOAttribute, Set<EORelationship>>();
		_createReferencingRelationshipsCache(cache);
		return cache;
	}

	public void _createReferencingRelationshipsCache(Map<EOAttribute, Set<EORelationship>> cache) {
		for (EOEntity entity : getEntities()) {
			for (EORelationship relationship : entity.getRelationships()) {
				Set<EOAttribute> relatedAttributes = relationship.getRelatedAttributes();
				for (EOAttribute attribute : relatedAttributes) {
					Set<EORelationship> relatedRelationships = cache.get(attribute);
					if (relatedRelationships == null) {
						relatedRelationships = new HashSet<EORelationship>();
						cache.put(attribute, relatedRelationships);
					}
					relatedRelationships.add(relationship);
				}
			}
		}
	}

	public Map<EOEntity, Set<EOEntity>> _createInheritanceCache() {
		Map<EOEntity, Set<EOEntity>> cache = new HashMap<EOEntity, Set<EOEntity>>();
		_createInheritanceCache(cache);
		return cache;
	}

	public void _createInheritanceCache(Map<EOEntity, Set<EOEntity>> cache) {
		for (EOEntity entity : getEntities()) {
			EOEntity parentEntity = entity.getParent();
			if (parentEntity != null) {
				Set<EOEntity> childrenEntities = cache.get(parentEntity);
				if (childrenEntities == null) {
					childrenEntities = new HashSet<EOEntity>();
					cache.put(parentEntity, childrenEntities);
				}
				childrenEntities.add(entity);
			}
		}
	}

	public Set<EODatabaseConfig> getDatabaseConfigs() {
		return myDatabaseConfigs;
	}

//	public int hashCode() {
//		return myName.hashCode();
//	}
//
//	public boolean equals(Object _obj) {
//		return (_obj instanceof EOModel && ((EOModel) _obj).myName.equals(myName));
//	}

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

	/**
	 * Returns the list of entities that are not prototypes.
	 * 
	 * @return list of non prototype entities
	 */
	public Set<EOEntity> getNonPrototypeEntities() {
		Set<EOEntity> entities = new HashSet<EOEntity>();
		for (EOEntity entity : getEntities()) {
			if (!entity.isPrototype()) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public Set<EOEntity> getEntities() {
		return myEntities;
	}

	public Set<EOEntity> getSortedEntities() {
		return new PropertyListSet<EOEntity>(myEntities);
	}

	public EOEntity _getEntityNamedAndCheckModelGroup(String newName) {
		EOEntity entity = myModelGroup.getEntityNamed(newName);
		if (entity == null) {
			entity = getEntityNamed(newName);
		}
		return entity;
	}

	public String findUnusedEntityName(String _newName) {
		return _findUnusedName(_newName, "_getEntityNamedAndCheckModelGroup");
	}

	public void _checkForDuplicateEntityName(EOEntity _entity, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateEntityNameException {
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

	@SuppressWarnings("unused")
	public void _entityNameChanged(String _originalName, String _oldName, String _newName) {
		if (myDeletedEntityNamesInObjectStore == null) {
			myDeletedEntityNamesInObjectStore = new PropertyListSet<String>();
		}
		if (_originalName != null && _originalName.length() > 0) {
			myDeletedEntityNamesInObjectStore.add(_originalName);
			// myDeletedEntityNamesInObjectStore.remove(_newName);
			myDeletedEntityNames.add(_originalName);
			// myDeletedEntityNames.remove(_newName);
		}
	}

	public boolean containsEntityNamed(String _entityName) {
		return getEntityNamed(_entityName) != null;
	}

	public Set importEntitiesFromModel(URL sourceModelURL, Set<EOModelVerificationFailure> failures) throws EOModelException, IOException {
		EOModelGroup sourceModelGroup = new EOModelGroup();
		EOModel sourceModel = new EOModel("Temp");
		sourceModelGroup.addModel(sourceModel);
		sourceModel.loadFromURL(sourceModelURL, false, failures);
		sourceModel.resolve(failures);
		sourceModel.resolveFlattened(failures);
		return importEntitiesFromModel(sourceModel, failures);
	}

	public Set<EOEntity> importEntitiesFromModel(EOModel sourceModel, Set<EOModelVerificationFailure> failures) throws DuplicateNameException {
		Set<EOEntity> clonedEntities = new HashSet<EOEntity>();
		for (EOEntity entity : sourceModel.getEntities()) {
			clonedEntities.add(entity._cloneModelObject());
		}
		addEntities(clonedEntities, failures);
		guessPrototypes(clonedEntities);
		return clonedEntities;
	}

	public void guessPrototypes() {
		guessPrototypes(getEntities());
	}

	public void guessPrototypes(Set<EOEntity> entities) {
		for (EOEntity entity : entities) {
			// clonedEntity.setName(StringUtils.toUppercaseFirstLetter(clonedEntity.getName().toLowerCase()));
			for (EOAttribute attribute : entity.getAttributes()) {
				attribute.guessPrototype(true);
				// clonedAttribute.setName(clonedAttribute.getName().toLowerCase());
			}
			// Iterator clonedRelationshipsIter =
			// clonedEntity.getRelationships().iterator();
			// while (clonedRelationshipsIter.hasNext()) {
			// EORelationship clonedRelationship = (EORelationship)
			// clonedRelationshipsIter.next();
			// clonedRelationship.setName(clonedRelationship.getName().toLowerCase());
			// }
		}
	}

	public void addEntities(Set<EOEntity> entities, Set<EOModelVerificationFailure> failures) throws DuplicateNameException {
		Set<EOEntity> oldEntities = new TreeSet<EOEntity>(new PropertyListSet<EOEntity>());
		oldEntities.addAll(myEntities);

		for (EOEntity entity : entities) {
			addEntity(entity, false, false, failures);
		}
		for (EOEntity entity : entities) {
			entity.pasted();
		}

		firePropertyChange(EOModel.ENTITIES, oldEntities, myEntities);
	}

	public void addEntity(EOEntity _entity) throws DuplicateNameException {
		addEntity(_entity, true, true, null);
	}

	public void addEntity(EOEntity entity, boolean pasteImmediately, boolean fireEvents, Set<EOModelVerificationFailure> failures) throws DuplicateNameException {

		entity._setModel(this);
		_checkForDuplicateEntityName(entity, entity.getName(), failures);
		if (pasteImmediately) {
			entity.pasted();
		}
		myDeletedEntityNames.remove(entity.getName());
		if (fireEvents) {
			Set<EOEntity> oldEntities = null;
			oldEntities = myEntities;
			Set<EOEntity> newEntities = new TreeSet<EOEntity>(new PropertyListSet<EOEntity>());
			newEntities.addAll(myEntities);
			newEntities.add(entity);
			myEntities = newEntities;
			_modelEvents.addEvent(new EOEntityAddedEvent(entity));
			firePropertyChange(EOModel.ENTITIES, oldEntities, myEntities);
		} else {
			myEntities.add(entity);
		}
	}

	public void removeEntity(EOEntity _entity) {
		myDeletedEntityNames.add(_entity.getName());
		Set<EOEntity> oldEntities = myEntities;
		Set<EOEntity> newEntities = new TreeSet<EOEntity>(new PropertyListSet<EOEntity>());
		newEntities.addAll(myEntities);
		newEntities.remove(_entity);
		myEntities = newEntities;
		_modelEvents.addEvent(new EOEntityDeletedEvent(_entity));
		firePropertyChange(EOModel.ENTITIES, oldEntities, myEntities);
		_entity._setModel(null);
	}

	public EOEntity getEntityNamed(String _name) {
		EOEntity matchingEntity = null;
		Iterator<EOEntity> entitiesIter = myEntities.iterator();
		while (matchingEntity == null && entitiesIter.hasNext()) {
			EOEntity entity = entitiesIter.next();
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

	public void addStoredProcedure(EOStoredProcedure _storedProcedure, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateStoredProcedureNameException {
		_storedProcedure._setModel(this);
		_checkForDuplicateStoredProcedureName(_storedProcedure, _storedProcedure.getName(), _failures);
		_storedProcedure.pasted();
		myDeletedStoredProcedureNames.remove(_storedProcedure.getName());
		if (_fireEvents) {
			Set<EOStoredProcedure> oldStoredProcedures = myStoredProcedures;
			Set<EOStoredProcedure> newStoredProcedures = new TreeSet<EOStoredProcedure>(new PropertyListSet<EOStoredProcedure>());
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
		Set<EOStoredProcedure> oldStoredProcedures = myStoredProcedures;
		Set<EOStoredProcedure> newStoredProcedures = new TreeSet<EOStoredProcedure>(new PropertyListSet<EOStoredProcedure>());
		newStoredProcedures.addAll(myStoredProcedures);
		newStoredProcedures.remove(_storedProcedure);
		myStoredProcedures = newStoredProcedures;
		firePropertyChange(EOModel.STORED_PROCEDURES, oldStoredProcedures, myStoredProcedures);
		_storedProcedure._setModel(null);
	}

	public String findUnusedStoredProcedureName(String _newName) {
		return _findUnusedName(_newName, "getStoredProcedureNamed");
	}

	public Set<EOStoredProcedure> getStoredProcedures() {
		return myStoredProcedures;
	}

	public Set<EOStoredProcedure> getSortedStoredProcedures() {
		return new PropertyListSet<EOStoredProcedure>(myStoredProcedures);
	}

	public EOStoredProcedure getStoredProcedureNamed(String _name) {
		EOStoredProcedure matchingStoredProcedure = null;
		Iterator<EOStoredProcedure> storedProceduresIter = myStoredProcedures.iterator();
		while (matchingStoredProcedure == null && storedProceduresIter.hasNext()) {
			EOStoredProcedure attribute = storedProceduresIter.next();
			if (ComparisonUtils.equals(attribute.getName(), _name)) {
				matchingStoredProcedure = attribute;
			}
		}
		return matchingStoredProcedure;
	}

	public void _checkForDuplicateStoredProcedureName(EOStoredProcedure _storedProcedure, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateStoredProcedureNameException {
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

	public void loadFromURL(URL modelURL, Set<EOModelVerificationFailure> failures) throws EOModelException, IOException {
		loadFromURL(modelURL, true, failures);
	}

	public void loadFromURL(URL _modelFolder, boolean createMissingDatabaseConfig, Set<EOModelVerificationFailure> _failures) throws EOModelException, MalformedURLException {
		// System.out.println("EOModel.loadFromURL: " + _modelFolder);
		URL indexURL = new URL(_modelFolder, "index.eomodeld");
		// if (!indexURL.exists()) {
		// throw new EOModelException(indexURL + " does not exist.");
		// }
		myModelURL = _modelFolder;
		Map rawModelMap;
		try {
			rawModelMap = (Map) WOLPropertyListSerialization.propertyListFromURL(indexURL, new EOModelParserDataStructureFactory());
		} catch (Exception e) {
			throw new EOModelException("index.eomodeld is corrupted.", e);
		}
		if (rawModelMap == null) {
			throw new EOModelException("index.eomodeld is corrupted.");
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

		Set<Map> entities = modelMap.getSet("entities");
		if (entities != null) {
			for (Map entitiesMap : entities) {
				EOModelMap entityMap = new EOModelMap(entitiesMap);
				String entityName = entityMap.getString("name", true);
				EOEntity entity = new EOEntity();
				URL entityURL = new URL(_modelFolder, entityName + ".plist");
				if (URLUtils.exists(entityURL)) {
					entity.loadFromURL(entityURL, _failures);
					URL fspecURL = new URL(_modelFolder, entityName + ".fspec");
					if (URLUtils.exists(fspecURL)) {
						entity.loadFetchSpecsFromURL(fspecURL, _failures);
					}
					if (entity.getName() == null) {
						_failures.add(new EOModelVerificationFailure(this, this, "The entity file " + entityURL + " defines an entity with no name.", false));
					}
					addEntity(entity, true, false, _failures);
				} else {
					_failures.add(new EOModelVerificationFailure(this, this, "The entity file " + entityURL + " was missing.", false));
				}
			}
		}

		Set<String> storedProcedureNames = modelMap.getSet("storedProcedures");
		if (storedProcedureNames != null) {
			for (String storedProcedureName : storedProcedureNames) {
				EOStoredProcedure storedProcedure = new EOStoredProcedure();
				URL storedProcedureURL = new URL(_modelFolder, storedProcedureName + ".storedProcedure");
				if (URLUtils.exists(storedProcedureURL)) {
					storedProcedure.loadFromURL(storedProcedureURL, _failures);
					addStoredProcedure(storedProcedure, false, _failures);
				} else {
					_failures.add(new EOModelVerificationFailure(this, this, "The stored procedure file " + storedProcedureURL + " was missing.", false));
				}
			}
		}

		Map<Object, Object> internalInfoMap = modelMap.getMap("internalInfo");
		if (internalInfoMap != null) {
			Set<String> deletedEntityNamesInObjectStore = modelMap.getSet("_deletedEntityNamesInObjectStore", true);
			if (deletedEntityNamesInObjectStore != null) {
				myDeletedEntityNamesInObjectStore = deletedEntityNamesInObjectStore;
			}
		}

		EOModelMap entityModelerMap = getEntityModelerMap(false);
		_entityNamingConvention = NamingConvention.loadFromMap("entityNamingConvention", entityModelerMap);
		_attributeNamingConvention = NamingConvention.loadFromMap("attributeNamingConvention", entityModelerMap);
		_reverseEngineered = entityModelerMap.getBoolean("reverseEngineered", false);

		Map<String, Map> databaseConfigs = entityModelerMap.getMap("databaseConfigs");
		if (databaseConfigs != null) {
			for (Map.Entry<String, Map> databaseConfigEntry : databaseConfigs.entrySet()) {
				String name = databaseConfigEntry.getKey();
				EODatabaseConfig databaseConfig = new EODatabaseConfig(name);
				databaseConfig.loadFromMap(new EOModelMap(databaseConfigEntry.getValue()), _failures);
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
		Map<Object, Object> connectionDictionary = modelMap.getMap("connectionDictionary", true);
		if (connectionDictionary != null && !connectionDictionary.isEmpty()) {
			String adaptorName = modelMap.getString("adaptorName", true);
			EODatabaseConfig tempConnectionDictionaryDatabaseConfig = _createDatabaseConfig(adaptorName, connectionDictionary);
			EODatabaseConfig connectionDictionaryDatabaseConfig = null;
			Iterator<EODatabaseConfig> databaseConfigsIter = myDatabaseConfigs.iterator();
			while (connectionDictionaryDatabaseConfig == null && databaseConfigsIter.hasNext()) {
				EODatabaseConfig databaseConfig = databaseConfigsIter.next();
				if (tempConnectionDictionaryDatabaseConfig.isEquivalent(databaseConfig, false)) {
					connectionDictionaryDatabaseConfig = databaseConfig;
				}
			}
			// if one isn't found, then make a new database config based off the
			// connection dictionary
			if (connectionDictionaryDatabaseConfig == null) {
				connectionDictionaryDatabaseConfig = tempConnectionDictionaryDatabaseConfig;
				if (canSave() && createMissingDatabaseConfig && isEditing()) {
					addDatabaseConfig(connectionDictionaryDatabaseConfig, false, _failures);
					_failures.add(new EOModelVerificationFailure(this, this, "Creating default database config for model '" + getName() + "'.", true, null));
				}
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
			activeDatabaseConfig = myDatabaseConfigs.iterator().next();
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

		Set<Map> entities = new PropertyListSet<Map>(EOModelMap.asArray(myModelMap.get("entities")));
		Set<String> entitiesWithSharedObjects = new PropertyListSet<String>(EOModelMap.asArray(myModelMap.get("entitiesWithSharedObjects")));
		for (EOEntity entity : myEntities) {
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

		Map<Object, Object> internalInfoMap = modelMap.getMap("internalInfo");
		if (internalInfoMap == null) {
			internalInfoMap = new HashMap<Object, Object>();
		}
		if (myDeletedEntityNamesInObjectStore != null && !myDeletedEntityNamesInObjectStore.isEmpty()) {
			internalInfoMap.put("_deletedEntityNamesInObjectStore", myDeletedEntityNamesInObjectStore);
		} else {
			internalInfoMap.remove("_deletedEntityNamesInObjectStore");
		}
		modelMap.setMap("internalInfo", internalInfoMap, true);

		Set<String> storedProcedures = new PropertyListSet<String>(EOModelMap.asArray(myModelMap.get("storedProcedures")));
		for (EOStoredProcedure storedProcedure : myStoredProcedures) {
			storedProcedures.add(storedProcedure.getName());
		}
		modelMap.setSet("storedProcedures", storedProcedures, true);

		EOModelMap entityModelerMap = getEntityModelerMap(true);
		Map<String, Map> databaseConfigs = new PropertyListMap<String, Map>();
		for (EODatabaseConfig databaseConfig : myDatabaseConfigs) {
			databaseConfigs.put(databaseConfig.getName(), databaseConfig.toMap());
		}
		entityModelerMap.setMap("databaseConfigs", databaseConfigs, true);

		NamingConvention.toMap(_entityNamingConvention, "entityNamingConvention", entityModelerMap);
		NamingConvention.toMap(_attributeNamingConvention, "attributeNamingConvention", entityModelerMap);

		if (myActiveDatabaseConfig == null || databaseConfigs.get(myActiveDatabaseConfig.getName()) == null) {
			entityModelerMap.remove("activeDatabaseConfigName");
		} else {
			entityModelerMap.put("activeDatabaseConfigName", myActiveDatabaseConfig.getName());
		}
		
		entityModelerMap.setBoolean("reverseEngineered", Boolean.valueOf(_reverseEngineered), EOModelMap.YNOptionalDefaultNo);

		writeUserInfo(modelMap);

		return modelMap;
	}

	/**
	 * Returns the .eomodeld for the given folder. If this is an .eomodeld
	 * folder, it returns the folder you pass in. Otherwise, it creates a child
	 * with this model's name.
	 * 
	 * @param folder
	 *            the folder to write to
	 * @return the corresponding .eomodeld folder
	 */
	public File _eomodeldFolderForFolder(File folder) {
		File modelFolder;
		if (folder.getName().endsWith(".eomodeld")) {
			modelFolder = folder;
		} else {
			modelFolder = new File(folder, myName + ".eomodeld");
		}
		return modelFolder;
	}

	/**
	 * Returns true if you are able to write this model to the folder it was
	 * loaded from.
	 * 
	 * @return true if you are able to save this model
	 * @throws MalformedURLException
	 *             if the index URL is invalid
	 */
	public boolean canSave() throws MalformedURLException {
		boolean canSave;
		URL indexURL = getIndexURL();
		if (indexURL == null) {
			canSave = false;
		} else {
			File indexFile = URLUtils.cheatAndTurnIntoFile(indexURL);
			File modelFolder = indexFile.getParentFile();
			canSave = canSaveToFolder(modelFolder);
		}
		return canSave;
	}

	/**
	 * Returns true if you are able to write this model to the given folder.
	 * 
	 * @param parentFolder
	 *            the folder to write the model into
	 * @return true if you are able to write to the folder
	 */
	public boolean canSaveToFolder(File parentFolder) {
		boolean canSave;
		File modelFolder = _eomodeldFolderForFolder(parentFolder);
		if (modelFolder.exists()) {
			if (modelFolder.isFile()) {
				canSave = false;
			} else {
				canSave = modelFolder.canWrite();
			}
		} else {
			canSave = modelFolder.getParentFile().canWrite();
		}
		return canSave;
	}

	/**
	 * Saves this model to the same folder it was loaded from.
	 * 
	 * @return the .eomodeld folder
	 * @throws IOException
	 *             if the model could not be saved
	 * @throws PropertyListParserException
	 */
	public File save() throws IOException, PropertyListParserException {
		URL indexURL = getIndexURL();
		if (indexURL == null) {
			throw new IOException("This model has no index file URL.");
		}
		File indexFile = URLUtils.cheatAndTurnIntoFile(indexURL);
		File modelFolder = indexFile.getParentFile();
		return saveToFolder(modelFolder.getParentFile());
	}

	public File saveToFolder(File parentFolder) throws IOException, PropertyListParserException {
		firePropertyChange(EOModel.MODEL_SAVING, Boolean.FALSE, Boolean.TRUE);
		try {
			File modelFolder = _eomodeldFolderForFolder(parentFolder);
			if (!modelFolder.exists()) {
				if (!modelFolder.mkdirs()) {
					throw new IOException("Failed to create folder '" + modelFolder + "'.");
				}
			}
			myModelURL = modelFolder.toURL();
			File indexFile = new File(modelFolder, "index.eomodeld");
			EOModelMap modelMap = toMap();
			WOLPropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, indexFile, modelMap);
	
			if (myDeletedEntityNames != null) {
				for (String entityName : myDeletedEntityNames) {
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
	
			for (EOEntity entity : myEntities) {
				String entityName = entity.getName();
				File entityFile = new File(modelFolder, entityName + ".plist");
				entity.saveToFile(entityFile);
				File fspecFile = new File(modelFolder, entityName + ".fspec");
				entity.saveFetchSpecsToFile(fspecFile);
				entity.entitySaved();
			}
	
			if (myDeletedStoredProcedureNames != null) {
				for (String storedProcedureName : myDeletedStoredProcedureNames) {
					File storedProcedureFile = new File(modelFolder, storedProcedureName + ".storedProcedure");
					if (storedProcedureFile.exists()) {
						storedProcedureFile.delete();
					}
				}
			}
	
			for (EOStoredProcedure storedProcedure : myStoredProcedures) {
				String storedProcedureName = storedProcedure.getName();
				File storedProcedureFile = new File(modelFolder, storedProcedureName + ".storedProcedure");
				storedProcedure.saveToFile(storedProcedureFile);
			}
	
			setDirty(false);
	
			return modelFolder;
		}
		finally {
			firePropertyChange(EOModel.MODEL_SAVING, Boolean.TRUE, Boolean.FALSE);
		}
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		for (EOEntity entity : myEntities) {
			entity.resolve(_failures);
		}
	}

	public void resolveFlattened(Set<EOModelVerificationFailure> _failures) {
		for (EOEntity entity : myEntities) {
			entity.resolveFlattened(_failures);
		}
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		VerificationContext verificationContext;
		EOModelGroup modelGroup = getModelGroup();
		if (modelGroup == null) {
			verificationContext = new VerificationContext(this);
		} else {
			verificationContext = new VerificationContext(modelGroup);
		}
		verify(_failures, verificationContext);
	}

	public void verify(Set<EOModelVerificationFailure> _failures, VerificationContext verificationContext) {
		// TODO

		for (EOEntity entity : myEntities) {
			entity.verify(_failures, verificationContext);
		}
	}

	public String getFullyQualifiedName() {
		return myName;
	}

	@Override
	public EOModel _cloneModelObject() {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Class<EOModelGroup> _getModelParentType() {
		return EOModelGroup.class;
	}

	@Override
	public EOModelGroup _getModelParent() {
		return getModelGroup();
	}

	@Override
	public void _addToModelParent(EOModelGroup modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			throw new IllegalArgumentException("Unable to unique model names.");
		}
		modelParent.addModel(this, failures);
	}

	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getModelGroup().removeModel(this, failures);
	}

	public ModelEvents getModelEvents() {
		return _modelEvents;
	}

	public String toString() {
		return "[EOModel: name = " + myName + "; entities = " + myEntities + "]";
	}

	/** Begin Prototypes * */
	public synchronized void clearCachedPrototypes(Set<EOModelVerificationFailure> _failures, boolean _reload) {
		myPrototypeAttributeCache = null;
		for (EOEntity entity : myEntities) {
			entity.clearCachedPrototypes(_failures, _reload);
		}
	}

	public synchronized Set<String> getPrototypeAttributeNames() {
		Set<String> prototypeAttributeNames = new PropertyListSet<String>();
		for (EOAttribute attribute : getPrototypeAttributes()) {
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

	public synchronized Set<EOAttribute> getPrototypeAttributes() {
		if (myPrototypeAttributeCache == null) {
			Map<String, EOAttribute> prototypeAttributeCache = new HashMap<String, EOAttribute>();

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
			myPrototypeAttributeCache = new PropertyListSet<EOAttribute>();
			myPrototypeAttributeCache.addAll(prototypeAttributeCache.values());
		}
		return myPrototypeAttributeCache;
	}

	protected void addPrototypeAttributes(String _prototypeEntityName, Map<String, EOAttribute> _prototypeAttributeCache) {
		if (_prototypeEntityName != null) {
			EOEntity prototypeEntity = myModelGroup.getEntityNamed(_prototypeEntityName);
			if (prototypeEntity != null) {
				for (EOAttribute prototypeAttribute : prototypeEntity.getAttributes()) {
					_prototypeAttributeCache.put(prototypeAttribute.getName(), prototypeAttribute);
				}
			}
		}
	}

	public EOAttribute getPrototypeAttributeNamed(String _name) {
		EOAttribute matchingAttribute = null;
		Set<EOAttribute> prototypeAttributes = getPrototypeAttributes();
		Iterator<EOAttribute> attributesIter = prototypeAttributes.iterator();
		while (matchingAttribute == null && attributesIter.hasNext()) {
			EOAttribute attribute = attributesIter.next();
			if (attribute.getName().equals(_name)) {
				matchingAttribute = attribute;
			}
		}
		return matchingAttribute;
	}

	/** End Prototypes * */

	/*
	 * public static void main(String[] args) throws IOException,
	 * EOModelException { Set<EOModelVerificationFailure> failures = new
	 * LinkedHashSet<EOModelVerificationFailure>();
	 * 
	 * EOModelGroup modelGroup = new EOModelGroup(); NullProgressMonitor
	 * progressMonitor = new NullProgressMonitor();
	 * modelGroup.loadModelsFromFolder(new
	 * File("/Library/Frameworks/ERPrototypes.framework/Resources").toURL(),
	 * failures, progressMonitor); modelGroup.loadModelsFromFolder(new
	 * File("/Users/mschrag/Documents/workspace/MDTask").toURL(), failures,
	 * progressMonitor); modelGroup.loadModelsFromFolder(new
	 * File("/Users/mschrag/Documents/workspace/MDTAccounting").toURL(),
	 * failures, progressMonitor); modelGroup.loadModelsFromFolder(new
	 * File("/Users/mschrag/Documents/workspace/MDTCMS").toURL(), failures,
	 * progressMonitor); modelGroup.loadModelsFromFolder(new
	 * File("/Users/mschrag/Documents/workspace/MDTWOExtensions").toURL(),
	 * failures, progressMonitor);
	 * 
	 * modelGroup.resolve(failures); modelGroup.verify(failures); Iterator
	 * failuresIter = failures.iterator(); while (failuresIter.hasNext()) {
	 * EOModelVerificationFailure failure = (EOModelVerificationFailure)
	 * failuresIter.next(); System.out.println("EOModel.main: " + failure); }
	 * 
	 * File outputPath = new File("/tmp"); System.out.println("EOModel.main:
	 * Saving model to " + outputPath + " ..."); EOModel mdtaskModel =
	 * modelGroup.getModelNamed("MDTask"); mdtaskModel.saveToFolder(outputPath);
	 * System.out.println("EOModel.main: Done."); }
	 */
}
