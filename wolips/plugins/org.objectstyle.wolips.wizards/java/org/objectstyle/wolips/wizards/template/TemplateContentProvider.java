package org.objectstyle.wolips.wizards.template;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TemplateContentProvider implements IStructuredContentProvider {
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		List<ProjectTemplate> templates = (List<ProjectTemplate>) inputElement;
		Object[] templateFilesArray = templates.toArray();
		return templateFilesArray;
	}

	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// DO NOTHING
	}
}