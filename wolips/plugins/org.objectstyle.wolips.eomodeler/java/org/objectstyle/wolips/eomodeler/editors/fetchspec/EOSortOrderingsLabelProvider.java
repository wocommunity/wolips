package org.objectstyle.wolips.eomodeler.editors.fetchspec;

import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOSortOrderingsLabelProvider extends TablePropertyLabelProvider {
	public EOSortOrderingsLabelProvider(String tableName) {
		super(tableName);
	}

	public Image getColumnImage(Object _element, String _property) {
		EOSortOrdering sortOrdering = (EOSortOrdering) _element;
		Image image = null;
		if (EOSortOrdering.ASCENDING.equals(_property)) {
			image = yesNoImage(Boolean.valueOf(sortOrdering.isAscending()), Activator.getDefault().getImageRegistry().get(Activator.ASCENDING_ICON), Activator.getDefault().getImageRegistry().get(Activator.DESCENDING_ICON), null);
		}
		return image;
	}

	protected String yesNoText(EOAttribute _attribute, Boolean _bool) {
		return yesNoText(_bool, !_attribute.getEntity().isPrototype());
	}

	public String getColumnText(Object _element, String _property) {
		EOSortOrdering sortOrdering = (EOSortOrdering) _element;
		String text = null;
		if (EOSortOrdering.ASCENDING.equals(_property)) {
			// DO NOTHING
		} else if (EOSortOrdering.CASE_INSENSITIVE.equals(_property)) {
			text = yesNoText(Boolean.valueOf(sortOrdering.isCaseInsensitive()), "i", "s", true);
		} else {
			text = super.getColumnText(_element, _property);
		}
		return text;
	}
}
