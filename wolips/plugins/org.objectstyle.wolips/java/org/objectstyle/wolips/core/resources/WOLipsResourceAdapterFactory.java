package org.objectstyle.wolips.core.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

public class WOLipsResourceAdapterFactory implements IAdapterFactory {
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object processedAdaptableObject = adaptableObject;
		if (adaptableObject instanceof ICompilationUnit) {
			try {
				processedAdaptableObject = ((ICompilationUnit) adaptableObject).getUnderlyingResource();
			} catch (JavaModelException e) {
				processedAdaptableObject = null;
			}
		}

		IWOLipsResource wolipsResource;
		if (processedAdaptableObject instanceof IResource) {
			IResource resource = (IResource) processedAdaptableObject;
			String extension = resource.getFileExtension();
			if (ComponentWOLipsResource.isComponentExtension(extension)) {
				wolipsResource = new ComponentWOLipsResource(resource);
			} else if ("eomodeld".equalsIgnoreCase(extension) || "eogen".equalsIgnoreCase(extension)) {
				wolipsResource = new EOModelWOLipsResource(resource);
			} else {
				wolipsResource = new GenericWOLipsResource(resource);
			}
		} else {
			wolipsResource = null;
		}
		return wolipsResource;
	}
	
	public Class[] getAdapterList() {
		return new Class[] { IWOLipsResource.class };
	}

}
