package org.objectstyle.wolips.eomodeler.editors.relationships;

import org.objectstyle.wolips.eomodeler.editors.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOJoin;
import org.objectstyle.wolips.eomodeler.model.EORelationship;

public class EORelationshipsViewerSorter extends TablePropertyViewerSorter {

  public EORelationshipsViewerSorter(String[] _properties) {
    super(_properties);
  }

  public Object getComparisonValue(Object _obj, String _property) {
    EORelationship relationship = (EORelationship) _obj;
    Object value = null;
    if (_property == EORelationshipsConstants.TO_MANY) {
      value = relationship.isToMany();
    }
    else if (_property == EORelationshipsConstants.CLASS_PROPERTY) {
      value = relationship.isClassProperty();
    }
    else if (_property == EORelationshipsConstants.NAME) {
      value = relationship.getName();
    }
    else if (_property == EORelationshipsConstants.DESTINATION) {
      EOEntity destination = relationship.getDestination();
      if (destination != null) {
        value = destination.getName();
      }
    }
    else if (_property == EORelationshipsConstants.SOURCE_ATTRIBUTE) {
      EOJoin firstJoin = relationship.getFirstJoin();
      if (firstJoin != null) {
        value = firstJoin.getSourceAttribute().getName();
      }
    }
    else if (_property == EORelationshipsConstants.DESTINATION_ATTRIBUTE) {
      EOJoin firstJoin = relationship.getFirstJoin();
      if (firstJoin != null) {
        value = firstJoin.getDestinationAttribute().getName();
      }
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return value;
  }
}
