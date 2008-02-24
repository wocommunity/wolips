package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.QuickRenameRefactoring;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class QuickRenameElementAction extends AbstractTemplateAction {
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
						ITextSelection templateSelection = (ITextSelection) templateEditor.getSourceEditor().getSelectionProvider().getSelection();
						int offset = templateSelection.getOffset();
						WodParserCache cache = templateEditor.getSourceEditor().getParserCache();
						QuickRenameRefactoring.renameHtmlSelection(offset, templateEditor.getSourceEditor().getViewer(), wodEditor.getViewer(), cache);
					} else if (activeEditorPart == wodEditor) {
						ITextSelection wodSelection = (ITextSelection) wodEditor.getSelectionProvider().getSelection();
						int offset = wodSelection.getOffset();
						WodParserCache cache = wodEditor.getParserCache();
						QuickRenameRefactoring.renameWodSelection(offset, templateEditor.getSourceEditor().getViewer(), wodEditor.getViewer(), cache);
					} else {
						System.out.println("QuickRenameElementAction.run: " + activeEditorPart);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
