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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.wo.WOVariables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author mnolte
 * @author uli
 */
public class FileFromTemplateCreator {

	private static final int PLUGIN_NAME = 0;
	private static final int CLASS = 1;
	private static final int DATE = 2;
	private static final int PROJECT_NAME = 3;
	private static final String[] ALL_KEYS =
		{ "PLUGIN_NAME", "CLASS", "DATE", "PROJECT_NAME" };
	private static final String[] SUPPORTED_TEMPLATE_IDS =
		{
			"html",
			"wod",
			"api",
			"wocomponent",
			"application",
			"session",
			"directAction",
			"applicationproject",
			"frameworkproject",
			"makefile",
			"makefile.preamble",
			"makefile.postamble",
			"antwoapplication",
			"antwoframework" };
	private static Hashtable keyToIntegerDict;
	private static ArrayList supportedExtensions;
	private IFile fileToCreate;
	private String fileNameWithoutExtension;
	private String templateID;
	private static Document templateDocument;
	//private static Properties templateProperties;

	static {
		// build keyToIntegerDict
		keyToIntegerDict = new Hashtable(ALL_KEYS.length);
		for (int i = 0; i < ALL_KEYS.length; i++) {
			keyToIntegerDict.put(ALL_KEYS[i], new Integer(i));
		}
		// build supported extensions list
		supportedExtensions = new ArrayList(SUPPORTED_TEMPLATE_IDS.length);
		for (int i = 0; i < SUPPORTED_TEMPLATE_IDS.length; i++) {
			supportedExtensions.add(SUPPORTED_TEMPLATE_IDS[i]);
		}
	}

	public FileFromTemplateCreator() {
		super();
	}

	/**
	 * Method create. Creates new file resource
	 * @param fileHandle to create
	 * @param template id used to create initial content
	 * @param progress monitor or null
	 * @throws FileCreationException
	 */
	public synchronized void create(
		IFile fileToCreate,
		String templateId,
		IProgressMonitor monitor)
		throws FileCreationException {
			
		this.fileToCreate = fileToCreate;
		String fileName = fileToCreate.getName();
		int extIndex = fileName.indexOf(".");
		if (extIndex != -1 && extIndex < fileName.length() - 1) {
			fileNameWithoutExtension = fileName.substring(0, extIndex);
		}
		try {
			if (supportedExtensions.contains(templateId)) {
				SubProgressMonitor subMonitor = null;
				if (monitor != null) {
					subMonitor = new SubProgressMonitor(monitor, 1);
				}
				fileToCreate.create(
					createInputStream(templateId),
					false,
					subMonitor);
			}
		} catch (org.eclipse.core.runtime.CoreException e) {
			throw new FileCreationException(fileName, e);
		}

	}

	/**
	 * Method create. Creates new file resource, the initial contents are
	 * based on the file names' extension which is also the template id.
	 * @param fileToCreate 
	 * @param progress monitor or null
	 * @throws FileCreationException
	 */
	public synchronized void create(
		IFile fileToCreate,
		IProgressMonitor monitor)
		throws FileCreationException {
		String fileName = fileToCreate.getName();
		String fileExtension = null;
		int extIndex = fileName.indexOf(".");
		if (extIndex != -1 && extIndex < fileName.length() - 1) {
			fileExtension = fileName.substring(extIndex + 1);
		}
		create(fileToCreate, fileExtension, monitor);
	}
	
		

	private InputStream createInputStream(String templateID)
		throws FileCreationException {

		Element elementForTemplate =
			getFileTemplateDocument().getElementById(templateID);
		StringBuffer content = new StringBuffer("");
		if (elementForTemplate != null
			&& elementForTemplate.getFirstChild() != null) {

			String templateContent =
				elementForTemplate.getFirstChild().getNodeValue();
			if (templateContent != null) {
				// assign initial content
				content.append(templateContent);
				// build list of variables to expand
				String variablesToExpand =
					elementForTemplate.getAttribute("variables");
				StringTokenizer variableTokenizer =
					new StringTokenizer(variablesToExpand, ",");
				ArrayList variableList =
					new ArrayList(variableTokenizer.countTokens());
				while (variableTokenizer.hasMoreElements()) {
					variableList.add(variableTokenizer.nextElement());
				}
				if (variablesToExpand != null && !variableList.isEmpty()) {

					// expand variables
					String variableToExpand = null;
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
		return new ByteArrayInputStream(content.toString().getBytes());
	}
	/*
	private static Properties templateProperties(){
		if ( templateProperties==null){
			templateProperties = new Properties();
			try {
				InputStream input =
						(new URL(WOPluginUtils.BASE_URL,WebObjectsEclipsePlugin.getResourceString(
								"webobjects.template.directory") + 
							WebObjectsEclipsePlugin.getResourceString(
								"webobjects.template.properties")))
							.openStream();
				templateProperties.load(input);
			} catch (MalformedURLException e) {
				// do nothing expand no variables
			} catch (IOException e) {
				// do nothing expand no variables
			}
		}
		return templateProperties;
	}
	*/
	/**
	 * Method expandVariable, expands found variable (${[variable]}) to the
	 * appropiate value
	 * 
	 * @param variableToExpand
	 * @return found value or null
	 */
	private String expandVariable(String variableToExpand) {
		// TODO dict expand to Integer && switch case
		Integer valueFromDict =
			(Integer) keyToIntegerDict.get(variableToExpand);
		// default value
		String expandedValue = variableToExpand;
		if (valueFromDict != null) {
			switch (valueFromDict.intValue()) {
				case PLUGIN_NAME :
					expandedValue =
						WOLipsPlugin
							.getDefault()
							.getDescriptor()
							.getLabel();
					break;
				case CLASS :
					expandedValue = fileNameWithoutExtension;
					break;
				case DATE :
					expandedValue = (new java.util.Date()).toString();
					break;
				case PROJECT_NAME :
					expandedValue = fileToCreate.getProject().getName();
					break;
			}
		}

		return expandedValue;
	}

	/**
	 * Returns the templateDocument.
	 * @return Document
	 */
	private static synchronized Document getFileTemplateDocument()
		throws FileCreationException {
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
				throw new FileCreationException(e);
			} catch (MalformedURLException e) {
				throw new FileCreationException(e);
			} catch (SAXException e) {
				throw new FileCreationException(e);
			} catch (IOException e) {
				throw new FileCreationException(e);
			} catch (NullPointerException e) {
				throw new FileCreationException(e);
			}
		}
		return templateDocument;
	}

	/**
		 * @author mnolte
		 *
		 * To change this generated comment edit the template variable "typecomment":
		 * Window>Preferences>Java>Templates.
		 * To enable and disable the creation of type comments go to
		 * Window>Preferences>Java>Code Generation.
		 */
	public static class FileCreationException extends Exception {

		private Exception wrappedException;

		/**
		 * Constructor for FileCreationException on creating file.
		 */
		public FileCreationException(
			String fileName,
			Exception wrappedException) {
			super(
				"FileCreationException ("
					+ wrappedException.getMessage()
					+ ") while creating file named "
					+ fileName);
		}

		/**
		 * Constructor for FileCreationException on creating template document.
		 */
		public FileCreationException(Exception wrappedException) {
			super(
				"FileCreationException ("
					+ wrappedException.getMessage()
					+ ") while building document template");
		}

	}
}
