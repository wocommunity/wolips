package org.objectstyle.wolips.wodclipse.core.parser;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;

public class RulePosition {
	private IDocument myDocument;

	private IRule myRule;

	private int myTokenOffset;

	private int myTokenLength;

	public RulePosition(IDocument _document, IRule _rule, int _tokenOffset, int _tokenLength) {
		myDocument = _document;
		myRule = _rule;
		myTokenOffset = _tokenOffset;
		myTokenLength = _tokenLength;
	}

	public IDocument getDocument() {
		return myDocument;
	}

	public IRule getRule() {
		return myRule;
	}

	public int getTokenLength() {
		return myTokenLength;
	}

	public int getTokenOffset() {
		return myTokenOffset;
	}

	public int getTokenEndOffset() {
		return myTokenOffset + myTokenLength;
	}

	public Position getPosition() {
		return new Position(myTokenOffset, myTokenLength);
	}

	public String getText() throws BadLocationException {
		String text = myDocument.get(myTokenOffset, myTokenLength);
		return text;
	}

	public String _getTextWithoutException() {
		String text;
		try {
			text = getText();
		} catch (Throwable e) {
			e.printStackTrace();
			text = "<<failed>>";
		}
		return text;
	}

	public boolean isRuleOfType(Class<? extends IRule> _ruleType) {
		return myRule != null && _ruleType.isAssignableFrom(myRule.getClass());
	}

	public boolean containsIndex(int _index) {
		return (myTokenOffset <= _index) && ((myTokenOffset + myTokenLength) >= _index);
	}

	@Override
  public String toString() {
		return "[RulePosition: rule = " + myRule + "; tokenOffset = " + myTokenOffset + "; tokenLength = " + myTokenLength + "; text = " + _getTextWithoutException() + "]";
	}

	public static boolean isRulePositionOfType(RulePosition _rulePosition, Class<? extends IRule> _ruleType) {
		return (_rulePosition != null && _rulePosition.isRuleOfType(_ruleType));
	}

	public static boolean isOperatorOfType(RulePosition _rulePosition, Class<? extends IWordDetector> _wordDetectorType) {
		return (_rulePosition != null && _rulePosition.getRule() instanceof OperatorRule && ((OperatorRule) _rulePosition.getRule()).isWordDetectorOfType(_wordDetectorType));
	}
}
