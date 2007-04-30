package tk.eclipse.plugin.xmleditor.editors;

import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditorContributer;

/**
 * The editor contributor for the <code>XMLEditor</code>.
 * 
 * @author Naoki Takezoe
 */
public class XMLEditorContributor extends HTMLSourceEditorContributer {
	
	public XMLEditorContributor(){
		addActionId(XMLEditor.ACTION_ESCAPE_XML);
		addActionId(XMLEditor.ACTION_COMMENT);
	}
	
}
