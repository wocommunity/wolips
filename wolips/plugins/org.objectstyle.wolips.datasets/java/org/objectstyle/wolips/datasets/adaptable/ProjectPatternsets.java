/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2005 The ObjectStyle Group 
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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.core.resources.pattern.PatternsetWriter;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.pattern.IStringMatcher;
import org.objectstyle.wolips.datasets.pattern.PatternsetMatcher;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public class ProjectPatternsets extends AbstractProjectAdapterType {

	/**
	 * EXTENSION file extension for patternset files
	 */
	public final static String EXTENSION = "patternset";

	/**
	 * name of the woproject folder
	 */
	public final static String ANT_FOLDER_NAME = "woproject";

	protected final static String LEGACY_ANT_FOLDER_NAME = "ant";

	private PatternsetMatcher woappResourcesIncludeMatcher = null;

	private PatternsetMatcher woappResourcesExcludeMatcher = null;

	private PatternsetMatcher resourcesIncludeMatcher = null;

	private PatternsetMatcher resourcesExcludeMatcher = null;

	private PatternsetMatcher classesIncludeMatcher = null;

	private PatternsetMatcher classesExcludeMatcher = null;

	private final static PatternsetMatcher DEFAULT_EXCLUDE_MATCHER = new PatternsetMatcher(new String[] { "**/.svn", "**/.svn/**", "**/CVS", "**/*.eomodeld~", "**/*.eomodeld~/**", "**/CVS/**", "**/build/**", "**/dist/**" });

	/**
	 * @param project
	 */
	protected ProjectPatternsets(IProject project) {
		super(project);
	}

	private String[] getWSResourcesIncludeStringsDefault() {
		return new String[] { "WebServerResources/**/*" };
	}

	private String[] getWSResourcesExcludeStringsDefault() {
		return new String[] {};
	}

	private String[] getResourcesIncludeStringsDefault() {
		return new String[] { "Components/**/*.wo/**/*", "Components/**/*.api", "Resources/**/*" };
	}

	private String[] getResourcesExcludeStringsDefault() {
		return new String[] { "Resources/**/*.eomodeld~/**" };
	}

	private String[] getClassesIncludeStringsDefault() {
		return new String[] { "**/*.class", "*.properties" };
	}

	private String[] getClassesExcludeStringsDefault() {
		return new String[] { "build.properties" };
	}

	/**
	 * Creates the folder "ant" within the project if it does not exist.
	 */
	public void createAntFolder() {
		if (this.getAntFolder().exists()) {
			return;
		}
		IWorkspaceRunnable operation = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				String string = getAntFolder().getLocation().toOSString();
				File file = new File(string);
				file.mkdirs();
				try {
					getAntFolder().refreshLocal(IResource.DEPTH_ZERO, monitor);
				} catch (CoreException e) {
					DataSetsPlugin.getDefault().getPluginLogger().log(e);
				}
			}

		};
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.run(operation, this.getIProject(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @return the ant folder within the project
	 */
	public IFolder getAntFolder() {
		IFolder folder = this.getIProject().getFolder(ProjectPatternsets.LEGACY_ANT_FOLDER_NAME);
		if (!folder.exists())
			folder = this.getIProject().getFolder(ProjectPatternsets.ANT_FOLDER_NAME);
		return folder;
	}

	private void run(IProject rule, PatternsetWorkspaceRunnable patternsetWorkspaceRunnable) {
		try {
			ResourcesPlugin.getWorkspace().run(patternsetWorkspaceRunnable, rule, IResource.NONE, null);
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	class PatternsetWorkspaceRunnable implements IWorkspaceRunnable {
		private IFile patternset;

		private PatternsetMatcher matcher;

		private String[] defaultPattern;

		PatternsetWorkspaceRunnable(IFile patternset, String[] defaultPattern) {
			super();
			this.patternset = patternset;
			this.defaultPattern = defaultPattern;

		}

		public void run(IProgressMonitor pm) throws CoreException {
			if (!patternset.exists()) {
				PatternsetWriter.create(patternset, defaultPattern);
				try {
					patternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
				} catch (CoreException e) {
					DataSetsPlugin.getDefault().getPluginLogger().log(e);
				}
			}
			this.matcher = new PatternsetMatcher(patternset);

		}

		protected PatternsetMatcher getMatcher() {
			return matcher;
		}
	}

	/**
	 * @return Returns the classesExcludeMatcher.
	 */
	private IStringMatcher getClassesExcludeMatcher() {
		if (this.classesExcludeMatcher != null) {
			return this.classesExcludeMatcher;
		}
		this.createAntFolder();
		IFile classesExcludePatternset = this.getAntFolder().getFile("classes.exclude.patternset");
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(classesExcludePatternset, getClassesExcludeStringsDefault());
		this.run(this.getIProject(), patternsetWorkspaceRunnable);
		this.classesExcludeMatcher = patternsetWorkspaceRunnable.getMatcher();
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
		IFile classesIncludePatternset = this.getAntFolder().getFile("classes.include.patternset");
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(classesIncludePatternset, getClassesIncludeStringsDefault());
		this.run(this.getIProject(), patternsetWorkspaceRunnable);
		this.classesIncludeMatcher = patternsetWorkspaceRunnable.getMatcher();
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
		IFile resourcesExcludePatternset = this.getAntFolder().getFile("resources.exclude.patternset");
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(resourcesExcludePatternset, getResourcesExcludeStringsDefault());
		this.run(this.getIProject(), patternsetWorkspaceRunnable);
		this.resourcesExcludeMatcher = patternsetWorkspaceRunnable.getMatcher();
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
		IFile resourcesIncludePatternset = this.getAntFolder().getFile("resources.include.patternset");
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(resourcesIncludePatternset, getResourcesIncludeStringsDefault());
		this.run(this.getIProject(), patternsetWorkspaceRunnable);
		this.resourcesIncludeMatcher = patternsetWorkspaceRunnable.getMatcher();
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
		IFile wsresourcesExcludePatternset = this.getAntFolder().getFile("wsresources.exclude.patternset");
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(wsresourcesExcludePatternset, getWSResourcesExcludeStringsDefault());
		this.run(this.getIProject(), patternsetWorkspaceRunnable);
		this.woappResourcesExcludeMatcher = patternsetWorkspaceRunnable.getMatcher();
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
		IFile wsresourcesIncludePatternset = this.getAntFolder().getFile("wsresources.include.patternset");
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(wsresourcesIncludePatternset, getWSResourcesIncludeStringsDefault());
		this.run(this.getIProject(), patternsetWorkspaceRunnable);
		this.woappResourcesIncludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.woappResourcesIncludeMatcher;
	}

	private String[] toProjectRelativePaths(IResource resource) {
		String[] returnValue = null;
		if (resource.getParent().getType() != IResource.ROOT) {

			String string = null;
			if (resource.getType() != IResource.FOLDER) {
				returnValue = new String[1];
			} else {
				returnValue = new String[2];
				string = "/" + resource.getName() + "/";
				returnValue[0] = string;
			}

		} else {
			returnValue = new String[1];
		}
		IPath path = resource.getProjectRelativePath();
		String string = path.toString();
		if (returnValue.length == 2) {
			returnValue[1] = string;
		} else {
			returnValue[0] = string;
		}
		return returnValue;
	}

	/**
	 * @param resource
	 * @return true if the resource matches the classes pattern
	 */
	public boolean matchesClassesPattern(IResource resource) {
		String[] strings = this.toProjectRelativePaths(resource);
		if (this.getClassesExcludeMatcher().match(strings))
			return false;
		if (DEFAULT_EXCLUDE_MATCHER.match(strings))
			return false;
		return this.getClassesIncludeMatcher().match(strings);
	}

	/**
	 * @param resource
	 * @return true if the resource matches the WOAppResources pattern
	 */
	public boolean matchesWOAppResourcesPattern(IResource resource) {
		String[] strings = this.toProjectRelativePaths(resource);
		if (this.getWoappResourcesExcludeMatcher().match(strings))
			return false;
		if (DEFAULT_EXCLUDE_MATCHER.match(strings))
			return false;
		return this.getWoappResourcesIncludeMatcher().match(strings);
	}

	/**
	 * @param resource
	 * @return true if the resource matches the Resources pattern
	 */
	public boolean matchesResourcesPattern(IResource resource) {
		String[] strings = this.toProjectRelativePaths(resource);
		if (this.getResourcesExcludeMatcher().match(strings))
			return false;
		if (DEFAULT_EXCLUDE_MATCHER.match(strings))
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
	 * @return true is the pattern allready exists
	 */
	public boolean hasClassesIncludePattern(String string) {
		return this.getClassesIncludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return true is the pattern allready exists
	 */
	public boolean hasClassesExcludePattern(String string) {
		return this.getClassesExcludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return true is the pattern allready exists
	 */
	public boolean hasWOAppResourcesIncludePattern(String string) {
		return this.getWoappResourcesIncludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return true is the pattern allready exists
	 */
	public boolean hasWOAppResourcesExcludePattern(String string) {
		return this.getWoappResourcesExcludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return true is the pattern allready exists
	 */
	public boolean hasResourcesIncludePattern(String string) {
		return this.getResourcesIncludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 * @return true is the pattern allready exists
	 */
	public boolean hasResourcesExcludePattern(String string) {
		return this.getResourcesExcludeMatcher().hasPattern(string);
	}

	/**
	 * @param string
	 */
	public void addClassesIncludePattern(String string) {
		List list = loadClassesIncludePatternSet();
		if (!list.contains(string)) {
			list.add(string);
			saveClassesIncludePatternList(list);
			removeClassesExcludePattern(string);
		}
	}

	/**
	 * @param string
	 */
	public void removeClassesIncludePattern(String string) {
		List patterns = loadClassesIncludePatternSet();
		if (patterns.remove(string)) {
			saveClassesIncludePatternList(patterns);
		}
	}

	protected List loadClassesIncludePatternSet() {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this.getClassesIncludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		return list;
	}

	protected void saveClassesIncludePatternList(List list) {
		IFile classesIncludePatternset = this.getAntFolder().getFile("classes.include.patternset");
		PatternsetWriter.create(classesIncludePatternset, (String[]) list.toArray(new String[list.size()]));
		this.classesIncludeMatcher = new PatternsetMatcher(classesIncludePatternset);
		try {
			classesIncludePatternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addClassesExcludePattern(String string) {
		List list = loadClassesExcludePatternList();
		if (!list.contains(string)) {
			list.add(string);
			saveClassesExcludePatternList(list);
			removeClassesIncludePattern(string);
		}
	}

	/**
	 * @param string
	 */
	public void removeClassesExcludePattern(String string) {
		List list = loadClassesExcludePatternList();
		if (list.remove(string)) {
			saveClassesExcludePatternList(list);
		}
	}

	/**
	 * @param string
	 */
	public List loadClassesExcludePatternList() {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this.getClassesExcludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		return list;
	}

	public void saveClassesExcludePatternList(List list) {
		IFile classesExcludePatternset = this.getAntFolder().getFile("classes.exclude.patternset");
		PatternsetWriter.create(classesExcludePatternset, (String[]) list.toArray(new String[list.size()]));
		this.classesExcludeMatcher = new PatternsetMatcher(classesExcludePatternset);
		try {
			classesExcludePatternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addWOAppResourcesIncludePattern(String string) {
		List list = loadWOAppResourcesIncludePatternList();
		if (!list.contains(string)) {
			list.add(string);
			saveWOAppResourcesIncludePatternList(list);
			removeWOAppResourcesExcludePattern(string);
		}
	}

	public void removeWOAppResourcesIncludePattern(String string) {
		List list = loadWOAppResourcesIncludePatternList();
		if (list.remove(string)) {
			saveWOAppResourcesIncludePatternList(list);
		}
	}

	public List loadWOAppResourcesIncludePatternList() {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this.getWoappResourcesIncludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		return list;
	}

	public void saveWOAppResourcesIncludePatternList(List list) {
		IFile wsresourcesIncludePatternset = this.getAntFolder().getFile("wsresources.include.patternset");
		PatternsetWriter.create(wsresourcesIncludePatternset, (String[]) list.toArray(new String[list.size()]));
		this.woappResourcesIncludeMatcher = new PatternsetMatcher(wsresourcesIncludePatternset);
		try {
			wsresourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addWOAppResourcesExcludePattern(String string) {
		List list = loadWOAppResourcesExcludePatternList();
		if (!list.contains(string)) {
			list.add(string);
			saveWOAppResourcesExcludePatternList(list);
			removeWOAppResourcesIncludePattern(string);
		}
	}

	public void removeWOAppResourcesExcludePattern(String string) {
		List list = loadWOAppResourcesExcludePatternList();
		if (list.remove(string)) {
			saveWOAppResourcesExcludePatternList(list);
		}
	}

	public List loadWOAppResourcesExcludePatternList() {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this.getWoappResourcesExcludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		return list;
	}

	public void saveWOAppResourcesExcludePatternList(List list) {
		IFile wsresourcesExcludePatternset = this.getAntFolder().getFile("wsresources.exclude.patternset");
		PatternsetWriter.create(wsresourcesExcludePatternset, (String[]) list.toArray(new String[list.size()]));
		this.woappResourcesExcludeMatcher = new PatternsetMatcher(wsresourcesExcludePatternset);
		try {
			wsresourcesExcludePatternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addResourcesIncludePattern(String string) {
		List list = loadResourcesIncludePatternList();
		if (!list.contains(string)) {
			list.add(string);
			saveResourcesIncludePatternList(list);
			removeResourcesExcludePattern(string);
		}
	}

	public void removeResourcesIncludePattern(String string) {
		List list = loadResourcesIncludePatternList();
		if (list.remove(string)) {
			saveResourcesIncludePatternList(list);
		}
	}

	public List loadResourcesIncludePatternList() {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this.getResourcesIncludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		return list;
	}

	public void saveResourcesIncludePatternList(List list) {
		IFile resourcesIncludePatternset = this.getAntFolder().getFile("resources.include.patternset");
		PatternsetWriter.create(resourcesIncludePatternset, (String[]) list.toArray(new String[list.size()]));
		this.resourcesIncludeMatcher = new PatternsetMatcher(resourcesIncludePatternset);
		try {
			resourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	/**
	 * @param string
	 */
	public void addResourcesExcludePattern(String string) {
		List list = loadResourcesExcludePatternList();
		if (!list.contains(string)) {
			list.add(string);
			saveResourcesExcludePatternList(list);
			removeResourcesIncludePattern(string);
		}
	}

	public void removeResourcesExcludePattern(String string) {
		List list = loadResourcesExcludePatternList();
		if (list.remove(string)) {
			saveResourcesExcludePatternList(list);
		}
	}

	public List loadResourcesExcludePatternList() {
		PatternsetMatcher patternsetMatcher = (PatternsetMatcher) this.getResourcesExcludeMatcher();
		String[] pattern = patternsetMatcher.getPattern();
		ArrayList list = new ArrayList();
		for (int i = 0; i < pattern.length; i++) {
			list.add(pattern[i]);
		}
		return list;
	}

	public void saveResourcesExcludePatternList(List list) {
		IFile resourcesexcludePatternset = this.getAntFolder().getFile("resources.exclude.patternset");
		PatternsetWriter.create(resourcesexcludePatternset, (String[]) list.toArray(new String[list.size()]));
		this.resourcesIncludeMatcher = new PatternsetMatcher(resourcesexcludePatternset);
		try {
			resourcesexcludePatternset.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

}