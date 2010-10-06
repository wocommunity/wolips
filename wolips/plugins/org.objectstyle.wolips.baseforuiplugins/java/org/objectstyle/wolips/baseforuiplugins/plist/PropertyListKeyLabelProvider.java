package org.objectstyle.wolips.baseforuiplugins.plist;

import org.eclipse.jface.viewers.ColumnLabelProvider;

public class PropertyListKeyLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		return ((PropertyListPath) element).getKey();
	}
}
