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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.preferences.NewJavaProjectPreferencePage;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.io.FileFromTemplateCreator;
import org.objectstyle.wolips.io.FileFromTemplateCreator.FileCreationException;
import org.objectstyle.wolips.wo.WOVariables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author mnolte
 * @author uli
 *
 * Create new project files based on xml template in webobjects.template.directory
 * named webobjects.template.project (see plugin.properties)
 * 
 * Template must have following DTD:<br><br>
 * <!DOCTYPE projectTypes [<br>
*	<!ELEMENT projectTypes (projectType+)><br>
*	<!ELEMENT projectType (nature+, classpath, files)><br>
*	<!ATTLIST projectType<br>
*	name CDATA #REQUIRED<br>
*		id ID #REQUIRED<br>
*><br>
*	<!ELEMENT nature EMPTY><br>
*	<!ATTLIST nature<br>
*	id CDATA #REQUIRED<br>
*><br>
*	<!ELEMENT classpath (classpathentry+)><br>
*	<!ATTLIST classpath<br>
*	variables CDATA #REQUIRED<br>
*><br>
*	<!ELEMENT classpathentry EMPTY><br>
*	<!ATTLIST classpathentry<br>
*	kind (lib | output | src | var) #REQUIRED<br>
*		path CDATA #REQUIRED<br>
*		rootpath CDATA #IMPLIED<br>
*		sourcepath CDATA #IMPLIED<br>
*><br>
*	<!ELEMENT files (file+)><br>
*	<!ELEMENT file EMPTY><br>
*	<!ATTLIST file<br>
*	templateId (application | session | directAction | project | makefile | makefile.preamble | makefile.postamble) #REQUIRED
*		name CDATA #REQUIRED<br>
*><br>
*]><br>
 * 
 */
public class NewWOProjectCreator implements IRunnableWithProgress {

	private IProject newProject;
	private static Document templateDocument;
	private Element elementForTemplate;
	private String templateID;
	private ArrayList variableList;
	/**
	 * Constructor for WOProjectCreator.
	 */
	public NewWOProjectCreator(IProject newProject, String templateID) {
		super();
		this.newProject = newProject;
		this.templateID = templateID;
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
				NewWOProjectMessages.getString("WOProjectCreator.progress.title"),
				1);

