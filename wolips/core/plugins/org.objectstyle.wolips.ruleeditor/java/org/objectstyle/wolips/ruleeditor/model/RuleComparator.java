package org.objectstyle.wolips.ruleeditor.model;

import java.util.Comparator;

public class RuleComparator implements Comparator<Rule> {

	public int compare(Rule rule1, Rule rule2) {
		return Integer.valueOf(rule1.getAuthor()).compareTo(Integer.valueOf(rule2.getAuthor()));
	}
}
