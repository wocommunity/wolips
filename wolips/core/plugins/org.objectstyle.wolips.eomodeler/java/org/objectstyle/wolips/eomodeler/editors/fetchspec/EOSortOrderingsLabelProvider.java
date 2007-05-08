package org.objectstyle.wolips.eomodeler.editors.fetchspec;

import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOSortOrderingsLabelProvider extends TablePropertyLabelProvider {
	public EOSortOrderingsLabelProvider(String[] _columnProperties) {
		super(_columnProperties);
	}

	public Image getColumnImage(Object _element, String _property) {
		EOSortOrdering sortOrdering = (EOSortOrdering) _element;
		Image image = null;
		if (_property == EOSortOrdering.ASCENDING) {
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
		if (_property == EOSortOrdering.ASCENDING) {
			// DO NOTHING
		} else if (_property == EOSortOrdering.CASE_INSENSITIVE) {
			text = yesNoText(Boolean.valueOf(sortOrdering.isCaseInsensitive()), "i", "s", true);
		} else {
			text = super.getColumnText(_element, _property);
		}
		return text;
	}
}
