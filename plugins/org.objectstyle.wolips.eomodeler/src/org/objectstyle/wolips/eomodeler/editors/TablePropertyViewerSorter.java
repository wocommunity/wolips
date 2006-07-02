package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TablePropertyViewerSorter extends ViewerSorter {
  private String[] myColumnProperties;
  private int mySortedColumn;
  private int myDirection;

  public TablePropertyViewerSorter(String[] _columnProperties) {
    myColumnProperties = _columnProperties;
  }

  public void sort(Viewer _viewer, String _property) {
    int matchingColumn = TableUtils.getColumnNumber(myColumnProperties, _property);
    if (matchingColumn != -1) {
      sort(_viewer, matchingColumn);
    }
  }

  public void sort(Viewer _viewer, int _column) {
    TableViewer tableViewer = (TableViewer) _viewer;
    Table table = tableViewer.getTable();
    TableColumn sortColumn = table.getSortColumn();
    TableColumn selectedColumn = table.getColumn(_column);
    int direction = table.getSortDirection();
    if (sortColumn == selectedColumn) {
      direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
    }
    else {
      table.setSortColumn(selectedColumn);
      direction = SWT.UP;
    }
    table.setSortDirection(direction);
    mySortedColumn = _column;
    myDirection = direction;
    tableViewer.refresh();
  }

  public abstract int compare(Object _o1, Object _o2, String _property);

  public int compare(Viewer _viewer, Object _o1, Object _o2) {
    String property = myColumnProperties[mySortedColumn];
    int comparison = compare(_o1, _o2, property);

    if (myDirection == SWT.DOWN) {
      comparison = -comparison;
    }

    return comparison;
  }
}
