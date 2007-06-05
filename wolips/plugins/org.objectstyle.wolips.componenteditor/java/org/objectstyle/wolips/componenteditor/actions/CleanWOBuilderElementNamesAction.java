package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.CleanWOBuilderRefactoring;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class CleanWOBuilderElementNamesAction extends AbstractTemplateAction {
	@Override
	public void run(IAction action) {
		try {
			TemplateEditor templateEditor = getTemplateEditor();
			WodEditor wodEditor = getWodEditor();
			if (templateEditor != null && wodEditor != null) {
				WodParserCache cache = templateEditor.getSourceEditor().getParserCache();
				CleanWOBuilderRefactoring.run(cache, false, new NullProgressMonitor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
