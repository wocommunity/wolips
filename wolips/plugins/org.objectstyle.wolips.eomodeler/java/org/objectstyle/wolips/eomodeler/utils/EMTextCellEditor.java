package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class EMTextCellEditor extends TextCellEditor {
	public EMTextCellEditor() {
		// DO NOTHING
	}

	public EMTextCellEditor(Composite parent) {
		super(parent);
	}

	public EMTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}
	
	public Text getText() {
		return text;
	}
}
