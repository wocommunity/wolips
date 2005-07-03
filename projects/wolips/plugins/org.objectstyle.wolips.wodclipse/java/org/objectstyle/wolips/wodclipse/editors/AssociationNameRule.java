package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class AssociationNameRule implements IPredicateRule {
  private IToken myToken;

  public AssociationNameRule(IToken _token) {
    myToken = _token;
  }

  public IToken getSuccessToken() {
    return myToken;
  }

  public IToken evaluate(ICharacterScanner _scanner) {
    return evaluate(_scanner, false);
  }

  public IToken evaluate(ICharacterScanner _scanner, boolean _resume) {
    int startColumn = _scanner.getColumn();
    IToken token = Token.UNDEFINED;
    int whitespaceCount = 0;
    int unreadCount = 0;
    int ch;
    while ((ch = _scanner.read()) != ICharacterScanner.EOF) {
      unreadCount++;
      if (ch == '=') {
        token = myToken;
        _scanner.unread();
        break;
      }
      else if (ch == ' ' || ch == '\t') {
        whitespaceCount++;
      }
      else if (ch == '{' || ch == '}' || ch == ';' || ch == '\n' || ch == '\r') {
        break;
      }
      else {
        whitespaceCount = 0;
      }
    }

    if (token == myToken) {
      unreadCount = whitespaceCount;
    }
    if (ch == ICharacterScanner.EOF) {
      unreadCount ++;
    }
    for (int i = 0; i < unreadCount; i++) {
      _scanner.unread();
    }

    return token;
  }
}
