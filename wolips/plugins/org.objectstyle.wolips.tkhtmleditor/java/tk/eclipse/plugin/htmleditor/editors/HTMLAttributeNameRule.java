package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

public class HTMLAttributeNameRule extends WordRule implements IPredicateRule {
  public HTMLAttributeNameRule(IToken defaultToken) {
    super(new HTMLAttributeWordDetector(), defaultToken);
  }

  public IToken getSuccessToken() {
    return fDefaultToken;
  }

  @Override
  public IToken evaluate(ICharacterScanner scanner) {
    IToken token = super.evaluate(scanner);
    return token;
  }
  
  public IToken evaluate(ICharacterScanner scanner, boolean resume) {
    IToken token = evaluate(scanner);
    return token;
  }

  protected static class HTMLAttributeWordDetector implements IWordDetector {
    public boolean isWordPart(char c) {
      return Character.isJavaIdentifierPart(c);
    }
  
    public boolean isWordStart(char c) {
      return Character.isJavaIdentifierStart(c);
    }
  }
}
