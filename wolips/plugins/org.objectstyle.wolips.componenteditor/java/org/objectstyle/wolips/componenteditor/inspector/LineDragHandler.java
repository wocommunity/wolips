package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.internal.dnd.DragUtil;
import org.eclipse.ui.internal.dnd.IDropTarget2;
import org.objectstyle.wolips.componenteditor.inspector.WOBrowser.WOBrowserColumn;

public class LineDragHandler implements DragSourceListener, IDropTarget2, PaintListener, DropTargetListener {
	private WOBrowserColumn _browser;

	private Point _startingPoint;

	private Point _currentPoint;

	private Canvas _lineCanvas;

	public LineDragHandler(WOBrowserColumn browser) {
		_browser = browser;
	}

	public Shell getShell() {
		return _browser.getShell();
	}

	public void createCanvas() {
		Shell shell = getShell();
		// shell.addPaintListener(this);
		// _parent.addPaintListener(this);
		if (_lineCanvas == null) {
			_lineCanvas = new Canvas(shell, SWT.NO_BACKGROUND);
			_lineCanvas.setLocation(0, 0);
			_lineCanvas.setSize(shell.getSize());
			_lineCanvas.moveAbove(null);
			_lineCanvas.addPaintListener(this);
			_lineCanvas.setCapture(false);
			_lineCanvas.setEnabled(false);

			DropTarget dropTarget = new DropTarget(_lineCanvas, DND.DROP_NONE | DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
			LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			transfer.setSelection(new StructuredSelection("Test"));
			dropTarget.setTransfer(new Transfer[] { transfer });
			dropTarget.addDropListener(this);
		}
	}

	public void disposeCanvas() {
		// getShell().removePaintListener(this);
		// _parent.removePaintListener(this);
		if (_lineCanvas != null) {
			if (!_lineCanvas.isDisposed()) {
				_lineCanvas.removePaintListener(this);
				_lineCanvas.dispose();
			}
			_lineCanvas = null;
		}
	}

	public void paintControl(PaintEvent e) {
		if (_startingPoint != null && _currentPoint != null) {
			e.gc.setForeground(e.widget.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			e.gc.setBackground(e.widget.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			Point startingPoint = getShell().toControl(_startingPoint);
			Point currentPoint = getShell().toControl(_currentPoint);
			// e.gc.setAlpha(150);
			e.gc.setLineWidth(2);
			// e.gc.setLineCap(SWT.CAP_ROUND);
			e.gc.setLineJoin(SWT.JOIN_ROUND);
			e.gc.drawLine(startingPoint.x, startingPoint.y, currentPoint.x, startingPoint.y);
			e.gc.drawLine(currentPoint.x, startingPoint.y, currentPoint.x, currentPoint.y);

			int box = 3;
			// e.gc.setAlpha(255);
			e.gc.fillRectangle(startingPoint.x - box, startingPoint.y - box, box * 2, box * 2);
		}
	}

	public void register() {
		DropTarget dropTarget = new DropTarget(_browser, DND.DROP_NONE | DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		transfer.setSelection(new StructuredSelection("Test"));
		dropTarget.setTransfer(new Transfer[] { transfer });
		dropTarget.addDropListener(this);

		// DragUtil.addDragTarget(_parent, this);
		// DragUtil.addDragTarget(getShell(), this);
		// DragUtil.addDragTarget(null, this);
	}

	public void dispose() {
		// DragUtil.removeDragTarget(_shell, this);
		// DragUtil.removeDragTarget(null, this);
		disposeCanvas();
	}

	public void dragEnter(DropTargetEvent event) {
		System.out.println("LineDragHandler.dragEnter: " + event);
		// event.feedback = DND.FEEDBACK_SELECT;
		event.detail = DND.DROP_COPY;
	}

	public void dragLeave(DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	public void dragOperationChanged(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	public void dragOver(DropTargetEvent event) {
		// DropTarget source = (DropTarget) event.getSource();
		_currentPoint = new Point(event.x, event.y);
		_lineCanvas.redraw();

		Control control = LineDragHandler.findControl(getShell(), _currentPoint, _lineCanvas);
		System.out.println("LineDragHandler.dragOver: " + control);
		event.detail = DND.DROP_COPY;
	}

	public void drop(DropTargetEvent event) {
		System.out.println("LineDragHandler.drop: drop");
		disposeCanvas();
	}

	public void dropAccept(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	public void dragFinished(boolean dropPerformed) {
		System.out.println("LineDragHandler.dragFinished: " + dropPerformed);
		disposeCanvas();
	}

	public void drop() {
		System.out.println("LineDragHandler.drop: " + _currentPoint);
	}

	public Cursor getCursor() {
		return null;
	}

	public Rectangle getSnapRectangle() {
		return null;
	}

	public void dragFinished(DragSourceEvent event) {
		System.out.println("LineDragHandler.dragFinished: ");
		disposeCanvas();
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = _browser.getSelection();
	}

	public void dragStart(DragSourceEvent event) {
		createCanvas();
		DragSource dragSource = (DragSource) event.getSource();
		Control control = dragSource.getControl();
		_startingPoint = null;
		_currentPoint = null;

		event.detail = DND.DROP_COPY;

		event.doit = false;

		ISelection selection = _browser.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			Control listControl = _browser.getViewer().getControl();
			Rectangle listBounds = listControl.getBounds();
			TableItem itemControl = (TableItem) _browser.getViewer().testFindItem(obj);
			Rectangle itemBounds = itemControl.getBounds();
			int magicRightMarginOnMac = 27;
			_startingPoint = control.toDisplay(new Point(listBounds.x + listBounds.width - magicRightMarginOnMac, itemBounds.y + itemBounds.height / 2));
			event.doit = true;
		}
	}

	public static Control findControl(Control[] toSearch, Point locationToFind, Control ignoreControl) {
		for (int idx = toSearch.length - 1; idx >= 0; idx--) {
			Control next = toSearch[idx];
			if (next != ignoreControl && !next.isDisposed() && next.isVisible()) {
				Rectangle bounds = DragUtil.getDisplayBounds(next);
				if (bounds.contains(locationToFind)) {
					if (next instanceof Composite) {
						Control result = LineDragHandler.findControl((Composite) next, locationToFind, ignoreControl);
						if (result != null) {
							return result;
						}
					}
					return next;
				}
			}
		}

		return null;
	}

	/**
	 * Finds the control in the given location
	 * 
	 * @param toSearch
	 * @param locationToFind
	 *            location (in display coordinates)
	 * @return
	 */
	public static Control findControl(Composite toSearch, Point locationToFind, Control ignoreControl) {
		Control[] children = toSearch.getChildren();
		return LineDragHandler.findControl(children, locationToFind, ignoreControl);
	}
}
