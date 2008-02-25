package org.objectstyle.wolips.componenteditor.bindings;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.componenteditor.actions.AbstractTemplateAction;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddKeyDialog;
import org.objectstyle.wolips.wodclipse.core.refactoring.AddKeyInfo;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class AddKeyAction extends AbstractTemplateAction {
	@Override
	public void run(IAction action) {
		try {
			ComponentEditorPart componentEditorPart = getComponentEditorPart();
			if (componentEditorPart != null) {
				TemplateEditor templateEditor = getTemplateEditor();
				WodEditor wodEditor = getWodEditor();
				if (templateEditor != null && wodEditor != null) {
					IType componentType = templateEditor.getParserCache().getComponentType();
					AddKeyInfo info = new AddKeyInfo(componentType);
					AddKeyDialog.open(info, getComponentEditorPart().getSite().getShell());
				}
			}
		} catch (Exception e) {
			ErrorUtils.openErrorDialog(getComponentEditorPart().getSite().getShell(), e);
		}
	}
}
