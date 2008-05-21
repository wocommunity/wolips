/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOAndQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public class AndQualifierType implements IQualifierType {
	public String getDisplayString() {
		return "And";
	}

	public boolean isTypeFor(EOQualifier qualifier) {
		return qualifier instanceof EOAndQualifier;
	}

	public void setQualifier(EOQualifier qualifier) {
		// DO NOTHING
	}

	public AbstractQualifierTypeEditor createEditor(Composite parent) {
		return new AndQualifierTypeEditor(parent, SWT.NONE);
	}

	public String toString() {
		return getDisplayString();
	}
}