package tk.eclipse.plugin.jseditor.editors;

import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditorContributer;

public class JavaScriptEditorContributor extends HTMLSourceEditorContributer {
	
	public JavaScriptEditorContributor(){
		addActionId(JavaScriptEditor.ACTION_COMMENT);
	}
	
}
