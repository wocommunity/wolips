package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class HTMLTagNameRule extends WordRule implements IPredicateRule {
  private IToken _woToken;
  public HTMLTagNameRule(IToken defaultToken, IToken woToken) {
    super(new HTMLTagWordDetector(), defaultToken);
    _woToken = woToken;
  }

  public IToken getSuccessToken() {
    return fDefaultToken;
  }

  @Override
  public IToken evaluate(ICharacterScanner scanner) {
    IToken token;
    int startColumn = scanner.getColumn();
    if (startColumn >= 1) {
      boolean isTag = false;
      scanner.unread();
      int ch = scanner.read();
      if (ch == '/') {
        if (startColumn >= 2) {
          scanner.unread();
          scanner.unread();
          ch = scanner.read();
          if (ch == '<') {
            isTag = true;
          }
          scanner.read();
        }
      }
      else if (ch == '<') {
        isTag = true;
      }
      
      int possibleWoChar = scanner.read();
      boolean possibleWoTag = possibleWoChar == 'w' || possibleWoChar == 'W';
      scanner.unread();
      
      if (isTag) {
        token = super.evaluate(scanner);
        if (possibleWoTag && token == fDefaultToken) {
          int endColumn = scanner.getColumn();
          token = _woToken;
        }
      }
      else {
        token = Token.UNDEFINED;
      }
    }
    else {
      token = Token.UNDEFINED;
    }
    return token;
  }
  
  public IToken evaluate(ICharacterScanner scanner, boolean resume) {
    IToken token = evaluate(scanner);
    return token;
  }

  protected static class HTMLTagWordDetector implements IWordDetector {
    public boolean isWordPart(char c) {
      return Character.isLetterOrDigit(c) || c == ':' || c == '_';
    }
  
    public boolean isWordStart(char c) {
      return Character.isLetter(c);
    }
  }
}
