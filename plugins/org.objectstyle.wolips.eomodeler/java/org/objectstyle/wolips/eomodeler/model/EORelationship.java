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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.StringUtils;

public class EORelationship extends UserInfoableEOModelObject implements IEOAttribute, ISortableEOModelObject {
  public static final String TO_MANY = "toMany";
  public static final String TO_ONE = "toOne";
  public static final String CLASS_PROPERTY = "classProperty";
  public static final String CLIENT_CLASS_PROPERTY = "clientClassProperty";
  public static final String NAME = "name";
  public static final String DESTINATION = "destination";
  public static final String DEFINITION = "definition";
  public static final String DELETE_RULE = "deleteRule";
  public static final String JOIN_SEMANTIC = "joinSemantic";
  public static final String OPTIONAL = "optional";
  public static final String MANDATORY = "mandatory";
  public static final String OWNS_DESTINATION = "ownsDestination";
  public static final String PROPAGATES_PRIMARY_KEY = "propagatesPrimaryKey";
  public static final String NUMBER_OF_TO_MANY_FAULTS_TO_BATCH_FETCH = "numberOfToManyFaultsToBatchFetch";
  public static final String JOINS = "joins";
  public static final String JOIN = "join";

  private EOEntity myEntity;
  private EOEntity myDestination;
  private String myName;
  private String myDefinition;
  private Boolean myMandatory;
  private Boolean myToMany;
  private Boolean myOwnsDestination;
  private Boolean myPropagatesPrimaryKey;
  private Boolean myClassProperty;
  private Boolean myClientClassProperty;
  private Integer myNumberOfToManyFaultsToBatchFetch;
  private EODeleteRule myDeleteRule;
  private EOJoinSemantic myJoinSemantic;
  private Set myJoins;
  private EOModelMap myRelationshipMap;

  private EOEntity myEntityBeforeCloning;

  public EORelationship() {
    myJoins = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    myRelationshipMap = new EOModelMap();
    myDeleteRule = EODeleteRule.getDeleteRuleByID(null);
    myJoinSemantic = EOJoinSemantic.getJoinSemanticByID(null);
  }

  public EORelationship(String _name) {
    this();
    myName = _name;
  }

  public EORelationship(String _name, String _definition) {
    this(_name);
    myDefinition = _definition;
  }

  public Set getReferenceFailures() {
    return new HashSet();
  }

  public void pasted() throws DuplicateNameException {
    if (myEntityBeforeCloning != null) {
      if (myDestination == myEntityBeforeCloning) {
        myDestination = myEntity;
      }
      Iterator joinsIter = myJoins.iterator();
      while (joinsIter.hasNext()) {
        EOJoin join = (EOJoin) joinsIter.next();
        join.pasted();
      }
      myEntityBeforeCloning = null;
    }
  }

