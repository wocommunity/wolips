package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.TreeViewer;

public class TreeRefreshPropertyListener implements PropertyChangeListener {
  private TreeViewer myTreeViewer;
  private String myPropertyName;

  public TreeRefreshPropertyListener(TreeViewer _treeViewer, String _propertyName) {
    myTreeViewer = _treeViewer;
    myPropertyName = _propertyName;
  }

  public void propertyChange(PropertyChangeEvent _event) {
    String changedPropertyName = _event.getPropertyName();
    if (myPropertyName.equals(changedPropertyName)) {
      myTreeViewer.refresh();
    }
  }
}
