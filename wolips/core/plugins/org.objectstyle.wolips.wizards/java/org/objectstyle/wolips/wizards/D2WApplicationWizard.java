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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectPatternsets;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;

/**
 * @author mnolte
 * @author uli
 */
public class D2WApplicationWizard extends AbstractProjectWizard {

	public D2WApplicationWizard() {
		super();
	}

	public String getWindowTitle() {
		return Messages.getString("D2WApplicationWizard.title");
	}

	@Override
	protected void _createProject(IProject project, IProgressMonitor progressMonitor) throws Exception {
		String projectName = project.getName();
		String path = project.getLocation().toOSString();
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
		File ant = new File(path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME);
		ant.mkdirs();
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.init();
		templateEngine.getWolipsContext().setProjectName(projectName);
		templateEngine.getWolipsContext().setAntFolderName(ProjectPatternsets.ANT_FOLDER_NAME);
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Main.html.vm", path + File.separator + "Main.wo", "Main.html", "Main.html"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Main.wod.vm", path + File.separator + "Main.wo", "Main.wod", "Main.wod"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Main.woo.vm", path + File.separator + "Main.wo", "Main.woo", "Main.woo"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/MenuHeader.html.vm", path + File.separator + "MenuHeader.wo", "MenuHeader.html", "MenuHeader.html"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/MenuHeader.wod.vm", path + File.separator + "MenuHeader.wo", "MenuHeader.wod", "MenuHeader.wod"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/MenuHeader.woo.vm", path + File.separator + "MenuHeader.wo", "MenuHeader.woo", "MenuHeader.woo"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/PageWrapper.html.vm", path + File.separator + "PageWrapper.wo", "PageWrapper.html", "PageWrapper.html"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/PageWrapper.wod.vm", path + File.separator + "PageWrapper.wo", "PageWrapper.wod", "PageWrapper.wod"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Main.woo.vm", path + File.separator + "PageWrapper.wo", "PageWrapper.woo", "PageWrapper.woo"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Application.java.vm", path + File.separator + "src", "Application.java", "Application.java"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/DirectAction.java.vm", path + File.separator + "src", "DirectAction.java", "DirectAction.java"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Main.java.vm", path + File.separator + "src", "Main.java", "Main.java"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/MenuHeader.java.vm", path + File.separator + "src", "MenuHeader.java", "MenuHeader.java"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/PageWrapper.java.vm", path + File.separator + "src", "PageWrapper.java", "PageWrapper.java"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Session.java.vm", path + File.separator + "src", "Session.java", "Session.java"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/.classpath.vm", path, ".classpath", ".classpath"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/.project.vm", path, ".project", ".project"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/ant.classpaths.user.home.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.classpaths.user.home", "ant.classpaths.user.home"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/ant.classpaths.wo.wolocalroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.classpaths.wo.wolocalroot", "ant.classpaths.wo.wolocalroot"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/ant.classpaths.wo.wosystemroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.classpaths.wo.wosystemroot", "ant.classpaths.wo.wosystemroot"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/ant.frameworks.user.home.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.frameworks.user.home", "ant.frameworks.user.home"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/ant.frameworks.wo.wolocalroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.frameworks.wo.wolocalroot", "ant.frameworks.wo.wolocalroot"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/ant.frameworks.wo.wosystemroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.frameworks.wo.wosystemroot", "ant.frameworks.wo.wosystemroot"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/build.xml.vm", path, "build.xml", "build.xml"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/build.properties.vm", path, "build.properties", "build.properties"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/CustomInfo.plist.vm", path, "CustomInfo.plist", "CustomInfo.plist"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Main.api.vm", path, "Main.api", "Main.api"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/MenuHeader.api.vm", path, "MenuHeader.api", "MenuHeader.api"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/PageWrapper.api.vm", path, "PageWrapper.api", "PageWrapper.api"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Makefile.vm", path, "Makefile", "Makefile"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Makefile.postamble.vm", path, "Makefile.postamble", "Makefile.postamble"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Makefile.preamble.vm", path, "Makefile.preamble", "Makefile.preamble"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/PB.project.vm", path, "PB.project", "PB.project"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/Properties.vm", path, "Properties", "Properties"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/project.pbxproj.vm", path + File.separator + projectName + ".xcode", "project.pbxproj", "project.pbxproj"));
		templateEngine.addTemplate(new TemplateDefinition("d2w_application/user.d2wmodel.vm", path, "user.d2wmodel", "user.d2wmodel"));
		templateEngine.run(progressMonitor);
	}
}