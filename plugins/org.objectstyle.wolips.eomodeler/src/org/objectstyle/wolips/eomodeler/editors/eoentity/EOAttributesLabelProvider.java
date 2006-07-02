package org.objectstyle.wolips.eomodeler.editors.eoentity;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.editors.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;

public class EOAttributesLabelProvider extends TablePropertyLabelProvider implements ITableColorProvider, ITableFontProvider {
  public EOAttributesLabelProvider(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Image getColumnImage(Object _element, String _property) {
    EOAttribute attribute = (EOAttribute) _element;
    Image image = null;
    if (_property == EOAttributesConstants.PRIMARY_KEY) {
      image = yesNoImage(attribute.isPrimaryKey(), Activator.getDefault().getImageRegistry().get(EOAttributesConstants.PRIMARY_KEY));
    }
    else if (_property == EOAttributesConstants.LOCKING) {
      image = yesNoImage(attribute.isUsedForLocking(), Activator.getDefault().getImageRegistry().get(EOAttributesConstants.LOCKING));
    }
    else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
      image = yesNoImage(attribute.isClassProperty(), Activator.getDefault().getImageRegistry().get(EOAttributesConstants.CLASS_PROPERTY));
    }
    else if (_property == EOAttributesConstants.ALLOW_NULL) {
      image = yesNoImage(attribute.isAllowsNull(), Activator.getDefault().getImageRegistry().get("check"));
    }
    return image;
  }

  protected Image yesNoImage(Boolean _bool, Image _image) {
    Image image;
    if (_bool == null) {
      image = null;
    }
    else if (_bool.booleanValue()) {
      image = _image;
    }
    else {
      image = null;
    }
    return image;
  }

  protected String yesNoText(EOAttribute _attribute, Boolean _bool) {
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
      text = null;
      //yesNoText(attribute, attribute.isPrimaryKey());
    }
    else if (_property == EOAttributesConstants.LOCKING) {
      text = null;
      //yesNoText(attribute, attribute.isUsedForLocking());
    }
    else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
      text = null;
      //yesNoText(attribute, attribute.isClassProperty());
    }
    else if (_property == EOAttributesConstants.ALLOW_NULL) {
      text = null;
      //yesNoText(attribute, attribute.isAllowsNull());
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

  public Font getFont(Object _element, int _columnIndex) {
    EOAttribute attribute = (EOAttribute) _element;
    return null;
  }

  public Color getBackground(Object _element, int _columnIndex) {
    EOAttribute attribute = (EOAttribute) _element;
    return null;
  }

  public Color getForeground(Object _element, int _columnIndex) {
    EOAttribute attribute = (EOAttribute) _element;
    return null;
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
