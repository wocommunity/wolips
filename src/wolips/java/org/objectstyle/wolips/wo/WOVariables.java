package org.objectstyle.wolips.wo;

import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.env.Environment;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WOVariables {

	/**
	 * Constructor for WOVariables.
	 */
	private WOVariables() {
		super();
	}
	
	public static String nextRoot() {
		return Environment.nextRoot();
	}
	
	public static String developerDir() {
		String returnValue = "";
		if (Environment.isNextRootSet()) returnValue = WOVariables.nextRoot();
		returnValue = returnValue + "/Developer";
		return returnValue;
	}
	
	public static String developerAppsDir() {
		String returnValue = "";
		if (Environment.isNextRootSet()) returnValue = WOVariables.nextRoot();
		returnValue = returnValue + "/Developer/Applications";
		return returnValue;
	}
	
	public static String libraryDir() {
		String returnValue = "";
		returnValue = WOVariables.nextRoot() + "/Library";
		return returnValue;
	}
	
	public static String localDeveloperDir() {
		String returnValue = "";
		if (Environment.isNextRootSet()) returnValue = WOVariables.nextRoot();
		returnValue = returnValue + "/Developer";
		return returnValue;
	}
	
	public static String localLibraryDir() {
		String returnValue = "";
		if (Environment.isNextRootSet()) returnValue = WOVariables.nextRoot();
		returnValue = returnValue + "/Library";
		return returnValue;
	}

	public static String woTemplateDirectory() {
		return "templates";
	}
	
	public static String woTemplateFiles() {
		return "/wo_file_templates.xml";
	}
	
	public static String woTemplateProject() {
		return "/wo_project_templates.xml";
	}
	
	public static String woProjectFileName() {
		return "PB.project";
	}
	
	public static String woProjectTypeJavaApplication() {
		return "JavaWebObjectsApplication";
	}
	
	public static String woProjectTypeJavaFramework() {
		return "JavaWebObjectsFramework";
	}
	
	public static String classPathVariableToExpand(String aString) {
		String returnValue = "";
		if (aString != null) {
			if(aString.equals("webobjects.next.root"))
				returnValue = WOVariables.nextRoot();
			if(aString.equals("webobjects.system.library.dir"))
				returnValue = WOVariables.libraryDir();
		}
		if ((returnValue == null) || (returnValue.equals("")))
			WOLipsPlugin.log("Can not resolve classpath variable: " + aString);
		return returnValue;
	}
}
