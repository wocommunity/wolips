package org.objectstyle.wolips.wooeditor.plisteditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class PlistScanner extends RuleBasedScanner {

	public PlistScanner(IColorManager colorManager) {
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new PlistWhitespaceDetector()));
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
