package org.objectstyle.wolips.wooeditor.eomodel;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public abstract class EODataSource {
	private EOModelGroup myModelGroup;


	public EODataSource(final EOModelGroup modelGroup) {
		myModelGroup = modelGroup;
	}


	public abstract void loadFromMap(EOModelMap dataSource,
			Set<EOModelVerificationFailure> failures);


	public EOModelGroup getModelGroup() {
		return myModelGroup;
	}


	public void setmodelGroup(final EOModelGroup modelGroup) {
		myModelGroup = modelGroup;
	}

	public abstract EOModelMap toMap();

}
