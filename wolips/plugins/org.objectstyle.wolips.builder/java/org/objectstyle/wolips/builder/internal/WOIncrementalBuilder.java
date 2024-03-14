/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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
package org.objectstyle.wolips.builder.internal;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.baseforplugins.AbstractBaseActivator;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.types.folder.IWoprojectAdapter;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.templateengine.ProjectInput;
import org.objectstyle.wolips.templateengine.ProjectTemplate;
import org.objectstyle.wolips.variables.BuildProperties;

/**
 * @author Harald Niesche The incremental builder creates the
 *         build/ProjectName.woa or build/ProjectName.framework folder that
 *         contains an approximation of the structure needed to run a WebObjects
 *         application or use a framework
 */
public class WOIncrementalBuilder extends AbstractIncrementalProjectBuilder {
	private BuildVisitor _buildVisitor;

	private JarBuilder _jarBuilder;

	/**
	 * Constructor for WOProjectBuilder.
	 */
	public WOIncrementalBuilder() {
		super();
	}

	public boolean isEnabled() {
		return true;
	}

	/*
	 * this is duplicated from ProjectNaturePage, couldn't find a good place for
	 * now
	 */
	private String getArg(Map values, String key, String defVal) {
		String result = null;
		try {
			result = (String) values.get(key);
		} catch (Exception up) {
			getLogger().log(up);
		}
		if (null == result)
			result = defVal;
		return result;
	}

	public void invokeOldBuilder(int kind, Map args, IProgressMonitor progressMonitor, IResourceDelta resourceDelta) throws Exception {
		if (!Preferences.mockBundleEnabled()) {
			// just don't do anything here
			return;
		}

		IResourceDelta delta = resourceDelta;
		if (kind != IncrementalProjectBuilder.FULL_BUILD && kind != IncrementalProjectBuilder.CLEAN_BUILD && !projectNeedsAnUpdate(delta)) {
			return;
		}
		getLogger().debug("<incremental build>");
		
		final SubMonitor subProgressMonitor = SubMonitor.convert(progressMonitor, "building WebObjects layout ...", 100);
		
		try {
			ProjectAdapter project = this.getProject().getAdapter(ProjectAdapter.class);
			boolean fullBuild = (kind == IncrementalProjectBuilder.FULL_BUILD || kind == IncrementalProjectBuilder.CLEAN_BUILD || patternsetDeltaVisitor().isFullBuildRequired());
			String oldPrincipalClass = getArg(args, BuilderPlugin.NS_PRINCIPAL_CLASS, "");
			if (oldPrincipalClass.length() == 0) {
				oldPrincipalClass = null;
			}
			BuildProperties buildProperties = project.getBuildProperties();
			String principalClass = buildProperties.getPrincipalClass(true);
			if (principalClass == null && oldPrincipalClass != null) {
				principalClass = oldPrincipalClass;
				buildProperties.setPrincipalClass(principalClass);
			}
			
			if (buildProperties.getWOVersion().isAtLeastVersion(5, 6)) {
				IContainer infoPListContainer = getProject().getFolder(IWoprojectAdapter.FOLDER_NAME);
				createInfoPlist(infoPListContainer);
			}
			else {
				if (_buildVisitor == null) {
					_buildVisitor = new BuildVisitor();
				}
				_buildVisitor.reinitForNextBuild(this.getProject());
				if (!fullBuild) {
					subProgressMonitor.subTask("checking directory structure ...");
					if (!_buildVisitor._checkDirs()) {
						delta = null;
						subProgressMonitor.worked(5);
					}
				} else {
					delta = null;
					long t0 = System.currentTimeMillis();
					IFolder buildFolder = getProject().getFolder("build");
					subProgressMonitor.subTask("scrubbing build folder ...");
					buildFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
					subProgressMonitor.worked(1);
					getLogger().debug("refresh build folder took: " + (System.currentTimeMillis() - t0) + " ms");
					t0 = System.currentTimeMillis();
					buildFolder.delete(true, false, null);
					subProgressMonitor.worked(2);
					getLogger().debug("scrubbing build folder took: " + (System.currentTimeMillis() - t0) + " ms");
					t0 = System.currentTimeMillis();
					buildFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
					subProgressMonitor.subTask("re-creating structure ...");
					_buildVisitor._checkDirs();
					subProgressMonitor.worked(2);
					getLogger().debug("re-creating build folder took: " + (System.currentTimeMillis() - t0) + " ms");
				}
				subProgressMonitor.subTask("creating Info.plist");
				IContainer infoPListContainer = getProject().getWorkspace().getRoot().getFolder(_buildVisitor.getInfoPath());
				createInfoPlist(infoPListContainer);
				subProgressMonitor.worked(1);
				if ((null != delta)) {
					getLogger().debug("<partial build>");
					subProgressMonitor.subTask("preparing partial build");
					long t0 = System.currentTimeMillis();
					_buildVisitor.resetCount();
					delta.accept(_buildVisitor, IResourceDelta.ALL_WITH_PHANTOMS);
					getLogger().debug("delta.accept with " + _buildVisitor.getCount() + " delta nodes took: " + (System.currentTimeMillis() - t0) + " ms");
					getLogger().debug("</partial build>");
					subProgressMonitor.worked(12);
				} else {
					getLogger().debug("<full build>");
					subProgressMonitor.subTask("preparing full build");
					long t0 = System.currentTimeMillis();
					t0 = System.currentTimeMillis();
					_buildVisitor.resetCount();
					getProject().accept(_buildVisitor);
					getLogger().debug("preparing with " + _buildVisitor.getCount() + " project nodes took: " + (System.currentTimeMillis() - t0) + " ms");
					getLogger().debug("</full build>");
					subProgressMonitor.worked(12);
				}
				long t0 = System.currentTimeMillis();
				_buildVisitor.executeTasks(subProgressMonitor);
				getLogger().debug("building structure took: " + (System.currentTimeMillis() - t0) + " ms");
				t0 = System.currentTimeMillis();
				subProgressMonitor.subTask("copying classes");
				jarBuild(delta, subProgressMonitor, getProject());
				getLogger().debug("copying classes took: " + (System.currentTimeMillis() - t0) + " ms");
				subProgressMonitor.done();
			}
		} catch (Exception up) {
			getLogger().log(up);
			throw up;
		}
		getLogger().debug("</incremental build>");
	}

