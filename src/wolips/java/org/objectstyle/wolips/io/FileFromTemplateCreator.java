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

package org.objectstyle.wolips.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.wo.WOVariables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
public class FileFromTemplateCreator {

	// translate expected variable strings to int 
	// for switch case in expandVariable
	private static final int PLUGIN_NAME = 0;
	private static final int CLASS = 1;
	private static final int DATE = 2;
	private static final int PROJECT_NAME = 3;
	private static final int PACKAGE_NAME = 4;
	private static final int ADAPTOR_NAME = 5;
	private static final String[] ALL_KEYS =
		{ "PLUGIN_NAME", "CLASS", "DATE", "PROJECT_NAME", "PACKAGE_NAME", "ADAPTOR_NAME" };
	private static Hashtable keyToIntegerDict;
	/////////////////////////////////////////////

	private IFile fileToCreate;
	private String fileNameWithoutExtension;
	private String templateID;
	private static Document templateDocument;
	private static Hashtable variableInfo;
	//private static Properties templateProperties;

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
		this.variableInfo = variableInfo;
	}

	/**
	 * Creates new file resource.
	 * <p>
	 * @param fileToCreate file handle to create
	 * @param templateId template id used to create initial content
	 * @param monitor progress monitor or null
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public synchronized void create(IFile fileToCreate, String templateId, IProgressMonitor monitor)
		throws InvocationTargetException {

		this.fileToCreate = fileToCreate;
		String fileName = fileToCreate.getName();
		String fileExtension = null;
		int extIndex = fileName.indexOf(".");
		if (extIndex != -1 && extIndex < fileName.length() - 1) {
			fileNameWithoutExtension = fileName.substring(0, extIndex);
			fileExtension = fileName.substring(extIndex + 1);
		}

		try {
			SubProgressMonitor subMonitor = null;
			if (monitor != null) {
				subMonitor = new SubProgressMonitor(monitor, 1);
			}
			fileToCreate.create(createInputStream(templateId), false, subMonitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
/*
		if (fileExtension != null) {
			QualifiedName resourceQualifier = WOPluginUtils.qualifierFromResourceIdentifier(fileExtension);
			String listId = (String) WOPluginUtils.getResourceQualifierToListIdDict().get(resourceQualifier);
			try {
				// mark resource as project depending
				fileToCreate.setPersistentProperty(resourceQualifier, listId);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}
		*/

	}

	/**
	 * Creates new file resource, the initial contents are
	 * based on the file names' extension which is also the template id.
	 * <p>
	 * @param fileToCreate file to create 
	 * @param monitor progress monitor or null
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public synchronized void create(IFile fileToCreate, IProgressMonitor monitor) throws InvocationTargetException {
		String fileName = fileToCreate.getName();
		String fileExtension = null;
		int extIndex = fileName.indexOf(".");
		if (extIndex != -1 && extIndex < fileName.length() - 1) {
			fileExtension = fileName.substring(extIndex + 1);
		}
		create(fileToCreate, fileExtension, monitor);

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
	private InputStream createInputStream(String templateID) throws InvocationTargetException {

		Element elementForTemplate = getFileTemplateDocument().getElementById(templateID);
		StringBuffer content = new StringBuffer("");
		
		if (elementForTemplate != null && (new Boolean(elementForTemplate.getAttribute("enabled")).booleanValue())  && elementForTemplate.getFirstChild() != null) {

			String templateContent = elementForTemplate.getFirstChild().getNodeValue();
			if (templateContent != null) {
				// assign initial content
				content.append(templateContent);
				// build list of variables to expand
				String variablesToExpand = elementForTemplate.getAttribute("variables");

				ArrayList variableList = WOLipsPlugin.arrayListFromCSV(variablesToExpand);

				if (variablesToExpand != null && !variableList.isEmpty()) {

					// expand variables
					String variableToExpand = null;
					int index = -1;
					for (int i = 0; i < variableList.size(); i++) {
						variableToExpand = (String) variableList.get(i);

						// replace all occurences of "${" + variableToExpand + "}"
						while ((index = templateContent.indexOf("${" + variableToExpand + "}")) != -1) {
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

		Integer valueFromDict = (Integer) getKeyToIntegerDict().get(variableToExpand);
		// default value
		String expandedValue = variableToExpand;
		IContainer parentResource;

		if (valueFromDict != null) {
			switch (valueFromDict.intValue()) {
				case PLUGIN_NAME :
					expandedValue = WOLipsPlugin.getDefault().getDescriptor().getLabel();
					break;

				case CLASS :
					expandedValue = fileNameWithoutExtension;
					break;

				case DATE :
					expandedValue = (new java.util.Date()).toString();
					break;

				case PROJECT_NAME :
					parentResource = fileToCreate.getParent();

					if (parentResource instanceof IProject) {
						expandedValue = ((IProject) parentResource).getName();

					} else if (parentResource instanceof IFolder) {
						expandedValue = ((IFolder) parentResource).getName();
					}
					break;

				case PACKAGE_NAME :
					parentResource = fileToCreate.getParent();
					if (parentResource instanceof IFolder) {
						StringBuffer packageNameBuffer = new StringBuffer("package ");

						IPath folderPath = ((IFolder) parentResource).getProjectRelativePath();
						for (int i = 0; i < folderPath.segmentCount(); i++) {
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
					break;

				case ADAPTOR_NAME :
					if (variableInfo != null && variableInfo.get(variableToExpand) != null) {
						expandedValue = (String) variableInfo.get(variableToExpand);
					}
					break;

			}
		}

		return expandedValue;
	}

	/**
	 * The file template document is a parsed xml file resource containing
	 * all file templates.
	 * <p> 
	 * @return the templateDocument
	 */
	private static synchronized Document getFileTemplateDocument() throws InvocationTargetException {
		if (templateDocument == null) {
			IPath templatePath;
			File templateFile;

			try {
				InputStream input =
					(new URL(WOLipsPlugin.baseURL(), WOVariables.woTemplateDirectory()
						+ WOVariables.woTemplateFiles()))
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

	private static Hashtable getKeyToIntegerDict() {
		if (keyToIntegerDict == null) {
			// build keyToIntegerDict
			keyToIntegerDict = new Hashtable(ALL_KEYS.length);
			for (int i = 0; i < ALL_KEYS.length; i++) {
				keyToIntegerDict.put(ALL_KEYS[i], new Integer(i));
			}
		}
		return keyToIntegerDict;
	}

}
