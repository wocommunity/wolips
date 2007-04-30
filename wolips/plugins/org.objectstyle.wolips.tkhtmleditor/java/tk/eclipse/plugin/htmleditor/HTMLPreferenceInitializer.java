package tk.eclipse.plugin.htmleditor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;

import tk.eclipse.plugin.htmleditor.editors.IHTMLColorConstants;

public class HTMLPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		store.setDefault(HTMLPlugin.PREF_COLOR_TAG,StringConverter.asString(IHTMLColorConstants.TAG));
    store.setDefault(HTMLPlugin.PREF_COLOR_ATTRIBUTE,StringConverter.asString(IHTMLColorConstants.ATTRIBUTE));
		store.setDefault(HTMLPlugin.PREF_COLOR_COMMENT,StringConverter.asString(IHTMLColorConstants.HTML_COMMENT));
		store.setDefault(HTMLPlugin.PREF_COLOR_DOCTYPE,StringConverter.asString(IHTMLColorConstants.PROC_INSTR));
		store.setDefault(HTMLPlugin.PREF_COLOR_STRING,StringConverter.asString(IHTMLColorConstants.STRING));
    store.setDefault(HTMLPlugin.PREF_COLOR_OGNL,StringConverter.asString(IHTMLColorConstants.SCRIPT));
    store.setDefault(HTMLPlugin.PREF_COLOR_DYNAMIC,StringConverter.asString(IHTMLColorConstants.SCRIPT));
		store.setDefault(HTMLPlugin.PREF_COLOR_SCRIPT,StringConverter.asString(IHTMLColorConstants.SCRIPT));
		store.setDefault(HTMLPlugin.PREF_COLOR_CSSPROP,StringConverter.asString(IHTMLColorConstants.CSS_PROP));
		store.setDefault(HTMLPlugin.PREF_COLOR_CSSCOMMENT,StringConverter.asString(IHTMLColorConstants.CSS_COMMENT));
		store.setDefault(HTMLPlugin.PREF_COLOR_CSSVALUE,StringConverter.asString(IHTMLColorConstants.CSS_VALUE));
		store.setDefault(HTMLPlugin.PREF_EDITOR_TYPE,"noPreview");
		store.setDefault(HTMLPlugin.PREF_DTD_URI,"");
		store.setDefault(HTMLPlugin.PREF_DTD_PATH,"");
		store.setDefault(HTMLPlugin.PREF_DTD_CACHE,true);
		store.setDefault(HTMLPlugin.PREF_ASSIST_AUTO,false);
		store.setDefault(HTMLPlugin.PREF_ASSIST_CHARS,"</\"");
		store.setDefault(HTMLPlugin.PREF_ASSIST_CLOSE,true);
		store.setDefault(HTMLPlugin.PREF_ASSIST_TIMES,0);
		store.setDefault(HTMLPlugin.PREF_USE_SOFTTAB,false);
		store.setDefault(HTMLPlugin.PREF_SOFTTAB_WIDTH,2);
		store.setDefault(HTMLPlugin.PREF_COLOR_FG,StringConverter.asString(IHTMLColorConstants.FOREGROUND));
		store.setDefault(HTMLPlugin.PREF_COLOR_BG,StringConverter.asString(IHTMLColorConstants.BACKGROUND));
		store.setDefault(HTMLPlugin.PREF_COLOR_BG_DEF,true);
		store.setDefault(HTMLPlugin.PREF_JSP_COMMENT,StringConverter.asString(IHTMLColorConstants.JAVA_COMMENT));
		store.setDefault(HTMLPlugin.PREF_JSP_STRING,StringConverter.asString(IHTMLColorConstants.JAVA_STRING));
		store.setDefault(HTMLPlugin.PREF_JSP_KEYWORD,StringConverter.asString(IHTMLColorConstants.JAVA_KEYWORD));
		store.setDefault(HTMLPlugin.PREF_PAIR_CHAR, true);
		store.setDefault(HTMLPlugin.PREF_SHOW_XML_ERRORS, true);
		store.setDefault(HTMLPlugin.PREF_COLOR_JSCOMMENT,StringConverter.asString(IHTMLColorConstants.JAVA_COMMENT));
		store.setDefault(HTMLPlugin.PREF_COLOR_JSSTRING,StringConverter.asString(IHTMLColorConstants.JAVA_STRING));
		store.setDefault(HTMLPlugin.PREF_COLOR_JSKEYWORD,StringConverter.asString(IHTMLColorConstants.JAVA_KEYWORD));
		store.setDefault(HTMLPlugin.PREF_CUSTOM_ATTRS, "");
		store.setDefault(HTMLPlugin.PREF_CUSTOM_ELEMENTS, "");
		store.setDefault(HTMLPlugin.PREF_TASK_TAGS, "FIXME\t2\nTODO\t1\nXXXX\t1\n");
		store.setDefault(HTMLPlugin.PREF_ENABLE_CLASSNAME, false);
		store.setDefault(HTMLPlugin.PREF_CLASSNAME_ATTRS, "type class classname className bean component");
		store.setDefault(HTMLPlugin.PREF_SCHEMA_MAPPINGS, "");
		
		getContributions(store);
	}
	
	private void getContributions(IPreferenceStore store) {
		try {

			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(HTMLPlugin.getDefault().getPluginId() + ".preferenceContributer");
			IExtension[] extensions = point.getExtensions();
			for(int i=0;i<extensions.length;i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if ("contributer".equals(elements[j].getName())) {
						IHTMLPreferenceContributer contributer = (IHTMLPreferenceContributer) elements[j].createExecutableExtension("class");
						contributer.initializeDefaultPreferences(store);
					}
				}
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}

}
