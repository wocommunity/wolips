package ch.rucotec.wolips.eomodeler.core.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

import ch.rucotec.wolips.eomodeler.core.gef.model.E_DiagramType;

/**
 * 
 * 
 * @author Savas Celik
 * @see AbstractDiagramCollection
 */
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
	
	/**
	 * Adds a blank class diagram with the given name to the {@code EOClassDiagramCollection}.
	 * 
	 * @param name
	 * @return the blank {@link EOClassDiagram}
	 * @throws DuplicateNameException
	 */
	// adding a ClassDiagram
	public EOClassDiagram addBlankClassDiagram(String name) throws DuplicateNameException{
		EOClassDiagram classdiagram = new EOClassDiagram(findUnusedDiagramName(name));
		addClassDiagram(classdiagram);
		return classdiagram;
	}
	
	public void addClassDiagram(EOClassDiagram classdiagram) throws DuplicateNameException {
		addClassDiagram(classdiagram, true, null);
	}
	
	/**
	 * Adds the given {@link EOClassDiagram} to the {@code EOClassDiagramCollection}.
	 * 
	 * @param classdiagram - which will be added to the {@link EOClassDiagram}.
	 * @param _fireEvents - if its should refresh after adding it.
	 * @param _failures
	 * @throws DuplicateNameException
	 */
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
	
	/**
	 * Checks whether or not the class diagram name is already taken, if so then a 
	 * {@code DuplicateNameException} is thrown.
	 * 
	 * @param classdiagram
	 * @param newName
	 * @param _failures
	 * @throws DuplicateNameException
	 */
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
	
	/**
	 * Removes the class diagram from the {@code EOClassDiagramCollection} and clears
	 * its data from every Entity.plist.
	 * 
	 * @param classdiagram
	 */
	public void removeClassDiagram(EOClassDiagram classdiagram) {
		Set<EOClassDiagram> oldClassDiagrams = getDiagrams();
		Set<EOClassDiagram> newClassDiagrams = new LinkedHashSet<EOClassDiagram>();
		newClassDiagrams.addAll(getDiagrams());
		newClassDiagrams.remove(classdiagram);
		getMyDeletedDiagrams().add(classdiagram);
		setDiagrams(newClassDiagrams);
		firePropertyChange(AbstractDiagramCollection.DIAGRAMS, oldClassDiagrams, newClassDiagrams);
		
		// Hier wird das Diagramm bei jedem entity wo er auftritt glöscht. (Sonst wird es im Entity.plist nicht entfernt)
		for (EOEntityDiagram eoclassdiagram : classdiagram.getDiagramEntities()) {
			eoclassdiagram.removeClassDiagramFromEntityPlist(classdiagram.getName());
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
		EOEntityDiagram entityClassDiagram = getEntityDiagramWithEntity(entity);
		
		entityClassDiagram.addDiagramDimensions(diagramList, E_DiagramType.CLASSDIAGRAM);
		
		for (int i = 0; i < diagramList.size(); i++) {
			Object digram = diagramList.get(i);
			if (digram instanceof Map) {
				Map diagramMap = (Map)digram;
				String diagramName = (String)diagramMap.get("diagramName");
				EOClassDiagram classdiagram = getDiagramNamed(diagramName);
				if (classdiagram == null) {
					classdiagram = new EOClassDiagram(diagramName);
					addClassDiagram(classdiagram, false, null);
				}
				classdiagram.addEntityToDiagram(entityClassDiagram);
			}
		}
	}

	@Override
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	/**
	 * Sets the parent and him self to dirty.
	 */
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
