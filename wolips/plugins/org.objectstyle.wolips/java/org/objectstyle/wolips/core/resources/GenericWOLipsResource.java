package org.objectstyle.wolips.core.resources;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.objectstyle.wolips.baseforuiplugins.utils.WorkbenchUtilities;

public class GenericWOLipsResource implements IWOLipsResource {
	private IResource _resource;

	public GenericWOLipsResource(IResource resource) {
		_resource = resource;
	}

	public IResource getUnderlyingResource() {
		return _resource;
	}

	public List<IResource> getRelatedResources() {
		return new LinkedList<IResource>();
	}

	@Override
	public int hashCode() {
		return _resource == null ? 0 : _resource.hashCode();
	}

	public boolean equals(Object obj) {
		return _resource != null && obj instanceof IWOLipsResource && _resource.equals(((IWOLipsResource) obj).getUnderlyingResource());
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ": " + _resource + "]";
	}

	public void open() {
		if (_resource instanceof IFile) {
			WorkbenchUtilities.open((IFile) _resource, null);
		}
	}
}
