package org.objectstyle.wolips.componenteditor.bindings;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.componenteditor.actions.AbstractTemplateAction;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddActionDialog;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddActionInfo;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class AddActionAction extends AbstractTemplateAction {
	@Override
	public void run(IAction action) {
		try {
			ComponentEditorPart componentEditorPart = getComponentEditorPart();
			if (componentEditorPart != null) {
				TemplateEditor templateEditor = getTemplateEditor();
				WodEditor wodEditor = getWodEditor();
				if (templateEditor != null && wodEditor != null) {
					IType componentType = templateEditor.getParserCache().getComponentType();
					AddActionInfo info = new AddActionInfo(componentType);
					AddActionDialog.open(info, getComponentEditorPart().getSite().getShell());
				}
			}
		} catch (Exception e) {
			ErrorUtils.openErrorDialog(getComponentEditorPart().getSite().getShell(), e);
		}
	}
}
