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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;

/**
 * @author mschrag
 */
public abstract class AbstractWodModel implements IWodModel {
  private List<IWodElement> _elements;

  private List<WodProblem> _parseProblems;

  public AbstractWodModel() {
    _elements = new LinkedList<IWodElement>();
    _parseProblems = new LinkedList<WodProblem>();
  }

  protected void clear() {
    _elements.clear();
    _parseProblems.clear();
  }

  public IWodElement getElementNamed(String name) {
    IWodElement matchingElement = null;
    if (name != null) {
      Iterator<IWodElement> wodElementsIter = _elements.iterator();
      while (matchingElement == null && wodElementsIter.hasNext()) {
        IWodElement wodElement = wodElementsIter.next();
        if (name.equals(wodElement.getElementName())) {
          matchingElement = wodElement;
        }
      }
    }
    return matchingElement;
  }

  public synchronized void addElement(IWodElement _element) {
    _elements.add(_element);
  }

  public synchronized List<IWodElement> getElements() {
    return _elements;
  }

  public void writeWodFormat(Writer writer, boolean alphabetize) throws IOException {
    List<IWodElement> elementsList = getElements();
    if (alphabetize) {
      elementsList = new LinkedList<IWodElement>(elementsList);
      Collections.sort(elementsList, new WodElementComparator());
    }
    for (IWodElement element : elementsList) {
      element.writeWodFormat(writer, alphabetize);
      writer.write("\n");
    }
  }

  public List<WodProblem> getProblems(IJavaProject javaProject, IType javaFileType, TypeCache typeCache, HtmlElementCache htmlCache) throws CoreException, IOException, ApiModelException {
    List<WodProblem> problems = new LinkedList<WodProblem>();
    boolean checkBindingValues = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VALIDATE_BINDING_VALUES);
    fillInProblems(javaProject, javaFileType, checkBindingValues, problems, typeCache, htmlCache);
    return problems;
  }

  protected void addParseProblem(WodProblem problem) {
    _parseProblems.add(problem);
  }

  public void fillInProblems(IJavaProject javaProject, IType javaFileType, boolean checkBindingValues, List<WodProblem> problems, TypeCache typeCache, HtmlElementCache htmlCache) throws CoreException, IOException, ApiModelException {
    problems.addAll(_parseProblems);
    Set<String> wodElementNames = new HashSet<String>();
    Iterator<IWodElement> elementsIter = getElements().iterator();
    while (elementsIter.hasNext()) {
      IWodElement element = elementsIter.next();
      String elementName = element.getElementName();
      if (wodElementNames.contains(elementName)) {
        problems.add(new WodElementProblem(element, "Duplicate definition of '" + elementName + "'", element.getElementNamePosition(), element.getLineNumber(), false));
      }
      else {
        wodElementNames.add(elementName);
      }
      element.fillInProblems(javaProject, javaFileType, checkBindingValues, problems, typeCache, htmlCache);
    }
  }

  @Override
  public String toString() {
    return "[" + getClass().getName() + ": elements = " + _elements + "]";
  }
}
