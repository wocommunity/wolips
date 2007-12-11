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
package org.objectstyle.wolips.wodclipse.core.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.bindings.wod.HtmlElementCache;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.bindings.woo.IEOModelGroupCache;
import org.objectstyle.wolips.bindings.woo.IWooModel;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.DocumentWodModel;
import org.objectstyle.wolips.wodclipse.core.document.WodFileDocumentProvider;
import org.objectstyle.wolips.wooeditor.model.EOModelGroupCache;
import org.objectstyle.wolips.wooeditor.model.WooModel;
import org.objectstyle.wolips.wooeditor.model.WooModelException;

/**
 * @author mschrag
 */
public class WodModelUtils {
  public static IWodModel createWodModel(IFile wodFile, IDocument wodDocument) {
    return new DocumentWodModel(wodFile, wodDocument);
  }

  public static IWooModel createWooModel(IDocument wooDocument) throws WooModelException {
	return (IWooModel) new WooModel(wooDocument.get());
  }
  
  public static IWooModel createWooModel(IFile wooFile) {
	return (IWooModel) new WooModel(wooFile);
  }
  
  public static void deleteWodProblems(IFile wodFile) {
    try {
      if (wodFile.exists()) {
        wodFile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
      }
    }
    catch (CoreException e) {
      Activator.getDefault().debug(e);
    }
  }

  public static List<WodProblem> getProblems(IWodElement wodElement, WodParserCache cache) throws CoreException, IOException {
    return WodModelUtils.getProblems(wodElement, cache.getComponentsLocateResults(), cache.getTypeCache(), cache.getHtmlElementCache());
  }
  
  public static List<WodProblem> getProblems(IWodElement wodElement, LocalizedComponentsLocateResult locateResult, TypeCache typeCache, HtmlElementCache htmlCache) throws CoreException, IOException {
    List<WodProblem> problems = new LinkedList<WodProblem>();
    if (wodElement != null) {
      IFile wodFile = locateResult.getFirstWodFile();
      if (wodFile != null) {
        IJavaProject javaProject = JavaCore.create(wodFile.getProject());
        wodElement.fillInProblems(javaProject, locateResult.getDotJavaType(), true, problems, typeCache, htmlCache);
      }
    }
    return problems;
  }

  public static List<WodProblem> getProblems(IWodModel wodModel, WodParserCache cache) throws CoreException, IOException {
    return WodModelUtils.getProblems(wodModel, cache.getComponentsLocateResults(), cache.getTypeCache(), cache.getHtmlElementCache());
  }
  
  public static List<WodProblem> getProblems(IWodModel wodModel, LocalizedComponentsLocateResult locateResult, TypeCache typeCache, HtmlElementCache htmlCache) throws CoreException, IOException {
    List<WodProblem> problems = new LinkedList<WodProblem>();
    if (wodModel != null) {
      IFile wodFile = locateResult.getFirstWodFile();
      if (wodFile != null) {
        IJavaProject javaProject = JavaCore.create(wodFile.getProject());
        wodModel.fillInProblems(javaProject, locateResult.getDotJavaType(), true, problems, typeCache, htmlCache);
      }
    }
    return problems;
  }

  public static void validateWodDocument(IDocument wodDocument, LocalizedComponentsLocateResult locateResult, TypeCache typeCache, HtmlElementCache htmlCache) {
    try {
      IFile wodFile = locateResult.getFirstWodFile();
      if (wodFile != null) {
        WodModelUtils.deleteWodProblems(wodFile);

        IJavaProject javaProject = JavaCore.create(wodFile.getProject());
        IWodModel wodModel = WodModelUtils.createWodModel(wodFile, wodDocument);
        List<WodProblem> problems = wodModel.getProblems(javaProject, locateResult.getDotJavaType(), typeCache, htmlCache);
        for (WodProblem problem : problems) {
          WodModelUtils.createMarker(wodFile, problem);
        }
      }
    }
    catch (Exception e) {
      Activator.getDefault().log(e);
    }
  }

  public static void validateWodFile(IFile wodFile, LocalizedComponentsLocateResult locateResults, TypeCache typeCache, HtmlElementCache htmlCache) throws CoreException {
    FileEditorInput input = new FileEditorInput(wodFile);
    WodFileDocumentProvider provider = new WodFileDocumentProvider();
    provider.connect(input);
    try {
      IDocument document = provider.getDocument(input);
      WodModelUtils.validateWodDocument(document, locateResults, typeCache, htmlCache);
    }
    finally {
      provider.disconnect(input);
    }
  }

  public static IMarker createMarker(IFile file, WodProblem wodProblem) {
    if (file == null) {
      return null;
    }
    
    Position problemPosition = wodProblem.getPosition();

    // String type = "org.eclipse.ui.workbench.texteditor.error";
    // String type = "org.eclipse.ui.workbench.texteditor.warning";
    // Annotation problemAnnotation = new Annotation(type, false,
    // problem.getMessage());
    // Position problemPosition = currentPosition.getPosition();
    // annotationModel.addAnnotation(problemAnnotation,
    // problemPosition);

    IMarker marker = null;
    try {
      if (wodProblem.getForceFile() != null) {
        marker = wodProblem.getForceFile().createMarker(Activator.TEMPLATE_PROBLEM_MARKER);
      }
      else {
        marker = file.createMarker(Activator.TEMPLATE_PROBLEM_MARKER);
      }
      marker.setAttribute(IMarker.MESSAGE, wodProblem.getMessage());
      int severity;
      if (wodProblem.isWarning()) {
        severity = IMarker.SEVERITY_WARNING;
      }
      else {
        severity = IMarker.SEVERITY_ERROR;
      }
      marker.setAttribute(IMarker.SEVERITY, new Integer(severity));
      if (problemPosition != null) {
//        IWodModel model = getModel();
//        if (_lineNumber == -1 && model instanceof DocumentWodModel) {
//          marker.setAttribute(IMarker.LINE_NUMBER, ((DocumentWodModel) model).getDocument().getLineOfOffset(problemPosition.getOffset()));
//        }
//        else
        if (wodProblem.getLineNumber() != -1) {
          marker.setAttribute(IMarker.LINE_NUMBER, wodProblem.getLineNumber());
        }
        marker.setAttribute(IMarker.CHAR_START, problemPosition.getOffset());
        marker.setAttribute(IMarker.CHAR_END, problemPosition.getOffset() + problemPosition.getLength());
      }
      marker.setAttribute(IMarker.TRANSIENT, false);
    }
    catch (CoreException e) {
      e.printStackTrace();
      Activator.getDefault().log(e);
    }
//    catch (BadLocationException e) {
//      Activator.getDefault().log(e);
//    }
    return marker;
  }

  public static IEOModelGroupCache createModelGroupCache() {
	  return (IEOModelGroupCache) new EOModelGroupCache();
  }
}
