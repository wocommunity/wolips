package org.objectstyle.wolips.eomodeler.editors.relationship;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

public class EOJoinsContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object _inputElement) {
		EORelationship relationship = (EORelationship) _inputElement;
		List<EOJoin> joinsList = relationship.getJoins();
		EOJoin[] joins = joinsList.toArray(new EOJoin[joinsList.size()]);
		return joins;
	}

	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
		// DO NOTHING
	}
}
