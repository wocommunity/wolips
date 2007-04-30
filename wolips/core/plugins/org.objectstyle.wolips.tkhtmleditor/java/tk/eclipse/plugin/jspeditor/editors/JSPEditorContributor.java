package tk.eclipse.plugin.jspeditor.editors;

import tk.eclipse.plugin.htmleditor.editors.HTMLEditorContributor;

public class JSPEditorContributor extends HTMLEditorContributor {
	
	protected void init(){
		super.init();
		contributer.addActionId(JSPSourceEditor.ACTION_JSP_COMMENT);
	}
	
}
