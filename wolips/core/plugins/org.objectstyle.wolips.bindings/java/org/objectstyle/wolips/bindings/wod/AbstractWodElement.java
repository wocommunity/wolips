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
package org.objectstyle.wolips.bindings.wod;

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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.api.ApiUtils;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Validation;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;

/**
 * @author mschrag
 */
public abstract class AbstractWodElement implements IWodElement, Comparable<IWodElement> {
  private List<IWodBinding> _bindings;

  private boolean _inline;

  private String _tagName;

  public AbstractWodElement() {
    _bindings = new LinkedList<IWodBinding>();
  }

  public boolean isInline() {
    return _inline;
  }

  public void setInline(boolean inline) {
    _inline = inline;
  }

  public void addBinding(IWodBinding binding) {
    _bindings.add(binding);
  }
  
  public void removeBinding(IWodBinding binding) {
    _bindings.remove(binding);
  }

  public List<IWodBinding> getBindings() {
    return _bindings;
  }

  public IWodBinding getBindingNamed(String name) {
    IWodBinding matchingBinding = null;
    Iterator<IWodBinding> wodBindingsIter = _bindings.iterator();
    while (matchingBinding == null && wodBindingsIter.hasNext()) {
      IWodBinding wodBinding = wodBindingsIter.next();
      if (name.equals(wodBinding.getName())) {
        matchingBinding = wodBinding;
      }
    }
    return matchingBinding;
  }

  public String getBindingValue(String name) {
    String value = null;
    IWodBinding binding = getBindingNamed(name);
    if (binding != null) {
      value = binding.getValue();
    }
    return value;
  }

  public Map<String, String> getBindingsMap() {
    Map<String, String> bindingsMap = new HashMap<String, String>();
    Iterator<IWodBinding> bindingsIter = _bindings.iterator();
    while (bindingsIter.hasNext()) {
      IWodBinding binding = bindingsIter.next();
      bindingsMap.put(binding.getName(), binding.getValue());
    }
    return bindingsMap;
  }

