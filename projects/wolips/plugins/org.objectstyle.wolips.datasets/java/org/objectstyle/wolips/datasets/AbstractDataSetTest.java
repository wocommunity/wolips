/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
package org.objectstyle.wolips.datasets;

import java.io.StringBufferInputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.objectstyle.wolips.tests.core.Utils;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractDataSetTest extends TestCase {

	private IProject project = null;
	private IJavaProject javaProject = null;
	
	/**
	 * Constructor for AbstractResourcesTest.
	 * @param arg0
	 */
	public AbstractDataSetTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		try {
			project = Utils.CreateProject(AbstractDataSetTest.class.getName());
			javaProject = Utils.CreateJavaProject(AbstractDataSetTest.class.getName()
						+ "Java");
		} catch (CoreException e) {
			assertNull("Project creation shouldnt throw an Exception",
					e);
		}
		}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		try {
			project.delete(false, new NullProgressMonitor());
			javaProject.getProject().delete(false,
						new NullProgressMonitor());
		} catch (CoreException e) {
			assertNull("Project deletion shouldnt throw an exception",
					e);
		}
	}
	
	protected void assertTypeAndExtensionForFile(int type, String extension) {
		int newType = DataSetsPlugin.getDefault().getAssociatedType(DataSetsPlugin.API_EXTENSION);
		Assert.assertEquals("type does not match", newType, type);
		this.assertFileType(type, extension);
	}
	
	protected void assertTypeAndExtensionForFolder(int type, String extension) {
		int newType = DataSetsPlugin.getDefault().getAssociatedType(DataSetsPlugin.API_EXTENSION);
		Assert.assertEquals("type does not match", newType, type);
		this.assertFolderType(type, extension);
	}
	
	private void assertFileType(int type, String extension) {
		IDataSet dataSet = this.getDataSet(this.file(extension));
		Assert.assertNotNull("getResource should not return null for: " + extension , dataSet);
		Assert.assertEquals("the dataset should return the same type", type, dataSet.getType());
	}
	
	private void assertFolderType(int type, String extension) {
		IDataSet dataSet = this.getDataSet(this.folder(extension));
		Assert.assertNotNull("getResource should not return null for: " + extension , dataSet);
		Assert.assertEquals("the dataset should return the same type", type, dataSet.getType());
	}
	
	private IDataSet getDataSet(IResource resource) {
		return DataSetsPlugin.getDefault().getDataSet(resource);
	}
	
	private IFile file(String string) {
		IFile file = project.getFile("foo." + string);
		try {
			file.create(new StringBufferInputStream(""), false, new NullProgressMonitor());
		} catch (CoreException e) {
			Assert.assertNull("file creation should not throw an exception", e);
		}
		return file;
	}

	private IFolder folder(String string) {
		IFolder folder =project.getFolder("foo." + string);
		try {
			folder.create(false, false, new NullProgressMonitor());
		} catch (CoreException e) {
			Assert.assertNull("Project creation should not throw an exception", e);
		}
		return folder;
	}

}
