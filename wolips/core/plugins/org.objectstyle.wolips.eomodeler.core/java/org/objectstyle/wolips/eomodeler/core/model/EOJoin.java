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

import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;

public class EOJoin extends EOModelObject<EORelationship> implements ISortableEOModelObject {
	public static final String DESTINATION_ATTRIBUTE = "destinationAttribute";

	public static final String SOURCE_ATTRIBUTE = "sourceAttribute";

	public static final String DESTINATION_ATTRIBUTE_NAME = "destinationAttributeName";

	public static final String SOURCE_ATTRIBUTE_NAME = "sourceAttributeName";

	private EORelationship myRelationship;

	private EOAttribute mySourceAttribute;

	private EOAttribute myDestinationAttribute;

	private EOModelMap myJoinMap;

	public EOJoin() {
		myJoinMap = new EOModelMap();
	}

	public EOJoin addInverseJoinInto(EORelationship _relationship, boolean _fireEvents) {
		EOJoin inverseJoin = new EOJoin();
		inverseJoin.setSourceAttribute(myDestinationAttribute);
		inverseJoin.setDestinationAttribute(mySourceAttribute);
		_relationship.addJoin(inverseJoin, _fireEvents);
		return inverseJoin;
	}

	public Set<EOModelVerificationFailure> getReferenceFailures() {
		return new HashSet<EOModelVerificationFailure>();
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (myRelationship != null) {
			myRelationship._joinChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	public String getName() {
		return ((mySourceAttribute != null) ? mySourceAttribute.getName() : "") + ((myDestinationAttribute != null) ? myDestinationAttribute.getName() : "");
	}

	public void _setRelationship(EORelationship _relationship) {
		myRelationship = _relationship;
	}

	public EORelationship getRelationship() {
		return myRelationship;
	}

	public boolean isInverseJoin(EOJoin _join) {
		return _join != null && ComparisonUtils.equals(mySourceAttribute, _join.myDestinationAttribute) && ComparisonUtils.equals(myDestinationAttribute, _join.mySourceAttribute);
	}

	public void pasted() throws DuplicateNameException {
		if (mySourceAttribute != null) {
			EOAttribute sourceAttribute = myRelationship.getEntity().getAttributeNamed(mySourceAttribute.getName());
			if (mySourceAttribute == null) {
				mySourceAttribute = mySourceAttribute._cloneModelObject();
				myRelationship.getEntity().addAttribute(sourceAttribute);
			} else {
				mySourceAttribute = sourceAttribute;
			}
		}
		if (myDestinationAttribute != null) {
			EOAttribute destinationAttribute = myRelationship.getDestination().getAttributeNamed(myDestinationAttribute.getName());
			if (destinationAttribute == null && myDestinationAttribute.getEntity() == myRelationship.getEntity()) {
				myDestinationAttribute = myDestinationAttribute._cloneModelObject();
				myRelationship.getEntity().addAttribute(destinationAttribute);
			} else {
				myDestinationAttribute = destinationAttribute;
			}
		}
	}

	public int hashCode() {
		int hashCode = (myRelationship == null) ? 1 : myRelationship.hashCode();
		if (mySourceAttribute != null) {
			hashCode *= mySourceAttribute.hashCode();
		}
		if (myDestinationAttribute != null) {
			hashCode *= myDestinationAttribute.hashCode();
		}
		return hashCode;
	}

	public boolean equals(Object _obj) {
		boolean equals = false;
		if (_obj instanceof EOJoin) {
			if (_obj == this) {
				equals = true;
			} else {
				EOJoin otherJoin = (EOJoin) _obj;
				if (ComparisonUtils.equals(otherJoin.myRelationship, myRelationship)) {
					if (mySourceAttribute != null && myDestinationAttribute != null && otherJoin.mySourceAttribute != null && otherJoin.myDestinationAttribute != null) {
						equals = mySourceAttribute.equals(otherJoin.mySourceAttribute) && myDestinationAttribute.equals(otherJoin.myDestinationAttribute);
					}
				}
			}
		}
		return equals;
	}

	public boolean isRelatedTo(EOAttribute _attribute) {
		return (getSourceAttribute() != null && getSourceAttribute().equals(_attribute)) || (getDestinationAttribute() != null && getDestinationAttribute().equals(_attribute));
	}

	public String getSourceAttributeName() {
		String sourceAttributeName = null;
		EOAttribute sourceAttribute = getSourceAttribute();
		if (sourceAttribute != null) {
			sourceAttributeName = sourceAttribute.getName();
		}
		return sourceAttributeName;
	}

	public void setSourceAttributeName(String _sourceAttributeName) {
		EOAttribute sourceAttribute = myRelationship.getEntity().getAttributeNamed(_sourceAttributeName);
		setSourceAttribute(sourceAttribute);
	}

	public EOAttribute getSourceAttribute() {
		return mySourceAttribute;
	}

	public void setSourceAttribute(EOAttribute _sourceAttribute) {
		setSourceAttribute(_sourceAttribute, true);
	}

	public void setSourceAttribute(EOAttribute _sourceAttribute, boolean _fireEvents) {
		EOAttribute oldSourceAttribute = mySourceAttribute;
		mySourceAttribute = _sourceAttribute;
		if (_fireEvents) {
			firePropertyChange(EOJoin.SOURCE_ATTRIBUTE, oldSourceAttribute, mySourceAttribute);
		}
	}

	public String getDestinationAttributeName() {
		String destinationAttributeName = null;
		EOAttribute destinationAttribute = getDestinationAttribute();
		if (destinationAttribute != null) {
			destinationAttributeName = destinationAttribute.getName();
		}
		return destinationAttributeName;
	}

	public void setDestinationAttributeName(String _destinationAttributeName) {
		EOAttribute destinationAttribute = myRelationship.getDestination().getAttributeNamed(_destinationAttributeName);
		setDestinationAttribute(destinationAttribute);
	}

	public EOAttribute getDestinationAttribute() {
		return myDestinationAttribute;
	}

	public void setDestinationAttribute(EOAttribute _destinationAttribute) {
		setDestinationAttribute(_destinationAttribute, true);
	}

	public void setDestinationAttribute(EOAttribute _destinationAttribute, boolean _fireEvents) {
		EOAttribute oldDestinationAttribute = myDestinationAttribute;
		myDestinationAttribute = _destinationAttribute;
		if (_fireEvents) {
			firePropertyChange(EOJoin.DESTINATION_ATTRIBUTE, oldDestinationAttribute, myDestinationAttribute);
		}
	}

	public void loadFromMap(EOModelMap _joinMap, Set _failures) {
		myJoinMap = _joinMap;
	}

	public EOModelMap toMap() {
		EOModelMap joinMap = myJoinMap.cloneModelMap();
		if (myDestinationAttribute != null) {
			joinMap.setString("destinationAttribute", myDestinationAttribute.getName(), true);
		} else {
			joinMap.remove("destinationAttribute");
		}
		if (mySourceAttribute != null) {
			joinMap.setString("sourceAttribute", mySourceAttribute.getName(), true);
		} else {
			joinMap.remove("sourceAttribute");
		}
		return joinMap;
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		String sourceAttributeName = myJoinMap.getString("sourceAttribute", true);
		mySourceAttribute = myRelationship.getEntity().getAttributeNamed(sourceAttributeName);
		if (mySourceAttribute == null) {
			_failures.add(new MissingAttributeFailure(myRelationship.getEntity(), sourceAttributeName));
		}

		String destinationAttributeName = myJoinMap.getString("destinationAttribute", true);
		EOEntity destination = myRelationship.getDestination();
		if (destination != null) {
			myDestinationAttribute = myRelationship.getDestination().getAttributeNamed(destinationAttributeName);
			if (myDestinationAttribute == null) {
				_failures.add(new MissingAttributeFailure(myRelationship.getDestination(), destinationAttributeName));
			}
		}
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		if (mySourceAttribute == null) {
			_failures.add(new EOModelVerificationFailure(getRelationship().getEntity().getModel(), getRelationship().getEntity().getName() + "'s " + getRelationship().getName() + "'s has a join with a missing source attribute.", false));
		}
		if (myDestinationAttribute == null) {
			_failures.add(new EOModelVerificationFailure(getRelationship().getEntity().getModel(), getRelationship().getEntity().getName() + "'s " + getRelationship().getName() + "'s has a join with a missing destination attribute.", false));
		}
	}

	public String getFullyQualifiedName() {
		return ((myRelationship == null) ? "?" : myRelationship.getFullyQualifiedName()) + ", join: " + getSourceAttributeName() + "=>" + getDestinationAttributeName();
	}

	@Override
	public EOJoin _cloneModelObject() {
		EOJoin join = new EOJoin();
		join.mySourceAttribute = mySourceAttribute;
		join.myDestinationAttribute = myDestinationAttribute;
		return join;
	}
	
	@Override
	public Class<EORelationship> _getModelParentType() {
		return EORelationship.class;
	}
	
	public EORelationship _getModelParent() {
		return getRelationship();
	}

	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getRelationship().removeJoin(this);
	}

	public void _addToModelParent(EORelationship modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) {
		modelParent.addJoin(this);
	}

	public String toString() {
		return "[EOJoin: sourceAttribute = " + ((mySourceAttribute == null) ? "null" : mySourceAttribute.getName()) + "; destinationAttribute = " + ((myDestinationAttribute == null) ? "null" : myDestinationAttribute.getName()) + "]"; //$NON-NLS-4$ //$NON-NLS-5$
	}
}
