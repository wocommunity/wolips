package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.ISortableEOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.UserInfoableEOModelObject;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.E_DiagramType;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;

/**
 * This class is handling everything what a standard diagram needs.
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
 * @param <T> - describing the parent Collection(group) (parent must extend {@link AbstractDiagramCollection}).
 * 	            <p>For example:</p>
 * 				<pre><code>AbstractDiagram<{@link EOClassDiagramCollection}></code></pre>
 */
public abstract class AbstractDiagram<T extends AbstractDiagramCollection> extends UserInfoableEOModelObject<T> implements ISortableEOModelObject {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	public static final String NAME = "name";
	public static final String ENTITYNAMES = "entityNames";
	public static final String DIAGRAMS = "diagrams";
	
	private Object myEOModelEditor;
	private String myName;
	private Set<EOEntity> myEntities;
	private Set<EOEntityDiagram> myDiagramEntities;
	private Set<EOEntityDiagram> myDeletedDiagramEntities;
	private T myDiagramCollection;
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * Used to initialize the needed variables and give the diagram its name.
	 * 
	 * @param name
	 */
	protected AbstractDiagram(String name) {
		myName = name;
		myDiagramEntities = new LinkedHashSet<EOEntityDiagram>();
		myDeletedDiagramEntities = new LinkedHashSet<EOEntityDiagram>();
		myEntities = new LinkedHashSet<EOEntity>();
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	public abstract void addEntityToDiagram(EOEntity entity);
	
	public void addEntityToDiagram(EOEntity parentEntity, Set<EOEntity> childrenEntites) {
		addEntityToDiagram(parentEntity);
		for (EOEntity children : childrenEntites) {
			addEntityToDiagram(children);
		}
	}
	
	/**
	 * Adds the given {@code EOEntityDiagram} ({@link EOEntityClassDiagram} or {@link EOEntityERDiagram})
	 * to the diagram.
	 * 
	 * @param entityDiagram
	 */
	public void addEntityToDiagram(EOEntityDiagram entityDiagram) {
		myDeletedDiagramEntities.remove(entityDiagram);
		myDiagramEntities.add(entityDiagram);
		myEntities.add(entityDiagram.getEntity());
	}
	
	/**
	 * Searches the EOEntityDiagram ({@link EOEntityClassDiagram} or {@link EOEntityERDiagram}) in every Diagram in the parent
	 * the parent is the DiagramCollection. If an Entity already has a EOEntityDiagram, then it returns the found EOEntityDiagram else it return null.
	 * 
	 * @param entity
	 * @return {@link EOEntityDiagram}
	 */
	public EOEntityDiagram getEntityDiagramWithEntity(EOEntity entity) {
		EOEntityDiagram foundEntityDiagram = null;
		for (AbstractDiagram diagram : (Set<AbstractDiagram>)myDiagramCollection.getDiagrams()) {
			for (EOEntityDiagram entityDiagram : (Set<EOEntityDiagram>)diagram.getDiagramEntities()) {
				if (entityDiagram.getEntity() == entity) {
					foundEntityDiagram = entityDiagram;
				}
			}
		}
		return foundEntityDiagram;
	}
	
	/**
	 * Removes the EOEntityDiagram (which is found with the given {@link EOEntity}) from the Diagram.
	 * 
	 * @param entity
	 */
	public void removeEntityFromDiagram(EOEntity entity) {
		for (EOEntityDiagram entityDiagram : myDiagramEntities) {
			if (entity == entityDiagram.getEntity()) {
				myDeletedDiagramEntities.add(entityDiagram);
				myEntities.remove(entity);
				myDiagramEntities.remove(entityDiagram);
				break;
			}
		}
	}
	
	public void removeEntityFromDiagram(EOEntity parentEntity, Set<EOEntity> childrenEntites) {
		removeEntityFromDiagram(parentEntity);
		for (EOEntity children : childrenEntites) {
			removeEntityFromDiagram(children);
		}
	}
	
	protected abstract AbstractDiagram createDiagram(String name);
	
	public abstract void saveToFile(File modelFolder) throws PropertyListParserException, IOException;
	
	/**
	 * Saves all the diagram information in the needed Entity.plist.
	 * 
	 * @param modelFolder
	 * @throws PropertyListParserException
	 * @throws IOException
	 */
	public void saveToFile(File modelFolder, E_DiagramType diagramType) throws PropertyListParserException, IOException {
		Iterator<EOEntityDiagram> deletedEntityDiagramIterator = myDeletedDiagramEntities.iterator();
		while (deletedEntityDiagramIterator.hasNext()) {
			EOEntityDiagram deletedEntityDiagram = deletedEntityDiagramIterator.next();
			deletedEntityDiagram.removeFromEntityPlist(this.getName(), diagramType);
			deletedEntityDiagram.saveToFile(modelFolder);
			myDiagramEntities.remove(deletedEntityDiagram);
		}
		
		for (EOEntityDiagram entityDiagram : myDiagramEntities) {
			entityDiagram.saveToFile(modelFolder);
		}
	}
	
	/**
	 * Clones a diagram.
	 * 
	 * @return {@link AbstractDiagram}
	 */
	protected AbstractDiagram cloneDiagram() {
		AbstractDiagram diagram = createDiagram(myName);
		cloneIntoDiagram(diagram);
		_cloneUserInfoInto(diagram);
		return diagram;
	}
	
	private void cloneIntoDiagram(AbstractDiagram diagram) {
		diagram.myDiagramEntities = myDiagramEntities; 
	}
	
	/**
	 * Sets the diagram name and fires the PropertyChanged listener.
	 * 
	 * @param name
	 * @param fireEvents
	 * @throws DuplicateNameException
	 */
//	@SuppressWarnings("unused")
//	public void setName(String name, boolean fireEvents) throws DuplicateNameException {
//		String oldName = myName;
//		for (AbstractEOEntityDiagram entityDiagram : myDiagramEntities) {
//			if (entityDiagram.getDiagramDimensions().get(oldName) != null) {
//				EOEntityDiagramDimension diagramDimension = entityDiagram.getDiagramDimensions().get(oldName);
//				entityDiagram.getDiagramDimensions().remove(oldName);
//				entityDiagram.getDiagramDimensions().put(name, diagramDimension);
//			}
//		}
//		myName = name;
//		if (fireEvents) {
//			firePropertyChange(AbstractDiagram.NAME, oldName, myName);
//		}
//	}
	
	/**
	 * Returns a String with all the entity names in the diagram.
	 * <p>
	 * This method is used to display all the entity names in one column for the TableView.
	 * The TableView knows this method due to the Constant = <code>AbstractDiagram.ENTITYNAMES</code>.
	 * </p>
	 * Which apears by clicking  on a Collection object in the EOModeller Application
	 * 
	 * @return a String with all the entity names in the entities list.
	 */
	// Diese Methode wird durch den Column ersteller des TableViews aufgerufen (weil die Konstante 
	//	AbstractDiagram.ENTITYNAMES als column mit gegeben wird ) hier wird dann der gewünschte String zurueck gegeben.
	public String getEntityNames() {
		StringBuilder sb = new StringBuilder();
		for (EOEntity entity : myEntities) {
			if (sb.length() == 0) {
				sb.append(entity.getName());
			} else {
				sb.append(", " + entity.getName());
			}
		}
		return sb.toString();
	}
	
	/**
	 * This method creates a {@link SimpleDiagram} which represents the whole diagram, this is
	 * later given to the JavaFX application which is then added to the canvas.
	 * 
	 * @return a {@link SimpleDiagram} which contains every {@link DiagramNode}s and 
	 * the {@link DiagramConnection}s
	 */
	public abstract SimpleDiagram drawDiagram();
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
	
	public Set<EOEntityDiagram> getDiagramEntities() {
		return myDiagramEntities;
	}
	
	@Override
	public T _getModelParent() {
		return myDiagramCollection;
	}
	
	public void _setModelParent(T diagramCollection) {
		myDiagramCollection = diagramCollection;
	}
	
	public T getDiagramCollection() {
		return myDiagramCollection;
	}

	@Override
	public String getName() {
		return myName;
	}
	
	public void setMyName(String name) {
		myName = name;
	}

	public Set<EOEntity> getEntities() {
		return myEntities;
	}

	/**
	 * @return the myEOModelEditor
	 */
	public Object getEOModelEditor() {
		return myEOModelEditor;
	}

	/**
	 * @param myEOModelEditor the myEOModelEditor to set
	 */
	public void setEOModelEditor(Object myEOModelEditor) {
		this.myEOModelEditor = myEOModelEditor;
	}
	
	@Override
	public String toString() {
		return "AbstractDiagram [myName=" + myName + ", myEntities=" + myEntities + ", myDiagramEntities=" + myDiagramEntities + ", myDeletedDiagramEntities=" + myDeletedDiagramEntities + ", myDiagramCollection=" + myDiagramCollection + "]";
	}
}
