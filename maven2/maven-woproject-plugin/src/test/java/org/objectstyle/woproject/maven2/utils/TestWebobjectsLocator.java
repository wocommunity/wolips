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
package org.objectstyle.woproject.maven2.utils;

import junit.framework.TestCase;

import org.apache.commons.lang.SystemUtils;

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class TestWebobjectsLocator extends TestCase
{
	protected WebobjectsLocator locator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		if( SystemUtils.IS_OS_WINDOWS )
		{
			locator = new WindowsWebobjectsLocator();
		}

		if( SystemUtils.IS_OS_MAC_OSX )
		{
			locator = new MacOsWebobjectsLocator();
		}
	}

	/**
	 * I know it's not good, but if you don't have WebObjects installed, forget.
	 * This test will fail.
	 * 
	 * @throws Exception
	 */
	public void testCheckWebObjectsVersionFile() throws Exception
	{
		assertNotNull( locator.getWebobjectsVersionFile() );
	}

	public void testFindWebObjectsRootFolder() throws Exception
	{
		assertNotNull( locator.getWebobjectsRootFolder() );
	}

	/**
	 * I know it's not good, but if you don't have WebObjects installed, forget.
	 * This test will fail.
	 * 
	 * @throws Exception
	 */
	public void testWebObjectsLibsFolder() throws Exception
	{
		assertNotNull( locator.getWebobjectsLibFolder() );
	}

	public void testWebObjectsRootFolderCannotBeFoundOnWindows() throws Exception
	{
		if( SystemUtils.IS_OS_WINDOWS )
		{
			locator = new WindowsWebobjectsLocator( "an_inexistent_evnvar_for_test" );

			assertNull( locator.getWebobjectsRootFolder() );
			assertNull( locator.getWebobjectsLibFolder() );
			assertNull( locator.getWebobjectsVersionFile() );
		}
	}
}
