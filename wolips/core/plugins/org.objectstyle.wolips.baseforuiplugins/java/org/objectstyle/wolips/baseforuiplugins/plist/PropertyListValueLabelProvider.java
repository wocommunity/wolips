package org.objectstyle.wolips.baseforuiplugins.plist;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class PropertyListValueLabelProvider extends ColumnLabelProvider {
	private Color _collectionColor;

	public PropertyListValueLabelProvider() {
		_collectionColor = new Color(Display.getCurrent(), 100, 100, 100);
	}

	@Override
	public String getText(Object element) {
		PropertyListPath path = (PropertyListPath) element;
		int childCount = path.getChildCount();
		String text;
		if (childCount >= 0) {
			text = MessageFormat.format("({0,number,integer} {0,choice,0#items|1#item|1<items})", childCount);
		} else {
			Object value = path.getValue();
			if (value == null) {
				text = "";
			}
			else {
				text = String.valueOf(value);
			}
		}
		return text;
	}

	@Override
	public Color getForeground(Object element) {
		PropertyListPath path = (PropertyListPath) element;
		Color foreground;
		if (path.isCollectionValue()) {
			foreground = _collectionColor;
		} else {
			foreground = super.getForeground(element);
		}
		return foreground;
	}

	@Override
	public void dispose() {
		_collectionColor.dispose();
		super.dispose();
	}
}
