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
package org.objectstyle.wolips.wizards;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.woenvironment.pb.PBProject;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.project.IWOLipsProject;
import org.objectstyle.wolips.core.project.WOLipsCore;
import org.objectstyle.wolips.core.project.WOLipsJavaProject;
import org.objectstyle.wolips.core.util.StringUtilities;
import org.objectstyle.wolips.wizards.templates.XercesDocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * @author mnolte
 * @author uli
 *
 * Create new project files based on xml template in webobjects.template.directory
 * named webobjects.template.project (see plugin.properties)
 * 
 * Template must have following DTD:<br><br>
 * &lt;!DOCTYPE projectTypes [<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT
 *		projectTypes (projectType+)&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT
 *		projectType (nature*, classpath?,
 *		files)&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ATTLIST
 *		projectType<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;name CDATA
 *		#REQUIRED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;id
 *		ID #REQUIRED<BR>
 *		&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT nature
 *		EMPTY&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ATTLIST
 *		nature<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;id CDATA
 *		#REQUIRED<BR>
 *		&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT
 *		classpath (classpathentry*)&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ATTLIST
 *		classpath<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;variables CDATA
 *		#IMPLIED<BR>
 *		&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT
 *		classpathentry EMPTY&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ATTLIST
 *		classpathentry<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;kind (lib | output
 *		| src | var) #REQUIRED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;path
 *		CDATA #REQUIRED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;rootpath
 *		CDATA #IMPLIED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sourcepath
 *		CDATA #IMPLIED<BR>
 *		&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT files
 *		(file+)&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT file
 *		EMPTY&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ATTLIST
 *		file<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;templateId
 *		(application | session | directAction |
 *		project | project.plist | subproject |
 *		framework.project | framework.plist |
 *		properties | makefile | makefile.preamble |
 *		makefile.postamble) #REQUIRED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name
 *		CDATA #REQUIRED<BR>
 *		&gt;<BR>
 *		]&gt;<BR>
 * 
 */
