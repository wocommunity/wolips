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
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EOERDiagramCollection extends AbstractDiagramCollection<EOModel, EOERDiagram> {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	public static final String DISPLAYNAME = "Entity Relationship Diagrams";
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------

	public EOERDiagramCollection(String name) {
		super(name);
	}
	
	//---------------------------------------------------------------------------
	// ### Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Adds a blank entity relationship diagram with the given name to the {@code EOERDiagramCollection}.
	 * 
	 * @param name
	 * @return the blank {@link EOERDiagram}
	 * @throws DuplicateNameException
	 */
	// adding an ERDiagram
	public EOERDiagram addBlankERDiagram(String name) throws DuplicateNameException{
		EOERDiagram erdiagram = new EOERDiagram(findUnusedDiagramName(name));
		addERDiagram(erdiagram);
		return erdiagram;
	}
	
	public void addERDiagram(EOERDiagram erdiagram) throws DuplicateNameException {
		addERDiagram(erdiagram, true, null);
	}
	
	/**
	 * Adds the given {@link EOERDiagram} to the {@code EOERDiagramCollection}.
	 * 
	 * @param erdiagram - which will be added to the {@link EOERDiagram}.
	 * @param _fireEvents - if its should refresh after adding it.
	 * @param _failures
	 * @throws DuplicateNameException
	 */
	public void addERDiagram(EOERDiagram erdiagram, boolean _fireEvents, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		erdiagram._setModelParent(this);
		checkForDuplicateERDiagramName(erdiagram, erdiagram.getName(), _failures);
		Set<EOERDiagram> oldERDiagrams = null;
		if (_fireEvents) {
			oldERDiagrams = getDiagrams();
			Set<EOERDiagram> newERDiagrams = new LinkedHashSet<EOERDiagram>();
			newERDiagrams.addAll(getDiagrams());
			newERDiagrams.add(erdiagram);
			setDiagrams(newERDiagrams);
			firePropertyChange(AbstractDiagramCollection.DIAGRAMS, oldERDiagrams, newERDiagrams);
		} else {
			getDiagrams().add(erdiagram);
		}
	}
	
	/**
	 * Checks whether or not the entity relationship diagram name is already taken, if so then a 
	 * {@code DuplicateNameException} is thrown.
	 * 
	 * @param erdiagram
	 * @param newName
	 * @param _failures
	 * @throws DuplicateNameException
	 */
	public void checkForDuplicateERDiagramName(EOERDiagram erdiagram, String newName, Set<EOModelVerificationFailure> _failures) throws DuplicateNameException {
		EOERDiagram existingERDiagram = getDiagramNamed(newName);
		if (existingERDiagram != null && existingERDiagram != erdiagram) {
			if (_failures == null) {
				throw new DuplicateNameException(newName, "This ERDiagram already exists");
			}
			
			// Das hier wird nur durchgefuehrt, wenn man EOModel neu lädt und 2 erds gleich heissen.
			String unusedName = findUnusedDiagramName(newName);
			existingERDiagram.setName(unusedName, true); 
			_failures.add(new DuplicateERDiagramFailure(this, newName, unusedName));
		}
	}
	
	/**
	 * Removes the entity relationship diagram from the {@code EOERDiagramCollection} and clears
	 * its data from every Entity.plist.
	 * 
	 * @param erdiagram
	 */
	public void removeERDiagram(EOERDiagram erdiagram) {
		Set<EOERDiagram> oldERDiagrams = getDiagrams();
		Set<EOERDiagram> newERDiagrams = new LinkedHashSet<EOERDiagram>();
		newERDiagrams.addAll(getDiagrams());
		newERDiagrams.remove(erdiagram);
		getMyDeletedDiagrams().add(erdiagram);
		setDiagrams(newERDiagrams);
		firePropertyChange(AbstractDiagramCollection.DIAGRAMS, oldERDiagrams, newERDiagrams);
		
		for (AbstractEOEntityDiagram eoerd : erdiagram.getDiagramEntities()) {
			eoerd.removeFromEntityPlist(erdiagram.getName());
		}
		
		erdiagram._setModelParent(null);
		
	}
	
	@SuppressWarnings("unused")
	protected void erdiagramChanged(EOERDiagram erdiagram, String _propertyName, Object _oldValue, Object _newValue) {
		firePropertyChange(AbstractDiagramCollection.DIAGRAM, null, erdiagram);
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Accessors & Overrides
	//---------------------------------------------------------------------------
	
	@Override
	public void loadDiagramFromEntity(EOEntity entity, List diagramList) throws EOModelException {
		List diagrams = diagramList;
		EOEntityERDiagram entityERDiagram = new EOEntityERDiagram(entity, diagramList, this);
		
		for (int i = 0; i < diagrams.size(); i++) {
			Object digram = diagrams.get(i);
			if (digram instanceof Map) {
				Map diagramMap = (Map)digram;
				String diagramName = (String)diagramMap.get("diagramName");
				EOERDiagram erdiagram = getDiagramNamed(diagramName);
				if (erdiagram == null) {
					erdiagram = new EOERDiagram(diagramName);
					addERDiagram(erdiagram, false, null);
				}
				erdiagram.addEntityToDiagram(entityERDiagram);
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
			getModel()._ERDiagramChanged(this, _propertyName, _oldValue, _newValue);
		}
	}
	
	@Override
	public EOERDiagramCollection _cloneModelObject() {
		EOERDiagramCollection erdiagram = new EOERDiagramCollection(getName());
		return erdiagram;
	}

	@Override
	public String getFullyQualifiedName() {
		return ((getModel() == null) ? "?" : getModel().getFullyQualifiedName()) + "/ERDiagramCollection: " + getName();
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
