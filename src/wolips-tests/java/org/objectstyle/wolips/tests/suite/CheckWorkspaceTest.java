/*
 * Created on 27.02.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.objectstyle.wolips.tests.suite;

import junit.framework.TestCase;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author uli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class CheckWorkspaceTest extends TestCase {

	private IWorkspace workspace = null;
	/**
	 * Constructor for CheckWorkspaceTest.
	 * @param arg0
	 */
	public CheckWorkspaceTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		workspace = ResourcesPlugin.getWorkspace();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		workspace = null;
	}

	public void testWorkspaceIsEmpty() {
		assertNotNull("there should allways be a workspace", workspace);
		assertNotNull("there should allways be a root for the workspace", workspace.getRoot());
		assertNotNull("the array may contain no entries but the result should be at least the array", workspace.getRoot().getProjects());
		assertEquals("before and after the tests, the workspace should be empty", workspace.getRoot().getProjects().length, 0);
	}
}
