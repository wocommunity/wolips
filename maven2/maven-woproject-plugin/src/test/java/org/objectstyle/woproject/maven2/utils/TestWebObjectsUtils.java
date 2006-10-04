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

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class TestWebObjectsUtils extends TestCase
{
	protected WebobjectsLocator mockLocator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		mockLocator = (WebobjectsLocator) EasyMock.createMock( WebobjectsLocator.class );
	}

	public void testCheckWebObjectsVersion() throws Exception
	{
		File versionFile = FileUtils.toFile( getClass().getResource( "/version.plist" ) );

		EasyMock.expect( mockLocator.getWebobjectsVersionFile() ).andReturn( versionFile );

		EasyMock.replay( new Object[] { mockLocator } );

		assertEquals( "5.2.4", WebobjectsUtils.getWebobjectsVersion( mockLocator ) );
	}

	public void testFileCannotBeFound() throws Exception
	{
		File versionFile = new File( FileUtils.toFile( getClass().getResource( "/" ) ), "inexistent.txt" );

		EasyMock.expect( mockLocator.getWebobjectsVersionFile() ).andReturn( versionFile );

		EasyMock.replay( new Object[] { mockLocator } );

		assertNull( WebobjectsUtils.getWebobjectsVersion( mockLocator ) );
	}

	public void testNullVersionFile() throws Exception
	{
		EasyMock.expect( mockLocator.getWebobjectsVersionFile() ).andReturn( null );

		EasyMock.replay( new Object[] { mockLocator } );

		assertNull( WebobjectsUtils.getWebobjectsVersion( mockLocator ) );
	}

	public void testNullWebObjectsLocator() throws Exception
	{
		assertNull( WebobjectsUtils.getWebobjectsVersion( null ) );
	}

	public void testObtainWebObjectsLibs() throws Exception
	{
		File jarsFolder = FileUtils.toFile( getClass().getResource( "/mock-jar1.jar" ) ).getParentFile();

		EasyMock.expect( mockLocator.getWebobjectsLibFolder() ).andReturn( jarsFolder );

		EasyMock.replay( new Object[] { mockLocator } );

		File[] foundLibs = WebobjectsUtils.getWebobjectsJars( mockLocator );

		assertEquals( 2, foundLibs.length );
	}

	public void testWebObjectsJarsWithNullLocator() throws Exception
	{
		assertNull( WebobjectsUtils.getWebobjectsJars( null ) );
	}

	public void testWebObjectsJarsWithNullLibFolder() throws Exception
	{
		EasyMock.expect( mockLocator.getWebobjectsLibFolder() ).andReturn( null );

		EasyMock.replay( new Object[] { mockLocator } );

		assertNull( WebobjectsUtils.getWebobjectsJars( mockLocator ) );
	}
}
