/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.woproject.env;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author uli
 *
 */
public class WOVariables {

	public static final String WOBUILD_PROPERTIES = "wobuild.properties";

	private static Properties wobuildProperties;

	static {
		// load properties
		wobuildProperties = new Properties();
		String propertyFileName =
			Environment.getEnvVars().getProperty("user.home")
				+ File.separator
				+ "Library"
				+ File.separator
				+ WOBUILD_PROPERTIES;
		File propertyFile = new File(propertyFileName);
		if (!propertyFile.exists() || propertyFile.isDirectory()) {
			// log
		} else {
			try {
				wobuildProperties.load(new FileInputStream(propertyFileName));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}
	/**
	 * Constructor for WOVariables.
	 */
	private WOVariables() {
		super();
	}

	/**
	 * Method nextRoot. NEXT_ROOT defined in wobuild.properties (key: <code>wo.
	 * woroot</code>)
	 * @return String
	 */
	public static String nextRoot() {
		return wobuildProperties.getProperty("wo.woroot");
	}

	/**
	 * Method localRoot. NEXT_LOCAL_ROOT defined in wobuild.properties (key:
	 * <code>wo.localroot</code>)
	 * @return String
	 */
	public static String localRoot() {
		return wobuildProperties.getProperty("wo.wolocalroot");
	}

	/**
	 * Method systemRoot. NEXT_SYSTEM_ROOT defined in wobuild.properties (key:
	 * <code>wo.systemroot</code>)
	 * @return String
	 */
	public static String systemRoot() {
		return wobuildProperties.getProperty("wo.wosystemroot");
	}
	
	/**
	 * Method developerDir.
	 * @return String
	 */
	public static String developerDir() {
		/*
		String returnValue = "";
		if (Environment.isNextRootSet())
			returnValue = Environment.nextRoot();
		returnValue = returnValue + File.separator + "Developer";
		return returnValue;
		*/
		return nextRoot()+ File.separator + "Developer";
	}
	/**
	 * Method developerAppsDir.
	 * @return String
	 */
	public static String developerAppsDir() {
		/*
		String returnValue = "";
		if (Environment.isNextRootSet())
			returnValue = Environment.nextRoot();
		returnValue =
			returnValue
				+ File.separator
				+ "Developer"
				+ File.separator
				+ "Applications";
		return returnValue;
		*/
		return nextRoot() + File.separator
		+ "Developer"
		+ File.separator
		+ "Applications"; 
	}
	/**
	 * Method libraryDir.
	 * @return String
	 */
	public static String libraryDir() {
		/*
		String returnValue = "";
		returnValue = Environment.nextRoot() + File.separator + "Library";
		return returnValue;
		*/
		return nextRoot() + File.separator + "Library";
	}
	/**
	 * Method localDeveloperDir.
	 * @return String
	 */
	public static String localDeveloperDir() {
		/*
		String returnValue = "";
		
		if (Environment.isNextRootSet())
			returnValue = WOVariables.nextRoot();
			
		returnValue = Environment.localRoot() + File.separator + "Developer";
		return returnValue;
		*/
		return localRoot() + File.separator + "Developer";
	}
	/**
	 * Method localLibraryDir.
	 * @return String
	 */
	public static String localLibraryDir() {
		/*
		String returnValue = "";
		
		if (Environment.isNextRootSet())
			returnValue = WOVariables.nextRoot();
			
		returnValue = Environment.localRoot() + File.separator + "Library";
		return returnValue;
		*/
		return localRoot() + File.separator + "Library";
	}
	/**
	 * Method woTemplateDirectory.
	 * @return String
	 */
	public static String woTemplateDirectory() {
		return "templates";
	}
	/**
	 * Method woTemplateFiles.
	 * @return String
	 */
	public static String woTemplateFiles() {
		return "/wo_file_templates.xml";
	}
	/**
	 * Method woTemplateProject.
	 * @return String
	 */
	public static String woTemplateProject() {
		return "/wo_project_templates.xml";
	}
	/**
	 * Method woProjectFileName.
	 * @return String
	 */
	public static String woProjectFileName() {
		return "PB.project";
	}
	/**
	 * Method webServerResourcesDirName.
	 * @return String
	 */
	public static String webServerResourcesDirName() {
		return "WebServerResources";
	}
	/**
	 * Method classPathVariableToExpand.
	 * @param aString
	 * @return String
	 */
	public static String classPathVariableToExpand(String aString) {
		if (aString == null)
			return null;
		if (aString.equals("webobjects.next.root"))
			return Environment.nextRoot();
		if (aString.equals("webobjects.system.library.dir"))
			return WOVariables.libraryDir();
		//WOLipsLog.log("Can not resolve classpath variable: " + aString);
		return null;
	}
}