			if (elementForTemplate == null) {
				elementForTemplate =
					getProjectTemplateDocument().getElementById(templateID);
			}

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

		} finally {
			monitor.done();
		}
	}

	private void createProjectContents(IProgressMonitor monitor)
		throws InvocationTargetException {
		//configNewProject(String[] natureIds, IProgressMonitor monitor)
		FileFromTemplateCreator fileCreator = new FileFromTemplateCreator();

		NewWOComponentCreator componentCreator =
			new NewWOComponentCreator(newProject);
		componentCreator.setFileCreator(fileCreator);

		NodeList fileNodeList = elementForTemplate.getElementsByTagName("file");
		try {
			
			//create src folder
			
			newProject.getFolder("src").create(true,true,monitor);
			
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
				fileCreator.create(
					newProject.getFile(currentFileName),
					currentFileTemplateId,
					new SubProgressMonitor(monitor, 1));
			}
			if (WOVariables.woProjectTypeJavaApplication().equals(templateID)) {
				// create main component
				componentCreator.createWOComponentNamed("Main", true, monitor);
			}

		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} catch (FileCreationException e) {
			throw new InvocationTargetException(e);
		}

	}

	private void configNewProject(String[] natureIds, IProgressMonitor monitor)
		throws InvocationTargetException {

		try {
			if (!newProject.exists()) {
				newProject.create(null);
			}
			if (!newProject.isOpen()) {
				newProject.open(null);
			}
			IProjectDescription desc = newProject.getDescription();
			//desc.setLocation(null);
			desc.setNatureIds(natureIds);

			newProject.setDescription(desc, monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}

		// test
		String[] cpVariables = JavaCore.getClasspathVariableNames();

		// add wo classpath entries
		IJavaProject myJavaProject = JavaCore.create(newProject);
		NodeList classpathNodeList =
			elementForTemplate.getElementsByTagName("classpathentry");

		int existentClasspathEntriesCnt = 0;
		IClasspathEntry[] existentClasspathEntries;

		try {
			existentClasspathEntries = myJavaProject.getRawClasspath();
			existentClasspathEntriesCnt =
				myJavaProject.getRawClasspath().length;
				WOLipsPlugin.debug("existentClasspathEntries: " + existentClasspathEntries);
				for (int x = 0; x < existentClasspathEntriesCnt; x++) {
					IClasspathEntry anIClasspathEntry = existentClasspathEntries[x];
					WOLipsPlugin.debug("IClasspathEntry.CPE_SOURCE: " + IClasspathEntry.CPE_SOURCE);
					WOLipsPlugin.debug("ContentKind: " + existentClasspathEntries[x].getContentKind());
					WOLipsPlugin.debug("EntryKind: " + existentClasspathEntries[x].getEntryKind());
					WOLipsPlugin.debug("path: " + existentClasspathEntries[x].getPath());
					if(anIClasspathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						existentClasspathEntries[x] = JavaCore.newSourceEntry(anIClasspathEntry.getPath().addTrailingSeparator().append("src"));
					}
				}
				
		} catch (JavaModelException e) {
			throw new InvocationTargetException(e);
		}

		IClasspathEntry[] allClasspathEntriesResolved =
			new IClasspathEntry[existentClasspathEntriesCnt
				+ classpathNodeList.getLength()];

		// copy existant classpath entries
		for (int i = 0; i < existentClasspathEntriesCnt; i++) {
			allClasspathEntriesResolved[i] = existentClasspathEntries[i];
		}

		if (classpathNodeList.getLength() > 0) {

			StringBuffer currentResolvedClassPath = null;
			String currentRawClassPath = null;
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

				currentRawClassPath =
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

				if ("lib".equals(classpathKind)) {

					currentResolvedClassPath =
						new StringBuffer(currentRawClassPath);

					for (int j = 0; j < variableList().size(); j++) {
						variableToExpand = (String) variableList().get(j);

						// replace all occurences of "${" + variableToExpand + "}"
						while ((index =
							currentRawClassPath.indexOf(
								"${" + variableToExpand + "}"))
							!= -1) {
							currentResolvedClassPath.replace(
								index,
								index + variableToExpand.length() + 3,
								WOVariables.classPathVariableToExpand(variableToExpand));

							currentRawClassPath =
								currentResolvedClassPath.toString();

						}
					}

					newClasspath =
						new Path(currentResolvedClassPath.toString());
					allClasspathEntriesResolved[existentClasspathEntriesCnt
						+ i] =
						JavaCore.newLibraryEntry(newClasspath, null, null);
				}
				else if ("var".equals(classpathKind)) {

					newClasspath = new Path(currentRawClassPath);

					if (currentRawSourcePath != null) {
						newSourcepath = new Path(currentRawSourcePath);
					}

					if (currentRawRootPath != null) {
						newRootpath = new Path(currentRawRootPath);
					}

					allClasspathEntriesResolved[existentClasspathEntriesCnt
						+ i] =
						JavaCore.newVariableEntry(
							newClasspath,
							newSourcepath,
							newRootpath);
				}
			}

		}
		try {
			myJavaProject.setRawClasspath(allClasspathEntriesResolved, monitor);
		} catch (JavaModelException e) {
			throw new InvocationTargetException(e);
		}
		//ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(newProject.getFile("Application.java"));
	}

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

			StringTokenizer variableTokenizer =
				new StringTokenizer(variablesToExpand, ",");
			variableList = new ArrayList(variableTokenizer.countTokens());
			while (variableTokenizer.hasMoreElements()) {
				variableList.add(variableTokenizer.nextElement());
			}
		}
		return variableList;

	}

	private static synchronized Document getProjectTemplateDocument()
		throws InvocationTargetException {
		if (templateDocument == null) {
			IPath templatePath;
			File templateFile;

			try {
				InputStream input =
					(new URL(WOLipsPlugin.baseURL(),
						WOVariables.woTemplateDirectory()
						+ WOVariables.woTemplateProject()))
						.openStream();

				templateDocument = WOLipsPlugin.documentBuilder().parse(input);

			} catch (java.util.MissingResourceException e) {
				throw new InvocationTargetException(e);
			} catch (MalformedURLException e) {
				throw new InvocationTargetException(e);
			} catch (SAXException e) {
				throw new InvocationTargetException(e);
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			} catch (NullPointerException e) {
				throw new InvocationTargetException(e);
			}
		}
		return templateDocument;
	}


	/*
		public static class ProjectCreationException extends Exception {
	
			private Exception wrappedException;
	
			
			public ProjectCreationException(Exception wrappedException) {
				super(
					"ProjectCreationException ("
						+ wrappedException.getMessage()
						+ ") while building project template");
			}
		}
		*/

}
