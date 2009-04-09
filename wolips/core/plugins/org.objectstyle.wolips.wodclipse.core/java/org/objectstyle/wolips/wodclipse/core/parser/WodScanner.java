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
package org.objectstyle.wolips.wodclipse.core.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;

/**
 * @author mike
 */
public class WodScanner extends AbstractJavaScanner {
	private static String[] WOD_TOKENS = { PreferenceConstants.ELEMENT_NAME, PreferenceConstants.ELEMENT_TYPE, PreferenceConstants.BINDING_NAME, PreferenceConstants.BINDING_NAMESPACE, PreferenceConstants.BINDING_VALUE, PreferenceConstants.BINDING_VALUE_NAMESPACE, PreferenceConstants.OGNL_BINDING_VALUE, PreferenceConstants.CONSTANT_BINDING_VALUE, PreferenceConstants.OPERATOR, PreferenceConstants.COMMENT, PreferenceConstants.UNKNOWN };

	public static WodScanner newWODScanner() {
		IColorManager colorManager = JavaPlugin.getDefault().getJavaTextTools().getColorManager();
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		WodScanner scanner = new WodScanner(colorManager, preferenceStore);
		return scanner;
	}

	public WodScanner(IColorManager _manager, IPreferenceStore _store) {
		super(_manager, _store);
		initialize();
	}

	@Override
  protected String[] getTokenProperties() {
		return WodScanner.WOD_TOKENS;
	}

