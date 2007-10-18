package org.objectstyle.wolips.eomodeler.core.model;

import java.util.HashSet;
import java.util.Set;

public class EOStoredProcedureFetchSpecReferenceFailure extends EOModelReferenceFailure<EOFetchSpecification, EOStoredProcedure> {
	public EOStoredProcedureFetchSpecReferenceFailure(EOFetchSpecification fetchSpec, EOStoredProcedure storedProcedure) {
		super(fetchSpec, storedProcedure, fetchSpec.getName() + " uses " + storedProcedure.getName() + " as its stored procedure.", false);
	}
	
	public Set<EOModelObject> getRecommendedDeletions() {
		Set<EOModelObject> recommendedDeletions = new HashSet<EOModelObject>();
		recommendedDeletions.add(getReferencingObject());
		return recommendedDeletions;
	}

}
