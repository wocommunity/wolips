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

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IBuilderAccessor {
	public static final int BuilderNotFound = -1;
		
	/**
	 * Installs the target builder.
	 * @throws CoreException
	 */
	public abstract void installTargetBuilder(int position) throws CoreException;
	/**
	 * Removes the target builder.
	 * @return postion of TargetBuilder. If not found IBuilderAccessor.BuilderNotFoundwill be returned.
	 * @throws CoreException
	 */
	public abstract int removeTargetBuilder() throws CoreException;
	/**
	 * Installs the ant builder.
	 * @throws CoreException
	 */
	public abstract void installAntBuilder() throws CoreException;
	/**
	 * Removes the ant builder.
	 * @throws CoreException
	 */
	public abstract void removeAntBuilder() throws CoreException;
	/**
	 * Installs the incremetal builder.
	 * @throws CoreException
	 */
	public abstract void installIncrementalBuilder() throws CoreException;
	/**
	 * Removes the incremental builder.
	 * @throws CoreException
	 */
	public abstract void removeIncrementalBuilder() throws CoreException;
	/**
	 * Installs the java builder.
	 * @throws CoreException
	 */
	public abstract void installJavaBuilder() throws CoreException;
	/**
	 * Installs the java builder.
	 * @throws CoreException
	 */
	public abstract void installJavaBuilder(int position) throws CoreException;
	/**
	 * Removes the incremental builder.
	 * @return postion of JavaBuilder if not found IBuilderAccessor.BuilderNotFoundwill be returned.
	 * @throws CoreException
	 */
	public abstract int removeJavaBuilder() throws CoreException;
	/**
	 * Return true if the target builder is installed.
	 */
	public abstract boolean isTargetBuilderInstalled();
	/**
	 * Return true if the ant builder is installed.
	 */
	public abstract boolean isAntBuilderInstalled();
	/**
	 * Return true if the incremental builder is installed.
	 */
	public abstract boolean isIncrementalBuilderInstalled();
	/**
	 * Return true if the java builder is installed.
	 */
	public abstract boolean isJavaBuilderInstalled();

	/**
	 * @return the with the wolips builder args
	 */
	public abstract Map getBuilderArgs();
}
