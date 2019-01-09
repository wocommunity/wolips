package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EOClassDiagramCollection extends AbstractDiagramCollection<EOModel, EOClassDiagram> {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	public static final String DISPLAYNAME = "Class Diagrams";
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	public EOClassDiagramCollection(String name) {
		super(name);
	}
	
	//---------------------------------------------------------------------------
	// ### Methods
	//---------------------------------------------------------------------------
	
	// adding a ClassDiagram
	public EOClassDiagram addBlankClassDiagram(String name) throws DuplicateNameException{
		EOClassDiagram classdiagram = new EOClassDiagram(findUnusedDiagramName(name));
		addClassDiagram(classdiagram);
		return classdiagram;
	}
	
	public void addClassDiagram(EOClassDiagram classdiagram) throws DuplicateNameException {
		addClassDiagram(classdiagram, true, null);
	}
	
	public void addClassDiagram(EOClassDiagram classdiagram, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		classdiagram._setModelParent(this);
		checkForDuplicateClassDiagramName(classdiagram, classdiagram.getName(), _failures);
		Set<EOClassDiagram> oldERDiagrams = null;
		if (_fireEvents) {
			oldERDiagrams = getDiagrams();
			Set<EOClassDiagram> newClassDiagrams = new LinkedHashSet<EOClassDiagram>();
			newClassDiagrams.addAll(getDiagrams());
			newClassDiagrams.add(classdiagram);
			setDiagrams(newClassDiagrams);
			firePropertyChange(AbstractDiagramCollection.DIAGRAMS, oldERDiagrams, newClassDiagrams);
		} else {
			getDiagrams().add(classdiagram);
		}
	}
	
	public void checkForDuplicateClassDiagramName(EOClassDiagram classdiagram, String newName, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		EOClassDiagram existingDiagram = getDiagramNamed(newName);
		if (existingDiagram != null && existingDiagram != classdiagram) {
			if (_failures == null) {
				throw new DuplicateNameException(newName, "This ClassDiagram already exists");
			}
			
			// Das hier wird nur durchgefuehrt, wenn man EOModel neu lädt und 2 erds gleich heissen.
//			String unusedName = findUnusedERDiagramName(newName);
//			existingDiagram.setName(unusedName, true); 
//			_failures.add(new DuplicateERDiagramFailure(this, newName, unusedName));
		}
	}
	
	public void removeClassDiagram(EOClassDiagram classdiagram) {
		Set<EOClassDiagram> oldClassDiagrams = getDiagrams();
		Set<EOClassDiagram> newClassDiagrams = new LinkedHashSet<EOClassDiagram>();
		newClassDiagrams.addAll(getDiagrams());
		newClassDiagrams.remove(classdiagram);
		setDiagrams(newClassDiagrams);
		firePropertyChange(AbstractDiagramCollection.DIAGRAMS, oldClassDiagrams, newClassDiagrams);
		
		// Hier wird das Diagramm auch im EOERD gelöscht (Sonst wird es im Entity.plist nicht entfernt)
		for (AbstractEOEntityDiagram eoerd : classdiagram.getDiagramEntities()) {
			eoerd.removeFromEntityPlist(classdiagram.getName());
		}
		
		classdiagram._setModelParent(null);
		
	}
	
	@SuppressWarnings("unused")
	protected void classdiagramChanged(EOClassDiagram classdiagram, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(AbstractDiagramCollection.DIAGRAM, null, classdiagram);
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Accessors & Overrides
	//---------------------------------------------------------------------------
	
	@Override
	public void loadDiagramFromEntity(EOEntity entity, List diagramList) throws EOModelException {
		List diagrams = diagramList;
		EOEntityClassDiagram entityERDiagram = new EOEntityClassDiagram(entity, diagramList, this);
		
		for (int i = 0; i < diagrams.size(); i++) {
			Object digram = diagrams.get(i);
			if (digram instanceof Map) {
				Map diagramMap = (Map)digram;
				String diagramName = (String)diagramMap.get("diagramName");
				EOClassDiagram erdiagram = getDiagramNamed(diagramName);
				if (erdiagram == null) {
					erdiagram = new EOClassDiagram(diagramName);
					addClassDiagram(erdiagram, false, null);
				}
				erdiagram.addEntityToDiagram(entityERDiagram);
			}
		}
	}

	@Override
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	@Override
	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (getModel() != null) {
			setDiagramCollectionDirty(true);
			getModel()._ClassDiagramChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return ((getModel() == null) ? "?" : getModel().getFullyQualifiedName()) + "/ClassDiagramCollection: " + getName();
	}

	@Override
	public EOModelObject<EOModel> _cloneModelObject() {
		EOClassDiagramCollection classdiagram = new EOClassDiagramCollection(getName());
		return classdiagram;
	}

	@Override
	public Class<EOModel> _getModelParentType() {
		return EOModel.class;
	}

	@Override
	public EOModel _getModelParent() {
		return getModel();
	}

}
