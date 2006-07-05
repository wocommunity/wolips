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

import java.util.List;

public class EOJoin {
  private EORelationship myRelationship;
  private String mySourceAttribute;
  private String myDestinationAttribute;
  private EOModelMap myJoinMap;

  public EOJoin(EORelationship _relationship) {
    myRelationship = _relationship;
    myJoinMap = new EOModelMap();
  }

  public int hashCode() {
    return myRelationship.hashCode() * mySourceAttribute.hashCode() * myDestinationAttribute.hashCode();
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof EOJoin && ((EOJoin) _obj).myRelationship.equals(myRelationship) && ((EOJoin) _obj).mySourceAttribute.equals(mySourceAttribute) && ((EOJoin) _obj).myDestinationAttribute.equals(myDestinationAttribute));
  }

  public boolean isRelatedTo(EOAttribute _attribute) {
    return getSourceAttribute().equals(_attribute) || getDestinationAttribute().equals(_attribute);
  }

  public EOAttribute getSourceAttribute() {
    EOEntity entity = myRelationship.getEntity();
    EOAttribute attribute = null;
    if (entity != null) {
      attribute = entity.getAttributeNamed(mySourceAttribute);
    }
    return attribute;
  }

  public void setSourceAttribute(EOAttribute _attribute) {
    if (_attribute == null) {
      mySourceAttribute = null;
    }
    else {
      mySourceAttribute = _attribute.getName();
    }
  }

  public EOAttribute getDestinationAttribute() {
    EOEntity entity = myRelationship.getDestination();
    EOAttribute attribute = null;
    if (entity != null) {
      attribute = entity.getAttributeNamed(myDestinationAttribute);
    }
    return attribute;
  }

  public void setDestinationAttribute(EOAttribute _attribute) {
    if (_attribute == null) {
      myDestinationAttribute = null;
    }
    else {
      myDestinationAttribute = _attribute.getName();
    }
  }

  public void loadFromMap(EOModelMap _joinMap) {
    myJoinMap = _joinMap;
    myDestinationAttribute = _joinMap.getString("destinationAttribute", true);
    mySourceAttribute = _joinMap.getString("sourceAttribute", true);
  }

  public EOModelMap toMap() {
    EOModelMap joinMap = myJoinMap.cloneModelMap();
    joinMap.setString("destinationAttribute", myDestinationAttribute, true);
    joinMap.setString("sourceAttribute", mySourceAttribute, true);
    return joinMap;
  }

  public void verify(List _failures) {
    if (getDestinationAttribute() == null) {
      _failures.add(new MissingAttributeFailure(myRelationship.getDestination(), myDestinationAttribute));
    }
    if (getSourceAttribute() == null) {
      _failures.add(new MissingAttributeFailure(myRelationship.getEntity(), mySourceAttribute));
    }
  }

  public String toString() {
    return "[EOJoin: sourceAttribute = " + mySourceAttribute + "; destinationAttribute = " + myDestinationAttribute + "]";
  }
}
