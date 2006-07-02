package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public abstract class TablePropertyLabelProvider implements ITableLabelProvider {
  private String[] myColumnProperties;

  public TablePropertyLabelProvider(String[] _columnProperties) {
    myColumnProperties = _columnProperties;
  }

  public abstract Image getColumnImage(Object _element, String _property);

  public Image getColumnImage(Object _element, int _columnIndex) {
    return getColumnImage(_element, myColumnProperties[_columnIndex]);
  }

  public abstract String getColumnText(Object _element, String _property);

  public String getColumnText(Object _element, int _columnIndex) {
    return getColumnText(_element, myColumnProperties[_columnIndex]);
  }

  protected Image yesNoImage(Boolean _bool, Image _yesImage, Image _noImage, Image _nullImage) {
    Image image;
    if (_bool == null) {
      image = _nullImage;
    }
    else if (_bool.booleanValue()) {
      image = _yesImage;
    }
    else {
      image = _noImage;
    }
    return image;
  }

  protected String yesNoText(Boolean _bool, boolean _nullIsNo) {
    String str;
    if (_bool == null) {
      if (_nullIsNo) {
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
}