  public int compareTo(IWodElement otherElement) {
    String otherName = otherElement.getElementName();
    int comparison = getElementName().compareTo(otherName);
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

  public void writeInlineFormat(Writer writer, String content, boolean alphabetize, String bindingPrefix, String bindingSuffix) throws IOException {
    writeInlineFormat(writer, content, alphabetize, true, true, true, bindingPrefix, bindingSuffix);
  }

  public void writeInlineFormat(Writer writer, String content, boolean alphabetize, boolean showOpenTag, boolean showContent, boolean showCloseTag, String bindingPrefix, String bindingSuffix) throws IOException {
    List<IWodBinding> bindings = getBindings();
    if (alphabetize) {
      bindings = new LinkedList<IWodBinding>(bindings);
      Collections.sort(bindings, new WodBindingComparator());
    }
    if (showOpenTag) {
      writer.write("<");
      writer.write(getTagName());
      for (IWodBinding binding : bindings) {
        binding.writeInlineFormat(writer, bindingPrefix, bindingSuffix);
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
      tagName = "wo:" + getElementType();
    }
    else {
      tagName = _tagName;
    }
    return tagName;
  }

  public Wo getApi(IJavaProject javaProject, TypeCache cache) throws JavaModelException, ApiModelException {
    String elementTypeName = getElementType();
    IType elementType = BindingReflectionUtils.findElementType(javaProject, elementTypeName, false, cache);
    Wo wo = ApiUtils.findApiModelWo(elementType, cache.getApiCache(javaProject));
    return wo;
  }

  public IApiBinding[] getApiBindings(Wo api) {
    IApiBinding[] wodBindings = null;
    boolean apiFound = false;
    try {
      if (api != null) {
        apiFound = true;
        List<IApiBinding> visibleBindings = new LinkedList<IApiBinding>();
        List<Binding> apiBindings = api.getBindings();
        visibleBindings.addAll(apiBindings);
        for (IWodBinding wodBinding : getBindings()) {
          String bindingName = wodBinding.getName();
          boolean wodBindingDefinedInApi = false;
          for (IApiBinding apiBinding : apiBindings) {
            if (apiBinding.getName().equals(bindingName)) {
              wodBindingDefinedInApi = true;
              break;
            }
          }
          if (!wodBindingDefinedInApi) {
            visibleBindings.add(wodBinding);
          }
        }
        wodBindings = visibleBindings.toArray(new IApiBinding[visibleBindings.size()]);
      }
    }
    catch (Throwable t) {
      Activator.getDefault().log("Failed to retrieve bindings for " + this + ".", t);
    }
    if (!apiFound) {
      List<IWodBinding> currentBindings = getBindings();
      wodBindings = currentBindings.toArray(new IApiBinding[currentBindings.size()]);
    }
    return wodBindings;
  }

  public abstract int getLineNumber();

  public void fillInProblems(IJavaProject javaProject, IType javaFileType, boolean checkBindingValues, List<WodProblem> problems, TypeCache typeCache, HtmlElementCache htmlCache) throws CoreException {
    String elementTypeName = getElementType();

    String elementName = getElementName();
    int lineNumber = getLineNumber();
    if (!_inline && !htmlCache.containsElementNamed(elementName)) {
      problems.add(new WodElementProblem(this, "There is no element named '" + elementName + "' in your component HTML file", getElementNamePosition(), lineNumber, true));
    }

    Wo wo = null;
    IType elementType = BindingReflectionUtils.findElementType(javaProject, elementTypeName, false, typeCache);
    if (elementType == null || (!elementType.getElementName().equals(elementTypeName) && !elementType.getFullyQualifiedName().equals(elementTypeName))) {
      problems.add(new WodElementProblem(this, "The class for '" + elementTypeName + "' is either missing or does not extend WOElement.", getElementTypePosition(), lineNumber, false));
    }
    else {
      try {
        wo = ApiUtils.findApiModelWo(elementType, typeCache.getApiCache(javaProject));
        if (wo != null) {
          Map<String, String> bindingsMap = getBindingsMap();
          List<Binding> bindings = wo.getBindings();
          for (Binding binding : bindings) {
            String bindingName = binding.getName();
            if (binding.isExplicitlyRequired() && !bindingsMap.containsKey(bindingName)) {
              problems.add(new ApiBindingValidationProblem(this, binding, getElementNamePosition(), lineNumber, false));
            }
          }
          List<Validation> failedValidations = wo.getFailedValidations(bindingsMap);
          for (Validation failedValidation : failedValidations) {
            problems.add(new ApiElementValidationProblem(this, failedValidation, getElementNamePosition(), lineNumber, false));
          }
        }
      }
      catch (Throwable e) {
        Activator.getDefault().log(e);
      }
    }

    Set<String> bindingNames = new HashSet<String>();
    Iterator<IWodBinding> checkForDuplicateBindingsIter = getBindings().iterator();
    while (checkForDuplicateBindingsIter.hasNext()) {
      IWodBinding binding = checkForDuplicateBindingsIter.next();
      String bindingName = binding.getName();
      if (bindingNames.contains(bindingName)) {
        problems.add(new WodBindingNameProblem(this, binding, bindingName, "Duplicate binding named '" + bindingName + "'", binding.getNamePosition(), binding.getLineNumber(), false));
      }
      else {
        bindingNames.add(bindingName);
      }
    }

    JavaModelException javaModelException = null;

    if (checkBindingValues && javaFileType != null) {
      Iterator<IWodBinding> bindingsIter = getBindings().iterator();
      while (bindingsIter.hasNext()) {
        IWodBinding binding = bindingsIter.next();
        try {
          IApiBinding apiBinding = null;
          if (wo != null) {
            apiBinding = wo.getBinding(binding.getName());
          }
          binding.fillInBindingProblems(this, apiBinding, javaProject, javaFileType, problems, typeCache);
        }
        catch (JavaModelException t) {
          javaModelException = t;
          Activator.getDefault().log("Failed to check wod binding values.", t);
        }
        catch (Throwable t) {
          Activator.getDefault().log("Failed to check wod binding values.", t);
        }
      }
    }

    if (javaModelException != null) {
      throw javaModelException;
    }
  }

  public boolean isWithin(IRegion region) {
    return getStartOffset() <= region.getOffset() && getEndOffset() > region.getOffset();
  }

  public boolean isTypeWithin(IRegion region) {
    Position typePosition = getElementTypePosition();
    return typePosition != null && typePosition.getOffset() <= region.getOffset() && typePosition.getOffset() + typePosition.getLength() > region.getOffset();
  }

  @Override
  public String toString() {
    return "[" + getClass().getName() + ": elementName = " + getElementName() + ";  elementType = " + getElementType() + "; bindings = " + _bindings + "]";
  }
}
