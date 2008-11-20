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

import java.util.HashSet;
import java.util.Set;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;

public class EOEntityIndex extends UserInfoableEOModelObject<EOEntity> implements IEOEntityRelative, ISortableEOModelObject {
	public static enum Constraint {
		Distinct("distinct"), FullText("fulltext"), Spatial("spatial"), None("none");

		private String _externalName;

		private Constraint(String externalName) {
			_externalName = externalName;
		}

		public String getExternalName() {
			return _externalName;
		}

		public static Constraint getConstraintNamed(String externalName) {
			Constraint matchingConstraint = null;
			if (externalName == null) {
				matchingConstraint = Constraint.None;
			} else {
				Constraint[] constraints = EOEntityIndex.Constraint.values();
				for (int i = 0; matchingConstraint == null && i < constraints.length; i++) {
					if (constraints[i].getExternalName().equalsIgnoreCase(externalName)) {
						matchingConstraint = constraints[i];
					}
				}
			}
			return matchingConstraint;
		}
	}

	public static enum IndexType {
		Clustered("clustered"), Hashed("hashed");

		private String _externalName;

		private IndexType(String externalName) {
			_externalName = externalName;
		}

		public String getExternalName() {
			return _externalName;
		}

		public static IndexType getIndexTypeNamed(String externalName) {
			IndexType matchingIndexType = null;
			if (externalName == null) {
				matchingIndexType = IndexType.Clustered;
			} else {
				IndexType[] indexTypes = EOEntityIndex.IndexType.values();
				for (int i = 0; matchingIndexType == null && i < indexTypes.length; i++) {
					if (indexTypes[i].getExternalName().equalsIgnoreCase(externalName)) {
						matchingIndexType = indexTypes[i];
					}
				}
			}
			return matchingIndexType;
		}
	}

	public static enum Order {
		Ascending("asc"), Descending("desc");

		private String _externalName;

		private Order(String externalName) {
			_externalName = externalName;
		}

		public String getExternalName() {
			return _externalName;
		}

		public static Order getOrderNamed(String externalName) {
			Order matchingOrder = null;
			if (externalName == null) {
				matchingOrder = Order.Ascending;
			} else {
				Order[] orders = EOEntityIndex.Order.values();
				for (int i = 0; matchingOrder == null && i < orders.length; i++) {
					if (orders[i].getExternalName().equalsIgnoreCase(externalName)) {
						matchingOrder = orders[i];
					}
				}
			}
			return matchingOrder;
		}
	}

	public static final String NAME = "name";

	public static final String ATTRIBUTES = "attributes";

	public static final String CONSTRAINT = "constraint";

	public static final String INDEX_TYPE = "indexType";

	public static final String ORDER = "order";

	private EOEntity _entity;

	private String _name;

	private EOEntityIndex.Constraint _constraint;

	private EOEntityIndex.IndexType _indexType;

	private EOEntityIndex.Order _order;

	private Set<EOAttribute> _attributes;

	private EOModelMap _entityIndexMap;

	public EOEntityIndex() {
		_constraint = EOEntityIndex.Constraint.None;
		_indexType = EOEntityIndex.IndexType.Clustered;
		_order = EOEntityIndex.Order.Ascending;
		_entityIndexMap = new EOModelMap();
		_attributes = new HashSet<EOAttribute>();
	}

	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	protected void _propertyChanged(String propertyName, Object oldValue, Object newValue) {
		if (_entity != null) {
			_entity._entityIndexChanged(this, propertyName, oldValue, newValue);
		}
	}

//	public int hashCode() {
//		return ((_entity == null) ? 1 : _entity.hashCode()) * ((_name == null) ? super.hashCode() : _name.hashCode());
//	}
//
//	public boolean equals(Object obj) {
//		boolean equals = false;
//		if (obj instanceof EOEntityIndex) {
//			EOEntityIndex entityIndex = (EOEntityIndex) obj;
//			equals = (entityIndex == this) || (ComparisonUtils.equals(entityIndex._entity, _entity) && ComparisonUtils.equals(entityIndex._name, _name));
//		}
//		return equals;
//	}

	public void setName(String name) throws DuplicateEntityIndexNameException {
		setName(name, true);
	}

	public void setName(String name, boolean fireEvents) throws DuplicateEntityIndexNameException {
		if (_entity != null) {
			_entity._checkForDuplicateEntityIndexName(this, name, null);
		}
		String oldName = _name;
		_name = name;
		if (fireEvents) {
			firePropertyChange(EOEntityIndex.NAME, oldName, _name);
		}
	}

	public EOEntityIndex.Constraint getConstraint() {
		return _constraint;
	}

	public void setConstraint(EOEntityIndex.Constraint constrain) {
		setConstraint(constrain, true);
	}

	public void setConstraint(EOEntityIndex.Constraint constraint, boolean fireEvents) {
		EOEntityIndex.Constraint oldConstrain = _constraint;
		if (constraint == null) {
			_constraint = EOEntityIndex.Constraint.None;
		} else {
			_constraint = constraint;
		}
		if (fireEvents) {
			firePropertyChange(EOEntityIndex.CONSTRAINT, oldConstrain, _constraint);
		}
	}

