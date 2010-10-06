package org.objectstyle.wolips.components.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorPart;

public interface IComponentEditor {
	public boolean embeddedEditorWillSave(IProgressMonitor progressMonitor);
	
	public IEditorPart getActiveEditor();
	
	public IEditorPart getWodEditor();
	
	public IEditorPart getTemplateEditor();

}
