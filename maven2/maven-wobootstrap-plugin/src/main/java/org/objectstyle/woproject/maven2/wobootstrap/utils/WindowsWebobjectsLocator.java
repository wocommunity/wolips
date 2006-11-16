/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group,
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
package org.objectstyle.woproject.maven2.wobootstrap.utils;

import java.io.File;

/**
 * This class is an implementation of {@link WebobjectsLocator} specific for
 * Windows.
 * 
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @since 2.0
 */
public class WindowsWebobjectsLocator extends AbstractWebobjectsLocator
{
	/**
	 * The deafult WebObjects root variable on Windows
	 * @deprecated New versions of JDK throw Exceptions about getenv not more supported. Please use
	 * the <code>next.root</code> property instead
	 */
	protected static final String DEFAULT_WO_ROOT_VARIABLE = "NEXT_ROOT";

	/**
	 * The deafult WebObjects root property on Windows
	 */
	protected static final String DEFAULT_WO_ROOT_PROPERTY = "next.root";

	/**
	 * The name of the environment that contains the path for WebObjects folder
	 */
	protected final String woRootVariable;

	/**
	 * Creates a new <code>WindowsWebobjectsLocator</code> using the default
	 * WebObjects root variable.
	 */
	public WindowsWebobjectsLocator()
	{
		woRootVariable = System.getProperty(DEFAULT_WO_ROOT_PROPERTY);
	}

	/**
	 * Creates a new <code>WindowsWebobjectsLocator</code> using the variable
	 * passed by parameter.
	 * 
	 * @param woRootVariable The WebObjects root variable
	 */
	public WindowsWebobjectsLocator( String woRootVariable )
	{
		this.woRootVariable = woRootVariable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectstyle.woproject.maven2.utils.WebobjectsLocator#webobjectsRootDirectory()
	 */
	public File getWebobjectsRootFolder()
	{
		String nextRoot = woRootVariable;

		if( nextRoot == null )
		{
			return null;
		}

		return new File( nextRoot );
	}

}
