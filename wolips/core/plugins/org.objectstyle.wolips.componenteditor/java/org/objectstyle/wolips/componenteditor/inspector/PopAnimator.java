package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class PopAnimator implements PaintListener {
	private static final int minBorderWidth = 2;

	private static final int maxBorderWidth = 6;

	private static final int animationDuration = 150;

	private static final int borderRadius = 10;

	private static final int borderRedrawSize = maxBorderWidth * 2;

	private boolean _isAnimating;

	private long _startTime;

	private Rectangle _animationRect;

	private Color _backgroundColor;

	private Control _control;

	public PopAnimator() {
		RGB bindingHoverColorPreference = new RGB(240, 220, 0);
		_backgroundColor = new Color(Display.getCurrent(), bindingHoverColorPreference);
	}

	public void setControl(Control control) {
		if (_control != null) {
			_control.removePaintListener(this);
		}
		_control = control;
		if (_control != null) {
			_control.addPaintListener(this);
		}
	}

	public void setAnimationRect(Rectangle animationRect) {
		repaint();
		_animationRect = animationRect;
	}

	public void step() {
		if (isAnimating()) {
			repaint();
		}
	}

	public void repaint() {
		repaint(_animationRect);
	}

	public void repaint(Rectangle rect) {
		if (_control != null && rect != null) {
			_control.redraw(rect.x - borderRedrawSize, rect.y - borderRedrawSize, rect.width + 2 * borderRedrawSize, rect.height + 2 * borderRedrawSize, true);
		}
	}

	public boolean isAnimating() {
		return _isAnimating;
	}

	public void dispose() {
		setControl(null);
		if (_backgroundColor != null) {
			_backgroundColor.dispose();
		}
		_backgroundColor = null;
	}

	public synchronized void stopAnimation() {
		_isAnimating = false;
		Rectangle animationRect = _animationRect;
		_animationRect = null;
		if (animationRect != _animationRect) {
			repaint(animationRect);
		}
	}

	public synchronized void startAnimation() {
		_startTime = System.currentTimeMillis();
		_isAnimating = true;
		repaint();
	}

	public void paintControl(PaintEvent e) {
		try {
			boolean shouldThrob;
			double animationTime;
			Rectangle selectionRect;
			synchronized (this) {
				// Determine whether or not the animation should still be
				// running
				animationTime = System.currentTimeMillis() - _startTime;
				if (_isAnimating && animationTime > 2 * animationDuration) {
					_isAnimating = false;
				}
				if (_animationRect != null) {
					selectionRect = new Rectangle(_animationRect.x, _animationRect.y, _animationRect.width, _animationRect.height);
				} else {
					selectionRect = null;
				}
				shouldThrob = _isAnimating;
			}

			if (_animationRect != null) {
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
					// the text is rendered underneath us, so we can only render
					// the bottom of the shadow
					if (true || lineWidth > minBorderWidth) {
						int shadowHeight = 2 * margin;
						int shadowMargin = borderRadius / 2 + 2 * margin;
						e.gc.setAlpha(80);
						e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
						e.gc.drawRoundRectangle(selectionRect.x + shadowMargin, selectionRect.y + selectionRect.height, selectionRect.width - 2 * shadowMargin, shadowHeight, borderRadius, borderRadius);
					}

					e.gc.setForeground(_backgroundColor);
					e.gc.setBackground(_backgroundColor);

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

}
