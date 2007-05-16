package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class EMTableViewer extends TableViewer {

	public EMTableViewer(Composite parent) {
		super(parent);
	}

	public EMTableViewer(Table table) {
		super(table);
	}

	public EMTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public int indexForElement(Object element) {
		return super.indexForElement(element);
	}

}
