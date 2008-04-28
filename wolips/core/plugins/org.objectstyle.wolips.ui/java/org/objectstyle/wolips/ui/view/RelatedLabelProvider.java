package org.objectstyle.wolips.ui.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

public class RelatedLabelProvider extends AppearanceAwareLabelProvider implements ITableLabelProvider {
	private Set<IResource> duplicateResourceSet;

	public RelatedLabelProvider() {
		super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED, AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | JavaElementImageProvider.SMALL_ICONS);
		addLabelDecorator(PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

	public void setResultList(Object[] items) {
		int length = items.length;
		duplicateResourceSet = new HashSet<IResource>(length);
		Map<String, IResource> filenameToItemMap = new HashMap<String, IResource>(length);
		int i = length;
		while (i-- > 0) {
			if (!(items[i] instanceof IResource)) {
				continue;
			}
			IResource thisResource = (IResource) items[i];
			IResource otherResource = filenameToItemMap.get(thisResource.getName());
			if (otherResource != null) {
				duplicateResourceSet.add(thisResource);
				duplicateResourceSet.add(otherResource);
			}
			filenameToItemMap.put(thisResource.getName(), thisResource);
		}
	}

	public String getColumnText(Object _element, int _columnIndex) {
		String text = null;
		if (_element instanceof IResource) {
			IResource resource = (IResource) _element;
			String ext = resource.getFileExtension();
			String name = resource.getName();
			if (_columnIndex == 0) {
				if (ext != null) {
					if ("java".equalsIgnoreCase(ext)) {
						text = "Java";
					} else
					if ("groovy".equalsIgnoreCase(ext)) {
						text = "Groovy";
					} else
					if (!ext.matches("^wod|wo|woo|html|api$")) {
						text = ext.toUpperCase();
						if (resource.getParent() != null && resource.getParent().getFileExtension() != null && resource.getParent().getFileExtension().equals("lproj")) {
							text = resource.getParent().getName().replaceAll("\\.lproj", "");
						}
						else if (text.equals("EOMODELD")) {
							text = "EOM";
						}
					} else {
						text = ext.toUpperCase();
					}					
				}
			} else {
				text = name;
				if (duplicateResourceSet.contains(resource)) {
					text += " (" + resource.getProject().getName() + ")";
				}
			}
		}
		if (text == null) {
			text = getText(_element);
		}
		return text;
	}

	public Image getColumnImage(Object _element, int _columnIndex) {
		Image image = null;
		if (_columnIndex == 0) {
			image = getImage(_element);
		}
		return image;
	}
}