	protected void createInfoPlist(IContainer targetContainer) throws Exception {
		ProjectTemplate infoPListTemplate;
		IProject project = getProject();
		ProjectAdapter projectAdapter = project.getAdapter(ProjectAdapter.class);
		if (projectAdapter.isFramework()) {
			infoPListTemplate = ProjectTemplate.loadProjectTemplateNamed("MiscTemplates", "FrameworkInfoPList");
		}
		else {
			infoPListTemplate = ProjectTemplate.loadProjectTemplateNamed("MiscTemplates", "ApplicationInfoPList");
		}
		infoPListTemplate.addInput(new ProjectInput("buildProperties", projectAdapter.getBuildProperties()));
		
		targetContainer.refreshLocal(IResource.DEPTH_ZERO, null);
		infoPListTemplate.createProjectContents(project, targetContainer, new NullProgressMonitor());
		IFile infoPList = targetContainer.getFile(new Path("Info.plist"));
		infoPList.refreshLocal(IResource.DEPTH_ZERO, null);
		infoPList.setDerived(true, null);
	}

	private AbstractBaseActivator getLogger() {
		return BuilderPlugin.getDefault();
	}

	private void jarBuild(IResourceDelta delta, IProgressMonitor monitor, IProject project) throws CoreException {
		getLogger().debug("<jar build>");
		if (_jarBuilder == null)
			_jarBuilder = new JarBuilder();
		_jarBuilder.reinitForNextBuild(project);
		long t0 = System.currentTimeMillis();
		if (null != delta) {
			delta.accept(_jarBuilder, IResourceDelta.ALL_WITH_PHANTOMS);
		} else {
			IPath outPath = getJavaProject().getOutputLocation();
			IContainer output = getProject();
			if (!outPath.segment(0).equals(getProject().getName())) {
				output = getProject().getParent().getFolder(outPath);
			}
			output.accept(_jarBuilder);
		}
		getLogger().debug("prepare jar copy took " + (System.currentTimeMillis() - t0) + " ms");
		monitor.worked(10);
		t0 = System.currentTimeMillis();
		_jarBuilder.executeTasks(monitor);
		getLogger().debug("executing jar copy took " + (System.currentTimeMillis() - t0) + " ms");
		getLogger().debug("</jar build>");
	}

	private IJavaProject getJavaProject() {
		try {
			final IProject project = getProject();
			if (project.hasNature(JavaCore.NATURE_ID)) {
				return JavaCore.create(getProject());
			}
		} catch (CoreException up) {
			this.getLogger().log(up);
		}
		return null;
	}

	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#startupOnInitialize()
	 */
	protected void startupOnInitialize() {
		// try {
		// IJavaProject javaProject = getJavaProject();
		// _getLogger().debug(javaProject.getOutputLocation());
		// } catch (Throwable up) {
		// }
		// super.startupOnInitialize();
	}
}