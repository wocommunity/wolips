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
public class WOApplicationWizard extends AbstractProjectWizard {

	public WOApplicationWizard() {
		super();
	}

	@Override
	protected WizardType wizardType(){
		return WizardType.WO_APPLICATION_WIZARD;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectstyle.wolips.wizards.AbstractWOWizard#getWindowTitle()
	 */
	public String getWindowTitle() {
		return Messages.getString("WOApplicationCreationWizard.title");
	}

	protected String getTemplateFolder() {
		return "woapplication";
	}

	@Override
	protected void _createProject(IProject project, IProgressMonitor progressMonitor) throws Exception {
		String templateFolder = getTemplateFolder();
		String projectName = project.getName();
		String path = project.getLocation().toOSString();

		//Java Package support
		String packagePath = "";
		String packageName = "";
		String fullSrcPath = path+File.separator+"src";
		if (_packagePage != null) {
			packageName = _packagePage.getTextData();
			packagePath = _packagePage.getConvertedPath();
			fullSrcPath += File.separator+packagePath;
		}
		createJavaPackageSupport(project, packagePath);

		File bin = new File(path + File.separator + "bin");
		bin.mkdirs();
		// File xcode = new File(path + File.separator + projectName
		// + ".xcode");
		// xcode.mkdirs();
		// File xcodeproj = new File(path + File.separator + projectName
		// + ".xcodeproj");
		// xcodeproj.mkdirs();
		File ant = new File(path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME);
		ant.mkdirs();
		// project.close(nullProgressMonitor);
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.init();
		templateEngine.getWolipsContext().setProjectName(projectName);
		templateEngine.getWolipsContext().setAntFolderName(ProjectPatternsets.ANT_FOLDER_NAME);
		templateEngine.getWolipsContext().setPackageName(packageName);

		addComponentDefinition(templateFolder, templateEngine, path, "Main", packagePath);

		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Application.java.vm", fullSrcPath, "Application.java", "Application.java"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/DirectAction.java.vm", fullSrcPath, "DirectAction.java", "DirectAction.java"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Session.java.vm", fullSrcPath, "Session.java", "Session.java"));

		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/.classpath.vm", path, ".classpath", ".classpath"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/.project.vm", path, ".project", ".project"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/ant.classpaths.user.home.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.classpaths.user.home", "ant.classpaths.user.home"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/ant.classpaths.wo.wolocalroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.classpaths.wo.wolocalroot", "ant.classpaths.wo.wolocalroot"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/ant.classpaths.wo.wosystemroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.classpaths.wo.wosystemroot", "ant.classpaths.wo.wosystemroot"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/ant.frameworks.user.home.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.frameworks.user.home", "ant.frameworks.user.home"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/ant.frameworks.wo.wolocalroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.frameworks.wo.wolocalroot", "ant.frameworks.wo.wolocalroot"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/ant.frameworks.wo.wosystemroot.vm", path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME, "ant.frameworks.wo.wosystemroot", "ant.frameworks.wo.wosystemroot"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/build.xml.vm", path, "build.xml", "build.xml"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/build.properties.vm", path, "build.properties", "build.properties"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/CustomInfo.plist.vm", path, "CustomInfo.plist", "CustomInfo.plist"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Makefile.vm", path, "Makefile.postamble", "Makefile.postamble"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Makefile.postamble.vm", path, "Makefile.postamble", "Makefile.postamble"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Makefile.preamble.vm", path, "Makefile.preamble", "Makefile.preamble"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/PB.project.vm", path, "PB.project", "PB.project"));
		templateEngine.addTemplate(new TemplateDefinition(templateFolder + "/Properties.vm", path, "Properties", "Properties"));
		// templateEngine.addTemplate(new TemplateDefinition(
		// "woapplication/project.pbxproj.vm", path
		// + File.separator + projectName + ".xcode",
		// "project.pbxproj", "project.pbxproj"));

		createWebServicesSupport(project, templateEngine);

		templateEngine.run(progressMonitor);

		createEOModelSupport(project);

	}


}