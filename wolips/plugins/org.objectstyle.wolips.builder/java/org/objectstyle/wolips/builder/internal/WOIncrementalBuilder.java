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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.runtime.AbstractCorePlugin;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.pattern.StringUtilities;

/**
 * @author Harald Niesche The incremental builder creates the
 *         build/ProjectName.woa or build/ProjectName.framework folder that
 *         contains an approximation of the structure needed to run a WebObjects
 *         application or use a framework
 */
public class WOIncrementalBuilder extends AbstractIncrementalProjectBuilder {
	private BuildVisitor buildVisitor;

	private JarBuilder jarBuilder;

	private String principalClass;

	private String customInfoPListContent;

	private String eoAdaptorClassName;

	/**
	 * Constructor for WOProjectBuilder.
	 */
	public WOIncrementalBuilder() {
		super();
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

	public void invokeOldBuilder(int kind, Map args, IProgressMonitor progressMonitor, IResourceDelta resourceDelta) throws CoreException {
		IProgressMonitor subProgressMonitor = null;
		if (null == progressMonitor) {
			subProgressMonitor = new NullProgressMonitor();
		} else {
			subProgressMonitor = new SubProgressMonitor(progressMonitor, 100 * 1000);
		}
		IResourceDelta delta = resourceDelta;
		if (kind != IncrementalProjectBuilder.FULL_BUILD && !projectNeedsAnUpdate(delta)) {
			subProgressMonitor.done();
		}
		getLogger().debug("<incremental build>");
		subProgressMonitor.beginTask("building WebObjects layout ...", 100);
		try {
			Project project = (Project) this.getProject().getAdapter(Project.class);
			boolean fullBuild = (null != delta) && (kind == IncrementalProjectBuilder.FULL_BUILD || project.fullBuildRequired);
			project.fullBuildRequired = false;
			String oldPrincipalClass = getArg(args, BuilderPlugin.NS_PRINCIPAL_CLASS, "");
			if (oldPrincipalClass.length() == 0) {
				oldPrincipalClass = null;
			}
			principalClass = project.getPrincipalClass(true);
			if (principalClass == null && oldPrincipalClass != null) {
				principalClass = oldPrincipalClass;
				project.setPrincipalClass(principalClass);
			}
			customInfoPListContent = project.getCustomInfoPListContent(true);
			eoAdaptorClassName = project.getEOAdaptorClassName(true);
			if (buildVisitor == null) {
				buildVisitor = new BuildVisitor();
			}
			buildVisitor.reinitForNextBuild(project);
			if (!fullBuild) {
				subProgressMonitor.subTask("checking directory structure ...");
				if (!buildVisitor._checkDirs()) {
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
				buildVisitor._checkDirs();
				subProgressMonitor.worked(2);
				getLogger().debug("re-creating build folder took: " + (System.currentTimeMillis() - t0) + " ms");
			}
			subProgressMonitor.subTask("creating Info.plist");
			createInfoPlist();
			subProgressMonitor.worked(1);
			if ((null != delta)) {
				getLogger().debug("<partial build>");
				subProgressMonitor.subTask("preparing partial build");
				long t0 = System.currentTimeMillis();
				buildVisitor.resetCount();
				delta.accept(buildVisitor, IResourceDelta.ALL_WITH_PHANTOMS);
				getLogger().debug("delta.accept with " + buildVisitor.getCount() + " delta nodes took: " + (System.currentTimeMillis() - t0) + " ms");
				getLogger().debug("</partial build>");
				subProgressMonitor.worked(12);
			} else {
				getLogger().debug("<full build>");
				subProgressMonitor.subTask("preparing full build");
				long t0 = System.currentTimeMillis();
				t0 = System.currentTimeMillis();
				buildVisitor.resetCount();
				getProject().accept(buildVisitor);
				getLogger().debug("preparing with " + buildVisitor.getCount() + " project nodes took: " + (System.currentTimeMillis() - t0) + " ms");
				getLogger().debug("</full build>");
				subProgressMonitor.worked(12);
			}
			long t0 = System.currentTimeMillis();
			buildVisitor.executeTasks(subProgressMonitor);
			getLogger().debug("building structure took: " + (System.currentTimeMillis() - t0) + " ms");
			t0 = System.currentTimeMillis();
			subProgressMonitor.subTask("copying classes");
			jarBuild(delta, subProgressMonitor, project);
			getLogger().debug("copying classes took: " + (System.currentTimeMillis() - t0) + " ms");
			subProgressMonitor.done();
		} catch (RuntimeException up) {
			getLogger().log(up);
			throw up;
		} catch (CoreException up) {
			getLogger().log(up);
			throw up;
		}
		getLogger().debug("</incremental build>");
	}

	private void createInfoPlist() throws CoreException {
		Project project = (Project) (getProject()).getAdapter(Project.class);
		String infoPlist;
		if (project.isFramework()) {
			infoPlist = INFO_PLIST_FRAMEWORK;
		} else {
			infoPlist = INFO_PLIST_APPLICATION;
		}
		infoPlist = StringUtilities.replace(infoPlist, "$$name$$", buildVisitor.getResultName());
		infoPlist = StringUtilities.replace(infoPlist, "$$basename$$", getProject().getName());
		infoPlist = StringUtilities.replace(infoPlist, "$$res$$", buildVisitor.getResourceName().toString());
		infoPlist = StringUtilities.replace(infoPlist, "$$wsr$$", buildVisitor.getWebResourceName().toString());
		infoPlist = StringUtilities.replace(infoPlist, "$$type$$", buildVisitor.isFramework() ? "FMWK" : "APPL");
		if (principalClass != null && principalClass.length() > 0) {
			String string = "  <key>NSPrincipalClass</key>" + "\r\n" + "  <string>" + principalClass + "</string>" + "\r\n";
			infoPlist = StringUtilities.replace(infoPlist, "$$principalclass$$", string);
		} else {
			infoPlist = StringUtilities.replace(infoPlist, "$$principalclass$$", "");
		}
		if (customInfoPListContent != null) {
			infoPlist = StringUtilities.replace(infoPlist, "$$customInfoPListContent$$", customInfoPListContent);
		} else {
			infoPlist = StringUtilities.replace(infoPlist, "$$customInfoPListContent$$", "");
		}
		if (project.isFramework() && eoAdaptorClassName != null && eoAdaptorClassName.length() > 0) {
			String string = "  <key>EOAdaptorClassName</key>" + "\r\n" + "  <string>" + eoAdaptorClassName + "</string>" + "\r\n";
			infoPlist = StringUtilities.replace(infoPlist, "$$EOAdaptorClassName$$", string);
		} else {
			infoPlist = StringUtilities.replace(infoPlist, "$$EOAdaptorClassName$$", "");
		}
		IPath infoPath = buildVisitor.getInfoPath().append("Info.plist");
		IFile resFile = getProject().getWorkspace().getRoot().getFile(infoPath);

		byte newBytes[];
		try {
			newBytes = infoPlist.getBytes("UTF-8");
		} catch (UnsupportedEncodingException uee) {
			// shouldn't happen anyway, since utf8 must be supported by every
			// JVM
			getLogger().log(uee);
			return;
		}

		boolean changed = true;
		if (resFile.exists()) {
			boolean retry = true;
			while (retry) {
				try {
					InputStream oldContent = resFile.getContents(false);
					InputStream newContent = new ByteArrayInputStream(newBytes);
					changed = streamsAreDifferent(oldContent, newContent);
					newContent.close();
					oldContent.close();
				} catch (CoreException up) {
					resFile.refreshLocal(1, null);
				} catch (IOException e) {
					resFile.refreshLocal(1, null);
				}
				retry = false;
			}
		}
		if (changed) {
			resFile.delete(true, false, null);
			InputStream is = new ByteArrayInputStream(newBytes);
			resFile.create(is, true, null);
			resFile.setDerived(true);
		}
	}

	private boolean streamsAreDifferent(InputStream is1, InputStream is2) {
		byte buffer1[] = new byte[1024];
		byte buffer2[] = new byte[1024];
		int r1 = 0;
		int r2 = 0;
		try {
			do {
				r1 = is1.read(buffer1);
				r2 = is2.read(buffer2);
				if (r1 != r2) {
					return true;
				}
				if (r1 > -1) {
					if (r1 < buffer1.length) {
						Arrays.fill(buffer1, r1, buffer1.length, (byte) 0);
						Arrays.fill(buffer2, r2, buffer2.length, (byte) 0);
					}
					if (!Arrays.equals(buffer1, buffer2)) {
						return true;
					}
				}
			} while (r1 == r2 && r1 > 0);
		} catch (IOException up) {
			return true;
		}
		return false;
	}

	private AbstractCorePlugin getLogger() {
		return BuilderPlugin.getDefault();
	}

	private void jarBuild(IResourceDelta delta, IProgressMonitor monitor, Project project) throws CoreException {
		getLogger().debug("<jar build>");
		if (jarBuilder == null)
			jarBuilder = new JarBuilder();
		jarBuilder.reinitForNextBuild(project);
		long t0 = System.currentTimeMillis();
		if (null != delta) {
			delta.accept(jarBuilder, IResourceDelta.ALL_WITH_PHANTOMS);
		} else {
			IPath outPath = getJavaProject().getOutputLocation();
			IContainer output = getProject();
			if (!outPath.segment(0).equals(getProject().getName())) {
				output = getProject().getParent().getFolder(outPath);
			}
			output.accept(jarBuilder);
		}
		getLogger().debug("prepare jar copy took " + (System.currentTimeMillis() - t0) + " ms");
		monitor.worked(10);
		t0 = System.currentTimeMillis();
		jarBuilder.executeTasks(monitor);
		getLogger().debug("executing jar copy took " + (System.currentTimeMillis() - t0) + " ms");
		getLogger().debug("</jar build>");
	}

	private IJavaProject getJavaProject() {
		try {
			return ((IJavaProject) (getProject().getNature(JavaCore.NATURE_ID)));
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

	static final String INFO_PLIST_APPLICATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\r\n"
	// +"<!DOCTYPE plist SYSTEM
			// \"file://localhost/System/Library/DTDs/PropertyList.dtd\">" +
			// "\r\n"
			+ "<plist version=\"0.9\">" + "\r\n" + "<dict>" + "\r\n" + "  <key>NOTE</key>" + "\r\n" + "  <string>" + "\r\n" + "    Please, feel free to change this file " + "\r\n" + "    -- It was generated by the WOLips incremental builder and " + "\r\n" + "    *will be overwritten* anyway.." + "\r\n" + "  </string>" + "\r\n" + "  <key>CFBundleDevelopmentRegion</key>" + "\r\n" + "  <string>English</string>" + "\r\n" + "  <key>CFBundleExecutable</key>" + "\r\n" + "  <string>$$basename$$</string>" + "\r\n" + "  <key>CFBundleIconFile</key>" + "\r\n" + "  <string>WOAfile.icns</string>" + "\r\n" + "  <key>CFBundleInfoDictionaryVersion</key>" + "\r\n" + "  <string>6.0</string>" + "\r\n" + "  <key>CFBundlePackageType</key>" + "\r\n" + "  <string>APPL</string>" + "\r\n" + "  <key>CFBundleSignature</key>" + "\r\n" + "  <string>webo</string>" + "\r\n" + "  <key>CFBundleVersion</key>" + "\r\n" + "  <string>0.0.1d1</string>" + "\r\n" + "  <key>NSExecutable</key>" + "\r\n" + "  <string>$$basename$$</string>" + "\r\n" + "  <key>NSJavaNeeded</key>" + "\r\n" + "  <true/>" + "\r\n" + "  <key>NSJavaPath</key>" + "\r\n" + "  <array>" + "\r\n" + "    <string>$$basename$$.jar</string>" + "\r\n" + "  </array>" + "\r\n" + "  <key>NSJavaPathClient</key>" + "\r\n" + "  <string>$$basename$$.jar</string>" + "\r\n" + "  <key>NSJavaRoot</key>" + "\r\n" + "  <string>Contents/Resources/Java</string>" + "\r\n" + "  <key>NSJavaRootClient</key>" + "\r\n" + "  <string>Contents/WebServerResources/Java</string>" + "\r\n" + "$$principalclass$$" + "$$customInfoPListContent$$" + "\r\n" + "</dict>" + "\r\n" + "</plist>" + "\r\n";

	static final String INFO_PLIST_FRAMEWORK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\r\n" + "<plist version=\"0.9\">" + "\r\n" + "<dict>" + "\r\n" + "  <key>NOTE</key>" + "\r\n" + "  <string>" + "\r\n" + "    Please, feel free to change this file " + "\r\n" + "    -- It was generated by the WOLips incremental builder and " + "\r\n" + "    *will be overwritten* anyway.." + "\r\n" + "  </string>" + "\r\n" + "  <key>NSJavaPathClient</key>" + "\r\n" + "  <string>theTestFramework.jar</string>" + "\r\n" + "  <key>CFBundleIconFile</key>" + "\r\n" + "  <string>WOAfile.icns</string>" + "\r\n" + "  <key>CFBundleExecutable</key>" + "\r\n" + "  <string>$$basename$$</string>" + "\r\n" + "  <key>NSJavaRoot</key>" + "\r\n" + "  <string>$$res$$/Java</string>" + "\r\n" + "  <key>NSJavaRootClient</key>" + "\r\n" + "  <string>$$wsr$$/Java</string>" + "\r\n" + "  <key>NSJavaNeeded</key>" + "\r\n" + "  <true/>" + "\r\n" + "  <key>CFBundleName</key>" + "\r\n" + "  <string></string>" + "\r\n" + "  <key>NSExecutable</key>" + "\r\n" + "  <string>$$basename$$</string>" + "\r\n" + "  <key>NSJavaPath</key>" + "\r\n" + "  <array>" + "\r\n" + "    <string>$$basename$$.jar</string>" + "\r\n" + "  </array>" + "\r\n" + "  <key>CFBundleInfoDictionaryVersion</key>" + "\r\n" + "  <string>6.0</string>" + "\r\n" + "  <key>Has_WOComponents</key>" + "\r\n" + "  <true/>" + "\r\n" + "  <key>CFBundleSignature</key>" + "\r\n" + "  <string>webo</string>" + "\r\n" + "  <key>CFBundleShortVersionString</key>" + "\r\n" + "  <string></string>" + "\r\n" + "  <key>CFBundleIdentifier</key>" + "\r\n" + "  <string></string>" + "\r\n" + "  <key>CFBundlePackageType</key>" + "\r\n" + "  <string>$$type$$</string>" + "\r\n" + "$$principalclass$$" + "$$customInfoPListContent$$" + "$$EOAdaptorClassName$$" + "\r\n" + "</dict>" + "\r\n" + "</plist>" + "\r\n";
}