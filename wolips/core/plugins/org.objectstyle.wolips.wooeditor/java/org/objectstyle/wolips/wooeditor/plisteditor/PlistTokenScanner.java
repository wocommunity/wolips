package org.objectstyle.wolips.wooeditor.plisteditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

public class PlistTokenScanner extends RuleBasedScanner {

	public PlistTokenScanner(IColorManager colorManager) {
		IToken string =
			new Token(
				new TextAttribute(colorManager.getColor(IPlistColorConstants.STRING)));

		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		// Add a rule for single quotes
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new PlistWhitespaceDetector()));

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
