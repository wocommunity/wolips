package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TableSortHandler extends SelectionAdapter {
  private TableViewer myTableViewer;
  private String myProperty;

  public TableSortHandler(TableViewer _tableViewer, String _property) {
    myTableViewer = _tableViewer;
    myProperty = _property;
  }

  public void widgetSelected(SelectionEvent _event) {
    TablePropertyViewerSorter sorter = (TablePropertyViewerSorter) myTableViewer.getSorter();
    sorter.sort(myTableViewer, myProperty);
  }
}