	@Override
  protected List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
    rules.add(new MultilineCommentRule(getToken(PreferenceConstants.COMMENT)));
    rules.add(new CommentRule(getToken(PreferenceConstants.COMMENT)));
    rules.add(new WOOGNLRule("\"~", "\"", getToken(PreferenceConstants.OGNL_BINDING_VALUE), '\\', true, false));
    rules.add(new WOOGNLRule("\"~", null, getToken(PreferenceConstants.OGNL_BINDING_VALUE), '\\', true, false));
		rules.add(new WOOGNLRule("~", ";", getToken(PreferenceConstants.OGNL_BINDING_VALUE), '\\'));
    rules.add(new SingleLineRule("~", null, getToken(PreferenceConstants.OGNL_BINDING_VALUE), '\\', true, false));
		rules.add(new StringLiteralRule("\"", "\"", getToken(PreferenceConstants.CONSTANT_BINDING_VALUE), '\\'));
		rules.add(new StringLiteralRule("'", "'", getToken(PreferenceConstants.CONSTANT_BINDING_VALUE), '\\'));
    rules.add(new SingleLineRule("\"", null, getToken(PreferenceConstants.CONSTANT_BINDING_VALUE), '\\', true, false));
		rules.add(new WhitespaceRule(new WodWhitespaceDetector()));
    rules.add(new OperatorRule(new ElementTypeOperatorWordDetector(), getToken(PreferenceConstants.OPERATOR)));
		rules.add(new OperatorRule(new OpenDefinitionWordDetector(), getToken(PreferenceConstants.OPERATOR)));
		rules.add(new OperatorRule(new AssignmentOperatorWordDetector(), getToken(PreferenceConstants.OPERATOR)));
		rules.add(new OperatorRule(new EndAssignmentWordDetector(), getToken(PreferenceConstants.OPERATOR)));
		rules.add(new OperatorRule(new CloseDefinitionWordDetector(), getToken(PreferenceConstants.OPERATOR)));
		rules.add(new ElementNameRule(getToken(PreferenceConstants.ELEMENT_NAME)));
		rules.add(new ElementTypeRule(getToken(PreferenceConstants.ELEMENT_TYPE)));
		rules.add(new BindingNameRule(getToken(PreferenceConstants.BINDING_NAME)));
    rules.add(new BindingValueNamespaceRule(getToken(PreferenceConstants.BINDING_VALUE_NAMESPACE)));
		String allowedBindingCharacters = org.objectstyle.wolips.bindings.Activator.getDefault().getPreferenceStore().getString(org.objectstyle.wolips.bindings.preferences.PreferenceConstants.ALLOWED_BINDING_CHARACTERS);
		rules.add(new BindingValueRule(getToken(PreferenceConstants.BINDING_VALUE), allowedBindingCharacters));
		rules.add(new WordPredicateRule(new UnknownWordDetector(), getToken(PreferenceConstants.UNKNOWN)));
		// setDefaultReturnToken(getToken("Default"));
		return rules;
	}

	@Override
  public Token getToken(String _key) {
		return super.getToken(_key);
	}
	
	public RulePosition getRulePositionAtOffset(int offset) {
		RulePosition matchingRulePosition = null;
		RulePosition rulePosition = null;
		while (matchingRulePosition == null && (rulePosition = nextRulePosition()) != null) {
			if (rulePosition.getTokenOffset() <= offset && rulePosition.getTokenEndOffset() > offset) {
				matchingRulePosition = rulePosition;
			}
		}
		return matchingRulePosition;
	}

	public RulePosition getFirstRulePositionOfType(Class<? extends IRule> _ruleType) {
		RulePosition rulePosition = null;
		while ((rulePosition == null || !rulePosition.isRuleOfType(_ruleType)) && (rulePosition = nextRulePosition()) != null) {
			// ignore
		}

		if (rulePosition == null || !rulePosition.isRuleOfType(_ruleType)) {
			rulePosition = null;
		}

		return rulePosition;
	}

	public List<RulePosition> getRulePositionsOfType(Class<? extends IRule> _ruleType) {
		List<RulePosition> rulePositions = new LinkedList<RulePosition>();
		RulePosition rulePosition = null;
		while ((rulePosition = nextRulePosition()) != null) {
			if (rulePosition.isRuleOfType(_ruleType)) {
				rulePositions.add(rulePosition);
			}
		}
		return rulePositions;
	}

	public RulePosition firstRulePositionOfTypeWithText(Class<? extends IRule> _ruleType, String _text) throws BadLocationException {
		RulePosition rulePosition = null;
		while ((rulePosition = nextRulePosition()) != null) {
			if (rulePosition.isRuleOfType(_ruleType) && _text.equals(rulePosition.getText())) {
				return rulePosition;
			}
		}
		return null;
	}

	public RulePosition nextRulePosition() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;

		IRule matchingRule = null;
		if (fRules != null) {
			for (int i = 0; matchingRule == null && i < fRules.length; i++) {
				IToken token = fRules[i].evaluate(this);
				if (!token.isUndefined()) {
					matchingRule = fRules[i];
				}
			}
		}

		RulePosition rulePosition;
		if (matchingRule != null) {
			rulePosition = new RulePosition(fDocument, matchingRule, getTokenOffset(), getTokenLength());
		} else {
			// NTS: Not sure why I do this :)
			if (read() == EOF) {
				rulePosition = null;
			} else {
				rulePosition = new RulePosition(fDocument, null, getTokenOffset(), getTokenLength());
			}
		}

		return rulePosition;
	}

	@Override
  public IToken nextToken() {
		IToken token = super.nextToken();
		return token;
	}

	public static WodScanner wodScannerForDocument(IDocument _document) {
		WodScanner scanner = WodScanner.newWODScanner();
		scanner.setRange(_document, 0, _document.getLength());
		return scanner;
	}

	public static List<RulePosition> getRulePositionsOfType(IDocument _document, Class<? extends IRule> _ruleType) {
		List<RulePosition> rulePositions = WodScanner.wodScannerForDocument(_document).getRulePositionsOfType(_ruleType);
		return rulePositions;
	}

	public static List<String> getTextForRulePositions(List<RulePosition> _rulePositions) throws BadLocationException {
		List<String> text = new LinkedList<String>();
		Iterator<RulePosition> rulePositionsIter = _rulePositions.iterator();
		while (rulePositionsIter.hasNext()) {
			RulePosition rulePosition = rulePositionsIter.next();
			text.add(rulePosition.getText());
		}
		return text;
	}

	public static Set<String> getTextForRulesOfType(IDocument _document, Class<? extends IRule> _ruleType) throws BadLocationException {
		List<RulePosition> rulePositions = WodScanner.getRulePositionsOfType(_document, _ruleType);
		List<String> textList = WodScanner.getTextForRulePositions(rulePositions);
		Set<String> textSet = new HashSet<String>(textList);
		return textSet;
	}
}
