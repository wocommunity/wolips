package org.objectstyle.wolips.baseforuiplugins.plist;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class PropertyListTypeLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		PropertyListPath path = (PropertyListPath) element;
		return path.getType().getName();
	}
}
