/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2006 The ObjectStyle Group 
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
package org.objectstyle.woproject.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides stream copy utilities.
 * 
 * @author andrus
 */
class FileUtil {

	static void ensureParentDirExists(File file) {
		File parent = file.getParentFile();
		if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
			throw new BuildException("Failed to create directory " + parent.getAbsolutePath());
		}
	}

	/**
	 * Performs a binary copy of a ClassLoader resource to a file.
	 */
	static void copy(String fromResource, File to) throws BuildException {

		int bufferSize = 8 * 1024;

		ensureParentDirExists(to);

		InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(fromResource);

		if (in == null) {
			throw new BuildException("Resource not found: " + fromResource);
		}

		try {
			in = new BufferedInputStream(in, bufferSize);

			byte[] buffer = new byte[bufferSize];
			int read;
			OutputStream out = new BufferedOutputStream(new FileOutputStream(to), bufferSize);

			try {

				while ((read = in.read(buffer, 0, bufferSize)) >= 0) {
					out.write(buffer, 0, read);
				}

				out.flush();

			} finally {
				try {
					out.close();
				} catch (IOException ioex) {
					// ignore
				}
			}

		} catch (IOException e) {
			throw new BuildException("Error copying resource " + fromResource, e);
		} finally {
			try {
				in.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}
	
	public static List<Node> getClasspathEntriesOfKind(File baseDir, String desiredKind) throws SAXException, IOException, ParserConfigurationException {
	  List<Node> classpathEntryNodes = new LinkedList<Node>();
    File classpathFile = new File(baseDir, ".classpath");
    if (!classpathFile.exists()) {
      throw new BuildException("You specified eclipse = 'true', but " + classpathFile + " does not exist.");
    }
    Document classpathDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(classpathFile);
    classpathDocument.normalize();
    NodeList classpathentries = classpathDocument.getElementsByTagName("classpathentry");
    for (int classpathentryNum = 0; classpathentryNum < classpathentries.getLength(); classpathentryNum++) {
      Node classpathentry = classpathentries.item(classpathentryNum);
      NamedNodeMap attributes = classpathentry.getAttributes();
      Node kindAttribute = attributes.getNamedItem("kind");
      String kind = kindAttribute.getTextContent();
      if (desiredKind.equals(kind)) {
        classpathEntryNodes.add(classpathentry);
      }
    }
    return classpathEntryNodes;
	}
}
