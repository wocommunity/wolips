/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.projectbuild.builder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.cayenne.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.listener.PatternsetDeltaVisitor;
import org.objectstyle.wolips.datasets.pattern.IStringMatcher;
import org.objectstyle.wolips.datasets.pattern.PatternsetMatcher;
import org.objectstyle.wolips.datasets.pattern.StringUtilities;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;
import org.objectstyle.wolips.projectbuild.natures.IncrementalNature;
import org.objectstyle.wolips.projectbuild.util.ResourceUtilities;
/**
 * @author Harald Niesche
 * 
 * The incremental builder creates the build/ProjectName.woa or
 * build/ProjectName.framework folder that contains an approximation of the
 * structure needed to run a WebObjects application or use a framework
 */
public class WOIncrementalBuilder extends IncrementalProjectBuilder {
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
	static String _getArg(Map values, String key, String defVal) {
		String result = null;
		try {
			result = (String) values.get(key);
		} catch (Exception up) {
			// hmm, how did that get there?
		}
		if (null == result)
			result = defVal;
		return result;
	}
	/**
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, Map,
	 *      IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (null == monitor) {
			monitor = new NullProgressMonitor();
		}
		//monitor = new SubProgressMonitor (monitor, 100*1000);
		//System.out.println("<incremental build>");
		monitor.beginTask("building WebObjects layout ...", 100);
		try {
			IResourceDelta delta = getDelta(getProject());
			delta.accept(new PatternsetDeltaVisitor());
			//System.out.println(delta);
			boolean fullBuild = (null != delta) && (kind == FULL_BUILD);
			if (null != this._buildVisitor) {
				fullBuild = this._buildVisitor.setBuildArgs(args) || fullBuild;
			} else {
				fullBuild = true;
			}
			this._principalClass = _getArg(args, ProjectBuildPlugin.NS_PRINCIPAL_CLASS, "");
			if (this._principalClass.length() == 0) {
				this._principalClass = null;
			}
			if ((null != this._buildVisitor) && !fullBuild) {
				monitor.subTask("checking directory structure ...");
				if (!this._buildVisitor._checkDirs()) {
					delta = null;
					monitor.worked(5);
				}
			} else {
				delta = null;
				if (null == this._buildVisitor) {
					this._buildVisitor = new WOBuildVisitor(monitor, getProject(),
							args);
				} else {
					this._buildVisitor.reset();
				}
				//long t0 = System.currentTimeMillis();
				delta = null;
				IFolder buildFolder = getProject().getFolder("build");
				monitor.subTask("scrubbing build folder ...");
				buildFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
				monitor.worked(1);
				//System.out.println("refresh build folder took: "
				//		+ (System.currentTimeMillis() - t0) + " ms");
				//t0 = System.currentTimeMillis();
				buildFolder.delete(true, false, null);
				monitor.worked(2);
				//System.out.println("scrubbing build folder took: "
				//		+ (System.currentTimeMillis() - t0) + " ms");
				//t0 = System.currentTimeMillis();
				buildFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
				monitor.subTask("re-creating structure ...");
				this._buildVisitor._checkDirs();
				monitor.worked(2);
				//System.out.println("re-creating build folder took: "
				//		+ (System.currentTimeMillis() - t0) + " ms");
			}
			monitor.subTask("creating Info.plist");
			_createInfoPlist();
			monitor.worked(1);
			if ((null != delta) && (null != this._buildVisitor)) {
				//System.out.println("<partial build>");
				monitor.subTask("preparing partial build");
				//long t0 = System.currentTimeMillis();
				this._buildVisitor.resetCount();
				delta.accept(this._buildVisitor, IResourceDelta.ALL_WITH_PHANTOMS);
				//System.out.println("delta.accept with " + _buildVisitor.count
				//		+ " delta nodes took: "
				//		+ (System.currentTimeMillis() - t0) + " ms");
				//System.out.println("</partial build>");
				monitor.worked(12);
			} else {
				//System.out.println("<full build>");
				monitor.subTask("preparing full build");
				//long t0 = System.currentTimeMillis();
				//t0 = System.currentTimeMillis();
				this._buildVisitor.resetCount();
				getProject().accept(this._buildVisitor);
				//System.out.println("preparing with " + _buildVisitor.count
				//		+ " project nodes took: "
				//		+ (System.currentTimeMillis() - t0) + " ms");
				//System.out.println("</full build>");
				monitor.worked(12);
			}
			//long t0 = System.currentTimeMillis();
			this._buildVisitor.executeTasks(monitor);
			//System.out.println("building structure took: "
			//		+ (System.currentTimeMillis() - t0) + " ms");
			//t0 = System.currentTimeMillis();
			monitor.subTask("copying classes");
			_jarBuild(delta, monitor);
			////System.out.println("copying classes took:
			// "+(System.currentTimeMillis()-t0)+" ms");
			monitor.done();
		} catch (RuntimeException up) {
			up.printStackTrace();
			throw up;
		} catch (CoreException up) {
			up.printStackTrace();
			throw up;
		}
		//System.out.println("</incremental build>");
		return null;
	}
	private void _createInfoPlist() throws CoreException {
		Project project = (Project) (this.getProject())
		.getAdapter(Project.class);
		IncrementalNature won = (IncrementalNature) project.getIncrementalNature();
		HashMap customInfo = null;
		IFile cipl = this.getProject().getFile("CustomInfo.plist");
		if (cipl.exists()) {
			try {
				Object o = PropertyListSerialization.propertyListFromFile(cipl
						.getLocation().toFile());
				//HashMap hash =
				////System.out.println ("PLS.pLFS: "+o);
				if (o instanceof HashMap) {
					customInfo = (HashMap) o;
					ResourceUtilities
							.unmarkResource(cipl, ProjectBuildPlugin.MARKER_BUILD_PROBLEM);
				} else {
					ResourceUtilities
							.markResource(
									cipl,
									ProjectBuildPlugin.MARKER_BUILD_PROBLEM,
									IMarker.SEVERITY_WARNING,
									"Cayenne parser can't parse this file (comments are not supported for now)",
									"unknown");
				}
				//      } catch (ParseException pe) {
				//        //System.out.println("parsing CustomInfo.plist:");
				//        
				//        pe.printStackTrace();
				//        int location = pe.currentToken.beginLine;
				//        IMarker marker = ResourceUtilities.markResource(cipl,
				// MARKER_BUILD_PROBLEM, IMarker.SEVERITY_WARNING,
				// pe.getMessage(), "unknown");
			} catch (Throwable up) {
				//System.out.println("parsing CustomInfo.plist:");
				up.printStackTrace();
				ResourceUtilities.markResource(cipl, ProjectBuildPlugin.MARKER_BUILD_PROBLEM,
						IMarker.SEVERITY_WARNING, up.getMessage(), "unknown");
			}
		}
		String infoPlist;
		if (won.isFramework()) {
			infoPlist = INFO_PLIST_FRAMEWORK;
		} else {
			infoPlist = INFO_PLIST_APPLICATION;
		}
		infoPlist = StringUtilities.replace(infoPlist, "$$name$$", won
				.getResultName());
		infoPlist = StringUtilities.replace(infoPlist, "$$basename$$",
				getProject().getName());
		infoPlist = StringUtilities.replace(infoPlist, "$$res$$", won
				.getResourceName().toString());
		infoPlist = StringUtilities.replace(infoPlist, "$$wsr$$", won
				.getWebResourceName().toString());
		infoPlist = StringUtilities.replace(infoPlist, "$$type$$", won
				.isFramework() ? "FMWK" : "APPL");
		String principalClass = (this._principalClass == null && customInfo != null)
				? (String) customInfo.get("NSPrincipalClass")
				: this._principalClass;
		if (principalClass != null) {
			String principal = "  <key>NSPrincipalClass</key>" + "\r\n"
					+ "  <string>" + principalClass + "</string>" + "\r\n";
			infoPlist = StringUtilities.replace(infoPlist,
					"$$principalclass$$", principal);
		}
		IPath infoPath = won.getInfoPath().append("Info.plist");
		IFile resFile = getProject().getWorkspace().getRoot().getFile(infoPath);
		resFile.delete(true, false, null);
		try {
			InputStream is = new ByteArrayInputStream(infoPlist
					.getBytes("UTF-8"));
			resFile.create(is, true, null);
			resFile.setDerived(true);
		} catch (UnsupportedEncodingException uee) {
			// shouldn't happen anyway, since utf8 must be supported by every
			// JVM
			uee.printStackTrace();
		}
	}
	private void _jarBuild(IResourceDelta delta, IProgressMonitor m)
			throws CoreException {
		//System.out.println("<jar build>");
		WOJarBuilder jarBuilder = new WOJarBuilder(m, getProject());
		//long t0 = System.currentTimeMillis();
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
		//System.out.println("prepare jar copy took "
		//		+ (System.currentTimeMillis() - t0) + " ms");
		m.worked(10);
		//t0 = System.currentTimeMillis();
		jarBuilder.executeTasks(m);
		//System.out.println("executing jar copy took "
		//		+ (System.currentTimeMillis() - t0) + " ms");
		//System.out.println("</jar build>");
	}
	private IJavaProject getJavaProject() {
		try {
			return ((IJavaProject) (getProject().getNature(JavaCore.NATURE_ID)));
		} catch (CoreException up) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(up);
		}
		return null;
	}
	/**
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#startupOnInitialize()
	 */
	protected void startupOnInitialize() {
		//try {
			//IJavaProject javaProject = getJavaProject();
			//System.out.println(javaProject.getOutputLocation());
		//} catch (Throwable up) {
		//}
		//super.startupOnInitialize();
	}
	WOBuildVisitor _buildVisitor = null;
	static abstract class WOBuildHelper extends ResourceUtilities
			implements
				IResourceDeltaVisitor,
				IResourceVisitor {
		/**
		 * @author ulrich
		 * 
		 * To change the template for this generated type comment go to
		 * Window>Preferences>Java>Code Generation>Code and Comments
		 */
		public static interface Buildtask {
			/**
			 * @return
			 */
			public int amountOfWork();
			/**
			 * @param m
			 * @throws CoreException
			 */
			public void doWork(IProgressMonitor m) throws CoreException;
		}
		/**
		 * @author ulrich
		 * 
		 * To change the template for this generated type comment go to
		 * Window>Preferences>Java>Code Generation>Code and Comments
		 */
		public static abstract class BuildtaskAbstract implements Buildtask {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper.Buildtask#amountOfWork()
			 */
			/**
			 * @return Returns the amount of work.
			 */
			public int amountOfWork() {
				return this._workAmount;
			}
			protected int _workAmount = 1000;
		}
		/**
		 * @author ulrich
		 * 
		 * To change the template for this generated type comment go to
		 * Window>Preferences>Java>Code Generation>Code and Comments
		 */
		public static class CopyTask extends BuildtaskAbstract {
			/**
			 * @param res
			 * @param destination
			 * @param msgPrefix
			 */
			public CopyTask(IResource res, IPath destination, String msgPrefix) {
				this._res = res;
				this._dest = destination;
				this._msgPrefix = msgPrefix;
				this._workAmount = 1000;
				//        if (res instanceof IFile) {
				//          File localFile = ((IFile)res).getLocation().toFile();
				//      
				//          if (localFile.exists()) {
				//            _workAmount = (int)localFile.length(); // if the file is
				// larger than 2G, we have a problem anyway
				//          }
				//        }
			}
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper.Buildtask#doWork(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public void doWork(IProgressMonitor m) throws CoreException {
				String error = null;
				try {
					int n = this._dest.segmentCount() - 3;
					IPath dstShortened = this._dest;
					if (n > 0) {
						dstShortened = this._dest.removeFirstSegments(n);
					}
					m.subTask("create " + dstShortened);
					ResourceUtilities.copyDerived(this._res, this._dest, m);
				} catch (CoreException up) {
					error = " *failed* to copy resource " + this._res + " -> "
							+ this._dest + " (" + up.getMessage() + ")";
					//System.out.println(_msgPrefix + error);
					//          up.printStackTrace();
					//          m.setCanceled(true);
					//throw up;
				} catch (RuntimeException up) {
					error = " *failed* to copy resource " + this._res + " -> "
							+ this._dest + " (" + up.getMessage() + ")";
					//System.out.println(_msgPrefix + error);
					//          up.printStackTrace();
					//          throw up;
				}
				if (null == error) {
					//_res.deleteMarkers(IMarker.PROBLEM, true, 1);
					this._res.deleteMarkers(ProjectBuildPlugin.MARKER_BUILD_PROBLEM, true, 0);
				} else {
					markResource(this._res, ProjectBuildPlugin.MARKER_BUILD_PROBLEM,
							IMarker.SEVERITY_ERROR, error, this._dest.toString());
				}
			}
			IResource _res;
			IPath _dest;
			String _msgPrefix;
		}
		/**
		 * @author ulrich
		 * 
		 * To change the template for this generated type comment go to
		 * Window>Preferences>Java>Code Generation>Code and Comments
		 */
		public static class DeleteTask extends BuildtaskAbstract {
			/**
			 * @param path
			 * @param msgPrefix
			 */
			public DeleteTask(IPath path, String msgPrefix) {
				this._workAmount = 1000;
				this._path = path;
				this._msgPrefix = msgPrefix;
			}
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper.Buildtask#doWork(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public void doWork(IProgressMonitor m) throws CoreException {
				if (this._path == null) {
					// this really really should not happen! (again ...)
					throw new OperationCanceledException(
							"(deleting a null path wipes the workspace)");
				}
				IResource res = getWorkspaceRoot().findMember(this._path);
				if (null != res) {
					res.refreshLocal(IResource.DEPTH_ONE, m);
				}
				IFile theFile = getWorkspaceRoot().getFile(this._path);
				IContainer theFolder = getWorkspaceRoot().getFolder(this._path);
				if (null != theFile) {
					////System.out.println (_msgPrefix+" delete "+_path);
					m.subTask("delete " + this._path);
					theFile.delete(true, true, null);
				} else if ((null != theFolder)
						&& (theFolder instanceof IFolder)) {
					//System.out.println(_msgPrefix + " delete " + _path);
					m.subTask("delete " + this._path);
					((IFolder) theFolder).delete(true, true, null);
				}
				/*
				 * if (theFile.exists()) { if (theFile.isFile()) {
				 * //System.out.println (_msgPrefix+" delete "+_path);
				 * theFile.delete(); } else if (theFile.isDirectory()) {
				 * //System.out.println ("*** not deleting folder: "+theFile); } }
				 */
				/*
				 * if ((null != res) && res.exists()) { //System.out.println
				 * (_msgPrefix+" delete "+_path); res.delete (true, m); } else {
				 * //System.out.println (_msgPrefix+" delete (not) "+_path); }
				 */
			}
			IPath _path;
			String _msgPrefix;
		}
		/**
		 * @param monitor
		 * @param project
		 * @throws CoreException
		 */
		public WOBuildHelper(IProgressMonitor monitor, IProject project)
				throws CoreException {
			this._monitor = monitor;
			this._project = project;
			Project wolipsProject = (Project) (project)
			.getAdapter(Project.class);
			this._woNature = (IncrementalNature) wolipsProject.getIncrementalNature();
			this._buildPath = this._woNature.getBuildPath();
			this._distPath = new Path("dist");
			this._resultMatcher = new PatternsetMatcher(new String[] {"*.woa", "*.framework"});
		}
		/**
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			return _visitResource(delta.getResource(), delta);
		}
		/**
		 * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
		 */
		public boolean visit(IResource resource) throws CoreException {
			return _visitResource(resource, null);
		}
		private boolean _visitResource(IResource res, IResourceDelta delta)
				throws CoreException {
			IPath resPath = res.getProjectRelativePath();
			if (this._buildPath.isPrefixOf(resPath) || this._distPath.isPrefixOf(resPath)
					|| this._resultMatcher.match(resPath.toString())) {
				return false;
			}
			handleResource(res, delta);
			return true;
		}
		/**
		 * @param task
		 */
		public synchronized void addTask(Buildtask task) {
			this._buildTasks.add(task);
			this._buildWork += task.amountOfWork();
		}
		/**
		 * @param m
		 * @throws CoreException
		 */
		public void executeTasks(IProgressMonitor m) throws CoreException {
			List tasks = null;
			int amountOfWork = 0;
			synchronized (this) {
				tasks = this._buildTasks;
				this._buildTasks = new ArrayList();
				amountOfWork = this._buildWork;
				this._buildWork = 0;
			}
			m = new SubProgressMonitor(m, 41);
			m.beginTask("building ...", amountOfWork);
			Iterator iter = tasks.iterator();
			while (iter.hasNext()) {
				Buildtask thisTask = (Buildtask) iter.next();
				thisTask.doWork(m);
				m.worked(thisTask.amountOfWork());
				if (m.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
			m.done();
		}
		abstract void handleResource(IResource res, IResourceDelta delta)
				throws CoreException;
		protected IProgressMonitor _monitor;
		protected IProject _project;
		protected IPath _buildPath;
		protected IPath _distPath;
		protected IStringMatcher _resultMatcher;
		protected IncrementalNature _woNature = null;
		private List _buildTasks = new ArrayList();
		private int _buildWork = 0;
	}
	static class WOJarBuilder extends WOBuildHelper {
		/**
		 * @param monitor
		 * @param project
		 * @throws CoreException
		 */
		public WOJarBuilder(IProgressMonitor monitor, IProject project)
				throws CoreException {
			super(monitor, project);
			this._outPath = this._woNature.getJavaOutputPath();
			this._compilerOutPath = this._woNature.getJavaProject().getOutputLocation();
			this._baseSegments = this._compilerOutPath.segmentCount();
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper#handleResource(org.eclipse.core.resources.IResource,
		 *      org.eclipse.core.resources.IResourceDelta)
		 */
		/**
		 * @param resource
		 * @param delta
		 * @throws CoreException
		 */
		public void handleResource(IResource resource, IResourceDelta delta)
				throws CoreException {
			Project adaptedProject = (Project) resource.getProject().getAdapter(Project.class);
			if (adaptedProject.matchesClassesPattern(resource)) {
				IPath path = resource.getFullPath();
				if (this._compilerOutPath.isPrefixOf(path)
						&& !this._outPath.isPrefixOf(path)) {
					IPath cp = path.removeFirstSegments(this._baseSegments);
					path = this._outPath.append(cp);
					if ((null != delta)
							&& (delta.getKind() == IResourceDelta.REMOVED)) {
						addTask(new DeleteTask(path, "jar"));
					} else {
						addTask(new CopyTask(resource, path, "jar"));
					}
				}
			}
		}
		int _baseSegments;
		IPath _outPath;
		IPath _compilerOutPath;
	}
	static class WOBuildVisitor extends WOBuildHelper {
		WOBuildVisitor(IProgressMonitor monitor, IProject project, Map args)
				throws CoreException {
			super(monitor, project);
			this.adaptedProject = (Project) project.getAdapter(Project.class);
			try {
				IJavaProject jp = this._woNature.getJavaProject();
				this._outputPath = jp.getOutputLocation();
				this._checkJavaOutputPath = !this._outputPath.equals(jp.getPath());
			} catch (CoreException up) {
				this._outputPath = new Path("/dummy");
			}
			setBuildArgs(args);
		}
		/**
		 * @param args
		 * @return true if new args, false if args were not changed
		 */
		public boolean setBuildArgs(Map args) {
			if (this._buildArgs == null || !this._buildArgs.equals(args)) {
				this._buildArgs = args;
				return true;
			}
			return false;
		}
		/**
		 *  
		 */
		public void reset() {
			this._destinations.clear();
		}
		/**
		 *  
		 */
		public void resetCount() {
			this.count = 0;
		}
		/**
		 * @param res
		 * @param delta
		 * @param copyToPath
		 * @return
		 * @throws CoreException
		 */
		public boolean _checkResource(IResource res, IResourceDelta delta,
				IPath copyToPath) throws CoreException {
			boolean result;
			if (null == copyToPath) {
				unmarkResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE);
				return false;
			}
			IResource src = (IResource) this._destinations.get(copyToPath);
			boolean deleted = (null != delta)
					&& (delta.getKind() == IResourceDelta.REMOVED);
			if (null == src) {
				if (!deleted) {
					this._destinations.put(copyToPath, res);
				}
				result = true;
			} else if (src.equals(res)) {
				if (deleted) {
					this._destinations.remove(copyToPath);
				}
				result = true;
			} else {
				if (!deleted) {
					IPath shortened = copyToPath.removeFirstSegments(2);
					String message = "duplicate resource for destination .../"
							+ shortened.toString();
					//System.out.println("** " + message);
					markResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE,
							IMarker.SEVERITY_ERROR, message, src.getFullPath()
									.toString());
					result = false; // ignore this one, it's a duplicate
				} else {
					result = true;
				}
			}
			if (result && !deleted) {
				unmarkResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE);
			}
			return result;
		}
		boolean _checkDirs() throws CoreException {
			IPath buildPath = this._woNature.getBuildPath();
			IPath resPath = this._woNature.getResourceOutputPath();
			IPath javaPath = this._woNature.getJavaOutputPath();
			IPath webresPath = this._woNature.getWebResourceOutputPath();
			boolean result = checkDerivedDir(buildPath, null);
			result = checkDerivedDir(resPath, null) && result;
			result = checkDerivedDir(javaPath, null) && result;
			result = checkDerivedDir(webresPath, null) && result;
			this._buildPath = buildPath;
			return (result);
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper#handleResource(org.eclipse.core.resources.IResource,
		 *      org.eclipse.core.resources.IResourceDelta)
		 */
		public void handleResource(IResource res, IResourceDelta delta)
				throws CoreException {
			++this.count;
			IPath fullPath = res.getFullPath();
			boolean ignore = false;
			// ignore resources already in build folder
			if (this._buildPath.isPrefixOf(fullPath)) {
				ignore = true;
			}
			// ignore resources copied to the Java output folder
			if (!ignore && this._checkJavaOutputPath
					&& this._outputPath.isPrefixOf(fullPath)) {
				ignore = true;
			}
			boolean handled = false;
			if (!ignore) {
				if (this.adaptedProject.matchesResourcesPattern(res)) {
					IPath dest = this._woNature.asResourcePath(res.getFullPath(),
							res);
					if (_checkResource(res, delta, dest)) {
						handled = _handleResource(res, delta, dest);
					} else {
						handled = true;
					}
				}
				if (this.adaptedProject.matchesWOAppResourcesPattern(res)) {
					IPath dest = this._woNature.asWebResourcePath(res.getFullPath(),
							res);
					if (_checkResource(res, delta, dest)) {
						handled = _handleResource(res, delta, dest);
					} else {
						handled = true;
					}
				}
			}
			if (!handled) {
				////System.out.println("//ignore: "+res);
				unmarkResource(res, ProjectBuildPlugin.MARKER_BUILD_DUPLICATE);
			}
		}
		/**
		 * @param res
		 * @param delta
		 * @param copyToPath
		 * @return         
		 */
		public boolean _handleResource(IResource res, IResourceDelta delta,
				IPath copyToPath) {
			if (null == copyToPath)
				return false;
			if ((null != delta) && (delta.getKind() == IResourceDelta.REMOVED)) {
				addTask(new DeleteTask(copyToPath, "build"));
			} else {
				addTask(new CopyTask(res, copyToPath, "build"));
			}
			return true;
			/*
			 * IPath resPath = res.getFullPath(); if
			 * (!_outputPath.isPrefixOf(resPath)) { //System.out.println(res); if
			 * (null != delta) //System.out.println(delta); }
			 */
		}
		IPath _outputPath = null;
		//IPath _buildPath = null;
		boolean _checkJavaOutputPath = false;
		Map _buildArgs = null;
		int count = 0;
		// key: IPath/destination, value: IResource/source
		private Map _destinations = new HashMap();
		private Project adaptedProject = null;
	}
	String _principalClass = null;
	static final String INFO_PLIST_APPLICATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "\r\n"
			//+"<!DOCTYPE plist SYSTEM
			// \"file://localhost/System/Library/DTDs/PropertyList.dtd\">" +
			// "\r\n"
			+ "<plist version=\"0.9\">"
			+ "\r\n"
			+ "<dict>"
			+ "\r\n"
			+ "  <key>NOTE</key>"
			+ "\r\n"
			+ "  <string>"
			+ "\r\n"
			+ "    Please, feel free to change this file "
			+ "\r\n"
			+ "    -- It was generated by the WOLips incremental builder and "
			+ "\r\n"
			+ "    *will be overwritten* anyway.."
			+ "\r\n"
			+ "  </string>"
			+ "\r\n"
			+ "  <key>CFBundleDevelopmentRegion</key>"
			+ "\r\n"
			+ "  <string>English</string>"
			+ "\r\n"
			+ "  <key>CFBundleExecutable</key>"
			+ "\r\n"
			+ "  <string>$$basename$$</string>"
			+ "\r\n"
			+ "  <key>CFBundleIconFile</key>"
			+ "\r\n"
			+ "  <string>WOAfile.icns</string>"
			+ "\r\n"
			+ "  <key>CFBundleInfoDictionaryVersion</key>"
			+ "\r\n"
			+ "  <string>6.0</string>"
			+ "\r\n"
			+ "  <key>CFBundlePackageType</key>"
			+ "\r\n"
			+ "  <string>APPL</string>"
			+ "\r\n"
			+ "  <key>CFBundleSignature</key>"
			+ "\r\n"
			+ "  <string>webo</string>"
			+ "\r\n"
			+ "  <key>CFBundleVersion</key>"
			+ "\r\n"
			+ "  <string>0.0.1d1</string>"
			+ "\r\n"
			+ "  <key>NSExecutable</key>"
			+ "\r\n"
			+ "  <string>$$basename$$</string>"
			+ "\r\n"
			+ "  <key>NSJavaNeeded</key>"
			+ "\r\n"
			+ "  <true/>"
			+ "\r\n"
			+ "  <key>NSJavaPath</key>"
			+ "\r\n"
			+ "  <array>"
			+ "\r\n"
			+ "    <string>$$basename$$.jar</string>"
			+ "\r\n"
			+ "  </array>"
			+ "\r\n"
			+ "  <key>NSJavaPathClient</key>"
			+ "\r\n"
			+ "  <string>$$basename$$.jar</string>"
			+ "\r\n"
			+ "  <key>NSJavaRoot</key>"
			+ "\r\n"
			+ "  <string>Contents/Resources/Java</string>"
			+ "\r\n"
			+ "  <key>NSJavaRootClient</key>"
			+ "\r\n"
			+ "  <string>Contents/WebServerResources/Java</string>"
			+ "\r\n"
			+ "$$principalclass$$" + "</dict>" + "\r\n" + "</plist>" + "\r\n";
	static final String INFO_PLIST_FRAMEWORK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "\r\n"
			+ "<plist version=\"0.9\">"
			+ "\r\n"
			+ "<dict>"
			+ "\r\n"
			+ "  <key>NOTE</key>"
			+ "\r\n"
			+ "  <string>"
			+ "\r\n"
			+ "    Please, feel free to change this file "
			+ "\r\n"
			+ "    -- It was generated by the WOLips incremental builder and "
			+ "\r\n"
			+ "    *will be overwritten* anyway.."
			+ "\r\n"
			+ "  </string>"
			+ "\r\n"
			+ "  <key>NSJavaPathClient</key>"
			+ "\r\n"
			+ "  <string>theTestFramework.jar</string>"
			+ "\r\n"
			+ "  <key>CFBundleIconFile</key>"
			+ "\r\n"
			+ "  <string>WOAfile.icns</string>"
			+ "\r\n"
			+ "  <key>CFBundleExecutable</key>"
			+ "\r\n"
			+ "  <string>$$basename$$</string>"
			+ "\r\n"
			+ "  <key>NSJavaRoot</key>"
			+ "\r\n"
			+ "  <string>$$res$$/Java</string>"
			+ "\r\n"
			+ "  <key>NSJavaRootClient</key>"
			+ "\r\n"
			+ "  <string>$$wsr$$/Java</string>"
			+ "\r\n"
			+ "  <key>NSJavaNeeded</key>"
			+ "\r\n"
			+ "  <true/>"
			+ "\r\n"
			+ "  <key>CFBundleName</key>"
			+ "\r\n"
			+ "  <string></string>"
			+ "\r\n"
			+ "  <key>NSExecutable</key>"
			+ "\r\n"
			+ "  <string>$$basename$$</string>"
			+ "\r\n"
			+ "  <key>NSJavaPath</key>"
			+ "\r\n"
			+ "  <array>"
			+ "\r\n"
			+ "    <string>$$basename$$.jar</string>"
			+ "\r\n"
			+ "  </array>"
			+ "\r\n"
			+ "  <key>CFBundleInfoDictionaryVersion</key>"
			+ "\r\n"
			+ "  <string>6.0</string>"
			+ "\r\n"
			+ "  <key>Has_WOComponents</key>"
			+ "\r\n"
			+ "  <true/>"
			+ "\r\n"
			+ "  <key>CFBundleSignature</key>"
			+ "\r\n"
			+ "  <string>webo</string>"
			+ "\r\n"
			+ "  <key>CFBundleShortVersionString</key>"
			+ "\r\n"
			+ "  <string></string>"
			+ "\r\n"
			+ "  <key>CFBundleIdentifier</key>"
			+ "\r\n"
			+ "  <string></string>"
			+ "\r\n"
			+ "  <key>CFBundlePackageType</key>"
			+ "\r\n"
			+ "  <string>$$type$$</string>"
			+ "\r\n"
			+ "$$principalclass$$"
			+ "</dict>" + "\r\n" + "</plist>" + "\r\n";
}