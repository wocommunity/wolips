/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group 
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
package org.objectstyle.wolips.core.resources.internal.types.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.pattern.PatternsetMatcher;
import org.objectstyle.wolips.core.resources.pattern.PatternsetWriter;
import org.objectstyle.wolips.core.resources.types.ILocalizedPath;
import org.objectstyle.wolips.core.resources.types.IPBDotProjectOwner;
import org.objectstyle.wolips.core.resources.types.IResourceType;
import org.objectstyle.wolips.core.resources.types.project.IProjectPatternsets;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author ulrich
 */
public class ProjectPatternsets implements IProjectPatternsets, IResourceType {

	/**
	 * EXTENSION file extension for patternset files
	 */
	public final static String EXTENSION = "patternset";

	/**
	 * name of the woproject folder
	 */
	public final static String ANT_FOLDER_NAME = "woproject";

	protected final static String LEGACY_ANT_FOLDER_NAME = "ant";

	private Map<IFile, Long> _modificationStamps;
	
	private PatternsetMatcher woappResourcesIncludeMatcher;

	private PatternsetMatcher woappResourcesExcludeMatcher;

	private PatternsetMatcher resourcesIncludeMatcher;

	private PatternsetMatcher resourcesExcludeMatcher;

	private PatternsetMatcher classesIncludeMatcher;

	private PatternsetMatcher classesExcludeMatcher;

	private final static PatternsetMatcher DEFAULT_EXCLUDE_MATCHER = new PatternsetMatcher(new String[] { "**/.svn", "**/.svn/**", "**/CVS", "**/*.eomodeld~", "**/*.eomodeld~/**", "**/CVS/**", "**/build/**", "**/dist/**" });

	private IProject project;

	/**
	 * @param project
	 */
	public ProjectPatternsets(IProject project) {
		super();
		this.project = project;
		_modificationStamps = new HashMap<IFile, Long>();
	}
	
	protected boolean useCachedVersion(IFile file) {
		Long cachedModificationStamp = _modificationStamps.get(file);
		long modificationStamp = file.getModificationStamp();
		boolean useCachedVersion = true;
		if (cachedModificationStamp == null || modificationStamp > cachedModificationStamp.longValue()) {
			_modificationStamps.put(file, Long.valueOf(modificationStamp));
			useCachedVersion = false;
		}
		return useCachedVersion;
	}

	private String[] getStringsFromDefaults(String key, String[] def) {
		String values = VariablesPlugin.getDefault().getProperty(key);
		if (values == null) {
			return def;
		}
		return values.split("\\,\\s*");
	}

	private String[] getWSResourcesIncludeStringsDefault() {
		return getStringsFromDefaults("wsresources.include.patternset", new String[] { "WebServerResources/*" });
	}

	private String[] getWSResourcesExcludeStringsDefault() {
		return getStringsFromDefaults("wsresources.exclude.patternset", new String[] {});
	}

	private String[] getResourcesIncludeStringsDefault() {
		return getStringsFromDefaults("resources.include.patternset", new String[] { "Components/**/*.wo/**/*", "Components/**/*.api", "Resources/**/*" });
	}

	private String[] getResourcesExcludeStringsDefault() {
		return getStringsFromDefaults("resources.exclude.patternset", new String[] { "Resources/**/*.eomodeld~/**" });
	}

	private String[] getClassesIncludeStringsDefault() {
		return getStringsFromDefaults("classes.include.patternset", new String[] { "**/*.class", "*.properties" });
	}

