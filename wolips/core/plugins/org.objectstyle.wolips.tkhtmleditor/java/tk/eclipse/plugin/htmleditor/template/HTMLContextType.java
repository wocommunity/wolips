package tk.eclipse.plugin.htmleditor.template;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * 
 * @author Naoki Takezoe
 */
public class HTMLContextType extends TemplateContextType {
	
	public static final String CONTEXT_TYPE 
		= HTMLPlugin.getDefault().getPluginId() + ".templateContextType.html";
	
	public HTMLContextType(){
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
	}
	
}
