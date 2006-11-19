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
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * This class contains some utility methods that retrieves information about the
 * installed WebObjects.
 * 
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @since 2.0
 */
public class WebobjectsUtils
{
	/**
	 * Avoid <code>WebobjectsUtils</code> instantiation.
	 */
	WebobjectsUtils()
	{
	}

	/**
	 * Search for WebObjects JARs into the WebObjects lib folder and returns an
	 * array of files. It uses a <code>WebobjectsLocator</code> to find the
	 * WebObjects lib folder.
	 * 
	 * @param locator The WebObjects locator
	 * @return Returns an array of WebObjcts JARs or <code>null</code> if
	 *         cannot find the libs folder
	 */
	public static File[] getWebobjectsJars( WebobjectsLocator locator )
	{
		if( locator == null )
		{
			return null;
		}

		File folder = locator.getWebobjectsLibFolder();

		if( folder == null )
		{
			return null;
		}

		Collection jars = FileUtils.listFiles( folder, new String[] { "jar" }, false );

		File files[] = new File[jars.size()];

		return (File[]) jars.toArray( files );
	}

	/**
	 * Retrieves the version of installed WebObjects. It uses a
	 * <code>WebobjectsLocator</code> to find the WebObjects version file.
	 * 
	 * @param locator The WebObjects locator
	 * @return Returns the WebObjects version or <code>null</code> if cannot
	 *         discover the WebObjects version
	 */
	public static String getWebobjectsVersion( WebobjectsLocator locator )
	{
		if( locator == null )
		{
			return null;
		}

		File versionFile = locator.getWebobjectsVersionFile();

		if( versionFile == null || !versionFile.exists())
		{
			return null;
		}

		String version = null;

		LineIterator iterator = null;

		try
		{
			iterator = FileUtils.lineIterator( versionFile, null );

			while( iterator.hasNext() )
			{
				String line = iterator.nextLine();

				if( "<key>CFBundleShortVersionString</key>".equals( line.trim() ) )
				{
					String versionLine = iterator.nextLine();

					version = versionLine.trim().replaceAll( "</?string>", "" );

					break;
				}
			}

		}
		catch( IOException exception )
		{
			// TODO: hprange, write an info to log instead
			exception.printStackTrace();
		}
		finally
		{
			if( iterator != null )
			{
				LineIterator.closeQuietly( iterator );
			}
		}

		return version;
	}
}
