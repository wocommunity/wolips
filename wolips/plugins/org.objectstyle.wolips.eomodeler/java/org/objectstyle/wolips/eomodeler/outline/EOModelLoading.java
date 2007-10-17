package org.objectstyle.wolips.eomodeler.outline;

import org.objectstyle.wolips.eomodeler.core.model.EOModel;

public class EOModelLoading {
	private EOModel _model;

	public EOModelLoading(EOModel model) {
		_model = model;
	}

	public EOModel getModel() {
		return _model;
	}
	
	public void setModel(EOModel model) {
		_model = model;
	}
}
