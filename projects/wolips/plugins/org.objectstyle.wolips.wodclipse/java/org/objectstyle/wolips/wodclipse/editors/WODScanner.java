package org.objectstyle.wolips.wodclipse.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.JavaWhitespaceDetector;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.preferences.PreferenceConstants;

public class WODScanner extends AbstractJavaScanner {
  private static String[] WOD_TOKENS = { PreferenceConstants.ELEMENT_NAME, PreferenceConstants.ELEMENT_TYPE, PreferenceConstants.ASSOCIATION_NAME, PreferenceConstants.ASSOCIATION_VALUE, PreferenceConstants.CONSTANT_ASSOCIATION_VALUE, PreferenceConstants.OPERATOR, PreferenceConstants.UNKNOWN };

  public static WODScanner newWODScanner() {
    IColorManager colorManager = JavaPlugin.getDefault().getJavaTextTools().getColorManager();
    IPreferenceStore preferenceStore = WodclipsePlugin.getDefault().getPreferenceStore();
    WODScanner scanner = new WODScanner(colorManager, preferenceStore);
    return scanner;
  }

  public WODScanner(IColorManager _manager, IPreferenceStore _store) {
    super(_manager, _store);
    initialize();
  }

  protected String[] getTokenProperties() {
    return WODScanner.WOD_TOKENS;
  }

  protected List createRules() {
    List rules = new ArrayList();
    rules.add(new SingleLineRule("\"", "\"", getToken(PreferenceConstants.CONSTANT_ASSOCIATION_VALUE), '\\'));
    rules.add(new SingleLineRule("'", "'", getToken(PreferenceConstants.CONSTANT_ASSOCIATION_VALUE), '\\'));
    rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));
    rules.add(new OperatorRule(new ElementTypeOperatorWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new OpenDefinitionWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new AssignmentOperatorWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new EndAssignmentWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new OperatorRule(new CloseDefinitionWordDetector(), getToken(PreferenceConstants.OPERATOR)));
    rules.add(new ElementNameRule(getToken(PreferenceConstants.ELEMENT_NAME)));
    rules.add(new ElementTypeRule(getToken(PreferenceConstants.ELEMENT_TYPE)));
    rules.add(new AssociationNameRule(getToken(PreferenceConstants.ASSOCIATION_NAME)));
    rules.add(new AssociationValueRule(getToken(PreferenceConstants.ASSOCIATION_VALUE)));
    rules.add(new WordPredicateRule(new UnknownWordDetector(), getToken(PreferenceConstants.UNKNOWN)));
    //setDefaultReturnToken(getToken("Default"));
    return rules;
  }

  public Token getToken(String _key) {
    return super.getToken(_key);
  }

  public IRule nextMatchingRule() {
    fTokenOffset = fOffset;
    fColumn = UNDEFINED;

    if (fRules != null) {
      for (int i = 0; i < fRules.length; i++) {
        IToken token = fRules[i].evaluate(this);
        if (!token.isUndefined()) {
          return fRules[i];
        }
      }
    }

    if (read() == EOF) {
      return null;
    }

    return null;
  }

  public IToken nextToken() {
    IToken token = super.nextToken();
    /**
     try {
     System.out.println("WODScanner.nextToken: '" + fDocument.get(getTokenOffset(), getTokenLength()) + "'");
     }
     catch (Throwable t) {
     System.out.println("WODScanner.nextToken: " + t);
     }
     /**/
    return token;
  }
}
