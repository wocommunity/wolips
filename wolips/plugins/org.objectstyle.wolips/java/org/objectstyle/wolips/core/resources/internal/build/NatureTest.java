/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.internal.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.core.resources.tests.AbstractProjectTestCase;

public class NatureTest extends AbstractProjectTestCase {

	public void testAddIncrementalFrameworkNature() {
		IProject project = this.getProject("testAddIncrementalFrameworkNature");
		NullProgressMonitor monitor = new NullProgressMonitor();
		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = Nature.addIncrementalFrameworkNatureToProject(project, monitor);
			assertTrue(success);
			success = Nature.addIncrementalFrameworkNatureToProject(project, monitor);
			assertTrue(success);
			Nature nature = Nature.getNature(project);
			assertTrue(nature.isFramework());
		} catch (CoreException e) {
			assertTrue(false);
		}
		CoreException exception = null;
		try {
			Nature.addIncrementalApplicationNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addAntApplicationNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addAntFrameworkNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	public void testAddIncrementalApplicationNature() {
		IProject project = this.getProject("testAddIncrementalApplicationNature");
		NullProgressMonitor monitor = new NullProgressMonitor();
		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = Nature.addIncrementalApplicationNatureToProject(project, monitor);
			assertTrue(success);
			success = Nature.addIncrementalApplicationNatureToProject(project, monitor);
			assertTrue(success);
			Nature nature = Nature.getNature(project);
			assertFalse(nature.isFramework());
		} catch (CoreException e) {
			assertTrue(false);
		}
		CoreException exception = null;
		try {
			Nature.addIncrementalFrameworkNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addAntApplicationNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addAntFrameworkNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	public void testAddAndFrameworkNature() {
		IProject project = this.getProject("testAddAndFrameworkNature");
		NullProgressMonitor monitor = new NullProgressMonitor();
		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = Nature.addAntFrameworkNatureToProject(project, monitor);
			assertTrue(success);
			success = Nature.addAntFrameworkNatureToProject(project, monitor);
			assertTrue(success);
			Nature nature = Nature.getNature(project);
			assertTrue(nature.isFramework());
		} catch (CoreException e) {
			assertTrue(false);
		}
		CoreException exception = null;
		try {
			Nature.addAntApplicationNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addIncrementalFrameworkNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addIncrementalApplicationNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	public void testAddAntApplicationNature() {
		IProject project = this.getProject("testAddAntApplicationNature");
		NullProgressMonitor monitor = new NullProgressMonitor();
		try {
			project.create(monitor);
			project.open(monitor);
			IProjectDescription description = project.getDescription();
			List<String> naturesList = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			naturesList.add("org.eclipse.jdt.core.javanature");
			description.setNatureIds(naturesList.toArray(new String[naturesList.size()]));
			project.setDescription(description, monitor);
			boolean success = Nature.addAntApplicationNatureToProject(project, monitor);
			assertTrue(success);
			success = Nature.addAntApplicationNatureToProject(project, monitor);
			assertTrue(success);
			Nature nature = Nature.getNature(project);
			assertFalse(nature.isFramework());
		} catch (CoreException e) {
			assertTrue(false);
		}
		CoreException exception = null;
		try {
			Nature.addAntFrameworkNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addIncrementalApplicationNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			Nature.addIncrementalFrameworkNatureToProject(project, monitor);
		} catch (CoreException e) {
			exception = e;
		}
		assertNotNull(exception);
	}

}