  public EORelationship cloneRelationship() {
    EORelationship relationship = new EORelationship(myName);
    if (myEntity == null) {
      relationship.myEntityBeforeCloning = myEntityBeforeCloning;
    }
    else {
      relationship.myEntityBeforeCloning = myEntity;
    }
    relationship.myDestination = myDestination;
    relationship.myDefinition = myDefinition;
    relationship.myMandatory = myMandatory;
    relationship.myToMany = myToMany;
    relationship.myOwnsDestination = myOwnsDestination;
    relationship.myPropagatesPrimaryKey = myPropagatesPrimaryKey;
    relationship.myClassProperty = myClassProperty;
    relationship.myClientClassProperty = myClientClassProperty;
    relationship.myNumberOfToManyFaultsToBatchFetch = myNumberOfToManyFaultsToBatchFetch;
    relationship.myDeleteRule = myDeleteRule;
    relationship.myJoinSemantic = myJoinSemantic;
    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      EOJoin newJoin = join.cloneJoin();
      relationship.addJoin(newJoin, false);
    }
    return relationship;
  }

  protected void _joinChanged(EOJoin _join, String _propertyName, Object _oldValue, Object _newValue) {
    firePropertyChange(EORelationship.JOIN, null, _join);
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    if (myEntity != null) {
      myEntity._relationshipChanged(this, _propertyName, _oldValue, _newValue);
    }
  }

  public int hashCode() {
    return ((myEntity == null) ? 1 : myEntity.hashCode()) * ((myName == null) ? super.hashCode() : myName.hashCode());
  }

  public boolean equals(Object _obj) {
    boolean equals = false;
    if (_obj instanceof EORelationship) {
      EORelationship relationship = (EORelationship) _obj;
      equals = (relationship == this) || (ComparisonUtils.equals(relationship.myEntity, myEntity) && ComparisonUtils.equals(relationship.myName, myName));
    }
    return equals;
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

  public Boolean getClassProperty() {
    return isClassProperty();
  }

  public void setClientClassProperty(Boolean _clientClassProperty) {
    setClientClassProperty(_clientClassProperty, true);
  }

  public void setClientClassProperty(Boolean _clientClassProperty, boolean _fireEvents) {
    Boolean oldClientClassProperty = myClientClassProperty;
    myClientClassProperty = _clientClassProperty;
    if (_fireEvents) {
      firePropertyChange(EORelationship.CLIENT_CLASS_PROPERTY, oldClientClassProperty, myClientClassProperty);
    }
  }

  public Boolean isClientClassProperty() {
    return myClientClassProperty;
  }

  public Boolean getClientClassProperty() {
    return isClientClassProperty();
  }

  public boolean isFlattened() {
    return myDefinition != null;
  }

  public boolean isInherited() {
    boolean inherited = false;
    EOEntity parent = myEntity.getParent();
    if (parent != null) {
      EORelationship attribute = parent.getRelationshipNamed(myName);
      inherited = (attribute != null);
    }
    return inherited;
  }

  public void _setEntity(EOEntity _entity) {
    myEntity = _entity;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public void setName(String _name) throws DuplicateNameException {
    setName(_name, true);
  }

  public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
    if (_name == null) {
      throw new NullPointerException(Messages.getString("EORelationship.noBlankRelationshipNames"));
    }
    String oldName = myName;
    if (myEntity != null) {
      myEntity._checkForDuplicateRelationshipName(this, _name, null);
    }
    myName = _name;
    if (_fireEvents) {
      firePropertyChange(EORelationship.NAME, oldName, myName);
    }
  }

  public String getName() {
    return myName;
  }

  public EODeleteRule getDeleteRule() {
    return myDeleteRule;
  }

  public void setDeleteRule(EODeleteRule _deleteRule) {
    EODeleteRule oldDeleteRule = myDeleteRule;
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

  public EOJoinSemantic getJoinSemantic() {
    return myJoinSemantic;
  }

  public void setJoinSemantic(EOJoinSemantic _joinSemantic) {
    EOJoinSemantic oldJoinSemantic = myJoinSemantic;
    myJoinSemantic = _joinSemantic;
    firePropertyChange(EORelationship.JOIN_SEMANTIC, oldJoinSemantic, myJoinSemantic);
  }

  public Boolean getMandatory() {
    return isMandatory();
  }

  public Boolean isMandatory() {
    return myMandatory;
  }

  public void setMandatory(Boolean _mandatory) {
    Boolean oldMandatory = myMandatory;
    myMandatory = _mandatory;
    firePropertyChange(EORelationship.MANDATORY, oldMandatory, myMandatory);
    firePropertyChange(EORelationship.OPTIONAL, BooleanUtils.negate(oldMandatory), BooleanUtils.negate(myMandatory));
  }

  public Boolean getOptional() {
    return isOptional();
  }

  public Boolean isOptional() {
    return BooleanUtils.negate(isMandatory());
  }

  public void setOptional(Boolean _optional) {
    setMandatory(BooleanUtils.negate(_optional));
  }

  public Boolean getOwnsDestination() {
    return isOwnsDestination();
  }

  public Boolean isOwnsDestination() {
    return myOwnsDestination;
  }

  public void setOwnsDestination(Boolean _ownsDestination) {
    Boolean oldOwnsDestination = myOwnsDestination;
    myOwnsDestination = _ownsDestination;
    firePropertyChange(EORelationship.OWNS_DESTINATION, oldOwnsDestination, myOwnsDestination);
  }

  public Boolean getPropagatesPrimaryKey() {
    return isPropagatesPrimaryKey();
  }

  public Boolean isPropagatesPrimaryKey() {
    return myPropagatesPrimaryKey;
  }

  public void setPropagatesPrimaryKey(Boolean _propagatesPrimaryKey) {
    Boolean oldPropagatesPrimaryKey = myPropagatesPrimaryKey;
    myPropagatesPrimaryKey = _propagatesPrimaryKey;
    firePropertyChange(EORelationship.PROPAGATES_PRIMARY_KEY, oldPropagatesPrimaryKey, myPropagatesPrimaryKey);
  }

  public Boolean getToMany() {
    return isToMany();
  }

  public Boolean isToMany() {
    Boolean toMany = null;
    if (isFlattened() && myEntity != null) {
      AbstractEOAttributePath targetAttributePath = myEntity.resolveKeyPath(getDefinition());
      if (targetAttributePath != null && targetAttributePath.getChildIEOAttribute() != this) {
        toMany = targetAttributePath.isToMany();
      }
    }
    else {
      toMany = myToMany;
    }
    return toMany;
  }

  public void setToMany(Boolean _toMany) {
    if (!isFlattened()) {
      Boolean oldToMany = myToMany;
      myToMany = _toMany;
      firePropertyChange(EORelationship.TO_MANY, oldToMany, myToMany);
      firePropertyChange(EORelationship.TO_ONE, BooleanUtils.negate(oldToMany), BooleanUtils.negate(myToMany));
    }
  }

  public Boolean getToOne() {
    return isToOne();
  }

  public Boolean isToOne() {
    return BooleanUtils.negate(isToMany());
  }

  public void setToOne(Boolean _toOne) {
    setToMany(BooleanUtils.negate(_toOne));
  }

  public void setNumberOfToManyFaultsToBatchFetch(Integer _numberOfToManyFaultsToBatchFetch) {
    Integer oldNumberOfToManyFaultsToBatchFetch = myNumberOfToManyFaultsToBatchFetch;
    myNumberOfToManyFaultsToBatchFetch = _numberOfToManyFaultsToBatchFetch;
    firePropertyChange(EORelationship.NUMBER_OF_TO_MANY_FAULTS_TO_BATCH_FETCH, oldNumberOfToManyFaultsToBatchFetch, myNumberOfToManyFaultsToBatchFetch);
  }

  public Integer getNumberOfToManyFaultsToBatchFetch() {
    return myNumberOfToManyFaultsToBatchFetch;
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
    // TODO: Check duplicates?
    _join._setRelationship(this);
    Set oldJoins = null;
    if (_fireEvents) {
      oldJoins = myJoins;
      Set newJoins = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
      newJoins.addAll(myJoins);
      newJoins.add(_join);
      myJoins = newJoins;
      firePropertyChange(EORelationship.JOINS, oldJoins, myJoins);
    }
    else {
      myJoins.add(_join);
    }
  }

  public void removeJoin(EOJoin _join) {
    Set oldJoins = myJoins;
    Set newJoins = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    newJoins.addAll(myJoins);
    newJoins.remove(_join);
    myJoins = newJoins;
    firePropertyChange(EORelationship.JOINS, oldJoins, newJoins);
    _join._setRelationship(null);
  }

  public Set getJoins() {
    return myJoins;
  }

  public EOJoin getFirstJoin() {
    EOJoin join = null;
    Iterator joinsIter = myJoins.iterator();
    if (joinsIter.hasNext()) {
      join = (EOJoin) joinsIter.next();
    }
    return join;
  }

  public void loadFromMap(EOModelMap _relationshipMap, Set _failures) {
    myRelationshipMap = _relationshipMap;
    myDefinition = _relationshipMap.getString("definition", true);
    myMandatory = _relationshipMap.getBoolean("isMandatory");
    myToMany = _relationshipMap.getBoolean("isToMany");
    String joinSemanticID = _relationshipMap.getString("joinSemantic", true);
    myJoinSemantic = EOJoinSemantic.getJoinSemanticByID(joinSemanticID);
    myName = _relationshipMap.getString("name", true);
    String deleteRuleID = _relationshipMap.getString("deleteRule", true);
    myDeleteRule = EODeleteRule.getDeleteRuleByID(deleteRuleID);
    myOwnsDestination = _relationshipMap.getBoolean("ownsDestination");
    myNumberOfToManyFaultsToBatchFetch = _relationshipMap.getInteger("numberOfToManyFaultsToBatchFetch");
    myPropagatesPrimaryKey = _relationshipMap.getBoolean("propagatesPrimaryKey");
    Set joins = _relationshipMap.getSet("joins");
    if (joins != null) {
      Iterator joinsIter = joins.iterator();
      while (joinsIter.hasNext()) {
        EOModelMap joinMap = new EOModelMap((Map) joinsIter.next());
        EOJoin join = new EOJoin();
        join.loadFromMap(joinMap, _failures);
        addJoin(join, false);
      }
    }
    setUserInfo(_relationshipMap.getMap("userInfo", true), false);
  }

  public EOModelMap toMap() {
    EOModelMap relationshipMap = myRelationshipMap.cloneModelMap();
    if (myDestination != null) {
      relationshipMap.setString("destination", myDestination.getName(), true);
    }
    relationshipMap.setString("definition", myDefinition, true);
    relationshipMap.setBoolean("isMandatory", myMandatory, EOModelMap.YN);
    relationshipMap.setBoolean("isToMany", myToMany, EOModelMap.YN);
    if (!isFlattened() && myJoinSemantic != null) {
      relationshipMap.setString("joinSemantic", myJoinSemantic.getID(), true);
    }
    relationshipMap.setString("name", myName, true);
    if (myDeleteRule != null && myDeleteRule != EODeleteRule.NULLIFY) {
      relationshipMap.setString("deleteRule", myDeleteRule.getID(), true);
    }
    relationshipMap.setBoolean("ownsDestination", myOwnsDestination, EOModelMap.YN);
    relationshipMap.setBoolean("propagatesPrimaryKey", myPropagatesPrimaryKey, EOModelMap.YN);
    relationshipMap.setInteger("numberOfToManyFaultsToBatchFetch", myNumberOfToManyFaultsToBatchFetch);
    Set joins = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      EOModelMap joinMap = join.toMap();
      joins.add(joinMap);
    }
    relationshipMap.setSet("joins", joins, true);
    relationshipMap.setMap("userInfo", getUserInfo(), true);
    return relationshipMap;
  }

  public void resolve(Set _failures) {
    if (!isFlattened()) {
      String destinationName = myRelationshipMap.getString("destination", true);
      if (destinationName == null) {
        _failures.add(new EOModelVerificationFailure(myEntity.getName() + "'s " + myName + " relationship has no destination entity."));
      }
      else {
        myDestination = myEntity.getModel().getModelGroup().getEntityNamed(destinationName);
        if (myDestination == null) {
          _failures.add(new MissingEntityFailure(destinationName));
        }
      }
    }

    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      join.resolve(_failures);
    }
  }

  public void verify(Set _failures) {
    String name = getName();
    if (name == null || name.trim().length() == 0) {
      _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + " has an empty name."));
    }
    else {
      if (name.indexOf(' ') != -1) {
        _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + "'s name has a space in it."));
      }
      if (!StringUtils.isLowercaseFirstLetter(name)) {
        _failures.add(new EOModelVerificationFailure("Relationship names should not be capitalized, but " + getFullyQualifiedName() + " is ."));
      }
    }
    if (isFlattened()) {
      if (myEntity.resolveKeyPath(getDefinition()) == null) {
        _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + " is flattened and either creates a loop or points to a non-existent target."));
      }
    }
    else {
      if (myDestination == null) {
        _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + " has no destination entity."));
      }
    }
    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      join.verify(_failures);
    }
  }

  public String getFullyQualifiedName() {
    return ((myEntity == null) ? "?" : myEntity.getFullyQualifiedName()) + "/Relationship:" + getName();
  }

  public String toString() {
    return "[EORelationship: name = " + myName + "; destination = " + ((myDestination == null) ? "null" : myDestination.getName()) + "; joins = " + myJoins + "]"; //$NON-NLS-4$ //$NON-NLS-5$
  }
}
