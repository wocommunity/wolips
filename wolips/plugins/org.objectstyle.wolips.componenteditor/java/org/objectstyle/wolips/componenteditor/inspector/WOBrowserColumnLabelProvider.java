package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;

public class WOBrowserColumnLabelProvider extends BaseLabelProvider implements ILabelProvider, ITableLabelProvider {
	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}

	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image image = null;
		if (columnIndex == 1) {
			BindingValueKey bindingValueKey = (BindingValueKey) element;
			if (bindingValueKey != null) {
				try {
					if (!bindingValueKey.isLeaf()) {
						image = ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.TO_ONE_ICON);
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
		return image;
	}

	public String getColumnText(Object element, int columnIndex) {
		String text = null;
		if (columnIndex == 0) {
			BindingValueKey bindingValueKey = (BindingValueKey) element;
			if (bindingValueKey != null) {
				text = bindingValueKey.getBindingName();
				int minWidth = 40;
				if (text != null && text.length() < minWidth) {
					StringBuffer textBuffer = new StringBuffer(text);
					for (int i = text.length(); i < minWidth; i++) {
						textBuffer.append(' ');
					}
					text = textBuffer.toString();
				}
			}
			if (text == null) {
				text = "<unknown>";
			}
		} else if (columnIndex == 1) {
			text = null;
		}
		return text;
	}
}