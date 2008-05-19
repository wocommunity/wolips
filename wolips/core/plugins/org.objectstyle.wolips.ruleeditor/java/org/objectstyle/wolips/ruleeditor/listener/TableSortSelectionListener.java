package org.objectstyle.wolips.ruleeditor.listener;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.objectstyle.wolips.ruleeditor.sorter.*;

public class TableSortSelectionListener implements SelectionListener {

	private final TableColumn column;

	private InvertableSorter currentSorter;

	private final boolean keepDirection;

	private final InvertableSorter sorter;

	private final TableViewer viewer;

	/**
	 * The constructor of this listener.
	 * 
	 * @param viewer
	 *            the tableviewer this listener belongs to
	 * @param column
	 *            the column this listener is responsible for
	 * @param sorter
	 *            the sorter this listener uses
	 * @param defaultDirection
	 *            the default sorting direction of this Listener. Possible
	 *            values are {@link SWT.UP} and {@link SWT.DOWN}
	 * @param keepDirection
	 *            if true, the listener will remember the last sorting direction
	 *            of the associated column and restore it when the column is
	 *            reselected. If false, the listener will use the default soting
	 *            direction
	 */
	public TableSortSelectionListener(final TableViewer viewer, final TableColumn column, final AbstractInvertableTableSorter sorter, final int defaultDirection, final boolean keepDirection) {
		this.viewer = viewer;
		this.column = column;
		this.keepDirection = keepDirection;
		this.sorter = (defaultDirection == SWT.UP) ? sorter : sorter.getInverseSorter();
		currentSorter = this.sorter;

		this.column.addSelectionListener(this);
	}

	/**
	 * Chooses the colum of this listener for sorting of the table. Mainly used
	 * when first initialising the table.
	 */
	public void chooseColumnForSorting() {
		viewer.getTable().setSortColumn(column);
		viewer.getTable().setSortDirection(currentSorter.getSortDirection());
		viewer.setSorter(currentSorter);
	}

	public void widgetDefaultSelected(final SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(final SelectionEvent e) {
		InvertableSorter newSorter;
		if (viewer.getTable().getSortColumn() == column) {
			newSorter = ((InvertableSorter) viewer.getSorter()).getInverseSorter();
		} else {
			if (keepDirection) {
				newSorter = currentSorter;
			} else {
				newSorter = sorter;
			}
		}

		currentSorter = newSorter;
		chooseColumnForSorting();
	}
}
