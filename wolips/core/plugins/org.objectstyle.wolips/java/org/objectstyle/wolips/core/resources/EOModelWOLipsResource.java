package org.objectstyle.wolips.core.resources;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.baseforuiplugins.utils.WorkbenchUtilities;
import org.objectstyle.wolips.locate.LocatePlugin;

public class EOModelWOLipsResource implements IWOLipsResource {
	private IResource _resource;

	public EOModelWOLipsResource(IResource resource) {
		_resource = resource;
	}

	public IResource getUnderlyingResource() {
		return _resource;
	}

	public List<IResource> getRelatedResources() {
		List<IResource> relatedResources = new LinkedList<IResource>();
		relatedResources.add(_resource);
		String extension = _resource.getFileExtension();
		String fileWithoutExtension = LocatePlugin.getDefault().fileNameWithoutExtension(_resource);
		IResource relatedResource;
		if ("eogen".equals(extension)) {
			relatedResource = _resource.getParent().getFolder(new Path(fileWithoutExtension + ".eomodeld"));
		}
		else {
			relatedResource = _resource.getParent().getFile(new Path(fileWithoutExtension + ".eogen"));
		}
		if (relatedResource.exists()) {
			relatedResources.add(relatedResource);
		}
		return relatedResources;
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
		IFile index = (IFile) ((IFolder) _resource).findMember("index.eomodeld");
		WorkbenchUtilities.open(index);
	}
}
