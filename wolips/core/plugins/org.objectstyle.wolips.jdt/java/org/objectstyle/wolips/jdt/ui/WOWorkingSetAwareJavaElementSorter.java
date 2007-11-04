package org.objectstyle.wolips.jdt.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkingSet;

public class WOWorkingSetAwareJavaElementSorter extends WOJavaElementComparator {

	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IWorkingSet || e2 instanceof IWorkingSet)
			return 0;

		return super.compare(viewer, e1, e2);
	}
}
