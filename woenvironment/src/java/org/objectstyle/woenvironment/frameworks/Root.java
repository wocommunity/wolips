/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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
package org.objectstyle.woenvironment.frameworks;

import java.util.Set;

public abstract class Root<T extends IFramework> {
	public static final String PROJECT_ROOT = "Project";
	public static final String PROJECT_LOCAL_ROOT = "ProjectLocal";
	public static final String EXTERNAL_ROOT = "External";
	public static final String USER_ROOT = "User";
	public static final String NETWORK_ROOT = "Network";
	public static final String LOCAL_ROOT = "Local";
	public static final String SYSTEM_ROOT = "System";

	private String shortName;
	private String name;
	private long creationDate;

	public Root(String shortName, String name) {
		this.shortName = shortName;
		this.name = name;
		this.creationDate = System.currentTimeMillis();
	}
	
	public boolean shouldReload() {
		return System.currentTimeMillis() - this.creationDate > 30000;
	}

	public abstract Set<T> getFrameworks();

	public T getFrameworkWithName(String frameworkName) {
		Set<T> frameworks = getFrameworks();
		for (T framework : frameworks) {
			if (framework.getName().equals(frameworkName)) {
				return framework;
			}
		}
		return null;
	}

	public abstract Set<T> getApplications();

	public T getApplicationWithName(String applicationName) {
		Set<T> applications = getApplications();
		for (T application : applications) {
			if (application.getName().equals(applicationName)) {
				return application;
			}
		}
		return null;
	}

	public String getShortName() {
		return shortName;
	}

	public String getName() {
		return this.name;
	}
}
