package org.objectstyle.wolips.jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.objectstyle.wolips.baseforplugins.util.WOLipsNatureUtils;

public class ProjectFrameworkAdapterFactory implements IAdapterFactory {
	private Class[] _adapterList = new Class[] { ProjectFrameworkAdapter.class };

	public Class[] getAdapterList() {
		return _adapterList;
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		IProject project = (IProject) adaptableObject;
		if (WOLipsNatureUtils.isWOLipsNature(project) && project.isAccessible() && adapterType == ProjectFrameworkAdapter.class) {
			return new ProjectFrameworkAdapter(project);
		}
		return null;
	}
}