package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.QuickRenameRefactoring;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class QuickRenameElementAction extends AbstractTemplateAction {
	@Override
	public void run(IAction action) {
		try {
			TemplateEditor templateEditor = getTemplateEditor();
			WodEditor wodEditor = getWodEditor();
			if (templateEditor != null && wodEditor != null) {
				ITextSelection templateSelection = (ITextSelection) templateEditor.getSourceEditor().getSelectionProvider().getSelection();
				int offset = templateSelection.getOffset();
				WodParserCache cache = templateEditor.getSourceEditor().getParserCache();
				QuickRenameRefactoring.rename(offset, templateEditor.getSourceEditor().getViewer(), wodEditor.getViewer(), cache);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
