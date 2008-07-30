package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.objectstyle.wolips.baseforplugins.util.ResourceUtilities;

public class WOComponentAPIFileFilter extends ViewerFilter {
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (parentElement instanceof IFolder && element instanceof IFile) {
			IFile file = (IFile)element;
			if ("api".equalsIgnoreCase(file.getFileExtension())) {
				 String nameWithoutExtension = ResourceUtilities.getFileNameWithoutExtension(file);
				 IFolder parentFolder = (IFolder)parentElement;
				 return !parentFolder.exists(new Path(nameWithoutExtension + ".wo"));
			}
		}
		return true;
	}

}
