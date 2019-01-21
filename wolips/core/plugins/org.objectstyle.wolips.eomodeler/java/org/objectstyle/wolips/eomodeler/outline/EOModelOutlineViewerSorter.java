package org.objectstyle.wolips.eomodeler.outline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagramCollection;
import ch.rucotec.wolips.eomodeler.core.model.EOClassDiagramCollection;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramCollection;

public class EOModelOutlineViewerSorter extends ViewerSorter {
	private static final int MAX_ORDER = 9;

	private Map<Class, Integer> myOrder;

	public EOModelOutlineViewerSorter() {
		myOrder = new HashMap<Class, Integer>();
		myOrder.put(EOModel.class, Integer.valueOf(1));
		myOrder.put(EOEntity.class, Integer.valueOf(10));
		myOrder.put(EOAttribute.class, Integer.valueOf(3));
		myOrder.put(EOAttributePath.class, Integer.valueOf(3));
		myOrder.put(EOArgument.class, Integer.valueOf(4));
		myOrder.put(EORelationship.class, Integer.valueOf(5));
		myOrder.put(EORelationshipPath.class, Integer.valueOf(5));
		myOrder.put(EOFetchSpecification.class, Integer.valueOf(6));
		myOrder.put(EOStoredProcedure.class, Integer.valueOf(7));
		myOrder.put(EOEntityIndex.class, Integer.valueOf(20));
		myOrder.put(EODatabaseConfig.class, Integer.valueOf(30)); 
		// SAVAS sortier priorität
		myOrder.put(EOClassDiagramCollection.class, Integer.valueOf(29));
		myOrder.put(EOERDiagramCollection.class, Integer.valueOf(29));
	}

	protected int getOrder(Object _obj) {
		int order;
		if (_obj == null) {
			order = EOModelOutlineViewerSorter.MAX_ORDER;
		} else {
			Integer orderInteger = myOrder.get(_obj.getClass());
			if (orderInteger == null) {
				order = MAX_ORDER;
			} else {
				if (_obj instanceof EOModel && ((EOModel)_obj).isEditing()) {
					order = 0;
				}
				else {
					order = orderInteger.intValue();
				}
			}
		}
		return order;
	}

	public int compare(Viewer _viewer, Object _e1, Object _e2) {
		ILabelProvider labelProvider = (ILabelProvider) ((ContentViewer) _viewer).getLabelProvider();
		int order1 = getOrder(_e1);
		int order2 = getOrder(_e2);
		String name1 = String.format("%1$02d%2$s", order1, labelProvider.getText(_e1));
		String name2 = String.format("%1$02d%2$s", order2, labelProvider.getText(_e2));
		int comparison = getComparator().compare(name1, name2);
		return comparison;
	}
}
