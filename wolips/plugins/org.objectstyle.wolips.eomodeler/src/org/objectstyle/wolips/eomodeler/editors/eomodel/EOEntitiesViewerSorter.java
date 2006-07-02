package org.objectstyle.wolips.eomodeler.editors.eomodel;

import org.objectstyle.wolips.eomodeler.editors.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOEntitiesViewerSorter extends TablePropertyViewerSorter {

  public EOEntitiesViewerSorter(String[] _properties) {
    super(_properties);
  }

  public int compare(Object _o1, Object _o2, String _property) {
    EOEntity entity1 = (EOEntity) _o1;
    EOEntity entity2 = (EOEntity) _o2;
    Object value1 = null;
    Object value2 = null;
    if (_property == EOEntitiesConstants.NAME) {
      value1 = entity1.getName();
      value2 = entity2.getName();
    }
    else if (_property == EOEntitiesConstants.TABLE) {
      value1 = entity1.getExternalName();
      value2 = entity2.getExternalName();
    }
    else if (_property == EOEntitiesConstants.CLASS_NAME) {
      value1 = entity1.getClassName();
      value2 = entity2.getClassName();
    }
    else if (_property == EOEntitiesConstants.PARENT) {
      value1 = entity1.getParentName();
      value2 = entity2.getParentName();
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }

    int comparison = 0;
    if (value1 == null && value2 == null) {
      comparison = 0;
    }
    else if (value1 == null) {
      comparison = -1;
    }
    else if (value2 == null) {
      comparison = 1;
    }
    else {
      comparison = collator.compare(value1, value2);
    }

    return comparison;
  }
}
