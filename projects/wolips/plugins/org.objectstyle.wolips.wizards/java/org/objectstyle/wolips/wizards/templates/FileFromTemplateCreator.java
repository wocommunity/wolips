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
package org.objectstyle.wolips.wizards.templates;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.util.StringUtilities;
import org.objectstyle.wolips.core.util.WorkbenchUtilities;
import org.objectstyle.wolips.wizards.Messages;
import org.w3c.dom.Element;
/**
 * FileFromTemplateCreator creates files from templates and given file handles.
 * The templates are identified by an unique id. Predefined variables mentioned 
 * in the template are expanded to the proper value.
 * <br>
 * The file templates are based on xml template in webobjects.template.directory
 * named webobjects.template.files (see plugin.properties).
 * <br>
 * Template must have following DTD:<br><br>
 *
 * &lt;!DOCTYPE templates [<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT
 *		templates (template)*&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ELEMENT
 *		template (#PCDATA)&gt;<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&lt;!ATTLIST
 *		template<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;name CDATA
 *		#REQUIRED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;id
 *		ID #REQUIRED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;description
 *		CDATA #IMPLIED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;variables
 *		CDATA #IMPLIED<BR>
 *		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;enabled
 *		(true | false) #REQUIRED<BR>
 *		&gt;<BR>
 *		]&gt;<BR>
 * @author mnolte
 *
 */
