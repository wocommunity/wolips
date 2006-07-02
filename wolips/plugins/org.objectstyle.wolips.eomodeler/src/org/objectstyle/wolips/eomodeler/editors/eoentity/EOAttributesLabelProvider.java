package org.objectstyle.wolips.eomodeler.editors.eoentity;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.editors.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;

public class EOAttributesLabelProvider extends TablePropertyLabelProvider {
  public EOAttributesLabelProvider(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Image getColumnImage(Object _element, String _property) {
    return null;
  }

  protected String yesNo(EOAttribute _attribute, Boolean _bool) {
    String str;
    if (_bool == null) {
      if (!_attribute.getEntity().isPrototype()) {
        str = "N";
      }
      else {
        str = "";
      }
    }
    else if (_bool.booleanValue()) {
      str = "Y";
    }
    else {
      str = "N";
    }
    return str;
  }

  public String getColumnText(Object _element, String _property) {
    EOAttribute attribute = (EOAttribute) _element;
    String text = null;
    if (_property == EOAttributesConstants.PRIMARY_KEY) {
      text = yesNo(attribute, attribute.isPrimaryKey());
    }
    else if (_property == EOAttributesConstants.LOCKING) {
      text = yesNo(attribute, attribute.isUsedForLocking());
    }
    else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
      text = yesNo(attribute, attribute.isClassProperty());
    }
    else if (_property == EOAttributesConstants.ALLOW_NULL) {
      text = yesNo(attribute, attribute.isAllowsNull());
    }
    else if (_property == EOAttributesConstants.NAME) {
      text = attribute.getName();
    }
    else if (_property == EOAttributesConstants.COLUMN) {
      text = attribute.getColumnName();
    }
    else if (_property == EOAttributesConstants.PROTOTYPE) {
      EOAttribute prototype = attribute.getPrototype();
      if (prototype != null) {
        text = prototype.getName();
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
