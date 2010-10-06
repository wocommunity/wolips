package org.objectstyle.wolips.baseforuiplugins.utils;

import java.util.LinkedList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellNavigationStrategy;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;

public class WOTreeCellNavigationStrategy extends CellNavigationStrategy {
	public void collapse(ColumnViewer viewer, ViewerCell cellToCollapse, Event event) {
		if (cellToCollapse != null) {
			((TreeItem) cellToCollapse.getItem()).setExpanded(false);
		}
	}

	public void expand(ColumnViewer viewer, ViewerCell cellToExpand, Event event) {
		if (cellToExpand != null) {
			TreeViewer v = (TreeViewer) viewer;

			TreeItem item = (TreeItem)cellToExpand.getItem();
			LinkedList<Object> segments = new LinkedList<Object>();
			while (item != null) {
				Object segment = item.getData();
				Assert.isNotNull(segment);
				segments.addFirst(segment);
				item = item.getParentItem();
			}
			TreePath selectedPath = new TreePath(segments.toArray());
			v.setExpandedState(selectedPath, true);
		}
	}

	public boolean isCollapseEvent(ColumnViewer viewer, ViewerCell cellToCollapse, Event event) {

		if (cellToCollapse == null) {
			return false;
		}

		return cellToCollapse != null && ((TreeItem) cellToCollapse.getItem()).getExpanded() && event.keyCode == SWT.ARROW_LEFT && isFirstColumnCell(cellToCollapse);
	}

	public boolean isExpandEvent(ColumnViewer viewer, ViewerCell cellToExpand, Event event) {
		if (cellToExpand == null) {
			return false;
		}
		return cellToExpand != null && ((TreeItem) cellToExpand.getItem()).getItemCount() > 0 && !((TreeItem) cellToExpand.getItem()).getExpanded() && event.keyCode == SWT.ARROW_RIGHT && isFirstColumnCell(cellToExpand);
	}

	@SuppressWarnings("unused")
	private boolean isFirstColumnCell(ViewerCell cell) {
		return true;
	}
}
