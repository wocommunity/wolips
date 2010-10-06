package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.ConvertInlineToWodRefactoring;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class ConvertInlineToWodAction extends AbstractTemplateAction {
	@Override
	public void run(IAction action) {
		try {
			ComponentEditorPart componentEditorPart = getComponentEditorPart();
			if (componentEditorPart != null) {
				IEditorPart activeEditorPart = componentEditorPart.getActiveEditor();
				TemplateEditor templateEditor = getTemplateEditor();
				WodEditor wodEditor = getWodEditor();
				if (templateEditor != null && wodEditor != null && activeEditorPart == templateEditor) {
					ITextSelection templateSelection = (ITextSelection) templateEditor.getSourceEditor().getSelectionProvider().getSelection();
					int offset = templateSelection.getOffset();
					WodParserCache cache = templateEditor.getSourceEditor().getParserCache();
					BuildProperties buildProperties = (BuildProperties)cache.getProject().getAdapter(BuildProperties.class);
					ConvertInlineToWodRefactoring.run(cache, offset, buildProperties, new NullProgressMonitor());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
