package org.objectstyle.wolips.eomodeler.editors.entities;

import org.objectstyle.wolips.eomodeler.editors.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOEntitiesViewerSorter extends TablePropertyViewerSorter {

  public EOEntitiesViewerSorter(String[] _properties) {
    super(_properties);
  }

  public Object getComparisonValue(Object _obj, String _property) {
    EOEntity entity = (EOEntity) _obj;
    Object value = null;
    if (_property == EOEntitiesConstants.NAME) {
      value = entity.getName();
    }
    else if (_property == EOEntitiesConstants.TABLE) {
      value = entity.getExternalName();
    }
    else if (_property == EOEntitiesConstants.CLASS_NAME) {
      value = entity.getClassName();
    }
    else if (_property == EOEntitiesConstants.PARENT) {
      value = entity.getParentName();
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return value;
  }
}
