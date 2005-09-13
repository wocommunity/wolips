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
package org.objectstyle.wolips.wodclipse.wod.completion;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.objectstyle.wolips.core.resources.types.api.Binding;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.wod.model.WodModelUtils;
import org.objectstyle.wolips.wodclipse.wod.parser.AssignmentOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.BindingNameRule;
import org.objectstyle.wolips.wodclipse.wod.parser.BindingValueRule;
import org.objectstyle.wolips.wodclipse.wod.parser.CloseDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementTypeOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.wod.parser.EndAssignmentWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.OpenDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.wod.parser.OperatorRule;
import org.objectstyle.wolips.wodclipse.wod.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.wod.parser.WodScanner;

/**
 * @author mike
 */
public class WodCompletionProcessor implements IContentAssistProcessor {
  private IEditorPart myEditor;
  private Set myValidElementNames;
  private long myTemplateLastModified;

  public WodCompletionProcessor(IEditorPart _editor) {
    myEditor = _editor;
  }

  public char[] getContextInformationAutoActivationCharacters() {
    return null;
  }

  public IContextInformationValidator getContextInformationValidator() {
    return null;
  }

  public IContextInformation[] computeContextInformation(ITextViewer _viewer, int _offset) {
    return null;
  }

  public char[] getCompletionProposalAutoActivationCharacters() {
    return new char[] { ':', '.', '=' };
  }

  public ICompletionProposal[] computeCompletionProposals(ITextViewer _viewer, int _offset) {
    Set completionProposalsSet = new TreeSet();

    try {
      IDocument document = _viewer.getDocument();
      IEditorInput input = myEditor.getEditorInput();
      if (input instanceof IPathEditorInput) {
        IPathEditorInput pathInput = (IPathEditorInput) input;
        IPath path = pathInput.getPath();
        IResource file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
        IJavaProject javaProject = JavaCore.create(file.getProject());

        // Without an underlying model, we have to rescan the line to figure out exactly 
        // what the current matching rule was, so we know what kind of token we're dealing with.
        IRegion lineRegion = document.getLineInformationOfOffset(_offset);
        WodScanner scanner = WodScanner.newWODScanner();
        scanner.setRange(document, lineRegion.getOffset(), lineRegion.getLength());
        boolean foundToken = false;
        RulePosition rulePosition = null;
        while (!foundToken && (rulePosition = scanner.nextRulePosition()) != null) {
          int tokenOffset = rulePosition.getTokenOffset();
          if (_offset == lineRegion.getOffset() && _offset == tokenOffset) {
            foundToken = true;
          }
          else if (_offset > tokenOffset && _offset <= rulePosition.getTokenEndOffset()) {
            foundToken = true;
          }
        }

        // We can't reliably use rulePosition here because it might be null ...
        int tokenOffset = scanner.getTokenOffset();
        int tokenLength = scanner.getTokenLength();
        IRule rule = (rulePosition == null) ? null : rulePosition.getRule();
        // If you make a completion request in the middle of whitespace, we
        // don't want to select the whitespace, so zero out the 
        // whitespace token offsets.
        if (rule instanceof WhitespaceRule) {
          int partialOffset = (_offset - tokenOffset);
          _offset += partialOffset;
          tokenOffset += partialOffset;
          tokenLength = 0;
        }
        else {
          _viewer.setSelectedRange(_offset, tokenLength - (_offset - tokenOffset));
        }
        String token = document.get(tokenOffset, tokenLength);
        String tokenType = null;
        if (foundToken) {
          if (rulePosition.isRuleOfType(ElementNameRule.class)) {
            tokenType = PreferenceConstants.ELEMENT_NAME;
          }
          else if (rulePosition.isRuleOfType(ElementTypeRule.class)) {
            tokenType = PreferenceConstants.ELEMENT_TYPE;
          }
          else if (rulePosition.isRuleOfType(BindingNameRule.class)) {
            tokenType = PreferenceConstants.BINDING_NAME;
          }
          else if (rulePosition.isRuleOfType(BindingValueRule.class)) {
            tokenType = PreferenceConstants.BINDING_VALUE;
          }
          else if (rulePosition.isRuleOfType(OperatorRule.class)) {
            tokenOffset += tokenLength;
            tokenLength = 0;
            if (RulePosition.isOperatorOfType(rulePosition, CloseDefinitionWordDetector.class)) {
              tokenType = PreferenceConstants.ELEMENT_NAME;
            }
            else if (RulePosition.isOperatorOfType(rulePosition, ElementTypeOperatorWordDetector.class)) {
              tokenType = PreferenceConstants.ELEMENT_TYPE;
            }
            else if (RulePosition.isOperatorOfType(rulePosition, OpenDefinitionWordDetector.class) || RulePosition.isOperatorOfType(rulePosition, EndAssignmentWordDetector.class)) {
              tokenType = PreferenceConstants.BINDING_NAME;
            }
            else if (RulePosition.isOperatorOfType(rulePosition, AssignmentOperatorWordDetector.class)) {
              tokenType = PreferenceConstants.BINDING_VALUE;
            }
            else {
              tokenType = null;//PreferenceConstants.OPERATOR;
            }
          }
          else {
            tokenType = null;
          }
        }

        // If there was no matching token type, then that means we're
        // in an invalid parse state and we have to "guess".  So we backscan
        // until we find an operator that we know about, which will tell us
        // where we are in the document.
        if (tokenType == null) {
          int startOffset = tokenOffset;
          if (startOffset != 0 && startOffset == document.getLength()) {
            startOffset--;
          }
          int hintChar = -1;
          //for (int startOffset = tokenOffset - 1; tokenType == null && startOffset > 0; startOffset--) {
          for (; tokenType == null && startOffset > 0; startOffset--) {
            int ch = document.getChar(startOffset);
            if (ch == ':') {
              tokenType = PreferenceConstants.ELEMENT_TYPE;
            }
            else if (ch == '{' || ch == ';') {
              tokenType = PreferenceConstants.BINDING_NAME;
            }
            else if (ch == '=') {
              tokenType = PreferenceConstants.BINDING_VALUE;
            }
            else if (ch == '}') {
              tokenType = PreferenceConstants.ELEMENT_NAME;
            }
          }
        }

        if (tokenType == null) {
          tokenType = PreferenceConstants.ELEMENT_NAME;
        }

        // ... Fill in completion proposals based on the token type
        if (tokenType == PreferenceConstants.ELEMENT_NAME) {
          fillInElementNameCompletionProposals(javaProject, document, path, token, tokenOffset, _offset, completionProposalsSet);
        }
        else if (tokenType == PreferenceConstants.ELEMENT_TYPE) {
          fillInElementTypeCompletionProposals(javaProject, token, tokenOffset, _offset, completionProposalsSet);
        }
        else if (tokenType == PreferenceConstants.BINDING_NAME) {
          IType elementType = WodCompletionProcessor.findNearestElementType(javaProject, document, scanner, tokenOffset);
          fillInBindingNameCompletionProposals(javaProject, elementType, path, token, tokenOffset, _offset, completionProposalsSet);
        }
        else if (tokenType == PreferenceConstants.BINDING_VALUE) {
          String elementTypeName = path.removeFileExtension().lastSegment();
          IType elementType = WodBindingUtils.findElementType(javaProject, elementTypeName, true, new HashMap());
          fillInBindingValueCompletionProposals(javaProject, elementType, token, tokenOffset, _offset, completionProposalsSet);
        }
      }
    }
    catch (JavaModelException e) {
      WodclipsePlugin.getDefault().log(e);
    }
    catch (BadLocationException e) {
      WodclipsePlugin.getDefault().log(e);
    }
    catch (CoreException e) {
      WodclipsePlugin.getDefault().log(e);
    }
    catch (IOException e) {
      WodclipsePlugin.getDefault().log(e);
    }

    ICompletionProposal[] completionProposals = new ICompletionProposal[completionProposalsSet.size()];
    Iterator completionProposalsIter = completionProposalsSet.iterator();
    for (int i = 0; completionProposalsIter.hasNext(); i++) {
      WodCompletionProposal wodCompletionProposal = (WodCompletionProposal) completionProposalsIter.next();
      completionProposals[i] = wodCompletionProposal.toCompletionProposal();
    }
    return completionProposals;
  }

