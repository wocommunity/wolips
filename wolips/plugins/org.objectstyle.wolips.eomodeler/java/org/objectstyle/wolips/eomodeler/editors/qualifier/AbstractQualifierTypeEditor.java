/**
 * 
 */
package org.objectstyle.wolips.eomodeler.editors.qualifier;

import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.core.model.qualifier.EOQualifier;

public abstract class AbstractQualifierTypeEditor extends Composite {
	private IQualifierTypeEditorListener _listener;

	public AbstractQualifierTypeEditor(Composite parent, int style) {
		super(parent, style);
	}

	public void setQualifierTypeEditorListener(IQualifierTypeEditorListener listener) {
		_listener = listener;
	}

	public IQualifierTypeEditorListener getQualifierTypeEditorListener() {
		return _listener;
	}

	public abstract void setQualifier(EOQualifier qualifier);

	public abstract EOQualifier getQualifier();
}