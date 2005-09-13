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
package org.objectstyle.wolips.wodclipse.wod.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.types.api.Validation;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.wod.completion.WodBindingUtils;

/**
 * @author mschrag
 */
public class WodModelUtils {
  public static IWodModel createWodModel(IFile _wodFile, IDocument _wodDocument) {
    return new DocumentWodModel(_wodFile, _wodDocument);
  }

  public static void writeWodFormat(IWodModel _wodModel, Writer _writer) {
    PrintWriter pw = new PrintWriter(_writer);
    Iterator elementsIter = _wodModel.getElements().iterator();
    while (elementsIter.hasNext()) {
      IWodElement element = (IWodElement) elementsIter.next();
      pw.print(element.getElementName());
      pw.print(" : ");
      pw.print(element.getElementType());
      pw.print(" { ");
      pw.println();
      Iterator bindingsIter = element.getBindings().iterator();
      while (bindingsIter.hasNext()) {
        IWodBinding binding = (IWodBinding) bindingsIter.next();
        pw.print("  ");
        pw.print(binding.getName());
        pw.print(" = ");
        pw.print(binding.getValue());
        pw.print(";");
        pw.println();
      }
      pw.print("}");
      pw.println();
      if (elementsIter.hasNext()) {
        pw.println();
      }
    }
    pw.flush();
  }

  public static List getSemanticProblems(IJavaProject _javaProject, IWodModel _wodModel, Map _elementNameToTypeCache, Map _typeToApiModelWoCache) throws CoreException, IOException {
    long startTime = System.currentTimeMillis();
    boolean hasPositions = (_wodModel instanceof DocumentWodModel);
    Set htmlElementNames;
    if (hasPositions) {
      IFile wodFile = ((DocumentWodModel) _wodModel).getWodFile();
      IPath wodFilePath = wodFile.getLocation();
      IFile wodHtmlFile = WodModelUtils.getHtmlFileForWodFilePath(wodFilePath);
      htmlElementNames = new HashSet();
      if (wodHtmlFile != null) {
        WodModelUtils.fillInHtmlElementNames(wodHtmlFile, htmlElementNames);
      }
    }
    else {
      htmlElementNames = null;
    }

    Set elementNames = new HashSet();

    List problems = new LinkedList();
    Iterator elementsIter = _wodModel.getElements().iterator();
    while (elementsIter.hasNext()) {
      IWodElement element = (IWodElement) elementsIter.next();
      String elementName = element.getElementName();
      if (htmlElementNames != null && !htmlElementNames.contains(elementName)) {
        problems.add(new WodProblem(_wodModel, "There is no element named '" + elementName + "' in your component HTML file", (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null));
      }
      if (elementNames.contains(elementName)) {
        problems.add(new WodProblem(_wodModel, "Duplicate definition of '" + elementName + "'", (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null));
      }
      else {
        elementNames.add(elementName);
      }

      String elementTypeName = element.getElementType();
      IType elementType = WodBindingUtils.findElementType(_javaProject, elementTypeName, false, _elementNameToTypeCache);
      if (elementType == null) {
        problems.add(new WodProblem(_wodModel, "The class for '" + elementTypeName + "' is either missing or does extend WOElement.", (hasPositions) ? ((DocumentWodElement) element).getElementTypePosition() : null));
      }
      else {
        Wo wo = WodBindingUtils.findApiModelWo(elementType, _typeToApiModelWoCache);
        if (wo != null) {
          Map bindingsMap = element.getBindingsMap();
          Validation[] failedValidations = wo.getFailedValidations(bindingsMap);
          for (int i = 0; i < failedValidations.length; i ++) {
            String failedValidationMessage = failedValidations[i].getMessage();
            problems.add(new WodProblem(_wodModel, failedValidationMessage, (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null));
          }
        }
      }

      Set bindingNames = new HashSet();
      Iterator bindingsIter = element.getBindings().iterator();
      while (bindingsIter.hasNext()) {
        IWodBinding binding = (IWodBinding) bindingsIter.next();
        String bindingName = binding.getName();
        if (bindingNames.contains(bindingName)) {
          problems.add(new WodProblem(_wodModel, "Duplicate binding named '" + bindingName + "'", (hasPositions) ? ((DocumentWodBinding) binding).getNamePosition() : null));
        }
        else {
          bindingNames.add(bindingName);
        }
      }
    }

    if (htmlElementNames != null) {
      htmlElementNames.removeAll(elementNames);
      Iterator undefinedHtmlElementNames = htmlElementNames.iterator();
      while (undefinedHtmlElementNames.hasNext()) {
        String htmlElementName = (String) undefinedHtmlElementNames.next();
        problems.add(new WodProblem(_wodModel, "The component HTML file references an element '" + htmlElementName + "' which does not appear in the WOD file", null));
      }
    }

    return problems;
  }

  public static IFile getHtmlFileForWodFilePath(IPath _wodFilePath) {
    IPath templatePath = _wodFilePath.removeFileExtension().addFileExtension("html");
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(templatePath);
    return file;
  }

  public static void fillInHtmlElementNames(IFile _htmlFile, Set _htmlElementNames) throws CoreException, IOException {
    FileEditorInput fileInput = new FileEditorInput(_htmlFile);
    InputStream fileContents = fileInput.getStorage().getContents();
    BufferedInputStream bis = new BufferedInputStream(fileContents);
    int ch;

    char[] stringToMatch = { '<', 'w', 'e', 'b', 'o', 'b', 'j', 'e', 'c', 't', 'n', 'a', 'm', 'e', '=' };
    int matchIndex = 0;
    StringBuffer elementNameBuffer = null;
    boolean elementFound = false;
    while ((ch = bis.read()) != -1) {
      if (elementNameBuffer == null) {
        if (ch == ' ') {
          // ignore spaces
        }
        else if (Character.toLowerCase((char) ch) == stringToMatch[matchIndex]) {
          matchIndex++;
          if (matchIndex == stringToMatch.length) {
            elementNameBuffer = new StringBuffer();
            matchIndex = 0;
          }
        }
        else {
          matchIndex = 0;
          if (Character.toLowerCase((char) ch) == stringToMatch[matchIndex]) {
            matchIndex++;
          }
        }
      }
      else {
        if (ch == ' ') {
        }
        else if (ch == '"') {
        }
        else if (ch == '/') {
        }
        else if (ch == '>') {
          String elementName = elementNameBuffer.toString();
          _htmlElementNames.add(elementName);
          elementNameBuffer = null;
        }
        else {
          elementNameBuffer.append((char) ch);
        }
      }
    }
  }
}
