package org.objectstyle.wolips.eomodeler.outline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.objectstyle.wolips.eomodeler.model.EOArgument;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;

public class EOModelOutlineViewerSorter extends ViewerSorter {
	private static final int MAX_ORDER = 9;

	private Map<Class, Integer> myOrder;

	public EOModelOutlineViewerSorter() {
		myOrder = new HashMap<Class, Integer>();
		myOrder.put(EOModel.class, new Integer(0));
		myOrder.put(EOEntity.class, new Integer(1));
		myOrder.put(EOAttribute.class, new Integer(2));
		myOrder.put(EOAttributePath.class, new Integer(2));
		myOrder.put(EOArgument.class, new Integer(3));
		myOrder.put(EORelationship.class, new Integer(4));
		myOrder.put(EORelationshipPath.class, new Integer(4));
		myOrder.put(EOFetchSpecification.class, new Integer(5));
		myOrder.put(EOStoredProcedure.class, new Integer(6));
		myOrder.put(EOEntityIndex.class, new Integer(7));
		myOrder.put(EODatabaseConfig.class, new Integer(8));
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
				order = orderInteger.intValue();
			}
		}
		return order;
	}

	public int compare(Viewer _viewer, Object _e1, Object _e2) {
		ILabelProvider labelProvider = (ILabelProvider) ((ContentViewer) _viewer).getLabelProvider();
		String name1 = getOrder(_e1) + labelProvider.getText(_e1);
		String name2 = getOrder(_e2) + labelProvider.getText(_e2);
		int comparison = getComparator().compare(name1, name2);
		return comparison;
	}
}
