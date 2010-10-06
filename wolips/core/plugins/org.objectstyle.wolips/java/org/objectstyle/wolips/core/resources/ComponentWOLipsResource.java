package org.objectstyle.wolips.core.resources;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.objectstyle.wolips.baseforuiplugins.utils.WorkbenchUtilities;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;

public class ComponentWOLipsResource implements IWOLipsResource {
	public static String[] EXTENSIONS = new String[] { "java", "groovy", "wo", "html", "wod", "woo", "api" };

	private IResource _resource;

	public ComponentWOLipsResource(IResource resource) {
		_resource = resource;
	}

	public List<IResource> getRelatedResources() {
		List<IResource> list = new LinkedList<IResource>();
		try {
			String fileName = _resource.getName();
			String extension = _resource.getFileExtension();
			int length = fileName.length() - extension.length() - 1;
			if (length > 0) {
				fileName = fileName.substring(0, length);
				LocalizedComponentsLocateResult results = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(_resource);
				list.addAll(Arrays.asList(results.getResources()));
				if (results.getFirstHtmlFile() != null) {
					list.add(results.getFirstHtmlFile());
				}
				if (results.getFirstWodFile() != null) {
					list.add(results.getFirstWodFile());
				}
				if (results.getFirstWooFile() != null) {
					list.add(results.getFirstWooFile());
				}
			}
		} catch (Exception e) {
			CorePlugin.getDefault().log(e);
		}
		return list;
	}

	public IResource getUnderlyingResource() {
		return _resource;
	}

	public void open() {
		String extension = _resource.getFileExtension();
		if ("wo".equals(extension)) {
			String fileName = _resource.getName();
			fileName = fileName.substring(0, fileName.length() - extension.length() - 1);
			IFile wodFile = (IFile) ((IFolder) _resource).findMember(fileName + ".wod");
			if (wodFile != null) {
				WorkbenchUtilities.open(wodFile, "org.objectstyle.wolips.componenteditor.ComponentEditor");
			}
		} else {
			WorkbenchUtilities.open((IFile) _resource, null);
		}
	}

	@Override
	public int hashCode() {
		return _resource.hashCode();
	}

	public boolean equals(Object obj) {
		return obj instanceof IWOLipsResource && _resource.equals(((IWOLipsResource) obj).getUnderlyingResource());
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ": " + _resource + "]";
	}

	public static boolean isComponentExtension(String extension) {
		if (extension != null) {
			String lowercaseExtension = extension.toLowerCase();
			for (String componentExtension : ComponentWOLipsResource.EXTENSIONS) {
				if (lowercaseExtension.equals(componentExtension)) {
					return true;
				}
			}
		}
		return false;
	}
}
