package org.objectstyle.wolips.wodclipse.editors;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

public class WODFilePartitionScanner extends RuleBasedPartitionScanner implements IWODFilePartitions {
  public WODFilePartitionScanner() {
    List rules = new LinkedList();
    rules.add(new DefinitionRule());
    /*
     Token constant = new Token(IWODFilePartitions.CONSTANT_ASSOCIATION_VALUE);
     Token operator = new Token(IWODFilePartitions.OPERATOR);
     Token componentName = new Token(IWODFilePartitions.COMPONENT_NAME);
     Token componentType = new Token(IWODFilePartitions.COMPONENT_TYPE);
     Token associationName = new Token(IWODFilePartitions.ASSOCIATION_NAME);
     Token associationValue = new Token(IWODFilePartitions.ASSOCIATION_VALUE);
     rules.add(new SingleLineRule("\"", "\"", constant, '\\'));
     rules.add(new SingleLineRule("'", "'", constant, '\\'));
     rules.add(new WhitespacePredicateRule(new JavaWhitespaceDetector()));
     rules.add(new WordPredicateRule(new ComponentTypeOperatorWordDetector(), operator));
     rules.add(new WordPredicateRule(new OpenDefinitionWordDetector(), operator));
     rules.add(new WordPredicateRule(new AssignmentOperatorWordDetector(), operator));
     rules.add(new WordPredicateRule(new EndAssignmentWordDetector(), operator));
     rules.add(new WordPredicateRule(new CloseDefinitionWordDetector(), operator));
     rules.add(new ComponentNameRule(componentName));
     rules.add(new ComponentTypeRule(componentType));
     rules.add(new KeyBindingNameRule(associationName));
     rules.add(new KeyBindingValueRule(associationValue));
     */
    IPredicateRule[] rulesArray = new IPredicateRule[rules.size()];
    rules.toArray(rulesArray);
    setPredicateRules(rulesArray);
  }

  public IToken nextToken() {
    IToken token = super.nextToken();
    return token;
  }
}
