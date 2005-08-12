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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.resources.builder.IBuilder;
import org.objectstyle.wolips.core.resources.types.ILocalizedPath;
import org.objectstyle.wolips.core.resources.types.IPBDotProjectOwner;
import org.objectstyle.wolips.core.resources.types.file.IPBDotProjectAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotWoAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

public class PBDotProjectBuilder implements IBuilder {

	private Hashtable affectedPBDotProjectOwner;

	public PBDotProjectBuilder() {
		super();
	}

	private String key(IResource resource) {
		return resource.getLocation().toPortableString();
	}

	private IPBDotProjectAdapter getIPBDotProjectAdapterForKey(
			IResource resource) {
		String key = this.key(resource);
		if (affectedPBDotProjectOwner.containsKey(key)) {
			return (IPBDotProjectAdapter) affectedPBDotProjectOwner.get(key);
		}
		return null;
	}

	private void setIPBDotProjectOwnerForKey(
			IPBDotProjectAdapter pbDotProjectAdapter, IResource resource) {
		affectedPBDotProjectOwner.put(this.key(resource), pbDotProjectAdapter);
	}

	public void buildStarted(int kind, Map args, IProgressMonitor monitor,
			IProject project) {
		this.affectedPBDotProjectOwner = new Hashtable();
	}

	public void visitingDeltasDone(int kind, Map args,
			IProgressMonitor monitor, IProject project) {
		Iterator iterator = affectedPBDotProjectOwner.values().iterator();
		while (iterator.hasNext()) {
			Object object = iterator
					.next();
			IPBDotProjectAdapter pbDotProjectAdapter = (IPBDotProjectAdapter) object;
			pbDotProjectAdapter.save();
		}
		this.affectedPBDotProjectOwner = null;
	}

	private IPBDotProjectOwner getIPBDotProjectOwner(IResource resource) {
		IProject project = resource.getProject();
		IProjectAdapter projectAdapter = (IProjectAdapter) project
				.getAdapter(IProjectAdapter.class);
		IPBDotProjectOwner pbDotProjectOwner = projectAdapter
				.getPBDotProjectOwner(resource);
		return pbDotProjectOwner;
	}

	public IPBDotProjectAdapter getIPBDotProjectAdapter(
			IPBDotProjectOwner pbDotProjectOwner) {

		IPBDotProjectAdapter pbDotProjectAdapter = this
				.getIPBDotProjectAdapterForKey(pbDotProjectOwner
						.getUnderlyingResource());
		if (pbDotProjectAdapter == null) {
			pbDotProjectAdapter = pbDotProjectOwner.getPBDotProjectAdapter();
			this.setIPBDotProjectOwnerForKey(pbDotProjectAdapter,
					pbDotProjectOwner.getUnderlyingResource());
		}
		return pbDotProjectAdapter;
	}

	public void handleClassesDelta(IResourceDelta delta) {
		IResource resource = delta.getResource();
		String extension = resource.getFileExtension();
		if (extension != null && extension.equals("class")) {
			return;
		}
		int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
			IPBDotProjectOwner pbDotProjectOwner = this
					.getIPBDotProjectOwner(resource);
			IPBDotProjectAdapter pbDotProjectAdapter = this
					.getIPBDotProjectAdapter(pbDotProjectOwner);
			ILocalizedPath localizedPath = pbDotProjectAdapter
					.localizedRelativeResourcePath(pbDotProjectOwner, resource);
			if (kind == IResourceDelta.ADDED) {
				pbDotProjectAdapter.addClass(localizedPath);
			} else if (kind == IResourceDelta.REMOVED) {
				pbDotProjectAdapter.removeClass(localizedPath);
			}
		}
	}

	public void handleWoappResourcesDelta(IResourceDelta delta) {
		IResource resource = delta.getResource();
		int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
			IPBDotProjectOwner pbDotProjectOwner = this
					.getIPBDotProjectOwner(resource);
			IPBDotProjectAdapter pbDotProjectAdapter = this
					.getIPBDotProjectAdapter(pbDotProjectOwner);
			ILocalizedPath localizedPath = pbDotProjectAdapter
					.localizedRelativeResourcePath(pbDotProjectOwner, resource);
			IDotWoAdapter dotWoAdapter = (IDotWoAdapter) resource
					.getAdapter(IDotWoAdapter.class);
			boolean isDotWO = dotWoAdapter != null;
			IDotWoAdapter parentWoAdapter = null;
			if (resource.getParent() != null) {
				parentWoAdapter = (IDotWoAdapter) resource.getParent()
						.getAdapter(IDotWoAdapter.class);
			}
			boolean parentIsDotWO = parentWoAdapter != null;
			if (parentIsDotWO) {
				return;
			}
			if (kind == IResourceDelta.ADDED) {
				if (isDotWO) {
					pbDotProjectAdapter.addWoComponent(localizedPath);
				} else {
					pbDotProjectAdapter.addWoappResource(localizedPath);
				}
			} else if (kind == IResourceDelta.REMOVED) {
				if (isDotWO) {
					pbDotProjectAdapter.removeWoComponent(localizedPath);
				} else {
					pbDotProjectAdapter.removeWoappResource(localizedPath);
				}
			}
		}
	}

	public void handleWebServerResourcesDelta(IResourceDelta delta) {
		IResource resource = delta.getResource();
		int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
			IPBDotProjectOwner pbDotProjectOwner = this
					.getIPBDotProjectOwner(resource);
			IPBDotProjectAdapter pbDotProjectAdapter = this
					.getIPBDotProjectAdapter(pbDotProjectOwner);
			ILocalizedPath localizedPath = pbDotProjectAdapter
					.localizedRelativeResourcePath(pbDotProjectOwner, resource);
			if (kind == IResourceDelta.ADDED) {
				pbDotProjectAdapter.addWebServerResource(localizedPath);
			} else if (kind == IResourceDelta.REMOVED) {
				pbDotProjectAdapter.removeWebServerResource(localizedPath);
			}
		}
	}

	public void handleOtherDelta(IResourceDelta delta) {
		IResource resource = delta.getResource();
		String extension = resource.getFileExtension();
		if (extension == null || !extension.equals("java")) {
			return;
		}
		int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
			IPBDotProjectOwner pbDotProjectOwner = this
					.getIPBDotProjectOwner(resource);
			IPBDotProjectAdapter pbDotProjectAdapter = this
					.getIPBDotProjectAdapter(pbDotProjectOwner);
			ILocalizedPath localizedPath = pbDotProjectAdapter
					.localizedRelativeResourcePath(pbDotProjectOwner, resource);
			if (kind == IResourceDelta.ADDED) {
				pbDotProjectAdapter.addClass(localizedPath);
			} else if (kind == IResourceDelta.REMOVED) {
				pbDotProjectAdapter.removeClass(localizedPath);
			}
		}
	}

}
