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
import org.objectstyle.woproject.util.FileStringScanner;
/**
 * @author uli
 *
 */
public class WOVariables {
	private final String WO_ROOT = "wo.woroot";
	//private final String LOCAL_ROOT = "wo.localroot";
	private final String WO_WO_SYSTEM_ROOT = "wo.wosystemroot";
	private final String WO_WO_LOCAL_ROOT = "wo.wolocalroot";
	private final String HOME_ROOT = "wo.homeroot";
	//private final String ABSOLUTE_ROOT = "wo.absoluteroot";
	/**
	 * Key for setting wobuild.properties path by environment
	 * @see WOVariables
	 */
	private final String WOBUILD_PROPERTIES = "WOBUILD_PROPERTIES";
	private final String WOBUILD_PROPERTIES_FILE_NAME = "wobuild.properties";
	private Properties wobuildProperties;
	private File wobuildPropertiesFile;
	private Environment environment;
	/**
	 * Constructor for WOVariables.
	 */
	protected WOVariables(Environment environment) {
		super();
		this.environment = environment;
		this.init();
	}
	/**
	 * Method init.Tries to load wobuild.properties file in the following way
	 * <ul>
	 * <li>looking for a java system property with key
	 * <code>WOBUILD_PROPERTIES</code></li>
	 * <li>looking for an environment variable
	 * <code>Environment.WOBUILD_PROPERTIES</code></li>
	 * <li>try to find wobuild.properties in user.home</li>
	 * This method is invoked when the class is loaded.
	 */
	private void init() {
		// load properties
		wobuildProperties = new Properties();
		// try user home
		if (!validateWobuildPropertiesFile(environment.userHome()
			+ File.separator
			+ "Library"
			+ File.separator
			+ this.WOBUILD_PROPERTIES_FILE_NAME)) {
			// try system property
			if (!validateWobuildPropertiesFile(System
				.getProperty(this.WOBUILD_PROPERTIES))) {
				// try environment variable
				if (!validateWobuildPropertiesFile(environment
					.getEnvVars()
					.getProperty(this.WOBUILD_PROPERTIES))) {
				}
			}
		}
		if (wobuildPropertiesFile != null) {
			try {
				wobuildProperties.load(
					new FileInputStream(wobuildPropertiesFile));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}
	private boolean validateWobuildPropertiesFile(String fileName) {
		if (fileName != null) {
			wobuildPropertiesFile = new File(fileName);
			if (wobuildPropertiesFile.exists()
				&& !wobuildPropertiesFile.isDirectory()) {
				return true;
			}
		}
		wobuildPropertiesFile = null;
		return false;
	}
	/**
	 * Method nextRoot. NEXT_ROOT defined in wobuild.properties (key: <code>wo.
	 * woroot</code>)
	 * @deprecated  Not for public use in the future.
	 * Use localRoot() and systemRoot() instead. 
	 * @return String
	 */
	public String nextRoot() {
		return wobuildProperties.getProperty(WO_ROOT);
	}
	/**
	 * Method localRoot. NEXT_LOCAL_ROOT defined in wobuild.properties (key:
	 * <code>wo.localroot</code>)
	 * @return String
	 */
	public String localRoot() {
		return wobuildProperties.getProperty(WO_WO_LOCAL_ROOT);
	}
	/**
	 * Method systemRoot. NEXT_SYSTEM_ROOT defined in wobuild.properties (key:
	 * <code>wo.systemroot</code>)
	 * @return String
	 */
	public String systemRoot() {
		return wobuildProperties.getProperty(WO_WO_SYSTEM_ROOT);
	}
	public String userHome() {
		String userHome = wobuildProperties.getProperty(HOME_ROOT);
		if (userHome == null) {
			userHome = environment.userHome();
		}
		return userHome;
	}
	/**
	* Method developerDir.
	* @return String
	*/
	public String developerDir() {
		/*
		String returnValue = "";
		if (Environment.isNextRootSet())
			returnValue = Environment.nextRoot();
		returnValue = returnValue + File.separator + "Developer";
		return returnValue;
		*/
		return systemRoot() + File.separator + "Developer";
	}
	/**
	 * Method developerAppsDir.
	 * @return String
	 */
	public String developerAppsDir() {
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
		return systemRoot()
			+ File.separator
			+ "Developer"
			+ File.separator
			+ "Applications";
	}
	/**
	 * Method libraryDir.
	 * @return String
	 */
	public String libraryDir() {
		/*
		String returnValue = "";
		returnValue = Environment.nextRoot() + File.separator + "Library";
		return returnValue;
		*/
		return systemRoot() + File.separator + "Library";
	}
	/**
	 * Method localDeveloperDir.
	 * @return String
	 */
	public String localDeveloperDir() {
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
	public String localLibraryDir() {
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
	/* mn: moved to WOLipsUtils
	public static String woTemplateDirectory() {
		return "templates";
	}*/
	/**
	 * Method woTemplateFiles.
	 * @return String
	 */
	/* mn: moved to WOLipsUtils
	public static String woTemplateFiles() {
		return "/wo_file_templates.xml";
	}*/
	/**
	 * Method woTemplateProject.
	 * @return String
	 */
	/* mn: moved to WOLipsUtils
	public static String woTemplateProject() {
		return "/wo_project_templates.xml";
	}*/
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
	 * @return String with path to the foundation.jar
	 */
	public String foundationJarPath() {
		return "file:///"
			+ systemRoot()
			+ "/Library/Frameworks/JavaFoundation.framework/Resources/Java/javafoundation.jar";
	}
	/**
		 * Method encodePathForFile.
		 * @param aFile
		 * @return String
		 */
	public String encodePathForFile(File aFile) {
		String userHome = null;
		String systemRoot = null;
		String localRoot = null;
		String aPath = null;
		try {
			localRoot = this.localRoot();
			userHome = this.userHome();
			systemRoot = this.systemRoot();
			int localRootLength = 0;
			int userHomeLength = 0;
			int systemRootLength = 0;
			if (localRoot != null)
				localRootLength = localRoot.length();
			if (userHome != null)
				userHomeLength = userHome.length();
			if (systemRoot != null)
				systemRootLength = systemRoot.length();
			//aPath = aFile.getCanonicalPath();
			//u.k. the CanonicalPath will resolve links this will
			//result in path with /Versions/a in it
			aPath = this.convertWindowsPath(aFile.getPath());
			//            aPrefix = this.getAppRootPath();
			//            if((aPrefix != null) && (aPrefix.length() > 1) && (aPath.startsWith(aPrefix))) {
			//            	return "APPROOT" + aPath.substring(aPrefix.length());
			//            }
			if (localRoot != null && aPath.startsWith(localRoot)) {
				boolean otherRoot = false;
				if (localRootLength < userHomeLength
					&& aPath.startsWith(userHome))
					otherRoot = true;
				if (localRootLength < systemRootLength
					&& aPath.startsWith(systemRoot))
					otherRoot = true;
				if (!otherRoot) {
					if (localRootLength == 1) //MacOSX
						return "LOCALROOT" + aPath;
					return "LOCALROOT" + aPath.substring(localRootLength);
				}
			}
			if (userHome != null && aPath.startsWith(userHome)) {
				boolean otherRoot = false;
				if (userHomeLength < systemRootLength
					&& aPath.startsWith(systemRoot))
					otherRoot = true;
				if (!otherRoot)
					return "HOMEROOT" + aPath.substring(userHomeLength);
			}
			if (systemRoot != null && aPath.startsWith(systemRoot)) {
				return "WOROOT" + aPath.substring(systemRootLength);
			}
			return aPath;
		} catch (Exception anException) {
			System.out.println(
				"Exception occured during encoding of the path " + anException);
		} finally {
			localRoot = null;
			userHome = null;
			systemRoot = null;
			aPath = null;
		}
		return null;
	}
	private String convertWindowsPath(String path) {
		if (path == null || path.length() == 0)
			return null;
		return FileStringScanner.replace(path, "\\", "/");
	}
	/**
	 * @return boolean
	 */
	public boolean foundWOBuildProperties() {
		return (wobuildPropertiesFile != null);
	}
}
