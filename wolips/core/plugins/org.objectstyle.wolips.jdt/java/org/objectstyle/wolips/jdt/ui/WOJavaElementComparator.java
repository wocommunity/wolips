package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jface.viewers.Viewer;

public class WOJavaElementComparator extends JavaElementComparator {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		boolean useNormalComparison = true;
		int comparison = 0;
		if (e1 instanceof IResource && e2 instanceof IResource) {
			String name1 = ((IResource) e1).getName();
			String name2 = ((IResource) e2).getName();
			if ((name1.endsWith(".wo") || name1.endsWith(".api")) && (name2.endsWith(".wo") || name2.endsWith(".api"))) {
				comparison = name1.compareTo(name2);
				useNormalComparison = false;
			}
		}
		if (useNormalComparison) {
			comparison = super.compare(viewer, e1, e2);
		}
		return comparison;
	}
}
