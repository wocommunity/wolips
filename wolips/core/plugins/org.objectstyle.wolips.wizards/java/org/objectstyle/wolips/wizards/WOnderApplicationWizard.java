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
package org.objectstyle.wolips.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectPatternsets;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author mnolte
 * @author uli
 */
public class WOnderApplicationWizard extends AbstractWonderProjectWizard {

	public WOnderApplicationWizard() {
		super();
	}

	public String getWindowTitle() {
		return Messages.getString("WOnderApplicationCreationWizard.title");
	}

	protected String getTemplateFolder() {
		return "wonderapplication";
	}
	
	private class Operation extends WorkspaceModifyOperation {
		IProject project = null;

		private String templateFolder;

		/**
		 * @param project
		 */
		public Operation(IProject project, String templateFolder) {
			super();
			this.project = project;
			this.templateFolder = templateFolder;
		}

		protected void execute(IProgressMonitor monitor) throws InvocationTargetException {
			String projectName = this.project.getName();
			String path = this.project.getLocation().toOSString();
			NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
			try {
				prepare(path);
				TemplateEngine templateEngine = new TemplateEngine();
				try {
					templateEngine.init();
				} catch (Exception e) {
					WizardsPlugin.getDefault().log(e);
					throw new InvocationTargetException(e);
				}
				String cptype = "";
				if("true".equals(VariablesPlugin.getDefault().getProperty("wonder.useprojects"))) {
					cptype = ".usingprojects";
				}
				templateEngine.getWolipsContext().setProjectName(projectName);
				templateEngine.getWolipsContext().setAntFolderName(ProjectPatternsets.ANT_FOLDER_NAME);
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/.classpath" + cptype + ".vm", path, ".classpath", ".classpath"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/.project.vm", path, ".project", ".project"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/build.xml.vm", path, "build.xml", "build.xml"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/build.properties.vm", path, "build.properties", "build.properties"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/CustomInfo.plist.vm", path, "CustomInfo.plist", "CustomInfo.plist"));
				addComponentDefinition(templateFolder, templateEngine, path, "Main");
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Application.java.vm", path + File.separator + "Sources", "Application.java", "Application.java"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/DirectAction.java.vm", path + File.separator + "Sources", "DirectAction.java", "DirectAction.java"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Main.java.vm", path + File.separator + "Sources", "Main.java", "Main.java"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Session.java.vm", path + File.separator + "Sources", "Session.java", "Session.java"));
				templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Properties.vm", path + File.separator + "Resources", "Properties", "Properties"));
				templateEngine.run(nullProgressMonitor);
				this.project.refreshLocal(IResource.DEPTH_INFINITE, nullProgressMonitor);
			} catch (Exception e) {
				throw new InvocationTargetException(e);
			}
		}
	}

	/**
	 * (non-Javadoc) Method declared on IWizard
	 * 
	 * @return
	 */
	public boolean performFinish() {
		boolean success = super.performFinish();
		if (success) {
			IProject project = super.getNewProject();
			Operation operation = new Operation(project, getTemplateFolder());
			try {
				operation.run(new NullProgressMonitor());
			} catch (InvocationTargetException e) {
				WizardsPlugin.getDefault().log(e);
				success = false;
			} catch (InterruptedException e) {
				WizardsPlugin.getDefault().log(e);
				success = false;
			}
		}
		return success;
	}
}