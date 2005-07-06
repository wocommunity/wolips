package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;

public class OperatorRule extends WordPredicateRule {
  public OperatorRule(IWordDetector _detector, IToken _defaultToken) {
    super(_detector, _defaultToken);
  }

}
