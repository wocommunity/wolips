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

package org.objectstyle.wolips.launching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;
import org.objectstyle.wolips.core.project.INaturesAccessor;
import org.objectstyle.wolips.core.project.IWOLipsProject;
import org.objectstyle.wolips.core.project.WOLipsCore;

/**
 * @author hn3000
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class WORuntimeClasspathProvider
	extends StandardClasspathProvider {
	public final static String ID =
		"org.objectstyle.wolips.launching.WORuntimeClasspath";

	/* (non-Javadoc)
		 * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#computeUnresolvedClasspath(org.eclipse.debug.core.ILaunchConfiguration)
		 */
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration)
		throws CoreException {
		return super.computeUnresolvedClasspath(configuration);
	}

	/* (non-Javadoc)
		 * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#resolveClasspath(org.eclipse.jdt.launching.IRuntimeClasspathEntry[], org.eclipse.debug.core.ILaunchConfiguration)
		 */
	public IRuntimeClasspathEntry[] resolveClasspath(
		IRuntimeClasspathEntry[] entries,
		ILaunchConfiguration configuration)
		throws CoreException {

		List others = new ArrayList();
		List resolved = new ArrayList();

		// used for duplicate removal
		Set allEntries = new HashSet();

		// resolve WO framework projects ourselves, let super do the rest
		for (int i = 0; i < entries.length; ++i) {
			IResource archive = _getWOJavaArchive(entries[i]);
			if (null != archive) {
				if (!allEntries.contains(archive.getLocation())) {
					resolved.add(
						JavaRuntime.newArchiveRuntimeClasspathEntry(archive));

					allEntries.add(archive.getLocation());
				}
			} else {
				others.add(entries[i]);
			}
		}

		// ... let super do the rest but remove duplicates from the resulting classpath ...
		if (null != others) {
			IRuntimeClasspathEntry oe[] =
				super.resolveClasspath(
					(IRuntimeClasspathEntry[]) others.toArray(
						new IRuntimeClasspathEntry[others.size()]),
					configuration);

			for (int i = 0; i < oe.length; ++i) {
				IRuntimeClasspathEntry entry = oe[i];
				String ls = entry.getLocation();
				IPath loc = (null == ls) ? null : new Path(ls);
				if (null == loc) {
					resolved.add(entry);
				} else {
					if (!allEntries.contains(loc)) {
						resolved.add(entry);
						allEntries.add(loc);
					}
				}
			}
		}
		return (IRuntimeClasspathEntry[]) resolved.toArray(
			new IRuntimeClasspathEntry[resolved.size()]);
	}

	IResource _getWOJavaArchive(IRuntimeClasspathEntry entry)
		throws CoreException {
		IResource result = null;

		if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
			IProject project = (IProject) entry.getResource();

			IWOLipsProject wop = WOLipsCore.createProject(project);
			INaturesAccessor na = wop.getNaturesAccessor();

			String projectName = project.getName();
			String projectNameLC = projectName.toLowerCase();

			// I'd rather use the knowledge from the IncrementalNature, but that fragment is not
			// visible here (so I can't use the class, I think) [hn3000]
			if (na.isFramework()) {
				if (na.isAnt()) {
					result =
						project.getFile(
							"dist/"
								+ projectName
								+ ".framework/Resources/Java/"
								+ projectNameLC
								+ ".jar");
				} else if (na.isIncremental()) {
					result =
						project.getFolder(
							"build/"
								+ projectName
								+ ".framework/Resources/Java");
				}
			} else if (na.isApplication()) { // must be application
				if (na.isAnt()) {
					result =
						project.getFile(
							"dist/"
								+ projectName
								+ ".woa/Contents/Resources/Java/"
								+ projectNameLC
								+ ".jar");
				} else if (na.isIncremental()) {
					result =
						project.getFolder(
							"build/"
								+ projectName
								+ ".woa/Contents/Resources/Java");
				}
			}

			// check if folder exists, otherwise let Eclipse to its default thing
			if ((null != result) && (!result.exists())) {
				System.out.println(
					"expected resource is not there: "
						+ result.getLocation().toOSString());
				result = null;
			}
		}
		return result;
	}
}
