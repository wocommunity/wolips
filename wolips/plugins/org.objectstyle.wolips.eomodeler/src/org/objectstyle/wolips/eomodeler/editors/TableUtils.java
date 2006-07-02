package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableUtils {
  public static void packTableColumns(TableViewer _viewer) {
    Table table = _viewer.getTable();
    int columnCount = table.getColumnCount();
    for (int columnNum = 0; columnNum < columnCount; columnNum++) {
      table.getColumn(columnNum).pack();
    }
  }

  public static void createTableColumns(TableViewer _viewer, String[] _properties) {
    Table table = _viewer.getTable();
    for (int columnNum = 0; columnNum < _properties.length; columnNum++) {
      TableColumn column = new TableColumn(table, SWT.LEFT);
      column.setMoveable(true);
      column.setText(_properties[columnNum]);
      column.addSelectionListener(new TableSortHandler(_viewer, _properties[columnNum]));
    }

    table.setSortColumn(table.getColumn(0));
    table.setSortDirection(SWT.UP);
  }

  public static int getColumnNumber(String[] _properties, String _property) {
    int matchingColumnIndex = -1;
    for (int columnNum = 0; columnNum < _properties.length; columnNum++) {
      if (_properties[columnNum].equals(_property)) {
        matchingColumnIndex = columnNum;
      }
    }
    return matchingColumnIndex;
  }

}
