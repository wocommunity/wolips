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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.internal.databinding.provisional.observable.list.WritableList;

public class EORelationship extends EOModelObject implements IEOAttribute {
  public static final String TO_MANY = "toMany"; //$NON-NLS-1$
  public static final String CLASS_PROPERTY = "classProperty"; //$NON-NLS-1$
  public static final String NAME = "name"; //$NON-NLS-1$
  public static final String DESTINATION = "destination"; //$NON-NLS-1$
  public static final String DEFINITION = "definition"; //$NON-NLS-1$
  public static final String DELETE_RULE = "eleteRule"; //$NON-NLS-1$
  public static final String JOIN_SEMANTIC = "joinSemantic"; //$NON-NLS-1$
  public static final String MANDATORY = "mandatory"; //$NON-NLS-1$
  public static final String OWNS_DESTINATION = "ownsDestination"; //$NON-NLS-1$
  public static final String PROPAGATES_PRIMARY_KEY = "propagatesPrimaryKey"; //$NON-NLS-1$
  public static final String JOINS = "joins"; //$NON-NLS-1$

  private EOEntity myEntity;
  private EOEntity myDestination;
  private String myName;
  private String myDefinition;
  private Boolean myMandatory;
  private Boolean myToMany;
  private Boolean myOwnsDestination;
  private Boolean myPropagatesPrimaryKey;
  private Boolean myClassProperty;
  private String myDeleteRule;
  private String myJoinSemantic;
  private List myJoins;
  private EOModelMap myRelationshipMap;
  private Map myUserInfo;

  public EORelationship(EOEntity _entity) {
    myEntity = _entity;
    myJoins = new WritableList(new LinkedList(), EOJoin.class);
    myRelationshipMap = new EOModelMap();
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    myEntity._relationshipChanged(this);
  }

