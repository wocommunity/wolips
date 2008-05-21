/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.core.model.EOQualifierFactory;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOTruePredicate;

public class ExpressionQualifierTypeEditor extends AbstractQualifierTypeEditor {
	private Text _expressionText;

	public ExpressionQualifierTypeEditor(Composite parent, int style) {
		super(parent, style);
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.marginWidth = 0;
		setLayout(layout);
		_expressionText = new Text(this, SWT.BORDER);
	}

	public void setQualifier(EOQualifier qualifier) {
		String qualifierString;
		if (qualifier instanceof EOTruePredicate) {
			qualifierString = "";
		} else {
			qualifierString = EOQualifierFactory.toString(qualifier);
		}
		if (qualifierString == null) {
			qualifierString = "";
		}
		_expressionText.setText(qualifierString);
	}

	public EOQualifier getQualifier() {
		EOQualifier qualifier = EOQualifierFactory.fromString(_expressionText.getText());
		if (qualifier == null) {
			qualifier = new EOTruePredicate();
		}
		return qualifier;
	}
}