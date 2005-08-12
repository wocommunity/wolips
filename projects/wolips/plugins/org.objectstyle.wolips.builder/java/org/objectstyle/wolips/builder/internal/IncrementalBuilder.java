/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.objectstyle.wolips.core.resources.builder.IBuilder;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

public class IncrementalBuilder implements IBuilder {

	private IBuildAdapter buildAdapter;

	private List buildTasks = new ArrayList();

	private int buildWork = 0;

	private int buildKind;

	public IncrementalBuilder() {
		super();
	}

	public void buildStarted(int kind, Map args, IProgressMonitor monitor,
			IProject project) {
		buildKind = kind;
		IProjectAdapter projectAdapter = (IProjectAdapter) project
				.getAdapter(IProjectAdapter.class);
		this.buildAdapter = projectAdapter.getBuildAdapter();
		if (!this.buildAdapter.getUnderlyingFolder().exists()) {
			try {
				this.buildAdapter.getUnderlyingFolder().create(true, true,
						monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void visitingDeltasDone(int kind, Map args,
			IProgressMonitor monitor, IProject project) {
		for (int i = 0; i < this.buildTasks.size(); i++) {
			Buildtask buildtask = (Buildtask) this.buildTasks.get(i);
			try {
				buildtask.doWork(monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		this.buildAdapter = null;
	}

	public void handleClassesDelta(IResourceDelta delta) {
		// TODO Auto-generated method stub

	}

	public void handleWoappResourcesDelta(IResourceDelta delta) {
		try {
			if(buildKind == IncrementalProjectBuilder.FULL_BUILD || delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED) {
			IResource resource = buildAdapter.getProductAdapter()
					.getContentsAdapter().getResourcesAdapter().copy(
							delta.getResource());
			resource.setDerived(true);
			}
			else if(buildKind == IncrementalProjectBuilder.CLEAN_BUILD || delta.getKind() == IResourceDelta.REMOVED) {
				buildAdapter.getProductAdapter()
				.getContentsAdapter().getResourcesAdapter().deleteCopy(
						delta.getResource());
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void handleWebServerResourcesDelta(IResourceDelta delta) {
		// TODO Auto-generated method stub

	}

	public void handleOtherDelta(IResourceDelta delta) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param task
	 */
	public synchronized void addTask(Buildtask task) {
		buildTasks.add(task);
		buildWork += task.amountOfWork();
	}

	/**
	 * @param res
	 * @param delta
	 * @param copyToPath
	 * @return
	 * @throws CoreException
	 */
	private boolean handleResource(IResource res, IResourceDelta delta,
			IPath copyToPath) {
		if (null == copyToPath)
			return false;

		boolean handled = false;
		if ((null != delta) && (delta.getKind() == IResourceDelta.REMOVED)) {
			addTask(new DeleteTask(copyToPath, "build"));
			handled = true;
		} else {
			if (!res.isTeamPrivateMember()) {
				addTask(new CopyTask(res, copyToPath, "build"));
				handled = true;
			}
		}
		return handled;
	}

	/**
	 * @author Harald Niesche A single resource-related task (copy or delete a
	 *         resource, see subclasses)
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
			return (this.workAmount);
		}

		protected int workAmount = 1000;

	}

	/**
	 * @author Harald Niesche
	 */
	public static class CopyTask extends BuildtaskAbstract {
		/**
		 * @param res
		 * @param destination
		 * @param msgPrefix
		 */
		public CopyTask(IResource res, IPath destination, String msgPrefix) {
			this.res = res;
			this.dest = destination;
			this.msgPrefix = msgPrefix;
			this.workAmount = 1000;
			// if (res instanceof IFile) {
			// File localFile = ((IFile)res).getLocation().toFile();
			//      
			// if (localFile.exists()) {
			// workAmount = (int)localFile.length(); // if the file is
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
				int n = dest.segmentCount() - 3;
				IPath dstShortened = dest;
				if (n > 0) {
					dstShortened = dest.removeFirstSegments(n);
				}
				m.subTask("create " + dstShortened);
				ResourceUtilities.copyDerived(res, dest, m);
			} catch (CoreException up) {
				error = " *failed* to copy resource " + res + " -> " + dest
						+ " (" + up.getMessage() + ")";
			} catch (RuntimeException up) {
				error = " *failed* to copy resource " + res + " -> " + dest
						+ " (" + up.getMessage() + ")";
			}
		}

		IResource res;

		IPath dest;

		String msgPrefix;
	}

	/**
	 * @author Harald Niesche To change the template for this generated type
	 *         comment go to Window>Preferences>Java>Code Generation>Code and
	 *         Comments
	 */
	public static class DeleteTask extends BuildtaskAbstract {
		/**
		 * @param path
		 * @param msgPrefix
		 */
		public DeleteTask(IPath path, String msgPrefix) {
			this.workAmount = 1000;
			this.path = path;
			this.msgPrefix = msgPrefix;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.objectstyle.wolips.projectbuild.builder.WOIncrementalBuilder.WOBuildHelper.Buildtask#doWork(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public void doWork(IProgressMonitor m) throws CoreException {
			if (path == null) {
				// this really really should not happen! (again ...)
				throw new OperationCanceledException(
						"(deleting a null path wipes the workspace)");
			}
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(path);
			if (null != res) {
				res.refreshLocal(IResource.DEPTH_ONE, m);
			}
			IFile theFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
					path);
			IContainer theFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(path);
			if (null != theFile) {
				m.subTask("delete " + path);
				theFile.delete(true, true, null);
			} else if ((null != theFolder) && (theFolder instanceof IFolder)) {
				m.subTask("delete " + path);
				((IFolder) theFolder).delete(true, true, null);
			}
			/*
			 * if (theFile.exists()) { if (theFile.isFile()) {
			 * //getLogger().debug (msgPrefix+" delete "+path);
			 * theFile.delete(); } else if (theFile.isDirectory()) {
			 * //getLogger().debug ("*** not deleting folder: "+theFile); } }
			 */
			/*
			 * if ((null != res) && res.exists()) { //getLogger().debug
			 * (msgPrefix+" delete "+path); res.delete (true, m); } else {
			 * //getLogger().debug (msgPrefix+" delete (not) "+path); }
			 */
		}

		IPath path;

		String msgPrefix;
	}

}
