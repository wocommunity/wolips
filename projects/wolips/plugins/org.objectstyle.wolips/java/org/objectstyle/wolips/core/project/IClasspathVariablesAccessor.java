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
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IClasspathVariablesAccessor {

	public final static String UserHomeClasspathVariable = "USER_HOME";
	public final static String ProjectWonderHomeClasspathVariable =
		"PROJECT_WONDER_HOME";

	/**
	 * @return IPath from NextLocalRoot Classpath variable if it exists.
	 */
	public abstract IPath getNextLocalRootClassPathVariable();

	/**
	 * @return IPath from NextRoot Classpath variable if it exists.
	 */
	public abstract IPath getNextRootClassPathVariable();

	/**
	 * @return IPath from NextSystemRoot Classpath variable if it exists.
	 */
	public abstract IPath getNextSystemRootClassPathVariable();

	/**
	 * @return IPath from UserHome Classpath variable if it exists.
	 */
	public abstract IPath getUserHomeClassPathVariable();

	/**
	 * @return IPath from UserHome Classpath variable if it exists.
	 */
	public abstract IPath getProjectWonderHomeClassPathVariable();

	/**
	 *set IPath for NextLocalRoot Classpath variable.
	 */
	public abstract void setNextLocalRootClassPathVariable(IPath path)
		throws JavaModelException;

	/**
	 * set IPath for NextRoot Classpath variable.
	 */
	public abstract void setNextRootClassPathVariable(IPath path)
		throws JavaModelException;

	/**
	 * set IPath for NextSystemRoot Classpath variable.
	 */
	public abstract void setNextSystemRootClassPathVariable(IPath path)
		throws JavaModelException;

	/**
	 * set IPath for UserHome Classpath variable.
	 */
	public abstract void setUserHomeClassPathVariable(IPath path)
		throws JavaModelException;

	/**
	 * set IPath for UserHome Classpath variable.
	 */
	public abstract void setProjectWonderHomeClassPathVariable(IPath path)
		throws JavaModelException;

	/**
	 * @return Returns true if the named classpath variable is controlled by WOLips.
	 */
	public abstract boolean isUnderWOLipsControl(String classpathVariable);

	/**
	 * Method classPathVariableToExpand.
	 * @param aString
	 * @return String
	 */
	public abstract String classPathVariableToExpand(String aString);
	
	/**
	 * Method classpathVariables.
	 * The variables sorted by priority
	 * @return String[]
	 */
	public abstract String[] classpathVariables();

	/**
	 * @param string
	 * @return Returns the localized name of a given classpath variable. If the variable does not exist null is returned.
	 */
	public abstract String getclasspathVariableName(String string);
	
}