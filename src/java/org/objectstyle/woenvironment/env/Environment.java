/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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
package org.objectstyle.woenvironment.env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author uli Utility for the environment.
 */
public class Environment {
	/**
	 * The String NEXT_ROOT.
	 */
	private final String NEXT_ROOT = "NEXT_ROOT";

	private final String NEXT_LOCAL_ROOT = "NEXT_LOCAL_ROOT";

	private final String NEXT_SYSTEM_ROOT = "NEXT_SYSTEM_ROOT";

	private Properties envVars;

	/**
	 * Constructor for Environment.
	 */
	protected Environment() {
		super();
	}

	/**
	 * The values are cached.
	 * 
	 * @return environment variables as Properties.
	 * @throws Exception
	 */
	public Properties getEnvVars() {
		if (envVars != null)
			return envVars;
		Process process = null;
		BufferedReader br = null;
		String line = null;
		try {
			process = Environment.osProcess();
		} catch (InvocationTargetException e) {
			System.out.println("getEnvVars -> unable to load environment variables" + e);
		}
		envVars = new Properties();
		if (process == null) {
			return envVars;
		}
		br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		try {
			while ((line = br.readLine()) != null) {
				int idx = line.indexOf('=');
				
				if( idx < 0)
				{
					continue;
				}
				
				String key = line.substring(0, idx);
				String value = line.substring(idx + 1);
				envVars.setProperty(key, value);
			}
		} catch (IOException e) {
			System.out.println("getEnvVars -> unable to load environment variables" + e);
		}
		process.destroy();
		return envVars;
	}

	/**
	 * Method osProcess.
	 * 
	 * @return Process
	 * @throws Exception
	 */
	 static Process osProcess() throws InvocationTargetException {
		Process p = null;
		Runtime r = null;
		String OS = null;
		try {
			r = Runtime.getRuntime();
			OS = System.getProperty("os.name").toLowerCase();
			if (OS.indexOf("windows 9") > -1) {
				p = r.exec("command.com /c set");
			} else if ((OS.indexOf("nt") > -1) || (OS.indexOf("windows 200") > -1)) {
				p = r.exec("cmd.exe /c set");
			} else {
				p = r.exec("env");
			}
			return p;
		} catch (IOException e) {
			throw new InvocationTargetException(e);
		}
	}

	public String userHome() {
		if (System.getProperty("user.home") != null) {
			return System.getProperty("user.home");
		} else if (this.getEnvVars().getProperty("USERPROFILE") != null) {
			return this.getEnvVars().getProperty("USERPROFILE");
		} else {
			System.out.println("userHome -> no user home found");
			return null;
		}
	}

	/**
	 * @return
	 */
	public String getNEXT_LOCAL_ROOT() {
		return NEXT_LOCAL_ROOT;
	}

	/**
	 * @return
	 */
	public String getNEXT_ROOT() {
		return NEXT_ROOT;
	}

	/**
	 * @return
	 */
	public String getNEXT_SYSTEM_ROOT() {
		return NEXT_SYSTEM_ROOT;
	}

}
