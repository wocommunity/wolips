package ch.rucotec.wolips.eomodeler.core.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.UserInfoableEOModelObject;

public abstract class AbstractDiagramGroup <T extends EOModelObject, U extends AbstractDiagram> extends UserInfoableEOModelObject<T>{
	
	public static final String NAME = "name";
	
	private Set<U> myDiagrams;
	private String myName;
	private EOModel myModel;
	private boolean diagramGroupDirty;
	
	public AbstractDiagramGroup() {
		myDiagrams = new LinkedHashSet<U>();
		diagramGroupDirty = true;
	}
	
	public AbstractDiagramGroup(String _name) {
		this();
		myName = _name;
	}
	
	public abstract void loadDiagramFromEntity(EOEntity entity, List diagramList) throws EOModelException;
	
	public void setModelDirty(boolean dirty) {
		myModel.setDirty(dirty);
		diagramGroupDirty = dirty;
	}
	
	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException {
		// Removing is not allowed
	}

	@Override
	public void _addToModelParent(T modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		// Adding is not allowed.	
	}
	
	@Override
	public String getName() {
		return myName;
	}
	
	public void setName(String name) {
		myName = name;
	}
	
	public void _setModel(EOModel _model) {
		myModel = _model;
	}

	public EOModel getModel() {
		return myModel;
	}

	public boolean isDiagramGroupDirty() {
		return diagramGroupDirty;
	}

	public void setDiagramGroupDirty(boolean diagramGroupDirty) {
		this.diagramGroupDirty = diagramGroupDirty;
	}

	public Set<U> getDiagrams() {
		return myDiagrams;
	}
	
	public void setDiagrams(Set<U> diagrams) {
		myDiagrams = diagrams;
	}
}
