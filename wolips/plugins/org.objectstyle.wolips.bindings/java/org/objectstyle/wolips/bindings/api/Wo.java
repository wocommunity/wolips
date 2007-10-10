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
package org.objectstyle.wolips.bindings.api;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Wo extends AbstractApiModelElement {

  private final static String CLASS = "class";

  private final static String WOCOMPONENTCONTENT = "wocomponentcontent";

  protected Wo(Element element, ApiModel apiModel) {
    super(element, apiModel);
  }

  public ApiModel getModel() {
    return apiModel;
  }

  public String getClassName() {
    return element.getAttribute(CLASS);
  }

  public void setClassName(String className) {
    element.setAttribute(CLASS, className);
  }

  public boolean isComponentContent() {
    String value = element.getAttribute(WOCOMPONENTCONTENT);
    if (value == null) {
      return false;
    }
    return value.equals("true");
  }

  public void setComponentContent(boolean isComponentContent) {
    if (isComponentContent) {
      element.setAttribute(WOCOMPONENTCONTENT, "true");
    }
    else {
      element.setAttribute(WOCOMPONENTCONTENT, "false");
    }
  }

  public List<Binding> getRequiredBindings() {
    List<Binding> requiredBindings = new LinkedList<Binding>();
    for (Binding binding : getBindings()) {
      if (binding.isRequired()) {
        requiredBindings.add(binding);
      }
    }
    return requiredBindings;
  }

  public List<Binding> getBindings() {
    NodeList bindingElements = element.getElementsByTagName(Binding.BINDING);
    List<Binding> bindings = new LinkedList<Binding>();
    for (int i = 0; i < bindingElements.getLength(); i++) {
      Element bindingElement = (Element) bindingElements.item(i);
      Binding binding = new Binding(bindingElement, apiModel, this);
      bindings.add(binding);
    }
    return bindings;
  }

  public List<Validation> getValidations() {
    NodeList validationElements = element.getElementsByTagName(Validation.VALIDATION);
    List<Validation> validations = new LinkedList<Validation>();
    for (int i = 0; i < validationElements.getLength(); i++) {
      Element validationElement = (Element) validationElements.item(i);
      Validation validation = new Validation(validationElement, apiModel);
      validations.add(validation);
    }
    return validations;
  }

  public List<Validation> getAffectedValidations(String bindingName) {
    List<Validation> validations = getValidations();
    List<Validation> affectedValidations = new LinkedList<Validation>();
    for (Validation validation : validations) {
      if (validation.isAffectedByBindingNamed(bindingName)) {
        affectedValidations.add(validation);
      }
    }
    return affectedValidations;
  }

  public List<Validation> getFailedValidations(Map<String, String> bindings) {
    List<Validation> validations = getValidations();
    List<Validation> failedValidations = new LinkedList<Validation>();
    for (Validation validation : validations) {
      if (validation.evaluate(bindings)) {
        failedValidations.add(validation);
      }
    }
    return failedValidations;
  }

  public Binding getBinding(String name) {
    Binding matchingBinding = null;
    List<Binding> bindings = getBindings();
    for (int bindingNum = 0; matchingBinding == null && bindingNum < bindings.size(); bindingNum++) {
      Binding binding = bindings.get(bindingNum);
      if (name != null && name.equals(binding.getName())) {
        matchingBinding = binding;
      }
    }
    return matchingBinding;
  }

  public boolean containsBinding(String name) {
    return getBinding(name) == null;
  }

  public Binding createBinding(String name) {
    Binding binding = getBinding(name);
    if (binding == null) {
      Element newBindingElement = this.element.getOwnerDocument().createElement(Binding.BINDING);
      newBindingElement.setAttribute(Binding.NAME, name);
      this.element.appendChild(newBindingElement);
      this.apiModel.markAsDirty();
      binding = getBinding(name);
    }
    return binding;
  }

  public void removeBinding(String name) {
    Binding binding = getBinding(name);
    if (binding != null) {
      removeBinding(binding);
    }
  }

  public void removeBinding(Binding binding) {
    List<Validation> affectedValidations = getAffectedValidations(binding.getName());
    for (Validation affectedValidation : affectedValidations) {
      this.element.removeChild(affectedValidation.element);
    }
    this.element.removeChild(binding.element);
    this.apiModel.markAsDirty();
  }

  public String getPreview() {
    String preview = null;
    NodeList previewNodes = this.element.getElementsByTagName("preview");
    if (previewNodes.getLength() == 1) {
      try {
        StringWriter sw = new StringWriter();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", new Integer(4));
        StreamResult output = new StreamResult(sw);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

//      TransformerFactory xformerFactory = TransformerFactory.newInstance();
//      xformerFactory.setAttribute("indent-number", new Integer(4));
//      OutputFormat outputFormat = new OutputFormat("XML", "UTF-8", true);
//      outputFormat.setIndent(1);
//      outputFormat.setIndenting(true);
//      outputFormat.setOmitXMLDeclaration(true);
//      XMLSerializer serializer = new XMLSerializer(sw, outputFormat);
//        serializer.asDOMSerializer();
        for (int nodeNum = 0; nodeNum < previewNodes.getLength(); nodeNum++) {
          Element previewElement = (Element) previewNodes.item(nodeNum);
          NodeList previewChildren = previewElement.getChildNodes();
          for (int childNum = 0; childNum < previewChildren.getLength(); childNum++) {
            Node childElement = previewChildren.item(childNum);
            if (childElement instanceof Element) {
              transformer.transform(new DOMSource(childElement), output);
//              serializer.serialize((Element) childElement);
            }
            else if (childElement instanceof Text) {
              sw.append(((Text) childElement).getTextContent());
            }
          }
        }
        
        preview = sw.toString();
      }
      catch (Exception e) {
        e.printStackTrace();
        preview = null;
      }
    }
    return preview;
  }

}
