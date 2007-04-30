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
package org.objectstyle.wolips.wodclipse.editor;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionProposal;
import org.objectstyle.wolips.wodclipse.core.completion.WodCompletionUtils;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.parser.AssignmentOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.BindingNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.BindingValueRule;
import org.objectstyle.wolips.wodclipse.core.parser.CloseDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ElementNameRule;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeOperatorWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.core.parser.EndAssignmentWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.OpenDefinitionWordDetector;
import org.objectstyle.wolips.wodclipse.core.parser.OperatorRule;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.core.util.WodApiUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;

/**
 * @author mike
 */
public class WodCompletionProcessor implements IContentAssistProcessor {
	private WodEditor _editor;

	public WodCompletionProcessor(WodEditor editor) {
		_editor = editor;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { ':', '.', '=' };
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int _offset) {
		Set<WodCompletionProposal> completionProposalsSet = new TreeSet<WodCompletionProposal>();
		try {
			int offset = _offset;
			WodParserCache cache = WodParserCache.parser(((FileEditorInput) _editor.getEditorInput()).getFile());
			IDocument document = viewer.getDocument();
			IEditorInput input = _editor.getEditorInput();
			if (input instanceof IPathEditorInput) {
				IPathEditorInput pathInput = (IPathEditorInput) input;
				IPath path = pathInput.getPath();
				IFile wodFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
				IProject project = wodFile.getProject();
				IJavaProject javaProject = JavaCore.create(project);

				// Without an underlying model, we have to rescan the line to
				// figure out exactly
				// what the current matching rule was, so we know what kind of
				// token we're dealing with.
				IRegion lineRegion = document.getLineInformationOfOffset(offset);
				WodScanner scanner = WodScanner.newWODScanner();
				scanner.setRange(document, lineRegion.getOffset(), lineRegion.getLength());
				boolean foundToken = false;
				RulePosition rulePosition = null;
				while (!foundToken && (rulePosition = scanner.nextRulePosition()) != null) {
					int tokenOffset = rulePosition.getTokenOffset();
					if (offset == lineRegion.getOffset() && offset == tokenOffset) {
						foundToken = true;
					} else if (offset > tokenOffset && offset <= rulePosition.getTokenEndOffset()) {
						foundToken = true;
					}
				}

				// We can't reliably use rulePosition here because it might be
				// null ...
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				IRule rule = (rulePosition == null) ? null : rulePosition.getRule();
				// If you make a completion request in the middle of whitespace,
				// we
				// don't want to select the whitespace, so zero out the
				// whitespace token offsets.
				if (rule instanceof WhitespaceRule) {
					int partialOffset = (offset - tokenOffset);
					offset += partialOffset;
					tokenOffset += partialOffset;
					tokenLength = 0;
				} else {
					viewer.setSelectedRange(offset, tokenLength - (offset - tokenOffset));
				}
				String token = document.get(tokenOffset, tokenLength);
				String tokenType = null;
				if (foundToken && rulePosition != null) {
					if (rulePosition.isRuleOfType(ElementNameRule.class)) {
						tokenType = PreferenceConstants.ELEMENT_NAME;
					} else if (rulePosition.isRuleOfType(ElementTypeRule.class)) {
						tokenType = PreferenceConstants.ELEMENT_TYPE;
					} else if (rulePosition.isRuleOfType(BindingNameRule.class)) {
						tokenType = PreferenceConstants.BINDING_NAME;
					} else if (rulePosition.isRuleOfType(BindingValueRule.class)) {
						tokenType = PreferenceConstants.BINDING_VALUE;
					} else if (rulePosition.isRuleOfType(OperatorRule.class)) {
						tokenOffset += tokenLength;
						tokenLength = 0;
						if (RulePosition.isOperatorOfType(rulePosition, CloseDefinitionWordDetector.class)) {
							tokenType = PreferenceConstants.ELEMENT_NAME;
						} else if (RulePosition.isOperatorOfType(rulePosition, ElementTypeOperatorWordDetector.class)) {
							tokenType = PreferenceConstants.ELEMENT_TYPE;
						} else if (RulePosition.isOperatorOfType(rulePosition, OpenDefinitionWordDetector.class) || RulePosition.isOperatorOfType(rulePosition, EndAssignmentWordDetector.class)) {
							tokenType = PreferenceConstants.BINDING_NAME;
						} else if (RulePosition.isOperatorOfType(rulePosition, AssignmentOperatorWordDetector.class)) {
							tokenType = PreferenceConstants.BINDING_VALUE;
						}
					}
				}

				boolean guessed = false;
				// If there was no matching token type, then that means we're
				// in an invalid parse state and we have to "guess". So we
				// backscan
				// until we find an operator that we know about, which will tell
				// us
				// where we are in the document.
				if (tokenType == null) {
					int startOffset = tokenOffset;
					if (startOffset != 0 && startOffset == document.getLength()) {
						startOffset--;
					}
					// int hintChar = -1;
					// for (int startOffset = tokenOffset - 1; tokenType == null
					// && startOffset > 0; startOffset--) {
					for (; tokenType == null && startOffset > 0; startOffset--) {
						int ch = document.getChar(startOffset);
						if (ch == ':') {
							tokenType = PreferenceConstants.ELEMENT_TYPE;
							guessed = true;
						} else if (ch == '{' || ch == ';') {
							tokenType = PreferenceConstants.BINDING_NAME;
							guessed = true;
						} else if (ch == '=') {
							tokenType = PreferenceConstants.BINDING_VALUE;
							guessed = true;
						} else if (ch == '}') {
							tokenType = PreferenceConstants.ELEMENT_NAME;
							guessed = true;
						}
					}
				}

				if (tokenType == null) {
					tokenType = PreferenceConstants.ELEMENT_NAME;
					guessed = true;
				}

				// ... Fill in completion proposals based on the token type
				if (tokenType == PreferenceConstants.ELEMENT_NAME) {
					// We really need something like the AST ... This is a
					// pretty expensive
					// way to go here. To find element names that have already
					// been mapped, we
					// reparse the wod file. Lame.
					Set<String> alreadyUsedElementNames = WodScanner.getTextForRulesOfType(document, ElementNameRule.class);
					WodCompletionUtils.fillInElementNameCompletionProposals(alreadyUsedElementNames, token, tokenOffset, offset, completionProposalsSet, guessed, cache.getHtmlElementCache());
				} else if (tokenType == PreferenceConstants.ELEMENT_TYPE) {
					WodCompletionUtils.fillInElementTypeCompletionProposals(javaProject, token, tokenOffset, offset, completionProposalsSet, guessed);
				} else if (tokenType == PreferenceConstants.BINDING_NAME) {
					IType elementType = findNearestElementType(javaProject, document, scanner, tokenOffset, cache);
					WodCompletionUtils.fillInBindingNameCompletionProposals(javaProject, elementType, token, tokenOffset, offset, completionProposalsSet, guessed, cache);
				} else if (tokenType == PreferenceConstants.BINDING_VALUE) {
					String elementTypeName = path.removeFileExtension().lastSegment();
					IType elementType = WodReflectionUtils.findElementType(javaProject, elementTypeName, true, cache);
					boolean checkBindingValue = WodCompletionUtils.fillInBindingValueCompletionProposals(javaProject, elementType, token, tokenOffset, offset, completionProposalsSet, cache);
					if (checkBindingValue) {
						try {
							// We might (probably do) have a syntactically
							// invalid wod file
							// at this point, so we need to
							// hunt for the name of the binding that this value
							// corresponds
							// to ...
							int equalsIndex = WodCompletionProcessor.scanBackFor(document, offset, new char[] { '=' }, false);
							int noSpaceIndex = WodCompletionProcessor.scanBackFor(document, equalsIndex - 1, new char[] { ' ', '\t', '\n', '\r' }, true);
							int spaceIndex = WodCompletionProcessor.scanBackFor(document, noSpaceIndex, new char[] { ' ', '\t', '\n', '\r' }, false);
							String bindingName = document.get(spaceIndex + 1, noSpaceIndex - spaceIndex);
							elementType = findNearestElementType(javaProject, document, scanner, offset, cache);
							String[] validValues = WodApiUtils.getValidValues(javaProject, _editor.getComponentsLocateResults().getDotJavaType(), elementType, bindingName, cache);
							if (validValues != null) {
								String partialToken = WodCompletionUtils.partialToken(token, tokenOffset, offset);
								String lowercasePartialToken = partialToken.toLowerCase();
								for (int i = 0; i < validValues.length; i++) {
									if (validValues[i].toLowerCase().startsWith(lowercasePartialToken)) {
										completionProposalsSet.add(new WodCompletionProposal(token, tokenOffset, offset, validValues[i]));
									}
								}
							}
						} catch (Throwable t) {
							t.printStackTrace();
							WodclipsePlugin.getDefault().log(t);
						}
					}
				}
			}
		} catch (Exception e) {
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

	protected IType findNearestElementType(IJavaProject _project, IDocument _document, WodScanner _scanner, int _offset, WodParserCache cache) throws BadLocationException, JavaModelException {
		// Go hunting for the element type in a potentially malformed document
		// ...
		IType type;
		int colonOffset = WodCompletionProcessor.scanBackFor(_document, _offset, new char[] { ':' }, false);
		if (colonOffset != -1) {
			_scanner.setRange(_document, colonOffset, _offset);
			RulePosition elementRulePosition = _scanner.getFirstRulePositionOfType(ElementTypeRule.class);
			if (elementRulePosition != null) {
				String elementTypeName = elementRulePosition.getText();
				type = WodReflectionUtils.findElementType(_project, elementTypeName, false, cache);
			} else {
				// we didn't find a ElementTypeRule
				type = null;
			}
		} else {
			// failed colonoscopy
			type = null;
		}
		return type;
	}

	protected static int scanBackFor(IDocument _document, int _offset, char[] _lookForChars, boolean _negate) throws BadLocationException {
		int offset = _offset;
		if (offset >= _document.getLength()) {
			offset--;
		}
		int foundIndex = -1;
		for (int i = offset; foundIndex == -1 && i >= 0; i--) {
			char ch = _document.getChar(i);
			for (int lookForCharNum = 0; foundIndex == -1 && lookForCharNum < _lookForChars.length; lookForCharNum++) {
				if (ch == _lookForChars[lookForCharNum]) {
					foundIndex = i;
				}
			}
			if (_negate) {
				if (foundIndex != -1) {
					foundIndex = -1;
				} else {
					foundIndex = i;
				}
			}
		}
		return foundIndex;
	}
}
