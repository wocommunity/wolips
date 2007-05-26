package tk.eclipse.plugin.htmleditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;


/**
 * This is a class to access and modify project preferences.
 * 
 * @author Naoki Takezoe
 */
public class HTMLProjectParams {

	private String _root = "/";
	private boolean _useDTD = true;
	private boolean _validateXML = true;
	private boolean _validateHTML = true;
	private boolean _validateJSP = true;
	private boolean _validateDTD = true;
	private boolean _validateJS = true;
	private boolean _removeMarkers = false;
	private boolean _detectTaskTag = false;
	private String[] _javaScripts = new String[0];
	
	public static final String P_ROOT = "root";
	public static final String P_USE_DTD = "useDTD";
	public static final String P_VALIDATE_XML = "validateXML";
	public static final String P_VALIDATE_HTML = "validateHTML";
	public static final String P_VALIDATE_JSP = "validateJSP";
	public static final String P_VALIDATE_DTD = "validateDTD";
	public static final String P_VALIDATE_JS = "validateJS";
	public static final String P_REMOVE_MARKERS = "removeMarkers";
	public static final String P_JAVA_SCRIPTS = "javaScripts";
	
	/**
	 * Create empty WebProjectParams.
	 */
	public HTMLProjectParams() {
	}
	
	/**
	 * Create WebProjectParams loading specified project configuration.
	 * 
	 * @param javaProject Java project
	 * @throws Exception
	 */
	public HTMLProjectParams(IProject project) throws Exception {
		load(project);
	}
	
	/**
	 * Returns root of the web application.
	 * 
	 * @return Root of the web application
	 */
	public String getRoot() {
		return _root;
	}
	
	/**
	 * Sets root of the web application.
	 * 
	 * @param webAppRoot Root of the web application
	 */
	public void setRoot(String webAppRoot) {
		this._root = webAppRoot;
	}
	
	/**
	 * @param useDTD enable DTD based validation and code completion or not
	 * <ul>
	 *   <li>true - enable</li>
	 *   <li>false - disable</li>
	 * </ul>
	 */
	public void setUseDTD(boolean useDTD){
		this._useDTD = useDTD;
	}
	
	/**
	 * @return enable DTD based validation and code completion or not
	 * <ul>
	 *   <li>true - enable</li>
	 *   <li>false - disable</li>
	 * </ul>
	 */
	public boolean getUseDTD(){
		return this._useDTD;
	}
	
	public void setValidateHTML(boolean validateHTML){
		this._validateHTML = validateHTML;
	}
	
	public boolean getValidateHTML(){
		return this._validateHTML;
	}
	
	public void setValidateJSP(boolean validateJSP){
		this._validateJSP = validateJSP;
	}
	
	public boolean getValidateJSP(){
		return this._validateJSP;
	}
	
	public void setValidateDTD(boolean validateDTD){
		this._validateDTD = validateDTD;
	}
	
	public boolean getValidateDTD(){
		return this._validateDTD;
	}
	
	public void setValidateJavaScript(boolean validateJS){
		this._validateJS = validateJS;
	}
	
	public boolean getValidateJavaScript(){
		return this._validateJS;
	}
	
	public void setValidateXML(boolean validateXML){
		this._validateXML = validateXML;
	}
	
	public boolean getValidateXML(){
		return this._validateXML;
	}
	
	public void setRemoveMarkers(boolean removeMarkers){
		this._removeMarkers = removeMarkers;
	}
	
	public boolean getRemoveMarkers(){
		return this._removeMarkers;
	}
	
	public void setDetectTaskTag(boolean detectTaskTag){
		this._detectTaskTag = detectTaskTag;
	}
	
	public boolean getDetectTaskTag(){
		return this._detectTaskTag;
	}
	
	public void setJavaScripts(String[] javaScripts){
		this._javaScripts = javaScripts;
	}
	
	public String[] getJavaScripts(){
		return this._javaScripts;
	}
	
