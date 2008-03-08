package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class Autoscroller {
	private static final int scrollTopLeftMargin = 20;

	private static final int scrollBottomRightMargin = 20;

	private static final int initialScrollFrequency = 500;

	private static final int continuousScrollFrequency = 50;

	private boolean _scrollStarted;

	private long _lastScrollTime;

	private StyledText _st;

	private Autoscroller.Delegate _delegate;

	public Autoscroller(StyledText st) {
		_st = st;
		_scrollStarted = false;
		_lastScrollTime = -1;
	}

	public void setDelegate(Autoscroller.Delegate delegate) {
		_delegate = delegate;
	}

	public boolean isScrollStarted() {
		return _scrollStarted;
	}

	public void stopScroll() {
		_lastScrollTime = -1;
		_scrollStarted = false;
	}

	public void autoscroll(Point scrollPoint) {
		Rectangle controlBounds = _st.getBounds();
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
				int oldTopIndex = _st.getTopIndex();
				int oldHorizontalIndex = _st.getHorizontalIndex();

				if (scrollPoint.y < scrollTopLeftMargin) {
					_st.setTopIndex(oldTopIndex - 1);
				} else if ((controlBounds.height - scrollPoint.y) < scrollBottomRightMargin) {
					_st.setTopIndex(oldTopIndex + 1);
				}

				if (scrollPoint.x < scrollTopLeftMargin) {
					_st.setHorizontalIndex(oldHorizontalIndex - 1);
				} else if ((controlBounds.width - scrollPoint.x) < scrollBottomRightMargin) {
					_st.setHorizontalIndex(oldHorizontalIndex + 1);
				}

				if (_st.getTopIndex() != oldTopIndex || _st.getHorizontalIndex() != oldHorizontalIndex) {
					if (_delegate != null) {
						_delegate.autoscrollOccurred(this);
					}
					_st.redraw();
					_lastScrollTime = scrollTime;
					_scrollStarted = true;
				} else {
					_scrollStarted = false;
				}
			}
		}
	}

	public static interface Delegate {
		public void autoscrollOccurred(Autoscroller scroller);
	}
}
