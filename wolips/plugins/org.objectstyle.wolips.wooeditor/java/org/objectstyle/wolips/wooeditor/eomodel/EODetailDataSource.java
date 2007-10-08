package org.objectstyle.wolips.wooeditor.eomodel;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EODetailDataSource extends EODataSource {
	private String myDetailKey;
	private String myMasterClass;

	public EODetailDataSource(final EOModelGroup modelGroup) {
		super(modelGroup);
	}

	public String getDetailKey() {
		return myDetailKey;
	}

	public void setDetailKey(final String detailKey) {
		myDetailKey = detailKey;
	}

	public String getMasterClass() {
		return myMasterClass;
	}

	public void setMasterClass(final String masterClass) {
		myMasterClass = masterClass;
	}

	@Override
	public void loadFromMap(final EOModelMap map,
			final Set<EOModelVerificationFailure> failures) {
		String className = map.getString("class", true);
		if ("EODetailDataSource".equals(className)) {
			myDetailKey = map.getString("detailKey", true);
			myMasterClass = map.getString("masterClassDescription", true);
		} else {
			throw new IllegalArgumentException("Unmatched className '"
					+ className + "' for class EODetailDataSource.");
		}
	}

	@Override
	public EOModelMap toMap() {
		EOModelMap modelMap = new EOModelMap();
		modelMap.setString("class", "EODetailDataSource", true);
		modelMap.setString("detailKey", myDetailKey, true);
		modelMap.setString("masterClassDescription", myMasterClass, true);
		return modelMap;
	}

}
