/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.datasets.adaptable;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.pattern.IStringMatcher;
import org.objectstyle.wolips.datasets.pattern.PatternsetMatcher;
import org.objectstyle.wolips.datasets.pattern.PatternsetWriter;

/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProjectPatternsets extends AbstractProjectAdapterType {
	/**
	 * Comment for <code>EXTENSION</code>
	 */
	public final static String EXTENSION = "patternset";

	/**
	 * Comment for <code>ANT_FOLDER_NAME</code>
	 */
	public final static String ANT_FOLDER_NAME = "woproject";

	public final static String LEGACY_ANT_FOLDER_NAME = "ant";

	private PatternsetMatcher woappResourcesIncludeMatcher = null;

	private PatternsetMatcher woappResourcesExcludeMatcher = null;

	private PatternsetMatcher resourcesIncludeMatcher = null;

	private PatternsetMatcher resourcesExcludeMatcher = null;

	private PatternsetMatcher classesIncludeMatcher = null;

	private PatternsetMatcher classesExcludeMatcher = null;

	/**
	 * @param project
	 */
	protected ProjectPatternsets(IProject project) {
		super(project);
	}

	/**
	 * Creates the folder "ant" within the project if it does not exist.
	 */
	public void createAntFolder() {
		String string = this.getAntFolder().getLocation().toOSString();
		File file = new File(string);
		file.mkdirs();
		try {
			this.getAntFolder().refreshLocal(IResource.DEPTH_ZERO,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @return the ant folder within the project
	 */
	public IFolder getAntFolder() {
		IFolder folder = this.getIProject().getFolder(
				ProjectPatternsets.LEGACY_ANT_FOLDER_NAME);
		if (!folder.exists())
			folder = this.getIProject().getFolder(
					ProjectPatternsets.ANT_FOLDER_NAME);
		return folder;
	}

	/**
	 * @return Returns the classesExcludeMatcher.
	 */
	private IStringMatcher getClassesExcludeMatcher() {
		if (this.classesExcludeMatcher != null) {
			return this.classesExcludeMatcher;
		}
		this.createAntFolder();
		IFile classesExcludePatternset = this.getAntFolder().getFile(
				"classes.exclude.patternset");
		if (!classesExcludePatternset.exists())
			PatternsetWriter.create(classesExcludePatternset,
					new String[] { "build.properties" });
		this.classesExcludeMatcher = new PatternsetMatcher(
				classesExcludePatternset);
		try {
			classesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return this.classesExcludeMatcher;
	}

	/**
	 * @return Returns the classesIncludeMatcher.
	 */
	private IStringMatcher getClassesIncludeMatcher() {
		if (this.classesIncludeMatcher != null) {
			return this.classesIncludeMatcher;
		}
		this.createAntFolder();
		IFile classesIncludePatternset = this.getAntFolder().getFile(
				"classes.include.patternset");
		if (!classesIncludePatternset.exists())
			PatternsetWriter.create(classesIncludePatternset, new String[] {
					"**/*.class", "*.properties" });
		this.classesIncludeMatcher = new PatternsetMatcher(
				classesIncludePatternset);
		try {
			classesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return this.classesIncludeMatcher;
	}

	/**
	 * @return Returns the resourcesExcludeMatcher.
	 */
	private IStringMatcher getResourcesExcludeMatcher() {
		if (this.resourcesExcludeMatcher != null) {
			return this.resourcesExcludeMatcher;
		}
		this.createAntFolder();
		IFile resourcesExcludePatternset = this.getAntFolder().getFile(
				"resources.exclude.patternset");
		if (!resourcesExcludePatternset.exists())
			PatternsetWriter.create(resourcesExcludePatternset, new String[] {
					"**/*.eomodeld~/", "**/*.woa/**", "**/*.framework/**" });
		this.resourcesExcludeMatcher = new PatternsetMatcher(
				resourcesExcludePatternset);
		try {
			resourcesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return this.resourcesExcludeMatcher;
	}

	/**
	 * @return Returns the resourcesIncludeMatcher.
	 */
	private IStringMatcher getResourcesIncludeMatcher() {
		if (this.resourcesIncludeMatcher != null) {
			return this.resourcesIncludeMatcher;
		}
		this.createAntFolder();
		IFile resourcesIncludePatternset = this.getAntFolder().getFile(
				"resources.include.patternset");
		if (!resourcesIncludePatternset.exists())
			PatternsetWriter.create(resourcesIncludePatternset, new String[] {
					"Properties", "**/*.eomodeld/", "**/*.d2wmodel",
					"**/*.wo/", "**/*.api", "**/*.strings" });
		this.resourcesIncludeMatcher = new PatternsetMatcher(
				resourcesIncludePatternset);
		try {
			resourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return this.resourcesIncludeMatcher;
	}

	/**
	 * @return Returns the woappResourcesExcludeMatcher.
	 */
	private IStringMatcher getWoappResourcesExcludeMatcher() {
		if (this.woappResourcesExcludeMatcher != null) {
			return this.woappResourcesExcludeMatcher;
		}
		this.createAntFolder();
		IFile wsresourcesExcludePatternset = this.getAntFolder().getFile(
				"wsresources.exclude.patternset");
		if (!wsresourcesExcludePatternset.exists())
			PatternsetWriter.create(wsresourcesExcludePatternset, new String[] {
					"**/*.woa/**", "**/*.framework/**" });
		this.woappResourcesExcludeMatcher = new PatternsetMatcher(
				wsresourcesExcludePatternset);
		try {
			wsresourcesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return this.woappResourcesExcludeMatcher;
	}

	/**
	 * @return Returns the woappResourcesIncludeMatcher.
	 */
	private IStringMatcher getWoappResourcesIncludeMatcher() {
		if (this.woappResourcesIncludeMatcher != null) {
			return this.woappResourcesIncludeMatcher;
		}
		this.createAntFolder();
		IFile wsresourcesIncludePatternset = this.getAntFolder().getFile(
				"wsresources.include.patternset");
		if (!wsresourcesIncludePatternset.exists())
			PatternsetWriter.create(wsresourcesIncludePatternset, new String[] {
					"**/*.gif", "**/*.xsl", "**/*.css", "**/*.png", "**/*.jpg",
					"**/*.js" });
		this.woappResourcesIncludeMatcher = new PatternsetMatcher(
				wsresourcesIncludePatternset);
		try {
			wsresourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return this.woappResourcesIncludeMatcher;
	}

	private String[] toProjectRelativePaths(IResource resource) {
		String[] returnValue = null;
		if (resource.getParent().getType() != IResource.ROOT
				&& resource.getParent().getType() != IResource.PROJECT) {
			returnValue = new String[2];
			IPath path = resource.getParent().getProjectRelativePath();
			String string = resource.getProject().getName() + "/"
					+ path.toString() + "/";
			returnValue[0] = string;
		} else {
			returnValue = new String[1];
		}
		IPath path = resource.getProjectRelativePath();
		String string = resource.getProject().getName() + "/" + path.toString();
		if (returnValue.length == 2) {
			returnValue[1] = string;
		} else {
			returnValue[0] = string;
		}
		return returnValue;
	}

	/**
	 * @param resource
	 * @return
	 */
	public boolean matchesClassesPattern(IResource resource) {
		String[] strings = this.toProjectRelativePaths(resource);
		if (this.getClassesExcludeMatcher().match(strings))
			return false;
		return this.getClassesIncludeMatcher().match(strings);
	}

	/**
	 * @param resource
	 * @return
	 */
	public boolean matchesWOAppResourcesPattern(IResource resource) {
		String[] strings = this.toProjectRelativePaths(resource);
		if (this.getWoappResourcesExcludeMatcher().match(strings))
			return false;
		return this.getWoappResourcesIncludeMatcher().match(strings);
	}

	/**
	 * @param resource
	 * @return
	 */
	public boolean matchesResourcesPattern(IResource resource) {
		String[] strings = this.toProjectRelativePaths(resource);
		if (this.getResourcesExcludeMatcher().match(strings))
			return false;
		return this.getResourcesIncludeMatcher().match(strings);
	}

	/**
	 * Sets up the *.patternset files in the case they don't exist.
	 */
	public void setUpPatternsetFiles() {
		this.getClassesExcludeMatcher();
		this.getClassesIncludeMatcher();
		this.getResourcesExcludeMatcher();
		this.getResourcesIncludeMatcher();
		this.getWoappResourcesExcludeMatcher();
		this.getWoappResourcesIncludeMatcher();
	}

	/**
	 * Releases the patternset cache
	 */
	public void releasePatternsetCache() {
		this.woappResourcesIncludeMatcher = null;
		this.woappResourcesExcludeMatcher = null;
		this.resourcesIncludeMatcher = null;
		this.resourcesExcludeMatcher = null;
		this.classesIncludeMatcher = null;
		this.classesExcludeMatcher = null;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean hasClassesIncludePattern(String string) {
		return this.getClassesIncludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean hasClassesExcludePattern(String string) {
		return this.getClassesExcludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean hasWOAppResourcesIncludePattern(String string) {
		return this.getWoappResourcesIncludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean hasWOAppResourcesExcludePattern(String string) {
		return this.getWoappResourcesExcludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean hasResourcesIncludePattern(String string) {
		return this.getResourcesIncludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean hasResourcesExcludePattern(String string) {
		return this.getResourcesExcludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 */
	public void addClassesIncludePattern(String string) {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this
				.getClassesIncludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		list.add(string);
		IFile classesIncludePatternset = this.getAntFolder().getFile(
				"classes.include.patternset");
		PatternsetWriter.create(classesIncludePatternset, (String[]) list
				.toArray(new String[list.size()]));
		this.classesIncludeMatcher = new PatternsetMatcher(
				classesIncludePatternset);
		try {
			classesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addClassesExcludePattern(String string) {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this
				.getClassesExcludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		list.add(string);
		IFile classesExcludePatternset = this.getAntFolder().getFile(
				"classes.exclude.patternset");
		PatternsetWriter.create(classesExcludePatternset, (String[]) list
				.toArray(new String[list.size()]));
		this.classesExcludeMatcher = new PatternsetMatcher(
				classesExcludePatternset);
		try {
			classesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addWOAppResourcesIncludePattern(String string) {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this
				.getWoappResourcesIncludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		list.add(string);
		IFile wsresourcesIncludePatternset = this.getAntFolder().getFile(
				"wsresources.include.patternset");
		PatternsetWriter.create(wsresourcesIncludePatternset, (String[]) list
				.toArray(new String[list.size()]));
		this.woappResourcesIncludeMatcher = new PatternsetMatcher(
				wsresourcesIncludePatternset);
		try {
			wsresourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addWOAppResourcesExcludePattern(String string) {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this
				.getWoappResourcesExcludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		list.add(string);
		IFile wsresourcesExcludePatternset = this.getAntFolder().getFile(
				"wsresources.exclude.patternset");
		PatternsetWriter.create(wsresourcesExcludePatternset, (String[]) list
				.toArray(new String[list.size()]));
		this.woappResourcesIncludeMatcher = new PatternsetMatcher(
				wsresourcesExcludePatternset);
		try {
			wsresourcesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addResourcesIncludePattern(String string) {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this
				.getResourcesIncludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		list.add(string);
		IFile resourcesIncludePatternset = this.getAntFolder().getFile(
				"resources.exclude.patternset");
		PatternsetWriter.create(resourcesIncludePatternset, (String[]) list
				.toArray(new String[list.size()]));
		this.woappResourcesIncludeMatcher = new PatternsetMatcher(
				resourcesIncludePatternset);
		try {
			resourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addResourcesExcludePattern(String string) {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this
				.getResourcesExcludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		list.add(string);
		IFile resourcesexcludePatternset = this.getAntFolder().getFile(
				"resources.exclude.patternset");
		PatternsetWriter.create(resourcesexcludePatternset, (String[]) list
				.toArray(new String[list.size()]));
		this.resourcesIncludeMatcher = new PatternsetMatcher(
				resourcesexcludePatternset);
		try {
			resourcesexcludePatternset.refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

}