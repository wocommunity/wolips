package org.objectstyle.wolips.baseforuiplugins.plist;

public interface IPropertyListChangeListener {
	public void pathRenamed(String oldPath, String newPath);

	public void pathAdded(String path, Object value);

	public void pathRemoved(String path, Object value);

	public void pathChanged(String path, Object oldValue, Object newValue);
}
