package org.objectstyle.wolips.eomodeler.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.TableViewer;

public class TableRowRefreshPropertyListener implements PropertyChangeListener {
  private TableViewer myTableViewer;
  private String myPropertyName;

  public TableRowRefreshPropertyListener(TableViewer _tableViewer, String _propertyName) {
    myTableViewer = _tableViewer;
    myPropertyName = _propertyName;
  }

  public void propertyChange(PropertyChangeEvent _event) {
    String changedPropertyName = _event.getPropertyName();
    if (myPropertyName.equals(changedPropertyName)) {
      Object newValue = _event.getNewValue();
      myTableViewer.refresh(newValue, true);
    }
  }
}
