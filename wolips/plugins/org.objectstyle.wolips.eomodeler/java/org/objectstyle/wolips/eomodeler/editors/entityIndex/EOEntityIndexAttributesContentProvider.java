package org.objectstyle.wolips.eomodeler.editors.entityIndex;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityIndex;

public class EOEntityIndexAttributesContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		EOEntityIndex entityIndex = (EOEntityIndex) inputElement;
		return entityIndex.getAttributes().toArray();
	}

	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// DO NOTHING
	}

}
