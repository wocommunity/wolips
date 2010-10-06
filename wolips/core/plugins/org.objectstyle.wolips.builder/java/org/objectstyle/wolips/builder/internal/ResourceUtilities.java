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

package org.objectstyle.wolips.builder.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.FileUtilities;

/**
 * @author Harald Niesche
 * 
 */
public class ResourceUtilities {

	/**
	 * create a Folder recursively
	 * 
	 * @param f
	 *            the Folder to be created
	 * @param m
	 *            a ProgressMonitor
	 * @throws CoreException
	 */
	public static void createFolder(IFolder f, IProgressMonitor m) throws CoreException {
		if (f.exists()) {
			return;
		}
		IContainer parent = f.getParent();
		if (!f.getParent().exists()) {
			if (parent instanceof IFolder) {
				createFolder((IFolder) parent, m);
			}
		}
		f.create(true, true, m);
	}

	/**
	 * check if a folder exists under a path, create it if necessary
	 * 
	 * @param path
	 *            the path to the folder to be created (relative to the
	 *            workspace root or absolute)
	 * @param m
	 *            a ProgressMonitor
	 * @return true, iff the folder already existed, false, if it had to be
	 *         created
	 * @throws CoreException
	 */
	public static boolean checkDir(IPath path, IProgressMonitor m) throws CoreException {
		return checkDir(path, m, false);
	}

	/**
	 * @param path
	 * @param m
	 * @return
	 * @throws CoreException
	 */
	public static boolean checkDerivedDir(IPath path, IProgressMonitor m) throws CoreException {
		return checkDir(path, m, true);
	}

	public static boolean checkDir(IPath path, IProgressMonitor m, boolean derived) throws CoreException {
		boolean result = true;

		IFolder f = getWorkspaceRoot().getFolder(path);
		if (!f.exists()) {
			createFolder(f, m);
			if (derived) {
				f.setDerived(true);
			}
			result = false;
		}

		return (result);
	}

	/**
	 * checks if a path is fit to be used as destination for a copy operation if
	 * not, the destination is prepared to be used as destination (i.e.,
	 * existing files and folders are deleted, the parent path is created, if
	 * necessary)
	 * 
	 * @param path
	 *            the candidate destination path
	 * @param m
	 *            a ProgressMonitor
	 * @return
	 * @throws CoreException
	 */
	public static IResource checkDestination(IPath path, IProgressMonitor m, boolean deleteIfExists) throws CoreException {
		return checkDestination(path, m, false, deleteIfExists);
	}

	public static IResource checkDerivedDestination(IPath path, IProgressMonitor m, boolean deleteIfExists) throws CoreException {
		return checkDestination(path, m, true, deleteIfExists);
	}

	public static IResource checkDestination(IPath path, IProgressMonitor m, boolean derived, boolean deleteIfExists) throws CoreException {
		if (checkDir(path.removeLastSegments(1), m, derived)) {
			IResource res = getWorkspaceRoot().findMember(path);
			if (null != res && res.exists()) {
				if (deleteIfExists) {
					try {
						File f = res.getLocation().toFile();
						if (f.exists()) {
							FileUtilities.deleteRecursively(f);
						}
						else {
							res.delete(true, m);
						}
						// } catch
						// (org.eclipse.core.internal.resources.ResourceException e)
						// {
					} catch (CoreException ce) {
						ce.printStackTrace();
						// NOTE AK: this code was commented out, I re-instated it as
						// I think it's
						// better to have the non-deletable files in one place,
						// instead of messing the build directory
						IPath trashFolder = res.getProject().getFullPath().append("build/.trash");
						IPath trashPath = trashFolder.append(res.getName() + (_getUniqifier()));
						checkDir(trashFolder, m);
						res.move(trashPath, true, null);
						/*
						 * IPath newName = res.getLocation().removeLastSegments(1);
						 * newName = newName.append(res.getName()+_getUniqifier());
						 * File resFile = res.getLocation().toFile(); if
						 * (!resFile.renameTo(newName.toFile())) { throw ce; }
						 */
					}
				}
				res.refreshLocal(IResource.DEPTH_ONE, m);
			}
			return res;
		}
		return null;
	}

