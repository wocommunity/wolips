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
package org.objectstyle.woenvironment.pb;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.objectstyle.woenvironment.pb.PBProject;

/**
 * PBProject JUnit tests.
 * 
 * @author Andrei Adamchik
 */
public class PBProjectTest extends TestCase {
	protected PBProject proj;

	/**
	 * Constructor for PBProjectTest.
	 * @param arg0
	 */
	public PBProjectTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		proj = new PBProject(new File("PB.project"), false);
	}

	public void testProjectName() throws Exception {
		proj.setProjectName("abc");
		assertEquals("abc", proj.getProjectName());
	}
	
    public void testDefaultProjectFile() throws Exception {
    	assertNotNull(proj.getProjectFile());
		assertEquals("PB.project", proj.getProjectFile().toString());
	}


	public void testProjectVersion() throws Exception {
		proj.setProjectVersion("abc");
		assertEquals("abc", proj.getProjectVersion());
	}

	public void testProjectFile() throws Exception {
		File f = new File("PB.project.test");
		proj.setProjectFile(f);
		assertSame(f, proj.getProjectFile());
	}

	public void testSaveChanges() throws Exception {
		File f = new File("PB.project.testsave");
		assertTrue(!f.exists());

		try {
			proj.setProjectFile(f);
			proj.saveChanges();
			assertTrue(f.exists());
		} finally {
			if (f.exists()) {
				f.delete();
			}
		}
	}
	
	public void testClasses() throws Exception {
		ArrayList classes = new ArrayList();
		classes.add("abc");
		proj.setClasses(classes);
		assertEquals(classes, proj.getFilesTable().get(PBProject.CLASSES));
	}
}
