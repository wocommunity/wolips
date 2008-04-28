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

package org.objectstyle.wolips.datasets.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public abstract class WOLipsResource implements IWOLipsResource {
	private IResource resource = null;

	protected WOLipsResource() {
		super();
	}

	/**
	 * @return Returns the IResource.
	 */
	public IResource getCorrespondingResource() {
		return this.resource;
	}

	/**
	 * @param resource
	 */
	public void setCorrespondingResource(IResource resource) {
		this.resource = resource;
	}

	public List getRelatedResources() {
		return getRelatedWOComponentResources(this.getCorrespondingResource());
	}
	
	@Override
	public int hashCode() {
		return resource == null ? 0 : resource.hashCode();
	}
	
	public boolean equals(Object obj) {
		return resource != null && obj instanceof WOLipsResource && resource.equals(((WOLipsResource)obj).resource);
	}
	
	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ": " + resource + "]";
	}

	/** static help method to find all related resources for WO component files */
	public static List getRelatedWOComponentResources(IResource resource) {
		List list = new ArrayList();
		if (resource != null) {
			try {
				String fileName = resource.getName();
				String extName = resource.getFileExtension();
				int length = fileName.length() - extName.length() - 1;
				if (length > 0) {
					fileName = fileName.substring(0, length);
					String[] extensions = new String[] { "java", "groovy", WOLipsModel.WOCOMPONENT_BUNDLE_EXTENSION, WOLipsModel.WOCOMPONENT_HTML_EXTENSION, WOLipsModel.WOCOMPONENT_WOD_EXTENSION, WOLipsModel.WOCOMPONENT_WOO_EXTENSION, WOLipsModel.WOCOMPONENT_API_EXTENSION };
					list = WorkbenchUtilitiesPlugin.findResourcesInProjectByNameAndExtensions(resource.getProject(), fileName, extensions, false);
				}
			} catch (Exception e) {
				DataSetsPlugin.getDefault().getPluginLogger().log(e);
			}
		}
		return list;
	}

}
