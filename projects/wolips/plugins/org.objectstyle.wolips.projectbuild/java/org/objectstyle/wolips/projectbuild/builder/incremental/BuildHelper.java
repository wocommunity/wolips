/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002, 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.projectbuild.builder.incremental;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
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
import org.objectstyle.wolips.commons.logging.ILogger;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;
import org.objectstyle.wolips.projectbuild.natures.IncrementalNature;
import org.objectstyle.wolips.projectbuild.util.ResourceUtilities;

/**
 * @author Harald Niesche
 */
public abstract class BuildHelper extends ResourceUtilities implements
		IResourceDeltaVisitor, IResourceVisitor {
	private Project _project;

	private IPath _distPath;

	private IncrementalNature _woNature = null;

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

		protected ILogger _getLogger() {
			return ProjectBuildPlugin.getDefault().getPluginLogger();
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
				int n = _dest.segmentCount() - 3;
				IPath dstShortened = _dest;
				if (n > 0) {
					dstShortened = _dest.removeFirstSegments(n);
				}
				m.subTask("create " + dstShortened);
				ResourceUtilities.copyDerived(_res, _dest, m);
				_getLogger().debug("copy " + _res + " -> " + _dest);

			} catch (CoreException up) {
				error = " *failed* to copy resource " + _res + " -> " + _dest
						+ " (" + up.getMessage() + ")";
				_getLogger().debug(_msgPrefix + error, up);
				//          up.printStackTrace();
				//          m.setCanceled(true);
				//throw up;
			} catch (RuntimeException up) {
				error = " *failed* to copy resource " + _res + " -> " + _dest
						+ " (" + up.getMessage() + ")";
				_getLogger().log(_msgPrefix + error, up);
				//          up.printStackTrace();
				//          throw up;
			}
			if (null == error) {
				//_res.deleteMarkers(IMarker.PROBLEM, true, 1);
				_res.deleteMarkers(ProjectBuildPlugin.MARKER_BUILD_PROBLEM,
						true, 0);
			} else {
				markResource(_res, ProjectBuildPlugin.MARKER_BUILD_PROBLEM,
						IMarker.SEVERITY_ERROR, error, _dest.toString());
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
				throw new OperationCanceledException(
						"(deleting a null path wipes the workspace)");
			}
			IResource res = getWorkspaceRoot().findMember(_path);
			if (null != res) {
				res.refreshLocal(IResource.DEPTH_ONE, m);
			}
			IFile theFile = getWorkspaceRoot().getFile(_path);
			IContainer theFolder = getWorkspaceRoot().getFolder(_path);
			if (null != theFile) {
				_getLogger().debug(_msgPrefix + " delete " + _path);
				m.subTask("delete " + _path);
				theFile.delete(true, true, null);
			} else if ((null != theFolder) && (theFolder instanceof IFolder)) {
				_getLogger().debug(_msgPrefix + " delete " + _path);
				m.subTask("delete " + _path);
				((IFolder) theFolder).delete(true, true, null);
			}
			/*
			 * if (theFile.exists()) { if (theFile.isFile()) {
			 * //_getLogger().debug (_msgPrefix+" delete "+_path);
			 * theFile.delete(); } else if (theFile.isDirectory()) {
			 * //_getLogger().debug ("*** not deleting folder: "+theFile); } }
			 */
			/*
			 * if ((null != res) && res.exists()) { //_getLogger().debug
			 * (_msgPrefix+" delete "+_path); res.delete (true, m); } else {
			 * //_getLogger().debug (_msgPrefix+" delete (not) "+_path); }
			 */
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
		try {
			_woNature = (IncrementalNature) _project.getIncrementalNature();
		} catch (CoreException e) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(e);
		}
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

	private boolean _visitResource(IResource res, IResourceDelta delta)
			throws CoreException {
		IPath resPath = res.getProjectRelativePath();
		if (this.getIncrementalNature().getBuildPath().isPrefixOf(resPath)
				|| _distPath.isPrefixOf(resPath)) {
			return false;
		}
		handleResource(res, delta);
		return true;
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
	public void executeTasks(IProgressMonitor m) throws CoreException {
		m = new SubProgressMonitor(m, 41);
		m.beginTask("building ...", _buildWork);
		Iterator iter = _buildTasks.iterator();
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

	protected ILogger _getLogger() {
		return ProjectBuildPlugin.getDefault().getPluginLogger();
	}

	public abstract void handleResource(IResource res, IResourceDelta delta)
			throws CoreException;

	protected IncrementalNature getIncrementalNature() {
		return _woNature;
	}

	public Project getProject() {
		return _project;
	}
}