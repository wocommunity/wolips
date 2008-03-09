package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractAutoscroller<T extends Control> implements IAutoscroller {
	private static final int scrollTopLeftMargin = 20;

	private static final int scrollBottomRightMargin = 20;

	private static final int initialScrollFrequency = 500;

	private static final int continuousScrollFrequency = 50;

	private boolean _scrollStarted;

	private long _lastScrollTime;

	private T _control;

	private IAutoscroller.Delegate _delegate;

	public AbstractAutoscroller(T control) {
		_control = control;
		_scrollStarted = false;
		_lastScrollTime = -1;
	}

	public void setDelegate(IAutoscroller.Delegate delegate) {
		_delegate = delegate;
	}

	public boolean isScrollStarted() {
		return _scrollStarted;
	}

	public void stopScroll() {
		_lastScrollTime = -1;
		_scrollStarted = false;
	}

	public T getControl() {
		return _control;
	}

	public void autoscroll(Point scrollPoint) {
		if (_control == null) {
			return;
		}
		
		Rectangle controlBounds = _control.getBounds();
		controlBounds.x = 0;
		controlBounds.y = 0;
		if (!controlBounds.contains(scrollPoint)) {
			stopScroll();
		} else {
			if (_lastScrollTime <= 0) {
				_lastScrollTime = System.currentTimeMillis();
			}

			int scrollFrequency = (_scrollStarted) ? continuousScrollFrequency : initialScrollFrequency;

			long scrollTime = System.currentTimeMillis();
			if ((scrollTime - _lastScrollTime) > scrollFrequency) {
				int oldVerticalPosition = getVerticalPosition();
				int oldHorizontalPosition = getHorizontalPosition();

				if (scrollPoint.y < scrollTopLeftMargin) {
					scrollUp(scrollPoint.y);
				} else if ((controlBounds.height - scrollPoint.y) < scrollBottomRightMargin) {
					scrollDown(controlBounds.height - scrollPoint.y);
				}

				if (scrollPoint.x < scrollTopLeftMargin) {
					scrollLeft(scrollPoint.x);
				} else if ((controlBounds.width - scrollPoint.x) < scrollBottomRightMargin) {
					scrollRight(controlBounds.width - scrollPoint.x);
				}

				if (getVerticalPosition() != oldVerticalPosition || getHorizontalPosition() != oldHorizontalPosition) {
					if (_delegate != null) {
						_delegate.autoscrollOccurred(this);
					}
					_control.redraw();
					_lastScrollTime = scrollTime;
					_scrollStarted = true;
				} else {
					_scrollStarted = false;
				}
			}
		}
	}

	protected abstract void scrollUp(int speed);

	protected abstract void scrollDown(int speed);

	protected abstract void scrollLeft(int speed);

	protected abstract void scrollRight(int speed);

	protected abstract int getVerticalPosition();

	protected abstract int getHorizontalPosition();
}
