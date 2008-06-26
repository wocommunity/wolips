/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
package org.objectstyle.wolips.apple.mavenintegration.wizards;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.project.BuildPathManager;
import org.objectstyle.wolips.apple.util.StatusLogger;
import org.objectstyle.wolips.core.resources.internal.build.Nature;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;
import org.objectstyle.wolips.wizards.Messages;

/**
 * Main wizard for creating WebObjects applications linking against Apple Maven repository
 */
public class WOMavenFrameworkProjectWizard extends AbstractMavenProjectWizard {
	private WizardNewProjectCreationPage _aMainPage;
	private WizardNewProjectReferencePage _aReferencePage;
	protected MavenProjectWizardArtifactPage _mavenArtifactPage;
	protected MavenDependenciesProjectWizardPage _mavenDependencyPage;

	@Override
	public String getWindowTitle() {
		return "WebObjects Maven Application";
	}

	@Override
	protected void _createProject(IProject project, IProgressMonitor progressMonitor) throws Exception {
		createDirectoryStructure(project, progressMonitor);
//		createEOModelSupport(project);
		createMaven2EclipseProjectFiles(project, progressMonitor);
	}


	protected MavenProjectWizardArtifactPage createMavenArtifactPage() {
		MavenProjectWizardArtifactPage mavenArtPage = new MavenProjectWizardArtifactPage("MavenArtifactWizardPage");
		mavenArtPage.setTitle(Messages.getString("MavenProjectWizardArtifactPage.title"));
		mavenArtPage.setDescription(Messages.getString("MavenProjectWizardArtifactPage.description"));
		return mavenArtPage;
	}

	protected MavenDependenciesProjectWizardPage createMavenDependencyPage() {
		MavenDependenciesProjectWizardPage dependsPage = new MavenDependenciesProjectWizardPage();
		return dependsPage;
	}

