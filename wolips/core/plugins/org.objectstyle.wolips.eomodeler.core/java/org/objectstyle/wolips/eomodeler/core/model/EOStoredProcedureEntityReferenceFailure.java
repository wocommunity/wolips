package org.objectstyle.wolips.eomodeler.core.model;

import java.util.HashSet;
import java.util.Set;

public class EOStoredProcedureEntityReferenceFailure extends EOModelReferenceFailure<EOEntity, EOStoredProcedure> {
	private String propertyName;

	public EOStoredProcedureEntityReferenceFailure(EOEntity entity, EOStoredProcedure storedProcedure, String propertyName) {
		super(entity, storedProcedure, entity.getFullyQualifiedName() + " references " + storedProcedure.getFullyQualifiedName() + " in its " + propertyName + " property.", false);
	}

	public String getPropertyName() {
		return propertyName;
	}
	
	public Set<EOModelObject> getRecommendedDeletions() {
		Set<EOModelObject> recommendedDeletions = new HashSet<EOModelObject>();
		return recommendedDeletions;
	}
}
