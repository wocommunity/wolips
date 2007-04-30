package tk.eclipse.plugin.htmleditor.template;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

public class JavaScriptContextType extends TemplateContextType {
	
	public static final String CONTEXT_TYPE 
		= HTMLPlugin.getDefault().getPluginId() + ".templateContextType.javascript";

	public JavaScriptContextType(){
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
	}
	
}
