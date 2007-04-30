package tk.eclipse.plugin.htmleditor.template;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * The preference page for HTML code completion templates.
 * 
 * @author Naoki Takezoe
 */
public class HTMLTemplatePreferencePage extends TemplatePreferencePage  implements IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 */
	public HTMLTemplatePreferencePage() {
		try {
			setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
			setTemplateStore(HTMLTemplateManager.getInstance().getTemplateStore());
			setContextTypeRegistry(HTMLTemplateManager.getInstance().getContextTypeRegistry());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	protected boolean isShowFormatterSetting() {
		return false;
	}
	
	public boolean performOk() {
		boolean ok = super.performOk();
		HTMLPlugin.getDefault().savePluginPreferences();
		return ok;
	}

}
