package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class UnwrapTagAction extends AbstractTemplateAction {
	@Override
	public void run(IAction action) {
		try {
			ComponentEditorPart componentEditorPart = getComponentEditorPart();
			if (componentEditorPart != null) {
				IEditorPart activeEditorPart = componentEditorPart.getActiveEditor();
				TemplateEditor templateEditor = getTemplateEditor();
				WodEditor wodEditor = getWodEditor();
				if (templateEditor != null && wodEditor != null) {
					if (activeEditorPart == templateEditor) {
						templateEditor.getSourceEditor().new UnwrapTagAction().run();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
