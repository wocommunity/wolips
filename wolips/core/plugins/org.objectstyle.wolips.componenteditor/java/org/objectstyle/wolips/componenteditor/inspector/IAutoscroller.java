package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.graphics.Point;

public interface IAutoscroller {
	public void setDelegate(IAutoscroller.Delegate delegate);

	public boolean isScrollStarted();

	public void stopScroll();

	public void autoscroll(Point scrollPoint);

	public static interface Delegate {
		public void autoscrollOccurred(IAutoscroller scroller);
	}
}