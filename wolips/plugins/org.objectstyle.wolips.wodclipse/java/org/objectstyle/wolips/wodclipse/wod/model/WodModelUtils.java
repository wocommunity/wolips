/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.types.api.Binding;
import org.objectstyle.wolips.core.resources.types.api.Validation;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.wod.completion.WodBindingUtils;

/**
 * @author mschrag
 */
public class WodModelUtils {
  public static IWodModel createWodModel(IFile _wodFile, IDocument _wodDocument) {
    return new DocumentWodModel(_wodFile, _wodDocument);
  }

  public static void writeWodFormat(IWodModel _wodModel, Writer _writer, boolean _alphabetize) {
    PrintWriter pw = new PrintWriter(_writer);
    List elementsList = _wodModel.getElements();
    if (_alphabetize) {
      elementsList = new LinkedList(elementsList);
      Collections.sort(elementsList);
    }
    Iterator elementsIter = elementsList.iterator();
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

  public static List getSemanticProblems(IWodModel _wodModel, LocalizedComponentsLocateResult _locateResults, IJavaProject _javaProject, Map _elementNameToTypeCache, Map _typeToApiModelWoCache) throws CoreException {
    long startTime = System.currentTimeMillis();
    boolean hasPositions = (_wodModel instanceof DocumentWodModel);
    boolean checkBindingValues = WodclipsePlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.CHECK_BINDING_VALUES);

    String htmlFileName = null;
    Set htmlElementNames = null;
    try {
      htmlElementNames = new HashSet();
      IFile htmlFile = _locateResults.getFirstHtmlFile();
      if (htmlFile != null) {
        htmlFileName = htmlFile.getName();
        WodModelUtils.fillInHtmlElementNames(htmlFile, htmlElementNames);
      }
    }
    catch (IOException e) {
      WodclipsePlugin.getDefault().log("Failed to locate html, java, or api file.", e);
    }
    
    IType javaFileType = _locateResults.getDotJavaType();
    String javaFileName = null;
    if (javaFileType != null) {
      javaFileName = _locateResults.getDotJava().getName();
    }
    
    String apiFileName = null;
    IFile apiFile = _locateResults.getDotApi();
    if (apiFile != null) {
      apiFileName = apiFile.getName();
    }

    Set elementNames = new HashSet();

    List problems = new LinkedList();
    Iterator elementsIter = _wodModel.getElements().iterator();
    while (elementsIter.hasNext()) {
      IWodElement element = (IWodElement) elementsIter.next();
      String elementName = element.getElementName();
      if (htmlElementNames != null && !htmlElementNames.contains(elementName)) {
        problems.add(new WodProblem(_wodModel, "There is no element named '" + elementName + "' in your component HTML file", (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null, true, htmlFileName));
      }
      if (elementNames.contains(elementName)) {
        problems.add(new WodProblem(_wodModel, "Duplicate definition of '" + elementName + "'", (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null, false, (String)null));
      }
      else {
        elementNames.add(elementName);
      }

      String elementTypeName = element.getElementType();
      IType elementType = WodBindingUtils.findElementType(_javaProject, elementTypeName, false, _elementNameToTypeCache);
      if (elementType == null) {
        problems.add(new WodProblem(_wodModel, "The class for '" + elementTypeName + "' is either missing or does not extend WOElement.", (hasPositions) ? ((DocumentWodElement) element).getElementTypePosition() : null, false, elementTypeName + ".java"));
      }
      else {
        Wo wo;
        try {
          wo = WodBindingUtils.findApiModelWo(elementType, _typeToApiModelWoCache);
          if (wo != null) {
            Map bindingsMap = element.getBindingsMap();
            Binding[] bindings = wo.getBindings();
            for (int i = 0; i < bindings.length; i++) {
              String bindingName = bindings[i].getName();
              if (bindings[i].isExplicitlyRequired() && !bindingsMap.containsKey(bindingName)) {
                problems.add(new WodProblem(_wodModel, "Binding '" + bindingName + "' is required for " + wo.getClassName(), (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null, false, elementTypeName + ".api"));
              }
            }
            Validation[] failedValidations = wo.getFailedValidations(bindingsMap);
            for (int i = 0; i < failedValidations.length; i++) {
              String failedValidationMessage = failedValidations[i].getMessage();
              problems.add(new WodProblem(_wodModel, failedValidationMessage, (hasPositions) ? ((DocumentWodElement) element).getElementNamePosition() : null, false, elementTypeName + ".api"));
            }
          }
        }
        catch (Throwable e) {
          WodclipsePlugin.getDefault().log(e);
        }
      }

      Set bindingNames = new HashSet();
      Iterator checkForDuplicateBindingsIter = element.getBindings().iterator();
      while (checkForDuplicateBindingsIter.hasNext()) {
        IWodBinding binding = (IWodBinding) checkForDuplicateBindingsIter.next();
        String bindingName = binding.getName();
        if (bindingNames.contains(bindingName)) {
          problems.add(new WodProblem(_wodModel, "Duplicate binding named '" + bindingName + "'", (hasPositions) ? ((DocumentWodBinding) binding).getNamePosition() : null, false, (String)null));
        }
        else {
          bindingNames.add(bindingName);
        }
      }

      if (checkBindingValues && javaFileType != null) {
        try {
          Iterator checkValuesBindingsIter = element.getBindings().iterator();
          while (checkValuesBindingsIter.hasNext()) {
            IWodBinding binding = (IWodBinding) checkValuesBindingsIter.next();
            if (binding.shouldValidate() && WodModelUtils.isBindingValueKeyPath(binding)) {
              String bindingValue = binding.getValue();
              BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(bindingValue, javaFileType, _javaProject);
              // NTS: Technically these need to be related to every java file name in the key path
              if (!bindingValueKeyPath.isValid()) {
                problems.add(new WodProblem(_wodModel, "There is no key path '" + bindingValue + "' for " + javaFileType.getElementName(), (hasPositions) ? ((DocumentWodBinding) binding).getValuePosition() : null, false, bindingValueKeyPath.getRelatedToFileNames()));
              }
              else if (bindingValueKeyPath.isAmbiguous()) {
                problems.add(new WodProblem(_wodModel, "Unable to verify key path '" + bindingValue + "' for " + javaFileType.getElementName(), (hasPositions) ? ((DocumentWodBinding) binding).getValuePosition() : null, true, bindingValueKeyPath.getRelatedToFileNames()));
              }
              //              else {
              //                String[] validApiValues = WodBindingUtils.getValidValues(elementType, binding.getName(), _typeToApiModelWoCache);
              //                if (validApiValues != null && !Arrays.asList(validApiValues).contains(bindingValue)) {
              //                  problems.add(new WodProblem(_wodModel, "The .api file for " + wodJavaType.getElementName() + " declares '" + bindingValue + "' to be an invalid value.", (hasPositions) ? ((DocumentWodBinding) binding).getValuePosition() : null, false));
              //                }
              //              }
            }
          }
        }
        catch (Throwable t) {
          WodclipsePlugin.getDefault().log("Failed to check wod binding values.", t);
        }
      }
    }

    if (htmlElementNames != null) {
      htmlElementNames.removeAll(elementNames);
      Iterator undefinedHtmlElementNames = htmlElementNames.iterator();
      while (undefinedHtmlElementNames.hasNext()) {
        String htmlElementName = (String) undefinedHtmlElementNames.next();
        problems.add(new WodProblem(_wodModel, "The component HTML file references an element '" + htmlElementName + "' which does not appear in the WOD file", null, false, htmlFileName));
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
    IEncodedStorage storage = (IEncodedStorage) fileInput.getStorage();
    InputStream fileContents = storage.getContents();
    BufferedReader br = new BufferedReader(new InputStreamReader(fileContents, storage.getCharset()));
    int ch;

    char[] stringToMatch = { '<', 'w', 'e', 'b', 'o', 'b', 'j', 'e', 'c', 't', 'n', 'a', 'm', 'e', '=' };
    int matchIndex = 0;
    StringBuffer elementNameBuffer = null;
    boolean elementFound = false;
    while ((ch = br.read()) != -1) {
      if (elementNameBuffer == null) {
        if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
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
        if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
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

  public static boolean isIndexContainedByWodUnit(int _index, IWodUnit _wodUnit) {
    return _index >= _wodUnit.getStartOffset() && _index <= _wodUnit.getEndOffset();
  }

  public static boolean isBindingValueKeyPath(IWodBinding _binding) {
    String bindingValue = _binding.getValue();
    boolean isBindingValueKeyPath;
    if (bindingValue.length() > 0) {
      char ch = bindingValue.charAt(0);
      isBindingValueKeyPath = Character.isJavaIdentifierStart(ch);
    }
    else {
      isBindingValueKeyPath = false;
    }
    return isBindingValueKeyPath;
  }

}
