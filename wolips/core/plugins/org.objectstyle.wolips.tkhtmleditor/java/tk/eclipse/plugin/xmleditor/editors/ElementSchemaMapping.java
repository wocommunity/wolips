package tk.eclipse.plugin.xmleditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * The model object for the mapping between the root element and the schema.
 * <p>
 * This class also provides two static methods which load and save models
 * from / to the preference store.
 * 
 * @author Naoki Takezoe
 */
public class ElementSchemaMapping {
	
	private String rootElement;
	private String filePath;
	
	public ElementSchemaMapping(String rootElement, String filePath){
		setRootElement(rootElement);
		setFilePath(filePath);
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getRootElement() {
		return rootElement;
	}
	
	public void setRootElement(String rootElement) {
		this.rootElement = rootElement;
	}

	/**
	 * Save models to the preference store.
	 * 
	 * @param list the list which contans models
	 */
	public static void saveToPreference(List<ElementSchemaMapping> list){
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list.size();i++){
			ElementSchemaMapping mapping = list.get(i);
			sb.append(mapping.getRootElement());
			sb.append("\t");
			sb.append(mapping.getFilePath());
			sb.append("\n");
		}
		store.setValue(HTMLPlugin.PREF_SCHEMA_MAPPINGS, sb.toString());
	}
	
	/**
	 * Load models from the preference store as the <code>java.util.List</code>
	 * which contains <code>ElementSchemaMapping</code>.
	 * 
	 * @return the list which contans loaded models
	 */
	public static List<ElementSchemaMapping> loadFromPreference(){
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		List<ElementSchemaMapping> list = new ArrayList<ElementSchemaMapping>();
		
		String customMappings = store.getString(HTMLPlugin.PREF_SCHEMA_MAPPINGS);
		String[] dim = customMappings.split("\n");
		for(int i=0;i<dim.length;i++){
			if(dim[i].length() > 0){
				String[] elemAndPath = dim[i].split("\t");
				ElementSchemaMapping mapping = new ElementSchemaMapping(
						elemAndPath[0], elemAndPath[1]);
				list.add(mapping);
			}
		}
		
		return list;		
	}
	
}
