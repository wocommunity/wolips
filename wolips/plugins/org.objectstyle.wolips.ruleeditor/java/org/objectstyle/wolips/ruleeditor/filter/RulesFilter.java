package org.objectstyle.wolips.ruleeditor.filter;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

import org.eclipse.jface.viewers.*;
import org.objectstyle.wolips.ruleeditor.model.*;

/**
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 */
public class RulesFilter extends ViewerFilter {
	private static boolean containsAllIgnoringCase(String text, String... keywords) {
		for (String keyword : keywords) {
			if (!containsIgnoreCase(text, keyword)) {
				return false;
			}
		}

		return true;
	}

	private final String regex;

	public RulesFilter(final String regex) {
		this.regex = regex;
	}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		Rule rule = (Rule) element;

		StringBuilder ruleText = new StringBuilder();
		LeftHandSide lhs = rule.getLeftHandSide();
		RightHandSide rhs = rule.getRightHandSide();
		
		if (lhs != null) {
			ruleText.append(lhs.toString());
		}
		
		if (rhs != null) {
			if (rhs.getKeyPath() != null) {
				ruleText.append(rhs.getKeyPath().toString());
			}
			
			if (rhs.getValue() != null) {
				ruleText.append(rhs.getValue());
			}
		}
		
		return containsAllIgnoringCase(ruleText.toString(), regex.split(" "));
	}
}
