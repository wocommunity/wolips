package org.objectstyle.wolips.wodclipse.editors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.objectstyle.wolips.wodclipse.wodclipse.preferences.PreferenceConstants;


public class WODCompletionProcessor implements IContentAssistProcessor {
  private final static String[] PROPOSALS = { "myTag", "html", "form" };

  private IEditorPart myEditor;

  public WODCompletionProcessor(IEditorPart _editor) {
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
    return new char[] { ' ' };
  }

  public ICompletionProposal[] computeCompletionProposals(ITextViewer _viewer, int _offset) {
    List completionProposalsList = new LinkedList();

    try {
      IDocument document = _viewer.getDocument();
      IEditorInput input = myEditor.getEditorInput();
      if (input instanceof IPathEditorInput) {
        IPathEditorInput pathInput = (IPathEditorInput) input;
        IPath path = pathInput.getPath();
        IResource file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
        IJavaProject javaProject = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject(file);

        IRegion lineRegion = document.getLineInformationOfOffset(_offset);
        WODScanner scanner = WODScanner.newWODScanner();
        scanner.setRange(document, lineRegion.getOffset(), lineRegion.getLength());
        boolean foundToken = false;
        IRule matchingRule = null;
        while (!foundToken && (matchingRule = scanner.nextMatchingRule()) != null) {
          if (_offset > scanner.getTokenOffset() && _offset <= (scanner.getTokenOffset() + scanner.getTokenLength())) {
            foundToken = true;
          }
        }

        int tokenOffset = scanner.getTokenOffset();
        int tokenLength = scanner.getTokenLength();
        String token = document.get(tokenOffset, tokenLength);
        String tokenType = null;
        if (foundToken) {
          if (matchingRule instanceof ComponentNameRule) {
            tokenType = PreferenceConstants.COMPONENT_NAME;
          }
          else if (matchingRule instanceof ComponentTypeRule) {
            tokenType = PreferenceConstants.COMPONENT_TYPE;
          }
          else if (matchingRule instanceof AssociationNameRule) {
            tokenType = PreferenceConstants.ASSOCIATION_NAME;
          }
          else if (matchingRule instanceof AssociationValueRule) {
            tokenType = PreferenceConstants.ASSOCIATION_VALUE;
          }
          else {
            tokenType = null;
          }
        }

        if (tokenType == null) {
          if (tokenOffset == document.getLength()) {
            tokenOffset--;
          }
          if (tokenOffset != -1) {
            int hintChar = -1;
            for (int startOffset = tokenOffset; tokenType == null && startOffset > 0; startOffset--) {
              int ch = document.getChar(startOffset);
              if (ch == ':') {
                tokenType = PreferenceConstants.COMPONENT_TYPE;
              }
              else if (ch == '{' || ch == ';') {
                tokenType = PreferenceConstants.ASSOCIATION_NAME;
              }
              else if (ch == '=') {
                tokenType = PreferenceConstants.ASSOCIATION_VALUE;
              }
              else if (ch == '}') {
                tokenType = PreferenceConstants.COMPONENT_NAME;
              }
            }
          }
        }

        if (tokenType == null) {
          tokenType = PreferenceConstants.COMPONENT_NAME;
        }

        System.out.println("WODCompletionProcessor.computeCompletionProposals: Token = " + token + ", Token Type = " + tokenType);

        if (tokenType == PreferenceConstants.COMPONENT_NAME) {
          fillInComponentNameCompletionProposals(javaProject, path, token, tokenOffset, _offset, completionProposalsList);
        }
        else if (tokenType == PreferenceConstants.COMPONENT_TYPE) {
          fillInComponentTypeCompletionProposals(javaProject, token, tokenOffset, _offset, completionProposalsList);
        }
        else {
          IType componentType = findComponentType(javaProject, document, scanner, tokenOffset);
          System.out.println("WODCompletionProcessor.computeCompletionProposals: Type Proposal = " + componentType);
          if (tokenType == PreferenceConstants.ASSOCIATION_NAME) {
          }
          else if (tokenType == PreferenceConstants.ASSOCIATION_VALUE) {

          }
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

    ICompletionProposal[] completionProposals = new ICompletionProposal[completionProposalsList.size()];
    completionProposalsList.toArray(completionProposals);
    return completionProposals;
  }

  public String getErrorMessage() {
    return null;
  }

  protected List validComponentNames() {
    List validComponentNames = new LinkedList();
    validComponentNames.add("Component1");
    validComponentNames.add("Component2");
    validComponentNames.add("Component3");
    validComponentNames.add("TestComponent");
    validComponentNames.add("TestCamel");
    validComponentNames.add("Testaverde");
    return validComponentNames;
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

  protected void fillInComponentNameCompletionProposals(IJavaProject _project, IPath _filePath, String _token, int _tokenOffset, int _offset, List _completionProposalsList) {
    String partialToken = partialToken(_token, _tokenOffset, _offset);
    System.out.println("WODCompletionProcessor.fillInComponentNameCompletionProposals: " + partialToken);
    Iterator validComponentNamesIter = validComponentNames().iterator();
    while (validComponentNamesIter.hasNext()) {
      String validComponentName = (String) validComponentNamesIter.next();
      if (validComponentName.startsWith(partialToken)) {
        _completionProposalsList.add(completionProposal(_token, _tokenOffset, _offset, validComponentName));
      }
    }
  }

  protected void fillInComponentTypeCompletionProposals(IJavaProject _project, String _token, int _tokenOffset, int _offset, List _completionProposalsList) throws JavaModelException {
    TypeNameCollector typeNameCollector = new TypeNameCollector(_project);
    String partialToken = partialToken(_token, _tokenOffset, _offset);
    System.out.println("WODCompletionProcessor.fillInComponentTypeCompletionProposals: " + partialToken);
    findMatchingComponentClassNames(partialToken, SearchPattern.R_PREFIX_MATCH, typeNameCollector);
    Iterator matchingComponentClassNamesIter = typeNameCollector.typeNames();
    while (matchingComponentClassNamesIter.hasNext()) {
      String matchingComponentClassName = (String) matchingComponentClassNamesIter.next();
      String matchingComponentTypeName;
      int lastDotIndex = matchingComponentClassName.lastIndexOf('.');
      if (lastDotIndex == -1) {
        matchingComponentTypeName = matchingComponentClassName;
      }
      else {
        matchingComponentTypeName = matchingComponentClassName.substring(lastDotIndex + 1);
      }
      CompletionProposal completionProposal = completionProposal(_token, _tokenOffset, _offset, matchingComponentTypeName);
      _completionProposalsList.add(completionProposal);
    }
  }

  protected CompletionProposal completionProposal(String _token, int _tokenOffset, int _offset, String _proposal) {
    CompletionProposal completionProposal = new CompletionProposal(_proposal, _tokenOffset, _token.length(), _offset);
    return completionProposal;
  }

  protected IType findComponentType(IJavaProject _project, IDocument _document, WODScanner _scanner, int _offset) throws BadLocationException, JavaModelException {
    IType type;
    int colonOffset = _offset;
    for (colonOffset = _offset; colonOffset >= 0; colonOffset--) {
      char ch = _document.getChar(colonOffset);
      if (ch == ':') {
        break;
      }
    }
    if (colonOffset != -1) {
      _scanner.setRange(_document, colonOffset, _offset);
      IRule componentTypeRule = null;
      while (!(componentTypeRule instanceof ComponentTypeRule) && (componentTypeRule = _scanner.nextMatchingRule()) != null) {
      }
      if (componentTypeRule instanceof ComponentTypeRule) {
        String componentTypeName = _document.get(_scanner.getTokenOffset(), _scanner.getTokenLength());
        TypeNameCollector typeNameCollector = new TypeNameCollector(_project);
        findMatchingComponentClassNames(componentTypeName, SearchPattern.R_EXACT_MATCH, typeNameCollector);
        if (typeNameCollector.isExactMatch()) {
          String matchingComponentClassName = typeNameCollector.firstTypeName();
          type = typeNameCollector.getTypeForClassName(matchingComponentClassName);
        }
        else {
          // there was more than one matching class!  crap!
          type = null;
        }
      }
      else {
        // we didn't find a ComponentTypeRule
        type = null;
      }
    }
    else {
      // failed colonoscopy
      type = null;
    }
    return type;
  }

  protected void findMatchingComponentClassNames(String _componentTypeName, int _matchType, TypeNameCollector _typeNameCollector) throws JavaModelException {
    SearchEngine searchEngine = new SearchEngine();
    IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
    searchEngine.searchAllTypeNames(null, _componentTypeName.toCharArray(), _matchType /*| SearchPattern.R_CASE_SENSITIVE*/, IJavaSearchConstants.TYPE, searchScope, _typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
  }
}
