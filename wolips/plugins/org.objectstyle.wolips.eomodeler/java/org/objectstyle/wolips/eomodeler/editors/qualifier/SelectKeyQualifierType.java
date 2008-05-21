/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public class SelectKeyQualifierType implements IQualifierType {
	public boolean isTypeFor(EOQualifier qualifier) {
		return false;
	}

	public String getDisplayString() {
		return "Select Key ...";
	}

	public void setQualifier(EOQualifier qualifier) {
		// DO NOTHING
	}

	public AbstractQualifierTypeEditor createEditor(Composite parent) {
		return new EmptyQualifierTypeEditor(parent, SWT.NONE);
	}

	public String toString() {
		return getDisplayString();
	}
}