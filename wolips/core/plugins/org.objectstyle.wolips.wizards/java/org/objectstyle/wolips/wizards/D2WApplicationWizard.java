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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
import org.objectstyle.wolips.wizards.D2WApplicationConfigurationPage.D2WLook;

/**
 * Configures velocity engine to create D2W project as specified in D2WApplicationConfigurationPage
 * @author mnolte
 * @author uli
 */
public class D2WApplicationWizard extends AbstractProjectWizard {

	/**
	 * Default constructor
	 */
	public D2WApplicationWizard() {
		super();
	}

	protected WizardType getWizardType(){
		return WizardType.D2W_APPLICATION_WIZARD;
	}

	public String getWindowTitle() {
		return Messages.getString("D2WApplicationWizard.title");
	}

	/**
	 * Return relative path to template for a given D2W Look
	 * @param look
	 * @return
	 */
	public String pathForTemplateLook(D2WLook look) {

		if (look == D2WLook.BASIC54) {
			return D2WLook.BASIC54.getTemplatePath();
		} else if (look == D2WLook.NEUTRAL54) {
			return D2WLook.NEUTRAL54.getTemplatePath();
		} else if (look == D2WLook.WEBOBJECTS54) {
			return D2WLook.WEBOBJECTS54.getTemplatePath();
		} else if (look == D2WLook.BASIC52) {
			return D2WLook.BASIC52.getTemplatePath();
		} else if (look == D2WLook.NEUTRAL52) {
			return D2WLook.NEUTRAL52.getTemplatePath();
		} else if (look == D2WLook.WEBOBJECTS52) {
			return D2WLook.WEBOBJECTS52.getTemplatePath();
		} else if (look == D2WLook.WONDER) {
			return D2WLook.WONDER.getTemplatePath();
		}

		return D2WLook.BASIC54.getTemplatePath();
	}

	@Override
	protected void postInstallTemplate(IProject project, IProgressMonitor progressMonitor) throws Exception {

		String pathRoot = pathForTemplateLook(currentD2WLook());
		String projectName = project.getName();
		String path = project.getLocation().toOSString();

		//Java Package support
		String packagePath = "";
		String packageName = "";
		String fullSrcPath = path+File.separator+"src";
		if (_packagePage != null) {
			packageName = _packagePage.getPackageName();
			packagePath = _packagePage.getConvertedPath();
			fullSrcPath += File.separator+packagePath;

		}
		createJavaPackageSupport(project, packagePath);

		File bin = new File(path + File.separator + "bin");
		bin.mkdirs();

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.init();
		templateEngine.getWolipsContext().setProjectName(projectName);
		templateEngine.getWolipsContext().setPackageName(packageName);

		addMavenComponentDefinition(pathRoot, templateEngine, path, "Main", packagePath);
		addMavenComponentDefinition(pathRoot, templateEngine, path, "MenuHeader", packagePath);
		addMavenComponentDefinition(pathRoot, templateEngine, path, "PageWrapper", packagePath);

		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/Application.java.vm", fullSrcPath, "Application.java", "Application.java"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/DirectAction.java.vm", fullSrcPath, "DirectAction.java", "DirectAction.java"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/Session.java.vm", fullSrcPath, "Session.java", "Session.java"));

		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/.classpath.vm", path, ".classpath", ".classpath"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/.project.vm", path, ".project", ".project"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/build.xml.vm", path, "build.xml", "build.xml"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/build.properties.vm", path, "build.properties", "build.properties"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/Properties.vm", path, "Properties", "Properties"));
		templateEngine.addTemplate(new TemplateDefinition(pathRoot+"/user.d2wmodel.vm", path, "user.d2wmodel", "user.d2wmodel"));

		createWebServicesSupport(project, templateEngine);

		templateEngine.run(progressMonitor);

	}
}