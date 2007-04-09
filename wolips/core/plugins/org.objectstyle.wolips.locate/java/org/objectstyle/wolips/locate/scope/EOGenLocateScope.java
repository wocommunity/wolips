package org.objectstyle.wolips.locate.scope;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class EOGenLocateScope implements ILocateScope {
	private IProject myProject;

	public EOGenLocateScope(IProject _project) {
		myProject = _project;
	}

	public boolean addToResult(IFile _file) {
		return ("eogen".equalsIgnoreCase(_file.getFileExtension()));
	}

	public boolean addToResult(IContainer _container) {
		return false;
	}

	public boolean ignoreContainer(IContainer _container) {
		return (_container.getType() == IResource.PROJECT && !_container.equals(myProject));
	}
}
