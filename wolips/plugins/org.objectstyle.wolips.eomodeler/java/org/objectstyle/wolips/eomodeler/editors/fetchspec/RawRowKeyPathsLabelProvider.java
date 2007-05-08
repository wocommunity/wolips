package org.objectstyle.wolips.eomodeler.editors.fetchspec;

import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class RawRowKeyPathsLabelProvider extends TablePropertyLabelProvider {
	public RawRowKeyPathsLabelProvider(String[] _columnProperties) {
		super(_columnProperties);
	}

	public Image getColumnImage(Object _element, String _property) {
		return null;
	}

	public String getColumnText(Object _element, String _property) {
		String rawRowRelationshipKeyPath = (String) _element;
		String text = null;
		if (_property == EOFetchSpecification.RAW_ROW_KEY_PATH) {
			text = rawRowRelationshipKeyPath;
		} else {
			text = super.getColumnText(_element, _property);
		}
		return text;
	}
}
