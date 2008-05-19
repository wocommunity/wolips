package org.objectstyle.wolips.ruleeditor.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.ruleeditor.model.Rule;

public class TableLabelProvider implements ITableLabelProvider {

	public void addListener(final ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	public String getColumnText(final Object element, final int columnIndex) {

		switch (columnIndex) {
		case 0:

			if (((Rule) element).getLeftHandSide() == null) {
				return "*true*";
			}

			return ((Rule) element).getLeftHandSide().toString();

		case 1:

			return ((Rule) element).getRightHandSide().getKeyPath();
		case 2:

			return ((Rule) element).getRightHandSide().getValue();
		case 3:

			return ((Rule) element).getAuthor();

		default:
			break;
		}
		return null;
	}

	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	public void removeListener(final ILabelProviderListener listener) {

	}

}
