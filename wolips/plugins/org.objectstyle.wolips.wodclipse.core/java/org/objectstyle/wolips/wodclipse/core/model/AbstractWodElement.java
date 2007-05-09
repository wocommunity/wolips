/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.wodclipse.core.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.core.resources.types.api.Binding;
import org.objectstyle.wolips.core.resources.types.api.Validation;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodApiUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;

/**
 * @author mschrag
 */
public abstract class AbstractWodElement implements IWodElement, Comparable {
  private List<IWodBinding> _bindings;

  private boolean _isTemporary;

  private String _tagName;
  
  public AbstractWodElement() {
    _bindings = new LinkedList<IWodBinding>();
  }
  
  public void setTemporary(boolean isTemporary) {
    _isTemporary = isTemporary;
  }

  public void addBinding(IWodBinding _binding) {
    _bindings.add(_binding);
  }

  public List<IWodBinding> getBindings() {
    return _bindings;
  }

  public Map<String, Object> getBindingsMap() {
    Map<String, Object> bindingsMap = new HashMap<String, Object>();
    Iterator bindingsIter = _bindings.iterator();
    while (bindingsIter.hasNext()) {
      IWodBinding binding = (IWodBinding) bindingsIter.next();
      bindingsMap.put(binding.getName(), binding.getValue());
    }
    return bindingsMap;
  }

  public int compareTo(Object _otherObj) {
    int comparison;
    if (_otherObj instanceof IWodElement) {
      String otherName = ((IWodElement) _otherObj).getElementName();
      comparison = getElementName().compareTo(otherName);
    }
    else {
      comparison = -1;
    }
    return comparison;
  }
  
  public void writeWodFormat(Writer writer, boolean alphabetize) throws IOException {
    List<IWodBinding> bindings = getBindings();
    if (alphabetize) {
      bindings = new LinkedList<IWodBinding>(bindings);
      Collections.sort(bindings, new WodBindingComparator());
    }
    writer.write(getElementName());
    writer.write(" : ");
    writer.write(getElementType());
    writer.write(" {");
    writer.write("\n");
    for (IWodBinding binding : bindings) {
      binding.writeWodFormat(writer);
      writer.write("\n");
    }
    writer.write("}\n");
  }
  
  public void writeInlineFormat(Writer writer, String content, boolean alphabetize) throws IOException {
    writeInlineFormat(writer, content, alphabetize, true, true, true);
  }
  
  public void writeInlineFormat(Writer writer, String content, boolean alphabetize, boolean showOpenTag, boolean showContent, boolean showCloseTag) throws IOException {
    List<IWodBinding> bindings = getBindings();
    if (alphabetize) {
      bindings = new LinkedList<IWodBinding>(bindings);
      Collections.sort(bindings, new WodBindingComparator());
    }
    if (showOpenTag) {
      writer.write("<");
      writer.write(getTagName());
      for (IWodBinding binding : bindings) {
        binding.writeInlineFormat(writer);
      }
      if (content == null) {
        writer.write("/>");
      }
      else {
        writer.write(">");
      }
    }
    if (content != null) {
      if (showContent) {
        writer.write(content);
      }
      if (showCloseTag) {
        writer.write("</");
        writer.write(getTagName());
        writer.write(">");
      }
    }
  }

  public void setTagName(String tagName) {
    _tagName = tagName;
  }
  
  public String getTagName() {
    String tagName;
    if (_tagName == null) {
      tagName = "wo: " + getElementType();
    }
    else {
      tagName = _tagName;
    }
    return tagName;
  }
  
  public abstract int getLineNumber();

  public void fillInProblems(IJavaProject javaProject, IType javaFileType, boolean checkBindingValues, List<WodProblem> problems, WodParserCache cache) throws CoreException, IOException {
    String elementTypeName = getElementType();

    String elementName = getElementName();
    int lineNumber = getLineNumber();
    if (!_isTemporary && !cache.getHtmlElementCache().containsKey(elementName)) {
      problems.add(new WodElementProblem("There is no element named '" + elementName + "' in your component HTML file", getElementNamePosition(), lineNumber, true, elementTypeName + ".html"));
    }

    IType elementType = WodReflectionUtils.findElementType(javaProject, elementTypeName, false, cache);
    if (elementType == null) {
      problems.add(new WodElementProblem("The class for '" + elementTypeName + "' is either missing or does not extend WOElement.", getElementTypePosition(), lineNumber, false, elementTypeName + ".java"));
    }
    else {
      Wo wo;
      try {
        wo = WodApiUtils.findApiModelWo(elementType, cache);
        if (wo != null) {
          Map bindingsMap = getBindingsMap();
          Binding[] bindings = wo.getBindings();
          for (int i = 0; i < bindings.length; i++) {
            String bindingName = bindings[i].getName();
            if (bindings[i].isExplicitlyRequired() && !bindingsMap.containsKey(bindingName)) {
              problems.add(new WodElementProblem("Binding '" + bindingName + "' is required for " + wo.getClassName(), getElementNamePosition(), lineNumber, false, elementTypeName + ".api"));
            }
          }
          Validation[] failedValidations = wo.getFailedValidations(bindingsMap);
          for (int i = 0; i < failedValidations.length; i++) {
            String failedValidationMessage = failedValidations[i].getMessage();
            problems.add(new WodElementProblem(failedValidationMessage, getElementNamePosition(), lineNumber, false, elementTypeName + ".api"));
          }
        }
      }
      catch (Throwable e) {
        Activator.getDefault().log(e);
      }
    }

    Set<String> bindingNames = new HashSet<String>();
    Iterator checkForDuplicateBindingsIter = getBindings().iterator();
    while (checkForDuplicateBindingsIter.hasNext()) {
      IWodBinding binding = (IWodBinding) checkForDuplicateBindingsIter.next();
      String bindingName = binding.getName();
      if (bindingNames.contains(bindingName)) {
        problems.add(new WodBindingNameProblem(bindingName, "Duplicate binding named '" + bindingName + "'", binding.getNamePosition(), binding.getLineNumber(), false, (String) null));
      }
      else {
        bindingNames.add(bindingName);
      }
    }

    if (checkBindingValues && javaFileType != null) {
      Iterator bindingsIter = getBindings().iterator();
      while (bindingsIter.hasNext()) {
        IWodBinding binding = (IWodBinding) bindingsIter.next();
        try {
          binding.fillInBindingProblems(javaProject, javaFileType, problems, cache);
        }
        catch (Throwable t) {
          Activator.getDefault().log("Failed to check wod binding values.", t);
        }
      }
    }
  }

  @Override
  public String toString() {
    return "[" + getClass().getName() + ": elementName = " + getElementName() + ";  elementType = " + getElementType() + "; bindings = " + _bindings + "]";
  }
}
