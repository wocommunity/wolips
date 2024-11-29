package ch.rucotec.wolips.eomodeler.core.model;

import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class DuplicateERDiagramFailure extends EOModelVerificationFailure {
	
	private EOERDiagramCollection myERDiagramCollection;

	private String myERDiagramName;

	private String myNewERDiagramName;

	public DuplicateERDiagramFailure(EOERDiagramCollection erdiagramCollection, String erdiagramName, String newERDiagramName) {
		this(erdiagramCollection, erdiagramName, newERDiagramName, null);
	}

	public DuplicateERDiagramFailure(EOERDiagramCollection erdiagramCollection, String erdiagramName, String newERDiagramName, Throwable _throwable) {
		super(erdiagramCollection.getModel(), "There was more than one ERDiagram named '" + erdiagramName + "' in " + erdiagramCollection.getName() + ", so one was renamed to '" + newERDiagramName + "'.", false, _throwable);
		myERDiagramCollection = erdiagramCollection;
		myERDiagramName = erdiagramName;
		myNewERDiagramName = newERDiagramName;
	}
	
	@Override
	public EOModelObject getFailedObject() {
		return myERDiagramCollection.getDiagramNamed(myERDiagramName);
	}

	public EOERDiagramCollection getERDiagramCollection() {
		return myERDiagramCollection;
	}

	public String getERDiagramName() {
		return myERDiagramName;
	}

	public String getNewERDiagramName() {
		return myNewERDiagramName;
	}
}
