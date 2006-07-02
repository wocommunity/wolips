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

  public abstract Object getComparisonValue(Object _obj, String _property);

  public int compare(Viewer _viewer, Object _o1, Object _o2) {
    String property = myColumnProperties[mySortedColumn];
    Object o1 = getComparisonValue(_o1, property);
    Object o2 = getComparisonValue(_o2, property);
    int comparison = 0;
    if (o1 == null && o2 == null) {
      comparison = 0;
    }
    else if (o1 == null) {
      comparison = -1;
    }
    else if (o2 == null) {
      comparison = 1;
    }
    else if (o1 instanceof Boolean) {
      comparison = ((Boolean) o1).compareTo((Boolean) o2);
    }
    else if (o1 instanceof Integer) {
      comparison = ((Integer) o1).compareTo((Integer) o2);
    }
    else if (o1 instanceof String) {
      comparison = collator.compare(o1, o2);
    }

    if (myDirection == SWT.DOWN) {
      comparison = -comparison;
    }

    return comparison;
  }
}
