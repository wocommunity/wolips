package org.objectstyle.wolips.ruleeditor.sorter;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;

public abstract class AbstractInvertableTableSorter extends InvertableSorter {
	private final InvertableSorter inverse = new InvertableSorter() {

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return (-1) * AbstractInvertableTableSorter.this.compare(viewer, e1, e2);
		}

		@Override
		public InvertableSorter getInverseSorter() {
			return AbstractInvertableTableSorter.this;
		}

		@Override
		public int getSortDirection() {
			return SWT.DOWN;
		}

	};

	@Override
	public InvertableSorter getInverseSorter() {
		return inverse;
	}

	@Override
	public int getSortDirection() {
		return SWT.UP;
	}
}
