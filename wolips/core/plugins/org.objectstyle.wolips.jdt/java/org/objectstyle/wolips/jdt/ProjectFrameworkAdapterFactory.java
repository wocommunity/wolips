package org.objectstyle.wolips.jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;

public class ProjectFrameworkAdapterFactory implements IAdapterFactory {
	private Class[] _adapterList = new Class[] { ProjectFrameworkAdapter.class };

	public Class[] getAdapterList() {
		return _adapterList;
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		IProject project = (IProject) adaptableObject;
		if (adapterType == ProjectFrameworkAdapter.class) {
			return new ProjectFrameworkAdapter(project);
		}
		return null;
	}
}