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

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.tools.ant.Project;

/**
 * Unit test for WOPropertiesHandler
 *
 * @author Andrei Adamchik
 */
public class WOPropertiesHandlerTest extends TestCase {
	protected WOPropertiesHandler handler;
	protected Project project;

	public WOPropertiesHandlerTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		project = new Project();
		handler = new WOPropertiesHandler(project);
	}

	public void testProject() throws Exception {
		assertSame(project, handler.getProject());
	}

	public void testWORoot1() throws Exception {
		String woroot = "abc";
		project.setProperty(WOPropertiesHandler.WO_ROOT, woroot);
		assertEquals(woroot, handler.getWORoot());
	}

	public void testWORoot2() throws Exception {
		String woroot = "abc_next_root";
		handler.env = new Properties();
		handler.env.setProperty(WOPropertiesHandler.WO_ROOT, woroot);

		// no project level settings - must use environment
		assertEquals(woroot, handler.getWORoot());
	}
	
    public void testWORoot3() throws Exception {
		String wrong = "abc_next_root";
		String right = "abc";
		handler.env = new Properties();
		handler.env.setProperty(WOPropertiesHandler.WO_ROOT, wrong);
		project.setProperty(WOPropertiesHandler.WO_ROOT, right);
		
		// project level properties must take precedence over the environment
		assertEquals(right, handler.getWORoot());
	}


	public void testLocalRoot1() throws Exception {
		String localRoot = "xyz";
		project.setProperty(WOPropertiesHandler.LOCAL_ROOT, localRoot);
		assertEquals(localRoot, handler.getLocalRoot());
	}
	

	public void testLocalRoot2() throws Exception {
		String right = "abc_next_root";
		handler.env = new Properties();
		handler.env.setProperty(WOPropertiesHandler.WO_ROOT, right);

        // no project-level setting, must be derived from WORoot
		assertEquals(handler.getWORoot() + "/Local", handler.getLocalRoot());
	}


	public void testHomeRoot1() throws Exception {
		String homeRoot = "123";
		project.setProperty(WOPropertiesHandler.HOME_ROOT, homeRoot);
		assertEquals(homeRoot, handler.getHomeRoot());
	}

	public void testHomeRoot2() throws Exception {
		assertEquals(System.getProperty("user.home"), handler.getHomeRoot());
	}
}