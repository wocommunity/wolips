package org.objectstyle.wolips.ruleeditor.sorter;

import org.eclipse.jface.viewers.*;

public abstract class InvertableSorter extends ViewerSorter {
	@Override
	public abstract int compare(Viewer viewer, Object e1, Object e2);

	public abstract InvertableSorter getInverseSorter();

	public abstract int getSortDirection();
}
