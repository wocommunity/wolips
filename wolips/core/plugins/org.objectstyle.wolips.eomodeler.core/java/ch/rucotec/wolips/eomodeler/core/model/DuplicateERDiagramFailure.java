package ch.rucotec.wolips.eomodeler.core.model;

import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class DuplicateERDiagramFailure extends EOModelVerificationFailure {
	
	private EOERDiagramGroup myERDiagramGroup;

	private String myERDiagramName;

	private String myNewERDiagramName;

	public DuplicateERDiagramFailure(EOERDiagramGroup erdiagramGroup, String erdiagramName, String newERDiagramName) {
		this(erdiagramGroup, erdiagramName, newERDiagramName, null);
	}

	public DuplicateERDiagramFailure(EOERDiagramGroup erdiagramGroup, String erdiagramName, String newERDiagramName, Throwable _throwable) {
		super(erdiagramGroup.getModel(), "There was more than one ERDiagram named '" + erdiagramName + "' in " + erdiagramGroup.getName() + ", so one was renamed to '" + newERDiagramName + "'.", false, _throwable);
		myERDiagramGroup = erdiagramGroup;
		myERDiagramName = erdiagramName;
		myNewERDiagramName = newERDiagramName;
	}
	
	@Override
	public EOModelObject getFailedObject() {
		return myERDiagramGroup.getERDiagramNamed(myERDiagramName);
	}

	public EOERDiagramGroup getERDiagramGroup() {
		return myERDiagramGroup;
	}

	public String getERDiagramName() {
		return myERDiagramName;
	}

	public String getNewERDiagramName() {
		return myNewERDiagramName;
	}
}
