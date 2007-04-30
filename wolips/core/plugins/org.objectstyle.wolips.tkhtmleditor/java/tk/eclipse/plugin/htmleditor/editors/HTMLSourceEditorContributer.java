package tk.eclipse.plugin.htmleditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * Extended TextEditorActionContributor.
 * <p>
 * This contributor contributes actions which are got from Texteditor to the Edit menu.
 * </p>
 * 
 * @author Naoki Takezoe
 */
public class HTMLSourceEditorContributer extends TextEditorActionContributor {
	
	private List actionIds = new ArrayList();
	private List actions = new ArrayList();
	
	public void addActionId(String id){
		this.actionIds.add(id);
	}
	
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}
	
	private void doSetActiveEditor(IEditorPart part) {
		ITextEditor textEditor= null;
		if (part instanceof ITextEditor){
			textEditor = (ITextEditor) part;
		}
		if(textEditor!=null){
			for(int i=0;i<this.actions.size();i++){
				RetargetTextEditorAction action = (RetargetTextEditorAction)actions.get(i);
				IAction targetAction = textEditor.getAction((String)actionIds.get(i));
				if(targetAction!=null){
					action.setAccelerator(targetAction.getAccelerator());
					action.setAction(targetAction);
				} else {
					action.setAccelerator(SWT.NULL);
					action.setAction(null);
				}
			}
		}
	}
	
	public void init(IActionBars bars) {
		super.init(bars);
		
		IMenuManager menuManager = bars.getMenuManager();
		IMenuManager editMenu = menuManager.findMenuUsingPath("edit");
		editMenu.insertBefore("additions", new Separator("amateras"));
		
		if (editMenu != null) {
			for(int i=0;i<actionIds.size();i++){
				RetargetTextEditorAction action = new RetargetTextEditorAction(HTMLPlugin.getDefault().getResourceBundle(), null);
				this.actions.add(action);
				editMenu.appendToGroup("amateras",action);
			}
		}
	}

}
