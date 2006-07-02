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
}
