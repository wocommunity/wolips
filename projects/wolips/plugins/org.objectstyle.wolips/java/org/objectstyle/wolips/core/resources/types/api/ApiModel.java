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
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ApiModel {

  private Document document;

  private URL url;
  private File file;

  private boolean isDirty = false;

  public ApiModel(File file) throws ApiModelException {
    super();
    this.file = file;
    this.parse();
  }

  public ApiModel(URL url) throws ApiModelException {
    super();
    this.url = url;
    this.parse();
  }

  public String getLocation() {
    String location;
    if (this.url != null) {
      location = this.url.toExternalForm();
    }
    else {
      location = this.file.getAbsolutePath();
    }
    return location;
  }

  private void parse() throws ApiModelException {
    try {
      DocumentBuilder documentBuilder = documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      if (this.url != null) {
        this.document = documentBuilder.parse(this.url.toExternalForm());
      }
      else {
        this.document = documentBuilder.parse(this.file);
      }
    }
    catch (Throwable e) {
      throw new ApiModelException("Failed to parse API file " + getLocation() + ".", e);
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

  public void saveChanges() throws ApiModelException {
    if (file == null) {
      throw new ApiModelException("You can not saveChanges to an ApiModel that is not backed by a file.");
    }

    // Prepare the DOM document for writing
    Source source = new DOMSource(this.document);

    // Prepare the output file
    Result result = new StreamResult(file);

    // Write the DOM document to the file
    try {
      Transformer xformer = TransformerFactory.newInstance().newTransformer();
      xformer.transform(source, result);
      isDirty = false;
    }
    catch (Throwable t) {
      throw new ApiModelException("Failed to save API file " + getLocation() + ".", t);
    }
  }

  public boolean isDirty() {
    return isDirty;
  }

  public void markAsDirty() {
    isDirty = true;
  }
}