	private String[] getClassesExcludeStringsDefault() {
		return getStringsFromDefaults("classes.exclude.patternset", new String[] { "build.properties" });
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
					CorePlugin.getDefault().log(e);
				}
			}

		};
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.run(operation, this.getIProject(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		} catch (CoreException e) {
			CorePlugin.getDefault().log(e);
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

	protected static class PatternsetWorkspaceRunnable implements Runnable {
		IFile patternset;

		private PatternsetMatcher matcher;

		String[] defaultPattern;

		PatternsetWorkspaceRunnable(IFile patternset, String[] defaultPattern) {
			super();
			this.patternset = patternset;
			this.defaultPattern = defaultPattern;

		}

		public void run() {
			if (!patternset.exists()) {
				IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
					public void run(final IProgressMonitor pm) throws CoreException {
						PatternsetWriter.create(patternset, defaultPattern);
						try {
							patternset.refreshLocal(IResource.DEPTH_ONE, pm);
						} catch (CoreException e) {
							CorePlugin.getDefault().log(e);
						}
					}
				};
				try {
					ResourcesPlugin.getWorkspace().run(workspaceRunnable, patternset, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
				} catch (CoreException e) {
					CorePlugin.getDefault().log(e);
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
	public PatternsetMatcher getClassesExcludeMatcher() {
		IFile classesExcludePatternset = this.getAntFolder().getFile("classes.exclude.patternset");
		if (this.classesExcludeMatcher != null && useCachedVersion(classesExcludePatternset)) {
			return this.classesExcludeMatcher;
		}
		this.createAntFolder();
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(classesExcludePatternset, getClassesExcludeStringsDefault());
		patternsetWorkspaceRunnable.run();
		this.classesExcludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.classesExcludeMatcher;
	}

	/**
	 * @return Returns the classesIncludeMatcher.
	 */
	public PatternsetMatcher getClassesIncludeMatcher() {
		IFile classesIncludePatternset = this.getAntFolder().getFile("classes.include.patternset");
		if (this.classesIncludeMatcher != null && useCachedVersion(classesIncludePatternset)) {
			return this.classesIncludeMatcher;
		}
		this.createAntFolder();
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(classesIncludePatternset, getClassesIncludeStringsDefault());
		patternsetWorkspaceRunnable.run();
		this.classesIncludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.classesIncludeMatcher;
	}

	/**
	 * @return Returns the resourcesExcludeMatcher.
	 */
	public PatternsetMatcher getResourcesExcludeMatcher() {
		IFile resourcesExcludePatternset = this.getAntFolder().getFile("resources.exclude.patternset");
		if (this.resourcesExcludeMatcher != null && useCachedVersion(resourcesExcludePatternset)) {
			return this.resourcesExcludeMatcher;
		}
		this.createAntFolder();
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(resourcesExcludePatternset, getResourcesExcludeStringsDefault());
		patternsetWorkspaceRunnable.run();
		this.resourcesExcludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.resourcesExcludeMatcher;
	}

	/**
	 * @return Returns the resourcesIncludeMatcher.
	 */
	public PatternsetMatcher getResourcesIncludeMatcher() {
		IFile resourcesIncludePatternset = this.getAntFolder().getFile("resources.include.patternset");
		if (this.resourcesIncludeMatcher != null && useCachedVersion(resourcesIncludePatternset)) {
			return this.resourcesIncludeMatcher;
		}
		this.createAntFolder();
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(resourcesIncludePatternset, getResourcesIncludeStringsDefault());
		patternsetWorkspaceRunnable.run();
		this.resourcesIncludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.resourcesIncludeMatcher;
	}

	/**
	 * @return Returns the woappResourcesExcludeMatcher.
	 */
	public PatternsetMatcher getWoappResourcesExcludeMatcher() {
		IFile wsresourcesExcludePatternset = this.getAntFolder().getFile("wsresources.exclude.patternset");
		if (this.woappResourcesExcludeMatcher != null && useCachedVersion(wsresourcesExcludePatternset)) {
			return this.woappResourcesExcludeMatcher;
		}
		this.createAntFolder();
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(wsresourcesExcludePatternset, getWSResourcesExcludeStringsDefault());
		patternsetWorkspaceRunnable.run();
		this.woappResourcesExcludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.woappResourcesExcludeMatcher;
	}

	/**
	 * @return Returns the woappResourcesIncludeMatcher.
	 */
	public PatternsetMatcher getWoappResourcesIncludeMatcher() {
		IFile wsresourcesIncludePatternset = this.getAntFolder().getFile("wsresources.include.patternset");
		if (this.woappResourcesIncludeMatcher != null && useCachedVersion(wsresourcesIncludePatternset)) {
			return this.woappResourcesIncludeMatcher;
		}
		this.createAntFolder();
		PatternsetWorkspaceRunnable patternsetWorkspaceRunnable = new PatternsetWorkspaceRunnable(wsresourcesIncludePatternset, getWSResourcesIncludeStringsDefault());
		patternsetWorkspaceRunnable.run();
		this.woappResourcesIncludeMatcher = patternsetWorkspaceRunnable.getMatcher();
		return this.woappResourcesIncludeMatcher;
	}

	/**
	 * @param resource
	 * @return true if the resource matches the classes pattern
	 */
	public boolean matchesClassesPattern(IResource resource) {
		String relativePath = resource.getProjectRelativePath().toString();
		return !getClassesExcludeMatcher().match(relativePath) && !DEFAULT_EXCLUDE_MATCHER.match(relativePath) && getClassesIncludeMatcher().match(relativePath);
	}

	/**
	 * @param resource
	 * @return true if the resource matches the WOAppResources pattern
	 */
	public boolean matchesWOAppResourcesPattern(IResource resource) {
		String relativePath = resource.getProjectRelativePath().toString();
		return !this.getWoappResourcesExcludeMatcher().match(relativePath) && !DEFAULT_EXCLUDE_MATCHER.match(relativePath) && this.getWoappResourcesIncludeMatcher().match(relativePath);
	}

	/**
	 * @param resource
	 * @return true if the resource matches the Resources pattern
	 */
	public boolean matchesResourcesPattern(IResource resource) {
		String relativePath = resource.getProjectRelativePath().toString();
		boolean matches = !this.getResourcesExcludeMatcher().match(relativePath) && !DEFAULT_EXCLUDE_MATCHER.match(relativePath) && this.getResourcesIncludeMatcher().match(relativePath);
		return matches;
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
		PatternsetMatcher patternsetMatcher = this.getClassesIncludeMatcher();
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
			CorePlugin.getDefault().log(e);
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
		PatternsetMatcher patternsetMatcher = this.getClassesExcludeMatcher();
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
			CorePlugin.getDefault().log(e);
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
		PatternsetMatcher patternsetMatcher = this.getWoappResourcesIncludeMatcher();
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
			CorePlugin.getDefault().log(e);
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
		PatternsetMatcher patternsetMatcher = this.getWoappResourcesExcludeMatcher();
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
			CorePlugin.getDefault().log(e);
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
		PatternsetMatcher patternsetMatcher = this.getResourcesIncludeMatcher();
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
			CorePlugin.getDefault().log(e);
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
		PatternsetMatcher patternsetMatcher = this.getResourcesExcludeMatcher();
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
			CorePlugin.getDefault().log(e);
		}
	}

	public IProject getIProject() {
		return project;
	}

	public IPBDotProjectOwner getPBDotProjectOwner() {
		return null;
	}

	public IPBDotProjectOwner getPBDotProjectOwner(IResource resource) {
		return null;
	}

	public IResource getUnderlyingResource() {
		return null;
	}

	public ILocalizedPath localizedRelativeResourcePath(IPBDotProjectOwner pbDotProjectOwner, IResource resource) {
		return null;
	}

}