package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;
import org.objectstyle.wolips.componenteditor.part.HtmlWodTab;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

/**
 * This is the superclass of actions that need access to both the template
 * editor and the wod editor.
 * 
 * @author mschrag
 */
public abstract class AbstractTemplateAction extends ActionDelegate implements IEditorActionDelegate {
	private IEditorPart _activeEditor;

	protected ComponentEditorPart getComponentEditorPart() {
		return (ComponentEditorPart)_activeEditor;
	}
	
	/**
	 * This method will return the current template editor or null if there is
	 * not a current template editor.
	 */
	protected TemplateEditor getTemplateEditor() {
		TemplateEditor templateEditor = null;
		if (_activeEditor != null) {
			ComponentEditorPart cep = (ComponentEditorPart) _activeEditor;
			HtmlWodTab hwt = cep.htmlWodTab();
			if (hwt != null) {
				templateEditor = hwt.templateEditor();
			}
		}
		return templateEditor;
	}

	/**
	 * This method will return the current WOD editor or null if there is not a
	 * current WOD editor.
	 */
	protected WodEditor getWodEditor() {
		WodEditor wodEditor = null;
		if (_activeEditor != null) {
			ComponentEditorPart cep = (ComponentEditorPart) _activeEditor;
			HtmlWodTab hwt = cep.htmlWodTab();
			if (hwt != null) {
				wodEditor = hwt.wodEditor();
			}
		}
		return wodEditor;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		_activeEditor = targetEditor;
	}
}
