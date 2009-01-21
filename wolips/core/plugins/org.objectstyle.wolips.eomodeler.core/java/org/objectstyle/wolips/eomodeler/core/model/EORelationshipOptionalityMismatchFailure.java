package org.objectstyle.wolips.eomodeler.core.model;

public class EORelationshipOptionalityMismatchFailure extends EOModelVerificationFailure {
	public EORelationshipOptionalityMismatchFailure(EOModel model, EORelationship failedObject, String message, boolean warning) {
		super(model, failedObject, message, warning);
	}

	public EORelationshipOptionalityMismatchFailure(EOModel model, EORelationship failedObject, String message, boolean warning, Throwable rootCause) {
		super(model, failedObject, message, warning, rootCause);
	}

}