	private static synchronized int _getUniqifier() {
		return _uniqifier++;
	}

	/**
	 * @param res
	 * @param dest
	 * @param m
	 * @throws CoreException
	 */
	public static void copyDerivedOld(IResource res, IPath dest, IProgressMonitor m) throws CoreException {
		if (res.isTeamPrivateMember() || res.getName().equals(".svn"))
			return;

		IResource rdest = checkDestination(dest, m, true);
		res.copy(dest, true, null);
		if (null != rdest) {
			rdest.setDerived(true);
		}
	}

	/**
	 * @param res
	 *            the Resource to copy
	 * @param dest
	 *            where it should be copied to
	 * @param m
	 *            ProgressMonitor to show what's going on
	 * 
	 * @throws CoreException
	 *             if something goes wrong (and we notice)
	 */
	public static void copyDerived(IResource res, IPath dest, IProgressMonitor m) throws CoreException {
		if (res.isTeamPrivateMember() || res.getName().equals(".svn"))
			return;

		if (res instanceof IFolder) {
			IResource[] members = ((IFolder) res).members();
			// MS: This is super goofy, but apparently Nonlocalized.lproj folders get fiddled with
			// during the build and end up going to build/Whatever.framework/Resources directly,
			// which makes the Resource folder get deleted, potentially tossing files you just built.
			boolean deleteDestination = !res.getName().equals("Nonlocalized.lproj"); 
			IResource rdest = checkDerivedDestination(dest, m, deleteDestination);

			for (int i = 0; i < members.length; i++) {
				IResource thisOne = members[i];
				if (!members[i].isTeamPrivateMember()) {
					IPath thisDest = dest.append(thisOne.getName());
					copyDerived(thisOne, thisDest, m);
				}
			}
			if (null != rdest) {
				rdest.setDerived(true);
			}
		} else {
			// it's a file, let's just copy it
			IResource rdest = checkDestination(dest, m, true);
			res.copy(dest, true, m);
			if (null != rdest) {
				rdest.setDerived(true);
			}
		}
	}

	/**
	 * @return
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * @return
	 */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected ResourceUtilities() {
		super();
	}

	private static int _uniqifier = 1;

	/**
	 * @param res
	 * @param markerId
	 * @throws CoreException
	 */
	public static void unmarkResource(IResource res, String markerId) throws CoreException {
		if (res.exists()) {
			res.deleteMarkers(markerId, true, 0);
		}
	}

	/**
	 * @param res
	 * @param markerId
	 * @param severity
	 * @param message
	 * @param location
	 * @return
	 * @throws CoreException
	 */
	public static IMarker markResource(IResource res, String markerId, int severity, String message, String location) throws CoreException {
		IMarker newMarker;
		if (!res.exists()) {
			newMarker = null;
		}
		else {
			try {
			IMarker[] marker = res.findMarkers(markerId, true, 0);
			if (marker.length != 1) {
				if (marker.length > 1) {
					res.deleteMarkers(markerId, false, 0);
				}
				marker = new IMarker[1];
				marker[0] = res.createMarker(markerId);
			}
	
			if (!marker[0].exists()) {
				marker[0] = res.createMarker(markerId);
			}
			Map attr = new HashMap();
	
			attr.put(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_HIGH));
			attr.put(IMarker.SEVERITY, new Integer(severity));
			attr.put(IMarker.MESSAGE, message);
			attr.put(IMarker.LOCATION, location);
	
			marker[0].setAttributes(attr);
			newMarker = marker[0];
			}
			catch (ResourceException e) {
				// MS: This happens a lot and it's not actually a problem.  This is almost always a deleted file.
				e.printStackTrace();
				newMarker = null;
			}
		}
		return newMarker;
	}
}
