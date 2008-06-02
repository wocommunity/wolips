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
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.eomodeler.core.Messages;
import org.objectstyle.wolips.eomodeler.core.kvc.KeyPath;
import org.objectstyle.wolips.eomodeler.core.model.history.EOAttributeAddedEvent;
import org.objectstyle.wolips.eomodeler.core.model.history.EOAttributeDeletedEvent;
import org.objectstyle.wolips.eomodeler.core.model.history.EOEntityRenamedEvent;
import org.objectstyle.wolips.eomodeler.core.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.core.utils.NameSyncUtils;
import org.objectstyle.wolips.eomodeler.core.utils.NameSyncUtils.NamePair;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;

public class EOEntity extends UserInfoableEOModelObject<EOModel> implements IEOEntityRelative, ISortableEOModelObject {
	private static final String EONEXT_PRIMARY_KEY_PROCEDURE = "EONextPrimaryKeyProcedure";

	private static final String EOFETCH_WITH_PRIMARY_KEY_PROCEDURE = "EOFetchWithPrimaryKeyProcedure";

	private static final String EOFETCH_ALL_PROCEDURE = "EOFetchAllProcedure";

	private static final String EOINSERT_PROCEDURE = "EOInsertProcedure";

	private static final String EODELETE_PROCEDURE = "EODeleteProcedure";

	private static final String FETCH_ALL = "FetchAll";

	public static final String ATTRIBUTE = "attribute";

	public static final String RELATIONSHIP = "relationship";

	public static final String FETCH_SPECIFICATION = "fetchSpecification";

	public static final String ENTITY_INDEX = "entityIndex";

	public static final String NAME = "name";

	public static final String CLASS_NAME = "className";

	public static final String CLIENT_CLASS_NAME = "clientClassName";

	public static final String PARENT_CLASS_NAME = "parentClassName";

	public static final String PARENT = "parent";

	public static final String PARTIAL_ENTITY = "partialEntity";

	public static final String GENERATE_SOURCE = "generateSource";

	public static final String EXTERNAL_QUERY = "externalQuery";

	public static final String MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH = "maxNumberOfInstancesToBatchFetch";

	public static final String READ_ONLY = "readOnly";

	public static final String EXTERNAL_NAME = "externalName";

	public static final String ABSTRACT_ENTITY = "abstractEntity";

	public static final String CACHES_OBJECTS = "cachesObjects";

	public static final String RESTRICTING_QUALIFIER = "restrictingQualifier";

	public static final String FETCH_SPECIFICATIONS = "fetchSpecifications";

	public static final String ENTITY_INDEXES = "entityIndexes";

	public static final String ATTRIBUTES = "attributes";

	public static final String RELATIONSHIPS = "relationships";

	public static final String DELETE_PROCEDURE = "deleteProcedure";

	public static final String FETCH_ALL_PROCEDURE = "fetchAllProcedure";

	public static final String FETCH_WITH_PRIMARY_KEY_PROCEDURE = "fetchWithPrimaryKeyProcedure";

	public static final String INSERT_PROCEDURE = "insertProcedure";

	public static final String NEXT_PRIMARY_KEY_PROCEDURE = "nextPrimaryKeyProcedure";

	private EOModel myModel;

	private EOEntity myParent;

	private EOEntity myPartialEntity;

	private boolean myGenerateSource;

	private String myOriginalName;

	private String myName;

	private String myExternalName;

	private String myClassName;

	private String myClientClassName;
	
	private String myParentClassName;

	private String myRestrictingQualifier;

	private String myExternalQuery;

	private Boolean myCachesObjects;

	private Boolean myAbstractEntity;

	private Boolean myReadOnly;

	private Integer myMaxNumberOfInstancesToBatchFetch;

	private Set<EOAttribute> myAttributes;

	private Set<EORelationship> myRelationships;

	private Set<EOFetchSpecification> myFetchSpecs;

	private Set<EOEntityIndex> myEntityIndexes;

	private EOModelMap myEntityMap;

	private EOModelMap myFetchSpecsMap;

	private EOStoredProcedure myDeleteProcedure;

	private EOStoredProcedure myFetchAllProcedure;

	private EOStoredProcedure myFetchWithPrimaryKeyProcedure;

	private EOStoredProcedure myInsertProcedure;

	private EOStoredProcedure myNextPrimaryKeyProcedure;

	public EOEntity() {
		myAttributes = new HashSet<EOAttribute>();
		myRelationships = new HashSet<EORelationship>();
		myFetchSpecs = new HashSet<EOFetchSpecification>();
		myEntityIndexes = new HashSet<EOEntityIndex>();
		myEntityMap = new EOModelMap();
		myFetchSpecsMap = new EOModelMap();
		myGenerateSource = true;
	}

	public EOEntity(String _name) {
		this();
		myName = _name;
	}

	public AbstractEOAttributePath resolveKeyPath(String _keyPath) {
		AbstractEOAttributePath targetAttribute = resolveKeyPath(_keyPath, null, new HashSet<EORelationship>());
		return targetAttribute;
	}

	public AbstractEOAttributePath resolveKeyPath(String _keyPath, EORelationshipPath _parentRelationshipPath, Set<EORelationship> _visitedRelationships) {
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
		for (EOAttribute attribute : getAttributes()) {
			attribute.pasted();
		}

		for (EORelationship relationship : getRelationships()) {
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
		return joinInManyToManyWith(_entity2, true, relationshipName, true, inverseRelationshipName, joinEntityName, true);
	}

	public EOEntity joinInManyToManyWith(EOEntity _entity2, boolean createRelationship, String _relationshipName, boolean createInverseRelationship, String _inverseRelationshipName, String _joinEntityName, boolean _flatten) throws DuplicateNameException {
		EOEntity manyToManyEntity = new EOEntity(_joinEntityName);
		manyToManyEntity.setExternalName(manyToManyEntity.getName());
		Set<EOEntity> joiningEntitiesSet = new HashSet<EOEntity>();
		joiningEntitiesSet.add(this);
		joiningEntitiesSet.add(_entity2);
		String packageName = getModel().guessPackageName(joiningEntitiesSet);
		if (_flatten) {
			manyToManyEntity.setClassName("EOGenericRecord");
		} else {
			String className = manyToManyEntity.getName();
			if (packageName != null && packageName.length() > 0) {
				className = packageName + "." + className;
			}
			manyToManyEntity.setClassName(className);
		}

		EORelationship entity1Relationship = manyToManyEntity.addBlankRelationship(StringUtils.toLowercaseFirstLetter(getName()));
		entity1Relationship.setToMany(Boolean.FALSE);
		entity1Relationship.setDestination(this);
		entity1Relationship.setClassProperty(Boolean.valueOf(!_flatten));
		entity1Relationship.setMandatory(Boolean.TRUE);
		Iterator<EOAttribute> entity1PrimaryKeyAttributesIter = getPrimaryKeyAttributes().iterator();
		if (!entity1PrimaryKeyAttributesIter.hasNext()) {
			throw new IllegalStateException("The entity " + getFullyQualifiedName() + " does not have any primary keys.");
		}
		while (entity1PrimaryKeyAttributesIter.hasNext()) {
			EOAttribute entity1PrimaryKeyAttribute = entity1PrimaryKeyAttributesIter.next();
			EOAttribute manyToManyPrimaryKeyAttribute = entity1PrimaryKeyAttribute._cloneModelObject();
			manyToManyPrimaryKeyAttribute.setName(manyToManyEntity.findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(getName()) + StringUtils.toUppercaseFirstLetter(manyToManyPrimaryKeyAttribute.getName())));
			manyToManyPrimaryKeyAttribute.setColumnName(manyToManyPrimaryKeyAttribute.getName());
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
		Iterator<EOAttribute> entity2PrimaryKeyAttributesIter = _entity2.getPrimaryKeyAttributes().iterator();
		if (!entity2PrimaryKeyAttributesIter.hasNext()) {
			throw new IllegalStateException("The entity " + _entity2.getFullyQualifiedName() + " does not have any primary keys.");
		}
		while (entity2PrimaryKeyAttributesIter.hasNext()) {
			EOAttribute entity2PrimaryKeyAttribute = entity2PrimaryKeyAttributesIter.next();
			EOAttribute manyToManyPrimaryKeyAttribute = entity2PrimaryKeyAttribute._cloneModelObject();
			manyToManyPrimaryKeyAttribute.setName(manyToManyEntity.findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(_entity2.getName()) + StringUtils.toUppercaseFirstLetter(manyToManyPrimaryKeyAttribute.getName())));
			manyToManyPrimaryKeyAttribute.setColumnName(manyToManyPrimaryKeyAttribute.getName());
			EOJoin entity2Join = new EOJoin();
			entity2Join.setSourceAttribute(manyToManyPrimaryKeyAttribute);
			entity2Join.setDestinationAttribute(entity2PrimaryKeyAttribute);
			entity2Relationship.addJoin(entity2Join, false);
			manyToManyEntity.addAttribute(manyToManyPrimaryKeyAttribute);
		}
		manyToManyEntity.addRelationship(entity2Relationship);

		if (createRelationship) {
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
		}

		if (createInverseRelationship) {
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
		}

		getModel().addEntity(manyToManyEntity);

		return manyToManyEntity;
	}

