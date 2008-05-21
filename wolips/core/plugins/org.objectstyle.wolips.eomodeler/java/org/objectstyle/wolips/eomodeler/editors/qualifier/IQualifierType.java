/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public interface IQualifierType {
	public boolean isTypeFor(EOQualifier qualifier);

	public void setQualifier(EOQualifier qualifier);
	
	public String getDisplayString();

	public AbstractQualifierTypeEditor createEditor(Composite parent);
}