package org.objectstyle.wolips.eomodeler.editors.attributes;

import org.objectstyle.wolips.eomodeler.editors.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;

public class EOAttributesViewerSorter extends TablePropertyViewerSorter {

  public EOAttributesViewerSorter(String[] _properties) {
    super(_properties);
  }

  public Object getComparisonValue(Object _obj, String _property) {
    EOAttribute attribute = (EOAttribute) _obj;
    Object value = null;
    if (_property == EOAttributesConstants.PRIMARY_KEY) {
      value = attribute.isPrimaryKey();
    }
    else if (_property == EOAttributesConstants.LOCKING) {
      value = attribute.isUsedForLocking();
    }
    else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
      value = attribute.isClassProperty();
    }
    else if (_property == EOAttributesConstants.ALLOW_NULL) {
      value = attribute.isAllowsNull();
    }
    else if (_property == EOAttributesConstants.NAME) {
      value = attribute.getName();
    }
    else if (_property == EOAttributesConstants.COLUMN) {
      value = attribute.getColumnName();
    }
    else if (_property == EOAttributesConstants.PROTOTYPE) {
      value = attribute.getPrototypeName();
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return value;
  }
}
