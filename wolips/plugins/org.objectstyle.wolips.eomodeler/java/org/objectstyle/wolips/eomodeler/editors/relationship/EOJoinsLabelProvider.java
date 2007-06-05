package org.objectstyle.wolips.eomodeler.editors.relationship;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOJoinsLabelProvider extends TablePropertyLabelProvider {
	public EOJoinsLabelProvider(String tableName) {
		super(tableName);
	}

	public String getColumnText(Object _element, String _property) {
		String text = super.getColumnText(_element, _property);
		if (text == null && (EOJoin.SOURCE_ATTRIBUTE_NAME.equals(_property) || EOJoin.DESTINATION_ATTRIBUTE_NAME.equals(_property))) {
			text = Messages.getString("EOJoin.selectAttribute");
		}
		return text;
	}
}
