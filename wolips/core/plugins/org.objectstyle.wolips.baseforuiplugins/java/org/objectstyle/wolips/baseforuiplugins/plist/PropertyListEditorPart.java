package org.objectstyle.wolips.baseforuiplugins.plist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class PropertyListEditorPart extends EditorPart {

	public PropertyListEditorPart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// DO NOTHING
	}

	@Override
	public void doSaveAs() {
		// DO NOTHING
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// DO NOTHING
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		// DO NOTHING
	}

	@Override
	public void setFocus() {
		// DO NOTHING
	}

}
