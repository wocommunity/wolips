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
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.kvc.KeyPath;
import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.StringUtils;
import org.objectstyle.wolips.eomodeler.wocompat.PropertyListSerialization;

public class EOEntity extends UserInfoableEOModelObject implements IEOEntityRelative, ISortableEOModelObject {
	private static final String EONEXT_PRIMARY_KEY_PROCEDURE = "EONextPrimaryKeyProcedure";

	private static final String EOFETCH_WITH_PRIMARY_KEY_PROCEDURE = "EOFetchWithPrimaryKeyProcedure";

	private static final String EOFETCH_ALL_PROCEDURE = "EOFetchAllProcedure";

	private static final String EOINSERT_PROCEDURE = "EOInsertProcedure";

	private static final String EODELETE_PROCEDURE = "EODeleteProcedure";

	private static final String FETCH_ALL = "FetchAll";

	public static final String ATTRIBUTE = "attribute";

	public static final String RELATIONSHIP = "relationship";

	public static final String FETCH_SPECIFICATION = "fetchSpecification";

	public static final String NAME = "name";

	public static final String CLASS_NAME = "className";

	public static final String CLIENT_CLASS_NAME = "clientClassName";

	public static final String PARENT = "parent";

	public static final String EXTERNAL_QUERY = "externalQuery";

	public static final String MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH = "maxNumberOfInstancesToBatchFetch";

	public static final String READ_ONLY = "readOnly";

	public static final String EXTERNAL_NAME = "externalName";

	public static final String ABSTRACT_ENTITY = "abstractEntity";

	public static final String CACHES_OBJECTS = "cachesObjects";

	public static final String RESTRICTING_QUALIFIER = "restrictingQualifier";

	public static final String FETCH_SPECIFICATIONS = "fetchSpecifications";

	public static final String ATTRIBUTES = "attributes";

	public static final String RELATIONSHIPS = "relationships";

	public static final String DELETE_PROCEDURE = "deleteProcedure";

	public static final String FETCH_ALL_PROCEDURE = "fetchAllProcedure";

	public static final String FETCH_WITH_PRIMARY_KEY_PROCEDURE = "fetchWithPrimaryKeyProcedure";

	public static final String INSERT_PROCEDURE = "insertProcedure";

	public static final String NEXT_PRIMARY_KEY_PROCEDURE = "nextPrimaryKeyProcedure";

	private EOModel myModel;

	private EOEntity myParent;

	private String myName;

	private String myExternalName;

	private String myClassName;

	private String myClientClassName;

	private String myRestrictingQualifier;

	private String myExternalQuery;

	private Boolean myCachesObjects;

	private Boolean myAbstractEntity;

	private Boolean myReadOnly;

	private Integer myMaxNumberOfInstancesToBatchFetch;

	private Set myAttributes;

	private Set myRelationships;

	private Set myFetchSpecs;

	private EOModelMap myEntityMap;

	private EOModelMap myFetchSpecsMap;

	private EOStoredProcedure myDeleteProcedure;

	private EOStoredProcedure myFetchAllProcedure;

	private EOStoredProcedure myFetchWithPrimaryKeyProcedure;

	private EOStoredProcedure myInsertProcedure;

	private EOStoredProcedure myNextPrimaryKeyProcedure;

	public EOEntity() {
		myAttributes = new HashSet();
		myRelationships = new HashSet();
		myFetchSpecs = new HashSet();
		myEntityMap = new EOModelMap();
		myFetchSpecsMap = new EOModelMap();
	}

	public EOEntity(String _name) {
		this();
		myName = _name;
	}

	public AbstractEOAttributePath resolveKeyPath(String _keyPath) {
		AbstractEOAttributePath targetAttribute = resolveKeyPath(_keyPath, null, new HashSet());
		return targetAttribute;
	}

	public AbstractEOAttributePath resolveKeyPath(String _keyPath, EORelationshipPath _parentRelationshipPath, Set _visitedRelationships) {
		AbstractEOAttributePath targetAttributePath = null;
		if (_keyPath != null && _keyPath.length() > 0) {
			int dotIndex = _keyPath.indexOf('.');
			if (dotIndex == -1) {
				IEOAttribute attribute = getAttributeOrRelationshipNamed(_keyPath);
				if (attribute instanceof EOAttribute) {
					targetAttributePath = new EOAttributePath(_parentRelationshipPath, (EOAttribute) attribute);
				} else {
					targetAttributePath = new EORelationshipPath(_parentRelationshipPath, (EORelationship) attribute);
				}
			} else {
				EORelationship relationship = getRelationshipNamed(_keyPath.substring(0, dotIndex));
				if (relationship != null) {
					if (_visitedRelationships.contains(relationship)) {
						System.out.println("EOEntity.resolveKeyPath: you have an invalid flattened relationship '" + _keyPath + "' which creates a loop.");
						// throw new IllegalStateException("The definition '" +
						// _keyPath + "' results in a loop in " + getName() +
						// ".");
					} else {
						_visitedRelationships.add(relationship);
						EOEntity destination = relationship.getDestination();
						if (destination != null) {
							EORelationshipPath nextRelationshipPath = new EORelationshipPath(_parentRelationshipPath, relationship);
							targetAttributePath = destination.resolveKeyPath(_keyPath.substring(dotIndex + 1), nextRelationshipPath, _visitedRelationships);
						}
					}
				}
			}
		}
		return targetAttributePath;
	}

	public void pasted() throws DuplicateNameException {
		Iterator attributesIter = getAttributes().iterator();
		while (attributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributesIter.next();
			attribute.pasted();
		}

		Iterator relationshipsIter = getRelationships().iterator();
		while (relationshipsIter.hasNext()) {
			EORelationship relationship = (EORelationship) relationshipsIter.next();
			relationship.pasted();
		}
	}

	public String _findUnusedRelationshipName(String _name, boolean _toMany) {
		String name = StringUtils.toLowercaseFirstLetter(_name);
		if (_toMany) {
			name = StringUtils.toPlural(name);
		}
		name = findUnusedRelationshipName(name);
		return name;
	}

	public EOEntity joinInManyToManyWith(EOEntity _entity2) throws DuplicateNameException {
		String relationshipName = findUnusedRelationshipName(StringUtils.toPlural(StringUtils.toLowercaseFirstLetter(_entity2.getName())));
		String inverseRelationshipName = _entity2.findUnusedRelationshipName(StringUtils.toPlural(StringUtils.toLowercaseFirstLetter(getName())));
		String joinEntityName = getModel().findUnusedEntityName(getName() + _entity2.getName());
		return joinInManyToManyWith(_entity2, relationshipName, inverseRelationshipName, joinEntityName, true);
	}

	public EOEntity joinInManyToManyWith(EOEntity _entity2, String _relationshipName, String _inverseRelationshipName, String _joinEntityName, boolean _flatten) throws DuplicateNameException {
		EOEntity manyToManyEntity = new EOEntity(_joinEntityName);
		manyToManyEntity.setExternalName(manyToManyEntity.getName());
		Set joiningEntitiesSet = new HashSet();
		joiningEntitiesSet.add(this);
		joiningEntitiesSet.add(_entity2);
		String packageName = getModel().guessPackageName(joiningEntitiesSet);
		String className = manyToManyEntity.getName();
		if (packageName != null && packageName.length() > 0) {
			className = packageName + "." + className;
		}
		manyToManyEntity.setClassName(className);

		EORelationship entity1Relationship = manyToManyEntity.addBlankRelationship(StringUtils.toLowercaseFirstLetter(getName()));
		entity1Relationship.setToMany(Boolean.FALSE);
		entity1Relationship.setDestination(this);
		entity1Relationship.setClassProperty(Boolean.valueOf(!_flatten));
		entity1Relationship.setMandatory(Boolean.TRUE);
		Iterator entity1PrimaryKeyAttributesIter = getPrimaryKeyAttributes().iterator();
		if (!entity1PrimaryKeyAttributesIter.hasNext()) {
			throw new IllegalStateException("The entity " + getFullyQualifiedName() + " does not have any primary keys.");
		}
		while (entity1PrimaryKeyAttributesIter.hasNext()) {
			EOAttribute entity1PrimaryKeyAttribute = (EOAttribute) entity1PrimaryKeyAttributesIter.next();
			EOAttribute manyToManyPrimaryKeyAttribute = entity1PrimaryKeyAttribute.cloneAttribute();
			manyToManyPrimaryKeyAttribute.setName(manyToManyEntity.findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(getName()) + StringUtils.toUppercaseFirstLetter(manyToManyPrimaryKeyAttribute.getName())));
			manyToManyPrimaryKeyAttribute.setColumnName(manyToManyEntity.getName());
			EOJoin entity1Join = new EOJoin();
			entity1Join.setSourceAttribute(manyToManyPrimaryKeyAttribute);
			entity1Join.setDestinationAttribute(entity1PrimaryKeyAttribute);
			entity1Relationship.addJoin(entity1Join, false);
			manyToManyEntity.addAttribute(manyToManyPrimaryKeyAttribute);
		}
		manyToManyEntity.addRelationship(entity1Relationship);

