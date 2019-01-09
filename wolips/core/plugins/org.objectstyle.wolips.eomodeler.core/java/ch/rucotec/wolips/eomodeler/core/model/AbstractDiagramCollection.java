package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.UserInfoableEOModelObject;

public abstract class AbstractDiagramCollection <T extends EOModelObject, U extends AbstractDiagram> extends UserInfoableEOModelObject<T>{
	
	public static final String NAME = "name";
	public static final String DIAGRAM = "diagram";
	public static final String DIAGRAMS = "diagrams";
	
	private Set<U> myDiagrams;
	private String myName;
	private EOModel myModel;
	private boolean diagramCollectionDirty;
	
	private AbstractDiagramCollection() {
		myDiagrams = new LinkedHashSet<U>();
		diagramCollectionDirty = true;
	}
	
	public AbstractDiagramCollection(String _name) {
		this();
		myName = _name;
	}
	
	public abstract void loadDiagramFromEntity(EOEntity entity, List diagramList) throws EOModelException;
	
	public void setModelDirty(boolean dirty) {
		myModel.setDirty(dirty);
		diagramCollectionDirty = dirty;
	}
	
	public void saveToFile(File modelFolder) throws PropertyListParserException, IOException {
		for (U diagram : myDiagrams) {
			diagram.saveToFile(modelFolder);
		}
	}
	
	public String findUnusedDiagramName(String _newName) {
		return _findUnusedName(_newName, "getDiagramNamed");
	}
	
	public U getDiagramNamed(String _name) {
		U matchingDiagram = null;
		Iterator<U> diagramIterator = myDiagrams.iterator();
		while (matchingDiagram == null && diagramIterator.hasNext()) {
			U diagram = diagramIterator.next();
			if (ComparisonUtils.equals(diagram.getName(), _name)) {
				matchingDiagram = diagram;
			}
		}
		return matchingDiagram;
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

	public boolean isDiagramCollectionDirty() {
		return diagramCollectionDirty;
	}

	public void setDiagramCollectionDirty(boolean diagramCollectionDirty) {
		this.diagramCollectionDirty = diagramCollectionDirty;
	}

	public Set<U> getDiagrams() {
		return myDiagrams;
	}
	
	public void setDiagrams(Set<U> diagrams) {
		myDiagrams = diagrams;
	}
}
