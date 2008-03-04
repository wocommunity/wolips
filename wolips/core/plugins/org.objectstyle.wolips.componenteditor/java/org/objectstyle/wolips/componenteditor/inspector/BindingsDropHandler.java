package org.objectstyle.wolips.componenteditor.inspector;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.templateeditor.TemplateSourceEditor;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class BindingsDropHandler implements IWOBrowserDelegate, PaintListener {
	private static final int minBorderWidth = 2;

	private static final int maxBorderWidth = 6;

	private static final int animationDuration = 150;

	private static final int borderRadius = 10;

	private static final int borderRedrawSize = maxBorderWidth * 2;

	private static final int scrollTopLeftMargin = 20;

	private static final int scrollBottomRightMargin = 20;

	private static final int initialScrollFrequency = 500;

	private static final int continuousScrollFrequency = 50;

	private ComponentEditor _componentEditor;

	private IRegion _selectionRegion;

	private Rectangle _selectionRect;

	private StyleRange[] _previousStyleRanges;

	private long _selectionTime;

	private Color _selectionBackgroundColor;

	private boolean _shouldThrob;

	private boolean _scrollStarted;

	private long _lastScrollTime;

	public BindingsDropHandler(ComponentEditor componentEditor) {
		_componentEditor = componentEditor;
		_selectionBackgroundColor = new Color(Display.getCurrent(), 240, 220, 0);
		_componentEditor.getTemplateEditor().getSourceEditor().getViewer().getTextWidget().addPaintListener(this);
		_scrollStarted = false;
		_lastScrollTime = -1;
	}

	public void paintControl(PaintEvent e) {
		try {
			// Determine whether or not the animation should still be running
			double animationTime = System.currentTimeMillis() - _selectionTime;
			if (_shouldThrob && animationTime > 2 * animationDuration) {
				_shouldThrob = false;
			}

			IRegion selectionRegion = _selectionRegion;
			if (selectionRegion != null) {
				// Set the line width based on the animation curve
				int lineWidth = minBorderWidth;
				if (_shouldThrob) {
					lineWidth += (int) ((maxBorderWidth - minBorderWidth) * Math.sin(0.5 * Math.PI * animationTime / animationDuration));
				}

				int margin = lineWidth / 2;
				if (lineWidth > 0) {
					e.gc.setLineWidth(lineWidth);

					// Set the rectangle size according to the current animation
					// position
					Rectangle selectionRect = new Rectangle(_selectionRect.x, _selectionRect.y, _selectionRect.width, _selectionRect.height);
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

	public IRegion getPreviousRegion() {
		return _selectionRegion;
	}

	public ComponentEditor getComponentEditor() {
		return _componentEditor;
	}

	public void dispose() {
		_selectionBackgroundColor.dispose();
		_componentEditor.getTemplateEditor().getSourceEditor().getViewer().getTextWidget().removePaintListener(this);
	}

	protected void clearHighlightedTextStyle() {
		_shouldThrob = false;
		if (_previousStyleRanges != null) {
			Rectangle selectionRect = _selectionRect;
			TemplateEditor templateEditor = _componentEditor.getTemplateEditor();
			TemplateSourceEditor templateSourceEditor = templateEditor.getSourceEditor();
			StyledText st = templateSourceEditor.getViewer().getTextWidget();
			st.replaceStyleRanges(_selectionRegion.getOffset(), _selectionRegion.getLength(), _previousStyleRanges);
			_selectionRect = null;
			_selectionRegion = null;
			_previousStyleRanges = null;
			repaintSelectionRect(st, selectionRect);
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
			StyledText st = templateSourceEditor.getViewer().getTextWidget();
			Point controlDragPoint = st.toControl(dragPoint);

			Rectangle controlBounds = st.getBounds();
			controlBounds.x = 0;
			controlBounds.y = 0;
			// Don't bother doing anything if we're outside of the Template
			// Editor's bounds
			if (controlBounds.contains(controlDragPoint)) {
				autoscroll(st, controlDragPoint, controlBounds);

				FuzzyXMLElement element = templateSourceEditor.getElementAtPoint(controlDragPoint);
				// We only want to throb WO tags ...
				if (WodHtmlUtils.isWOTag(element)) {
					IRegion selectionRegion = templateSourceEditor.getSelectionRegionAtPoint(controlDragPoint, false);
					// If there's no current selection, clear a previous
					// selection
					if (selectionRegion == null) {
						clearHighlightedTextStyle();

						// If there is a current selection and it differs from
						// the previous
						// selection, calculate new offsets and throb it ...
					} else if (previousRegion == null || previousRegion.getOffset() != selectionRegion.getOffset()) {
						clearHighlightedTextStyle();

						// If we're scrolling, don't do tag highlighting, or
						// we'll mess
						// up the metrics
						if (_scrollStarted) {
							_selectionRegion = null;
							_selectionRect = null;
							_previousStyleRanges = null;
						} else {
							// We need to save the previous text styles because
							// we're going
							// to replace them with our colored highlight
							_selectionRegion = selectionRegion;
							_selectionRect = st.getTextBounds(selectionRegion.getOffset(), selectionRegion.getOffset() + selectionRegion.getLength() - 1);
							_previousStyleRanges = st.getStyleRanges(selectionRegion.getOffset(), selectionRegion.getLength());

							StyleRange[] styleRanges = new StyleRange[_previousStyleRanges.length];
							for (int i = 0; i < _previousStyleRanges.length; i++) {
								styleRanges[i] = (StyleRange) _previousStyleRanges[i].clone();
								styleRanges[i].background = _selectionBackgroundColor;
								styleRanges[i].fontStyle = SWT.BOLD;
							}

							// And now apply our custom styling
							StyleRange styleRange = new StyleRange(selectionRegion.getOffset(), selectionRegion.getLength(), null, _selectionBackgroundColor);
							st.setStyleRange(styleRange);

							// And initiate a throb ...
							_shouldThrob = true;
							_selectionTime = System.currentTimeMillis();
							repaintSelectionRect(st, _selectionRect);
						}

						// Otherwise we're on the same selection, so repaint it
						// so it throbs
					} else if (_shouldThrob) {
						repaintSelectionRect(st, _selectionRect);
					}

					// It's not a WO tag, so just clear any current selection
					// ...
				} else {
					clearHighlightedTextStyle();
				}
			} else {
				_lastScrollTime = -1;
				_scrollStarted = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void autoscroll(StyledText st, Point controlDragPoint, Rectangle controlBounds) {
		if (_lastScrollTime <= 0) {
			_lastScrollTime = System.currentTimeMillis();
		}

		int scrollFrequency = (_scrollStarted) ? continuousScrollFrequency : initialScrollFrequency;

		long scrollTime = System.currentTimeMillis();
		if ((scrollTime - _lastScrollTime) > scrollFrequency) {
			int oldTopIndex = st.getTopIndex();
			int oldHorizontalIndex = st.getHorizontalIndex();

			if (controlDragPoint.y < scrollTopLeftMargin) {
				st.setTopIndex(oldTopIndex - 1);
			} else if ((controlBounds.height - controlDragPoint.y) < scrollBottomRightMargin) {
				st.setTopIndex(oldTopIndex + 1);
			}
			
			if (controlDragPoint.x < scrollTopLeftMargin) {
				st.setHorizontalIndex(oldHorizontalIndex - 1);
			} else if ((controlBounds.width - controlDragPoint.x) < scrollBottomRightMargin) {
				st.setHorizontalIndex(oldHorizontalIndex + 1);
			}

			if (st.getTopIndex() != oldTopIndex || st.getHorizontalIndex() != oldHorizontalIndex) {
				clearHighlightedTextStyle();
				st.redraw();
				_lastScrollTime = scrollTime;
				_scrollStarted = true;
			} else {
				_scrollStarted = false;
			}
		}
	}

	public void bindingDragged(WOBrowserColumn column, Point dropPoint) {
		try {
			_lastScrollTime = -1;
			_scrollStarted = false;

			clearHighlightedTextStyle();

			TemplateEditor templateEditor = _componentEditor.getTemplateEditor();
			TemplateSourceEditor templateSourceEditor = templateEditor.getSourceEditor();
			FuzzyXMLElement selectedElement = templateSourceEditor.getElementAtPoint(dropPoint);

			// System.out.println("BindingsInspectorPage.bindingDragged: " +
			// selectedElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void browserColumnAdded(WOBrowserColumn column) {
		System.out.println("WOBrowserPageBookView.browserColumnAdded: " + column);
	}

	public void browserColumnRemoved(WOBrowserColumn column) {
		System.out.println("BindingsInspectorPage.browserColumnRemoved: " + column);
	}
}
