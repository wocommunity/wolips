/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOOrQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public class OrQualifierType implements IQualifierType {
	public String getDisplayString() {
		return "Or";
	}

	public boolean isTypeFor(EOQualifier qualifier) {
		return qualifier instanceof EOOrQualifier;
	}

	public void setQualifier(EOQualifier qualifier) {
		// DO NOTHING
	}

	public AbstractQualifierTypeEditor createEditor(Composite parent) {
		return new OrQualifierTypeEditor(parent, SWT.NONE);
	}

	public String toString() {
		return getDisplayString();
	}
}