public class WOProjectCreator extends WOProjectResourceCreator {
	private static Document templateDocument;
	private Element elementForTemplate;
	private String templateID;
	private ArrayList variableList;
	private IPath locationPath;
	private IPath importPath;
	/**
	 * Constructor for WOProjectCreator.
	 */
	public WOProjectCreator(IResource newProject, String templateID) {
		super(newProject);
		this.templateID = templateID;
	}
	/**
	 * Constructor for WOProjectCreator.
	 */
	public WOProjectCreator(
		IResource newProject,
		String templateID,
		IPath locationPath,
		IPath importPath) {
		this(newProject, templateID);
		// validate location path
		this.locationPath =
			locationPath.makeAbsolute().toFile().isDirectory()
				? locationPath
				: null;
		if (importPath != null) {
			// validate import path
			this.importPath =
				importPath.makeAbsolute().toFile().isDirectory()
					? importPath
					: null;
		}
	}
	/**
	 * @see org.objectstyle.wolips.wizards.WOProjectResourceCreator#getType()
	 */
	protected int getType() {
		return PROJECT_CREATOR;
	}
	/**
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask(
				Messages.getString("WOProjectCreator.progress.title"),
				10);
			if (elementForTemplate == null) {
				elementForTemplate =
					getProjectTemplateDocument().getElementById(templateID);
			}
			// search for project natures in project template
			NodeList natureNodeList =
				elementForTemplate.getElementsByTagName("nature");
			String[] natureIds = new String[natureNodeList.getLength()];
			Node currentNatureIdAttribute = null;
			for (int i = 0; i < natureNodeList.getLength(); i++) {
				currentNatureIdAttribute =
					natureNodeList.item(i).getAttributes().getNamedItem("id");
				natureIds[i] = currentNatureIdAttribute.getNodeValue();
			}
			configNewProject(natureIds, monitor);
			createProjectContents(monitor);
			if (importPath != null
				&& !importPath.equals(
					parentResource.getLocation().removeLastSegments(1))) {
				// project content path is no default
				// add additional project resources from location path
				monitor.subTask(
					Messages.getString("WOProjectCreator.progress.import"));
				addAdditionalContents(
					monitor,
					(IContainer) this.parentResource,
					importPath.toFile());
				// try to add any existing classpath entries
				addAdditionalFrameworks(monitor, importPath.toFile());
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}
	}
	/**
	 * Method createProjectContents.
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	private void createProjectContents(IProgressMonitor monitor)
		throws InvocationTargetException {
		WOComponentCreator componentCreator =
			new WOComponentCreator(parentResource, "Main", true, true, true);
		NodeList fileNodeList = elementForTemplate.getElementsByTagName("file");
		try {
			String currentFileTemplateId = null;
			String currentFileName = null;
			for (int i = 0; i < fileNodeList.getLength(); i++) {
				currentFileTemplateId =
					fileNodeList
						.item(i)
						.getAttributes()
						.getNamedItem("templateId")
						.getNodeValue();
				currentFileName =
					fileNodeList
						.item(i)
						.getAttributes()
						.getNamedItem("name")
						.getNodeValue();
				createNewProjectResource(
					currentFileName,
					currentFileTemplateId,
					parentResource,
					monitor);
			}
			if (Messages
				.getString("webobjects.projectType.java.application")
				.equals(templateID)) {
				// create main component
				componentCreator.createWOComponent(monitor);
			}
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}
	/**
	 * Method createNewProjectResource.
	 * @param fileName
	 * @param fileTemplateId
	 * @param parentResource
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	private void createNewProjectResource(
		String fileName,
		String fileTemplateId,
		IResource parentResource,
		IProgressMonitor monitor)
		throws InvocationTargetException {
		boolean isJavaFile =
			fileName != null
				&& fileName.endsWith("." + IWOLipsPluginConstants.EXT_JAVA);
		IFile fileToCreate = null;
		WOLipsJavaProject wolipsJavaProject = null;
		switch (parentResource.getType()) {
			case IResource.PROJECT :
				if (isJavaFile) {
					// add java file to source folder
					wolipsJavaProject =
						new WOLipsJavaProject(
							JavaCore.create((IProject) parentResource));
					fileToCreate =
						wolipsJavaProject
							.getClasspathAccessor()
							.getProjectSourceFolder()
							.getFile(
							new Path(fileName));
				} else {
					// add wo resource file
					fileToCreate =
						((IProject) parentResource).getFile(fileName);
				}
				try {
					fileCreator().create(
						fileToCreate,
						fileTemplateId,
						new SubProgressMonitor(monitor, 1));
				} catch (InvocationTargetException e) {
					// a this stage of project creation no project file exists
					// throw only non-missing project file exceptions
					//if (!(e.getTargetException() instanceof WOProjectFileUpdater.MissingProjectFileException)) {
					throw e;
					//}
				}
				break;
			case IResource.FOLDER :
				if (isJavaFile) {
					// add java file to source folder
					wolipsJavaProject =
						new WOLipsJavaProject(
							JavaCore.create(parentResource.getProject()));
					fileToCreate =
						wolipsJavaProject
							.getClasspathAccessor()
							.getSubprojectSourceFolder(
								(IFolder) parentResource,
								true)
							.getFile(fileName);
				} else {
					// add wo resource file
					fileToCreate = ((IFolder) parentResource).getFile(fileName);
				}
				fileCreator().create(
					fileToCreate,
					fileTemplateId,
					new SubProgressMonitor(monitor, 1));
				break;
			default :
				throw new InvocationTargetException(
					new Exception("Wrong parent resource - check validation"));
		}
	}
	/**
	 * Method configNewProject.
	 * @param natureIds
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	private void configNewProject(String[] natureIds, IProgressMonitor monitor)
		throws InvocationTargetException {
		switch (parentResource.getType()) {
			// application project or framework project
			case IResource.PROJECT :
				final IProject newProjectHandle = (IProject) parentResource;
				try {
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					final IProjectDescription description =
						workspace.newProjectDescription(
							newProjectHandle.getName());
					description.setLocation(locationPath);
					description.setNatureIds(natureIds);
					if (!newProjectHandle.exists()) {
						// set description only in this way
						// to ensure project location is set
						newProjectHandle.create(
							description,
							new SubProgressMonitor(monitor, 1000));
					}
					if (!newProjectHandle.isOpen()) {
						newProjectHandle.open(null);
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
				// add wo classpath entries
				IJavaProject myJavaProject = JavaCore.create(newProjectHandle);
				try {
					IWOLipsProject wolipsProject =
						WOLipsCore.createProject(myJavaProject.getProject());
					wolipsProject.getBuilderAccessor().installJavaBuilder();
				} catch (Exception anException) {
					WOLipsLog.log(anException);
				}
				try {
					//newProjectHandle.setDescription(newProjectHandle.getDescription(), new SubProgressMonitor(monitor, 1000));
					IWOLipsProject woLipsProject =
						WOLipsCore.createProject(newProjectHandle);
					woLipsProject.getNaturesAccessor().callConfigure();
				} catch (Exception anException) {
					WOLipsLog.log(anException);
				}
				NodeList classpathNodeList =
					elementForTemplate.getElementsByTagName("classpathentry");
				ArrayList allClasspathEntriesResolved =
					new ArrayList(classpathNodeList.getLength());
				if (classpathNodeList.getLength() > 0) {
					StringBuffer currentResolvedClassPath = null;
					String currentRawPath = null;
					String currentRawSourcePath = null;
					String currentRawRootPath = null;
					String variableToExpand = null;
					String classpathKind = null;
					IPath newClasspath = null;
					IPath newSourcepath = null;
					IPath newRootpath = null;
					int index = -1;
					for (int i = 0; i < classpathNodeList.getLength(); i++) {
						classpathKind =
							classpathNodeList
								.item(i)
								.getAttributes()
								.getNamedItem("kind")
								.getNodeValue();
						currentRawPath =
							classpathNodeList
								.item(i)
								.getAttributes()
								.getNamedItem("path")
								.getNodeValue();
						if (classpathNodeList
							.item(i)
							.getAttributes()
							.getNamedItem("sourcepath")
							!= null) {
							currentRawSourcePath =
								classpathNodeList
									.item(i)
									.getAttributes()
									.getNamedItem("sourcepath")
									.getNodeValue();
						}
						if (classpathNodeList
							.item(i)
							.getAttributes()
							.getNamedItem("rootpath")
							!= null) {
							currentRawRootPath =
								classpathNodeList
									.item(i)
									.getAttributes()
									.getNamedItem("rootpath")
									.getNodeValue();
						}
						////////////////// kind "src"
						if (IWOLipsPluginConstants
							.EXT_SRC
							.equals(classpathKind)) {
							// set source path extension
							if (!".".equals(currentRawPath) && false) {
								currentRawPath += "."
									+ IWOLipsPluginConstants.EXT_SRC;
							} else if (false) {
								// source path must be folder to create
								// non-nested source path folder for subprojects
								currentRawPath =
									parentResource.getName()
										+ "."
										+ IWOLipsPluginConstants.EXT_SRC;
							}
							IPath sourcePath;
							// is path project name depending
							if (currentRawPath.indexOf("${project.name}")
								!= -1) {
								while ((index =
									currentRawPath.indexOf("${project.name}"))
									!= -1) {
									StringBuffer resolvedOutputPath =
										new StringBuffer(currentRawPath);
									resolvedOutputPath.replace(
										index,
										index + "${project.name}".length(),
										parentResource.getName());
									currentRawPath =
										resolvedOutputPath.toString();
								}
							}
							sourcePath = new Path(currentRawPath);
							IFolder sourceFolder;
							sourceFolder =
								newProjectHandle.getFolder(sourcePath);
							if (!sourceFolder.exists()) {
								// create source folder
								try {
									sourceFolder.create(true, true, monitor);
								} catch (CoreException e) {
									throw new InvocationTargetException(e);
								}
							}
							allClasspathEntriesResolved.add(
								JavaCore.newSourceEntry(
									sourceFolder.getFullPath()));
						}
						////////////////// kind "lib"
						if ("lib".equals(classpathKind)) {
							currentResolvedClassPath =
								new StringBuffer(currentRawPath);
							for (int j = 0; j < variableList().size(); j++) {
								variableToExpand =
									(String) variableList().get(j);
								// replace all occurences of "${" + variableToExpand + "}"
								while ((index =
									currentRawPath.indexOf(
										"${" + variableToExpand + "}"))
									!= -1) {
									currentResolvedClassPath.replace(
										index,
										index + variableToExpand.length() + 3,
										expandVariable(variableToExpand));
									currentRawPath =
										currentResolvedClassPath.toString();
								}
							}
							newClasspath =
								new Path(currentResolvedClassPath.toString());
							allClasspathEntriesResolved.add(
								JavaCore.newLibraryEntry(
									newClasspath,
									null,
									null));
						}
						////////////////// kind "var"
						else if ("var".equals(classpathKind)) {
							newClasspath = new Path(currentRawPath);
							if (currentRawSourcePath != null) {
								newSourcepath = new Path(currentRawSourcePath);
							}
							if (currentRawRootPath != null) {
								newRootpath = new Path(currentRawRootPath);
							}
							allClasspathEntriesResolved.add(
								JavaCore.newVariableEntry(
									newClasspath,
									newSourcepath,
									newRootpath));
						}
						////////////////// kind "var"
						else if ("con".equals(classpathKind)) {
							newClasspath = new Path(currentRawPath);
							allClasspathEntriesResolved.add(
								JavaCore.newContainerEntry(newClasspath));
						}
						////////////////// kind "output
						else if ("output".equals(classpathKind)) {
							IPath absoluteOutputPath = null;
							// is path project name depending
							if (currentRawPath.indexOf("${project.name}")
								!= -1) {
								while ((index =
									currentRawPath.indexOf("${project.name}"))
									!= -1) {
									StringBuffer resolvedOutputPath =
										new StringBuffer(currentRawPath);
									resolvedOutputPath.replace(
										index,
										index + "${project.name}".length(),
										parentResource.getName());
									currentRawPath =
										resolvedOutputPath.toString();
								}
								absoluteOutputPath =
									parentResource.getFullPath().append(
										currentRawPath);
							} else {
								absoluteOutputPath = new Path(currentRawPath);
							}
							if (absoluteOutputPath.isAbsolute()) {
								try {
									myJavaProject.setOutputLocation(
										absoluteOutputPath,
										monitor);
								} catch (JavaModelException e) {
									throw new InvocationTargetException(e);
								}
							}
						}
					}
				}
				try {
					Object[] classPathEntriesAsObjects =
						allClasspathEntriesResolved.toArray();
					IClasspathEntry[] classPathEntries =
						new IClasspathEntry[classPathEntriesAsObjects.length];
					for (int i = 0; i < classPathEntries.length; i++) {
						classPathEntries[i] =
							(IClasspathEntry) classPathEntriesAsObjects[i];
					}
					myJavaProject.setRawClasspath(classPathEntries, monitor);
				} catch (JavaModelException e) {
					throw new InvocationTargetException(e);
				}
				break;
			case IResource.FOLDER :
				// subproject
				IFolder newSubproject = (IFolder) parentResource;
				IFolder newSubprojectSourceFolder =
					newSubproject.getFolder(
						new Path(IWOLipsPluginConstants.EXT_SRC));
				try {
					if (!parentResource.exists()) {
						createResourceFolderInProject(newSubproject, monitor);
					}
					if (!newSubprojectSourceFolder.exists()) {
						newSubprojectSourceFolder.create(false, true, monitor);
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
				WOLipsJavaProject wolipsJavaProject =
					new WOLipsJavaProject(
						JavaCore.create(parentResource.getProject()));
				wolipsJavaProject
					.getClasspathAccessor()
					.addNewSourcefolderToClassPath(
					newSubprojectSourceFolder,
					monitor);
				// subproject
				break;
			default :
				throw new InvocationTargetException(
					new Exception("Wrong parent resource - check validation"));
		}
	}
	/**
	 * Method expandVariable, expands found variable (${[variable]}) to the
	 * appropiate value found in plugin properties.
	 * <br>
	 * @param variableToExpand
	 * @return found value or null
	 */
	private String expandVariable(String variableToExpand) {
		return WOLipsCore
			.getClasspathVariablesAccessor()
			.classPathVariableToExpand(
			variableToExpand);
	}
	/**
	 * Method variableList. List of variables to expand from templates
	 * classpath.
	 * <br>
	 * @return ArrayList
	 */
	private ArrayList variableList() {
		if (variableList == null) {
			// build list of variables to expand
			String variablesToExpand =
				elementForTemplate
					.getElementsByTagName("classpath")
					.item(0)
					.getAttributes()
					.getNamedItem("variables")
					.getNodeValue();
			variableList = StringUtilities.arrayListFromCSV(variablesToExpand);
		}
		return variableList;
	}
	/**
	 * Method getProjectTemplateDocument.
	 * @return Document
	 * @throws InvocationTargetException
	 */
	private static synchronized Document getProjectTemplateDocument()
		throws InvocationTargetException {
		if (templateDocument != null)
			return templateDocument;
		try {
			InputStream input =
				(new URL(WOLipsPlugin.baseURL(),
					Messages.getString("webobjects.template.directory")
						+ "/"
						+ Messages.getString("webobjects.template.project")))
					.openStream();
			templateDocument =
				XercesDocumentBuilder.documentBuilder().parse(input);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
		return templateDocument;
	}
	/**
	 * Method addAdditionalFrameworks.
	 * @param monitor
	 * @param additionalResourceDir
	 */
	private void addAdditionalFrameworks(
		IProgressMonitor monitor,
		File additionalResourceDir) {
		File projectFile = new File(additionalResourceDir, PROJECT_FILE_NAME);
		if (this.parentResource instanceof IProject
			&& projectFile.exists()
			&& !projectFile.isDirectory()) {
			boolean isFramework =
				EXT_FRAMEWORK.equals(this.parentResource.getFileExtension());
			// try to add frameworks from project file
			PBProject pbProject;
			try {
				pbProject = new PBProject(projectFile, isFramework);
			} catch (IOException e) {
				WOLipsLog.log(e);
				return;
			}
			IJavaProject newJavaProject =
				JavaCore.create((IProject) parentResource);
			try {
				WOLipsJavaProject wolipsJavaProject =
					new WOLipsJavaProject(newJavaProject);
				IClasspathEntry[] newClasspathEntries =
					wolipsJavaProject
						.getClasspathAccessor()
						.addFrameworkListToClasspathEntries(
						pbProject.getFrameworks());
				newJavaProject.setRawClasspath(newClasspathEntries, monitor);
			} catch (JavaModelException e) {
				WOLipsLog.log(e);
			}
		}
	}
	/**
	 * Method addAdditionalContents.
	 * @param monitor
	 * @param parentContainer
	 * @param additionalResourcesDir
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void addAdditionalContents(
		IProgressMonitor monitor,
		IContainer parentContainer,
		File additionalResourcesDir)
		throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Copying project contents", IProgressMonitor.UNKNOWN);
		CopyFileToResource copy;
		File projectFile = new File(additionalResourcesDir, PROJECT_FILE_NAME);
		// init directory scanner includes/excludes
		String[] resourcesIncludes;
		String[] resourcesExcludes =
			new String[] {
				PROJECT_FILE_NAME,
				"CVS",
				"*." + EXT_WOA,
				"*." + EXT_BUILD,
				"*." + EXT_SUBPROJECT,
				"*." + EXT_JAVA };
		String[] classesIncludes;
		String[] classesExcludes =
			new String[] {
				PROJECT_FILE_NAME,
				"CVS",
				"*." + EXT_WOA,
				"*." + EXT_BUILD,
				"*." + EXT_SUBPROJECT };
		String[] subprojectsIncludes;
		String[] subprojectsExcludes =
			new String[] { "CVS", "*." + EXT_WOA, "*." + EXT_BUILD };
		PBProject pbProject = null;
		if (projectFile.exists()) {
			// try to set includes from PB.Project
			try {
				pbProject =
					new PBProject(
						projectFile,
						additionalResourcesDir.getName().endsWith(
							"." + EXT_FRAMEWORK));
				List woAppResources =
					pbProject.getWoAppResources() != null
						? pbProject.getWoAppResources()
						: new ArrayList();
				List woComponents =
					pbProject.getWoComponents() != null
						? pbProject.getWoComponents()
						: new ArrayList();
				// wo app resources and wocomponents
				resourcesIncludes =
					new String[woAppResources.size() + woComponents.size()];
				for (int i = 0; i < woAppResources.size(); i++) {
					resourcesIncludes[i] = (String) woAppResources.get(i);
				}
				for (int i = 0; i < woComponents.size(); i++) {
					resourcesIncludes[i + woAppResources.size()] =
						(String) woComponents.get(i);
				}
				// java files
				List classes =
					pbProject.getClasses() != null
						? pbProject.getClasses()
						: new ArrayList();
				classesIncludes = new String[classes.size()];
				for (int i = 0; i < classes.size(); i++) {
					classesIncludes[i] = (String) classes.get(i);
				}
				// subprojects
				// java files
				List subprojects =
					pbProject.getSubprojects() != null
						? pbProject.getSubprojects()
						: new ArrayList();
				subprojectsIncludes = new String[subprojects.size()];
				for (int i = 0; i < subprojects.size(); i++) {
					subprojectsIncludes[i] = (String) subprojects.get(i);
				}
			} catch (IOException e) {
				resourcesIncludes = new String[] { "*" };
				classesIncludes = new String[] { "*." + EXT_JAVA };
				subprojectsIncludes = new String[] { "*." + EXT_SUBPROJECT };
				WOLipsLog.log(e);
			}
		} else {
			resourcesIncludes = new String[] { "*" };
			classesIncludes = new String[] { "*." + EXT_JAVA };
			subprojectsIncludes = new String[] { "*." + EXT_SUBPROJECT };
		}
		// scan directory for non subproject resources
		DirectoryScanner ds = new DirectoryScanner();
		ds.setBasedir(additionalResourcesDir);
		ds.setCaseSensitive(true);
		ds.setExcludes(resourcesExcludes);
		ds.setIncludes(resourcesIncludes);
		ds.scan();
		String[] foundFiles = ds.getIncludedFiles();
		// copy found files to actual project's parent container
		for (int i = 0; i < foundFiles.length; i++) {
			try {
				copy =
					new CopyFileToResource(
						additionalResourcesDir,
						parentContainer,
						foundFiles[i],
						monitor);
			} catch (FileNotFoundException e) {
				WOLipsLog.log(e);
				continue;
			}
			try {
				copy.execute();
			} catch (CoreException e) {
				WOLipsLog.log(e);
				continue;
			}
		}
		String[] foundDirs = ds.getIncludedDirectories();
		for (int i = 0; i < foundDirs.length; i++) {
			if (foundDirs[i].endsWith("." + EXT_COMPONENT)
				|| foundDirs[i].endsWith("." + EXT_EOMODEL)) {
				try {
					copy =
						new CopyFileToResource(
							additionalResourcesDir,
							parentContainer,
							foundDirs[i],
							monitor);
				} catch (FileNotFoundException e) {
					WOLipsLog.log(e);
					continue;
				}
				try {
					copy.execute();
				} catch (CoreException e) {
					WOLipsLog.log(e);
					continue;
				}
			}
		}
		// scan directory for java classes
		ds.setExcludes(classesExcludes);
		ds.setIncludes(classesIncludes);
		ds.scan();
		String[] foundJavaFiles = ds.getIncludedFiles();
		if (foundJavaFiles.length > 0) {
			IContainer sourceFolder = null;
			WOLipsJavaProject wolipsJavaProject = null;
			switch (parentContainer.getType()) {
				case IResource.PROJECT :
					wolipsJavaProject =
						new WOLipsJavaProject(
							JavaCore.create((IProject) parentContainer));
					sourceFolder =
						wolipsJavaProject
							.getClasspathAccessor()
							.getProjectSourceFolder();
					break;
				case IResource.FOLDER :
					wolipsJavaProject =
						new WOLipsJavaProject(
							JavaCore.create(parentContainer.getProject()));
					sourceFolder =
						wolipsJavaProject
							.getClasspathAccessor()
							.getSubprojectSourceFolder(
							(IFolder) parentContainer,
							true);
					break;
			}
			if (sourceFolder != null) {
				for (int i = 0; i < foundJavaFiles.length; i++) {
					try {
						copy =
							new CopyFileToResource(
								additionalResourcesDir,
								sourceFolder,
								foundJavaFiles[i],
								monitor);
					} catch (FileNotFoundException e) {
						WOLipsLog.log(e);
						continue;
					}
					try {
						copy.execute();
					} catch (CoreException e) {
						WOLipsLog.log(e);
						continue;
					}
				}
			}
		}
		// scan directory for subproject resources
		ds.setExcludes(subprojectsExcludes);
		ds.setIncludes(subprojectsIncludes);
		ds.scan();
		String[] foundSubprojects = ds.getIncludedDirectories();
		WOSubprojectCreator currentSubprojectCreator;
		String currentSubprojectName;
		for (int i = 0; i < foundSubprojects.length; i++) {
			if (foundSubprojects[i].indexOf("." + EXT_SUBPROJECT) != -1) {
				currentSubprojectName =
					foundSubprojects[i].substring(
						0,
						foundSubprojects[i].lastIndexOf("." + EXT_SUBPROJECT));
			} else {
				currentSubprojectName = foundSubprojects[i];
			}
			currentSubprojectCreator =
				new WOSubprojectCreator(parentContainer, currentSubprojectName);
			try {
				currentSubprojectCreator.createSubproject(
					new SubProgressMonitor(monitor, 1));
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
			IContainer subprojectContainer =
				parentContainer.getFolder(new Path(foundSubprojects[i]));
			File additionalSubprojectResourcesFile =
				new File(additionalResourcesDir, foundSubprojects[i]);
			// recursively add subproject resources
			addAdditionalContents(
				new SubProgressMonitor(monitor, 1),
				subprojectContainer,
				additionalSubprojectResourcesFile);
		}
	}
	/**
	 * @author uli
	 *To change this generated comment edit the template variable "typecomment":
	 *Window>Preferences>Java>Templates. To enable and disable the creation of
	 *type comments go to Window>Preferences>Java>Code Generation.
	 */
	private class CopyFileToResource {
		private IFile resourceFileToCreate;
		private IFolder resourceFolderToCreate;
		private File sourceFile;
		private IProgressMonitor monitor;
		/**
		 * Constructor for CopyFileToResource.
		 */
		public CopyFileToResource(
			File baseDir,
			IContainer destContainer,
			String sourceFileName,
			IProgressMonitor monitor)
			throws FileNotFoundException {
			super();
			sourceFile = new File(baseDir, sourceFileName);
			if (!"CVS".equals(sourceFile.getName())
				&& !"Icon?".equals(sourceFile.getName())) {
				if (sourceFile.isDirectory()) {
					resourceFolderToCreate =
						destContainer.getFolder(new Path(sourceFileName));
				} else {
					resourceFileToCreate =
						destContainer.getFile(new Path(sourceFileName));
				}
			}
			this.monitor = monitor;
		}
		/**
		 * Method execute.
		 * @throws CoreException
		 */
		public void execute() throws CoreException {
			if (resourceFileToCreate == null) {
				this.executeWithNOResourceFileToCreate();
				return;
			}
			BufferedInputStream resourceFileInputStream = null;
			try {
				resourceFileInputStream =
					new BufferedInputStream(new FileInputStream(sourceFile));
			} catch (FileNotFoundException e) {
				// nothing file exists (see constructor)
			}
			try {
				if (resourceFileToCreate.exists()) {
					resourceFileToCreate.setContents(
						resourceFileInputStream,
						true,
						true,
						monitor);
				} else {
					resourceFileToCreate.create(
						resourceFileInputStream,
						true,
						monitor);
				}
			} finally {
				try {
					if (resourceFileInputStream != null)
						resourceFileInputStream.close();
				} catch (IOException e) {
				}
			}
		}
		/**
		 * Method executeWithNOResourceFileToCreate.
		 * @throws CoreException
		 */
		private void executeWithNOResourceFileToCreate() throws CoreException {
			if (resourceFolderToCreate == null)
				return;
			this.createIfNotExist(resourceFolderToCreate);
			String[] directoryContent = sourceFile.list();
			for (int i = 0; i < directoryContent.length; i++) {
				try {
					CopyFileToResource copy =
						new CopyFileToResource(
							sourceFile,
							resourceFolderToCreate,
							directoryContent[i],
							monitor);
					copy.execute();
				} catch (Exception e) {
					WOLipsLog.log(e);
					continue;
				}
			}
		}
		/**
		 * Method createIfNotExist.
		 * @param aFolder
		 * @throws CoreException
		 */
		private void createIfNotExist(IFolder aFolder) throws CoreException {
			if (aFolder.exists())
				return;
			aFolder.create(true, true, monitor);
		}
	}
}