  public int hashCode() {
    return myEntity.hashCode() * myName.hashCode();
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof EORelationship && ((EORelationship) _obj).myEntity.equals(myEntity) && ((EORelationship) _obj).myName.equals(myName));
  }

  public boolean isRelatedTo(EOEntity _entity) {
    return _entity.equals(myDestination);
  }

  public boolean isRelatedTo(EOAttribute _attribute) {
    boolean isRelated = false;
    Iterator joinsIter = myJoins.iterator();
    while (!isRelated && joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      isRelated = join.isRelatedTo(_attribute);
    }
    return isRelated;
  }

  public void setDefinition(String _definition) {
    String oldDefinition = myDefinition;
    myDefinition = _definition;
    firePropertyChange(EORelationship.DEFINITION, oldDefinition, myDefinition);
  }

  public String getDefinition() {
    return myDefinition;
  }

  public void setClassProperty(Boolean _classProperty) {
    setClassProperty(_classProperty, true);
  }

  public void setClassProperty(Boolean _classProperty, boolean _fireEvents) {
    Boolean oldClassProperty = myClassProperty;
    myClassProperty = _classProperty;
    if (_fireEvents) {
      firePropertyChange(EORelationship.CLASS_PROPERTY, oldClassProperty, myClassProperty);
    }
  }

  public Boolean isClassProperty() {
    return myClassProperty;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public void setName(String _name) throws DuplicateRelationshipNameException {
    String oldName = myName;
    myEntity._checkForDuplicateRelationshipName(this, _name);
    myName = _name;
    firePropertyChange(EORelationship.NAME, oldName, myName);
  }

  public String getName() {
    return myName;
  }

  public String getDeleteRule() {
    return myDeleteRule;
  }

  public void setDeleteRule(String _deleteRule) {
    String oldDeleteRule = myDeleteRule;
    myDeleteRule = _deleteRule;
    firePropertyChange(EORelationship.DELETE_RULE, oldDeleteRule, myDeleteRule);
  }

  public EOEntity getDestination() {
    return myDestination;
  }

  public void setDestination(EOEntity _destination) {
    setDestination(_destination, true);
  }

  public void setDestination(EOEntity _destination, boolean _fireEvents) {
    EOEntity oldDestination = myDestination;
    myDestination = _destination;
    if (_fireEvents) {
      firePropertyChange(EORelationship.DESTINATION, oldDestination, myDestination);
    }
  }

  public String getJoinSemantic() {
    return myJoinSemantic;
  }

  public void setJoinSemantic(String _joinSemantic) {
    String oldJoinSemantic = myJoinSemantic;
    myJoinSemantic = _joinSemantic;
    firePropertyChange(EORelationship.JOIN_SEMANTIC, oldJoinSemantic, myJoinSemantic);
  }

  public Boolean isMandatory() {
    return myMandatory;
  }

  public void setMandatory(Boolean _mandatory) {
    Boolean oldMandatory = myMandatory;
    myMandatory = _mandatory;
    firePropertyChange(EORelationship.MANDATORY, oldMandatory, myMandatory);
  }

  public Boolean isOwnsDestination() {
    return myOwnsDestination;
  }

  public void setOwnsDestination(Boolean _ownsDestination) {
    Boolean oldOwnsDestination = myOwnsDestination;
    myOwnsDestination = _ownsDestination;
    firePropertyChange(EORelationship.OWNS_DESTINATION, oldOwnsDestination, myOwnsDestination);
  }

  public Boolean isPropagatesPrimaryKey() {
    return myPropagatesPrimaryKey;
  }

  public void setPropagatesPrimaryKey(Boolean _propagatesPrimaryKey) {
    Boolean oldPropagatesPrimaryKey = myPropagatesPrimaryKey;
    myPropagatesPrimaryKey = _propagatesPrimaryKey;
    firePropertyChange(EORelationship.PROPAGATES_PRIMARY_KEY, oldPropagatesPrimaryKey, myPropagatesPrimaryKey);
  }

  public Boolean isToMany() {
    return myToMany;
  }

  public void setToMany(Boolean _toMany) {
    Boolean oldToMany = myToMany;
    myToMany = _toMany;
    firePropertyChange(EORelationship.TO_MANY, oldToMany, myToMany);
  }

  public void clearJoins() {
    myJoins.clear();
    firePropertyChange(EORelationship.JOINS, null, null);
  }

  public void setJoins(List _joins) {
    myJoins.clear();
    myJoins.addAll(_joins);
    firePropertyChange(EORelationship.JOINS, null, null);
  }

  public void addJoin(EOJoin _join) {
    addJoin(_join, true);
  }

  public void addJoin(EOJoin _join, boolean _fireEvents) {
    // TODO: Check duplicates
    myJoins.add(_join);
    if (_fireEvents) {
      firePropertyChange(EORelationship.JOINS, null, null);
    }
  }

  public void removeJoin(EOJoin _join) {
    myJoins.remove(_join);
    firePropertyChange(EORelationship.JOINS, null, null);
  }

  public List getJoins() {
    return myJoins;
  }

  public EOJoin getFirstJoin() {
    EOJoin join = null;
    if (myJoins.size() > 0) {
      join = (EOJoin) myJoins.get(0);
    }
    return join;
  }

  public void loadFromMap(EOModelMap _relationshipMap) {
    myRelationshipMap = _relationshipMap;
    myDefinition = _relationshipMap.getString("definition", true); //$NON-NLS-1$
    myMandatory = _relationshipMap.getBoolean("isMandatory"); //$NON-NLS-1$
    myToMany = _relationshipMap.getBoolean("isToMany"); //$NON-NLS-1$
    myJoinSemantic = _relationshipMap.getString("joinSemantic", true); //$NON-NLS-1$
    myName = _relationshipMap.getString("name", true); //$NON-NLS-1$
    myDeleteRule = _relationshipMap.getString("deleteRule", true); //$NON-NLS-1$
    myOwnsDestination = _relationshipMap.getBoolean("ownsDestination"); //$NON-NLS-1$
    myPropagatesPrimaryKey = _relationshipMap.getBoolean("propagatesPrimaryKey"); // TODO: verify //$NON-NLS-1$
    List joins = _relationshipMap.getList("joins"); //$NON-NLS-1$
    if (joins != null) {
      Iterator joinsIter = joins.iterator();
      while (joinsIter.hasNext()) {
        EOModelMap joinMap = new EOModelMap((Map) joinsIter.next());
        EOJoin join = new EOJoin(this);
        join.loadFromMap(joinMap);
        addJoin(join, false);
      }
    }
    myUserInfo = _relationshipMap.getMap("userInfo", true); //$NON-NLS-1$
  }

  public EOModelMap toMap() {
    EOModelMap relationshipMap = myRelationshipMap.cloneModelMap();
    relationshipMap.setString("destination", myDestination.getName(), true); //$NON-NLS-1$
    relationshipMap.setString("definition", myDefinition, true); //$NON-NLS-1$
    relationshipMap.setBoolean("isMandatory", myMandatory); //$NON-NLS-1$
    relationshipMap.setBoolean("isToMany", myToMany); //$NON-NLS-1$
    relationshipMap.setString("joinSemantic", myJoinSemantic, true); //$NON-NLS-1$
    relationshipMap.setString("name", myName, true); //$NON-NLS-1$
    relationshipMap.setString("deleteRule", myDeleteRule, true); //$NON-NLS-1$
    relationshipMap.setBoolean("ownsDestination", myOwnsDestination); //$NON-NLS-1$
    relationshipMap.setBoolean("propagatesPrimaryKey", myPropagatesPrimaryKey); // TODO: verify //$NON-NLS-1$
    List joins = new LinkedList();
    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      EOModelMap joinMap = join.toMap();
      joins.add(joinMap);
    }
    relationshipMap.setList("joins", joins); //$NON-NLS-1$
    relationshipMap.setMap("userInfo", myUserInfo); //$NON-NLS-1$
    return relationshipMap;
  }

  public void resolve(List _failures) {
    String destinationName = myRelationshipMap.getString("destination", true); //$NON-NLS-1$
    if (destinationName == null) {
      _failures.add(new EOModelVerificationFailure(myEntity.getName() + "'s " + myName + " relationship has no destination entity."));
    }
    else {
      myDestination = myEntity.getModel().getModelGroup().getEntityNamed(destinationName);
      if (myDestination == null) {
        _failures.add(new MissingEntityFailure(destinationName));
      }
    }

    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      join.resolve(_failures);
    }
  }

  public void verify(List _failures) {
    if (myDestination == null) {
      _failures.add(new EOModelVerificationFailure(myEntity.getName() + "'s " + myName + " relationship has no destination entity."));
    }
    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      join.verify(_failures);
    }
  }

  public String toString() {
    return "[EORelationship: name = " + myName + "; destination = " + ((myDestination == null) ? "null" : myDestination.getName()) + "; joins = " + myJoins + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
  }
}
