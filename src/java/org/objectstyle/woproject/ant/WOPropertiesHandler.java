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
package org.objectstyle.woproject.ant;

import java.util.*;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Execute;

/**
 * Handles properties specific to WOProject tasks,
 * initializing them with defaults if needed.
 *
 * @author Andrei Adamchik
 */
public class WOPropertiesHandler extends ProjectComponent {
	public static final String WO_ROOT = "wo.woroot";
	public static final String LOCAL_ROOT = "wo.localroot";
	public static final String HOME_ROOT = "wo.homeroot";
	public static final String ABSOLUTE_ROOT = "wo.absoluteroot";

	static Properties env;
	static final Object envLock = new Object();

	protected String woRoot;
	protected String localRoot;
	protected String homeRoot;

	/** 
	 * Creates new WOPropertiesHandler and 
	 * sets its parent Project.
	 */
	public WOPropertiesHandler(Project project) {
		super.setProject(project);
	}

	/** 
	 * Returns the value of "wo.woroot" property. Search algorithm is the following:
	 * <ul>
	 *    <li>Project <code>wo.woroot</code> property value.</li>
	 *    <li>NEXT_ROOT environment variable.</li>
	 *    <li>"/" directory.</li>
	 * </ul>
	 */
	public String getWORoot() {
		if (woRoot == null) {
			woRoot = getProject().getProperty(WO_ROOT);

			if (woRoot == null) {
				woRoot = getEnvironmentProperty("NEXT_ROOT");

				if (woRoot == null) {
					woRoot = "/";
				}
			}
		}

		return woRoot;
	}

	/** 
	 * Returns the value of "wo.localroot" property. Search algorithm is the following:
	 * <ul>
	 *    <li>Project <code>wo.localroot</code> property value.</li>
	 *    <li><code>${wo.woroot}/Local</code>.</li>
	 * </ul>
	 */
	public String getLocalRoot() {
		if (localRoot == null) {
			localRoot = getProject().getProperty(LOCAL_ROOT);

			if (localRoot == null) {
				localRoot = getWORoot() + "/Local";
			}
		}

		return localRoot;
	}

	/** 
	 * Returns the value of "wo.homeroot" property. Search algorithm is the following:
	 * <ul>
	 *    <li>Project <code>wo.homeroot</code> property value.</li>
	 *    <li><code>user.home</code> system property.</li>
	 * </ul>
	 */
	public String getHomeRoot() {
		if (homeRoot == null) {
			homeRoot = getProject().getProperty(HOME_ROOT);

			if (homeRoot == null) {
				homeRoot = System.getProperty("user.home");
			}
		}

		return homeRoot;
	}

	/** 
	 * Returns a value of environment variable. The environment
	 * will be loaded from WOPropertyHandler only once during a 
	 * project lifecycle. All environment variables will be cached 
	 * in a project under "woenv.*" keys. 
	 */
	public String getEnvironmentProperty(String name) {
		synchronized (envLock) {
			if (env == null) {
				loadEnvironment();
			}
		}
		return env.getProperty(name);
	}

	/** 
	 * Reads environment properties, initializes internal
	 * collection using "woenv.*" prefix.
	 */
	private void loadEnvironment() {
		Properties props = new Properties();

		log("Loading Environment", Project.MSG_VERBOSE);
		Vector osEnv = Execute.getProcEnvironment();
		for (Enumeration e = osEnv.elements(); e.hasMoreElements();) {
			String entry = (String) e.nextElement();
			int pos = entry.indexOf('=');
			if (pos == -1) {
				log("Ignoring: " + entry, Project.MSG_WARN);
			} else {
				props.put(entry.substring(0, pos), entry.substring(pos + 1));
			}
		}
		env = props;
	}
}