		EORelationship entity2Relationship = manyToManyEntity.addBlankRelationship(StringUtils.toLowercaseFirstLetter(_entity2.getName()));
		entity2Relationship.setToMany(Boolean.FALSE);
		entity2Relationship.setDestination(_entity2);
		entity2Relationship.setClassProperty(Boolean.valueOf(!_flatten));
		entity2Relationship.setMandatory(Boolean.TRUE);
		Iterator entity2PrimaryKeyAttributesIter = _entity2.getPrimaryKeyAttributes().iterator();
		if (!entity2PrimaryKeyAttributesIter.hasNext()) {
			throw new IllegalStateException("The entity " + _entity2.getFullyQualifiedName() + " does not have any primary keys.");
		}
		while (entity2PrimaryKeyAttributesIter.hasNext()) {
			EOAttribute entity2PrimaryKeyAttribute = (EOAttribute) entity2PrimaryKeyAttributesIter.next();
			EOAttribute manyToManyPrimaryKeyAttribute = entity2PrimaryKeyAttribute.cloneAttribute();
			manyToManyPrimaryKeyAttribute.setName(manyToManyEntity.findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(_entity2.getName()) + StringUtils.toUppercaseFirstLetter(manyToManyPrimaryKeyAttribute.getName())));
			EOJoin entity2Join = new EOJoin();
			entity2Join.setSourceAttribute(manyToManyPrimaryKeyAttribute);
			entity2Join.setDestinationAttribute(entity2PrimaryKeyAttribute);
			entity2Relationship.addJoin(entity2Join, false);
			manyToManyEntity.addAttribute(manyToManyPrimaryKeyAttribute);
		}
		manyToManyEntity.addRelationship(entity2Relationship);

		String entity1ToManyName;
		if (_flatten) {
			entity1ToManyName = StringUtils.toPlural(StringUtils.toLowercaseFirstLetter(manyToManyEntity.getName()));
		} else {
			entity1ToManyName = _relationshipName;
		}
		EORelationship entity1ToManyRelationship = entity1Relationship.createInverseRelationshipNamed(entity1ToManyName, true);
		entity1ToManyRelationship.setClassProperty(Boolean.valueOf(!_flatten));
		entity1ToManyRelationship.setPropagatesPrimaryKey(Boolean.TRUE);
		entity1ToManyRelationship.setDeleteRule(EODeleteRule.CASCADE);
		addRelationship(entity1ToManyRelationship);

		if (_flatten) {
			EORelationship entity1ToManyFlattenedRelationship = new EORelationship(_relationshipName, new KeyPath(new String[] { entity1ToManyRelationship.getName(), entity2Relationship.getName() }).toKeyPath());
			entity1ToManyFlattenedRelationship.setClassProperty(Boolean.TRUE);
			addRelationship(entity1ToManyFlattenedRelationship);
		}

		String entity2ToManyName;
		if (_flatten) {
			entity2ToManyName = StringUtils.toPlural(StringUtils.toLowercaseFirstLetter(manyToManyEntity.getName()));
		} else {
			entity2ToManyName = _inverseRelationshipName;
		}
		EORelationship entity2ToManyRelationship = entity2Relationship.createInverseRelationshipNamed(entity2ToManyName, true);
		entity2ToManyRelationship.setClassProperty(Boolean.valueOf(!_flatten));
		entity2ToManyRelationship.setPropagatesPrimaryKey(Boolean.TRUE);
		entity2ToManyRelationship.setDeleteRule(EODeleteRule.CASCADE);
		_entity2.addRelationship(entity2ToManyRelationship);

		if (_flatten) {
			EORelationship entity2ToManyFlattenedRelationship = new EORelationship(_inverseRelationshipName, new KeyPath(new String[] { entity2ToManyRelationship.getName(), entity1Relationship.getName() }).toKeyPath());
			entity2ToManyFlattenedRelationship.setClassProperty(Boolean.TRUE);
			_entity2.addRelationship(entity2ToManyFlattenedRelationship);
		}

		getModel().addEntity(manyToManyEntity);

		return manyToManyEntity;
	}

	public EOAttribute getSinglePrimaryKeyAttribute() throws EOModelException {
		Set destinationPrimaryKeys = getPrimaryKeyAttributes();
		if (destinationPrimaryKeys.size() > 1) {
			throw new EOModelException(getName() + " has a compound primary key.");
		}
		EOAttribute primaryKey = (EOAttribute) destinationPrimaryKeys.iterator().next();
		return primaryKey;
	}

	public EOAttribute createForeignKeyTo(EOEntity foreignEntity, String foreignKeyName, String foreignKeyColumnName, boolean allowsNull) throws EOModelException {
		EOAttribute foreignPrimaryKey = foreignEntity.getSinglePrimaryKeyAttribute();
		EOAttribute foreignKeyAttribute = foreignPrimaryKey.cloneAttribute();
		foreignKeyAttribute.setName(foreignKeyName);
		foreignKeyAttribute.setColumnName(foreignKeyColumnName);
		foreignKeyAttribute.setAllowsNull(Boolean.valueOf(allowsNull));
		foreignKeyAttribute.setPrimaryKey(Boolean.FALSE);
		addAttribute(foreignKeyAttribute);
		return foreignKeyAttribute;
	}

	public EORelationship createRelationshipTo(EOEntity _destinationEntity, boolean _toMany) {
		return createRelationshipTo(_destinationEntity, _toMany, _findUnusedRelationshipName(_destinationEntity.getName(), _toMany));
	}

	public EORelationship createRelationshipTo(EOEntity _destinationEntity, boolean _toMany, String _name) {
		EORelationship relationship = new EORelationship(_name);
		relationship.setDestination(_destinationEntity, false);
		relationship.setClassProperty(Boolean.TRUE);
		relationship.setToMany(Boolean.valueOf(_toMany));
		Iterator destinationPrimaryKeyIter = _destinationEntity.getPrimaryKeyAttributes().iterator();
		while (destinationPrimaryKeyIter.hasNext()) {
			EOAttribute destinationPrimaryKeyAttribute = (EOAttribute) destinationPrimaryKeyIter.next();
			EOJoin inverseJoin = new EOJoin();
			inverseJoin.setDestinationAttribute(destinationPrimaryKeyAttribute, false);
			relationship.addJoin(inverseJoin, false);
		}
		relationship._setEntity(this);
		return relationship;
	}

	public EOEntity cloneEntity() throws DuplicateNameException {
		EOEntity entity = _cloneJustEntity();
		entity._cloneAttributesAndRelationshipsFrom(this, false, null, false);
		entity._cloneFetchSpecificationsFrom(this, false);
		entity.setUserInfo(new HashMap(getUserInfo()));
		return entity;
	}

	protected EOEntity _cloneJustEntity() {
		EOEntity entity = new EOEntity(myName);
		entity.myParent = myParent;
		entity.myExternalName = myExternalName;
		entity.myClassName = myClassName;
		entity.myClientClassName = myClientClassName;
		entity.myRestrictingQualifier = myRestrictingQualifier;
		entity.myExternalQuery = myExternalQuery;
		entity.myCachesObjects = myCachesObjects;
		entity.myAbstractEntity = myAbstractEntity;
		entity.myReadOnly = myReadOnly;
		entity.myMaxNumberOfInstancesToBatchFetch = myMaxNumberOfInstancesToBatchFetch;
		return entity;
	}

	public EOEntity subclass(String _subclassName, InheritanceType _inheritanceType) throws DuplicateNameException {
		EOEntity subclassEntity;
		if (_inheritanceType == InheritanceType.HORIZONTAL) {
			subclassEntity = _horizontalSubclass(_subclassName);
		} else if (_inheritanceType == InheritanceType.SINGLE_TABLE) {
			subclassEntity = _singleTableSubclass(_subclassName);
		} else if (_inheritanceType == InheritanceType.VERTICAL) {
			subclassEntity = _verticalSubclass(_subclassName);
		} else {
			throw new IllegalArgumentException("Unknown inheritance type " + _inheritanceType + ".");
		}
		return subclassEntity;
	}

	protected String _toSubclassName(String _subclassName) {
		String className = getClassName();
		if (className != null) {
			int lastDotIndex = className.lastIndexOf('.');
			className = className.substring(0, lastDotIndex + 1) + _subclassName;
		}
		return className;
	}

	public EOEntity _horizontalSubclass(String _subclassName) throws DuplicateNameException {
		EOEntity subclassEntity = _cloneJustEntity();
		subclassEntity.setName(_subclassName, false);
		subclassEntity.myClassName = _toSubclassName(_subclassName);
		subclassEntity.myParent = this;
		subclassEntity.myExternalName = _subclassName;
		if (subclassEntity.isAbstractEntity() != null) {
			subclassEntity.myAbstractEntity = Boolean.FALSE;
		}
		subclassEntity._cloneAttributesAndRelationshipsFrom(this, false, null, false);
		return subclassEntity;
	}

	public EOEntity _singleTableSubclass(String _subclassName) throws DuplicateNameException {
		EOEntity subclassEntity = _cloneJustEntity();
		subclassEntity.setName(_subclassName, false);
		subclassEntity.myClassName = _toSubclassName(_subclassName);
		subclassEntity.myParent = this;
		if (subclassEntity.isAbstractEntity() != null) {
			subclassEntity.myAbstractEntity = Boolean.FALSE;
		}
		subclassEntity._cloneAttributesAndRelationshipsFrom(this, false, null, false);
		return subclassEntity;
	}

	public EOEntity _verticalSubclass(String _subclassName) throws DuplicateNameException {
		EOEntity subclassEntity = _cloneJustEntity();
		subclassEntity.setName(_subclassName, false);
		subclassEntity.myClassName = _toSubclassName(_subclassName);
		subclassEntity.myParent = this;
		subclassEntity.myExternalName = _subclassName;
		if (subclassEntity.isAbstractEntity() != null) {
			subclassEntity.myAbstractEntity = Boolean.FALSE;
		}

		EORelationship superclassRelationship = subclassEntity.addBlankRelationship(subclassEntity.findUnusedRelationshipName(StringUtils.toLowercaseFirstLetter(getName())));
		superclassRelationship.setToMany(Boolean.FALSE);
		superclassRelationship.setDestination(this);
		superclassRelationship.setClassProperty(Boolean.FALSE, false);
		superclassRelationship.setMandatory(Boolean.TRUE);
		Set primaryKeyAttributes = getPrimaryKeyAttributes();
		Iterator primaryKeyAttributesIter = primaryKeyAttributes.iterator();
		while (primaryKeyAttributesIter.hasNext()) {
			EOAttribute superclassPrimaryKeyAttribute = (EOAttribute) primaryKeyAttributesIter.next();
			EOAttribute subclassPrimaryKeyAttribute = superclassPrimaryKeyAttribute.cloneAttribute();
			EOJoin superclassJoin = new EOJoin();
			superclassJoin.setSourceAttribute(subclassPrimaryKeyAttribute);
			superclassJoin.setDestinationAttribute(superclassPrimaryKeyAttribute);
			superclassRelationship.addJoin(superclassJoin, false);
			subclassEntity.addAttribute(subclassPrimaryKeyAttribute);
		}

		Iterator attributesIter = getAttributes().iterator();
		while (attributesIter.hasNext()) {
			EOAttribute inheritedAttribute = (EOAttribute) attributesIter.next();
			if (!primaryKeyAttributes.contains(inheritedAttribute) && BooleanUtils.isTrue(inheritedAttribute.isClassProperty())) {
				subclassEntity.addBlankAttribute(new EOAttributePath(new EORelationshipPath(null, superclassRelationship), inheritedAttribute));
			}
		}

		Iterator relationshipsIter = getRelationships().iterator();
		while (relationshipsIter.hasNext()) {
			EORelationship inheritedRelationship = (EORelationship) relationshipsIter.next();
			if (BooleanUtils.isTrue(inheritedRelationship.isClassProperty())) {
				subclassEntity.addBlankRelationship(new EORelationshipPath(new EORelationshipPath(null, superclassRelationship), inheritedRelationship));
			}
		}

		return subclassEntity;
	}

	protected void _cloneFetchSpecificationsFrom(EOEntity _entity, boolean _skipExistingNames) throws DuplicateNameException {
		Iterator fetchSpecsIter = _entity.getFetchSpecs().iterator();
		while (fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
			if (!_skipExistingNames || getFetchSpecNamed(fetchSpec.getName()) == null) {
				EOFetchSpecification clonedFetchSpec = fetchSpec.cloneFetchSpecification();
				clonedFetchSpec.setName(findUnusedFetchSpecificationName(clonedFetchSpec.getName()));
				addFetchSpecification(clonedFetchSpec);
			}
		}
	}

	// MS: replace with _cloneAttributesAndRelationships(Set) ?
	protected void _cloneAttributesAndRelationshipsFrom(EOEntity _entity, boolean _skipExistingNames, Set failures, boolean warnOnly) throws DuplicateNameException {
		Iterator attributesIter = _entity.getAttributes().iterator();
		while (attributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributesIter.next();
			if (!_skipExistingNames || getAttributeNamed(attribute.getName()) == null) {
				if (failures != null) {
					failures.add(new EOModelVerificationFailure(getModel(), getFullyQualifiedName() + " was missing the inherited attribute " + attribute.getName() + ".", true));
				}
				if (!warnOnly) {
					EOAttribute clonedAttribute = attribute.cloneAttribute();
					clonedAttribute.setName(findUnusedAttributeName(clonedAttribute.getName()));
					addAttribute(clonedAttribute);
				}
			}
		}

		Iterator relationshipsIter = _entity.getRelationships().iterator();
		while (relationshipsIter.hasNext()) {
			EORelationship relationship = (EORelationship) relationshipsIter.next();
			if (!_skipExistingNames || getRelationshipNamed(relationship.getName()) == null) {
				if (failures != null) {
					failures.add(new EOModelVerificationFailure(getModel(), getFullyQualifiedName() + " was missing the inherited relationship " + relationship.getName() + ".", true));
				}
				if (!warnOnly) {
					EORelationship clonedRelationship = relationship.cloneRelationship();
					clonedRelationship.setName(findUnusedRelationshipName(clonedRelationship.getName()));
					addRelationship(clonedRelationship, false, null, true);
				}
			}
		}
	}

	protected void _cloneAttributesAndRelationships(Set attributesAndRelationships) throws DuplicateNameException {
		Iterator attributesAndRelationshipsIter = attributesAndRelationships.iterator();
		while (attributesAndRelationshipsIter.hasNext()) {
			Object attributeOrRelationship = attributesAndRelationshipsIter.next();
			if (attributeOrRelationship instanceof EOAttribute) {
				EOAttribute attribute = (EOAttribute)attributeOrRelationship;
				EOAttribute clonedAttribute = attribute.cloneAttribute();
				clonedAttribute.setName(findUnusedAttributeName(clonedAttribute.getName()));
				addAttribute(clonedAttribute);
			}
			else {
				EORelationship relationship = (EORelationship)attributeOrRelationship;
				EORelationship clonedRelationship = relationship.cloneRelationship();
				clonedRelationship.setName(findUnusedRelationshipName(clonedRelationship.getName()));
				addRelationship(clonedRelationship, false, null, true);
			}
		}
	}
	
	protected Set _findMissingInheritedAttributesAndRelationships() {
		Set missingInheritedAttributesAndRelationships = new HashSet();
		EOEntity parentEntity = getParent();
		if (parentEntity != null) {
			Iterator attributesIter = parentEntity.getAttributes().iterator();
			while (attributesIter.hasNext()) {
				EOAttribute attribute = (EOAttribute) attributesIter.next();
				if (getAttributeNamed(attribute.getName()) == null) {
					missingInheritedAttributesAndRelationships.add(attribute);
				}
			}

			Iterator relationshipsIter = parentEntity.getRelationships().iterator();
			while (relationshipsIter.hasNext()) {
				EORelationship relationship = (EORelationship) relationshipsIter.next();
				if (getRelationshipNamed(relationship.getName()) == null) {
					missingInheritedAttributesAndRelationships.add(relationship);
				}
			}
		}
		return missingInheritedAttributesAndRelationships;
	}

	public EOEntity getEntity() {
		return this;
	}

	public IEOAttribute addBlankIEOAttribute(AbstractEOAttributePath _flattenAttribute) throws DuplicateNameException {
		if (_flattenAttribute instanceof EORelationshipPath) {
			return addBlankRelationship((EORelationshipPath) _flattenAttribute);
		} else if (_flattenAttribute instanceof EOAttributePath) {
			return addBlankAttribute((EOAttributePath) _flattenAttribute);
		} else {
			throw new IllegalArgumentException("Unknown attribute path: " + _flattenAttribute);
		}
	}

	public EORelationship addBlankRelationship(String _name) throws DuplicateNameException {
		return addBlankRelationship(_name, null);
	}

	public EORelationship addBlankRelationship(EORelationshipPath _flattenRelationship) throws DuplicateNameException {
		return addBlankRelationship(_flattenRelationship.toKeyPath().replace('.', '_'), _flattenRelationship);
	}

	public EORelationship addBlankRelationship(String _name, EORelationshipPath _flattenRelationship) throws DuplicateNameException {
		String newRelationshipName = findUnusedRelationshipName(_name);
		EORelationship relationship;
		if (_flattenRelationship != null) {
			relationship = new EORelationship(newRelationshipName, _flattenRelationship.toKeyPath());
		} else {
			relationship = new EORelationship(newRelationshipName);
		}
		relationship.setClassProperty(Boolean.TRUE);
		addRelationship(relationship);
		return relationship;
	}

	public EOAttribute addBlankAttribute(String _name) throws DuplicateNameException {
		return addBlankAttribute(_name, null);
	}

	public EOAttribute addBlankAttribute(EOAttributePath _flattenAttribute) throws DuplicateNameException {
		return addBlankAttribute(_flattenAttribute.toKeyPath().replace('.', '_'), _flattenAttribute);
	}

	public EOAttribute addBlankAttribute(String _name, EOAttributePath _flattenAttribute) throws DuplicateNameException {
		String newAttributeNameBase = _name;
		String newAttributeName = newAttributeNameBase;
		int newAttributeNum = 0;
		while (getAttributeNamed(newAttributeName) != null) {
			newAttributeNum++;
			newAttributeName = newAttributeNameBase + newAttributeNum;
		}
		EOAttribute attribute;
		if (_flattenAttribute != null) {
			attribute = new EOAttribute(newAttributeName, _flattenAttribute.toKeyPath());
		} else {
			attribute = new EOAttribute(newAttributeName);
			attribute.setUsedForLocking(Boolean.TRUE);
		}
		attribute.setClassProperty(Boolean.TRUE);
		attribute.setColumnName(newAttributeName);
		addAttribute(attribute);
		return attribute;
	}

	public EOFetchSpecification addBlankFetchSpec(String _name) throws DuplicateFetchSpecNameException {
		String newFetchSpecNameBase = _name;
		String newFetchSpecName = newFetchSpecNameBase;
		int newFetchSpecNum = 0;
		while (getFetchSpecNamed(newFetchSpecName) != null) {
			newFetchSpecNum++;
			newFetchSpecName = newFetchSpecNameBase + newFetchSpecNum;
		}
		EOFetchSpecification fetchSpec = new EOFetchSpecification(newFetchSpecName);
		addFetchSpecification(fetchSpec);
		return fetchSpec;
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (myModel != null) {
			myModel._entityChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	public boolean hasSharedObjects() {
		boolean hasSharedObjects = false;
		Iterator fetchSpecsIter = myFetchSpecs.iterator();
		while (!hasSharedObjects && fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
			hasSharedObjects = BooleanUtils.isTrue(fetchSpec.isSharesObjects());
		}
		return hasSharedObjects;
	}

	public void shareNoObjects() {
		Iterator fetchSpecsIter = myFetchSpecs.iterator();
		while (fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
			fetchSpec.setSharesObjects(Boolean.FALSE);
		}
	}

	public boolean isSharesAllObjectsOnly() {
		boolean sharesAllObjects = false;
		int sharedFetchSpecCount = 0;
		Iterator fetchSpecsIter = myFetchSpecs.iterator();
		while (fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
			if (BooleanUtils.isTrue(fetchSpec.isSharesObjects())) {
				sharedFetchSpecCount++;
				if (EOEntity.FETCH_ALL.equals(fetchSpec.getName())) {
					sharesAllObjects = true;
				}
			}
		}
		return sharesAllObjects && sharedFetchSpecCount == 1;
	}

	public void shareAllObjects() throws DuplicateFetchSpecNameException {
		EOFetchSpecification fetchAllFetchSpec = getFetchSpecNamed(EOEntity.FETCH_ALL);
		if (fetchAllFetchSpec != null) {
			fetchAllFetchSpec.setSharesObjects(Boolean.TRUE);
		} else {
			fetchAllFetchSpec = new EOFetchSpecification(EOEntity.FETCH_ALL);
			fetchAllFetchSpec.setSharesObjects(Boolean.TRUE, false);
			addFetchSpecification(fetchAllFetchSpec);
		}
	}

	public Object getAdapter(Class _adapter) {
		Object adapter = null;
		// if (_adapter == IPropertySource.class) {
		// adapter = null;
		// }
		return adapter;
	}

	public void _setModel(EOModel _model) {
		myModel = _model;
	}

	public EOModel getModel() {
		return myModel;
	}

	public boolean isPrototype() {
		// MS: Normally it would be .endsWith("Prototypes"), but if there are
		// duplicate names, then
		// the entities get renamed to be EOXxxPrototypes1
		return myName != null && myName.startsWith("EO") && myName.indexOf("Prototypes") != -1;
	}

	public String getExternalQuery() {
		return myExternalQuery;
	}

	public void setExternalQuery(String _externalQuery) {
		String oldExternalQuery = myExternalQuery;
		myExternalQuery = _externalQuery;
		firePropertyChange(EOEntity.EXTERNAL_QUERY, oldExternalQuery, myExternalQuery);
	}

	public Integer getMaxNumberOfInstancesToBatchFetch() {
		return myMaxNumberOfInstancesToBatchFetch;
	}

	public void setMaxNumberOfInstancesToBatchFetch(Integer _maxNumberOfInstancesToBatchFetch) {
		Integer oldMaxNumberOfInstancesToBatchFetch = myMaxNumberOfInstancesToBatchFetch;
		myMaxNumberOfInstancesToBatchFetch = _maxNumberOfInstancesToBatchFetch;
		firePropertyChange(EOEntity.MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH, oldMaxNumberOfInstancesToBatchFetch, myMaxNumberOfInstancesToBatchFetch);
	}

	public Boolean getReadOnly() {
		return isReadOnly();
	}

	public Boolean isReadOnly() {
		return myReadOnly;
	}

	public void setReadOnly(Boolean _readOnly) {
		Boolean oldReadOnly = myReadOnly;
		myReadOnly = _readOnly;
		firePropertyChange(EOEntity.READ_ONLY, oldReadOnly, myReadOnly);
	}

	public String getName() {
		return myName;
	}

	public void setName(String _name) throws DuplicateEntityNameException {
		setName(_name, true);
	}

	public void setName(String _name, boolean _fireEvents) throws DuplicateEntityNameException {
		if (_name == null) {
			throw new NullPointerException(Messages.getString("EOEntity.noBlankEntityNames"));
		}
		String oldName = myName;
		if (myModel != null) {
			myModel._checkForDuplicateEntityName(this, _name, null);
			myModel._entityNameChanged(oldName, _name);
		}
		myName = _name;
		if (_fireEvents) {
			firePropertyChange(EOEntity.NAME, oldName, myName);
		}
	}

	public String getClassName() {
		return myClassName;
	}

	public void setClassName(String _className) {
		String oldClassName = myClassName;
		myClassName = _className;
		firePropertyChange(EOEntity.CLASS_NAME, oldClassName, myClassName);
	}

	public String getClientClassName() {
		return myClientClassName;
	}

	public void setClientClassName(String _clientClassName) {
		String oldClientClassName = myClientClassName;
		myClientClassName = _clientClassName;
		firePropertyChange(EOEntity.CLIENT_CLASS_NAME, oldClientClassName, myClientClassName);
	}

	public String getExternalName() {
		return myExternalName;
	}

	public void setExternalName(String _externalName) {
		String oldExternalName = myExternalName;
		myExternalName = _externalName;
		firePropertyChange(EOEntity.EXTERNAL_NAME, oldExternalName, myExternalName);
	}

	public int hashCode() {
		return (myName == null) ? super.hashCode() : myName.hashCode();
	}

	public boolean equals(Object _obj) {
		return (_obj instanceof EOEntity && ((_obj == this) || ComparisonUtils.equals(myName, ((EOEntity) _obj).myName)));
	}

	public Set getReferenceFailures() {
		Set referenceFailures = new HashSet();
		Iterator referencingEntitiesIter = getChildrenEntities().iterator();
		while (referencingEntitiesIter.hasNext()) {
			EOEntity referencingEntity = (EOEntity) referencingEntitiesIter.next();
			referenceFailures.add(new EOEntityParentReferenceFailure(this, referencingEntity));
		}

		Iterator referencingRelationshipsIter = getReferencingRelationships().iterator();
		while (referencingRelationshipsIter.hasNext()) {
			EORelationship referencingRelationship = (EORelationship) referencingRelationshipsIter.next();
			referenceFailures.add(new EOEntityRelationshipReferenceFailure(this, referencingRelationship));
		}
		return referenceFailures;
	}

	public Set getReferencingRelationships() {
		Set referencingRelationships = new HashSet();
		Iterator modelsIter = getModel().getModelGroup().getModels().iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			Iterator entitiesIter = model.getEntities().iterator();
			while (entitiesIter.hasNext()) {
				EOEntity entity = (EOEntity) entitiesIter.next();
				if (!entity.equals(this)) {
					Iterator relationshipsIter = entity.getRelationships().iterator();
					while (relationshipsIter.hasNext()) {
						EORelationship relationship = (EORelationship) relationshipsIter.next();
						if (relationship.isRelatedTo(this)) {
							referencingRelationships.add(relationship);
						}
					}
				}
			}
		}
		return referencingRelationships;
	}

	public Set getChildrenEntities() {
		Set children = new HashSet();
		if (myModel != null) {
			Iterator modelsIter = myModel.getModelGroup().getModels().iterator();
			while (modelsIter.hasNext()) {
				EOModel model = (EOModel) modelsIter.next();
				Iterator entitiesIter = model.getEntities().iterator();
				while (entitiesIter.hasNext()) {
					EOEntity entity = (EOEntity) entitiesIter.next();
					if (entity.getParent() == this) {
						children.add(entity);
					}
				}
			}
		}
		return children;
	}

	public EOEntity getParent() {
		return myParent;
	}

	public void setParent(EOEntity _parent) {
		EOEntity oldParent = myParent;
		myParent = _parent;
		firePropertyChange(EOEntity.PARENT, oldParent, myParent);
	}

	public void inheritParentAttributesAndRelationships(Set failures, boolean warnOnly) throws DuplicateNameException {
		EOEntity parent = getParent();
		if (parent != null) {
			if (parent.getModel() == getModel()) {
				parent.inheritParentAttributesAndRelationships(failures, warnOnly);
			}
			_cloneAttributesAndRelationshipsFrom(parent, true, failures, warnOnly);
		}
	}

	public Boolean getAbstractEntity() {
		return isAbstractEntity();
	}

	public Boolean isAbstractEntity() {
		return myAbstractEntity;
	}

	public boolean isInherited() {
		return getParent() != null;
	}

	public boolean isSingleTableInheritance() {
		return isInherited() && ComparisonUtils.equals(getExternalName(), getParent().getExternalName());
	}

	public boolean isVerticalInheritance() {
		boolean verticalInheritance = false;
		if (isInherited() && !isSingleTableInheritance()) {
			EOEntity parent = getParent();
			Iterator relationshipsIter = getRelationships().iterator();
			while (!verticalInheritance && relationshipsIter.hasNext()) {
				EORelationship relationship = (EORelationship) relationshipsIter.next();
				verticalInheritance = ComparisonUtils.equals(relationship.getDestination(), parent) && !relationship.getClassProperty().booleanValue();
			}
		}
		return verticalInheritance;
	}

	public boolean isHorizontalInheritance() {
		return isInherited() && !isSingleTableInheritance() && !isVerticalInheritance();
	}

	public void setAbstractEntity(Boolean _abstractEntity) {
		Boolean oldAbstractEntity = myAbstractEntity;
		myAbstractEntity = _abstractEntity;
		firePropertyChange(EOEntity.ABSTRACT_ENTITY, oldAbstractEntity, myAbstractEntity);
	}

	public Boolean getCachesObjects() {
		return isCachesObjects();
	}

	public Boolean isCachesObjects() {
		return myCachesObjects;
	}

	public void setCachesObjects(Boolean _cachesObjects) {
		Boolean oldCachesObjects = myCachesObjects;
		myCachesObjects = _cachesObjects;
		firePropertyChange(EOEntity.CACHES_OBJECTS, oldCachesObjects, myCachesObjects);
	}

	public String getRestrictingQualifier() {
		return myRestrictingQualifier;
	}

	public void setRestrictingQualifier(String _restrictingQualifier) {
		String oldRestrictingQualifier = myRestrictingQualifier;
		myRestrictingQualifier = _restrictingQualifier;
		firePropertyChange(EOEntity.RESTRICTING_QUALIFIER, oldRestrictingQualifier, myRestrictingQualifier);
	}

	public void clearCachedPrototypes(Set _failures, boolean _reload) {
		Iterator attributesIter = myAttributes.iterator();
		while (attributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributesIter.next();
			attribute.clearCachedPrototype(_failures, _reload);
		}
	}

	public Set getPrimaryKeyAttributes() {
		Set primaryKeyAttributes = new HashSet();
		Iterator attributesIter = myAttributes.iterator();
		while (attributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributesIter.next();
			Boolean primaryKey = attribute.isPrimaryKey();
			if (BooleanUtils.isTrue(primaryKey)) {
				primaryKeyAttributes.add(attribute);
			}
		}
		return primaryKeyAttributes;
	}

	public void setAttributes(Set _attributes) {
		myAttributes = _attributes;
		firePropertyChange(EOEntity.ATTRIBUTES, null, null);
	}

	public Set getAttributes() {
		return myAttributes;
	}

	public String[] getAttributeNames() {
		Set attributes = getAttributes();
		String[] attributeNames = new String[attributes.size()];
		Iterator attributeIter = attributes.iterator();
		for (int attributeNum = 0; attributeIter.hasNext(); attributeNum++) {
			EOAttribute attribute = (EOAttribute) attributeIter.next();
			attributeNames[attributeNum] = attribute.getName();
		}
		Arrays.sort(attributeNames);
		return attributeNames;
	}

	public Set getRelationships() {
		return myRelationships;
	}

	public Set getFetchSpecs() {
		return myFetchSpecs;
	}

	public String findUnusedAttributeName(String _newName) {
		boolean unusedNameFound = (getAttributeOrRelationshipNamed(_newName) == null);
		String unusedName = _newName;
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			IEOAttribute renameAttribute = getAttributeOrRelationshipNamed(unusedName);
			unusedNameFound = (renameAttribute == null);
		}
		return unusedName;
	}

	public IEOAttribute getAttributeOrRelationshipNamed(String _name) {
		IEOAttribute attribute = getAttributeNamed(_name);
		if (attribute == null) {
			attribute = getRelationshipNamed(_name);
		}
		return attribute;
	}

	public void _checkForDuplicateAttributeName(EOAttribute _attribute, String _newName, Set _failures) throws DuplicateNameException {
		IEOAttribute existingAttribute = getAttributeOrRelationshipNamed(_newName);
		if (existingAttribute != null && existingAttribute != _attribute) {
			if (_failures == null) {
				throw new DuplicateAttributeNameException(_newName, this);
			}

			String unusedName = findUnusedAttributeName(_newName);
			existingAttribute.setName(unusedName, true);
			_failures.add(new DuplicateAttributeFailure(this, _newName, unusedName));
		}
	}

	public EOFetchSpecification getFetchSpecNamed(String _name) {
		EOFetchSpecification matchingFetchSpec = null;
		Iterator fetchSpecsIter = myFetchSpecs.iterator();
		while (matchingFetchSpec == null && fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
			if (ComparisonUtils.equals(fetchSpec.getName(), _name)) {
				matchingFetchSpec = fetchSpec;
			}
		}
		return matchingFetchSpec;
	}

	public String findUnusedFetchSpecificationName(String _newName) {
		boolean unusedNameFound = (getFetchSpecNamed(_newName) == null);
		String unusedName = _newName;
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			EOFetchSpecification renameFetchSpec = getFetchSpecNamed(unusedName);
			unusedNameFound = (renameFetchSpec == null);
		}
		return unusedName;
	}

	public void _checkForDuplicateFetchSpecName(EOFetchSpecification _fetchSpec, String _newName, Set _failures) throws DuplicateFetchSpecNameException {
		EOFetchSpecification existingFetchSpec = getFetchSpecNamed(_newName);
		if (existingFetchSpec != null && existingFetchSpec != _fetchSpec) {
			if (_failures == null) {
				throw new DuplicateFetchSpecNameException(_newName, this);
			}

			String unusedName = findUnusedFetchSpecificationName(_newName);
			existingFetchSpec.setName(unusedName, true);
			_failures.add(new DuplicateFetchSpecFailure(this, _newName, unusedName));
		}
	}

	public void addFetchSpecification(EOFetchSpecification _fetchSpecification) throws DuplicateFetchSpecNameException {
		addFetchSpecification(_fetchSpecification, true, null);
	}

	public void addFetchSpecification(EOFetchSpecification _fetchSpecification, boolean _fireEvents, Set _failures) throws DuplicateFetchSpecNameException {
		_fetchSpecification._setEntity(this);
		_checkForDuplicateFetchSpecName(_fetchSpecification, _fetchSpecification.getName(), _failures);
		Set oldFetchSpecs = null;
		if (_fireEvents) {
			oldFetchSpecs = myFetchSpecs;
			Set newFetchSpecs = new HashSet();
			newFetchSpecs.addAll(myFetchSpecs);
			newFetchSpecs.add(_fetchSpecification);
			myFetchSpecs = newFetchSpecs;
			firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, oldFetchSpecs, myFetchSpecs);
		} else {
			myFetchSpecs.add(_fetchSpecification);
		}
	}

	public void removeFetchSpecification(EOFetchSpecification _fetchSpecification) {
		Set oldFetchSpecs = myFetchSpecs;
		Set newFetchSpecs = new HashSet();
		newFetchSpecs.addAll(myFetchSpecs);
		newFetchSpecs.remove(_fetchSpecification);
		myFetchSpecs = newFetchSpecs;
		firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, oldFetchSpecs, newFetchSpecs);
	}

	public void addAttribute(EOAttribute _attribute) throws DuplicateNameException {
		addAttribute(_attribute, true, null);
	}

	public synchronized void addAttribute(EOAttribute _attribute, boolean _fireEvents, Set _failures) throws DuplicateNameException {
		_attribute._setEntity(this);
		_checkForDuplicateAttributeName(_attribute, _attribute.getName(), _failures);
		_attribute.pasted();
		Set oldAttributes = null;
		if (_fireEvents) {
			oldAttributes = myAttributes;
			Set newAttributes = new HashSet();
			newAttributes.addAll(myAttributes);
			newAttributes.add(_attribute);
			myAttributes = newAttributes;
			firePropertyChange(EOEntity.ATTRIBUTES, oldAttributes, myAttributes);
		} else {
			myAttributes.add(_attribute);
		}
	}

	public void removeAttribute(EOAttribute _attribute, boolean _removeFromSubclasses) {
		String attributeName = _attribute.getName();
		Set oldAttributes = myAttributes;
		Set newAttributes = new HashSet();
		newAttributes.addAll(myAttributes);
		newAttributes.remove(_attribute);
		myAttributes = newAttributes;
		firePropertyChange(EOEntity.ATTRIBUTES, oldAttributes, newAttributes);
		if (_removeFromSubclasses) {
			Iterator childrenEntitiesIter = getChildrenEntities().iterator();
			while (childrenEntitiesIter.hasNext()) {
				EOEntity childEntity = (EOEntity) childrenEntitiesIter.next();
				EOAttribute childAttribute = childEntity.getAttributeNamed(attributeName);
				if (childAttribute != null) {
					childEntity.removeAttribute(childAttribute, _removeFromSubclasses);
				}
			}
		}
		_attribute._setEntity(null);
	}

	public EOAttribute getAttributeNamed(String _name) {
		EOAttribute matchingAttribute = null;
		Iterator attributesIter = myAttributes.iterator();
		while (matchingAttribute == null && attributesIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributesIter.next();
			if (ComparisonUtils.equals(attribute.getName(), _name)) {
				matchingAttribute = attribute;
			}
		}
		return matchingAttribute;
	}

	public String findUnusedRelationshipName(String _newName) {
		boolean unusedNameFound = (getAttributeOrRelationshipNamed(_newName) == null);
		String unusedName = _newName;
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			IEOAttribute renameRelationship = getAttributeOrRelationshipNamed(unusedName);
			unusedNameFound = (renameRelationship == null);
		}
		return unusedName;
	}

	public void _checkForDuplicateRelationshipName(EORelationship _relationship, String _newName, Set _failures) throws DuplicateNameException {
		IEOAttribute existingRelationship = getAttributeOrRelationshipNamed(_newName);
		if (existingRelationship != null && existingRelationship != _relationship) {
			if (_failures == null) {
				throw new DuplicateRelationshipNameException(_newName, this);
			}

			String unusedName = findUnusedRelationshipName(_newName);
			existingRelationship.setName(unusedName, true);
			_failures.add(new DuplicateRelationshipFailure(this, _newName, unusedName));
		}
	}

	protected void _attributeChanged(EOAttribute _attribute, String _propertyName, Object _oldValue, Object _newValue) {
		myAttributes = new HashSet(myAttributes);
		firePropertyChange(EOEntity.ATTRIBUTE, null, _attribute);
	}

	protected void _relationshipChanged(EORelationship _relationship, String _propertyName, Object _oldValue, Object _newValue) {
		myRelationships = new HashSet(myRelationships);
		firePropertyChange(EOEntity.RELATIONSHIP, null, _relationship);
	}

	protected void _fetchSpecificationChanged(EOFetchSpecification _fetchSpecification, String _propertyName, Object _oldValue, Object _newValue) {
		myFetchSpecs = new HashSet(myFetchSpecs);
		firePropertyChange(EOEntity.FETCH_SPECIFICATION, null, _fetchSpecification);
	}

	public void addRelationship(EORelationship relationship) throws DuplicateNameException {
		addRelationship(relationship, true, null, true);
	}

	public void addRelationship(EORelationship relationship, boolean pasteImmediately, Set failures, boolean fireEvents) throws DuplicateNameException {
		relationship._setEntity(this);
		_checkForDuplicateRelationshipName(relationship, relationship.getName(), failures);
		if (pasteImmediately) {
			relationship.pasted();
		}
		Set oldRelationships = null;
		if (fireEvents) {
			oldRelationships = myRelationships;
			Set newRelationships = new HashSet();
			newRelationships.addAll(myRelationships);
			newRelationships.add(relationship);
			myRelationships = newRelationships;
			firePropertyChange(EOEntity.RELATIONSHIPS, oldRelationships, myRelationships);
		} else {
			myRelationships.add(relationship);
		}
	}

	public void removeRelationship(EORelationship _relationship, boolean _removeFromSubclasses) {
		String relationshipName = _relationship.getName();
		Set oldRelationships = myRelationships;
		Set newRelationships = new HashSet();
		newRelationships.addAll(myRelationships);
		newRelationships.remove(_relationship);
		myRelationships = newRelationships;
		firePropertyChange(EOEntity.RELATIONSHIPS, oldRelationships, newRelationships);
		if (_removeFromSubclasses) {
			Iterator childrenEntitiesIter = getChildrenEntities().iterator();
			while (childrenEntitiesIter.hasNext()) {
				EOEntity childEntity = (EOEntity) childrenEntitiesIter.next();
				EORelationship childRelationship = childEntity.getRelationshipNamed(relationshipName);
				if (childRelationship != null) {
					childEntity.removeRelationship(childRelationship, _removeFromSubclasses);
				}
			}
		}
		_relationship._setEntity(null);
	}

	public EORelationship getRelationshipNamed(String _name) {
		EORelationship matchingRelationship = null;
		Iterator relationshipsIter = myRelationships.iterator();
		while (matchingRelationship == null && relationshipsIter.hasNext()) {
			EORelationship relationship = (EORelationship) relationshipsIter.next();
			if (ComparisonUtils.equals(relationship.getName(), _name)) {
				matchingRelationship = relationship;
			}
		}
		return matchingRelationship;
	}

	public void setDeleteProcedure(EOStoredProcedure _deleteProcedure) {
		EOStoredProcedure oldDeleteProcedure = myDeleteProcedure;
		myDeleteProcedure = _deleteProcedure;
		firePropertyChange(EOEntity.DELETE_PROCEDURE, oldDeleteProcedure, myDeleteProcedure);
	}

	public EOStoredProcedure getDeleteProcedure() {
		return myDeleteProcedure;
	}

	public void setFetchAllProcedure(EOStoredProcedure _fetchAllProcedure) {
		EOStoredProcedure oldFetchAllProcedure = myFetchAllProcedure;
		myFetchAllProcedure = _fetchAllProcedure;
		firePropertyChange(EOEntity.FETCH_ALL_PROCEDURE, oldFetchAllProcedure, myFetchAllProcedure);
	}

	public EOStoredProcedure getFetchAllProcedure() {
		return myFetchAllProcedure;
	}

	public void setFetchWithPrimaryKeyProcedure(EOStoredProcedure _fetchWithPrimaryKeyProcedure) {
		EOStoredProcedure oldFetchWithPrimaryKeyProcedure = myFetchWithPrimaryKeyProcedure;
		myFetchWithPrimaryKeyProcedure = _fetchWithPrimaryKeyProcedure;
		firePropertyChange(EOEntity.FETCH_WITH_PRIMARY_KEY_PROCEDURE, oldFetchWithPrimaryKeyProcedure, myFetchWithPrimaryKeyProcedure);
	}

	public EOStoredProcedure getFetchWithPrimaryKeyProcedure() {
		return myFetchWithPrimaryKeyProcedure;
	}

	public void setInsertProcedure(EOStoredProcedure _insertProcedure) {
		EOStoredProcedure oldInsertProcedure = myInsertProcedure;
		myInsertProcedure = _insertProcedure;
		firePropertyChange(EOEntity.INSERT_PROCEDURE, oldInsertProcedure, myInsertProcedure);
	}

	public EOStoredProcedure getInsertProcedure() {
		return myInsertProcedure;
	}

	public void setNextPrimaryKeyProcedure(EOStoredProcedure _nextPrimaryKeyProcedure) {
		EOStoredProcedure oldNextPrimaryKeyProcedure = myNextPrimaryKeyProcedure;
		myNextPrimaryKeyProcedure = _nextPrimaryKeyProcedure;
		firePropertyChange(EOEntity.NEXT_PRIMARY_KEY_PROCEDURE, oldNextPrimaryKeyProcedure, myNextPrimaryKeyProcedure);
	}

	public EOStoredProcedure getNextPrimaryKeyProcedure() {
		return myNextPrimaryKeyProcedure;
	}

	public void loadFromMap(EOModelMap _entityMap, Set _failures) throws DuplicateNameException {
		myEntityMap = _entityMap;
		myName = _entityMap.getString("name", true);
		myExternalName = _entityMap.getString("externalName", true);
		myClassName = _entityMap.getString("className", true);

		Map internalInfo = myEntityMap.getMap("internalInfo");
		if (internalInfo != null) {
			EOModelMap internalInfoModelMap = new EOModelMap(internalInfo);
			myClientClassName = internalInfoModelMap.getString("_javaClientClassName", true);
		}

		myCachesObjects = _entityMap.getBoolean("cachesObjects");
		if (_entityMap.containsKey("isFetchable")) {
			myAbstractEntity = Boolean.valueOf(!_entityMap.getBoolean("isFetchable").booleanValue());
		} else {
			myAbstractEntity = _entityMap.getBoolean("isAbstractEntity");
		}
		myReadOnly = _entityMap.getBoolean("isReadOnly");
		if (_entityMap.containsKey("mappingQualifier")) {
			myRestrictingQualifier = _entityMap.getString("mappingQualifier", true);
		} else {
			myRestrictingQualifier = _entityMap.getString("restrictingQualifier", true);
		}
		myExternalQuery = _entityMap.getString("externalQuery", true);
		myMaxNumberOfInstancesToBatchFetch = _entityMap.getInteger("maxNumberOfInstancesToBatchFetch");
		loadUserInfo(_entityMap);

		Set attributeList = _entityMap.getSet("attributes");
		if (attributeList != null) {
			Iterator attributeIter = attributeList.iterator();
			while (attributeIter.hasNext()) {
				EOModelMap attributeMap = new EOModelMap((Map) attributeIter.next());
				EOAttribute attribute = new EOAttribute();
				attribute.loadFromMap(attributeMap, _failures);
				addAttribute(attribute, false, _failures);
			}
		}

		Set relationshipList = _entityMap.getSet("relationships");
		if (relationshipList != null) {
			Iterator relationshipIter = relationshipList.iterator();
			while (relationshipIter.hasNext()) {
				EOModelMap relationshipMap = new EOModelMap((Map) relationshipIter.next());
				EORelationship relationship = new EORelationship();
				relationship.loadFromMap(relationshipMap, _failures);
				addRelationship(relationship, true, _failures, false);
			}
		}

		Set attributesUsedForLocking = _entityMap.getSet("attributesUsedForLocking");
		if (attributesUsedForLocking != null) {
			Iterator attributesUsedForLockingIter = attributesUsedForLocking.iterator();
			while (attributesUsedForLockingIter.hasNext()) {
				String attributeName = (String) attributesUsedForLockingIter.next();
				EOAttribute attribute = getAttributeNamed(attributeName);
				if (attribute != null) {
					attribute.setUsedForLocking(Boolean.TRUE, false);
				}
			}
		}
	}

	public void loadFetchSpecsFromMap(EOModelMap _map, Set _failures) throws EOModelException {
		myFetchSpecsMap = _map;
		Set sharedObjectFetchSpecificationNames = myEntityMap.getSet("sharedObjectFetchSpecificationNames");

		if (_map != null && !_map.isEmpty()) {
			Iterator fetchSpecIter = _map.entrySet().iterator();
			while (fetchSpecIter.hasNext()) {
				Map.Entry fetchSpecEntry = (Map.Entry) fetchSpecIter.next();
				_addFetchSpecificationFromMap(fetchSpecEntry, _failures, sharedObjectFetchSpecificationNames);
			}
		}

		Map fetchSpecificationsDictionary = myEntityMap.getMap("fetchSpecificationDictionary");
		if (fetchSpecificationsDictionary != null && !fetchSpecificationsDictionary.isEmpty()) {
			Iterator fetchSpecIter = fetchSpecificationsDictionary.entrySet().iterator();
			while (fetchSpecIter.hasNext()) {
				Map.Entry fetchSpecEntry = (Map.Entry) fetchSpecIter.next();
				_addFetchSpecificationFromMap(fetchSpecEntry, _failures, sharedObjectFetchSpecificationNames);
			}
		}
	}

	protected void _addFetchSpecificationFromMap(Map.Entry _fetchSpecEntry, Set _failures, Set _sharedObjectFetchSpecificationNames) throws EOModelException {
		String fetchSpecName = _fetchSpecEntry.getKey().toString();
		EOModelMap fetchSpecMap = new EOModelMap((Map) _fetchSpecEntry.getValue());
		EOFetchSpecification fetchSpec = new EOFetchSpecification(fetchSpecName);
		fetchSpec.loadFromMap(fetchSpecMap, _failures);
		if (_sharedObjectFetchSpecificationNames != null && _sharedObjectFetchSpecificationNames.contains(fetchSpecName)) {
			fetchSpec.setSharesObjects(Boolean.TRUE, false);
		}
		addFetchSpecification(fetchSpec, false, _failures);
	}

	public EOModelMap toEntityMap() {
		EOModelMap entityMap = myEntityMap.cloneModelMap();
		entityMap.setString("name", myName, true);
		entityMap.setString("externalName", myExternalName, true);
		entityMap.setString("className", myClassName, true);
		if (myParent != null) {
			entityMap.setString("parent", myParent.getName(), true);
		}
		entityMap.setBoolean("cachesObjects", myCachesObjects, EOModelMap.YN);
		entityMap.setBoolean("isAbstractEntity", myAbstractEntity, EOModelMap.YN);
		entityMap.remove("isFetchable");
		entityMap.setBoolean("isReadOnly", myReadOnly, EOModelMap.YN);
		entityMap.setString("restrictingQualifier", myRestrictingQualifier, true);
		entityMap.remove("mappingQualifier");
		entityMap.setString("externalQuery", myExternalQuery, true);
		entityMap.setInteger("maxNumberOfInstancesToBatchFetch", myMaxNumberOfInstancesToBatchFetch);

		entityMap.remove("fetchSpecificationDictionary");

		Set classProperties = new PropertyListSet();
		Set primaryKeyAttributes = new PropertyListSet();
		Set attributesUsedForLocking = new PropertyListSet();
		Set clientClassProperties = new PropertyListSet();
		Set attributes = new PropertyListSet();
		Iterator attributeIter = myAttributes.iterator();
		while (attributeIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributeIter.next();
			EOModelMap attributeMap = attribute.toMap();
			attributes.add(attributeMap);
			if (BooleanUtils.isTrue(attribute.isClassProperty())) {
				classProperties.add(attribute.getName());
			}
			if (BooleanUtils.isTrue(attribute.isPrimaryKey())) {
				primaryKeyAttributes.add(attribute.getName());
			}
			if (BooleanUtils.isTrue(attribute.isUsedForLocking())) {
				attributesUsedForLocking.add(attribute.getName());
			}
			if (BooleanUtils.isTrue(attribute.isClientClassProperty())) {
				clientClassProperties.add(attribute.getName());
			}
		}
		entityMap.setSet("attributes", attributes, true);

		Set relationships = new PropertyListSet();
		Iterator relationshipIter = myRelationships.iterator();
		while (relationshipIter.hasNext()) {
			EORelationship relationship = (EORelationship) relationshipIter.next();
			EOModelMap relationshipMap = relationship.toMap();
			relationships.add(relationshipMap);
			if (BooleanUtils.isTrue(relationship.isClassProperty())) {
				classProperties.add(relationship.getName());
			}
			if (BooleanUtils.isTrue(relationship.isClientClassProperty())) {
				clientClassProperties.add(relationship.getName());
			}
		}
		entityMap.setSet("relationships", relationships, true);
		entityMap.setSet("attributesUsedForLocking", attributesUsedForLocking, true);
		entityMap.setSet("classProperties", classProperties, true);
		entityMap.setSet("primaryKeyAttributes", primaryKeyAttributes, true);

		Set sharedObjectFetchSpecificationNames = new PropertyListSet();
		Iterator fetchSpecsIter = myFetchSpecs.iterator();
		while (fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
			if (BooleanUtils.isTrue(fetchSpec.isSharesObjects())) {
				sharedObjectFetchSpecificationNames.add(fetchSpec.getName());
			}
		}
		entityMap.setSet("sharedObjectFetchSpecificationNames", sharedObjectFetchSpecificationNames, true);

		Map internalInfoMap = entityMap.getMap("internalInfo");
		if (internalInfoMap == null) {
			internalInfoMap = new HashMap();
		}
		if (!clientClassProperties.isEmpty()) {
			internalInfoMap.put("_clientClassPropertyNames", clientClassProperties);
		} else {
			internalInfoMap.remove("_clientClassPropertyNames");
		}
		if (myClientClassName != null && myClientClassName.length() > 0) {
			internalInfoMap.put("_javaClientClassName", myClientClassName);
		} else {
			internalInfoMap.remove("_javaClientClassName");
		}
		entityMap.setMap("internalInfo", internalInfoMap, false);

		Map storedProcedureNames = myEntityMap.getMap("storedProcedureNames");
		if (storedProcedureNames == null) {
			storedProcedureNames = new HashMap();
		}
		if (myDeleteProcedure == null) {
			storedProcedureNames.remove(EOEntity.EODELETE_PROCEDURE);
		} else {
			storedProcedureNames.put(EOEntity.EODELETE_PROCEDURE, myDeleteProcedure.getName());
		}
		if (myInsertProcedure == null) {
			storedProcedureNames.remove(EOEntity.EOINSERT_PROCEDURE);
		} else {
			storedProcedureNames.put(EOEntity.EOINSERT_PROCEDURE, myInsertProcedure.getName());
		}
		if (myFetchAllProcedure == null) {
			storedProcedureNames.remove(EOEntity.EOFETCH_ALL_PROCEDURE);
		} else {
			storedProcedureNames.put(EOEntity.EOFETCH_ALL_PROCEDURE, myFetchAllProcedure.getName());
		}
		if (myFetchWithPrimaryKeyProcedure == null) {
			storedProcedureNames.remove(EOEntity.EOFETCH_WITH_PRIMARY_KEY_PROCEDURE);
		} else {
			storedProcedureNames.put(EOEntity.EOFETCH_WITH_PRIMARY_KEY_PROCEDURE, myFetchWithPrimaryKeyProcedure.getName());
		}
		if (myNextPrimaryKeyProcedure == null) {
			storedProcedureNames.remove(EOEntity.EONEXT_PRIMARY_KEY_PROCEDURE);
		} else {
			storedProcedureNames.put(EOEntity.EONEXT_PRIMARY_KEY_PROCEDURE, myNextPrimaryKeyProcedure.getName());
		}

		writeUserInfo(entityMap);

		return entityMap;
	}

	public EOModelMap toFetchSpecsMap() {
		EOModelMap fetchSpecsMap = myFetchSpecsMap.cloneModelMap();
		fetchSpecsMap.clear();
		Iterator fetchSpecIter = myFetchSpecs.iterator();
		while (fetchSpecIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
			EOModelMap fetchSpecMap = fetchSpec.toMap();
			fetchSpecsMap.setMap(fetchSpec.getName(), fetchSpecMap, true);
		}
		return fetchSpecsMap;
	}

	public void loadFromURL(URL entityURL, Set failures) throws EOModelException {
		try {
			EOModelMap entityMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromURL(entityURL, new EOModelParserDataStructureFactory()));
			loadFromMap(entityMap, failures);
		} catch (Throwable e) {
			throw new EOModelException("Failed to load entity from '" + entityURL + "'.", e);
		}
	}

	public void loadFetchSpecsFromURL(URL fetchSpecURL, Set failures) throws EOModelException {
		try {
			EOModelMap fspecMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromURL(fetchSpecURL, new EOModelParserDataStructureFactory()));
			loadFetchSpecsFromMap(fspecMap, failures);
		} catch (Throwable e) {
			throw new EOModelException("Failed to load fetch specifications from '" + fetchSpecURL + "'.", e);
		}
	}

	public void saveToFile(File _entityFile) {
		EOModelMap entityMap = toEntityMap();
		PropertyListSerialization.propertyListToFile(_entityFile, entityMap);
	}

	public void saveFetchSpecsToFile(File _fetchSpecFile) {
		if (myFetchSpecs.size() == 0) {
			_fetchSpecFile.delete();
		} else {
			EOModelMap fetchSpecMap = toFetchSpecsMap();
			PropertyListSerialization.propertyListToFile(_fetchSpecFile, fetchSpecMap);
		}
	}

	public void resolve(Set _failures) {
		String parentName = myEntityMap.getString("parent", true);
		if (parentName != null) {
			if (myModel != null) {
				myParent = myModel.getModelGroup().getEntityNamed(parentName);
			}
			if (myParent == null) {
				_failures.add(new MissingEntityFailure(myModel, parentName));
			}
		}

		Iterator attributeIter = myAttributes.iterator();
		while (attributeIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributeIter.next();
			attribute.resolve(_failures);
		}

		Iterator relationshipIter = myRelationships.iterator();
		while (relationshipIter.hasNext()) {
			EORelationship relationship = (EORelationship) relationshipIter.next();
			relationship.resolve(_failures);
		}

		Iterator fetchSpecIter = myFetchSpecs.iterator();
		while (fetchSpecIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
			fetchSpec.resolve(_failures);
		}

		Set classProperties = myEntityMap.getSet("classProperties");
		if (classProperties != null) {
			Iterator classPropertiesIter = classProperties.iterator();
			while (classPropertiesIter.hasNext()) {
				String attributeName = (String) classPropertiesIter.next();
				IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
				if (attribute != null) {
					attribute.setClassProperty(Boolean.TRUE, false);
				}
			}
		}

		Set primaryKeyAttributes = myEntityMap.getSet("primaryKeyAttributes");
		if (primaryKeyAttributes != null) {
			Iterator primaryKeyAttributesIter = primaryKeyAttributes.iterator();
			while (primaryKeyAttributesIter.hasNext()) {
				String attributeName = (String) primaryKeyAttributesIter.next();
				EOAttribute attribute = getAttributeNamed(attributeName);
				if (attribute != null) {
					attribute.setPrimaryKey(Boolean.TRUE, false);
				}
			}
		}

		Map internalInfo = myEntityMap.getMap("internalInfo");
		if (internalInfo != null) {
			EOModelMap internalInfoModelMap = new EOModelMap(internalInfo);
			Set clientClassPropertyNames = internalInfoModelMap.getSet("_clientClassPropertyNames");
			if (clientClassPropertyNames != null) {
				Iterator clientClassPropertyNameIter = clientClassPropertyNames.iterator();
				while (clientClassPropertyNameIter.hasNext()) {
					String attributeName = (String) clientClassPropertyNameIter.next();
					IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
					if (attribute != null) {
						attribute.setClientClassProperty(Boolean.TRUE, false);
					}
				}
			}
		}

		Map storedProcedureNames = myEntityMap.getMap("storedProcedureNames");
		if (storedProcedureNames != null) {
			String deleteProcedureName = (String) storedProcedureNames.get(EOEntity.EODELETE_PROCEDURE);
			if (deleteProcedureName != null) {
				myDeleteProcedure = myModel.getStoredProcedureNamed(deleteProcedureName);
				if (myDeleteProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s delete procedure '" + deleteProcedureName + "' is missing.", false));
				}
			}
			String fetchAllProcedureName = (String) storedProcedureNames.get(EOEntity.EOFETCH_ALL_PROCEDURE);
			if (fetchAllProcedureName != null) {
				myFetchAllProcedure = myModel.getStoredProcedureNamed(fetchAllProcedureName);
				if (myFetchAllProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s fetch all procedure '" + fetchAllProcedureName + "' is missing.", false));
				}
			}
			String fetchWithPrimaryKeyProcedureName = (String) storedProcedureNames.get(EOEntity.EOFETCH_WITH_PRIMARY_KEY_PROCEDURE);
			if (fetchWithPrimaryKeyProcedureName != null) {
				myFetchWithPrimaryKeyProcedure = myModel.getStoredProcedureNamed(fetchWithPrimaryKeyProcedureName);
				if (myFetchWithPrimaryKeyProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s fetch with primary key procedure '" + fetchWithPrimaryKeyProcedureName + "' is missing.", false));
				}
			}
			String insertProcedureName = (String) storedProcedureNames.get(EOEntity.EOINSERT_PROCEDURE);
			if (insertProcedureName != null) {
				myInsertProcedure = myModel.getStoredProcedureNamed(insertProcedureName);
				if (myInsertProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s insert procedure '" + insertProcedureName + "' is missing.", false));
				}
			}
			String nextPrimaryKeyProcedureName = (String) storedProcedureNames.get(EOEntity.EONEXT_PRIMARY_KEY_PROCEDURE);
			if (nextPrimaryKeyProcedureName != null) {
				myNextPrimaryKeyProcedure = myModel.getStoredProcedureNamed(nextPrimaryKeyProcedureName);
				if (myNextPrimaryKeyProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s next primary key procedure '" + nextPrimaryKeyProcedureName + "' is missing.", false));
				}
			}
		}
	}

	public void verify(Set _failures) {
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

		Iterator attributeIter = myAttributes.iterator();
		while (attributeIter.hasNext()) {
			EOAttribute attribute = (EOAttribute) attributeIter.next();
			attribute.verify(_failures);
		}

		Iterator relationshipIter = myRelationships.iterator();
		while (relationshipIter.hasNext()) {
			EORelationship relationship = (EORelationship) relationshipIter.next();
			relationship.verify(_failures);
		}

		Iterator fetchSpecIter = myFetchSpecs.iterator();
		while (fetchSpecIter.hasNext()) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
			fetchSpec.verify(_failures);
		}

		if (!isPrototype()) {
			String externalName = getExternalName();
			if (externalName == null || externalName.trim().length() == 0) {
				if (!BooleanUtils.isTrue(isAbstractEntity())) {
					_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + " has an empty table name.", false));
				}
			} else if (externalName.indexOf(' ') != -1) {
				_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + "'s table name '" + externalName + "' has a space in it.", false));
			}
		}

		EOEntity parent = getParent();
		if (parent != null && !BooleanUtils.isTrue(parent.isAbstractEntity()) && getRestrictingQualifier() == null && ComparisonUtils.equals(parent.getExternalName(), getExternalName())) {
			_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + " is a subclass of " + getParent().getName() + " but does not have a restricting qualifier.", false));
		}
		try {
			inheritParentAttributesAndRelationships(_failures, false);
		}
		catch (DuplicateNameException e) {
			_failures.add(new EOModelVerificationFailure(myModel, "Failed to fix inherited attributes and relationships for " + getFullyQualifiedName() + ".", true));
		}

		Set primaryKeyAttributes = getPrimaryKeyAttributes();
		if (primaryKeyAttributes.isEmpty()) {
			_failures.add(new EOModelVerificationFailure(myModel, getFullyQualifiedName() + " does not have a primary key.", false));
		}
	}

	public String getFullyQualifiedName() {
		return ((myModel == null) ? "?" : myModel.getFullyQualifiedName()) + ", " + myName;
	}

	public String toString() {
		return "[EOEntity: name = " + myName + "; attributes = " + myAttributes + "; relationships = " + myRelationships + "; fetchSpecs = " + myFetchSpecs + "]"; //$NON-NLS-4$ //$NON-NLS-5$
	}
}
