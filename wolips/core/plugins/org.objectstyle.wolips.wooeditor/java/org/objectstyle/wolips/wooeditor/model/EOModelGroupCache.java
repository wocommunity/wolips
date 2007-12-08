package org.objectstyle.wolips.wooeditor.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.objectstyle.wolips.bindings.woo.IEOModelGroupCache;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;

public class EOModelGroupCache implements IEOModelGroupCache {
	private Map<IJavaProject, EOModelGroup> myModelGroupCache;
	
	public EOModelGroupCache() {
		myModelGroupCache = new HashMap<IJavaProject, EOModelGroup>();
	}
	
	public EOModelGroup getModelGroup(IJavaProject project) {
		return myModelGroupCache.get(project);
	}
	
	public void setModelGroup(IJavaProject project, EOModelGroup modelGroup) {
		myModelGroupCache.put(project, modelGroup);
	}

	public void setModelGroup(IJavaProject key, Object value) {
		setModelGroup(key, (EOModelGroup)value);
	}

	public void clearCache() {
		myModelGroupCache.clear();
	}
}
