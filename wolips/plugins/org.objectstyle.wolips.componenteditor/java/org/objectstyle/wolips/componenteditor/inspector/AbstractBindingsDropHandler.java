package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractBindingsDropHandler<T, U, V, W extends Control> implements IWOBrowserDelegate, IAutoscroller.Delegate {
	private V _bindingsAnnotation;

	private U _selectedItem;

	private IAutoscroller _autoscroller;

	private PopAnimator _popper;

	private W _editorControl;

	@SuppressWarnings("unchecked")
	public AbstractBindingsDropHandler(W editorControl) {
		_editorControl = editorControl;

		_popper = new PopAnimator();
		_popper.setControl(_editorControl);

		_autoscroller = createAutoscroller(_editorControl);
		_autoscroller.setDelegate(this);
	}

	public W getEditorControl() {
		return _editorControl;
	}

	public void autoscrollOccurred(IAutoscroller scroller) {
		removeHoverAnnotation();
	}

	public void dispose() {
		if (_popper != null) {
			if (!_editorControl.isDisposed()) {
				_editorControl.removePaintListener(_popper);
			}
			_popper.dispose();
		}
	}

	public synchronized V getBindingsAnnotation() {
		return _bindingsAnnotation;
	}

	protected synchronized void addHoverAnnotation(U selectedItem) {
		_bindingsAnnotation = _addHoverAnnotation(selectedItem);
		_popper.startAnimation();
	}

	protected synchronized void removeHoverAnnotation() {
		_popper.stopAnimation();
		if (_bindingsAnnotation != null) {
			_removeHoverAnnotation(_bindingsAnnotation);
			_bindingsAnnotation = null;
			_selectedItem = null;
		}
	}

	protected boolean isEditorActive() {
		return _editorControl.isVisible();
	}

	public void bindingDragging(WOBrowserColumn column, Point dragPoint) {
		try {
			// If the editor isn't visible, don't allow dragging
			if (!isEditorActive()) {
				removeHoverAnnotation();
				return;
			}

			U previousSelectedItem = _selectedItem;
			Point controlDragPoint = _editorControl.toControl(dragPoint);
			_autoscroller.autoscroll(controlDragPoint);

			Rectangle controlBounds = _editorControl.getBounds();
			controlBounds.x = 0;
			controlBounds.y = 0;
			// Don't bother doing anything if we're outside of the Template
			// Editor's bounds
			if (controlBounds.contains(controlDragPoint)) {
				T selectedContainer = getSelectedContainerAtPoint(controlDragPoint, false);
				// We only want to throb WO tags ...
				if (selectedContainer != null) {
					// IRegion selectionRegion =
					// templateSourceEditor.getSelectionRegionForElementAtPoint(element,
					// controlDragPoint, false);
					U selectedItem = getSelectedItemAtPoint(selectedContainer, controlDragPoint);
					// If there's no current selection, clear a previous
					// selection
					if (selectedItem == null) {
						removeHoverAnnotation();

						// If there is a current selection and it differs from
						// the previous
						// selection, calculate new offsets and throb it ...
					} else if (isSelectedItemChanged(previousSelectedItem, selectedItem)) {
						removeHoverAnnotation();

						// If we're scrolling, don't do tag highlighting, or
						// we'll mess
						// up the metrics
						if (_autoscroller.isScrollStarted()) {
							// IGNORE
						} else {
							// Add the annotation
							try {
								_selectedItem = selectedItem;
								Rectangle selectionRect = getSelectionRectangle(selectedItem);
								_popper.setAnimationRect(selectionRect);
								addHoverAnnotation(selectedItem);
							} catch (Throwable t) {
								_popper.setAnimationRect(null);
							}
						}

						// Otherwise we're on the same selection, so repaint it
						// so it throbs
					} else {
						_popper.step();
					}

					// It's not a WO tag, so just clear any current selection
					// ...
				} else if (_bindingsAnnotation != null) {
					removeHoverAnnotation();
				}
			} else if (_bindingsAnnotation != null) {
				removeHoverAnnotation();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bindingDragCanceled(WOBrowserColumn column) {
		bindingDragFinished(column, null, false, null);
	}

	public boolean bindingDropped(WOBrowserColumn column, Point dropPoint, BindingsDragHandler dragHandler) {
		return bindingDragFinished(column, dropPoint, true, dragHandler);
	}

	public boolean bindingDragFinished(WOBrowserColumn column, Point dropPoint, boolean dropped, BindingsDragHandler dragHandler) {
		boolean dropFinished = true;
		try {
			_autoscroller.stopScroll();

			Point controlDragPoint = _editorControl.toControl(dropPoint);
			Rectangle controlBounds = _editorControl.getBounds();
			controlBounds.x = 0;
			controlBounds.y = 0;
			if (isEditorActive() && dropped && controlBounds.contains(controlDragPoint)) {
				dropFinished = dropFromColumnAtPoint(column, dropPoint, dragHandler);
			} else {
				removeHoverAnnotation();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dropFinished;
	}

	public void browserColumnAdded(WOBrowserColumn column) {
		// System.out.println("WOBrowserPageBookView.browserColumnAdded: " +
		// column);
	}

	public void browserColumnRemoved(WOBrowserColumn column) {
		// System.out.println("BindingsInspectorPage.browserColumnRemoved: " +
		// column);
	}

	protected abstract IAutoscroller createAutoscroller(W editorControl);

	protected abstract V _addHoverAnnotation(U selectedItem);

	protected abstract void _removeHoverAnnotation(V annotation);

	protected abstract boolean isSelectedItemChanged(U oldItem, U newItem);

	protected abstract U getSelectedItemAtPoint(T container, Point point);

	protected abstract T getSelectedContainerAtPoint(Point point, boolean forDrop) throws Exception;

	protected abstract Rectangle getSelectionRectangle(U item);

	protected abstract boolean dropFromColumnAtPoint(WOBrowserColumn column, Point dropPoint, BindingsDragHandler dragHandler) throws Exception;
}
