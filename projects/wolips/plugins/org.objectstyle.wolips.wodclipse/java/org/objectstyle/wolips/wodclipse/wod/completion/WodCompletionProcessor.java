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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
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
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.wodclipse.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.wod.parser.BindingNameRule;
import org.objectstyle.wolips.wodclipse.wod.parser.BindingValueRule;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.wod.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.wod.parser.OperatorRule;
import org.objectstyle.wolips.wodclipse.wod.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.wod.parser.WodScanner;

/**
 * @author mike
 */
public class WodCompletionProcessor implements IContentAssistProcessor {
  private static final String[] FIELD_PREFIXES = { "_" };
  private static final String[] SET_METHOD_PREFIXES = { "set", "_set" };
  private static final String[] GET_METHOD_PREFIXES = { "get", "_get", "is", "_is" };
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
          else {
            if (_offset > tokenOffset) {
              int tokenEndOffset = rulePosition.getTokenEndOffset();
              if (_offset < tokenEndOffset) {
                foundToken = true;
              }
              else if (_offset == tokenEndOffset) {
                // If you're sitting right after an operator, you don't want to
                // return the operator as the match, rather you want the
                // next token after the operator.
                foundToken = (rulePosition.getRule() instanceof OperatorRule);
              }
            }
          }
        }

        int tokenOffset = rulePosition.getTokenOffset();
        int tokenLength = rulePosition.getTokenLength();
        IRule rule = rulePosition.getRule();
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
          if (rule instanceof ElementNameRule) {
            tokenType = PreferenceConstants.ELEMENT_NAME;
          }
          else if (rule instanceof ElementTypeRule) {
            tokenType = PreferenceConstants.ELEMENT_TYPE;
          }
          else if (rule instanceof BindingNameRule) {
            tokenType = PreferenceConstants.BINDING_NAME;
          }
          else if (rule instanceof BindingValueRule) {
            tokenType = PreferenceConstants.BINDING_VALUE;
          }
          else if (rule instanceof OperatorRule) {
            tokenType = null;//PreferenceConstants.OPERATOR;
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
          IType elementType = findElementType(javaProject, document, scanner, tokenOffset);
          fillInBindingNameCompletionProposals(javaProject, elementType, path, token, tokenOffset, _offset, completionProposalsSet);
        }
        else if (tokenType == PreferenceConstants.BINDING_VALUE) {
          String associatedTypeName = path.removeFileExtension().lastSegment();
          IType associatedType = findElementType(javaProject, associatedTypeName, true);
          fillInBindingValueCompletionProposals(javaProject, associatedType, token, tokenOffset, _offset, completionProposalsSet);
        }
      }
    }
    catch (JavaModelException e) {
      e.printStackTrace();
    }
    catch (BadLocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
    IPath templatePath = _filePath.removeFileExtension().addFileExtension("html");
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(templatePath);
    //myTemplateLastModified = IFile.NULL_STAMP;
    if (file != null) {
      long templateLastModified = file.getModificationStamp();
      // if we either haven't either retrieved element names or the HTML template has been
      // modified since the last time we did, rescan the template for <webobject name =" tags,
      // ignoring spaces, etc.
      if (myValidElementNames == null || myTemplateLastModified == IFile.NULL_STAMP || templateLastModified > myTemplateLastModified) {
        myValidElementNames = new HashSet();
        myTemplateLastModified = templateLastModified;
        FileEditorInput fileInput = new FileEditorInput(file);
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
            else if (ch == '>') {
              String elementName = elementNameBuffer.toString();
              myValidElementNames.add(elementName);
              elementNameBuffer = null;
            }
            else {
              elementNameBuffer.append((char) ch);
            }
          }
        }
      }
    }
    return myValidElementNames;
  }

  protected String partialToken(String _token, int _tokenOffset, int _offset) {
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

  protected void fillInElementNameCompletionProposals(IJavaProject _project, IDocument _document, IPath _wodFilePath, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws CoreException, IOException {
    String partialToken = partialToken(_token, _tokenOffset, _offset).toLowerCase();
    Iterator validElementNamesIter = validElementNames(_wodFilePath).iterator();

    // We really need something like the AST ... This is a pretty expensive way to go here.  To
    // find element names that have already been mapped, we reparse the wod file.  Lame.
    Set alreadyUsedElementNames;
    try {
      alreadyUsedElementNames = WodScanner.getTextForRulesOfType(_document, ElementNameRule.class);
    }
    catch (Throwable t) {
      // It's not THAT big of a deal ...
      t.printStackTrace();
      alreadyUsedElementNames = new HashSet();
    }

    while (validElementNamesIter.hasNext()) {
      String validElementName = (String) validElementNamesIter.next();
      if (validElementName.toLowerCase().startsWith(partialToken) && !alreadyUsedElementNames.contains(validElementName)) {
        _completionProposalsSet.add(new WodCompletionProposal(_token, _tokenOffset, _offset, validElementName));
      }
    }
  }

  protected void fillInElementTypeCompletionProposals(IJavaProject _project, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws JavaModelException {
    // Lookup type names that extend WOElement based on the current partial token
    TypeNameCollector typeNameCollector = new TypeNameCollector(_project, false);
    String partialToken = partialToken(_token, _tokenOffset, _offset);
    if (partialToken.length() > 0) {
      findMatchingElementClassNames(partialToken, SearchPattern.R_PREFIX_MATCH, typeNameCollector);
      Iterator matchingElementClassNamesIter = typeNameCollector.typeNames();
      while (matchingElementClassNamesIter.hasNext()) {
        String matchingElementClassName = (String) matchingElementClassNamesIter.next();
        String matchingElementTypeName;
        int lastDotIndex = matchingElementClassName.lastIndexOf('.');
        if (lastDotIndex == -1) {
          matchingElementTypeName = matchingElementClassName;
        }
        else {
          matchingElementTypeName = matchingElementClassName.substring(lastDotIndex + 1);
        }
        WodCompletionProposal completionProposal = new WodCompletionProposal(_token, _tokenOffset, _offset, matchingElementTypeName);
        _completionProposalsSet.add(completionProposal);
      }
    }
  }

  protected void fillInBindingNameCompletionProposals(IJavaProject _project, IType _elementType, IPath _wodFilePath, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws JavaModelException {
    String partialToken = partialToken(_token, _tokenOffset, _offset).toLowerCase();

    // Walk the type hierarchy for the current type and try to find
    // set methods and public/protocted attributes.
    ITypeHierarchy typeHierarchy = _elementType.newSupertypeHierarchy(null);
    IType[] types = typeHierarchy.getAllTypes();
    for (int typeNum = 0; typeNum < types.length; typeNum++) {
      IField[] fields = types[typeNum].getFields();
      for (int fieldNum = 0; fieldNum < fields.length; fieldNum++) {
        findMemberProposals(fields[fieldNum], partialToken, WodCompletionProcessor.FIELD_PREFIXES, _token, _tokenOffset, _offset, _completionProposalsSet, 1, false, false);
      }

      IMethod[] methods = types[typeNum].getMethods();
      for (int methodNum = 0; methodNum < methods.length; methodNum++) {
        findMemberProposals(methods[methodNum], partialToken, WodCompletionProcessor.SET_METHOD_PREFIXES, _token, _tokenOffset, _offset, _completionProposalsSet, 1, false, false);
      }
    }

    // API files:
    /*
     try {
     IOpenable typeContainer = _elementType.getOpenable();
     if (typeContainer instanceof IClassFile) {
     IClassFile classFile = (IClassFile) typeContainer;
     IJavaElement parent = classFile.getParent();
     if (parent instanceof IPackageFragment) {
     IPackageFragment parentPackage = (IPackageFragment) parent;
     IPath packagePath = parentPackage.getPath();
     IPath apiPath = packagePath.removeLastSegments(2).append(_elementType.getElementName()).addFileExtension("api");
     File apiFile = apiPath.toFile();
     boolean fileExists = apiFile.exists();
     if (!fileExists && parentPackage.getElementName().startsWith("com.webobjects")) {
     apiFile = new File("/Developer/Applications/WebObjects/WebObjects Builder.app/Contents/Resources/WebObjectDefinitions.xml");
     fileExists = apiFile.exists();
     }
     if (fileExists) {
     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
     DocumentBuilder db = dbf.newDocumentBuilder();
     Document apiDoc = db.parse(apiFile);
     NodeList woNodes = apiDoc.getDocumentElement().getElementsByTagName("wo");
     int woNodesLength = woNodes.getLength();
     for (int i = 0; i < woNodesLength; i++) {
     System.out.println("WODCompletionProcessor.fillInAssociationNameCompletionProposals: " + woNodes.item(i).getNodeName());
     }
     }
     }
     }
     else if (typeContainer instanceof ICompilationUnit) {
     ICompilationUnit cu = (ICompilationUnit) typeContainer;
     }
     }
     catch (Throwable t) {
     // It's not that big a deal ... give up on api files
     t.printStackTrace();
     }
     */
  }

  protected void fillInBindingValueCompletionProposals(IJavaProject _project, IType _associatedType, String _token, int _tokenOffset, int _offset, Set _completionProposalsSet) throws JavaModelException {
    // Split association values on '.'
    String partialToken = partialToken(_token, _tokenOffset, _offset).toLowerCase();
    String[] accessors = partialToken.split("\\.");
    // Split tosses empty tokens, so we check to see if we're on the last "." and fake an empty token in the list
    if (partialToken.length() > 0 && partialToken.charAt(partialToken.length() - 1) == '.') {
      String[] addedBlankAccessor = new String[accessors.length + 1];
      System.arraycopy(accessors, 0, addedBlankAccessor, 0, accessors.length);
      addedBlankAccessor[addedBlankAccessor.length - 1] = "";
      accessors = addedBlankAccessor;
    }

    // Walk through the accessor keypath until we get to the end
    IType nextType = _associatedType;
    int offset = _tokenOffset;
    for (int i = 0; nextType != null && i < accessors.length - 1; i++) {
      String nextTypeName = nextType(nextType, accessors[i]);
      if (nextTypeName != null) {
        nextTypeName = Signature.toString(nextTypeName);
        ITypeHierarchy typeHierarchy = nextType.newSupertypeHierarchy(null);
        IType[] types = typeHierarchy.getAllTypes();
        IType nextTypeAttempt = null;
        for (int typeNum = 0; nextTypeAttempt == null && typeNum < types.length; typeNum++) {
          String[][] resolvedTypes = types[typeNum].resolveType(nextTypeName);
          //System.out.println("WODCompletionProcessor.fillInAssociationValueCompletionProposals: " + nextTypeName + ", " + resolvedTypes);
          if (resolvedTypes != null && resolvedTypes.length == 1) {
            String nextTypeNameTemp = Signature.toQualifiedName(resolvedTypes[0]);
            nextTypeAttempt = _project.findType(nextTypeNameTemp);
          }
        }
        nextType = nextTypeAttempt;
      }
      else {
        nextType = null;
      }
    }

    if (nextType != null) {
      // Jump forward to the last '.' and look for valid "get" method completion
      // proposals based on the partial token
      int previousTokenLength = partialToken.lastIndexOf('.') + 1;
      _tokenOffset += previousTokenLength;

      String accessor = accessors[accessors.length - 1];
      _token = accessors[accessors.length - 1];
      ITypeHierarchy typeHierarchy = nextType.newSupertypeHierarchy(null);
      IType[] types = typeHierarchy.getAllTypes();
      for (int typeNum = 0; typeNum < types.length; typeNum++) {
        IField[] fields = types[typeNum].getFields();
        for (int fieldNum = 0; fieldNum < fields.length; fieldNum++) {
          findMemberProposals(fields[fieldNum], accessor, WodCompletionProcessor.FIELD_PREFIXES, _token, _tokenOffset, _offset, _completionProposalsSet, 0, true, false);
        }

        IMethod[] methods = types[typeNum].getMethods();
        for (int methodsNum = 0; methodsNum < methods.length; methodsNum++) {
          findMemberProposals(methods[methodsNum], accessor, WodCompletionProcessor.GET_METHOD_PREFIXES, _token, _tokenOffset, _offset, _completionProposalsSet, 0, true, false);
        }
      }
    }
  }

  protected String nextType(IType _currentType, String _accessor) throws JavaModelException {
    // NTS: This looks pretty damn similar to the code right above ... these should collaapse together
    //System.out.println("WODCompletionProcessor.nextType: " + _currentType.getElementName() + ", accessor = " + _accessor);
    String partialToken = _accessor.toLowerCase();
    String nextTypeName = null;
    ITypeHierarchy typeHierarchy = _currentType.newSupertypeHierarchy(null);
    IType[] types = typeHierarchy.getAllTypes();
    for (int typeNum = 0; nextTypeName == null && typeNum < types.length; typeNum++) {
      IField[] fields = types[typeNum].getFields();
      for (int fieldNum = 0; nextTypeName == null && fieldNum < fields.length; fieldNum++) {
        nextTypeName = findMemberProposals(fields[fieldNum], partialToken, WodCompletionProcessor.FIELD_PREFIXES, "", 0, 0, new HashSet(), 0, true, true);
        //System.out.println("WODCompletionProcessor.nextType: field " + fields[fieldNum].getElementName() + "=>" + nextTypeName);
      }

      IMethod[] methods = types[typeNum].getMethods();
      for (int methodNum = 0; nextTypeName == null && methodNum < methods.length; methodNum++) {
        nextTypeName = findMemberProposals(methods[methodNum], partialToken, WodCompletionProcessor.GET_METHOD_PREFIXES, "", 0, 0, new HashSet(), 0, true, true);
        //System.out.println("WODCompletionProcessor.nextType: method " + methods[methodNum].getElementName() + "=>" + nextTypeName);
      }
    }
    return nextTypeName;
  }

  protected String findMemberProposals(IMember _member, String _partialToken, String[] _prefixes, String _token, int _tokenOffset, int _offset, Set _completionProposals, int _requiredParameterCount, boolean _returnValueRequired, boolean _requireExactNameMatch) throws JavaModelException {
    String nextType = null;
    int flags = _member.getFlags();
    // Look for non-static, non-private members ...
    if (!Flags.isStatic(flags) && !Flags.isPrivate(flags)) {
      boolean memberMatches = false;
      if (_member instanceof IMethod) {
        IMethod method = (IMethod) _member;
        // make sure the requested parameter count matches (get = 0, set = 1)
        if (method.getParameterNames().length == _requiredParameterCount) {
          nextType = method.getReturnType();
          // for get methods, we require a return type that is not void
          if (_returnValueRequired && nextType != null && !nextType.equals("V")) {
            memberMatches = true;
          }
          // for set methods, we require no return type or a void return type
          else if (!_returnValueRequired && (nextType == null || nextType.equals("V"))) {
            memberMatches = true;
          }
          else {
            nextType = null;
          }
        }
      }
      else {
        nextType = ((IField) _member).getTypeSignature();
        memberMatches = true;
      }

      if (memberMatches) {
        String elementName = _member.getElementName();
        String lowercaseElementName = elementName.toLowerCase();
        //System.out.println("WODCompletionProcessor.findMemberProposals: '" + _partialToken + "'=>" + _member.getElementName());

        // Run through our list of valid prefixes and look for a match (i.e. whatever, _whatever, _getWhatever, etc).
        // If we find a match, then turn it into wod-style naming -- lowercase first letter, dropping the prefix
        String proposalElementName = null;
        for (int prefixNum = 0; proposalElementName == null && prefixNum < _prefixes.length; prefixNum++) {
          if (lowercaseElementName.startsWith(_prefixes[prefixNum])) {
            int prefixLength = _prefixes[prefixNum].length();
            String noPrefixElementName = lowercaseElementName.substring(prefixLength);
            if ((_requireExactNameMatch && noPrefixElementName.equals(_partialToken)) || (!_requireExactNameMatch && noPrefixElementName.startsWith(_partialToken))) {
              proposalElementName = elementName.substring(prefixLength);
              if (proposalElementName.length() > 0) {
                char firstChar = proposalElementName.charAt(0);
                if (Character.isUpperCase(firstChar)) {
                  proposalElementName = Character.toLowerCase(firstChar) + proposalElementName.substring(1);
                }
              }
            }
          }
        }
        if (proposalElementName == null && ((_requireExactNameMatch && lowercaseElementName.equals(_partialToken)) || (!_requireExactNameMatch && lowercaseElementName.startsWith(_partialToken)))) {
          proposalElementName = elementName;
        }

        if (proposalElementName != null) {
          WodCompletionProposal completionProposal = new WodCompletionProposal(_token, _tokenOffset, _offset, proposalElementName);
          //_completionProposalsList.add(completionProposal);
          _completionProposals.add(completionProposal);
        }
        else {
          nextType = null;
        }
      }
    }
    return nextType;
  }

  protected IType findElementType(IJavaProject _project, IDocument _document, WodScanner _scanner, int _offset) throws BadLocationException, JavaModelException {
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
        type = findElementType(_project, elementTypeName, false);
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

  protected IType findElementType(IJavaProject _javaProject, String _elementTypeName, boolean _requireTypeInProject) throws JavaModelException {
    // Search the current project for the given element type name
    IType type;
    TypeNameCollector typeNameCollector = new TypeNameCollector(_javaProject, _requireTypeInProject);
    findMatchingElementClassNames(_elementTypeName, SearchPattern.R_EXACT_MATCH, typeNameCollector);
    if (typeNameCollector.isExactMatch()) {
      String matchingElementClassName = typeNameCollector.firstTypeName();
      type = typeNameCollector.getTypeForClassName(matchingElementClassName);
    }
    else {
      // there was more than one matching class!  crap!
      type = null;
    }
    return type;
  }

  protected void findMatchingElementClassNames(String _elementTypeName, int _matchType, TypeNameCollector _typeNameCollector) throws JavaModelException {
    SearchEngine searchEngine = new SearchEngine();
    IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
    searchEngine.searchAllTypeNames(null, _elementTypeName.toCharArray(), _matchType /*| SearchPattern.R_CASE_SENSITIVE*/, IJavaSearchConstants.TYPE, searchScope, _typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
  }
}
