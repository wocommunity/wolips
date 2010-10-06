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
import org.objectstyle.wolips.baseforplugins.util.ResourceUtilities;

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
			if (_columnIndex == 0) { // display of resource type (HTML/WOD/WOO/API/WO/...)
				if (ext != null) {
					if ("java".equalsIgnoreCase(ext)) {
						text = "Java";
					} else
					if ("groovy".equalsIgnoreCase(ext)) {
						text = "Groovy";
					} else
					if ("eomodeld".equalsIgnoreCase(ext)) {
						text = "EOM";
					} else
					if (ext.matches("^wod|wo|woo|html|api$")) {
						text = ext.toUpperCase();
					}
					else {
						// display language name if object is in a lproj-folder
						text = ResourceUtilities.getLocalizationName(resource);
						if (text == null) { // otherwise display extension 
							text = ext.toUpperCase();
						}
					}					
				}
			} else { // display of resource name, project and language
				text = name;
				if (duplicateResourceSet.contains(resource)) {
					final StringBuilder sb = new StringBuilder(text);
					sb.append(" (");
					final String languageName = ResourceUtilities.getLocalizationName(resource);
					if (languageName != null) {
						sb.append(languageName).append(", ");
					}
					sb.append(resource.getProject().getName()).append(")");
					text = sb.toString();
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