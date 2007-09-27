package org.objectstyle.wolips.eomodeler.outline;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.ISortableEOModelObject;

public class EOModelOutlineModifier implements ICellModifier {
	public boolean canModify(Object element, String property) {
		System.out.println("EOModelOutlineModifier.canModify: " + element);
		return (element instanceof ISortableEOModelObject);
	}

	public Object getValue(Object element, String property) {
		String text;
		if (element instanceof ISortableEOModelObject) {
			text = ((ISortableEOModelObject) element).getName();
//		} else if (element instanceof EORelationshipPath) {
//			EORelationshipPath relationshipPath = (EORelationshipPath) element;
//			text = relationshipPath.getChildRelationship().getName();
//		} else if (element instanceof EOAttributePath) {
//			EOAttributePath attributePath = (EOAttributePath) element;
//			text = attributePath.getChildAttribute().getName();
		} else {
			text = null;
		}
		if (text == null) {
			text = "";
		}
		System.out.println("EOModelOutlineModifier.getValue: " + text);
		return text;
	}

	public void modify(Object element, String property, Object value) {
		if (element instanceof ISortableEOModelObject) {
			String newName = (String) value;
			try {
				((ISortableEOModelObject)element).setName(newName);
			}
			catch (DuplicateNameException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Duplicate Name", "The name '" + newName + "' is already taken.");
			}
//		} else if (element instanceof EORelationshipPath) {
//			EORelationshipPath relationshipPath = (EORelationshipPath) element;
//			text = relationshipPath.getChildRelationship().getName();
//		} else if (element instanceof EOAttributePath) {
//			EOAttributePath attributePath = (EOAttributePath) element;
//			text = attributePath.getChildAttribute().getName();
		}
	}

}