  public String getErrorMessage() {
    return null;
  }

  protected Set validElementNames(IPath _filePath) throws CoreException, IOException {
    // Look for an html file of the same name as the .wod file we're editing now
    IFile htmlFile = WodModelUtils.getHtmlFileForWodFilePath(_filePath);
    //myTemplateLastModified = IFile.NULL_STAMP;
    if (htmlFile != null) {
      long templateLastModified = htmlFile.getModificationStamp();
      // if we either haven't either retrieved element names or the HTML template has been
      // modified since the last time we did, rescan the template for <webobject name =" tags,
      // ignoring spaces, etc.
      if (myValidElementNames == null || myTemplateLastModified == IFile.NULL_STAMP || templateLastModified > myTemplateLastModified) {
        myValidElementNames = new HashSet();
        myTemplateLastModified = templateLastModified;
        WodModelUtils.fillInHtmlElementNames(htmlFile, myValidElementNames);
      }
    }
    return myValidElementNames;
  }

  protected void fillInElementNameCompletionProposals(IJavaProject _project, IDocument _document, IPath _wodFilePath, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws CoreException, IOException {
    String partialToken = WodCompletionProcessor.partialToken(_token, _tokenOffset, _offset).toLowerCase();
    Iterator validElementNamesIter = validElementNames(_wodFilePath).iterator();

    // We really need something like the AST ... This is a pretty expensive way to go here.  To
    // find element names that have already been mapped, we reparse the wod file.  Lame.
    Set alreadyUsedElementNames;
    try {
      alreadyUsedElementNames = WodScanner.getTextForRulesOfType(_document, ElementNameRule.class);
    }
    catch (Throwable t) {
      // It's not THAT big of a deal ...
      WodclipsePlugin.getDefault().log(t);
      alreadyUsedElementNames = new HashSet();
    }

    while (validElementNamesIter.hasNext()) {
      String validElementName = (String) validElementNamesIter.next();
      if (validElementName.toLowerCase().startsWith(partialToken) && !alreadyUsedElementNames.contains(validElementName)) {
        _completionProposalsSet.add(new WodCompletionProposal(_token, _tokenOffset, _offset, validElementName));
      }
    }
  }

  protected static void fillInElementTypeCompletionProposals(IJavaProject _project, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws JavaModelException {
    // Lookup type names that extend WOElement based on the current partial token
    TypeNameCollector typeNameCollector = new TypeNameCollector(_project, false);
    String partialToken = WodCompletionProcessor.partialToken(_token, _tokenOffset, _offset);
    if (partialToken.length() > 0) {
      WodBindingUtils.findMatchingElementClassNames(partialToken, SearchPattern.R_PREFIX_MATCH, typeNameCollector);
      Iterator matchingElementClassNamesIter = typeNameCollector.typeNames();
      while (matchingElementClassNamesIter.hasNext()) {
        String matchingElementClassName = (String) matchingElementClassNamesIter.next();
        String shortElementTypeName = WodBindingUtils.getShortClassName(matchingElementClassName);
        WodCompletionProposal completionProposal = new WodCompletionProposal(_token, _tokenOffset, _offset, shortElementTypeName);
        _completionProposalsSet.add(completionProposal);
      }
    }
  }

  protected static IType findNearestElementType(IJavaProject _project, IDocument _document, WodScanner _scanner, int _offset) throws BadLocationException, JavaModelException {
    // Go hunting for the element type in a potentially malformed document ...
    IType type;
    int colonOffset = _offset;
    if (colonOffset >= _document.getLength()) {
      colonOffset--;
    }
    for (; colonOffset >= 0; colonOffset--) {
      char ch = _document.getChar(colonOffset);
      if (ch == ':') {
        break;
      }
    }
    if (colonOffset != -1) {
      _scanner.setRange(_document, colonOffset, _offset);
      RulePosition elementRulePosition = _scanner.getFirstRulePositionOfType(ElementTypeRule.class);
      if (elementRulePosition != null) {
        String elementTypeName = elementRulePosition.getText();
        type = WodBindingUtils.findElementType(_project, elementTypeName, false, new HashMap());
      }
      else {
        // we didn't find a ElementTypeRule
        type = null;
      }
    }
    else {
      // failed colonoscopy
      type = null;
    }
    return type;
  }

  protected static void fillInBindingNameCompletionProposals(IJavaProject _project, IType _elementType, IPath _wodFilePath, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws JavaModelException {
    String partialToken = WodCompletionProcessor.partialToken(_token, _tokenOffset, _offset);
    List bindingKeys = WodBindingUtils.createMatchingBindingKeys(_elementType, partialToken, false, WodBindingUtils.MUTATORS_ONLY);
    WodBindingUtils.fillInCompletionProposals(bindingKeys, _token, _tokenOffset, _offset, _completionProposalsSet);

    // API files:
    try {
      Wo wo = WodBindingUtils.findWo(_elementType);
      if (wo != null) {
        String lowercasePartialToken = partialToken.toLowerCase();
        Binding[] bindings = wo.getBindings();
        for (int i = 0; i < bindings.length; i++) {
          String bindingName = bindings[i].getName();
          String lowercaseBindingName = bindingName.toLowerCase();
          if (lowercaseBindingName.startsWith(lowercasePartialToken)) {
            _completionProposalsSet.add(new WodCompletionProposal(_token, _tokenOffset, _offset, bindingName));
          }
        }
      }
    }
    catch (Throwable t) {
      // It's not that big a deal ... give up on api files
      t.printStackTrace();
    }
  }

  protected static void fillInBindingValueCompletionProposals(IJavaProject _project, IType _elementType, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws JavaModelException {
    String partialToken = WodCompletionProcessor.partialToken(_token, _tokenOffset, _offset);
    String[] bindingKeyNames = WodBindingUtils.getBindingKeyNames(partialToken);
    IType lastType = WodBindingUtils.getLastType(_project, _elementType, bindingKeyNames);
    if (lastType != null) {
      // Jump forward to the last '.' and look for valid "get" method completion
      // proposals based on the partial token
      String bindingKeyName = bindingKeyNames[bindingKeyNames.length - 1];
      List bindingKeys = WodBindingUtils.createMatchingBindingKeys(lastType, bindingKeyName, false, WodBindingUtils.ACCESSORS_ONLY);
      WodBindingUtils.fillInCompletionProposals(bindingKeys, bindingKeyNames[bindingKeyNames.length - 1], _tokenOffset + partialToken.lastIndexOf('.') + 1, _offset, _completionProposalsSet);
    }
  }

  protected static String partialToken(String _token, int _tokenOffset, int _offset) {
    String partialToken;
    int partialIndex = _offset - _tokenOffset;
    if (partialIndex > _token.length()) {
      partialToken = _token;
    }
    else {
      partialToken = _token.substring(0, _offset - _tokenOffset);
    }
    return partialToken;
  }
}
