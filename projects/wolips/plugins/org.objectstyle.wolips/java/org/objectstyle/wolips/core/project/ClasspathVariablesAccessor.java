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

package org.objectstyle.wolips.core.project;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class ClasspathVariablesAccessor
	implements IClasspathVariablesAccessor {

	private final String[] classpathVariables =
		new String[] {
			IClasspathVariablesAccessor.UserHomeClasspathVariable,
			"NEXT_LOCAL_ROOT",
			"NEXT_SYSTEM_ROOT",
			IClasspathVariablesAccessor.ProjectWonderHomeClasspathVariable };
	private final String[] classpathVariablesNames =
		new String[] { "User Home", "Local", "System", "Project Wonder" };

	protected ClasspathVariablesAccessor() {
		super();
	}
	/**
	 * @return IPath from NextLocalRoot Classpath variable if it exists.
	 */
	public final IPath getNextLocalRootClassPathVariable() {
		return JavaCore.getClasspathVariable(
			this.getWOEnvironment().getNEXT_LOCAL_ROOT());
	}
	/**
	 * @return IPath from NextRoot Classpath variable if it exists.
	 */
	public final IPath getNextRootClassPathVariable() {
		return JavaCore.getClasspathVariable(
			this.getWOEnvironment().getNEXT_ROOT());
	}
	/**
	 * @return IPath from NextSystemRoot Classpath variable if it exists.
	 */
	public final IPath getNextSystemRootClassPathVariable() {
		return JavaCore.getClasspathVariable(
			this.getWOEnvironment().getNEXT_SYSTEM_ROOT());
	}
	/**
	 * @return IPath from UserHome Classpath variable if it exists.
	 */
	public final IPath getUserHomeClassPathVariable() {
		return JavaCore.getClasspathVariable(
			IClasspathVariablesAccessor.UserHomeClasspathVariable);
	}
	/**
	 * @return IPath from ProjectWonder Classpath variable if it exists.
	 */
	public final IPath getProjectWonderHomeClassPathVariable() {
		return JavaCore.getClasspathVariable(
			IClasspathVariablesAccessor.ProjectWonderHomeClasspathVariable);
	}

	/**
	 *set IPath for NextLocalRoot Classpath variable.
	 */
	public final void setNextLocalRootClassPathVariable(IPath path)
		throws JavaModelException {
		JavaCore.setClasspathVariable(
			this.getWOEnvironment().getNEXT_LOCAL_ROOT(),
			path,
			null);
	}
	/**
	 * set IPath for NextRoot Classpath variable.
	 */
	public final void setNextRootClassPathVariable(IPath path)
		throws JavaModelException {
		JavaCore.setClasspathVariable(
			this.getWOEnvironment().getNEXT_ROOT(),
			path,
			null);
	}
	/**
	 * set IPath for NextSystemRoot Classpath variable.
	 */
	public final void setNextSystemRootClassPathVariable(IPath path)
		throws JavaModelException {
		JavaCore.setClasspathVariable(
			this.getWOEnvironment().getNEXT_SYSTEM_ROOT(),
			path,
			null);
	}
	/**
	 * set IPath for UserHome Classpath variable.
	 */
	public final void setUserHomeClassPathVariable(IPath path)
		throws JavaModelException {
		JavaCore.setClasspathVariable(
			IClasspathVariablesAccessor.UserHomeClasspathVariable,
			path,
			null);
	}
	/**
	 * set IPath for UserHome Classpath variable.
	 */
	public final void setProjectWonderHomeClassPathVariable(IPath path)
		throws JavaModelException {
		JavaCore.setClasspathVariable(
			IClasspathVariablesAccessor.ProjectWonderHomeClasspathVariable,
			path,
			null);
	}

	/**
	 * @return WOEnvironment
	 */
	private final WOEnvironment getWOEnvironment() {
		return WOLipsPlugin.getDefault().getWOEnvironment();
	}

	/**
	 * @return Returns true if the named classpath variable is controlled by WOLips.
	 */
	public final boolean isUnderWOLipsControl(String classpathVariable) {
		if (classpathVariable == null)
			return false;
		return WOLipsPlugin
			.getDefault()
			.getWOEnvironment()
			.getNEXT_LOCAL_ROOT()
			.equals(
			classpathVariable)
			|| WOLipsPlugin
				.getDefault()
				.getWOEnvironment()
				.getNEXT_SYSTEM_ROOT()
				.equals(
				classpathVariable)
			|| WOLipsPlugin.getDefault().getWOEnvironment().getNEXT_ROOT().equals(
				classpathVariable)
			|| ClasspathVariablesAccessor.UserHomeClasspathVariable.equals(
				classpathVariable)
			|| ClasspathVariablesAccessor
				.ProjectWonderHomeClasspathVariable
				.equals(
				classpathVariable);
	}

	/**
	 * Method classPathVariableToExpand.
	 * @param aString
	 * @return String
	 */
	public String classPathVariableToExpand(String aString) {
		if (aString == null)
			return null;
		if (aString.equals("webobjects.next.root"))
			return WOLipsPlugin
				.getDefault()
				.getWOEnvironment()
				.getWOVariables()
				.systemRoot();
		if (aString.equals("webobjects.system.library.dir"))
			return WOLipsPlugin
				.getDefault()
				.getWOEnvironment()
				.getWOVariables()
				.libraryDir();
		WOLipsLog.log("Can not resolve classpath variable: " + aString);
		return null;
	}

	/**
	 * Method classpathVariables.
	 * The variables sorted by priority
	 * @return String[]
	 */
	public final String[] classpathVariables() {
		return classpathVariables;
	}

	/**
	 * @param string
	 * @return Returns the localized name of a given classpath variable. If the variable does not exist null is returned.
	 */
	public final String getclasspathVariableName(String string) {
		for (int i = 0; i < classpathVariables.length; i++) {
			if (string.equals(classpathVariables[i]))
				if (i < classpathVariablesNames.length)
					return classpathVariablesNames[i];
		}
		return null;
	}

}