	public EOEntityIndex.IndexType getIndexType() {
		return _indexType;
	}

	public void setIndexType(EOEntityIndex.IndexType indexType) {
		setIndexType(indexType, true);
	}

	public void setIndexType(EOEntityIndex.IndexType indexType, boolean fireEvents) {
		EOEntityIndex.IndexType oldIndexType = _indexType;
		_indexType = indexType;
		if (fireEvents) {
			firePropertyChange(EOEntityIndex.INDEX_TYPE, oldIndexType, _indexType);
		}
	}

	public EOEntityIndex.Order getOrder() {
		return _order;
	}

	public void setOrder(EOEntityIndex.Order order) {
		setOrder(order, true);
	}

	public void setOrder(EOEntityIndex.Order order, boolean fireEvents) {
		EOEntityIndex.Order oldOrder = _order;
		_order = order;
		if (fireEvents) {
			firePropertyChange(EOEntityIndex.ORDER, oldOrder, _order);
		}
	}

	public String getName() {
		return _name;
	}

	public void _setEntity(EOEntity entity) {
		_entity = entity;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public void addAttribute(EOAttribute attribute) {
		addAttribute(attribute, true);
	}

	public synchronized void addAttribute(EOAttribute attribute, boolean fireEvents) {
		Set<EOAttribute> oldAttributes = null;
		if (fireEvents) {
			oldAttributes = _attributes;
			Set<EOAttribute> newAttributes = new HashSet<EOAttribute>();
			newAttributes.addAll(_attributes);
			newAttributes.add(attribute);
			_attributes = newAttributes;
			firePropertyChange(EOEntityIndex.ATTRIBUTES, oldAttributes, newAttributes);
		} else {
			_attributes.add(attribute);
		}
	}

	public void removeAttribute(EOAttribute attribute, boolean fireEvents) {
		if (fireEvents) {
			Set<EOAttribute> oldAttributes = _attributes;
			Set<EOAttribute> newAttributes = new HashSet<EOAttribute>();
			newAttributes.addAll(_attributes);
			newAttributes.remove(attribute);
			_attributes = newAttributes;
			firePropertyChange(EOEntityIndex.ATTRIBUTES, oldAttributes, newAttributes);
		} else {
			_attributes.remove(attribute);
		}
	}

	public Set<EOAttribute> getAttributes() {
		return _attributes;
	}

	@SuppressWarnings("unused")
	public void loadFromMap(EOModelMap _map, Set<EOModelVerificationFailure> _failures) {
		_entityIndexMap = _map;
		_name = _map.getString("name", true);
		_constraint = EOEntityIndex.Constraint.getConstraintNamed(_map.getString("constraint", true));
		_indexType = EOEntityIndex.IndexType.getIndexTypeNamed(_map.getString("indexType", true));
		_order = EOEntityIndex.Order.getOrderNamed(_map.getString("order", true));
		loadUserInfo(_map);
	}

	public EOModelMap toMap() {
		EOModelMap entityIndexMap = _entityIndexMap.cloneModelMap();
		entityIndexMap.setString("name", _name, true);
		entityIndexMap.setString("constraint", _constraint.getExternalName(), true);
		entityIndexMap.setString("indexType", _indexType.getExternalName(), true);
		entityIndexMap.setString("order", _order.getExternalName(), true);

		Set<String> attributeNames = new HashSet<String>();
		for (EOAttribute attribute : _attributes) {
			attributeNames.add(attribute.getName());
		}
		entityIndexMap.setSet("attributes", attributeNames, false);
		writeUserInfo(entityIndexMap);

		return entityIndexMap;
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		_attributes.clear();
		Set<String> attributeNames = _entityIndexMap.getSet("attributes");
		for (String attributeName : attributeNames) {
			EOAttribute attribute = _entity.getAttributeNamed(attributeName);
			if (attribute == null) {
				_failures.add(new MissingAttributeFailure(_entity, attributeName));
			}
			_attributes.add(attribute);
		}
	}

	@SuppressWarnings("unused")
	public void verify(Set<EOModelVerificationFailure> _failures) {
		// DO NOTHING
	}

	public String getFullyQualifiedName() {
		return ((_entity == null) ? "?" : _entity.getFullyQualifiedName()) + "/index: " + getName();
	}

	@Override
	public EOEntityIndex _cloneModelObject() {
		EOEntityIndex entityIndex = new EOEntityIndex();
		entityIndex._name = _name;
		entityIndex._constraint = _constraint;
		entityIndex._indexType = _indexType;
		entityIndex._order = _order;
		_cloneUserInfoInto(entityIndex);
		return entityIndex;
	}

	@Override
	public Class<EOEntity> _getModelParentType() {
		return EOEntity.class;
	}
	
	public EOEntity _getModelParent() {
		return getEntity();
	}

	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getEntity().removeEntityIndex(this);
	}

	public void _addToModelParent(EOEntity modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedEntityIndexName(getName()));
		}
		modelParent.addEntityIndex(this);
	}

	public String toString() {
		return "[EOEntityIndex: name = " + _name + "]";
	}
}