	/**
	 * Save configuration.
	 * 
	 * @param javaProject Java project
	 * @throws Exception
	 */
	public void save(IProject project) throws Exception {
		IFile configFile = project.getFile(".amateras");
		Properties props = new Properties();
		props.put(P_ROOT, _root);
		props.put(P_USE_DTD, String.valueOf(_useDTD));
		props.put(P_VALIDATE_XML, String.valueOf(_validateXML));
		props.put(P_VALIDATE_HTML, String.valueOf(_validateHTML));
		props.put(P_VALIDATE_JSP, String.valueOf(_validateJSP));
		props.put(P_VALIDATE_DTD, String.valueOf(_validateDTD));
		props.put(P_VALIDATE_JS, String.valueOf(_validateJS));
		props.put(P_REMOVE_MARKERS, String.valueOf(_removeMarkers));
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<_javaScripts.length;i++){
			if(i!=0){
				sb.append("\n");
			}
			sb.append(_javaScripts[i]);
		}
		props.put(P_JAVA_SCRIPTS, sb.toString());
		
		File file = configFile.getLocation().makeAbsolute().toFile();
		if(!file.exists()){
			file.createNewFile();
		}
		props.store(new FileOutputStream(file), "EclipseHTMLEditor configuration file");
		
		if(_detectTaskTag){
			addNature(project);
		} else {
			removeNature(project);
		}
		
		project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
	}
	
	private void addNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		for(int i=0;i<natures.length;i++){
			if(natures[i].equals(HTMLProjectNature.HTML_NATURE_ID)){
				return;
			}
		}
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = HTMLProjectNature.HTML_NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}
	
	private void removeNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		List<String> newNatures = new ArrayList<String>();
		for(int i=0;i<natures.length;i++){
			if(!natures[i].equals(HTMLProjectNature.HTML_NATURE_ID)){
				newNatures.add(natures[i]);
			}
		}
		description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
		project.setDescription(description, null);
	}
	
	/**
	 * Load configuration.
	 * 
	 * @param javaProject Java project
	 * @throws Exception
	 */
	public void load(IProject project) throws Exception {
		IFile configFile = project.getFile(".amateras");
		
		String useDTD = null;
		String validateXML = null;
		String validateHTML = null;
		String validateJSP = null;
		String validateDTD = null;
		String validateJS = null;
		String removeMarkers = null;
		String javaScripts = "";
		
		if(configFile.exists()){
			File file = configFile.getLocation().makeAbsolute().toFile();
			Properties props = new Properties();
			props.load(new FileInputStream(file));
			
			_root = props.getProperty(P_ROOT);
			useDTD = props.getProperty(P_USE_DTD);
			validateXML = props.getProperty(P_VALIDATE_XML);
			validateHTML = props.getProperty(P_VALIDATE_HTML);
			validateJSP = props.getProperty(P_VALIDATE_JSP);
			validateDTD = props.getProperty(P_VALIDATE_DTD);
			validateJS = props.getProperty(P_VALIDATE_JS);
			removeMarkers = props.getProperty(P_REMOVE_MARKERS);
			
			javaScripts = props.getProperty(P_JAVA_SCRIPTS);
			if(javaScripts==null){
				javaScripts = "";
			}
			
		} else {
			// for old versions
			this._root = project.getPersistentProperty(
					new QualifiedName(HTMLPlugin.getDefault().getPluginId(), P_ROOT));
			useDTD = project.getPersistentProperty(
					new QualifiedName(HTMLPlugin.getDefault().getPluginId(), P_USE_DTD));
			validateHTML = project.getPersistentProperty(new QualifiedName(
					HTMLPlugin.getDefault().getPluginId(), P_VALIDATE_HTML));
		}
		
		if(this._root==null){
			this._root = "/";
		}
		
		this._useDTD = getBooleanValue(useDTD, true);
		this._validateXML = getBooleanValue(validateXML, true);
		this._validateHTML = getBooleanValue(validateHTML, true);
		this._validateJSP = getBooleanValue(validateJSP, true);
		this._validateDTD = getBooleanValue(validateDTD, true);
		this._validateJS = getBooleanValue(validateJS, true);
		this._removeMarkers = getBooleanValue(removeMarkers, false);
		this._detectTaskTag = project.hasNature(HTMLProjectNature.HTML_NATURE_ID);
		
		String[] dim = javaScripts.split("\n");
		List<String> list = new ArrayList<String>();
		for(int i=0;i<dim.length;i++){
			if(dim[i].trim().length()!=0){
				list.add(dim[i]);
			}
		}
		this._javaScripts = list.toArray(new String[list.size()]);
	}
	
	private boolean getBooleanValue(String value, boolean defaultValue){
		if(value!=null){
			if(value.equals("true")){
				return true;
			} else if(value.equals("false")){
				return false;
			}
		}
		return defaultValue;
	}

}
