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
package org.objectstyle.wolips.core.resources.internal.types.folder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.objectstyle.wolips.baseforplugins.util.WOLipsNatureUtils;
import org.objectstyle.wolips.core.resources.internal.types.AbstractResourceAdapterFactory;
import org.objectstyle.wolips.core.resources.types.IResourceType;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IContentsAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotApplicationAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotEOModeldAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotFrameworkAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotLprojAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotSubprojAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IDotWoAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IResourcesAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IWebServerResourcesAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IWoprojectAdapter;

/**
 * @author ulrich
 */
public class FolderAdapterFactory extends AbstractResourceAdapterFactory {

	private Class[] adapterList = new Class[] { IDotApplicationAdapter.class, IDotFrameworkAdapter.class, IDotLprojAdapter.class, IDotSubprojAdapter.class, IDotWoAdapter.class, IWoprojectAdapter.class };

	public Class[] getAdapterList() {
		return this.adapterList;
	}

	public boolean isSupported(Object adaptableObject, Class adapterType) {
		if (!(adaptableObject instanceof IFolder)) {
			return false;
		}
		if (adapterType == IBuildAdapter.class) {
			return true;
		} else if (adapterType == IContentsAdapter.class) {
			return true;
		} else if (adapterType == IResourcesAdapter.class) {
			return true;
		} else if (adapterType == IWebServerResourcesAdapter.class) {
			return true;
		} else if (adapterType == IDotApplicationAdapter.class) {
			return true;
		} else if (adapterType == IDotFrameworkAdapter.class) {
			return true;
		} else if (adapterType == IDotLprojAdapter.class) {
			return true;
		} else if (adapterType == IDotSubprojAdapter.class) {
			return true;
		} else if (adapterType == IDotWoAdapter.class) {
			return true;
		} else if (adapterType == IDotEOModeldAdapter.class) {
			return true;
		} else if (adapterType == IWoprojectAdapter.class) {
			return true;
		}
		return false;
	}

	public IResourceType createAdapter(Object adaptableObject, Class adapterType) {
		IFolder folder = (IFolder) adaptableObject;
		IProject project = folder.getProject();
		if (!WOLipsNatureUtils.isWOLipsNature(project)) {
			return null;
		}
		if (adapterType == IBuildAdapter.class) {
			if (folder.getName() != null && (IBuildAdapter.FILE_NAME_BUILD.equals(folder.getName()) || IBuildAdapter.FILE_NAME_DIST.equals(folder.getName()))) {
				return new BuildAdapter(folder);
			}
		} else if (adapterType == IContentsAdapter.class) {
			if (folder.getName() != null && IContentsAdapter.FILE_NAME.equals(folder.getName())) {
				return new ContentsAdapter(folder);
			}
		} else if (adapterType == IResourcesAdapter.class) {
			if (folder.getName() != null && IResourcesAdapter.FILE_NAME.equals(folder.getName())) {
				return new ResourcesAdapter(folder);
			}
		} else if (adapterType == IWebServerResourcesAdapter.class) {
			if (folder.getName() != null && IWebServerResourcesAdapter.FILE_NAME.equals(folder.getName())) {
				return new WebServerResourcesAdapter(folder);
			}
		} else if (adapterType == IDotApplicationAdapter.class) {
			if (folder.getFileExtension() != null && IDotApplicationAdapter.FILE_NAME_EXTENSION.equals(folder.getFileExtension())) {
				return new DotApplicationAdapter(folder);
			}
		} else if (adapterType == IDotFrameworkAdapter.class) {
			if (folder.getFileExtension() != null && IDotFrameworkAdapter.FILE_NAME_EXTENSION.equals(folder.getFileExtension())) {
				return new DotFrameworkAdapter(folder);
			}
		} else if (adapterType == IDotSubprojAdapter.class) {
			if (folder.getFileExtension() != null && IDotSubprojAdapter.FILE_NAME_EXTENSION.equals(folder.getFileExtension())) {
				return new DotSubprojAdapter(folder);
			}
		} else if (adapterType == IDotLprojAdapter.class) {
			if (folder.getFileExtension() != null && IDotLprojAdapter.FILE_NAME_EXTENSION.equals(folder.getFileExtension())) {
				return new DotLprojAdapter(folder);
			}
		} else if (adapterType == IDotWoAdapter.class) {
			if (folder.getFileExtension() != null && IDotWoAdapter.FILE_NAME_EXTENSION.equals(folder.getFileExtension())) {
				return new DotWoAdapter(folder);
			}
		} else if (adapterType == IDotEOModeldAdapter.class) {
			if (folder.getFileExtension() != null && IDotEOModeldAdapter.FILE_NAME_EXTENSION.equals(folder.getFileExtension())) {
				return new DotEOModeldAdapter(folder);
			}
		} else if (adapterType == IWoprojectAdapter.class) {
			if (folder.getFileExtension() == null && folder.getName() != null && folder.getParent() != null && folder.getParent().getType() == IResource.PROJECT && (IWoprojectAdapter.FOLDER_NAME.equals(folder.getName()) || (IWoprojectAdapter.FOLDER_NAME_DEPRECATED.equals(folder.getName())))) {
				return new WoprojectAdapter(folder);
			}
		}
		return null;
	}
}