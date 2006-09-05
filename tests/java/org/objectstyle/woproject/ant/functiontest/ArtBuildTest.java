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

package org.objectstyle.woproject.ant.functiontest;

import java.io.File;

import org.apache.tools.ant.Project;

/**
 * A test case that builds the art framework using its ant buildfile then does
 * various assertions about what ant produced.
 * 
 * @author Andrei Adamchik, Emily Bache
 */
public class ArtBuildTest extends BuildTestCase {

	protected Project project;

	public ArtBuildTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();

		String projectDir = "tests/wo/frameworks/art";
		project = getProject(new File(projectDir), new File(projectDir, "build.xml"));
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFilesPresent() throws Exception {
		String defaultTarget = project.getDefaultTarget();
		project.executeTarget(defaultTarget);

		FrameworkStructure artFrwk = new FrameworkStructure("art");
		artFrwk.setJars(new String[] { "art", "cayenne", "woproject" });
		configLocalizationTests(artFrwk);

		assertStructure(artFrwk);
	}

	/** Add tests for localized resources. */
	private void configLocalizationTests(FrameworkStructure artFrwk) {
		// test WOComponents
		// 1. Source: Top level, non localized
		artFrwk.addToWocomps("Comp5");

		// 2. Source: Sub directory, non localized
		artFrwk.addToWocomps("Comp2");

		// 3. Source: Sub directory, localized, but lproj is not top directory
		artFrwk.addToWocomps("Comp1");

		// 4. Source: Sub directory, localized, but lproj is Nonlocalized.lproj
		// directory
		artFrwk.addToWocomps("Comp3");

		// 5. Source: Sub directory, localized
		artFrwk.addToWocomps("English.lproj/Comp4");

		// test other resources
		// 1. Source: Top level, non localized
		artFrwk.addToWsResources("spacer1.gif");

		// 2. Source: Sub directory, non localized
		artFrwk.addToWsResources("images/spacer2.gif");

		// 3. Source: Sub directory, localized, but lproj is not top directory
		artFrwk.addToWsResources("images/NotALproj.lproj/spacer3.gif");

		// 4. Source: Sub directory, localized
		artFrwk.addToWsResources("English.lproj/spacer5.gif");

		// 5. Source: Sub directory, localized, but lproj is Nonlocalized.lproj
		// directory
		artFrwk.addToWsResources("spacer4.gif");
	}
}