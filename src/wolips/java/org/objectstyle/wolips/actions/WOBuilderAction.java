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
package org.objectstyle.wolips.actions;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.project.ProjectHelper;
/**
 * @author uli
 *
 * Action for adding and removing the WOBuilder
 */
public class WOBuilderAction extends ActionOnIProject {
	private static String WOBuilderRemoveID = "WOBuilder.Remove.ID";
	private static String WOFrameworkBuilderSetID = "WOFrameworkBuilder.Set.ID";
	private static String WOApplicationBuilderSetID =
		"WOApplicationBuilder.Set.ID";
	/**
	 * The constructor.
	 */
	public WOBuilderAction() {
		super();
	}
	/**
	 * Runs the action.
	 */
	public void run(IAction action) {
		if (project() != null) {
			try {
				if (action.getId().equals(WOBuilderAction.WOBuilderRemoveID)) {
					ProjectHelper.removeBuilder(
						project(),
						ProjectHelper.WOAPPLICATION_BUILDER_ID);
					ProjectHelper.removeBuilder(
						project(),
						ProjectHelper.WOFRAMEWORK_BUILDER_ID);
				} else {
					if (action
						.getId()
						.equals(WOBuilderAction.WOFrameworkBuilderSetID))
						ProjectHelper.installBuilder(
							project(),
							ProjectHelper.WOFRAMEWORK_BUILDER_ID);
					else {
						ProjectHelper.installBuilder(
							project(),
							ProjectHelper.WOAPPLICATION_BUILDER_ID);
					}
				}
			} catch (CoreException ex) {
				WOLipsPlugin.log(ex);
			}
		}
	}
	/**
	 * Disables and enables the menus for adding and removing WOBuilder.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		if (project() != null) {
			boolean isWOFramework = false;
			boolean isWOApplication = false;
			IProjectDescription projectDescription = null;
			try {
				projectDescription = project().getDescription();
			} catch (CoreException e) {
			}
			if (projectDescription != null) {
				if (projectDescription
					.hasNature(IWOLipsPluginConstants.WO_APPLICATION_NATURE)) {
					isWOApplication = true;
					isWOFramework = false;
				} else if (
					projectDescription.hasNature(
						IWOLipsPluginConstants.WO_FRAMEWORK_NATURE)) {
					isWOFramework = true;
					isWOApplication = false;
				}
			}
			if (action
				.getId()
				.equals(WOBuilderAction.WOFrameworkBuilderSetID)) {
				action.setEnabled(
					isWOFramework
						&& !ProjectHelper.isWOFwBuilderInstalled(project()));
			}
			if (action
				.getId()
				.equals(WOBuilderAction.WOApplicationBuilderSetID)) {
				action.setEnabled(
					isWOApplication
						&& !ProjectHelper.isWOAppBuilderInstalled(project()));
			}
			if (action.getId().equals(WOBuilderAction.WOBuilderRemoveID)) {
				action.setEnabled(
					ProjectHelper.isWOAppBuilderInstalled(project())
						|| ProjectHelper.isWOFwBuilderInstalled(project()));
			}
		} else {
			action.setEnabled(false);
		}
	}
}
