/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 *  4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
 *  5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */

package org.objectstyle.wolips.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.objectstyle.wolips.jdt.classpath.WOClasspathContainer;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WORuntimeClasspathResolver implements IRuntimeClasspathEntryResolver {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(org.eclipse.jdt.launching.IRuntimeClasspathEntry, org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(
			IRuntimeClasspathEntry entry,
			ILaunchConfiguration configuration
	)
	throws CoreException 
	{    
		IJavaProject prj = JavaRuntime.getJavaProject(configuration);
		return this.resolveRuntimeClasspathEntry(entry, prj);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(org.eclipse.jdt.launching.IRuntimeClasspathEntry, org.eclipse.jdt.core.IJavaProject)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(
			IRuntimeClasspathEntry entry,
			IJavaProject project
	)
	throws CoreException 
	{
		IPath path = entry.getClasspathEntry().getPath();
		
		List rawEntries = new ArrayList (Arrays.asList(project.getRawClasspath()));
		
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		Set referencedFrameworks = new HashSet();
		while (!rawEntries.isEmpty()) {
			IClasspathEntry thisOne = (IClasspathEntry)rawEntries.remove(0);
			if (IClasspathEntry.CPE_PROJECT == thisOne.getEntryKind()) {
				String name = thisOne.getPath().lastSegment();
				if (!referencedFrameworks.contains(name)) {
					referencedFrameworks.add(name);
					try {
						IJavaProject refPrj = JavaCore.create(wsRoot.getProject(name));
						rawEntries.addAll (Arrays.asList(refPrj.getRawClasspath()));
					} catch (CoreException up) {
						// ignore, for now
						System.out.println(up);
					}
				}
			}
		}

		IPath resultPath = new Path (path.segment(0));
		
		for (int i = 1; i < path.segmentCount(); ++i) {
			String segment = path.segment(i);
			if (!referencedFrameworks.contains(segment)) {
				resultPath = resultPath.append(segment);
			}
		}
		
		WOClasspathContainer rcc = new WOClasspathContainer (resultPath);
		
		IClasspathEntry re[] = rcc.getClasspathEntries();
		IRuntimeClasspathEntry rrce[] = new IRuntimeClasspathEntry[re.length];
		
		for (int i = 0; i < re.length; ++i) {
			rrce[i] = JavaRuntime.newArchiveRuntimeClasspathEntry(re[i].getPath());
		}
		
		return rrce;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver#resolveVMInstall(org.eclipse.jdt.core.IClasspathEntry)
	 */
	public IVMInstall resolveVMInstall(IClasspathEntry entry)
	throws CoreException {
		return null;
	}

}
