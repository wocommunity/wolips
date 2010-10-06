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
package org.objectstyle.wolips.core.resources.internal.types;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.objectstyle.wolips.core.resources.types.ILocalizedPath;
import org.objectstyle.wolips.core.resources.types.IPBDotProjectOwner;
import org.objectstyle.wolips.core.resources.types.IResourceType;
import org.objectstyle.wolips.core.resources.types.folder.IDotSubprojAdapter;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;

public abstract class AbstractResourceAdapter implements IResourceType {

	private IResource underlyingResource;

	public AbstractResourceAdapter(IResource underlyingResource) {
		super();
		this.underlyingResource = underlyingResource;
	}

	public IResource getUnderlyingResource() {
		return this.underlyingResource;
	}

	public IPBDotProjectOwner getPBDotProjectOwner() {
		return this.getPBDotProjectOwner(this.getUnderlyingResource());
	}

	public IPBDotProjectOwner getPBDotProjectOwner(IResource resource) {
		IContainer parent = null;
		if (resource instanceof IFile) {
			parent = resource.getParent();
		} else {
			parent = (IContainer) resource;
		}
		if (!(parent instanceof IProject)) {
			do {
				IDotSubprojAdapter subprojectAdapter = (IDotSubprojAdapter) parent.getAdapter(IDotSubprojAdapter.class);
				if (subprojectAdapter != null) {
					return subprojectAdapter;
				}
				parent = parent.getParent();
			} while (!(parent instanceof IProject));
		}
		if (this instanceof ProjectAdapter) {
			return (IPBDotProjectOwner) this;
		}
		ProjectAdapter projectAdapter = (ProjectAdapter) resource.getProject().getAdapter(ProjectAdapter.class);
		return projectAdapter;
	}

	/**
	 * Method localizedRelativeResourcePath.
	 * 
	 * @param pbDotProjectOwner
	 * @param resource
	 * @return ILocalizedPath
	 */
	public ILocalizedPath localizedRelativeResourcePath(IPBDotProjectOwner pbDotProjectOwner, IResource resource) {
		// determine relativ path to resource
		String resourcePath;
		if (pbDotProjectOwner.getUnderlyingResource().equals(resource.getParent())) {
			// same folder
			resourcePath = resource.getName();
		} else if (pbDotProjectOwner.getUnderlyingResource().getFullPath().matchingFirstSegments(resource.getFullPath()) == pbDotProjectOwner.getUnderlyingResource().getFullPath().segmentCount()) {
			// resource is deeper in directory structure
			resourcePath = resource.getFullPath().removeFirstSegments(pbDotProjectOwner.getUnderlyingResource().getFullPath().matchingFirstSegments(resource.getFullPath())).toString();
		} else {
			// resource is higher or paralell in directory structure
			resourcePath = resource.getProjectRelativePath().toString();
			for (int i = 0; i < pbDotProjectOwner.getUnderlyingResource().getProjectRelativePath().segmentCount() - 1; i++) {
				resourcePath = "../" + resourcePath;
			}
		}
		return new LocalizedPath(resourcePath, null);
	}
}
