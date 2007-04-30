package tk.eclipse.plugin.jseditor.launch;

import tk.eclipse.plugin.jseditor.launch.executer.JavaScriptExecutor;

/**
 * Defines constants for the JavaScript launcher.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptLaunchConstants {
	
	/** The executor class name. */
	public static final String JAVASCRIPT_EXECUTOR = JavaScriptExecutor.class.getName();
	
	public static final String ATTR_JAVASCRIPT_FILE = "javascript.file";
	public static final String ATTR_JAVASCRIPT_INCLUDES = "javascript.includes";

}
