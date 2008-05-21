/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EONotQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public class NotQualifierType implements IQualifierType {
	public String getDisplayString() {
		return "Not";
	}

	public boolean isTypeFor(EOQualifier qualifier) {
		return qualifier instanceof EONotQualifier;
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