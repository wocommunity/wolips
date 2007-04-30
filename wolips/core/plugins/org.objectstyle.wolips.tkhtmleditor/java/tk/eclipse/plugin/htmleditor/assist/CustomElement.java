package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * 
 * @author Naoki Takezoe
 */
public class CustomElement {
	
	private String displayName;
	private String assistString;
	
	public CustomElement(String displayName, String assistString){
		this.displayName = displayName;
		this.assistString = assistString;
	}
	
	public String getAssistString() {
		return assistString;
	}
	public void setAssistString(String assistString) {
		this.assistString = assistString;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public static List loadFromPreference(boolean defaults){
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String value = null;
		if(defaults){
			value = store.getDefaultString(HTMLPlugin.PREF_CUSTOM_ELEMENTS);
		} else {
			value = store.getString(HTMLPlugin.PREF_CUSTOM_ELEMENTS);
		}
		List list = new ArrayList();
		if(value!=null){
			String[] values = value.split("\n");
			for(int i=0;i<values.length;i++){
				String[] split = values[i].split("\t");
				if(split.length==2){
					list.add(new CustomElement(split[0], split[1]));
				}
			}
		}
		return list;
	}
	
	public static void saveToPreference(List list){
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list.size();i++){
			CustomElement element = (CustomElement)list.get(i);
			sb.append(element.getDisplayName());
			sb.append("\t");
			sb.append(element.getAssistString());
			sb.append("\n");
		}
		store.setValue(HTMLPlugin.PREF_CUSTOM_ELEMENTS, sb.toString());
	}
}
