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

import java.util.Set;

public class EOJoin extends EOModelObject implements ISortableEOModelObject {
  public static final String DESTINATION_ATTRIBUTE = "destinationAttribute"; //$NON-NLS-1$
  public static final String SOURCE_ATTRIBUTE = "sourceAttribute"; //$NON-NLS-1$
  public static final String DESTINATION_ATTRIBUTE_NAME = "destinationAttributeName"; //$NON-NLS-1$
  public static final String SOURCE_ATTRIBUTE_NAME = "sourceAttributeName"; //$NON-NLS-1$

  private EORelationship myRelationship;
  private EOAttribute mySourceAttribute;
  private EOAttribute myDestinationAttribute;
  private EOModelMap myJoinMap;

  public EOJoin(EORelationship _relationship) {
    myRelationship = _relationship;
    myJoinMap = new EOModelMap();
  }
  
  public String getName() {
    return ((mySourceAttribute != null) ? mySourceAttribute.getName() : "") + ((myDestinationAttribute != null) ? myDestinationAttribute.getName() : "");
  }

  public EORelationship getRelationship() {
    return myRelationship;
  }

  public EOJoin cloneInto(EORelationship _relationship, boolean _fireEvents, Set _failures) throws DuplicateNameException {
    EOJoin join = new EOJoin(_relationship);
    if (mySourceAttribute != null) {
      join.mySourceAttribute = _relationship.getEntity().getAttributeNamed(mySourceAttribute.getName());
      if (join.mySourceAttribute == null) {
        mySourceAttribute.cloneInto(_relationship.getEntity(), _fireEvents, _failures);
      }
    }
    if (myDestinationAttribute != null) {
      join.myDestinationAttribute = _relationship.getDestination().getAttributeNamed(myDestinationAttribute.getName());
      if (join.myDestinationAttribute == null && myDestinationAttribute.getEntity() == myRelationship.getEntity()) {
        myDestinationAttribute.cloneInto(_relationship.getEntity(), _fireEvents, _failures);
      }
    }
    _relationship.addJoin(join, _fireEvents);
    return join;
  }

  public int hashCode() {
    int hashCode = myRelationship.hashCode();
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
      }
      else {
        EOJoin otherJoin = (EOJoin) _obj;
        if (otherJoin.myRelationship.equals(myRelationship)) {
          if (mySourceAttribute != null && myDestinationAttribute != null && otherJoin.mySourceAttribute != null && otherJoin.myDestinationAttribute != null) {
            equals = mySourceAttribute.equals(otherJoin.mySourceAttribute) && myDestinationAttribute.equals(otherJoin.myDestinationAttribute);
          }
        }
      }
    }
    return equals;
  }

  public boolean isRelatedTo(EOAttribute _attribute) {
    return getSourceAttribute().equals(_attribute) || getDestinationAttribute().equals(_attribute);
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
      joinMap.setString("destinationAttribute", myDestinationAttribute.getName(), true); //$NON-NLS-1$
    }
    if (mySourceAttribute != null) {
      joinMap.setString("sourceAttribute", mySourceAttribute.getName(), true); //$NON-NLS-1$
    }
    return joinMap;
  }

  public void resolve(Set _failures) {
    String sourceAttributeName = myJoinMap.getString("sourceAttribute", true); //$NON-NLS-1$
    mySourceAttribute = myRelationship.getEntity().getAttributeNamed(sourceAttributeName);
    if (mySourceAttribute == null) {
      _failures.add(new MissingAttributeFailure(myRelationship.getEntity(), sourceAttributeName));
    }

    String destinationAttributeName = myJoinMap.getString("destinationAttribute", true); //$NON-NLS-1$
    EOEntity destination = myRelationship.getDestination();
    if (destination != null) {
      myDestinationAttribute = myRelationship.getDestination().getAttributeNamed(destinationAttributeName);
      if (myDestinationAttribute == null) {
        _failures.add(new MissingAttributeFailure(myRelationship.getDestination(), destinationAttributeName));
      }
    }
  }

  public void verify(Set _failures) {
    if (mySourceAttribute == null) {
      _failures.add(new EOModelVerificationFailure(getRelationship().getEntity().getName() + "'s " + getRelationship().getName() + "'s has a join with a missing source attribute."));
    }
    if (myDestinationAttribute == null) {
      _failures.add(new EOModelVerificationFailure(getRelationship().getEntity().getName() + "'s " + getRelationship().getName() + "'s has a join with a missing destination attribute."));
    }
  }

  public String toString() {
    return "[EOJoin: sourceAttribute = " + ((mySourceAttribute == null) ? "null" : mySourceAttribute.getName()) + "; destinationAttribute = " + ((myDestinationAttribute == null) ? "null" : myDestinationAttribute.getName()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
  }
}
