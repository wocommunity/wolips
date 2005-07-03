package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

public class WordPredicateRule extends WordRule implements IPredicateRule {
  public WordPredicateRule(IWordDetector _detector) {
    super(_detector);
  }

  public WordPredicateRule(IWordDetector _detector, IToken _defaultToken) {
    super(_detector, _defaultToken);
  }

  public IToken getSuccessToken() {
    return fDefaultToken;
  }

  public IToken evaluate(ICharacterScanner _scanner, boolean _resume) {
    IToken token = evaluate(_scanner);
    return token;
  }

}
