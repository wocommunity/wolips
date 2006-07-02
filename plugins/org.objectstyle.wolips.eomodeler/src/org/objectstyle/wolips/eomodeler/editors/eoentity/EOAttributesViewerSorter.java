package org.objectstyle.wolips.eomodeler.editors.eoentity;

import org.objectstyle.wolips.eomodeler.editors.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;

public class EOAttributesViewerSorter extends TablePropertyViewerSorter {

  public EOAttributesViewerSorter(String[] _properties) {
    super(_properties);
  }

  public int compare(Object _o1, Object _o2, String _property) {
    EOAttribute attribute1 = (EOAttribute) _o1;
    EOAttribute attribute2 = (EOAttribute) _o2;
    Object value1 = null;
    Object value2 = null;
    if (_property == EOAttributesConstants.PRIMARY_KEY) {
      value1 = attribute1.isPrimaryKey();
      value2 = attribute2.isPrimaryKey();
    }
    else if (_property == EOAttributesConstants.LOCKING) {
      value1 = attribute1.isUsedForLocking();
      value2 = attribute2.isUsedForLocking();
    }
    else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
      value1 = attribute1.isClassProperty();
      value2 = attribute2.isClassProperty();
    }
    else if (_property == EOAttributesConstants.ALLOW_NULL) {
      value1 = attribute1.isAllowsNull();
      value2 = attribute2.isAllowsNull();
    }
    else if (_property == EOAttributesConstants.NAME) {
      value1 = attribute1.getName();
      value2 = attribute2.getName();
    }
    else if (_property == EOAttributesConstants.COLUMN) {
      value1 = attribute1.getColumnName();
      value2 = attribute2.getColumnName();
    }
    else if (_property == EOAttributesConstants.PROTOTYPE) {
      value1 = attribute1.getPrototypeName();
      value2 = attribute2.getPrototypeName();
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
    else if (value1 instanceof Boolean) {
      comparison = ((Boolean) value1).compareTo((Boolean) value2);
    }
    else if (value1 instanceof Integer) {
      comparison = ((Integer) value1).compareTo((Integer) value2);
    }
    else if (value1 instanceof String) {
      comparison = collator.compare(value1, value2);
    }

    return comparison;
  }
}
