package org.objectstyle.wolips.core.resources;

import java.util.List;

import org.eclipse.core.resources.IResource;

public interface IWOLipsResource {
	public IResource getUnderlyingResource();

	public List<IResource> getRelatedResources();

	public void open();
}
