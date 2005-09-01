/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.internal.types.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.internal.pattern.PatternList;
import org.objectstyle.wolips.core.resources.pattern.IPattern;
import org.objectstyle.wolips.core.resources.pattern.IPatternList;
import org.objectstyle.wolips.core.resources.types.file.IDotWOLipsAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DotWOLipsAdapter extends AbstractFileAdapter implements
		IDotWOLipsAdapter {

	private Document document = null;

	private static final String DEFAULT_DOT_WOLIPS_XML_FILENAME = "DefaultDotWOLips.xml";

	private static final String DOT_WOLIPS_ELEMENT = "dot-wolips";

	private static final String PATTERNSET_ELEMENT = "patternset";

	private static final String PATTERNSET_NAME_ATTRIBUTE = "name";

	private static final String PATTERN_ELEMENT = "pattern";

	private static final String PATTERN_NAME_ATTRIBUTE = "name";

	private static final String CLASSES_EXCLUDE = "classes exclude";

	private static final String CLASSES_INCLUDE = "classes include";

	private static final String RESOURCES_EXCLUDE = "resources exclude";

	private static final String RESOURCES_INCLUDE = "resources include";

	private static final String WOAPP_RESOURCES_EXCLUDE = "woapp resources exclude";

	private static final String WOAPP_RESOURCES_INCLUDE = "woapp resources include";

	private PatternList classesExcludePatternList;

	private PatternList classesIncludePatternList;

	private PatternList resourcesExcludePatternList;

	private PatternList resourcesIncludePatternList;

	private PatternList woappResourcesExcludePatternList;

	private PatternList woappResourcesIncludePatternList;

	public DotWOLipsAdapter(IFile underlyingFile) {
		super(underlyingFile);
		this.loadAndParseDocument();
	}

	private void loadAndParseDocument() {
		if (this.document != null) {
			return;
		}
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while parsing .wolips",
					e);
		}
		if (!this.getUnderlyingResource().exists()) {
			InputStream inputStream = null;
			try {
				inputStream = this.getClass().getResourceAsStream(
						DEFAULT_DOT_WOLIPS_XML_FILENAME);
				this.document = documentBuilder.parse(inputStream);
			} catch (SAXException e) {
				CorePlugin.getDefault().debug(
						this.getClass().getName()
								+ "Error while parsing .wolips", e);
			} catch (IOException e) {
				CorePlugin.getDefault().debug(
						this.getClass().getName()
								+ "Error while parsing .wolips", e);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						CorePlugin.getDefault().debug(
								this.getClass().getName()
										+ "Error while parsing .wolips", e);
					}
				}
			}
		} else {
			try {
				this.document = documentBuilder.parse(this
						.getUnderlyingResource().getLocation().toOSString());
			} catch (SAXException e) {
				CorePlugin.getDefault().debug(
						this.getClass().getName()
								+ "Error while parsing .wolips", e);
			} catch (IOException e) {
				CorePlugin.getDefault().debug(
						this.getClass().getName()
								+ "Error while parsing .wolips", e);
			}
		}
		Element dotWOLipsElement = (Element) this.document
				.getElementsByTagName(DOT_WOLIPS_ELEMENT).item(0);
		NodeList nodeList = dotWOLipsElement
				.getElementsByTagName(PATTERNSET_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			this.parsePatternsetElement(element);
		}
		if (!this.getUnderlyingFile().isAccessible()
				|| !this.getUnderlyingFile().exists()) {
			this.saveDocument();
			try {
				this.getUnderlyingFile()
						.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e) {
				CorePlugin.getDefault().debug(
						this.getClass().getName()
								+ "Error while refreshing .wolips: "
								+ this.getUnderlyingFile(), e);
			}
		}
	}

	private void parsePatternsetElement(Element pattersetElement) {
		String name = pattersetElement.getAttribute(PATTERNSET_NAME_ATTRIBUTE);
		PatternList patternList = new PatternList();
		NodeList nodeList = pattersetElement
				.getElementsByTagName(PATTERN_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			String string = element.getAttribute(PATTERN_NAME_ATTRIBUTE);
			patternList.add(string);
		}
		if (CLASSES_EXCLUDE.equalsIgnoreCase(name)) {
			this.classesExcludePatternList = patternList;
		} else if (CLASSES_INCLUDE.equalsIgnoreCase(name)) {
			this.classesIncludePatternList = patternList;
		} else if (RESOURCES_EXCLUDE.equalsIgnoreCase(name)) {
			this.resourcesExcludePatternList = patternList;
		} else if (RESOURCES_INCLUDE.equalsIgnoreCase(name)) {
			this.resourcesIncludePatternList = patternList;
		} else if (WOAPP_RESOURCES_EXCLUDE.equalsIgnoreCase(name)) {
			this.woappResourcesExcludePatternList = patternList;
		} else if (WOAPP_RESOURCES_INCLUDE.equalsIgnoreCase(name)) {
			this.woappResourcesIncludePatternList = patternList;
		} else {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Unknown patternset name: "
							+ name);
		}
	}

	public void saveDocument() {
		NodeList nodeList = this.document
				.getElementsByTagName(PATTERNSET_ELEMENT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			this.savePatternsetElement(element);
		}

		// Prepare the DOM document for writing
		Source source = new DOMSource(this.document);

		// Prepare the output file
		File file = new File(this.getUnderlyingResource().getLocation()
				.toOSString());
		Result result = new StreamResult(file);

		// Write the DOM document to the file
		Transformer xformer = null;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while writing .wolips",
					e);
		} catch (TransformerFactoryConfigurationError e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while writing .wolips",
					e);
		}
		try {
			xformer.transform(source, result);
		} catch (TransformerException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while writing .wolips",
					e);
		}
		try {
			this.getUnderlyingResource().refreshLocal(IResource.DEPTH_ONE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while writing .wolips",
					e);
		}
	}

	private void savePatternsetElement(Element pattersetElement) {
		String name = pattersetElement.getAttribute(PATTERNSET_NAME_ATTRIBUTE);
		PatternList patternList = null;
		NodeList nodeList = pattersetElement
				.getElementsByTagName(PATTERN_ELEMENT);
		for (int i = nodeList.getLength() - 1; i != 0; i--) {
			pattersetElement.removeChild(nodeList.item(i));
		}
		if (CLASSES_EXCLUDE.equalsIgnoreCase(name)) {
			patternList = this.classesExcludePatternList;
		} else if (CLASSES_INCLUDE.equalsIgnoreCase(name)) {
			patternList = this.classesIncludePatternList;
		} else if (RESOURCES_INCLUDE.equalsIgnoreCase(name)) {
			patternList = this.resourcesIncludePatternList;
		} else if (RESOURCES_EXCLUDE.equalsIgnoreCase(name)) {
			patternList = this.resourcesExcludePatternList;
		} else if (WOAPP_RESOURCES_EXCLUDE.equalsIgnoreCase(name)) {
			patternList = this.woappResourcesExcludePatternList;
		} else if (WOAPP_RESOURCES_INCLUDE.equalsIgnoreCase(name)) {
			patternList = this.woappResourcesIncludePatternList;
		} else {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Unknown patternset name: "
							+ name);

		}
		IPattern[] pattern = patternList.getPattern();
		for (int i = 0; i < pattern.length; i++) {
			Element element = this.document.createElement(PATTERN_ELEMENT);
			element.setAttribute(PATTERN_NAME_ATTRIBUTE, pattern[i]
					.getPattern());
			pattersetElement.appendChild(element);
		}
	}

	public IPatternList getClassesExcludePatternList() {
		this.loadAndParseDocument();
		return this.classesExcludePatternList;
	}

	public IPatternList getClassesIncludePatternList() {
		this.loadAndParseDocument();
		return this.classesIncludePatternList;
	}

	public IPatternList getResourcesExcludePatternList() {
		this.loadAndParseDocument();
		return this.resourcesExcludePatternList;
	}

	public IPatternList getResourcesIncludePatternList() {
		this.loadAndParseDocument();
		return this.resourcesIncludePatternList;
	}

	public IPatternList getWoappResourcesExcludePatternList() {
		this.loadAndParseDocument();
		return this.woappResourcesExcludePatternList;
	}

	public IPatternList getWoappResourcesIncludePatternList() {
		this.loadAndParseDocument();
		return this.woappResourcesIncludePatternList;
	}
}
