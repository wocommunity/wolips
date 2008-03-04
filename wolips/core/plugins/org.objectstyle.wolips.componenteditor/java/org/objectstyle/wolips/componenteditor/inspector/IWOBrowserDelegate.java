package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.graphics.Point;

public interface IWOBrowserDelegate {
	public void browserColumnAdded(WOBrowserColumn column);
	
	public void browserColumnRemoved(WOBrowserColumn column);
	
	public void bindingDragging(WOBrowserColumn column, Point dragPoint);
	
	public void bindingDropped(WOBrowserColumn column, Point dropPoint);
}