package org.objectstyle.wolips.ruleeditor.filter;

import org.eclipse.jface.viewers.*;
import org.objectstyle.wolips.ruleeditor.model.*;

/**
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 */
public class RulesFilter extends ViewerFilter {

	private final String regex;

	public RulesFilter(final String regex) {
		this.regex = regex;
	}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

		Rule rule = (Rule) element;

		LeftHandSide lhs = rule.getLeftHandSide();

		if (lhs != null && lhs.toString().contains(regex)) {
			return true;
		}

		RightHandSide rhs = rule.getRightHandSide();

		if (rhs != null && (rhs.getKeyPath().contains(regex) || (rhs.getValue() != null && rhs.getValue().contains(regex)))) {
			return true;
		}

		return false;
	}

}
