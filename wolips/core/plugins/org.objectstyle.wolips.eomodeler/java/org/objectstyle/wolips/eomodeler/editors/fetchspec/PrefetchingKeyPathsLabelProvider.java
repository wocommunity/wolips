package org.objectstyle.wolips.eomodeler.editors.fetchspec;

import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class PrefetchingKeyPathsLabelProvider extends TablePropertyLabelProvider {
	public PrefetchingKeyPathsLabelProvider(String[] _columnProperties) {
		super(_columnProperties);
	}

	public Image getColumnImage(Object _element, String _property) {
		return null;
	}

	public String getColumnText(Object _element, String _property) {
		String prefetchingRelationshipKeyPath = (String) _element;
		String text = null;
		if (EOFetchSpecification.PREFETCHING_RELATIONSHIP_KEY_PATH.equals(_property)) {
			text = prefetchingRelationshipKeyPath;
		} else {
			text = super.getColumnText(_element, _property);
		}
		return text;
	}
}
