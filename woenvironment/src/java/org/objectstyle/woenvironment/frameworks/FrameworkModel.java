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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FrameworkModel<T extends IFramework> {
	private List<Root<T>> roots;

	protected abstract List<Root<T>> createRoots();
	
	public synchronized void invalidateRoots() {
		this.roots = null;
	}
	
	public synchronized List<Root<T>> getRoots() {
		if (this.roots == null) {
			this.roots = createRoots();
		}
		return this.roots;
	}

	public synchronized Set<T> getAllFrameworks() {
		Map<String, T> frameworks = new HashMap<String, T>();
		for (Root<T> root : getRoots()) {
			for (T framework : root.getFrameworks()) {
				String frameworkName = framework.getName();
				if (!frameworks.containsKey(frameworkName)) {
					frameworks.put(frameworkName, framework);
				}
			}
		}
		return new HashSet<T>(frameworks.values());
	}

	public synchronized Set<T> getAllApplications() {
		Map<String, T> applications = new HashMap<String, T>();
		for (Root<T> root : getRoots()) {
			for (T application : root.getApplications()) {
				String frameworkName = application.getName();
				if (!applications.containsKey(frameworkName)) {
					applications.put(frameworkName, application);
				}
			}
		}
		return new HashSet<T>(applications.values());
	}

	public synchronized void refreshRoots() {
		this.roots = null;
		getRoots();
	}

	public T getFrameworkWithName(String frameworkName) {
		for (Root<T> root : getRoots()) {
			T framework = root.getFrameworkWithName(frameworkName);
			if (framework != null) {
				return framework;
			}
		}
		return null;
	}

	public T getApplicationWithName(String applicationName) {
		for (Root<T> root : getRoots()) {
			T application = root.getApplicationWithName(applicationName);
			if (application != null) {
				return application;
			}
		}
		return null;
	}

  public Root<T> getRootWithShortName(String shortName) {
    for (Root<T> root : getRoots()) {
      if (shortName.equals(root.getShortName())) {
        return root;
      }
    }
    return null;
  }
}