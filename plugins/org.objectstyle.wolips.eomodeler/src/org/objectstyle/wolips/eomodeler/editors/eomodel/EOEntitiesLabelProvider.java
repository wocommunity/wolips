package org.objectstyle.wolips.eomodeler.editors.eomodel;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.editors.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOEntitiesLabelProvider extends TablePropertyLabelProvider {
  public EOEntitiesLabelProvider(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Image getColumnImage(Object _element, String _property) {
    return null;
  }

  public String getColumnText(Object _element, String _property) {
    EOEntity entity = (EOEntity) _element;
    String text = null;
    if (_property == EOEntitiesConstants.NAME) {
      text = entity.getName();
    }
    else if (_property == EOEntitiesConstants.TABLE) {
      text = entity.getExternalName();
    }
    else if (_property == EOEntitiesConstants.CLASS_NAME) {
      text = entity.getClassName();
    }
    else if (_property == EOEntitiesConstants.PARENT) {
      EOEntity parent = entity.getParent();
      if (parent != null) {
        text = parent.getName();
      }
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return text;
  }

  public void addListener(ILabelProviderListener _listener) {
  }

  public void dispose() {
  }

  public boolean isLabelProperty(Object _element, String _property) {
    return true;
  }

  public void removeListener(ILabelProviderListener _listener) {
  }
}
