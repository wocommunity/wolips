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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.baseforplugins.AbstractBaseActivator;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.datasets.adaptable.Project;

/**
 * @author Harald Niesche
 */
public abstract class BuildHelper extends ResourceUtilities implements IResourceDeltaVisitor, IResourceVisitor {
	private Project _project;

	private IPath _distPath;

	private List _buildTasks = new ArrayList();

	private int _buildWork = 0;

	/**
	 * @author Harald Niesche
	 * 
	 * A single resource-related task (copy or delete a resource, see
	 * subclasses)
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
	 * @author Harald Niesche
	 * 
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
			return (_workAmount);
		}

		protected int _workAmount = 1000;

		protected AbstractBaseActivator _getLogger() {
			return BuilderPlugin.getDefault();
		}

	}

	/**
	 * @author Harald Niesche
	 * 
	 */
	public static class CopyTask extends BuildtaskAbstract {
		/**
		 * @param res
		 * @param destination
		 * @param msgPrefix
		 */
		public CopyTask(IResource res, IPath destination, String msgPrefix) {
			_res = res;
			_dest = destination;
			_msgPrefix = msgPrefix;
			_workAmount = 1000;
			// if (res instanceof IFile) {
			// File localFile = ((IFile)res).getLocation().toFile();
			//      
			// if (localFile.exists()) {
			// _workAmount = (int)localFile.length(); // if the file is
			// larger than 2G, we have a problem anyway
			// }
			// }
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper.Buildtask#doWork(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public void doWork(IProgressMonitor m) throws CoreException {
			String error = null;
			try {
				int n = _dest.segmentCount() - 3;
				IPath dstShortened = _dest;
				if (n > 0) {
					dstShortened = _dest.removeFirstSegments(n);
				}
				m.subTask("create " + dstShortened);
				ResourceUtilities.copyDerived(_res, _dest, m);
				_getLogger().debug("copy " + _res + " -> " + _dest);

			} catch (CoreException up) {
				error = " *failed* to copy resource " + _res + " -> " + _dest + " (" + up.getMessage() + ")";
				_getLogger().debug(_msgPrefix + error, up);
				// up.printStackTrace();
				// m.setCanceled(true);
				// throw up;
			} catch (RuntimeException up) {
				error = " *failed* to copy resource " + _res + " -> " + _dest + " (" + up.getMessage() + ")";
				_getLogger().log(_msgPrefix + error, up);
				// up.printStackTrace();
				// throw up;
			}
			if (null == error) {
				// _res.deleteMarkers(IMarker.PROBLEM, true, 1);
				_res.deleteMarkers(BuilderPlugin.MARKER_BUILD_PROBLEM, true, 0);
			} else {
				markResource(_res, BuilderPlugin.MARKER_BUILD_PROBLEM, IMarker.SEVERITY_ERROR, error, _dest.toString());
			}
		}

		IResource _res;

		IPath _dest;

		String _msgPrefix;
	}

	/**
	 * @author Harald Niesche
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
			_workAmount = 1000;
			_path = path;
			_msgPrefix = msgPrefix;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper.Buildtask#doWork(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public void doWork(IProgressMonitor m) throws CoreException {
			if (_path == null) {
				// this really really should not happen! (again ...)
				throw new OperationCanceledException("(deleting a null path wipes the workspace)");
			}
			IResource res = getWorkspaceRoot().findMember(_path);
			if (res != null) {
				res.refreshLocal(IResource.DEPTH_ONE, m);
			}
			IFile theFile = getWorkspaceRoot().getFile(_path);
			IContainer theFolder = getWorkspaceRoot().getFolder(_path);
			if (theFolder instanceof IFolder && theFolder.exists()) {
				_getLogger().debug(_msgPrefix + " delete " + _path);
				m.subTask("delete " + _path);
				((IFolder) theFolder).delete(true, true, null);
			}
			else if (theFile != null && theFile.exists()) {
				_getLogger().debug(_msgPrefix + " delete " + _path);
				m.subTask("delete " + _path);
				theFile.delete(true, true, null);
			}
		}

		IPath _path;

		String _msgPrefix;
	}

	/**
	 * The constructor
	 */
	public BuildHelper() {
		super();
	}

