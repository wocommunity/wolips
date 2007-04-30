package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * @author Naoki Takezoe
 */
public class CustomAttribute {
	
	private String targetTag;
	private String attributeName;
	
	public CustomAttribute(String targetTag, String attributeName){
		this.targetTag = targetTag;
		this.attributeName = attributeName;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getTargetTag() {
		return targetTag;
	}

	public void setTargetTag(String targetTag) {
		this.targetTag = targetTag;
	}
	
	public static List loadFromPreference(boolean defaults){
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String value = null;
		if(defaults){
			value = store.getDefaultString(HTMLPlugin.PREF_CUSTOM_ATTRS);
		} else {
			value = store.getString(HTMLPlugin.PREF_CUSTOM_ATTRS);
		}
		List list = new ArrayList();
		if(value!=null){
			String[] values = value.split("\n");
			for(int i=0;i<values.length;i++){
				String[] split = values[i].split("\t");
				if(split.length==2){
					list.add(new CustomAttribute(split[0], split[1]));
				}
			}
		}
		return list;
	}
	
	public static void saveToPreference(List list){
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list.size();i++){
			CustomAttribute attrInfo = (CustomAttribute)list.get(i);
			sb.append(attrInfo.getTargetTag());
			sb.append("\t");
			sb.append(attrInfo.getAttributeName());
			sb.append("\n");
		}
		store.setValue(HTMLPlugin.PREF_CUSTOM_ATTRS, sb.toString());
	}
}