	public EOAttribute getSinglePrimaryKeyAttribute() throws EOModelException {
		Set<EOAttribute> destinationPrimaryKeys = getPrimaryKeyAttributes();
		if (destinationPrimaryKeys.size() > 1) {
			throw new EOModelException(getName() + " has a compound primary key.");
		}
		EOAttribute primaryKey = destinationPrimaryKeys.iterator().next();
		return primaryKey;
	}

	public EOAttribute createForeignKeyTo(EOEntity foreignEntity, String foreignKeyName, String foreignKeyColumnName, boolean allowsNull) throws EOModelException {
		EOAttribute foreignPrimaryKey = foreignEntity.getSinglePrimaryKeyAttribute();
		EOAttribute foreignKeyAttribute = foreignPrimaryKey._cloneModelObject();
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
		for (EOAttribute destinationPrimaryKeyAttribute : _destinationEntity.getPrimaryKeyAttributes()) {
			EOJoin inverseJoin = new EOJoin();
			inverseJoin.setDestinationAttribute(destinationPrimaryKeyAttribute, false);
			relationship.addJoin(inverseJoin, false);
		}
		relationship._setEntity(this);
		return relationship;
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
		Set<EOAttribute> primaryKeyAttributes = getPrimaryKeyAttributes();
		for (EOAttribute superclassPrimaryKeyAttribute : getPrimaryKeyAttributes()) {
			EOAttribute subclassPrimaryKeyAttribute = superclassPrimaryKeyAttribute._cloneModelObject();
			EOJoin superclassJoin = new EOJoin();
			superclassJoin.setSourceAttribute(subclassPrimaryKeyAttribute);
			superclassJoin.setDestinationAttribute(superclassPrimaryKeyAttribute);
			superclassRelationship.addJoin(superclassJoin, false);
			subclassEntity.addAttribute(subclassPrimaryKeyAttribute);
		}

		for (EOAttribute parentAttribute : getAttributes()) {
			if (!primaryKeyAttributes.contains(parentAttribute)) {
				EOAttributePath inheritedAttributePath = new EOAttributePath(new EORelationshipPath(null, superclassRelationship), parentAttribute);
				EOAttribute inheritedAttribute = subclassEntity.addBlankAttribute(parentAttribute.getName(), inheritedAttributePath);
				inheritedAttribute.setClassProperty(parentAttribute.isClassProperty());
			}
		}

		for (EORelationship parentRelationship : getRelationships()) {
			if (BooleanUtils.isTrue(parentRelationship.isClassProperty())) {
				EORelationshipPath inheritedRelationshipPath = new EORelationshipPath(new EORelationshipPath(null, superclassRelationship), parentRelationship);
				subclassEntity.addBlankRelationship(parentRelationship.getName(), inheritedRelationshipPath);
			}
		}

		return subclassEntity;
	}

	protected void _cloneFetchSpecificationsFrom(EOEntity _entity, boolean _skipExistingNames) throws DuplicateNameException {
		for (EOFetchSpecification fetchSpec : _entity.getFetchSpecs()) {
			if (!_skipExistingNames || getFetchSpecNamed(fetchSpec.getName()) == null) {
				EOFetchSpecification clonedFetchSpec = fetchSpec._cloneModelObject();
				clonedFetchSpec.setName(findUnusedFetchSpecificationName(clonedFetchSpec.getName()));
				addFetchSpecification(clonedFetchSpec);
			}
		}
	}

	protected void _cloneEntityIndexesFrom(EOEntity _entity, boolean _skipExistingNames) throws DuplicateNameException {
		for (EOEntityIndex entityIndex : _entity.getEntityIndexes()) {
			if (!_skipExistingNames || getEntityIndexNamed(entityIndex.getName()) == null) {
				EOEntityIndex clonedEntityIndex = entityIndex._cloneModelObject();
				clonedEntityIndex.setName(findUnusedEntityIndexName(clonedEntityIndex.getName()));
				addEntityIndex(clonedEntityIndex);
			}
		}
	}

	// MS: replace with _cloneAttributesAndRelationships(Set) ?
	protected void _cloneAttributesAndRelationshipsFrom(EOEntity _entity, boolean _skipExistingNames, Set<EOModelVerificationFailure> failures, boolean warnOnly) throws DuplicateNameException {
		for (EOAttribute attribute : _entity.getAttributes()) {
			if (!_skipExistingNames || getAttributeNamed(attribute.getName()) == null) {
				if (failures != null) {
					failures.add(new EOModelVerificationFailure(getModel(), this, "The entity " + getName() + " was missing the inherited attribute " + attribute.getName() + ".", true));
				}
				if (!warnOnly) {
					EOAttribute clonedAttribute = attribute._cloneModelObject();
					clonedAttribute.setName(findUnusedAttributeName(clonedAttribute.getName()));
					addAttribute(clonedAttribute);
				}
			}
		}

		for (EORelationship relationship : _entity.getRelationships()) {
			if (!_skipExistingNames || getRelationshipNamed(relationship.getName()) == null) {
				if (failures != null) {
					failures.add(new EOModelVerificationFailure(getModel(), this, "The entity " + getName() + " was missing the inherited relationship " + relationship.getName() + ".", true));
				}
				if (!warnOnly) {
					EORelationship clonedRelationship = relationship._cloneModelObject();
					clonedRelationship.setName(findUnusedRelationshipName(clonedRelationship.getName()));
					addRelationship(clonedRelationship, false, null, true);
				}
			}
		}
	}

	protected void _cloneAttributesAndRelationships(Set<IEOAttribute> attributesAndRelationships) throws DuplicateNameException {
		for (IEOAttribute attributeOrRelationship : attributesAndRelationships) {
			if (attributeOrRelationship instanceof EOAttribute) {
				EOAttribute attribute = (EOAttribute) attributeOrRelationship;
				EOAttribute clonedAttribute = attribute._cloneModelObject();
				clonedAttribute.setName(findUnusedAttributeName(clonedAttribute.getName()));
				addAttribute(clonedAttribute);
			} else {
				EORelationship relationship = (EORelationship) attributeOrRelationship;
				EORelationship clonedRelationship = relationship._cloneModelObject();
				clonedRelationship.setName(findUnusedRelationshipName(clonedRelationship.getName()));
				addRelationship(clonedRelationship, false, null, true);
			}
		}
	}

