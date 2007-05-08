package org.objectstyle.wolips.eomodeler.editors.relationship;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOJoinsLabelProvider extends TablePropertyLabelProvider {
	public EOJoinsLabelProvider(String[] _columnProperties) {
		super(_columnProperties);
	}

	public String getColumnText(Object _element, String _property) {
		String text = super.getColumnText(_element, _property);
		if (text == null && (_property == EOJoin.SOURCE_ATTRIBUTE_NAME || _property == EOJoin.DESTINATION_ATTRIBUTE_NAME)) {
			text = Messages.getString("EOJoin.selectAttribute");
		}
		return text;
	}
}