	/**
	 * @param project
	 */
	public void reinitForNextBuild(Project project) {
		_project = project;
		_distPath = new Path("dist");
		_buildWork = 0;
		_buildTasks = new ArrayList();
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

	private boolean _visitResource(IResource res, IResourceDelta delta) throws CoreException {
		IPath resPath = res.getProjectRelativePath();
		if (this.getBuildPath().isPrefixOf(resPath) || _distPath.isPrefixOf(resPath)) {
			return false;
		}
		boolean handleResourceChildren = handleResource(res, delta);
		return handleResourceChildren;
	}

	/**
	 * @param task
	 */
	public synchronized void addTask(Buildtask task) {
		_buildTasks.add(task);
		_buildWork += task.amountOfWork();
	}

	/**
	 * @param m
	 * @throws CoreException
	 */
	public void executeTasks(IProgressMonitor progressMonitor) throws CoreException {
		SubProgressMonitor subProgressMonitor = new SubProgressMonitor(progressMonitor, 41);
		subProgressMonitor.beginTask("building ...", _buildWork);
		Iterator iter = _buildTasks.iterator();
		while (iter.hasNext()) {
			Buildtask thisTask = (Buildtask) iter.next();
			thisTask.doWork(subProgressMonitor);
			subProgressMonitor.worked(thisTask.amountOfWork());
			if (subProgressMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		subProgressMonitor.done();
	}

	protected AbstractBaseActivator _getLogger() {
		return BuilderPlugin.getDefault();
	}

	public abstract boolean handleResource(IResource res, IResourceDelta delta) throws CoreException;

	public Project getProject() {
		return _project;
	}

	public IProject getIProject() {
		return _project.getIProject();
	}

	/**
	 * @return
	 * 
	 */
	public IJavaProject getJavaProject() {
		try {
			return ((IJavaProject) getIProject().getNature(JavaCore.NATURE_ID));
		} catch (CoreException up) {
			BuilderPlugin.getDefault().log(up);
		}

		return (null);
	}

	/**
	 * @return
	 */
	// public String getStringProperty(String key) {
	// String result = "";
	// try {
	// result =
	// getProject().getPersistentProperty(new QualifiedName("", key));
	// } catch (CoreException up) {
	// up.printStackTrace();
	// }
	//
	// return (result);
	// }
	//
	// public void setStringProperty(String key, String value) {
	// try {
	// getProject().setPersistentProperty(
	// new QualifiedName("", key),
	// value);
	// } catch (CoreException up) {
	// up.printStackTrace();
	// }
	//
	// }
	//
	// public boolean getBooleanProperty(String key) {
	// return ("true".equals(getStringProperty(key)));
	// }
	//
	// public void setBooleanProperty(String key, boolean value) {
	// setStringProperty(key, value ? "true" : "false");
	// }
	public boolean isFramework() {
		// return (getBooleanProperty(FRAMEWORK_PROPERTY));
		try {
			Project project = (Project) (this.getIProject()).getAdapter(Project.class);
			return (project.isFramework());
		} catch (CoreException up) {
			BuilderPlugin.getDefault().log(up.getStatus());
		}

		return false;
	}

	// public void setIsFramework(boolean isFramework) {
	// setBooleanProperty(FRAMEWORK_PROPERTY, isFramework);
	// }

	/**
	 * either name.woa or name.framework
	 * 
	 * @return
	 */
	public String getResultName() {
		String name = getIProject().getName();

		if (isFramework()) {
			return (name + ".framework");
		}
		return (name + ".woa");
	}

	/**
	 * @return
	 */
	public IPath getBuildPath() {
		return (getIProject().getFullPath().append("build"));
	}

	/**
	 * @return
	 */
	public IPath getResultPath() {
		return (getBuildPath().append(getResultName()));
	}

	/**
	 * @return
	 */
	public IPath getInfoPath() {
		if (isFramework()) {
			return (getResultPath().append("Resources"));
		}
		return (getResultPath().append(APPINFO_PATH));
	}

	/**
	 * @return
	 */
	public IPath _getResultPath() {
		if (isFramework()) {
			return (getResultPath());
		}
		return (getResultPath().append(APPINFO_PATH));
	}

	/**
	 * @return
	 */
	public IPath getResourceOutputPath() {
		return (_getResultPath().append(RESOURCE_PATH));
	}

	/**
	 * @return
	 */
	public IPath getJavaOutputPath() {
		return (_getResultPath().append(JAVA_PATH));
	}

	/**
	 * @return
	 */
	public IPath getWebResourceOutputPath() {
		return (_getResultPath().append(WEBRESOURCE_PATH));
	}

	/**
	 * @return
	 */
	public String getResourceName() {
		String result = "";
		if (!isFramework()) {
			result += APPINFO_PATH + "/";
		}
		result += RESOURCE_PATH;
		return (result);
	}

	/**
	 * @return
	 */
	public String getWebResourceName() {
		String result = "";
		if (!isFramework()) {
			result += APPINFO_PATH + "/";
		}
		result += WEBRESOURCE_PATH;
		return (result);
	}

	/* ************************************************************************ */

	// public IPath getDestinationPath(IResource res) {
	//
	// IPath fullPath = res.getFullPath();
	//
	// if (getBuildPath().isPrefixOf(fullPath)) {
	// return (null);
	// }
	//
	// try {
	// if (!getJavaProject()
	// .getOutputLocation()
	// .equals(getJavaProject().getPath())
	// && getJavaProject().getOutputLocation().isPrefixOf(fullPath)) {
	// return (null);
	// }
	// } catch (CoreException up) {
	// up.printStackTrace();
	// }
	//
	// IPath result = asResourcePath(fullPath, res);
	// if (null == result)
	// result = asWebResourcePath(fullPath, res);
	//
	// return (result);
	// }
	private IPath _appendSpecial(IPath destinationPrefix, IPath source) {
		String segments[] = source.segments();

		int n = segments.length - 1;
		for (int i = n; i >= 0; --i) {
			if (segments[i].endsWith(".lproj")) {
				n = i;
				if (segments[i].toLowerCase().startsWith("nonlocalized")) {
					n = i + 1;
				}
			}
		}
		IPath tmp = destinationPrefix;
		while (n < segments.length) {
			tmp = tmp.append(segments[n++]);
		}
		return (tmp);
	}

	/**
	 * @param path
	 * @param res
	 * @return
	 */
	public IPath asResourcePath(IPath path, IResource res) {
		if (IResource.FILE == res.getType() || IResource.FOLDER == res.getType()) {
			String lastSegment = path.lastSegment();
			if ((-1 == path.toString().indexOf(".eomodeld/")) && (-1 == path.toString().indexOf(".wo/"))) {
				return (_appendSpecial(getResourceOutputPath(), path));
			}

			String parentName = res.getParent().getName();
			if (parentName.endsWith(".wo") || parentName.endsWith(".eomodeld")) {
				return (_appendSpecial(getResourceOutputPath(), res.getParent().getProjectRelativePath()).append(lastSegment));
				// return
				// (getResourceOutputPath().append(parentName).append(lastSegment));
			}

		}

		return (null);
	}

	/**
	 * @param path
	 * @param res
	 * @return
	 */
	public IPath asWebResourcePath(IPath path, IResource res) {
		if (IResource.FILE == res.getType() || IResource.FOLDER == res.getType()) {
			return _appendSpecial(getWebResourceOutputPath(), path);
		}

		return (null);
	}

	/* ************************************************************************ */
	// public static IncrementalNature s_getNature(IProject project) {
	// try {
	// if (project.hasNature(INCREMENTAL_APPLICATION_NATURE_ID)) {
	// return (IncrementalNature) project.getNature(
	// INCREMENTAL_APPLICATION_NATURE_ID
	// );
	// } else if (project.hasNature(INCREMENTAL_FRAMEWORK_NATURE_ID)) {
	// return (IncrementalNature) project.getNature(
	// INCREMENTAL_FRAMEWORK_NATURE_ID
	// );
	// }
	//
	// } catch (CoreException exception) {
	// WOLipsLog.log(exception);
	// }
	// return null;
	// }
	// public static String getNature(boolean isFramework) {
	// if (isFramework)
	// return INCREMENTAL_FRAMEWORK_NATURE_ID;
	// return INCREMENTAL_APPLICATION_NATURE_ID;
	// }
	//
	// public static void s_addToProject(IProject project, boolean isFramework)
	// throws CoreException {
	// IProjectDescription desc = project.getDescription();
	//
	// String natures_array[] = desc.getNatureIds();
	//
	// List natures = new ArrayList(Arrays.asList(natures_array));
	// if (!natures.contains(getNature(isFramework))) {
	// natures.add(getNature(isFramework));
	// natures_array =
	// (String[]) natures.toArray(new String[natures.size()]);
	// desc.setNatureIds(natures_array);
	// s_setDescription(project, desc);
	// }
	// }
	//
	// public static void s_removeFromProject(
	// IProject project,
	// boolean isFramework)
	// throws CoreException {
	// IProjectDescription desc = project.getDescription();
	//
	// String natures_array[] = desc.getNatureIds();
	//
	// List natures = new ArrayList(Arrays.asList(natures_array));
	//
	// if (natures.contains(getNature(isFramework))) {
	// natures.remove(getNature(isFramework));
	// natures_array =
	// (String[]) natures.toArray(new String[natures.size()]);
	// desc.setNatureIds(natures_array);
	// s_setDescription(project, desc);
	// }
	// }
	//
	// private static void s_setDescription(
	// final IProject f_project,
	// final IProjectDescription f_desc) {
	// s_showProgress(new IRunnableWithProgress() {
	// public void run(IProgressMonitor pm) {
	// try {
	// f_project.setDescription(f_desc, pm);
	// } catch (CoreException up) {
	// pm.done();
	// }
	// }
	// });
	// }
	//
	// public static void s_showProgress(IRunnableWithProgress rwp) {
	// IWorkbench workbench = PlatformUI.getWorkbench();
	// Shell shell = null;
	// if (null != workbench) {
	// IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	// if (null != window) {
	// shell = window.getShell();
	// }
	// }
	//
	// ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
	//
	// try {
	// pmd.run(true, true, rwp);
	// } catch (InvocationTargetException e) {
	// // handle exception
	// e.printStackTrace();
	// } catch (InterruptedException e) {
	// // handle cancelation
	// e.printStackTrace();
	// }
	// }
	private static final String APPINFO_PATH = "Contents";

	private static final String RESOURCE_PATH = "Resources";

	private static final String WEBRESOURCE_PATH = "WebServerResources";

	private static final String JAVA_PATH = "Resources/Java";
}