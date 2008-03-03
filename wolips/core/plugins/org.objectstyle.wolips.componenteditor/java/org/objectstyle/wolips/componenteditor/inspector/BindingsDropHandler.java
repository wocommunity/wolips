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

	private static int borderRedrawSize = maxBorderWidth * 2;

	private ComponentEditor _componentEditor;

	private IRegion _selectionRegion;

	private Rectangle _selectionRect;

	private StyleRange[] _previousStyleRanges;

	private long _selectionTime;

	private Color _selectionBackgroundColor;

	private boolean _shouldThrob;

	public BindingsDropHandler(ComponentEditor componentEditor) {
		_componentEditor = componentEditor;
		_selectionBackgroundColor = new Color(Display.getCurrent(), 240, 220, 0);
		_componentEditor.getTemplateEditor().getSourceEditor().getViewer().getTextWidget().addPaintListener(this);
	}

	public void paintControl(PaintEvent e) {
		try {
			double animationTime = System.currentTimeMillis() - _selectionTime;
			if (_shouldThrob && animationTime > 2 * animationDuration) {
				_shouldThrob = false;
			}

			IRegion selectionRegion = _selectionRegion;
			if (selectionRegion != null) {
				int lineWidth = minBorderWidth;
				if (_shouldThrob) {
					lineWidth += (int) ((maxBorderWidth - minBorderWidth) * Math.sin(0.5 * Math.PI * animationTime / animationDuration));
				}
				int margin = lineWidth / 2;
				if (lineWidth > 0) {
					e.gc.setLineWidth(lineWidth);
					Rectangle selectionRect = new Rectangle(_selectionRect.x, _selectionRect.y, _selectionRect.width, _selectionRect.height);
					selectionRect.x -= margin;
					selectionRect.y -= margin;
					selectionRect.width += 2 * margin;
					selectionRect.height += 2 * margin;

					int radius = 10;

					if (true || lineWidth > minBorderWidth) {
						int shadowHeight = 2 * margin;
						int shadowMargin = radius / 2 + 2 * margin;
						e.gc.setAlpha(80);
						e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
						e.gc.drawRoundRectangle(selectionRect.x + shadowMargin, selectionRect.y + selectionRect.height, selectionRect.width - 2 * shadowMargin, shadowHeight, radius, radius);
					}

					e.gc.setForeground(_selectionBackgroundColor);
					e.gc.setBackground(_selectionBackgroundColor);

					e.gc.setAlpha(50);
					e.gc.fillRoundRectangle(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height, radius, radius);

					e.gc.setAlpha(255);
					e.gc.drawRoundRectangle(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height, radius, radius);
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

	protected void undoDragStyle() {
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

			FuzzyXMLElement element = templateSourceEditor.getElementAtPoint(dragPoint);
			if (WodHtmlUtils.isWOTag(element)) {
				StyledText st = templateSourceEditor.getViewer().getTextWidget();
				IRegion selectionRegion = templateSourceEditor.getSelectionRegionAtPoint(dragPoint, false);
				if (selectionRegion == null) {
					undoDragStyle();
				} else if (previousRegion == null || previousRegion.getOffset() != selectionRegion.getOffset()) {
					undoDragStyle();

					_selectionRegion = selectionRegion;
					_selectionRect = st.getTextBounds(selectionRegion.getOffset(), selectionRegion.getOffset() + selectionRegion.getLength() - 1);
					_previousStyleRanges = st.getStyleRanges(selectionRegion.getOffset(), selectionRegion.getLength());

					StyleRange[] styleRanges = new StyleRange[_previousStyleRanges.length];
					for (int i = 0; i < _previousStyleRanges.length; i++) {
						styleRanges[i] = (StyleRange) _previousStyleRanges[i].clone();
						styleRanges[i].background = _selectionBackgroundColor;
						styleRanges[i].fontStyle = SWT.BOLD;
					}

					StyleRange styleRange = new StyleRange(selectionRegion.getOffset(), selectionRegion.getLength(), null, _selectionBackgroundColor);
					st.setStyleRange(styleRange);
					repaintSelectionRect(st, _selectionRect);

					_shouldThrob = true;
					_selectionTime = System.currentTimeMillis();
				} else {
					repaintSelectionRect(st, _selectionRect);
				}
			} else {
				undoDragStyle();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bindingDragged(WOBrowserColumn column, Point dropPoint) {
		try {
			undoDragStyle();

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