public class FileFromTemplateCreator extends _FileFromTemplateCreator {
	/////////////////////////////////////////////
	private IFile fileToCreate;
	private String fileNameWithoutExtension;
	/**
	 * Standard constructor
	 */
	public FileFromTemplateCreator() {
		super();
	}
	/**
	 * Constructor providing additional info.
	 * @param variableInfo dictionary containing about template variables which non-resolvable by keyToIntegerDict.
	 * @see #getKeyToIntegerDict()
	 */
	public FileFromTemplateCreator(Hashtable variableInfo) {
		this();
		_FileFromTemplateCreator.variableInfo = variableInfo;
	}
	/**
	 * Creates new file resource.
	 * <p>
	 * @param fileToCreate file handle to create
	 * @param templateId template id used to create initial content
	 * @param monitor progress monitor or null
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public synchronized void create(
		IFile fileToCreate,
		String templateId,
		IProgressMonitor monitor)
		throws InvocationTargetException {
		this.fileToCreate = fileToCreate;
		String fileName = fileToCreate.getName();
		int extIndex = fileName.indexOf(".");
		if (extIndex != -1 && extIndex < fileName.length() - 1) {
			// fileNameWithoutExtension is used to resolve CLASS template key
			fileNameWithoutExtension = fileName.substring(0, extIndex);
		}
		SubProgressMonitor subMonitor = null;
		if (monitor != null) {
			subMonitor = new SubProgressMonitor(monitor, 1);
		}
		try {
			fileToCreate.create(
				createInputStream(templateId),
				false,
				subMonitor);
		} catch (CoreException e) {
			if (fileToCreate.exists()) {
				// ask for overwriting existing file
				if (MessageDialog
					.openQuestion(
						WorkbenchUtilities.getActiveWorkbenchShell(),
						Messages.getString("QuestionDialog.title"),
						e.getMessage()
							+ "\n"
							+ Messages.getString(
								"QuestionDialog.overwrite.file"))) {
					try {
						fileToCreate.delete(true, monitor);
					} catch (CoreException e2) {
						throw new InvocationTargetException(e2);
					}
					create(fileToCreate, templateId, monitor);
				}
			} else {
				// ask for continuing execution of process
				if (!MessageDialog
					.openQuestion(
						WorkbenchUtilities.getActiveWorkbenchShell(),
						Messages.getString("QuestionDialog.title"),
						e.getMessage()
							+ "\n"
							+ Messages.getString("QuestionDialog.continue"))) {
					throw new InvocationTargetException(e);
				}
			}
		} finally {
			fileName = null;
		}
	}
	/**
	 * Creates new file resource, the initial contents are
	 * based on the file names' extension which is also the template id.
	 * <p>
	 * @param fileToCreate file to create 
	 * @param monitor progress monitor or null
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public synchronized void create(
		IFile fileToCreate,
		IProgressMonitor monitor)
		throws InvocationTargetException {
		String fileName = null;
		String fileExtension = null;
		try {
			fileName = fileToCreate.getName();
			fileExtension = null;
			int extIndex = fileName.indexOf(".");
			if (extIndex != -1 && extIndex < fileName.length() - 1) {
				fileExtension = fileName.substring(extIndex + 1);
			}
			create(fileToCreate, fileExtension, monitor);
		} finally {
			fileName = null;
			fileExtension = null;
		}
	}
	/**
	 * Helper method to create an input stream containing content definded by
	 * template xml file and expanding all mentioned expanded variables.<br>
	 * See class description for details about template file.
	 * <br>
	 * @see #getFileTemplateDocument()
	 * @see #expandVariable(String)
	 * @see IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 * @param templateID template ID to get file content template
	 * @return InputStream of new file
	 * @throws InvocationTargetException
	 */
	private InputStream createInputStream(String templateID)
		throws InvocationTargetException {
		Element elementForTemplate = null;
		StringBuffer content = null;
		String templateContent = null;
		String variablesToExpand = null;
		ArrayList variableList = null;
		String variableToExpand = null;
		try {
			elementForTemplate =
				getFileTemplateDocument().getElementById(templateID);
			content = new StringBuffer("");
			if (elementForTemplate != null
				&& (new Boolean(elementForTemplate.getAttribute("enabled"))
					.booleanValue())
				&& elementForTemplate.getFirstChild() != null) {
				templateContent =
					elementForTemplate.getFirstChild().getNodeValue();
				if (templateContent != null) {
					// assign initial content
					content.append(templateContent);
					// build list of variables to expand
					variablesToExpand =
						elementForTemplate.getAttribute("variables");
					variableList =
						StringUtilities.arrayListFromCSV(variablesToExpand);
					if (variablesToExpand != null && !variableList.isEmpty()) {
						// expand variables
						variableToExpand = null;
						int index = -1;
						for (int i = 0; i < variableList.size(); i++) {
							variableToExpand = (String) variableList.get(i);
							// replace all occurences of "${" + variableToExpand + "}"
							while ((index =
								templateContent.indexOf(
									"${" + variableToExpand + "}"))
								!= -1) {
								content.replace(
									index,
									index + variableToExpand.length() + 3,
									expandVariable(variableToExpand));
								templateContent = content.toString();
							}
						}
					}
				}
			}
		} finally {
			elementForTemplate = null;
			templateContent = null;
			variablesToExpand = null;
			variableList = null;
			variableToExpand = null;
		}
		return new ByteArrayInputStream(content.toString().getBytes());
	}
	/**
	 * Method expandVariable, expands found variable (${[variable]}) to the
	 * appropiate value.
	 * Expansion is either hard coded or given as variable info Hashtable 
	 * given in constructor @link FileFromTemplateCreator. Hashtable values are prefered 
	 * if a the key (variable to expand) exists.
	 * <p>
	 * @param variableToExpand
	 * @return found value or null
	 */
	private String expandVariable(String variableToExpand) {
		Integer valueFromDict =
			(Integer) getKeyToIntegerDict().get(variableToExpand);
		// default value
		if (valueFromDict == null)
			return variableToExpand;
		String expandedValue = variableToExpand;
		switch (valueFromDict.intValue()) {
			case PLUGIN_NAME :
				expandedValue =
					WOLipsPlugin.getDefault().getDescriptor().getLabel();
				break;
			case CLASS :
				expandedValue = fileNameWithoutExtension;
				break;
			case DATE :
				expandedValue = (new java.util.Date()).toString();
				break;
			case PROJECT_NAME :
				expandedValue =
					this.getExpandedValueForProjectName(expandedValue);
				break;
			case PACKAGE_NAME :
				expandedValue =
					this.getExpandedValueForPackageName(expandedValue);
				break;
			case ADAPTOR_NAME :
				if (variableInfo != null
					&& variableInfo.get(variableToExpand) != null) {
					expandedValue = (String) variableInfo.get(variableToExpand);
				}
				break;
			case BUILD_DIR :
				expandedValue = this.getExpandedValueForBuildDir(expandedValue);
				break;
			case NEXT_SYSTEM_ROOT :
				expandedValue =
					WOLipsPlugin
						.getDefault()
						.getWOEnvironment()
						.getWOVariables()
						.systemRoot();
				break;
		}
		return expandedValue;
	}
	/**
	 * Method getExpandedValueForProjectName.
	 * @param expandedValue
	 * @return String
	 */
	private String getExpandedValueForProjectName(String expandedValue) {
		IContainer parentResource = fileToCreate.getParent();
		if (parentResource instanceof IProject)
			return ((IProject) parentResource).getName();
		if (parentResource instanceof IFolder)
			return ((IFolder) parentResource).getName();
		return expandedValue;
	}
	/**
	 * Method getExpandedValueForPackageName.
	 * @param expandedValue
	 * @param parentResource
	 * @return String
	 */
	private String getExpandedValueForPackageName(String expandedValue) {
		if (fileToCreate.getProjectRelativePath().segmentCount() > 2) {
			// source folders are non hierachical -> segment[0]
			// if file is at least in segment[1] it must be a package
			IContainer parentResource = fileToCreate.getParent();
			StringBuffer packageNameBuffer = new StringBuffer("package ");
			IPath folderPath =
				((IFolder) parentResource).getProjectRelativePath();
			for (int i = 1; i < folderPath.segmentCount(); i++) {
				packageNameBuffer.append(folderPath.segment(i));
				packageNameBuffer.append(".");
			}
			packageNameBuffer.deleteCharAt(packageNameBuffer.length() - 1);
			packageNameBuffer.append(";");
			expandedValue = packageNameBuffer.toString();
		} else {
			// no package declaration needed
			expandedValue = "";
		}
		return expandedValue;
	}
	/**
	 * Method getExpandedValueForBuildDir.
	 * @param expandedValue
	 * @return String
	 */
	private String getExpandedValueForBuildDir(String expandedValue) {
		//		relativ path to output location
		IJavaProject actualJavaProject =
			JavaCore.create(fileToCreate.getProject());
		IPath outputLocation;
		try {
			outputLocation = actualJavaProject.getOutputLocation();
		} catch (JavaModelException e) {
			return expandedValue;
		} finally {
			actualJavaProject = null;
		}
		expandedValue =
			outputLocation.makeRelative().removeFirstSegments(1).toString();
		for (int i = 0;
			i < fileToCreate.getProjectRelativePath().segmentCount() - 1;
			i++) {
			expandedValue = "../" + expandedValue;
		}
		return expandedValue;
	}
}
