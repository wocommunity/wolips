package org.objectstyle.wolips.eomodeler.editors.qualifier;

public interface IQualifierEditorListener {
	public void qualifierAddedAbove(EOQualifierEditor editor);

	public void qualifierAddedBelow(EOQualifierEditor editor);

	public void qualifierRemoved(EOQualifierEditor editor);
}
