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
