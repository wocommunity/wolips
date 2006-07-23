package org.objectstyle.wolips.eomodeler.utils;

import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.IEOEntityRelative;

public class EOModelUtils {
  public static EOModel getRelatedModel(Object _obj) {
    EOModel model = null;
    if (_obj instanceof EOModel) {
      model = (EOModel) _obj;
    }
    else if (_obj instanceof IEOEntityRelative) {
      model = ((IEOEntityRelative) _obj).getEntity().getModel();
    }
    return model;
  }

  public static EOEntity getRelatedEntity(Object _obj) {
    EOEntity entity = null;
    if (_obj instanceof IEOEntityRelative) {
      entity = ((IEOEntityRelative) _obj).getEntity();
    }
    return entity;
  }
}
