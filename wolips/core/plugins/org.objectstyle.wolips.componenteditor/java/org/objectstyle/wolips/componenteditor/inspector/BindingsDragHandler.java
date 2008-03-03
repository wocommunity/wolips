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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.internal.dnd.IDropTarget2;

public class BindingsDragHandler implements DragSourceListener, IDropTarget2, PaintListener, DropTargetListener {
	private static final int endpointSize = 3;
	
	private WOBrowserColumn _browserColumn;

	private Point _startingPoint;

	private Point _currentPoint;

	private Canvas _lineCanvas;

	public BindingsDragHandler(WOBrowserColumn browserColumn) {
		_browserColumn = browserColumn;
	}

	public Shell getShell() {
		return _browserColumn.getShell();
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
			Point startingPoint = getShell().toControl(_startingPoint);
			Point currentPoint = getShell().toControl(_currentPoint);

			e.gc.setForeground(e.widget.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			e.gc.setBackground(e.widget.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			
			// e.gc.setAlpha(150);
			e.gc.setLineWidth(2);
			e.gc.setLineJoin(SWT.JOIN_ROUND);

			e.gc.drawOval(currentPoint.x - endpointSize, currentPoint.y - endpointSize, endpointSize * 2, endpointSize * 2);
			
			e.gc.drawLine(startingPoint.x, startingPoint.y, currentPoint.x, startingPoint.y);
			e.gc.drawLine(currentPoint.x, startingPoint.y, currentPoint.x, currentPoint.y);

			// e.gc.setAlpha(255);
			e.gc.fillRectangle(startingPoint.x - endpointSize, startingPoint.y - endpointSize, endpointSize * 2, endpointSize * 2);
		}
	}

	public void register() {
		DropTarget dropTarget = new DropTarget(_browserColumn, DND.DROP_NONE | DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
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
		// System.out.println("LineDragHandler.dragEnter: " + event);
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
		Point lastPoint = _currentPoint;
		_currentPoint = new Point(event.x, event.y);

		Point startingPoint = getShell().toControl(_startingPoint);
		Point currentPoint = getShell().toControl(_currentPoint);

		int redrawX1 = Math.min(startingPoint.x, currentPoint.x);
		int redrawY1 = Math.min(startingPoint.y, currentPoint.y);
		int redrawX2 = Math.max(startingPoint.x, currentPoint.x);
		int redrawY2 = Math.max(startingPoint.y, currentPoint.y);
		if (lastPoint != null) {
			lastPoint = getShell().toControl(lastPoint);
			redrawX1 = Math.min(redrawX1, lastPoint.x);
			redrawY1 = Math.min(redrawY1, lastPoint.y);
			redrawX2 = Math.max(redrawX2, lastPoint.x);
			redrawY2 = Math.max(redrawY2, lastPoint.y);
		}
		int slop = 5 + endpointSize;
		redrawX1 -= slop;
		redrawY1 -= slop;
		redrawX2 += slop;
		redrawY2 += slop;
		_lineCanvas.redraw(redrawX1, redrawY1, redrawX2 - redrawX1, redrawY2 - redrawY1, true);

		event.detail = DND.DROP_COPY;

		if (_browserColumn.getDelegate() != null) {
			_browserColumn.getDelegate().bindingDragging(_browserColumn, _currentPoint);
		}
	}

	public void drop(DropTargetEvent event) {
		// System.out.println("LineDragHandler.drop(DropTargetEvent): drop");
		// disposeCanvas();
		_currentPoint = new Point(event.x, event.y);
	}

	public void dropAccept(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	public void dragFinished(boolean dropPerformed) {
		disposeCanvas();
	}

	public void drop() {
		// System.out.println("LineDragHandler.drop: " + _currentPoint);
	}

	public Cursor getCursor() {
		return null;
	}

	public Rectangle getSnapRectangle() {
		return null;
	}

	public void dragFinished(DragSourceEvent event) {
		disposeCanvas();

		if (_browserColumn.getDelegate() != null) {
			_browserColumn.getDelegate().bindingDragged(_browserColumn, _currentPoint);
		}

		// System.out.println("LineDragHandler.dragFinished: " +
		// getShell().getDisplay().getCursorControl());
		// Control control = SwtUtil.findControl(getShell(), _currentPoint);
		// System.out.println("LineDragHandler.dragFinished: control = " +
		// control);
		// IDropTarget dropTarget = DragUtil.getDropTarget(control, "Test",
		// _currentPoint, control.getBounds());
		// System.out.println("LineDragHandler.dragFinished: dropTarget = " +
		// dropTarget);
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = _browserColumn.getSelection();
	}

	public void dragStart(DragSourceEvent event) {
		createCanvas();
		DragSource dragSource = (DragSource) event.getSource();
		Control control = dragSource.getControl();
		_startingPoint = null;
		_currentPoint = null;

		event.detail = DND.DROP_COPY;

		event.doit = false;

		ISelection selection = _browserColumn.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			Control listControl = _browserColumn.getViewer().getControl();
			Rectangle listBounds = listControl.getBounds();
			TableItem itemControl = (TableItem) _browserColumn.getViewer().testFindItem(obj);
			Rectangle itemBounds = itemControl.getBounds();
			int magicRightMarginOnMac = 27;
			_startingPoint = control.toDisplay(new Point(listBounds.x + listBounds.width - magicRightMarginOnMac, itemBounds.y + itemBounds.height / 2));
			event.doit = true;
		}
	}
}
