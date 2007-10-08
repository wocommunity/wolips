package org.objectstyle.wolips.wooeditor.plisteditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.*;

public class PlistPartitionScanner extends RuleBasedPartitionScanner {
	public final static String PROPERTY = "__property";
	public final static String VALUE = "__value";


	public PlistPartitionScanner() {

		IToken property = new Token(PROPERTY);
		IToken value = new Token(VALUE);

		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		rules.add(new SingleLineRule("\"", "\"", value));
		rules.add(new SingleLineRule(";", null, new Token("test")));
		rules.add(new WordPatternRule(new IWordDetector() {

			public boolean isWordPart(char c) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isWordStart(char c) {
				// TODO Auto-generated method stub
				return c == '=';
			}

		}, "=", ";", property));
		//rules.add(new SingleLineRule("=", ";", value));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
