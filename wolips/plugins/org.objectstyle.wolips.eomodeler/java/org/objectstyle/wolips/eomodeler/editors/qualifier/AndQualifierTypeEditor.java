/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOAggregateQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOAndQualifier;

public class AndQualifierTypeEditor extends AggregateQualifierTypeEditor {
	public AndQualifierTypeEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected EOAggregateQualifier createQualifier() {
		return new EOAndQualifier();
	}
}