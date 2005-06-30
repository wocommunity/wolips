package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DefinitionRule implements IPredicateRule {
  public static final Token DEFINITION_TOKEN = new Token(IWODFilePartitions.DEFINITION);

  public IToken evaluate(ICharacterScanner _scanner) {
    return evaluate(_scanner, false);
  }

  public IToken evaluate(ICharacterScanner _scanner, boolean _resume) {
    boolean inDefinition = _resume;
    IToken token = Token.UNDEFINED;
    int whitespaceCount = 0;
    int ch;
    while ((ch = _scanner.read()) != ICharacterScanner.EOF) {
      if (inDefinition) {
        if (ch == '}') {
          token = DefinitionRule.DEFINITION_TOKEN;
          break;
        }
      }
      else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
        whitespaceCount++;
      }
      else {
        if (whitespaceCount > 0) {
          token = Token.WHITESPACE;
          _scanner.unread();
          break;
        }
        whitespaceCount = 0;
        inDefinition = true;
      }
    }
    return token;
  }

  public IToken getSuccessToken() {
    return DefinitionRule.DEFINITION_TOKEN;
  }
}