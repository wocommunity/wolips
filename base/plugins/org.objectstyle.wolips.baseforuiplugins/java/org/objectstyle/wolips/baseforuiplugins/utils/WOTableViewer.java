package org.objectstyle.wolips.baseforuiplugins.utils;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class WOTableViewer extends TableViewer {

	public WOTableViewer(Composite parent) {
		super(parent);
	}

	public WOTableViewer(Table table) {
		super(table);
	}

	public WOTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public int indexForElement(Object element) {
		return super.indexForElement(element);
	}

}
