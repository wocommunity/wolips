package org.objectstyle.wolips.componenteditor.actions;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.refactoring.DeleteTagRefactoring;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class DeleteTagAction extends AbstractTemplateAction {
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
						FuzzyXMLElement element = templateEditor.getSourceEditor().getElementAtOffset(offset, true);
				        boolean wo54 = Activator.getDefault().isWO54();
						DeleteTagRefactoring.run(element, wo54, templateEditor.getParserCache(), new NullProgressMonitor());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