	protected void createMaven2EclipseProjectFiles(final IProject project, IProgressMonitor progressMonitor) {
		final String projectName = project.getName();
		final String[] directories = new String[] {};
		MavenProject mProj = _mavenArtifactPage.getProjectModel(project.getLocation().toOSString()+File.separator+"pom.xml");
		final Model model = mProj.getModel();
		//FIXME: add support for dependencies (MavenDependenciesProjectWizardPage) page -dl
//		model.getDependencies().addAll(Arrays.asList(dependenciesPage.getDependencies()));
		final IClasspathEntry[] classpathEntries = defaultClassPathEntries(project);
		final IPath outputPath = project.getFullPath().append(File.separator+"target");
		final IPath location = null;

		Job job = new Job("Creating project " + projectName) {
			@Override
			public IStatus run(IProgressMonitor monitor) {
				try {
					doFinish(project, location, directories, model, classpathEntries, outputPath, monitor);
					return Status.OK_STATUS;
				} catch(CoreException e) {
					return e.getStatus();
				} finally {
					monitor.done();
				}
			}
		};

		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				IStatus result = event.getResult();
				if(!result.isOK()) {
					MessageDialog.openError(getShell(), "Failed to create project " + projectName, result.getMessage());
				}
			}
		});

		job.schedule();

	}

	/**
	 * Performs the actual project creation.
	 *
	 * <p>
	 * The following steps are executed in the given order:
	 * <ul>
	 *   <li>Create the actual project resource without configuring it yet.</li>
	 *   <li>Create the required Maven2 directories.</li>
	 *   <li>Create the POM file.</li>
	 *   <li>Configure the Maven2 project.</li>
	 *   <li>Add the Maven2 dependencies to the project.</li>
	 * </ul>
	 * </p>
	 *
	 * @param project           The handle for the project to create.
	 * @param location          The location at which to create the project.
	 * @param directories       The Maven2 directories to create.
	 * @param model             The POM containing the project artifact information.
	 * @param classpathEntries  The classpath entries of the project.
	 * @param outputPath        The default output location path to set in the
	 *                          .classpath file of the project.
	 * @param monitor           The monitor for displaying the project creation
	 *                          progress.
	 *
	 * @throws CoreException if any of the above listed actions fails.
	 */
	static void doFinish(IProject project, IPath location, String[] directories, Model model,
			IClasspathEntry[] classpathEntries, IPath outputPath, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creating maven eclipe project files", 5);

		if (!project.isOpen()) {
			project.open(monitor);
		}
		monitor.subTask("Configuring eclipse maven project files");
		createMaven2Project(project, classpathEntries, outputPath);
		monitor.worked(1);

		monitor.subTask("Configuring wolips project nature");
		addWOLipsNatureToProject(project, monitor);
		monitor.worked(1);

	}

	static boolean addWOLipsNatureToProject(IProject project, IProgressMonitor progressMonitor) {
		boolean success = false;
		try {
			success = Nature.addIncrementalApplicationNatureToProject(project, progressMonitor);
		} catch (CoreException e) {
			StatusLogger.getLogger().log(new IllegalStateException("Failed to to finish project creation", e));
		}

		return success;
	}

	/**
	 * Creates the actual Maven2 project by creating a normal Java project based
	 * on the given <code>project</code> and setting the Java as well as the
	 * Maven2 natures on the project. The Java and Maven2 classpath containers
	 * as well as the whole Java classpath are also set up.
	 *
	 * @param project           The project handle for the already existing
	 *                          but unconfigured project resource.
	 * @param classpathEntries  The classpath entries to include in the Java
	 *                          project.
	 * @param outputPath        The default output location path to set in the
	 *                          .classpath file.
	 *
	 * @throws CoreException if some error occurs while configuring the Maven2 project.
	 */
	private static void createMaven2Project(IProject project, IClasspathEntry[] classpathEntries, IPath outputPath)
	throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {JavaCore.NATURE_ID, MavenPlugin.NATURE_ID});
		project.setDescription(description, null);

		IJavaProject javaProject = JavaCore.create(project);

		javaProject.setRawClasspath(addContainersToClasspath(classpathEntries), outputPath, new NullProgressMonitor());
	}

	/**
	 * Adds the Java and Maven2 classpath containers to the given classpath
	 * <code>entries</code>.
	 *
	 * @param entries  A given set of classpath entries.
	 * @return         An array containing all of the initially provided classpath
	 *                 <code>entries</code> as well as the Java and Maven2
	 *                 classpath containers.
	 *                 Is never <code>null</code>.
	 */
	private static IClasspathEntry[] addContainersToClasspath(IClasspathEntry[] entries) {
		IClasspathEntry[] classpath = new IClasspathEntry[entries.length + 2];
		System.arraycopy(entries, 0, classpath, 0, entries.length);

		classpath[classpath.length - 2] = JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER));
		classpath[classpath.length - 1] = BuildPathManager.getDefaultContainerEntry();

		return classpath;
	}

	/**
	 * Add entries for:
	 * main/components
	 * main/java
	 * main/resources
	 * main/webserver-resources
	 * test/java
	 * test/resources
	 * @param project
	 * @return
	 */
	static IClasspathEntry[] defaultClassPathEntries(IProject project) {
		IPath projPath = project.getFullPath();
		IClasspathEntry cpath = JavaCore.newSourceEntry(projPath.append("src"+File.separator+"main"+File.separator+"java"));
		ArrayList<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		entries.add(cpath);

		cpath = JavaCore.newSourceEntry(projPath.append("src"+File.separator+"test"+File.separator+"java"));
		entries.add(cpath);

		cpath = JavaCore.newSourceEntry(projPath.append("src"+File.separator+"main"+File.separator+"resources"));
		entries.add(cpath);

		cpath = JavaCore.newSourceEntry(projPath.append("src"+File.separator+"main"+File.separator+"webserver-resources"));
		entries.add(cpath);

		cpath = JavaCore.newSourceEntry(projPath.append("src"+File.separator+"test"+File.separator+"resources"));
		entries.add(cpath);

		cpath = JavaCore.newSourceEntry(projPath.append("src"+File.separator+"main"+File.separator+"components"));
		entries.add(cpath);

		return entries.toArray(new IClasspathEntry[] {});
	}


	protected void createDirectoryStructure(IProject project, IProgressMonitor progressMonitor) {
		if (_mavenArtifactPage == null) {
			//log/warning here? -dlee
			return;
		}

		String projectName = project.getName();
		String rootProjPath = project.getLocation().toOSString();

		String packagePath = "";
		String packageName = "";
		String componentPath = "";

		//File System layout
		File file;
		String paths[] = _mavenArtifactPage.getDirectoryPaths();
		for (String aPath : paths) {
			file = new File(rootProjPath+File.separator+aPath);
			file.mkdirs();
		}

		//Package directory structure
		if (_packagePage != null) {
			packageName = _packagePage.getPackageName();
			packagePath = rootProjPath+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+_packagePage.getConvertedPath();
			file = new File(packagePath);
			file.mkdirs();
		}

		//The rest
		TemplateEngine templateEngine = new TemplateEngine();
		try {

			templateEngine.init();
			templateEngine.getWolipsContext().setProjectName(projectName);
			templateEngine.getWolipsContext().setPackageName(packageName);
			templateEngine.getWolipsContext().setArtifactId(_mavenArtifactPage.getArtifactId());
			templateEngine.getWolipsContext().setGroupId(_mavenArtifactPage.getGroupId());
			templateEngine.getWolipsContext().setMavenProjectVersion(_mavenArtifactPage.getVersion());

			String woapplicationTemplatePath = "woapplication";

			//maven project files
			templateEngine.addTemplate(new TemplateDefinition("maven/profiles.xml.tmp", rootProjPath, "profiles.xml", "profiles.xml"));
			templateEngine.addTemplate(new TemplateDefinition("maven/frameworkdeploy.xml.tmp", rootProjPath+File.separator+"src"+File.separator+"assembly", "deploy.xml", "deploy.xml"));

			//resources
			templateEngine.addTemplate(new TemplateDefinition("maven/Info.plist", rootProjPath+File.separator+"src"+File.separator+"main"+File.separator+"resources", "Info.plist", "Info.plist"));
			templateEngine.addTemplate(new TemplateDefinition("maven/Properties", rootProjPath+File.separator+"src"+File.separator+"main"+File.separator+"resources", "Properties", "Properties"));

			//component definitions
			if (_mavenArtifactPage.pathIsEnabled("components")) {
				componentPath = rootProjPath+File.separator+"src"+File.separator+"main"+File.separator+"components";
			} else {
				componentPath = rootProjPath+File.separator+"src"+File.separator+"main"+File.separator+"resources";
			}

			addMavenComponentDefinition(woapplicationTemplatePath, templateEngine, componentPath, "Main", packagePath);

			createWebServicesSupport(project, templateEngine);

			templateEngine.run(progressMonitor);

			//Write the pom.xml file out
			MavenProject mProj = _mavenArtifactPage.getProjectModel(rootProjPath+File.separator+"pom.xml");

			try {
				FileWriter fw = new FileWriter(mProj.getFile());
				mProj.writeModel(fw);
			} catch (RuntimeException e) {
				// TODO post an error dialog here
				StatusLogger.getLogger().log(new IllegalStateException("Failed to to finish project creation", e));
			}

		} catch (Exception e) {
			StatusLogger.getLogger().log(new IllegalStateException("Failed to to finish project creation", e));
			//FIXME error dialog?
		}

	}

	@Override
	protected void addMavenComponentDefinition(String templateFolder, TemplateEngine engine, String rootPath, String name, String packagePath) {
		//create component dir
		String componentPath = rootPath + File.separator + name + ".wo";
		File wo = new File(componentPath);
		wo.mkdirs();

		//create src dirs
		File srcPath = new File(packagePath);
		srcPath.mkdirs();

		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".html.vm", componentPath, name + ".html", name + ".html"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".wod.vm", componentPath, name + ".wod", name + ".wod"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".woo.vm", componentPath, name + ".woo", name + ".woo"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".api.vm", componentPath, name + ".api", name + ".api"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".java.vm", packagePath, name + ".java", name + ".java"));
	}

	@Override
	protected WizardType getWizardType() {
		return WizardType.WOMAVENPROJECT;
	}

	@Override
	public void addPages() {
		WizardType wizardType = getWizardType();
		_aMainPage = createMainPage();
		addPage(_aMainPage);
		super.setMainPage(_aMainPage);

		if (wizardType == WizardType.WOMAVENPROJECT || wizardType == WizardType.D2W_APPLICATION_WIZARD || wizardType == WizardType.D2WS_APPLICATION_WIZARD) {
			_packagePage = createPackageSpecifierWizardPage();
			if (_packagePage != null) {
				addPage(_packagePage);
			}
		}

		_aReferencePage = createReferencePage();
		if (_aReferencePage != null) {
			addPage(_aReferencePage);
			super.setReferencePage(_aReferencePage);
		}

//		if (!hasSettingsAvailable()) {
//		//install settings
//		}
//		else{
//		System.out.println("Settings File detected");
//		}

		_mavenArtifactPage = createMavenArtifactPage();
		if (_mavenArtifactPage != null) {
			addPage(_mavenArtifactPage);
		}


		_mavenDependencyPage = createMavenDependencyPage();
		if (_mavenDependencyPage != null) {
			addPage(_mavenDependencyPage);
		}


		_eomodelImportPage = createEOModelImportResourcePage();
		if (_eomodelImportPage != null) {
			addPage(_eomodelImportPage);
		}

		if (wizardType == WizardType.D2W_APPLICATION_WIZARD) {
			_d2wConfigurationPage = createD2WConfigurationPage();

			if (_d2wConfigurationPage != null) {
				addPage(_d2wConfigurationPage);
			}
		}

		if (wizardType == WizardType.WOMAVENPROJECT  || wizardType == WizardType.WO_APPLICATION_WIZARD || wizardType == WizardType.D2W_APPLICATION_WIZARD || wizardType == WizardType.WO_FRAMEWORK_WIZARD) {
			_webservicesSupportPage = createWebServicesSupportPage();
			if (_webservicesSupportPage != null) {
				addPage(_webservicesSupportPage);
			}
		}
	}

}
