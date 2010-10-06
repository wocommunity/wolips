package org.objectstyle.wolips.ui.preferences;

import java.lang.reflect.Field;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class WOLipsDirectoryFieldEditor extends DirectoryFieldEditor {

	public WOLipsDirectoryFieldEditor() {
		// DO NOTHING
	}

	public WOLipsDirectoryFieldEditor(String name, String labelText, int widthInChars, Composite parent) {
		super(name, labelText, parent);
		setWidthInChars(widthInChars);
		doFillIntoGrid(parent, getNumberOfControls());
	}

	public void setWidthInChars(int widthInChars) {
		try {
			Field f = StringFieldEditor.class.getDeclaredField("widthInChars");
			f.setAccessible(true);
			f.set(this, Integer.valueOf(widthInChars));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
