package org.objectstyle.wolips.ruleeditor.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.ruleeditor.model.D2WModel;

public class TableContentProvider implements IStructuredContentProvider {

	public void dispose() {
	}

	public Object[] getElements(final Object inputElement) {

		return ((D2WModel) inputElement).getRules().toArray();
	}

	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
	}

}
