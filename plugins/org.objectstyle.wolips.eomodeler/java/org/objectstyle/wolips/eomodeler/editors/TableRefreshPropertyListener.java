package org.objectstyle.wolips.eomodeler.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.TableViewer;

public class TableRefreshPropertyListener implements PropertyChangeListener {
  private TableViewer myTableViewer;
  private String myPropertyName;

  public TableRefreshPropertyListener(TableViewer _tableViewer, String _propertyName) {
    myTableViewer = _tableViewer;
    myPropertyName = _propertyName;
  }

  public void propertyChange(PropertyChangeEvent _event) {
    String changedPropertyName = _event.getPropertyName();
    if (myPropertyName.equals(changedPropertyName)) {
      myTableViewer.refresh();
    }
  }
}