	protected Set<IEOAttribute> _findMissingInheritedAttributesAndRelationships() {
		Set<IEOAttribute> missingInheritedAttributesAndRelationships = new HashSet<IEOAttribute>();
		EOEntity parentEntity = getParent();
		if (parentEntity != null) {
			for (EOAttribute attribute : parentEntity.getAttributes()) {
				if (getAttributeNamed(attribute.getName()) == null) {
					missingInheritedAttributesAndRelationships.add(attribute);
				}
			}

			for (EORelationship relationship : parentEntity.getRelationships()) {
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

	public EOAttribute _getTemplateNameAttribute(boolean checkOtherEntities) {
		EOAttribute templateNameAttribute = null;
		Set<EOAttribute> attributes = getAttributes();
		for (EOAttribute attribute : attributes) {
			String attributeName = attribute.getName();
			if (StringUtils.camelCaseToUnderscore(attributeName).indexOf('_') != -1) {
				templateNameAttribute = attribute;
				break;
			}
		}
		if (templateNameAttribute == null && checkOtherEntities) {
			EOModel model = getModel();
			if (model != null) {
				for (EOEntity entity : model.getEntities()) {
					if (entity != this) {
						templateNameAttribute = entity._getTemplateNameAttribute(false);
						if (templateNameAttribute != null) {
							break;
						}
					}
				}
			}
			if (templateNameAttribute == null && !attributes.isEmpty()) {
				templateNameAttribute = attributes.iterator().next();
			}
		}
		return templateNameAttribute;
	}

	public EOAttribute addBlankAttribute(String _name) throws DuplicateNameException {
		return addBlankAttribute(_name, null);
	}

	public EOAttribute addBlankAttribute(EOAttributePath _flattenAttribute) throws DuplicateNameException {
		return addBlankAttribute(_flattenAttribute.toKeyPath().replace('.', '_'), _flattenAttribute);
	}

	public EOAttribute addBlankAttribute(String _name, EOAttributePath _flattenAttribute) throws DuplicateNameException {
		String newAttributeName = findUnusedAttributeName(_name);
		EOAttribute attribute;
		if (_flattenAttribute != null) {
			EOAttribute attributeToFlatten = _flattenAttribute.getChildAttribute();
			attribute = attributeToFlatten._cloneModelObject();
			attribute.setName(newAttributeName);
			attribute.setColumnName("");
			attribute.setClassProperty(Boolean.TRUE);
			addAttribute(attribute);
			attribute.setDefinition(_flattenAttribute.toKeyPath());
		} else {
			attribute = new EOAttribute(newAttributeName);
			attribute.setUsedForLocking(Boolean.TRUE);
			attribute.guessColumnNameInEntity(this);
			attribute.setClassProperty(Boolean.TRUE);
			addAttribute(attribute);
		}
		return attribute;
	}

	public EOFetchSpecification addBlankFetchSpec(String _name) throws DuplicateFetchSpecNameException {
		String newFetchSpecName = findUnusedFetchSpecificationName(_name);
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
		Iterator<EOFetchSpecification> fetchSpecsIter = myFetchSpecs.iterator();
		while (!hasSharedObjects && fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = fetchSpecsIter.next();
			hasSharedObjects = BooleanUtils.isTrue(fetchSpec.isSharesObjects());
		}
		return hasSharedObjects;
	}

	public void shareNoObjects() {
		for (EOFetchSpecification fetchSpec : myFetchSpecs) {
			fetchSpec.setSharesObjects(Boolean.FALSE);
		}
	}

	public boolean isSharesAllObjectsOnly() {
		boolean sharesAllObjects = false;
		int sharedFetchSpecCount = 0;
		for (EOFetchSpecification fetchSpec : myFetchSpecs) {
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

	public String getPluralName() {
		return StringUtils.toPlural(myName);
	}

	public String getInitialLowercaseName() {
		return StringUtils.toLowercaseFirstLetter(getName());
	}

	public String getPluralInitialLowercaseName() {
		return StringUtils.toLowercaseFirstLetter(StringUtils.toPlural(getName()));
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
			myModel._entityNameChanged(myOriginalName, oldName, _name);
			myModel.getModelEvents().addEvent(new EOEntityRenamedEvent(this));
		}
		myName = _name;
		if (_fireEvents) {
			synchronizeNameChange(oldName, myName);
			firePropertyChange(EOEntity.NAME, oldName, myName);

			if (myModel != null) {
				for (EORelationship relationship : getReferencingRelationships()) {
					if (relationship.getEntity().getModel() != myModel) {
						relationship.getEntity().getModel().setDirty(true);
					}
				}
				for (EOEntity childEntity : getChildrenEntities()) {
					if (childEntity.getModel() != myModel) {
						childEntity.getModel().setDirty(true);
					}
				}
			}
		}
	}

	public void entitySaved() {
		myOriginalName = myName;
	}

	public String getOriginalName() {
		return myOriginalName;
	}

	public boolean isGenericRecord() {
		String className = EOModelRenderContext.getInstance().getClassNameForEntity(this);
		boolean isGenericRecord = className == null || className.length() == 0 || className.endsWith("GenericRecord");
		return isGenericRecord;
	}

	public String getClassNameWithDefault() {
		String className = EOModelRenderContext.getInstance().getClassNameForEntity(this);
		if (className == null) {
			className = EOModelRenderContext.getInstance().getEOGenericRecordClassName();
		}
		return className;
	}

	public String getPackageName() {
		String packageName;
		String className = getClassNameWithDefault();
		if (className == null) {
			packageName = null;
		} else {
			int lastDotIndex = className.lastIndexOf('.');
			if (lastDotIndex == -1) {
				packageName = null;
			} else {
				packageName = className.substring(0, lastDotIndex);
			}
		}
		return packageName;
	}

	public String getSuperclassPackageName() {
		String packageName = getPackageName();
		String superclassPackage = EOModelRenderContext.getInstance().getSuperclassPackage();
		String superclassPackageName;
		if (superclassPackage != null) {
			if (packageName != null) {
				superclassPackageName = packageName + "." + superclassPackage;
			} else {
				superclassPackageName = superclassPackage;
			}
		} else {
			superclassPackageName = packageName;
		}
		return superclassPackageName;
	}

	public String getInitialLowercaseClassNameWithoutPackage() {
		return StringUtils.toLowercaseFirstLetter(getClassNameWithoutPackage());
	}

	public String getPluralInitialLowercaseClassNameWithoutPackage() {
		return StringUtils.toLowercaseFirstLetter(StringUtils.toPlural(getClassNameWithoutPackage()));
	}

	public String getClassNameWithOptionalPackage() {
		String className;
		if (EOModelRenderContext.getInstance().getSuperclassPackage() != null) {
			className = EOModelRenderContext.getInstance().getClassNameForEntity(this);
		} else {
			className = getClassNameWithoutPackage();
		}
		return className;
	}

	public String getClassNameWithoutPackage() {
		String classNameWithoutPackage;
		String className = getClassNameWithDefault();
		if (className == null) {
			classNameWithoutPackage = null;
		} else {
			int lastDotIndex = className.lastIndexOf('.');
			if (lastDotIndex == -1) {
				classNameWithoutPackage = className;
			} else {
				classNameWithoutPackage = className.substring(lastDotIndex + 1);
			}
		}
		return classNameWithoutPackage;
	}

	public String getPrefixClassNameWithOptionalPackage() {
		String prefixClassName;
		if (EOModelRenderContext.getInstance().getSuperclassPackage() != null) {
			prefixClassName = getPrefixClassName();
		} else {
			prefixClassName = getPrefixClassNameWithoutPackage();
		}
		return prefixClassName;
	}

	public String getPrefixClassNameWithoutPackage() {
		String prefixClassNameWithoutPackage = getClassNameWithoutPackage();
		if (prefixClassNameWithoutPackage != null) {
			String prefix = EOModelRenderContext.getInstance().getPrefix();
			prefixClassNameWithoutPackage = prefix + prefixClassNameWithoutPackage;
		}
		return prefixClassNameWithoutPackage;
	}

	public String getPrefixClassName() {
		String prefixClassName;
		String className = getClassNameWithDefault();
		if (className == null) {
			prefixClassName = null;
		} else {
			String superclassPackage = EOModelRenderContext.getInstance().getSuperclassPackage();
			if (superclassPackage != null && superclassPackage.trim().length() > 0) {
				superclassPackage = superclassPackage + ".";
			} else {
				superclassPackage = "";
			}
			String prefix = EOModelRenderContext.getInstance().getPrefix();
			int lastDotIndex = className.lastIndexOf('.');
			if (lastDotIndex == -1) {
				prefixClassName = superclassPackage + prefix + className;
			} else {
				prefixClassName = className.substring(0, lastDotIndex + 1) + superclassPackage + prefix + className.substring(lastDotIndex + 1);
			}
		}
		return prefixClassName;
	}

	public boolean isClassNameSet() {
		return EOModelRenderContext.getInstance().getClassNameForEntity(this) != null;
	}

	public String getClassName() {
		return myClassName;
	}

	public void guessClassNameInModel(EOModel model) {
		String className = getName();
		String packageName = null;
		if (model != null) {
			packageName = model.guessPackageName();
		}
		if (packageName != null && packageName.length() > 0) {
			className = packageName + "." + className;
		}
		setClassName(className);
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

	public boolean isParentClassNameSet() {
		return myParentClassName != null;
	}

	public String getParentClassName() {
		return myParentClassName;
	}

	public void setParentClassName(String _parentClassName) {
		String oldParentClassName = myParentClassName;
		myParentClassName = _parentClassName;
		firePropertyChange(EOEntity.PARENT_CLASS_NAME, oldParentClassName, myParentClassName);
	}

	public String getExternalName() {
		return myExternalName;
	}

	public void guessExternalNameInModel(EOModel model) {
		String externalName = getName();
		if (model != null) {
			EOEntity otherEntity = model._getTemplateNameEntity();
			if (otherEntity != null) {
				String otherName = otherEntity.getName();
				String otherExternalName = otherEntity.getExternalName();
				String guessedExternalName = NameSyncUtils.newDependentName(otherName, getName(), otherExternalName, model.getEntityExternalNamePairs(null));
				if (!ComparisonUtils.equals(guessedExternalName, otherExternalName)) {
					externalName = guessedExternalName;
				}
			}
		}
		setExternalName(externalName);
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

	public Set<EOModelReferenceFailure> getReferenceFailures() {
		Set<EOModelReferenceFailure> referenceFailures = new HashSet<EOModelReferenceFailure>();
		for (EOEntity referencingEntity : getChildrenEntities()) {
			referenceFailures.add(new EOEntityParentReferenceFailure(referencingEntity, this));
		}

		for (EORelationship referencingRelationship : getReferencingRelationships()) {
			referenceFailures.add(new EOEntityRelationshipReferenceFailure(referencingRelationship, this));
		}
		return referenceFailures;
	}

	public Set<EOEntity> getReferencingEntities() {
		Set<EOEntity> referencingEntities = new PropertyListSet<EOEntity>();
		for (EORelationship relationship : getReferencingRelationships()) {
			referencingEntities.add(relationship.getEntity());
		}
		return referencingEntities;
	}
	
	public Set<EORelationship> getReferencingRelationships() {
		Set<EORelationship> referencingRelationships = new HashSet<EORelationship>();
		for (EOModel model : getModel().getModelGroup().getModels()) {
			for (EOEntity entity : model.getEntities()) {
				if (!entity.equals(this)) {
					for (EORelationship relationship : entity.getRelationships()) {
						if (relationship.isRelatedTo(this)) {
							referencingRelationships.add(relationship);
						}
					}
				}
			}
		}
		return referencingRelationships;
	}

	public Set<EOEntity> getChildrenEntities() {
		Set<EOEntity> children = new PropertyListSet<EOEntity>();
		if (myModel != null) {
			for (EOModel model : getModel().getModelGroup().getModels()) {
				for (EOEntity entity : model.getEntities()) {
					if (entity.getParent() == this) {
						children.add(entity);
					}
				}
			}
		}
		return children;
	}

	public boolean isParentSet() {
		return myParent != null;
	}

	public EOEntity getParent() {
		return myParent;
	}

	public void setParent(EOEntity _parent) {
		EOEntity oldParent = myParent;
		myParent = _parent;
		firePropertyChange(EOEntity.PARENT, oldParent, myParent);
	}

	public boolean isPartialBase() {
		for (EOModel model : getModel().getModelGroup().getModels()) {
			for (EOEntity entity : model.getEntities()) {
				if (entity.getPartialEntity() == this) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPartialEntitySet() {
		return myPartialEntity != null;
	}

	public EOEntity getPartialEntity() {
		return myPartialEntity;
	}

	public void setPartialEntity(EOEntity partialEntity) {
		EOEntity oldPartialEntity = myPartialEntity;
		myPartialEntity = partialEntity;
		firePropertyChange(EOEntity.PARTIAL_ENTITY, oldPartialEntity, myPartialEntity);
	}

	public boolean isGenerateSource() {
		return myGenerateSource;
	}

	public void setGenerateSource(boolean generateSource) {
		boolean oldGenerateSource = myGenerateSource;
		myGenerateSource = generateSource;
		firePropertyChange(EOEntity.GENERATE_SOURCE, oldGenerateSource, myGenerateSource);
	}

	public void inheritParentAttributesAndRelationships(Set<EOModelVerificationFailure> failures, boolean warnOnly) throws DuplicateNameException {
		EOEntity parent = getParent();
		if (parent != null) {
			if (parent.getModel() == getModel()) {
				parent.inheritParentAttributesAndRelationships(failures, warnOnly);
			}
			if (isVerticalInheritance()) {
				// MS: Need to do this
			} else {
				_cloneAttributesAndRelationshipsFrom(parent, true, failures, warnOnly);
			}
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
				verticalInheritance = ComparisonUtils.equals(relationship.getDestination(), parent) && (relationship.getClassProperty() == null || !relationship.getClassProperty().booleanValue());
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

	public Set<String> getRestrictingQualifierKeys() {
		Set<String> restrictingQualifierKeys;
		if (myRestrictingQualifier == null) {
			restrictingQualifierKeys = new HashSet<String>();
		} else {
			restrictingQualifierKeys = EOQualifierFactory.getQualifierKeysFromQualifierString(myRestrictingQualifier);
		}
		return restrictingQualifierKeys;
	}

	public void clearCachedPrototypes(Set<EOModelVerificationFailure> _failures, boolean _reload) {
		for (EOAttribute attribute : myAttributes) {
			attribute.clearCachedPrototype(_failures, _reload);
		}
	}

	public Set<EOAttribute> getPrimaryKeyAttributes() {
		Set<EOAttribute> primaryKeyAttributes = new HashSet<EOAttribute>();
		for (EOAttribute attribute : myAttributes) {
			Boolean primaryKey = attribute.isPrimaryKey();
			if (BooleanUtils.isTrue(primaryKey)) {
				primaryKeyAttributes.add(attribute);
			}
		}
		return primaryKeyAttributes;
	}

	public void setAttributes(Set<EOAttribute> _attributes) {
		myAttributes = _attributes;
		firePropertyChange(EOEntity.ATTRIBUTES, null, null);
	}

	public Set<EOAttribute> getClientClassAttributes() {
		Set<EOAttribute> clientClassAttributes = new HashSet<EOAttribute>();
		for (EOAttribute attribute : getAttributes()) {
			if (attribute.isClientClassProperty() != null && attribute.isClientClassProperty().booleanValue()) {
				clientClassAttributes.add(attribute);
			}
		}
		return clientClassAttributes;
	}

	public Set<EOAttribute> getSortedClientClassAttributes() {
		return new PropertyListSet<EOAttribute>(getClientClassAttributes());
	}

	public Set<EOAttribute> getCommonClassAttributes() {
		Set<EOAttribute> commonClassAttributes = new HashSet<EOAttribute>();
		for (EOAttribute attribute : getAttributes()) {
			if (attribute.isCommonClassProperty() != null && attribute.isCommonClassProperty().booleanValue()) {
				commonClassAttributes.add(attribute);
			}
		}
		return commonClassAttributes;
	}

	public Set<EOAttribute> getSortedCommonClassAttributes() {
		return new PropertyListSet<EOAttribute>(getCommonClassAttributes());
	}

	public Set<EOAttribute> getClassAttributes() {
		Set<EOAttribute> classAttributes = new HashSet<EOAttribute>();
		for (EOAttribute attribute : getAttributes()) {
			if (attribute.isClassProperty() != null && attribute.isClassProperty().booleanValue()) {
				classAttributes.add(attribute);
			}
		}
		return classAttributes;
	}

	public Set<EOAttribute> getSortedClassAttributes() {
		return new PropertyListSet<EOAttribute>(getClassAttributes());
	}

	public Set<EOAttribute> getNonClassAttributes() {
		Set<EOAttribute> nonClassAttributes = new HashSet<EOAttribute>();
		for (EOAttribute attribute : getAttributes()) {
			if (attribute.isClassProperty() == null || !attribute.isClassProperty().booleanValue()) {
				nonClassAttributes.add(attribute);
			}
		}
		return nonClassAttributes;
	}

	public Set<EOAttribute> getInheritedAttributes() {
		Set<EOAttribute> inheritedAttributes = new HashSet<EOAttribute>();
		for (EOAttribute attribute : getAttributes()) {
			if (attribute.isInherited()) {
				inheritedAttributes.add(attribute);
			}
		}
		return inheritedAttributes;
	}

	public Set<EOAttribute> getAttributes() {
		return myAttributes;
	}

	public Set<EOAttribute> getSortedAttributes() {
		return new PropertyListSet<EOAttribute>(myAttributes);
	}

	public String[] getAttributeNames() {
		Set<EOAttribute> attributes = getAttributes();
		String[] attributeNames = new String[attributes.size()];
		Iterator<EOAttribute> attributeIter = attributes.iterator();
		for (int attributeNum = 0; attributeIter.hasNext(); attributeNum++) {
			EOAttribute attribute = attributeIter.next();
			attributeNames[attributeNum] = attribute.getName();
		}
		Arrays.sort(attributeNames);
		return attributeNames;
	}

	public Set<EORelationship> getClientClassRelationships() {
		Set<EORelationship> clientClassRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isClientClassProperty() != null && relationship.isClientClassProperty().booleanValue()) {
				clientClassRelationships.add(relationship);
			}
		}
		return clientClassRelationships;
	}

	public Set<EORelationship> getSortedClientClassRelationships() {
		return new PropertyListSet<EORelationship>(getClientClassRelationships());
	}

	public Set<EORelationship> getCommonClassRelationships() {
		Set<EORelationship> commonClassRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isCommonClassProperty() != null && relationship.isCommonClassProperty().booleanValue()) {
				commonClassRelationships.add(relationship);
			}
		}
		return commonClassRelationships;
	}

	public Set<EORelationship> getSortedCommonClassRelationships() {
		return new PropertyListSet<EORelationship>(getCommonClassRelationships());
	}

	public Set<EORelationship> getClassRelationships() {
		Set<EORelationship> classRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isClassProperty() != null && relationship.isClassProperty().booleanValue()) {
				classRelationships.add(relationship);
			}
		}
		return classRelationships;
	}

	public Set<EORelationship> getSortedClassRelationships() {
		return new PropertyListSet<EORelationship>(getClassRelationships());
	}

	public Set<EORelationship> getNonClassRelationships() {
		Set<EORelationship> nonClassRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isClassProperty() == null || !relationship.isClassProperty().booleanValue()) {
				nonClassRelationships.add(relationship);
			}
		}
		return nonClassRelationships;
	}

	public Set<EORelationship> getInheritedRelationships() {
		Set<EORelationship> inheritedRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isInherited()) {
				inheritedRelationships.add(relationship);
			}
		}
		return inheritedRelationships;
	}

	public Set<EORelationship> getToOneRelationships() {
		Set<EORelationship> toOneRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToOne() != null && relationship.isToOne().booleanValue()) {
				toOneRelationships.add(relationship);
			}
		}
		return toOneRelationships;
	}

	public Set<EORelationship> getSortedToOneRelationships() {
		return new PropertyListSet<EORelationship>(getToOneRelationships());
	}

	public Set<EORelationship> getClassToOneRelationships() {
		Set<EORelationship> toOneRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToOne() != null && relationship.isToOne().booleanValue() && relationship.isClassProperty() != null && relationship.isClassProperty()) {
				toOneRelationships.add(relationship);
			}
		}
		return toOneRelationships;
	}

	public Set<EORelationship> getSortedClassToOneRelationships() {
		return new PropertyListSet<EORelationship>(getClassToOneRelationships());
	}

	public Set<EORelationship> getSortedClientClassToOneRelationships() {
		return new PropertyListSet<EORelationship>(getClientClassToOneRelationships());
	}

	public Set<EORelationship> getClientClassToOneRelationships() {
		Set<EORelationship> toOneRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToOne() != null && relationship.isToOne().booleanValue() && relationship.isClientClassProperty() != null && relationship.isClientClassProperty()) {
				toOneRelationships.add(relationship);
			}
		}
		return toOneRelationships;
	}

	public Set<EORelationship> getSortedCommonClassToOneRelationships() {
		return new PropertyListSet<EORelationship>(getCommonClassToOneRelationships());
	}

	public Set<EORelationship> getCommonClassToOneRelationships() {
		Set<EORelationship> toOneRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToOne() != null && relationship.isToOne().booleanValue() && relationship.isCommonClassProperty() != null && relationship.isCommonClassProperty()) {
				toOneRelationships.add(relationship);
			}
		}
		return toOneRelationships;
	}

	public Set<EORelationship> getToManyRelationships() {
		Set<EORelationship> toManyRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToMany() != null && relationship.isToMany().booleanValue()) {
				toManyRelationships.add(relationship);
			}
		}
		return toManyRelationships;
	}

	public Set<EORelationship> getSortedToManyRelationships() {
		return new PropertyListSet<EORelationship>(getToManyRelationships());
	}

	public Set<EORelationship> getClassToManyRelationships() {
		Set<EORelationship> toManyRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToMany() != null && relationship.isToMany().booleanValue() && relationship.isClassProperty() != null && relationship.isClassProperty()) {
				toManyRelationships.add(relationship);
			}
		}
		return toManyRelationships;
	}

	public Set<EORelationship> getSortedClassToManyRelationships() {
		return new PropertyListSet<EORelationship>(getClassToManyRelationships());
	}

	public Set<EORelationship> getClientClassToManyRelationships() {
		Set<EORelationship> toManyRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToMany() != null && relationship.isToMany().booleanValue() && relationship.isClientClassProperty() != null && relationship.isClientClassProperty()) {
				toManyRelationships.add(relationship);
			}
		}
		return toManyRelationships;
	}

	public Set<EORelationship> getSortedClientClassToManyRelationships() {
		return new PropertyListSet<EORelationship>(getClientClassToManyRelationships());
	}

	public Set<EORelationship> getCommonClassToManyRelationships() {
		Set<EORelationship> toManyRelationships = new HashSet<EORelationship>();
		for (EORelationship relationship : getRelationships()) {
			if (relationship.isToMany() != null && relationship.isToMany().booleanValue() && relationship.isCommonClassProperty() != null && relationship.isCommonClassProperty()) {
				toManyRelationships.add(relationship);
			}
		}
		return toManyRelationships;
	}

	public Set<EORelationship> getSortedCommonClassToManyRelationships() {
		return new PropertyListSet<EORelationship>(getCommonClassToManyRelationships());
	}

	public Set<EORelationship> getRelationships() {
		return myRelationships;
	}

	public Set<EORelationship> getSortedRelationships() {
		return new PropertyListSet<EORelationship>(myRelationships);
	}

	public Set<EOFetchSpecification> getFetchSpecs() {
		return myFetchSpecs;
	}

	public Set<EOFetchSpecification> getSortedFetchSpecs() {
		return new PropertyListSet<EOFetchSpecification>(myFetchSpecs);
	}

	public Set<EOEntityIndex> getEntityIndexes() {
		return myEntityIndexes;
	}

	public Set<EOEntityIndex> getSortedEntityIndexes() {
		return new PropertyListSet<EOEntityIndex>(myEntityIndexes);
	}

	public String findUnusedAttributeName(String _newName) {
		return _findUnusedName(_newName, "getAttributeOrRelationshipNamed");
	}

	public IEOAttribute getAttributeOrRelationshipNamed(String _name) {
		IEOAttribute attribute = getAttributeNamed(_name);
		if (attribute == null) {
			attribute = getRelationshipNamed(_name);
		}
		return attribute;
	}

	public void _checkForDuplicateAttributeName(EOAttribute _attribute, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
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

	public void addAttribute(EOAttribute _attribute) throws DuplicateNameException {
		addAttribute(_attribute, true, null);
	}

	public synchronized void addAttribute(EOAttribute _attribute, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		_attribute._setEntity(this);
		_checkForDuplicateAttributeName(_attribute, _attribute.getName(), _failures);
		_attribute.pasted();
		Set<EOAttribute> oldAttributes = null;
		if (_fireEvents) {
			oldAttributes = myAttributes;
			Set<EOAttribute> newAttributes = new HashSet<EOAttribute>();
			newAttributes.addAll(myAttributes);
			newAttributes.add(_attribute);
			myAttributes = newAttributes;
			if (myModel != null) {
				myModel.getModelEvents().addEvent(new EOAttributeAddedEvent(_attribute));
			}
			firePropertyChange(EOEntity.ATTRIBUTES, oldAttributes, myAttributes);
		} else {
			myAttributes.add(_attribute);
		}
	}

	public void removeAttribute(EOAttribute _attribute, boolean _removeFromSubclasses) {
		String attributeName = _attribute.getName();
		Set<EOAttribute> oldAttributes = myAttributes;
		Set<EOAttribute> newAttributes = new HashSet<EOAttribute>();
		newAttributes.addAll(myAttributes);
		newAttributes.remove(_attribute);
		myAttributes = newAttributes;
		if (myModel != null) {
			myModel.getModelEvents().addEvent(new EOAttributeDeletedEvent(_attribute));
		}
		firePropertyChange(EOEntity.ATTRIBUTES, oldAttributes, newAttributes);
		if (_removeFromSubclasses) {
			for (EOEntity childEntity : getChildrenEntities()) {
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
		Iterator<EOAttribute> attributesIter = myAttributes.iterator();
		while (matchingAttribute == null && attributesIter.hasNext()) {
			EOAttribute attribute = attributesIter.next();
			if (ComparisonUtils.equals(attribute.getName(), _name)) {
				matchingAttribute = attribute;
			}
		}
		return matchingAttribute;
	}

	public String findUnusedRelationshipName(String _newName) {
		return _findUnusedName(_newName, "getAttributeOrRelationshipNamed");
	}

	public void _checkForDuplicateRelationshipName(EORelationship _relationship, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
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

	@SuppressWarnings("unused")
	protected void _attributeChanged(EOAttribute _attribute, String _propertyName, Object _oldValue, Object _newValue) {
		myAttributes = new HashSet<EOAttribute>(myAttributes);
		// firePropertyChange(EOEntity.ATTRIBUTE + "." + _propertyName, new
		// ProxyChange(_attribute, _oldValue), new ProxyChange(_attribute,
		// _newValue));
		firePropertyChange(EOEntity.ATTRIBUTE, null, _attribute);
	}

	@SuppressWarnings("unused")
	protected void _relationshipChanged(EORelationship _relationship, String _propertyName, Object _oldValue, Object _newValue) {
		myRelationships = new HashSet<EORelationship>(myRelationships);
		// firePropertyChange(EOEntity.RELATIONSHIP + "." + _propertyName, new
		// ProxyChange(_relationship, _oldValue), new ProxyChange(_relationship,
		// _newValue));
		firePropertyChange(EOEntity.RELATIONSHIP, null, _relationship);
	}

	@SuppressWarnings("unused")
	protected void _fetchSpecificationChanged(EOFetchSpecification _fetchSpecification, String _propertyName, Object _oldValue, Object _newValue) {
		myFetchSpecs = new HashSet<EOFetchSpecification>(myFetchSpecs);
		// firePropertyChange(EOEntity.FETCH_SPECIFICATION + "." +
		// _propertyName, new ProxyChange(_fetchSpecification, _oldValue), new
		// ProxyChange(_fetchSpecification, _newValue));
		firePropertyChange(EOEntity.FETCH_SPECIFICATION, null, _fetchSpecification);
	}

	@SuppressWarnings("unused")
	protected void _entityIndexChanged(EOEntityIndex _entityIndex, String _propertyName, Object _oldValue, Object _newValue) {
		myEntityIndexes = new HashSet<EOEntityIndex>(myEntityIndexes);
		// firePropertyChange(EOEntity.ENTITY_INDEX + "." + _propertyName, new
		// ProxyChange(_entityIndex, _oldValue), new ProxyChange(_entityIndex,
		// _newValue));
		firePropertyChange(EOEntity.ENTITY_INDEX, null, _entityIndex);
	}

	public void addRelationship(EORelationship relationship) throws DuplicateNameException {
		addRelationship(relationship, true, null, true);
	}

	public void addRelationship(EORelationship relationship, boolean pasteImmediately, Set<EOModelVerificationFailure> failures, boolean fireEvents) throws DuplicateNameException {
		relationship._setEntity(this);
		_checkForDuplicateRelationshipName(relationship, relationship.getName(), failures);
		if (pasteImmediately) {
			relationship.pasted();
		}
		Set<EORelationship> oldRelationships = null;
		if (fireEvents) {
			oldRelationships = myRelationships;
			Set<EORelationship> newRelationships = new HashSet<EORelationship>();
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
		Set<EORelationship> oldRelationships = myRelationships;
		Set<EORelationship> newRelationships = new HashSet<EORelationship>();
		newRelationships.addAll(myRelationships);
		newRelationships.remove(_relationship);
		myRelationships = newRelationships;
		firePropertyChange(EOEntity.RELATIONSHIPS, oldRelationships, newRelationships);
		if (_removeFromSubclasses) {
			for (EOEntity childEntity : getChildrenEntities()) {
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
		Iterator<EORelationship> relationshipsIter = myRelationships.iterator();
		while (matchingRelationship == null && relationshipsIter.hasNext()) {
			EORelationship relationship = relationshipsIter.next();
			if (ComparisonUtils.equals(relationship.getName(), _name)) {
				matchingRelationship = relationship;
			}
		}
		return matchingRelationship;
	}

	public EOFetchSpecification getFetchSpecNamed(String _name) {
		EOFetchSpecification matchingFetchSpec = null;
		Iterator<EOFetchSpecification> fetchSpecsIter = myFetchSpecs.iterator();
		while (matchingFetchSpec == null && fetchSpecsIter.hasNext()) {
			EOFetchSpecification fetchSpec = fetchSpecsIter.next();
			if (ComparisonUtils.equals(fetchSpec.getName(), _name)) {
				matchingFetchSpec = fetchSpec;
			}
		}
		return matchingFetchSpec;
	}

	public String findUnusedFetchSpecificationName(String _newName) {
		return _findUnusedName(_newName, "getFetchSpecNamed");
	}

	public void _checkForDuplicateFetchSpecName(EOFetchSpecification _fetchSpec, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateFetchSpecNameException {
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

	public void addFetchSpecification(EOFetchSpecification _fetchSpecification, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateFetchSpecNameException {
		_fetchSpecification._setEntity(this);
		_checkForDuplicateFetchSpecName(_fetchSpecification, _fetchSpecification.getName(), _failures);
		Set<EOFetchSpecification> oldFetchSpecs = null;
		if (_fireEvents) {
			oldFetchSpecs = myFetchSpecs;
			Set<EOFetchSpecification> newFetchSpecs = new HashSet<EOFetchSpecification>();
			newFetchSpecs.addAll(myFetchSpecs);
			newFetchSpecs.add(_fetchSpecification);
			myFetchSpecs = newFetchSpecs;
			firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, oldFetchSpecs, myFetchSpecs);
		} else {
			myFetchSpecs.add(_fetchSpecification);
		}
	}

	public void removeFetchSpecification(EOFetchSpecification _fetchSpecification) {
		Set<EOFetchSpecification> oldFetchSpecs = myFetchSpecs;
		Set<EOFetchSpecification> newFetchSpecs = new HashSet<EOFetchSpecification>();
		newFetchSpecs.addAll(myFetchSpecs);
		newFetchSpecs.remove(_fetchSpecification);
		myFetchSpecs = newFetchSpecs;
		firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, oldFetchSpecs, newFetchSpecs);
	}

	public EOEntityIndex getEntityIndexNamed(String _name) {
		EOEntityIndex matchingEntityIndex = null;
		Iterator<EOEntityIndex> entityIndexesIter = myEntityIndexes.iterator();
		while (matchingEntityIndex == null && entityIndexesIter.hasNext()) {
			EOEntityIndex entityIndex = entityIndexesIter.next();
			if (ComparisonUtils.equals(entityIndex.getName(), _name)) {
				matchingEntityIndex = entityIndex;
			}
		}
		return matchingEntityIndex;
	}

	public String findUnusedEntityIndexName(String _newName) {
		return _findUnusedName(_newName, "getEntityIndexNamed");
	}

	public void _checkForDuplicateEntityIndexName(EOEntityIndex _entityIndex, String _newName, Set<EOModelVerificationFailure> _failures) throws DuplicateEntityIndexNameException {
		EOEntityIndex existingEntityIndex = getEntityIndexNamed(_newName);
		if (existingEntityIndex != null && existingEntityIndex != _entityIndex) {
			if (_failures == null) {
				throw new DuplicateEntityIndexNameException(_newName, this);
			}

			String unusedName = findUnusedEntityIndexName(_newName);
			existingEntityIndex.setName(unusedName, true);
			_failures.add(new DuplicateEntityIndexFailure(this, _newName, unusedName));
		}
	}

	public EOEntityIndex addEntityIndex(List<EOAttribute> attributes) throws DuplicateEntityIndexNameException {
		StringBuffer nameBuffer = new StringBuffer();
		for (EOAttribute attribute : attributes) {
			nameBuffer.append(attribute.getName());
			nameBuffer.append("_");
		}
		nameBuffer.append("idx");
		String name = nameBuffer.toString();
		EOEntityIndex entityIndex = addBlankEntityIndex(name);
		for (EOAttribute attribute : attributes) {
			entityIndex.addAttribute(attribute);
		}
		return entityIndex;
	}

	public EOEntityIndex addBlankEntityIndex(String _name) throws DuplicateEntityIndexNameException {
		String newEntityIndexName = findUnusedEntityIndexName(_name);
		EOEntityIndex entityIndex = new EOEntityIndex();
		entityIndex.setName(newEntityIndexName, false);
		addEntityIndex(entityIndex);
		return entityIndex;
	}

	public void addEntityIndex(EOEntityIndex _entityIndex) throws DuplicateEntityIndexNameException {
		addEntityIndex(_entityIndex, true, null);
	}

	public void addEntityIndex(EOEntityIndex _entityIndex, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateEntityIndexNameException {
		_entityIndex._setEntity(this);
		_checkForDuplicateEntityIndexName(_entityIndex, _entityIndex.getName(), _failures);
		Set<EOEntityIndex> oldEntityIndexes = null;
		if (_fireEvents) {
			oldEntityIndexes = myEntityIndexes;
			Set<EOEntityIndex> newEntityIndexes = new HashSet<EOEntityIndex>();
			newEntityIndexes.addAll(myEntityIndexes);
			newEntityIndexes.add(_entityIndex);
			myEntityIndexes = newEntityIndexes;
			firePropertyChange(EOEntity.ENTITY_INDEXES, oldEntityIndexes, myEntityIndexes);
		} else {
			myEntityIndexes.add(_entityIndex);
		}
	}

	public void removeEntityIndex(EOEntityIndex _entityIndex) {
		Set<EOEntityIndex> oldEntityIndexes = myEntityIndexes;
		Set<EOEntityIndex> newEntityIndexes = new HashSet<EOEntityIndex>();
		newEntityIndexes.addAll(myEntityIndexes);
		newEntityIndexes.remove(_entityIndex);
		myEntityIndexes = newEntityIndexes;
		firePropertyChange(EOEntity.ENTITY_INDEXES, oldEntityIndexes, newEntityIndexes);
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

	public void loadFromMap(EOModelMap _entityMap, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		myEntityMap = _entityMap;
		myName = _entityMap.getString("name", true);
		myOriginalName = myName;
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

		Set<Map> attributeList = _entityMap.getSet("attributes");
		if (attributeList != null) {
			Iterator<Map> attributeIter = attributeList.iterator();
			while (attributeIter.hasNext()) {
				EOModelMap attributeMap = new EOModelMap(attributeIter.next());
				EOAttribute attribute = new EOAttribute();
				attribute.loadFromMap(attributeMap, _failures);
				addAttribute(attribute, false, _failures);
			}
		}

		Set<Map> relationshipList = _entityMap.getSet("relationships");
		if (relationshipList != null) {
			Iterator<Map> relationshipIter = relationshipList.iterator();
			while (relationshipIter.hasNext()) {
				EOModelMap relationshipMap = new EOModelMap(relationshipIter.next());
				EORelationship relationship = new EORelationship();
				relationship.loadFromMap(relationshipMap, _failures);
				addRelationship(relationship, true, _failures, false);
			}
		}

		Set<Map> entityIndexesList = _entityMap.getSet("entityIndexes");
		if (entityIndexesList != null) {
			Iterator<Map> entityIndexIter = entityIndexesList.iterator();
			while (entityIndexIter.hasNext()) {
				EOModelMap entityIndexMap = new EOModelMap(entityIndexIter.next());
				EOEntityIndex entityIndex = new EOEntityIndex();
				entityIndex.loadFromMap(entityIndexMap, _failures);
				addEntityIndex(entityIndex, true, _failures);
			}
		}

		Set<String> attributesUsedForLocking = _entityMap.getSet("attributesUsedForLocking");
		if (attributesUsedForLocking != null) {
			Iterator<String> attributesUsedForLockingIter = attributesUsedForLocking.iterator();
			while (attributesUsedForLockingIter.hasNext()) {
				String attributeName = attributesUsedForLockingIter.next();
				EOAttribute attribute = getAttributeNamed(attributeName);
				if (attribute != null) {
					attribute.setUsedForLocking(Boolean.TRUE, false);
				}
			}
		}
	}

	public void loadFetchSpecsFromMap(EOModelMap _map, Set<EOModelVerificationFailure> _failures) throws EOModelException {
		myFetchSpecsMap = _map;
		Set<String> sharedObjectFetchSpecificationNames = myEntityMap.getSet("sharedObjectFetchSpecificationNames");

		if (_map != null && !_map.isEmpty()) {
			Set<Map.Entry<Object, Map>> fetchSpecEntries = _map.entrySet();
			for (Map.Entry<Object, Map> fetchSpecEntry : fetchSpecEntries) {
				_addFetchSpecificationFromMap(fetchSpecEntry, _failures, sharedObjectFetchSpecificationNames);
			}
		}

		Map<Object, Map> fetchSpecificationsDictionary = myEntityMap.getMap("fetchSpecificationDictionary");
		if (fetchSpecificationsDictionary != null && !fetchSpecificationsDictionary.isEmpty()) {
			for (Map.Entry<Object, Map> fetchSpecEntry : fetchSpecificationsDictionary.entrySet()) {
				_addFetchSpecificationFromMap(fetchSpecEntry, _failures, sharedObjectFetchSpecificationNames);
			}
		}
	}

	protected void _addFetchSpecificationFromMap(Map.Entry<Object, Map> _fetchSpecEntry, Set<EOModelVerificationFailure> _failures, Set<String> _sharedObjectFetchSpecificationNames) throws EOModelException {
		String fetchSpecName = _fetchSpecEntry.getKey().toString();
		EOModelMap fetchSpecMap = new EOModelMap(_fetchSpecEntry.getValue());
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
		} else {
			entityMap.remove("parent");
		}
		entityMap.setBoolean("cachesObjects", myCachesObjects, EOModelMap.YNOptionalDefaultNo);
		entityMap.setBoolean("isAbstractEntity", myAbstractEntity, EOModelMap.YNOptionalDefaultNo);
		entityMap.remove("isFetchable");
		entityMap.setBoolean("isReadOnly", myReadOnly, EOModelMap.YNOptionalDefaultNo);
		entityMap.setString("restrictingQualifier", myRestrictingQualifier, true);
		entityMap.remove("mappingQualifier");
		entityMap.setString("externalQuery", myExternalQuery, true);
		entityMap.setInteger("maxNumberOfInstancesToBatchFetch", myMaxNumberOfInstancesToBatchFetch);

		if (myFetchSpecs == null || myFetchSpecs.size() == 0) {
			entityMap.put("fetchSpecificationDictionary", new HashMap());
			// prevents EOF from hitting the filesystem to find out there are no fetch specs
		} else {
			entityMap.remove("fetchSpecificationDictionary");
		}

		Set<String> classProperties = new PropertyListSet<String>(EOModelMap.asArray(myEntityMap.get("classProperties")));
		Set<String> primaryKeyAttributes = new PropertyListSet<String>(EOModelMap.asArray(myEntityMap.get("primaryKeyAttributes")));
		Set<String> attributesUsedForLocking = new PropertyListSet<String>(EOModelMap.asArray(myEntityMap.get("attributesUsedForLocking")));

		Map<Object, Object> oldInternalInfo = myEntityMap.getMap("internalInfo");
		Set<String> clientClassProperties = new PropertyListSet<String>(oldInternalInfo != null ? EOModelMap.asArray(oldInternalInfo.get("_clientClassPropertyNames")) : null);
		Set<Map> attributes = new PropertyListSet<Map>(EOModelMap.asArray(myEntityMap.get("attributes")));
		for (EOAttribute attribute : myAttributes) {
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

		Set<Map> relationships = new PropertyListSet<Map>(EOModelMap.asArray(myEntityMap.get("relationships")));
		for (EORelationship relationship : myRelationships) {
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

		Set<Map> entityIndexes = new PropertyListSet<Map>(EOModelMap.asArray(myEntityMap.get("entityIndexes")));
		for (EOEntityIndex entityIndex : myEntityIndexes) {
			EOModelMap entityIndexMap = entityIndex.toMap();
			entityIndexes.add(entityIndexMap);
		}
		entityMap.setSet("entityIndexes", entityIndexes, true);

		entityMap.setSet("attributesUsedForLocking", attributesUsedForLocking, true);
		entityMap.setSet("classProperties", classProperties, true);
		entityMap.setSet("primaryKeyAttributes", primaryKeyAttributes, true);

		Set<String> sharedObjectFetchSpecificationNames = new PropertyListSet<String>(EOModelMap.asArray(myEntityMap.get("sharedObjectFetchSpecificationNames")));
		for (EOFetchSpecification fetchSpec : myFetchSpecs) {
			if (BooleanUtils.isTrue(fetchSpec.isSharesObjects())) {
				sharedObjectFetchSpecificationNames.add(fetchSpec.getName());
			}
		}
		entityMap.setSet("sharedObjectFetchSpecificationNames", sharedObjectFetchSpecificationNames, true);

		Map<Object, Object> internalInfoMap = entityMap.getMap("internalInfo");
		if (internalInfoMap == null) {
			internalInfoMap = new HashMap<Object, Object>();
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

		if (!internalInfoMap.isEmpty()) {
			entityMap.setMap("internalInfo", internalInfoMap, false);
		} else {
			entityMap.remove("internalInfo");
		}

		Map<String, String> storedProcedureNames = myEntityMap.getMap("storedProcedureNames");
		if (storedProcedureNames == null) {
			storedProcedureNames = new HashMap<String, String>();
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

		EOModelMap entityModelerMap = new EOModelMap((Map) getUserInfo().get(UserInfoableEOModelObject.ENTITY_MODELER_KEY));
		if (myPartialEntity == null) {
			entityModelerMap.remove(EOEntity.PARTIAL_ENTITY);
		} else {
			entityModelerMap.put(EOEntity.PARTIAL_ENTITY, myPartialEntity.getName());
		}
		if (myParentClassName == null) {
			entityModelerMap.remove(EOEntity.PARENT_CLASS_NAME);
		}
		else {
			entityModelerMap.put(EOEntity.PARENT_CLASS_NAME, myParentClassName);
		}
		if (myGenerateSource) {
			entityModelerMap.remove(EOEntity.GENERATE_SOURCE);
		} else {
			entityModelerMap.setBoolean(EOEntity.GENERATE_SOURCE, Boolean.FALSE, EOModelMap.YESNO);
		}
		if (entityModelerMap.isEmpty()) {
			getUserInfo().remove(UserInfoableEOModelObject.ENTITY_MODELER_KEY);
		} else {
			getUserInfo().put(UserInfoableEOModelObject.ENTITY_MODELER_KEY, entityModelerMap);
		}

		writeUserInfo(entityMap);

		return entityMap;
	}

	public EOModelMap toFetchSpecsMap() {
		EOModelMap fetchSpecsMap = myFetchSpecsMap.cloneModelMap();
		fetchSpecsMap.clear();
		for (EOFetchSpecification fetchSpec : myFetchSpecs) {
			EOModelMap fetchSpecMap = fetchSpec.toMap();
			fetchSpecsMap.setMap(fetchSpec.getName(), fetchSpecMap, true);
		}
		return fetchSpecsMap;
	}

	public void loadFromURL(URL entityURL, Set<EOModelVerificationFailure> failures) throws EOModelException {
		try {
			EOModelMap entityMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromURL(entityURL, new EOModelParserDataStructureFactory()));
			loadFromMap(entityMap, failures);
		} catch (Throwable e) {
			throw new EOModelException("Failed to load entity from '" + entityURL.getFile() + "'.", e);
		}
	}

	public void loadFetchSpecsFromURL(URL fetchSpecURL, Set<EOModelVerificationFailure> failures) throws EOModelException {
		try {
			EOModelMap fspecMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromURL(fetchSpecURL, new EOModelParserDataStructureFactory()));
			loadFetchSpecsFromMap(fspecMap, failures);
		} catch (Throwable e) {
			throw new EOModelException("Failed to load fetch specifications from '" + fetchSpecURL.getFile() + "'.", e);
		}
	}

	public void saveToFile(File _entityFile) throws PropertyListParserException, IOException {
		EOModelMap entityMap = toEntityMap();
		PropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, _entityFile, entityMap);
	}

	public void saveFetchSpecsToFile(File _fetchSpecFile) throws PropertyListParserException, IOException {
		if (myFetchSpecs.size() == 0) {
			_fetchSpecFile.delete();
		} else {
			EOModelMap fetchSpecMap = toFetchSpecsMap();
			PropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, _fetchSpecFile, fetchSpecMap);
		}
	}

	public void resolveFlattened(Set<EOModelVerificationFailure> _failures) {
		for (EOAttribute attribute : myAttributes) {
			if (attribute.isFlattened()) {
				attribute.resolve(_failures);
			}
		}

		for (EORelationship relationship : myRelationships) {
			if (relationship.isFlattened()) {
				relationship.resolve(_failures);
			}
		}
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		String parentName = myEntityMap.getString("parent", true);
		if (parentName != null) {
			if (myModel != null) {
				myParent = myModel.getModelGroup().getEntityNamed(parentName);
			}
			if (myParent == null) {
				_failures.add(new MissingEntityFailure(myModel, parentName));
			}
		}

		EOModelMap entityModelerMap = new EOModelMap((Map) getUserInfo().get(UserInfoableEOModelObject.ENTITY_MODELER_KEY));
		String partialEntityName = entityModelerMap.getString(EOEntity.PARTIAL_ENTITY, true);
		if (partialEntityName != null) {
			if (myModel != null) {
				myPartialEntity = myModel.getModelGroup().getEntityNamed(partialEntityName);
			}
			if (myPartialEntity == null) {
				_failures.add(new MissingEntityFailure(myModel, partialEntityName));
			}
		}
		String parentClassName = entityModelerMap.getString(EOEntity.PARENT_CLASS_NAME, true);
		if (parentClassName != null) {
			myParentClassName = parentClassName;
		}
		Boolean generateSource = entityModelerMap.getBoolean(EOEntity.GENERATE_SOURCE);
		if (generateSource == null) {
			myGenerateSource = true;
		} else {
			myGenerateSource = generateSource.booleanValue();
		}

		for (EOAttribute attribute : myAttributes) {
			attribute.resolve(_failures);
		}

		for (EORelationship relationship : myRelationships) {
			relationship.resolve(_failures);
		}

		for (EOFetchSpecification fetchSpec : myFetchSpecs) {
			fetchSpec.resolve(_failures);
		}

		for (EOEntityIndex entityIndex : myEntityIndexes) {
			entityIndex.resolve(_failures);
		}

		Set<String> classProperties = myEntityMap.getSet("classProperties");
		if (classProperties != null) {
			for (String attributeName : classProperties) {
				IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
				if (attribute != null) {
					attribute.setClassProperty(Boolean.TRUE, false);
				}
			}
		}

		Set<String> primaryKeyAttributes = myEntityMap.getSet("primaryKeyAttributes");
		if (primaryKeyAttributes != null) {
			for (String attributeName : primaryKeyAttributes) {
				EOAttribute attribute = getAttributeNamed(attributeName);
				if (attribute != null) {
					attribute.setPrimaryKey(Boolean.TRUE, false);
				}
			}
		}

		Map<Object, Object> internalInfo = myEntityMap.getMap("internalInfo");
		if (internalInfo != null) {
			EOModelMap internalInfoModelMap = new EOModelMap(internalInfo);
			Set<String> clientClassPropertyNames = internalInfoModelMap.getSet("_clientClassPropertyNames");
			if (clientClassPropertyNames != null) {
				for (String attributeName : clientClassPropertyNames) {
					IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
					if (attribute != null) {
						attribute.setClientClassProperty(Boolean.TRUE, false);
					}
				}
			}

			Set<String> commonClassPropertyNames = internalInfoModelMap.getSet("_commonClassPropertyNames");
      if (commonClassPropertyNames != null) {
        for (String attributeName : commonClassPropertyNames) {
          IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
          if (attribute != null) {
            attribute.setCommonClassProperty(Boolean.TRUE, false);
          }
        }
      }
		}

		Map<String, String> storedProcedureNames = myEntityMap.getMap("storedProcedureNames");
		if (storedProcedureNames != null) {
			String deleteProcedureName = storedProcedureNames.get(EOEntity.EODELETE_PROCEDURE);
			if (deleteProcedureName != null) {
				myDeleteProcedure = myModel.getStoredProcedureNamed(deleteProcedureName);
				if (myDeleteProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s delete procedure '" + deleteProcedureName + "' is missing.", false));
				}
			}
			String fetchAllProcedureName = storedProcedureNames.get(EOEntity.EOFETCH_ALL_PROCEDURE);
			if (fetchAllProcedureName != null) {
				myFetchAllProcedure = myModel.getStoredProcedureNamed(fetchAllProcedureName);
				if (myFetchAllProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s fetch all procedure '" + fetchAllProcedureName + "' is missing.", false));
				}
			}
			String fetchWithPrimaryKeyProcedureName = storedProcedureNames.get(EOEntity.EOFETCH_WITH_PRIMARY_KEY_PROCEDURE);
			if (fetchWithPrimaryKeyProcedureName != null) {
				myFetchWithPrimaryKeyProcedure = myModel.getStoredProcedureNamed(fetchWithPrimaryKeyProcedureName);
				if (myFetchWithPrimaryKeyProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s fetch with primary key procedure '" + fetchWithPrimaryKeyProcedureName + "' is missing.", false));
				}
			}
			String insertProcedureName = storedProcedureNames.get(EOEntity.EOINSERT_PROCEDURE);
			if (insertProcedureName != null) {
				myInsertProcedure = myModel.getStoredProcedureNamed(insertProcedureName);
				if (myInsertProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s insert procedure '" + insertProcedureName + "' is missing.", false));
				}
			}
			String nextPrimaryKeyProcedureName = storedProcedureNames.get(EOEntity.EONEXT_PRIMARY_KEY_PROCEDURE);
			if (nextPrimaryKeyProcedureName != null) {
				myNextPrimaryKeyProcedure = myModel.getStoredProcedureNamed(nextPrimaryKeyProcedureName);
				if (myNextPrimaryKeyProcedure == null) {
					_failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s next primary key procedure '" + nextPrimaryKeyProcedureName + "' is missing.", false));
				}
			}
		}
	}

	public void verify(Set<EOModelVerificationFailure> failures, VerificationContext verificationContext) {
		String name = getName();
		if (name == null || name.trim().length() == 0) {
			failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + " has an empty name.", false));
		} else {
			if (name.indexOf(' ') != -1) {
				failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s name has a space in it.", false));
			}
			if (!StringUtils.isUppercaseFirstLetter(myName)) {
				failures.add(new EOModelVerificationFailure(myModel, this, "Entity names should be capitalized, but " + getName() + " is not.", true));
			}
		}

		for (EOAttribute attribute : myAttributes) {
			attribute.verify(failures, verificationContext);
		}

		for (EORelationship relationship : myRelationships) {
			relationship.verify(failures);
		}

		for (EOFetchSpecification fetchSpec : myFetchSpecs) {
			fetchSpec.verify(failures);
		}

		for (EOEntityIndex entityIndex : myEntityIndexes) {
			entityIndex.verify(failures);
		}

		if (!isPrototype()) {
			String externalName = getExternalName();
			if (externalName == null || externalName.trim().length() == 0) {
				if (!BooleanUtils.isTrue(isAbstractEntity())) {
					failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + " has an empty table name.", false));
				}
			} else if (externalName.indexOf(' ') != -1 && !externalName.startsWith("[") && !externalName.endsWith("]")) {
				failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + "'s table name '" + externalName + "' has a space in it.", false));
			}
		}

		EOEntity parent = getParent();
		if (parent != null && !BooleanUtils.isTrue(parent.isAbstractEntity()) && getRestrictingQualifier() == null && ComparisonUtils.equals(parent.getExternalName(), getExternalName())) {
			failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + " is a subclass of " + getParent().getName() + " but does not have a restricting qualifier.", false));
		}
		try {
			inheritParentAttributesAndRelationships(failures, false);
		} catch (DuplicateNameException e) {
			failures.add(new EOModelVerificationFailure(myModel, "Failed to fix inherited attributes and relationships for " + getName() + ".", true));
		}

		Set<EOAttribute> primaryKeyAttributes = getPrimaryKeyAttributes();
		if (primaryKeyAttributes.isEmpty() && !isPartialEntitySet()) {
			failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + " does not have a primary key.", false));
		}

		if (isPartialEntitySet() && getPartialEntity().isPartialEntitySet()) {
			failures.add(new EOModelVerificationFailure(myModel, this, "The entity " + getName() + " is a partial of an entity that is itself a partial. This is not currently allowed.", false));
		}
	}

	public String getFullyQualifiedName() {
		return ((myModel == null) ? "?" : myModel.getFullyQualifiedName()) + "/" + myName;
	}

	protected EOEntity _cloneJustEntity() {
		EOEntity entity = new EOEntity(myName);
		entity.myParent = myParent;
		entity.myPartialEntity = myPartialEntity;
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

	@Override
	public EOEntity _cloneModelObject() {
		try {
			EOEntity entity = _cloneJustEntity();
			entity._cloneAttributesAndRelationshipsFrom(this, false, null, false);
			entity._cloneFetchSpecificationsFrom(this, false);
			entity._cloneEntityIndexesFrom(this, false);
			_cloneUserInfoInto(entity);
			return entity;
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
		getModel().removeEntity(this);
	}

	public Set<NamePair> getAttributeColumnNamePairs(EOAttribute excludeAttribute) {
		Set<NamePair> columnNamePairs = new HashSet<NamePair>();
		for (EOAttribute attribute : getAttributes()) {
			if (excludeAttribute == null || attribute != excludeAttribute) {
				columnNamePairs.add(new NameSyncUtils.NamePair(attribute.getName(), attribute.getColumnName()));
			}
		}
		return columnNamePairs;
	}

	public void synchronizeNameChange(String oldName, String newName) {
		Set<NameSyncUtils.NamePair> externalNamePairs = null;
		if (getModel() != null) {
			externalNamePairs = getModel().getEntityExternalNamePairs(this);
		}
		setExternalName(NameSyncUtils.newDependentName(oldName, newName, getExternalName(), externalNamePairs));
		setClassName(NameSyncUtils.newClassName(oldName, newName, getClassName()));
		setClientClassName(NameSyncUtils.newClassName(oldName, newName, getClientClassName()));
	}

	public void _addToModelParent(EOModel modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedEntityName(getName()));
		}
		modelParent.addEntity(this);
	}

	public boolean getSqlGenerationCreateInheritedProperties() {
		return isVerticalInheritance();
	}
	
	public String getSqlGenerationPrimaryKeyColumnNames() {
		StringBuffer sb = new StringBuffer();
		Iterator<EOAttribute> attributesIter = getPrimaryKeyAttributes().iterator();
		while (attributesIter.hasNext()) {
			EOAttribute attribute = attributesIter.next();
			sb.append("\"" + attribute.getColumnName() + "\"");
			if (attributesIter.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	public String toString() {
		return "[EOEntity: name = " + myName + "; attributes = " + myAttributes + "; relationships = " + myRelationships + "; fetchSpecs = " + myFetchSpecs + "]"; //$NON-NLS-4$ //$NON-NLS-5$
	}
}
