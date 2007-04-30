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

	private String root = "/";
	private boolean useDTD = true;
	private boolean validateXML = true;
	private boolean validateHTML = true;
	private boolean validateJSP = true;
	private boolean validateDTD = true;
	private boolean validateJS = true;
	private boolean removeMarkers = false;
	private boolean detectTaskTag = false;
	private String[] javaScripts = new String[0];
	
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
		return root;
	}
	
	/**
	 * Sets root of the web application.
	 * 
	 * @param webAppRoot Root of the web application
	 */
	public void setRoot(String webAppRoot) {
		this.root = webAppRoot;
	}
	
	/**
	 * @param useDTD enable DTD based validation and code completion or not
	 * <ul>
	 *   <li>true - enable</li>
	 *   <li>false - disable</li>
	 * </ul>
	 */
	public void setUseDTD(boolean useDTD){
		this.useDTD = useDTD;
	}
	
	/**
	 * @return enable DTD based validation and code completion or not
	 * <ul>
	 *   <li>true - enable</li>
	 *   <li>false - disable</li>
	 * </ul>
	 */
	public boolean getUseDTD(){
		return this.useDTD;
	}
	
	public void setValidateHTML(boolean validateHTML){
		this.validateHTML = validateHTML;
	}
	
	public boolean getValidateHTML(){
		return this.validateHTML;
	}
	
	public void setValidateJSP(boolean validateJSP){
		this.validateJSP = validateJSP;
	}
	
	public boolean getValidateJSP(){
		return this.validateJSP;
	}
	
	public void setValidateDTD(boolean validateDTD){
		this.validateDTD = validateDTD;
	}
	
	public boolean getValidateDTD(){
		return this.validateDTD;
	}
	
	public void setValidateJavaScript(boolean validateJS){
		this.validateJS = validateJS;
	}
	
	public boolean getValidateJavaScript(){
		return this.validateJS;
	}
	
	public void setValidateXML(boolean validateXML){
		this.validateXML = validateXML;
	}
	
	public boolean getValidateXML(){
		return this.validateXML;
	}
	
	public void setRemoveMarkers(boolean removeMarkers){
		this.removeMarkers = removeMarkers;
	}
	
	public boolean getRemoveMarkers(){
		return this.removeMarkers;
	}
	
	public void setDetectTaskTag(boolean detectTaskTag){
		this.detectTaskTag = detectTaskTag;
	}
	
	public boolean getDetectTaskTag(){
		return this.detectTaskTag;
	}
	
	public void setJavaScripts(String[] javaScripts){
		this.javaScripts = javaScripts;
	}
	
	public String[] getJavaScripts(){
		return this.javaScripts;
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
		props.put(P_ROOT, root);
		props.put(P_USE_DTD, String.valueOf(useDTD));
		props.put(P_VALIDATE_XML, String.valueOf(validateXML));
		props.put(P_VALIDATE_HTML, String.valueOf(validateHTML));
		props.put(P_VALIDATE_JSP, String.valueOf(validateJSP));
		props.put(P_VALIDATE_DTD, String.valueOf(validateDTD));
		props.put(P_VALIDATE_JS, String.valueOf(validateJS));
		props.put(P_REMOVE_MARKERS, String.valueOf(removeMarkers));
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<javaScripts.length;i++){
			if(i!=0){
				sb.append("\n");
			}
			sb.append(javaScripts[i]);
		}
		props.put(P_JAVA_SCRIPTS, sb.toString());
		
		File file = configFile.getLocation().makeAbsolute().toFile();
		if(!file.exists()){
			file.createNewFile();
		}
		props.store(new FileOutputStream(file), "EclipseHTMLEditor configuration file");
		
		if(detectTaskTag){
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
		List newNatures = new ArrayList();
		for(int i=0;i<natures.length;i++){
			if(!natures[i].equals(HTMLProjectNature.HTML_NATURE_ID)){
				newNatures.add(natures[i]);
			}
		}
		description.setNatureIds((String[])newNatures.toArray(new String[newNatures.size()]));
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
			
			root = props.getProperty(P_ROOT);
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
			this.root = project.getPersistentProperty(
					new QualifiedName(HTMLPlugin.getDefault().getPluginId(), P_ROOT));
			useDTD = project.getPersistentProperty(
					new QualifiedName(HTMLPlugin.getDefault().getPluginId(), P_USE_DTD));
			validateHTML = project.getPersistentProperty(new QualifiedName(
					HTMLPlugin.getDefault().getPluginId(), P_VALIDATE_HTML));
		}
		
		if(this.root==null){
			this.root = "/";
		}
		
		this.useDTD = getBooleanValue(useDTD, true);
		this.validateXML = getBooleanValue(validateXML, true);
		this.validateHTML = getBooleanValue(validateHTML, true);
		this.validateJSP = getBooleanValue(validateJSP, true);
		this.validateDTD = getBooleanValue(validateDTD, true);
		this.validateJS = getBooleanValue(validateJS, true);
		this.removeMarkers = getBooleanValue(removeMarkers, false);
		this.detectTaskTag = project.hasNature(HTMLProjectNature.HTML_NATURE_ID);
		
		String[] dim = javaScripts.split("\n");
		List list = new ArrayList();
		for(int i=0;i<dim.length;i++){
			if(dim[i].trim().length()!=0){
				list.add(dim[i]);
			}
		}
		this.javaScripts = (String[])list.toArray(new String[list.size()]);
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
