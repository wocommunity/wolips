package org.objectstyle.wolips.bindings.woo;

import org.eclipse.jdt.core.IJavaProject;

public interface IEOModelGroupCache {
	// Dummy interface to avoid need for additional plugin dependancies.
	public Object getModelGroup(IJavaProject key);
	
	public void setModelGroup(IJavaProject key, Object value);
	
	public void clearCache();
}
