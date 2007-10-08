package org.objectstyle.wolips.wooeditor.eomodel;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EODatabaseDataSource extends EODataSource {

	private String myEditingContext;

	private EOFetchSpecification myFetchSpecification;

	public EODatabaseDataSource(final EOModelGroup modelGroup) {
		super(modelGroup);
	}

	@Override
	public void loadFromMap(final EOModelMap map,
			final Set<EOModelVerificationFailure> failures) {
		EOModelMap fspecMap = new EOModelMap(map.getMap("fetchSpecification"));
		String fspecName = map.getString("fetchSpecificationName", true);
		String entityName = fspecMap.getString("entityName", true);
		if (fspecName == null) {
			myFetchSpecification = new EOFetchSpecification(null);
			myFetchSpecification.loadFromMap(fspecMap, failures);
			myFetchSpecification.setEntity(getModelGroup().getEntityNamed(
					entityName));
		} else {
			myFetchSpecification = getModelGroup().getEntityNamed(entityName)
					.getFetchSpecNamed(fspecName);
		}

		myEditingContext = map.getString("editingContext", true);
	}

	public String getEditingContext() {
		return myEditingContext;
	}

	public void setEditingContext(final String editingContext) {
		myEditingContext = editingContext;
	}

	public EOFetchSpecification getFetchSpecification() {
		if (myFetchSpecification == null) {
			myFetchSpecification = new EOFetchSpecification(null);
		}
		return myFetchSpecification;
	}

	public void setFetchSpecification(
			final EOFetchSpecification fetchSpecification) {
		myFetchSpecification = fetchSpecification;
	}

	public String getEntityName() {
		if (myFetchSpecification == null
				|| myFetchSpecification.getEntity() == null) {
			return null;
		}
		return myFetchSpecification.getEntity().getName();
	}

	@Override
	public EOModelMap toMap() {
		EOModelMap modelMap = new EOModelMap();
		modelMap.setString("class", "EODatabaseDataSource", true);
		modelMap.setString("editingContext", myEditingContext, true);
		if (myFetchSpecification != null) {
			modelMap.setMap("fetchSpecification", myFetchSpecification.toMap(),
					true);
			String fetchSpecName = myFetchSpecification.getName();
			if (fetchSpecName != null) {
				modelMap.setString("fetchSpecificationName", fetchSpecName,
						true);
			}
		}
		return modelMap;
	}
}
