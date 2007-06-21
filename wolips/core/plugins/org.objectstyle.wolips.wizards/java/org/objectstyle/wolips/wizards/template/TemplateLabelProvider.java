package org.objectstyle.wolips.wizards.template;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class TemplateLabelProvider implements ILabelProvider {
	public void addListener(ILabelProviderListener listener) {
		// DO NOTHING
	}

	public void dispose() {
		// DO NOTHING
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// DO NOTHING
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		ProjectTemplate template = (ProjectTemplate) element;
		return template.getName();
	}
}