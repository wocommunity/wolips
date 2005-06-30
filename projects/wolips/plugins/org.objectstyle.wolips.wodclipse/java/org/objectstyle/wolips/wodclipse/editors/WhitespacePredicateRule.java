package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.Token;

/**
 * An implementation of <code>IRule</code> capable of detecting whitespace.
 * A whitespace rule uses a whitespace detector in order to find out which
 * characters are whitespace characters.
 *
 * @see IWhitespaceDetector
 */
public class WhitespacePredicateRule implements IPredicateRule {

  /** The whitespace detector used by this rule */
  protected IWhitespaceDetector fDetector;

  /**
   * Creates a rule which, with the help of an
   * whitespace detector, will return a whitespace
   * token when a whitespace is detected.
   *
   * @param detector the rule's whitespace detector, may not be <code>null</code>
   */
  public WhitespacePredicateRule(IWhitespaceDetector detector) {
    Assert.isNotNull(detector);
    fDetector = detector;
  }

  public IToken evaluate(ICharacterScanner _scanner, boolean _resume) {
    return evaluate(_scanner, false);
  }

  public IToken getSuccessToken() {
    return Token.WHITESPACE;
  }

  /*
   * @see IRule#evaluate(ICharacterScanner)
   */
  public IToken evaluate(ICharacterScanner scanner) {
    int c = scanner.read();
    if (fDetector.isWhitespace((char) c)) {
      do {
        c = scanner.read();
      } while (fDetector.isWhitespace((char) c));
      scanner.unread();
      return Token.WHITESPACE;
    }

    scanner.unread();
    return Token.UNDEFINED;
  }
}
