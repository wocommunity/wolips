package org.objectstyle.wolips.ruleeditor.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.ruleeditor.model.Rule;

public class TextSorter extends AbstractInvertableTableSorter {

	private final int columnIdx;

	public TextSorter(final int index) {
		columnIdx = index;
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		Rule rule1 = (Rule) e1;
		Rule rule2 = (Rule) e2;
		
		if (columnIdx == 0) {
			if (rule1.getLeftHandSide() == null || rule2.getLeftHandSide() == null) {
				return 0;
			}
			return rule1.getLeftHandSide().toString().compareTo(rule2.getLeftHandSide().toString());
		} else if (columnIdx == 1) {
			if (rule1.getRightHandSide().getKeyPath() == null || rule2.getRightHandSide().getKeyPath() == null) {
				return 0;
			}
			return rule1.getRightHandSide().getKeyPath().compareTo(rule2.getRightHandSide().getKeyPath());
		} else if (columnIdx == 2) {
			if (rule1.getRightHandSide().getValue() == null || rule2.getRightHandSide().getValue() == null) {
				return 0;
			}
			return rule1.getRightHandSide().getValue().compareTo(rule2.getRightHandSide().getValue());
		} else {
			if (rule1.getAuthor() == null || rule2.getAuthor() == null) {
				return 0;
			}
			return Integer.valueOf(rule1.getAuthor()).compareTo(Integer.valueOf(rule2.getAuthor()));
		}

	}

}
