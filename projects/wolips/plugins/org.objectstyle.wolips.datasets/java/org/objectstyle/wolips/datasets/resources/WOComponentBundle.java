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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class WOComponentBundle
	extends WOLipsResource
	implements IWOComponentBundle {

	protected WOComponentBundle() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.objectstyle.wolips.core.resources.IWOLipsResource#getType()
	 */
	public final int getType() {
		return IWOLipsResource.WOCOMPONENT_BUNDLE;
	}

	/* (non-Javadoc)
	 * @see org.objectstyle.wolips.core.resources.IWOLipsResource#getRelatedResources()
	 */
	public final List getRelatedResources() {
		List list = new ArrayList();
		if (this.getCorrespondingResource() != null) {
			try {
				String fileName = this.getCorrespondingResource().getName();
				fileName = fileName.substring(0, fileName.length() - 3);
				String[] extensions =
					new String[] {
						WOLipsModel.WOCOMPONENT_WOD_EXTENSION,
						WOLipsModel.WOCOMPONENT_HTML_EXTENSION,
						WOLipsModel.WOCOMPONENT_WOO_EXTENSION,
						WOLipsModel.WOCOMPONENT_API_EXTENSION };
				list =
					WorkbenchUtilitiesPlugin
						.findResourcesInProjectByNameAndExtensions(
						this.getCorrespondingResource().getProject(),
						fileName,
						extensions,
						true);
			} catch (Exception e) {
				DataSetsPlugin.getDefault().getPluginLogger().log(e);
			}
		}
		return list;
	}

	/**
	 * Opens the resource in a Editor.
	 * @param forceToOpenIntextEditor If forceToOpenIntextEditor is set to true the resource opens in a texteditor.
	 */
	public final void open(boolean forceToOpenIntextEditor) {
		String fileName = this.getCorrespondingResource().getName();
		fileName = fileName.substring(0, fileName.length() - 3);
		if (forceToOpenIntextEditor) {
			WorkbenchUtilitiesPlugin.open(
				(IFile) ((IFolder) this.getCorrespondingResource()).findMember(
					fileName + "." + WOLipsModel.WOCOMPONENT_WOD_EXTENSION),
				forceToOpenIntextEditor,
				"org.objectstyle.wolips.internal.wod.editor");
			WorkbenchUtilitiesPlugin.open(
				(IFile) ((IFolder) this.getCorrespondingResource()).findMember(
					fileName + "." + WOLipsModel.WOCOMPONENT_HTML_EXTENSION),
				forceToOpenIntextEditor,
				"org.objectstyle.wolips.internal.html.editor");
		} else {
			WorkbenchUtilitiesPlugin.open(
				(IFile) ((IFolder) this.getCorrespondingResource()).findMember(
					fileName + "." + WOLipsModel.WOCOMPONENT_WOD_EXTENSION),
				forceToOpenIntextEditor,
				"org.objectstyle.wolips.internal.wod.editor");
		}
	}
}
