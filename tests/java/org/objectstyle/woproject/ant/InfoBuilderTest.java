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
package org.objectstyle.woproject.ant;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

/** 
 * Test cases for InfoBuilder class.
 * 
 * @author Emily Bache, Andrei Adamchik
 */
public class InfoBuilderTest extends TestCase {

	private final static String NS_JAVA_PATH_BEGIN = "NSJavaPath</key>";
	private final static String NS_JAVA_PATH_END =
		"<key>CFBundleInfoDictionaryVersion";

	protected FileReader fin;
	protected StringWriter sout;
	protected BufferedWriter bout;
	protected BufferedReader bin;
	protected TestTask parentTask;

	public InfoBuilderTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		fin = new FileReader("src/resources/woframework/Info.plist");
		bin = new BufferedReader(fin);
		sout = new StringWriter();
		bout = new BufferedWriter(sout);
		parentTask = new TestTask();
		parentTask.setName(this.getName());
	}

	public void tearDown() throws Exception {
		bout.close();
		bin.close();
		fin.close();
		sout.close();
		super.tearDown();
	}

	public void testNoLibs() throws Exception {
		parentTask.setHasClasses(true);
		InfoBuilder infoBuilder = new InfoBuilder(parentTask, new Vector());
		infoBuilder.writeText(bin, bout);

		String output = sout.toString();
		assertTrue(output.length() > 0);
		String nsJavaPath =
			output.substring(
				output.indexOf(NS_JAVA_PATH_BEGIN)
					+ NS_JAVA_PATH_BEGIN.length(),
				output.indexOf(NS_JAVA_PATH_END));
		assertEquals(
			"\n\t<array>\n"
				+ "\t\t<string>"
				+ getName().toLowerCase()
				+ ".jar</string>\n"
				+ "\t</array>\n\t",
			nsJavaPath);
	}

	public void testLibsAndClasses() throws Exception {
		parentTask.setHasClasses(true);
		String lib = "jar1.jar";
		Vector libs = new Vector();
		libs.add(lib);
		InfoBuilder infoBuilder = new InfoBuilder(parentTask, libs);
		infoBuilder.writeText(bin, bout);

		String output = sout.toString();
		String nsJavaPath =
			output.substring(
				output.indexOf(NS_JAVA_PATH_BEGIN)
					+ NS_JAVA_PATH_BEGIN.length(),
				output.indexOf(NS_JAVA_PATH_END));
		assertEquals(
			"\n\t<array>\n"
				+ "\t\t<string>"
				+ getName().toLowerCase()
				+ ".jar</string>\n"
				+ "\t\t<string>"
				+ lib
				+ "</string>\n"
				+ "\t</array>\n\t",
			nsJavaPath);

	}

	public void testJustLibs() throws Exception {
		parentTask.setHasClasses(false);
		String lib1 = "jar1.jar";
		String lib2 = "jar2.zip";
		String lib3 = "jar3.jar";
		Vector libs = new Vector();
		libs.add(lib1);
		libs.add(lib2);
		libs.add(lib3);
		InfoBuilder infoBuilder = new InfoBuilder(parentTask, libs);
		infoBuilder.writeText(bin, bout);

		String output = sout.toString();
		String nsJavaPath =
			output.substring(
				output.indexOf(NS_JAVA_PATH_BEGIN)
					+ NS_JAVA_PATH_BEGIN.length(),
				output.indexOf(NS_JAVA_PATH_END));
		assertEquals(
			"\n\t<array>\n"
				+ "\t\t<string>"
				+ lib1
				+ "</string>\n"
				+ "\t\t<string>"
				+ lib2
				+ "</string>\n"
				+ "\t\t<string>"
				+ lib3
				+ "</string>\n"
				+ "\t</array>\n\t",
			nsJavaPath);
	}

	static class TestTask extends WOFramework {
		protected boolean hasClasses;

		public boolean hasClasses() {
			return hasClasses;
		}

		public void setHasClasses(boolean flag) {
			hasClasses = flag;
		}
	};

}