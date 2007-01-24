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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * extractWebServerResources goal for WebObjects jars. Copy all
 * webserverresources fram a jar into a destination folder.
 * 
 * @goal extractWebServerResources
 * @author uli
 * @since 2.0
 */
public class WebServerResourcesFromJarExtractorMojo extends AbstractMojo {

	/**
	 * @parameter
	 * @required
	 * @readonly
	 */
	private String jarFileName;

	/**
	 * @parameter
	 * @required
	 * @readonly
	 */
	private String destinationFolderName;

	public WebServerResourcesFromJarExtractorMojo() {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Extracting webserverresources");
		if (jarFileName == null) {
			throw new MojoExecutionException("jarFileName is null");
		}
		if (destinationFolderName == null) {
			throw new MojoExecutionException("destinationFolderName is null");
		}
		FileInputStream fileInputStream;
		try {
			getLog().debug("Extract webserverresources: looking into jar named " + jarFileName);
			fileInputStream = new FileInputStream(jarFileName);
			JarInputStream jarInputStream = new JarInputStream(fileInputStream);
			int counter = 0;
			JarEntry jarEntry = null;
			while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
				if (!jarEntry.isDirectory()) {
					String jarEntryName = jarEntry.getName();
					String prefix = "WebServerResources";
					if (jarEntryName != null && jarEntryName.length() > prefix.length() && jarEntryName.startsWith(prefix)) {
						this.copyJarEntryToFile(jarEntry);
						counter++;
					}
				}
			}
			getLog().debug("Extract webserverresources: extracted " + counter + " webserverresources from  jar named " + jarFileName);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Could not open file input stream", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not open jar input stream", e);
		}
	}

	private void copyJarEntryToFile(JarEntry jarEntry) throws IOException, FileNotFoundException {
		File destinationFolder = new File(destinationFolderName);
		// getLog().info("Copy webserverresources: jarFileName " + jarFileName +
		// " destinationFolder " + destinationFolder);
		String name = jarEntry.getName();
		String destinationFolderWithPathFromJarEntry = destinationFolder + File.separator + name.substring(0, name.lastIndexOf('/'));
		String destinationName = name.substring(name.lastIndexOf('/') + 1);
		// getLog().info("Copy webserverresources:
		// destinationFolderWithPathFromJarEntry " +
		// destinationFolderWithPathFromJarEntry + " destinationName " +
		// destinationName);
		File file = new File(destinationFolderWithPathFromJarEntry, destinationName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		// getLog().info("Copy webserverresources: file " + file);
		file.createNewFile();

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new JarFile(jarFileName).getInputStream(jarEntry);
			outputStream = new FileOutputStream(file);

			byte[] buf = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}

			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
}
