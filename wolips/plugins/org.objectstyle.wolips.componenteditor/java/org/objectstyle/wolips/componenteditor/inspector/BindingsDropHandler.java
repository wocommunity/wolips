package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.document.ITextWOEditor;

public class BindingsDropHandler implements IWOBrowserDelegate, Autoscroller.Delegate, MenuListener {
	private ITextWOEditor _woEditor;

	private IRegion _selectionRegion;

	private BindingsPopUpMenu _bindingsMenu;

	private Annotation _bindingsAnnotation;

	private Autoscroller _autoscroller;

	private PopAnimator _popper;

	private StyledText _woEditorControl;

	public BindingsDropHandler(ITextWOEditor woEditor) {
		_woEditor = woEditor;
		_woEditorControl = _woEditor.getWOEditorControl();

		_popper = new PopAnimator();
		_popper.setControl(_woEditorControl);

		_autoscroller = new Autoscroller(_woEditorControl);
		_autoscroller.setDelegate(this);

		Shell shell = new Shell(_woEditorControl.getShell());
		try {
			_bindingsMenu = new BindingsPopUpMenu(shell, _woEditor.getParserCache());
			_bindingsMenu.getMenu().addMenuListener(this);
		} catch (Exception e) {
			ComponenteditorPlugin.getDefault().log(e);
		}
	}

	public void menuHidden(MenuEvent event) {
		removeHoverAnnotation();
	}

	public void menuShown(MenuEvent event) {
		// DO NOTHING
	}

	public void autoscrollOccurred(Autoscroller scroller) {
		removeHoverAnnotation();
	}

	public void dispose() {
		if (_bindingsMenu != null) {
			_bindingsMenu.dispose();
		}
		if (_popper != null) {
			_popper.dispose();
		}
		_woEditorControl.removePaintListener(_popper);
	}

	public synchronized Annotation getBindingsAnnotation() {
		return _bindingsAnnotation;
	}

	protected synchronized void addHoverAnnotation(IRegion selectionRegion) {
		_bindingsAnnotation = new Annotation(TemplateEditor.BINDING_HOVER_ANNOTATION, false, null);
		_woEditor.getWOSourceViewer().getAnnotationModel().addAnnotation(_bindingsAnnotation, new Position(selectionRegion.getOffset(), selectionRegion.getLength()));
		_popper.startAnimation();
	}

	protected synchronized void removeHoverAnnotation() {
		_popper.stopAnimation();
		if (_bindingsAnnotation != null) {
			_woEditor.getWOSourceViewer().getAnnotationModel().removeAnnotation(_bindingsAnnotation);
			_bindingsAnnotation = null;
			_selectionRegion = null;
		}
	}

	protected boolean isEditorActive() {
		return _woEditorControl.isVisible();
	}

	public void bindingDragging(WOBrowserColumn column, Point dragPoint) {
		try {
			// If the editor isn't visible, don't allow dragging
			if (!isEditorActive()) {
				removeHoverAnnotation();
				return;
			}

			IRegion previousRegion = _selectionRegion;
			Point controlDragPoint = _woEditorControl.toControl(dragPoint);
			_autoscroller.autoscroll(controlDragPoint);

			Rectangle controlBounds = _woEditorControl.getBounds();
			controlBounds.x = 0;
			controlBounds.y = 0;
			// Don't bother doing anything if we're outside of the Template
			// Editor's bounds
			if (controlBounds.contains(controlDragPoint)) {
				IWodElement wodElement = _woEditor.getWodElementAtPoint(controlDragPoint, false, true);
				// FuzzyXMLElement element =
				// templateSourceEditor.getElementAtPoint(controlDragPoint,
				// true);
				// We only want to throb WO tags ...
				if (wodElement != null) {
					// IRegion selectionRegion =
					// templateSourceEditor.getSelectionRegionForElementAtPoint(element,
					// controlDragPoint, false);
					IRegion selectionRegion = new Region(wodElement.getStartOffset(), wodElement.getFullEndOffset() - wodElement.getStartOffset());
					// If there's no current selection, clear a previous
					// selection
					if (selectionRegion == null) {
						removeHoverAnnotation();

						// If there is a current selection and it differs from
						// the previous
						// selection, calculate new offsets and throb it ...
					} else if (previousRegion == null || previousRegion.getOffset() != selectionRegion.getOffset()) {
						removeHoverAnnotation();

						// If we're scrolling, don't do tag highlighting, or
						// we'll mess
						// up the metrics
						if (_autoscroller.isScrollStarted()) {
							// IGNORE
						} else {
							// Add the annotation
							try {
								_selectionRegion = selectionRegion;
								Rectangle selectionRect = _woEditorControl.getTextBounds(selectionRegion.getOffset(), selectionRegion.getOffset() + selectionRegion.getLength() - 1);
								_popper.setAnimationRect(selectionRect);
								addHoverAnnotation(selectionRegion);
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
			}
			else if (_bindingsAnnotation != null) {
				removeHoverAnnotation();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bindingDragCanceled(WOBrowserColumn column) {
		bindingDragFinished(column, null, false);
	}

	public void bindingDropped(WOBrowserColumn column, Point dropPoint) {
		bindingDragFinished(column, dropPoint, true);
	}

	public void bindingDragFinished(WOBrowserColumn column, Point dropPoint, boolean dropped) {
		try {
			_autoscroller.stopScroll();

			if (isEditorActive() && dropped) {
				Point controlDropPoint = _woEditorControl.toControl(dropPoint);
				IWodElement wodElement = _woEditor.getWodElementAtPoint(controlDropPoint, true, true);
				if (wodElement == null) {
					removeHoverAnnotation();
				} else {
					String droppedKeyPath = column.getSelectedKeyPath();
					if (_bindingsMenu != null) {
						boolean menuShown = _bindingsMenu.showMenuAtLocation(wodElement, droppedKeyPath, dropPoint);
						if (!menuShown) {
							removeHoverAnnotation();
						}
					}
				}
			} else {
				removeHoverAnnotation();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void browserColumnAdded(WOBrowserColumn column) {
		// System.out.println("WOBrowserPageBookView.browserColumnAdded: " +
		// column);
	}

	public void browserColumnRemoved(WOBrowserColumn column) {
		// System.out.println("BindingsInspectorPage.browserColumnRemoved: " +
		// column);
	}
}
