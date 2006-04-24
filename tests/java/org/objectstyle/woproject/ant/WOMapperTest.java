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

import java.io.File;

import junit.framework.TestCase;

import org.apache.tools.ant.util.FileNameMapper;

/** 
 * Test cases for WOMapper class.
 * 
 * @author Andrei Adamchik
 */
public class WOMapperTest extends TestCase {
	protected FileNameMapper mapper;

	public WOMapperTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		mapper = new WOMapper(null, null).getImplementation();
	}

	public void testOtherResource() throws Exception {
		runMapperTest("Other/model.eomodeld", "Other/model.eomodeld");
		runMapperTest("English.lproj/Other/model.eomodeld", "English.lproj/Other/model.eomodeld");
	    runMapperTest("Nonlocalized.lproj/Other/a.plist", "Other/a.plist");
	}
	
	public void testWOComponent() throws Exception {
		runMapperTest("comp/Comp.wo", "Comp.wo");
		runMapperTest("comp/Some.lproj/comp/Comp.wo", "Comp.wo");
		runMapperTest("English.lproj/comp/Comp.wo", "English.lproj/Comp.wo");
		runMapperTest("Nonlocalized.lproj/comp/Comp.wo", "Comp.wo");
	}

	public void testWOComponentContent() throws Exception {
		runMapperTest("comp/Comp.wo/Comp.html", "Comp.wo/Comp.html");
		runMapperTest("comp/Some.lproj/comp/Comp.wo/Comp.wod", "Comp.wo/Comp.wod");
		runMapperTest("English.lproj/comp/Comp.wo/Comp.html", "English.lproj/Comp.wo/Comp.html");
		runMapperTest("Nonlocalized.lproj/comp/Comp.wo/Comp.html", "Comp.wo/Comp.html");
	}

	private void runMapperTest(String input, String expectedOutput) {
		String[] map = mapper.mapFileName(substSlashes(input));
		String output = (map == null) ? null : map[0];
		assertEquals(substSlashes(expectedOutput), output);
	}

	/** Fixes path to be platform dependent before running tests. */
	private String substSlashes(String s) {
		return (File.separatorChar == '/')
			? s
			: s.replace('/', File.separatorChar);
	}
}