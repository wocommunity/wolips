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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author uli
 * Utility for the environment.
 */
public class Environment {
	public static Log log = LogFactory.getLog(Environment.class);
	/**
	 * The String NEXT_ROOT.
	 */
	public static final String NEXT_ROOT = "NEXT_ROOT";
	public static final String NEXT_LOCAL_ROOT = "NEXT_LOCAL_ROOT";
	public static final String NEXT_SYSTEM_ROOT = "NEXT_SYSTEM_ROOT";
	/**
	 * Key for setting wobuild.properties path by environment
	 * @see WOVariables
	 */
	public static final String WOBUILD_PROPERTIES = "WOBUILD_PROPERTIES";
	private static Properties envVars;
	private static String nextRoot;
	private static String localRoot;
	private static String foundationJarPath;
	/**
	 * Constructor for Environment.
	 */
	private Environment() {
		super();
	}
	/**
	 * The values are cached.
	 * @return environment variables as Properties.
	 * @throws Exception
	 */
	public static Properties getEnvVars() {
		if (Environment.envVars != null)
			return Environment.envVars;
		Process p = null;
		BufferedReader br = null;
		String line = null;
		try {
			p = Environment.osProcess();
		} catch (InvocationTargetException e) {
			log.warn("getEnvVars -> unable to load environment variables", e);
		}
		Environment.envVars = new Properties();
		br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = br.readLine()) != null) {
				int idx = line.indexOf('=');
				String key = line.substring(0, idx);
				String value = line.substring(idx + 1);
				Environment.envVars.setProperty(key, value);
			}
		} catch (IOException e) {
			log.warn("getEnvVars -> unable to load environment variables", e);
		}

		p = null;
		br = null;
		line = null;
		return Environment.envVars;
	}
	/**
	 * Method osProcess.
	 * @return Process
	 * @throws Exception
	 */
	private static Process osProcess() throws InvocationTargetException {
		Process p = null;
		Runtime r = null;
		String OS = null;
		try {
			r = Runtime.getRuntime();
			OS = System.getProperty("os.name").toLowerCase();
			if (OS.indexOf("windows 9") > -1) {
				p = r.exec("command.com /c set");
			} else if (
				(OS.indexOf("nt") > -1) || (OS.indexOf("windows 2000") > -1)) {
				p = r.exec("cmd.exe /c set");
			} else {
				p = r.exec("env");
			}
			p = null;
			r = null;
			OS = null;
			return p;
		} catch (IOException e) {
			p = null;
			r = null;
			OS = null;
			throw new InvocationTargetException(e);
		}
	}
	/**
	 * @return String with path to the foundation.jar
	 */
	public static String foundationJarPath() {
		if (foundationJarPath == null) {

			if (Environment.isNextRootSet()) {
				foundationJarPath =
					"file:///"
						+ Environment.getEnvVars().getProperty(
							Environment.NEXT_ROOT)
						+ "/Library/Frameworks/JavaFoundation.framework/Resources/Java/javafoundation.jar";
				/*
				foundationJarPath =
					"file:///"
						+ FileStringScanner.replace(
							WOVariables.nextRoot(),
							"/",
							"\\")
						+ "\\Library\\Frameworks\\JavaFoundation.framework\\Resources\\Java\\javafoundation.jar";
						*/
			} else {
				foundationJarPath =
					"file:///System/Library/Frameworks/JavaFoundation.framework/Resources/Java/javafoundation.jar";
			}
		}
		return foundationJarPath;
	}
	
	public static String userHome() {
		if (System.getProperty("user.home") != null) {
			return System.getProperty("user.home");
		} else if (
			Environment.getEnvVars().getProperty("USERPROFILE") != null) {
			return Environment.getEnvVars().getProperty("USERPROFILE");
		} else {
			log.warn("userHome -> no user home found");
			return null;
		}
	}
	/**
	 * @return Returns the NEXT_ROOT
	 */
	/* mn: moved to WOVariables
	public static String nextRoot() {
		if (Environment.nextRoot != null)
			return Environment.nextRoot;
		Properties aEnv = null;
		try {
			aEnv = Environment.getEnvVars();
			if (aEnv.containsKey(Environment.NEXT_ROOT)) {
				Environment.nextRoot = aEnv.getProperty(Environment.NEXT_ROOT);
				return Environment.nextRoot;
			}
		} catch (Exception anException) {
			//WOLipsLog.log(anException);
		} finally {
			aEnv = null;
		}
		Environment.nextRoot = "/System";
		return Environment.nextRoot;
	}
	/
	/**
	 * Method localRoot.
	 * @return String
	 */
	/* mn: moved to WOVariables
	public static String localRoot() {
		if (Environment.localRoot == null) {
			if (isNextRootSet()) {
				// windows
				Environment.localRoot = nextRoot() + File.separator + "Local";
			} else {
				// mac
				Environment.localRoot = "";
			}
		}
		return Environment.localRoot;
	}
	*/
	/**
	 * Method isNextRootSet.
	 * @return boolean
	 */
	public static boolean isNextRootSet() {
		return Environment.getEnvVars().containsKey(Environment.NEXT_ROOT);
	}
}
