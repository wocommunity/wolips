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
package org.objectstyle.woproject.maven2.wobootstrap;

import java.io.File;
import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.ReflectionUtils;

/**
 * @author uli
 */
public class TestWebServerResourcesFromJarExtractorMojo extends TestCase {

	public void testParameter() throws Exception {
		try {
			WebServerResourcesFromJarExtractorMojo mojo = new WebServerResourcesFromJarExtractorMojo();

			mojo.execute();

			fail("properties not set, must throw an exception");
		} catch (MojoExecutionException exception) {
			// do nothing
		} catch (MojoFailureException e) {
			// do nothing
		}
	}

	protected void setVariableValueToObject(Object object, String variable, Object value) throws IllegalAccessException {
		Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(variable, object.getClass());

		field.setAccessible(true);

		field.set(object, value);
	}

	public void testExcecute() throws Exception {
		File resourcesPath = FileUtils.toFile(getClass().getResource("/webserverresources/"));

		String filePath = "mock-webserverresources1.jar";

		File mockJar = new File(resourcesPath, filePath);

		File file = File.createTempFile("bla", "foo");
		File tempDir = file.getParentFile();
		String destination = tempDir.getAbsolutePath() + File.separator + "TestWebServerResourcesFromJarExtractorMojo" + "-" + System.currentTimeMillis();
		File testWebServerResourcesFromJarExtractorMojoDirectory = new File(destination);
		testWebServerResourcesFromJarExtractorMojoDirectory.deleteOnExit();
		testWebServerResourcesFromJarExtractorMojoDirectory.mkdir();

		assertEquals(0, testWebServerResourcesFromJarExtractorMojoDirectory.list().length);

		WebServerResourcesFromJarExtractorMojo mojo = new WebServerResourcesFromJarExtractorMojo();

		this.setVariableValueToObject(mojo, "jarFileName", mockJar.getAbsolutePath());
		this.setVariableValueToObject(mojo, "destinationFolderName", destination);

		mojo.execute();

		assertEquals(0, testWebServerResourcesFromJarExtractorMojoDirectory.list().length);

		filePath = "mock-webserverresources2.jar";

		mockJar = new File(resourcesPath, filePath);

		mojo = new WebServerResourcesFromJarExtractorMojo();

		this.setVariableValueToObject(mojo, "jarFileName", mockJar.getAbsolutePath());
		this.setVariableValueToObject(mojo, "destinationFolderName", destination);

		mojo.execute();

		assertEquals(1, testWebServerResourcesFromJarExtractorMojoDirectory.list().length);
		assertEquals(1, testWebServerResourcesFromJarExtractorMojoDirectory.listFiles()[0].list().length);
		assertEquals(3, testWebServerResourcesFromJarExtractorMojoDirectory.listFiles()[0].listFiles()[0].list().length);

	}
}
