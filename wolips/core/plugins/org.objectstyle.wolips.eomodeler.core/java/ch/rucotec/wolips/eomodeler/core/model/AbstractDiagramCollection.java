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

/**
 * This class is handling everything what a standard diagram collection needs.
 * 
 * <p> 
 * An AbstractDiagramCollection has many AbstractDiagrams, an AbstractDiagram has only 1
 * parent of the AbstractDiagramCollection class.
 * </p>
 * <pre>
 *  +---------------------------+
 *  | AbstractDiagramCollection |
 *  +---------------------------+
 *  	        |1
 *  	        |
 *  	        |0..*
 *  +---------------------------+
 *  |      AbstractDiagram      |
 *  +---------------------------+
 * </pre>
 * 
 * @author Savas Celik
 *
 * @param <T> - describing the parent EOModel (parent class must extend {@link EOModelObject}).
 * 
 * @param <U> - describing the children of this class (children class must extend {@link AbstractDiagram}).
 * 	            <p>For example:</p>
 * 				<pre><code>AbstractDiagramCollection<{@link EOModel}, {@link EOClassDiagram}></code></pre>
 */
public abstract class AbstractDiagramCollection <T extends EOModelObject, U extends AbstractDiagram> extends UserInfoableEOModelObject<T>{
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	public static final String NAME = "name";
	public static final String DIAGRAM = "diagram";
	public static final String DIAGRAMS = "diagrams";
	
	private Set<U> myDiagrams;
	private Set<U> myDeletedDiagrams;
	private String myName;
	private EOModel myModel;
	private boolean diagramCollectionDirty;
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * Used to initialize the needed variables and give the diagram collection its name.
	 * 
	 * @param _name
	 */
	public AbstractDiagramCollection(String _name) {
		myName = _name;
		myDiagrams = new LinkedHashSet<U>();
		myDeletedDiagrams = new LinkedHashSet<U>();
		diagramCollectionDirty = true;
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	/**
	 * Loads all the diagrams for the given Entity.
	 * 
	 * @param entity
	 * @param diagramList - this is the List which describes all the diagram names and the coordinates for the given entity.
	 * @throws EOModelException
	 */
	public abstract void loadDiagramFromEntity(EOEntity entity, List diagramList) throws EOModelException;
	
	/**
	 * Sets the EOModel and its self to dirty.
	 * 
	 * @param dirty
	 */
	public void setModelDirty(boolean dirty) {
		myModel.setDirty(dirty);
		diagramCollectionDirty = dirty;
	}
	
	/**
	 * Calls saveToFile() method in the {@link AbstractDiagram} for every Diagram in the Collection.
	 * 
	 * @param modelFolder
	 * @throws PropertyListParserException
	 * @throws IOException
	 */
	public void saveToFile(File modelFolder) throws PropertyListParserException, IOException {
		for (U diagram : myDiagrams) {
			diagram.saveToFile(modelFolder);
		}
		for (U diagram : myDeletedDiagrams) {
			diagram.saveToFile(modelFolder);
		}
	}
	
	/**
	 * Finds an unused name for the diagram and returns the name as a String.
	 * 
	 * @param _newName
	 * @return a String with an unused name.
	 */
	public String findUnusedDiagramName(String _newName) {
		return _findUnusedName(_newName, "getDiagramNamed");
	}
	
	/**
	 * Checks if there is a matching diagram name with the given parameter.
	 * 
	 * @param _name
	 * @return the found {@link U (extends {@link AbstractDiagram}) or null if nothing is found.
	 */
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
	
	/**
	 * this method is triggered by deleting the collection, at the moment it does nothing.
	 * 
	 */
	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException {
		// Removing is not allowed
	}

	@Override
	public void _addToModelParent(T modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		// Adding is not allowed.	
	}
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
	
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
	
	public Set<U> getMyDeletedDiagrams() {
		return myDeletedDiagrams;
	}
}
