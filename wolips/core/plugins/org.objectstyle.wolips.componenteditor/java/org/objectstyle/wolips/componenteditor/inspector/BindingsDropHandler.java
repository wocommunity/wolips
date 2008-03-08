package org.objectstyle.wolips.componenteditor.inspector;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.templateeditor.TemplateSourceEditor;
import org.objectstyle.wolips.wodclipse.core.util.FuzzyXMLWodElement;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class BindingsDropHandler implements IWOBrowserDelegate, PaintListener, Autoscroller.Delegate, MenuListener {
	private static final int minBorderWidth = 2;

	private static final int maxBorderWidth = 6;

	private static final int animationDuration = 150;

	private static final int borderRadius = 10;

	private static final int borderRedrawSize = maxBorderWidth * 2;

	private ComponentEditor _componentEditor;

	private IRegion _selectionRegion;

	private Rectangle _selectionRect;

	private long _selectionTime;

	private Color _selectionBackgroundColor;

	private boolean _shouldThrob;

	private BindingsPopUpMenu _bindingsMenu;

	private Annotation _bindingsAnnotation;

	private Autoscroller _autoscroller;

	public BindingsDropHandler(ComponentEditor componentEditor) {
		_componentEditor = componentEditor;
		TemplateSourceEditor templateSourceEditor = _componentEditor.getTemplateEditor().getSourceEditor();
		AnnotationPreference bindingHoverPreference = templateSourceEditor.getAnnotationPreferenceLookup().getAnnotationPreference(TemplateEditor.BINDING_HOVER_ANNOTATION);
		RGB bindingHoverColorPreference = null;
		if (bindingHoverPreference != null) {
			bindingHoverColorPreference = bindingHoverPreference.getColorPreferenceValue();
		}
		if (bindingHoverColorPreference == null) {
			bindingHoverColorPreference = new RGB(240, 220, 0);
		}
		_selectionBackgroundColor = new Color(Display.getCurrent(), bindingHoverColorPreference);

		StyledText st = templateSourceEditor.getViewer().getTextWidget();
		st.addPaintListener(this);
		_autoscroller = new Autoscroller(st);
		_autoscroller.setDelegate(this);

		Shell shell = new Shell(_componentEditor.getSite().getShell());
		try {
			_bindingsMenu = new BindingsPopUpMenu(shell, _componentEditor.getParserCache());
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

	public void paintControl(PaintEvent e) {
		try {
			boolean shouldThrob;
			IRegion selectionRegion;
			double animationTime;
			Rectangle selectionRect;
			synchronized (this) {
				// Determine whether or not the animation should still be
				// running
				animationTime = System.currentTimeMillis() - _selectionTime;
				if (_shouldThrob && animationTime > 2 * animationDuration) {
					_shouldThrob = false;
				}
				selectionRegion = _selectionRegion;
				if (_selectionRect != null) {
					selectionRect = new Rectangle(_selectionRect.x, _selectionRect.y, _selectionRect.width, _selectionRect.height);
				} else {
					selectionRect = null;
				}
				shouldThrob = _shouldThrob;
			}

			if (selectionRegion != null) {
				// Set the line width based on the animation curve
				int lineWidth = minBorderWidth;
				if (shouldThrob) {
					lineWidth += (int) ((maxBorderWidth - minBorderWidth) * Math.sin(0.5 * Math.PI * animationTime / animationDuration));
				}

				int margin = lineWidth / 2;
				if (lineWidth > 0) {
					e.gc.setLineWidth(lineWidth);

					// Set the rectangle size according to the current animation
					// position
					selectionRect.x -= margin;
					selectionRect.y -= margin;
					selectionRect.width += 2 * margin;
					selectionRect.height += 2 * margin;

					// Make selections on the edge look a little nicer
					if (selectionRect.x < minBorderWidth / 2) {
						selectionRect.x += minBorderWidth;
						selectionRect.width -= minBorderWidth;
					}
					if (selectionRect.y < minBorderWidth / 2) {
						selectionRect.y += minBorderWidth;
						selectionRect.height -= minBorderWidth;
					}

					// Draw the shadow -- we have to cheat some here, because
					// the
					// text is rendered underneath us, so we can only render the
					// bottom
					// of the shadow
					if (true || lineWidth > minBorderWidth) {
						int shadowHeight = 2 * margin;
						int shadowMargin = borderRadius / 2 + 2 * margin;
						e.gc.setAlpha(80);
						e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
						e.gc.drawRoundRectangle(selectionRect.x + shadowMargin, selectionRect.y + selectionRect.height, selectionRect.width - 2 * shadowMargin, shadowHeight, borderRadius, borderRadius);
					}

					e.gc.setForeground(_selectionBackgroundColor);
					e.gc.setBackground(_selectionBackgroundColor);

					// Fill the rectangle with a light color for multiline
					// selections
					e.gc.setAlpha(50);
					e.gc.fillRoundRectangle(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height, borderRadius, borderRadius);

					// And now draw the border of the rectangle in full opaque
					e.gc.setAlpha(255);
					e.gc.drawRoundRectangle(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height, borderRadius, borderRadius);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public boolean shouldThrob() {
		return _shouldThrob;
	}

	public ComponentEditor getComponentEditor() {
		return _componentEditor;
	}

	public void dispose() {
		if (_bindingsMenu != null) {
			_bindingsMenu.dispose();
		}
		_selectionBackgroundColor.dispose();
		_componentEditor.getTemplateEditor().getSourceEditor().getViewer().getTextWidget().removePaintListener(this);
	}

	public synchronized Annotation getBindingsAnnotation() {
		return _bindingsAnnotation;
	}

	protected synchronized void addHoverAnnotation() {
		TemplateEditor templateEditor = _componentEditor.getTemplateEditor();
		TemplateSourceEditor templateSourceEditor = templateEditor.getSourceEditor();
		ISourceViewer templateViewer = templateSourceEditor.getViewer();

		_bindingsAnnotation = new Annotation(TemplateEditor.BINDING_HOVER_ANNOTATION, false, null);
		templateViewer.getAnnotationModel().addAnnotation(_bindingsAnnotation, new Position(_selectionRegion.getOffset(), _selectionRegion.getLength()));

		// And initiate a throb ...
		_shouldThrob = true;
		_selectionTime = System.currentTimeMillis();
		repaintSelectionRect(templateViewer.getTextWidget(), _selectionRect);
	}

	protected synchronized void removeHoverAnnotation() {
		_shouldThrob = false;
		if (_bindingsAnnotation != null) {
			Rectangle selectionRect = _selectionRect;
			TemplateEditor templateEditor = _componentEditor.getTemplateEditor();
			TemplateSourceEditor templateSourceEditor = templateEditor.getSourceEditor();
			ISourceViewer templateViewer = templateSourceEditor.getViewer();
			templateViewer.getAnnotationModel().removeAnnotation(_bindingsAnnotation);
			_bindingsAnnotation = null;
			_selectionRect = null;
			_selectionRegion = null;
			repaintSelectionRect(templateViewer.getTextWidget(), selectionRect);
		}
	}

	protected void repaintSelectionRect(StyledText st, Rectangle selectionRect) {
		if (st != null && selectionRect != null) {
			st.redraw(selectionRect.x - borderRedrawSize, selectionRect.y - borderRedrawSize, selectionRect.width + 2 * borderRedrawSize, selectionRect.height + 2 * borderRedrawSize, true);
		}
	}

	public void bindingDragging(WOBrowserColumn column, Point dragPoint) {
		try {
			IRegion previousRegion = _selectionRegion;

			TemplateEditor templateEditor = _componentEditor.getTemplateEditor();
			TemplateSourceEditor templateSourceEditor = templateEditor.getSourceEditor();
			ISourceViewer templateViewer = templateSourceEditor.getViewer();
			StyledText st = templateViewer.getTextWidget();
			Point controlDragPoint = st.toControl(dragPoint);

			_autoscroller.autoscroll(controlDragPoint);

			Rectangle controlBounds = st.getBounds();
			controlBounds.x = 0;
			controlBounds.y = 0;
			// Don't bother doing anything if we're outside of the Template
			// Editor's bounds
			if (controlBounds.contains(controlDragPoint)) {
				FuzzyXMLElement element = templateSourceEditor.getElementAtPoint(controlDragPoint);
				// We only want to throb WO tags ...
				if (WodHtmlUtils.isWOTag(element)) {
					IRegion selectionRegion = templateSourceEditor.getSelectionRegionAtPoint(controlDragPoint, false);
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
								_selectionRect = st.getTextBounds(selectionRegion.getOffset(), selectionRegion.getOffset() + selectionRegion.getLength() - 1);

								addHoverAnnotation();
							} catch (Throwable t) {
								_selectionRegion = null;
								_selectionRect = null;
							}
						}

						// Otherwise we're on the same selection, so repaint it
						// so it throbs
					} else if (_shouldThrob) {
						repaintSelectionRect(st, _selectionRect);
					}

					// It's not a WO tag, so just clear any current selection
					// ...
				} else {
					removeHoverAnnotation();
				}
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

			if (dropped) {
				TemplateEditor templateEditor = _componentEditor.getTemplateEditor();
				TemplateSourceEditor templateSourceEditor = templateEditor.getSourceEditor();
				StyledText st = templateSourceEditor.getViewer().getTextWidget();
				Point controlDropPoint = st.toControl(dropPoint);
				FuzzyXMLElement selectedElement = templateSourceEditor.getElementAtPoint(controlDropPoint);
				if (selectedElement == null || !WodHtmlUtils.isWOTag(selectedElement)) {
					removeHoverAnnotation();
				} else {
					FuzzyXMLWodElement wodElement = new FuzzyXMLWodElement(selectedElement, Activator.getDefault().isWO54());
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
