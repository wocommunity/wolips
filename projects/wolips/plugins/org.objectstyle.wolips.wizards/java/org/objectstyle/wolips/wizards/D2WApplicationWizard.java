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
package org.objectstyle.wolips.wizards;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
import org.objectstyle.wolips.templateengine.TemplateEnginePlugin;
/**
 * @author mnolte
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
public class D2WApplicationWizard extends AbstractProjectWizard {
	/**
	 * default contructor
	 */
	public D2WApplicationWizard() {
		super(TemplateEnginePlugin.D2W_ApplicationProject);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectstyle.wolips.wizards.AbstractWOWizard#getWindowTitle()
	 */
	public String getWindowTitle() {
		return Messages.getString("D2WApplicationWizard.title");
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
			String projectName = project.getName();
			String path = project.getLocation().toOSString();
			NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
			try {
				File mainwo = new File(path + File.separator + "Main.wo");
				mainwo.mkdirs();
				File menuHeaderwo = new File(path + File.separator + "MenuHeader.wo");
				menuHeaderwo.mkdirs();
				File pageWrapperwo = new File(path + File.separator + "PageWrapper.wo");
				pageWrapperwo.mkdirs();
				File src = new File(path + File.separator + "src");
				src.mkdirs();
				File bin = new File(path + File.separator + "bin");
				bin.mkdirs();
				File xcode = new File(path + File.separator + projectName
						+ ".xcode");
				xcode.mkdirs();
				//project.close(nullProgressMonitor);
				TemplateEngine templateEngine = new TemplateEngine();
				try {
					templateEngine.init();
				} catch (Exception e) {
					WizardsPlugin.getDefault().getPluginLogger().log(e);
					throw new InvocationTargetException(e);
				}
				templateEngine.getWolipsContext().setProjectName(projectName);
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Main.html.vm", path + File.separator
								+ "Main.wo", "Main.html", "Main.html"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Main.wod.vm", path + File.separator
								+ "Main.wo", "Main.wod", "Main.wod"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Main.woo.vm", path + File.separator
								+ "Main.wo", "Main.woo", "Main.woo"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/MenuHeader.html.vm", path + File.separator
								+ "MenuHeader.wo", "MenuHeader.html", "MenuHeader.html"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/MenuHeader.wod.vm", path + File.separator
								+ "MenuHeader.wo", "MenuHeader.wod", "MenuHeader.wod"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/MenuHeader.woo.vm", path + File.separator
								+ "MenuHeader.wo", "MenuHeader.woo", "MenuHeader.woo"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/PageWrapper.html.vm", path + File.separator
								+ "PageWrapper.wo", "PageWrapper.html", "PageWrapper.html"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/PageWrapper.wod.vm", path + File.separator
								+ "PageWrapper.wo", "PageWrapper.wod", "PageWrapper.wod"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Main.woo.vm", path + File.separator
								+ "PageWrapper.wo", "PageWrapper.woo", "PageWrapper.woo"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Application.java.vm", path
								+ File.separator + "src", "Application.java", "Application.java"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/DirectAction.java.vm", path
								+ File.separator + "src", "DirectAction.java", "DirectAction.java"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Main.java.vm", path + File.separator
								+ "src", "Main.java", "Main.java"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/MenuHeader.java.vm", path + File.separator
								+ "src", "MenuHeader.java", "MenuHeader.java"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/PageWrapper.java.vm", path + File.separator
								+ "src", "PageWrapper.java", "PageWrapper.java"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Session.java.vm", path + File.separator
								+ "src", "Session.java", "Session.java"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/.classpath.vm", path, ".classpath", ".classpath"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/.project.vm", path, ".project", ".project"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/ant.classpaths.user.home.vm", path,
						"ant.classpaths.user.home",
						"ant.classpaths.user.home"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/ant.classpaths.wo.wolocalroot.vm", path,
						"ant.classpaths.wo.wolocalroot",
						"ant.classpaths.wo.wolocalroot"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/ant.classpaths.wo.wosystemroot.vm",
						path, "ant.classpaths.wo.wosystemroot", "ant.classpaths.wo.wosystemroot"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/ant.frameworks.user.home.vm", path,
						"ant.frameworks.user.home",
						"ant.frameworks.user.home"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/ant.frameworks.wo.wolocalroot.vm", path,
						"ant.frameworks.wo.wolocalroot",
						"ant.frameworks.wo.wolocalroot"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/ant.frameworks.wo.wosystemroot.vm",
						path, "ant.frameworks.wo.wosystemroot", "ant.frameworks.wo.wosystemroot"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/build.xml.vm", path, "build.xml", "build.xml"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/build.properties.vm", path,
						"build.properties",
						"build.properties"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/CustomInfo.plist.vm", path,
						"CustomInfo.plist",
						"CustomInfo.plist"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Main.api.vm", path, "Main.api", "Main.api"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/MenuHeader.api.vm", path, "MenuHeader.api", "MenuHeader.api"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/PageWrapper.api.vm", path, "PageWrapper.api", "PageWrapper.api"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Makefile.vm", path, "Makefile", "Makefile"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Makefile.postamble.vm", path,
						"Makefile.postamble",
						"Makefile.postamble"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Makefile.preamble.vm", path,
						"Makefile.preamble",
						"Makefile.preamble"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/PB.project.vm", path, "PB.project", "PB.project"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/Properties.vm", path, "Properties", "Properties"));
				templateEngine.addTemplate(new TemplateDefinition(
						"d2wapplication/project.pbxproj.vm", path
								+ File.separator + projectName + ".xcode",
								"project.pbxproj",
								"project.pbxproj"));
				templateEngine.addTemplate(new TemplateDefinition(
								"d2wapplication/user.d2wmodel.vm", path,
								"user.d2wmodel",
								"user.d2wmodel"));
				templateEngine.run(nullProgressMonitor);
				//project.open(nullProgressMonitor);
				//RunAnt runAnt = new RunAnt();
				//runAnt.asAnt(path + File.separator + IWOLipsModel.DEFAULT_BUILD_FILENAME, null, null);
				project.refreshLocal(IResource.DEPTH_INFINITE,
						nullProgressMonitor);
			} catch (Exception e) {
				WizardsPlugin.getDefault().getPluginLogger().log(e);
				success = false;
			}
		}
		return success;
	}
}
