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
package org.objectstyle.wolips.core.resources.types.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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

import org.objectstyle.wolips.core.CorePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ApiModel {

	private Document document;

  private URL url;
	private File file;

	private boolean isDirty = false;

	public ApiModel(File file) {
		super();
		this.file = file;
		this.parse();
	}

  public ApiModel(URL url) {
    super();
    this.url = url;
    this.parse();
  }

	private void parse() {

		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while parsing .wolips",
					e);
		}
		try {
      if (this.url != null) {
        this.document = documentBuilder.parse(this.url.toExternalForm());
      }
      else {
        this.document = documentBuilder.parse(this.file);
      }
		} catch (SAXException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while parsing .wolips",
					e);
		} catch (IOException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while parsing .wolips",
					e);
		}
	}

	public Wodefinitions getWODefinitions() {
		Element element = document.getDocumentElement();
		return new Wodefinitions(element, this);
	}

	public Wo getWo() {
		Wodefinitions wodefinitions = this.getWODefinitions();
		if (wodefinitions == null) {
			return null;
		}
		return wodefinitions.getWo();
	}

	public void saveChanges() {
    if (file == null) {
      throw new IllegalStateException("You can not saveChanges to an ApiModel that is not backed by a file.");
    }
    
		// Prepare the DOM document for writing
		Source source = new DOMSource(this.document);

		// Prepare the output file
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
			isDirty = false;
		} catch (TransformerException e) {
			CorePlugin.getDefault().debug(
					this.getClass().getName() + "Error while writing .wolips",
					e);
		}
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void markAsDirty() {
		isDirty = true;
	}
}