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

public class EORelationship implements IEOAttribute {
  private EOEntity myEntity;
  private String myName;
  private String myDestination;
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
    myJoins = new LinkedList();
    myRelationshipMap = new EOModelMap();
  }

  public void setDefinition(String _definition) {
    myDefinition = _definition;
  }

  public String getDefinition() {
    return myDefinition;
  }

  public void setClassProperty(Boolean _classProperty) {
    myClassProperty = _classProperty;
  }

  public Boolean isClassProperty() {
    return myClassProperty;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public void setName(String _name) throws DuplicateRelationshipNameException {
    myEntity._checkForDuplicateRelationshipName(this, _name);
    myName = _name;
  }

  public String getName() {
    return myName;
  }

  public String getDeleteRule() {
    return myDeleteRule;
  }

  public void setDeleteRule(String _deleteRule) {
    myDeleteRule = _deleteRule;
  }

  public EOEntity getDestination() {
    return myEntity.getModel().getModelGroup().getEntityNamed(myDestination);
  }

  public void setDestination(EOEntity _destination) {
    if (_destination == null) {
      myDestination = null;
    }
    else {
      myDestination = _destination.getName();
    }
  }

  public String getJoinSemantic() {
    return myJoinSemantic;
  }

  public void setJoinSemantic(String _joinSemantic) {
    myJoinSemantic = _joinSemantic;
  }

  public Boolean isMandatory() {
    return myMandatory;
  }

  public void setMandatory(Boolean _mandatory) {
    myMandatory = _mandatory;
  }

  public Boolean isOwnsDestination() {
    return myOwnsDestination;
  }

  public void setOwnsDestination(Boolean _ownsDestination) {
    myOwnsDestination = _ownsDestination;
  }

  public Boolean isPropagatesPrimaryKey() {
    return myPropagatesPrimaryKey;
  }

  public void setPropagatesPrimaryKey(Boolean _propagatesPrimaryKey) {
    myPropagatesPrimaryKey = _propagatesPrimaryKey;
  }

  public Boolean isToMany() {
    return myToMany;
  }

  public void setToMany(Boolean _toMany) {
    myToMany = _toMany;
  }

  public void clearJoins() {
    myJoins.clear();
  }

  public void addJoin(EOJoin _join) {
    // TODO: Check duplicates
    myJoins.add(_join);
  }

  public void removeJoin(EOJoin _join) {
    myJoins.remove(_join);
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
    myDestination = _relationshipMap.getString("destination", true);
    myDefinition = _relationshipMap.getString("definition", true);
    myMandatory = _relationshipMap.getBoolean("isMandatory");
    myToMany = _relationshipMap.getBoolean("isToMany");
    myJoinSemantic = _relationshipMap.getString("joinSemantic", true);
    myName = _relationshipMap.getString("name", true);
    myDeleteRule = _relationshipMap.getString("deleteRule", true);
    myOwnsDestination = _relationshipMap.getBoolean("ownsDestination");
    myPropagatesPrimaryKey = _relationshipMap.getBoolean("propagatesPrimaryKey"); // TODO: verify
    List joins = _relationshipMap.getList("joins");
    if (joins != null) {
      Iterator joinsIter = joins.iterator();
      while (joinsIter.hasNext()) {
        EOModelMap joinMap = new EOModelMap((Map) joinsIter.next());
        EOJoin join = new EOJoin(this);
        join.loadFromMap(joinMap);
        addJoin(join);
      }
    }
    myUserInfo = _relationshipMap.getMap("userInfo", true);
  }

  public EOModelMap toMap() {
    EOModelMap relationshipMap = myRelationshipMap.cloneModelMap();
    relationshipMap.setString("destination", myDestination, true);
    relationshipMap.setString("definition", myDefinition, true);
    relationshipMap.setBoolean("isMandatory", myMandatory);
    relationshipMap.setBoolean("isToMany", myToMany);
    relationshipMap.setString("joinSemantic", myJoinSemantic, true);
    relationshipMap.setString("name", myName, true);
    relationshipMap.setString("deleteRule", myDeleteRule, true);
    relationshipMap.setBoolean("ownsDestination", myOwnsDestination);
    relationshipMap.setBoolean("propagatesPrimaryKey", myPropagatesPrimaryKey); // TODO: verify
    List joins = new LinkedList();
    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      EOModelMap joinMap = join.toMap();
      joins.add(joinMap);
    }
    relationshipMap.setList("joins", joins);
    relationshipMap.setMap("userInfo", myUserInfo);
    return relationshipMap;
  }

  public void verify(List _failures) {
    if (myDestination != null && getDestination() == null) {
      _failures.add(new MissingEntityFailure(myDestination));
    }

    // TODO

    Iterator joinsIter = myJoins.iterator();
    while (joinsIter.hasNext()) {
      EOJoin join = (EOJoin) joinsIter.next();
      join.verify(_failures);
    }
  }

  public String toString() {
    return "[EORelationship: destination = " + myDestination + "; joins = " + myJoins + "]";
  }
}
