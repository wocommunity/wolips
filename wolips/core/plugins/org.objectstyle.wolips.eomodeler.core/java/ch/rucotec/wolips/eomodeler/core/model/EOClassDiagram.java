package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.E_DiagramType;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;

/**
 * The {@code EOClassDiagram} represents a class diagram and extends {@link AbstractDiagram}
 * most methods in this class are just Overrides from its extended classes.
 * 
 * @author Savas Celik
 * @see AbstractDiagram
 */
public class EOClassDiagram extends AbstractDiagram<EOClassDiagramCollection>{
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * This constructor calls the constructor of its super and gives the name as parameter.
	 * 
	 * @param name
	 */
	public EOClassDiagram(String name) {
		super(name);
	}

	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	
	public void setName(String name) throws DuplicateNameException {
		setName(name, true);
	}
	
	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		String name = _name;
		if (name == null || name.isEmpty()) {
			return;
		}
		if (_getModelParent() != null) {
			_getModelParent().checkForDuplicateClassDiagramName(this, name, null);
		}
		String oldName = getName();
		for (EOEntityDiagram entityDiagram : getDiagramEntities()) {
			if (entityDiagram.getClassDiagramDimensions().get(oldName) != null) {
				EOEntityDiagramDimension diagramDimension = entityDiagram.getClassDiagramDimensions().get(oldName);
				entityDiagram.getClassDiagramDimensions().remove(oldName);
				entityDiagram.getClassDiagramDimensions().put(name, diagramDimension);
			}
		}

		super.setMyName(_name);
		if (_fireEvents) {
			firePropertyChange(AbstractDiagram.NAME, oldName, getName());
		}
	}

	/**
	 * Finds the {@link EOEntityClassDiagram} for the given entity.
	 * If there is none than one is created and is handed over to the 
	 * {@code addEntityToDiagram(AbstractEOEntityDiagram entityDiagram)}
	 * 
	 */
	@Override
	public void addEntityToDiagram(EOEntity entity) {
		EOModel eoModelOfMyCollection = getDiagramCollection().getModel();
		EOEntityDiagram entityClassDiagram = eoModelOfMyCollection.getEntityDiagramMap().get(entity);
		if (entityClassDiagram == null) {
			entityClassDiagram = eoModelOfMyCollection.createAndAddEntityDiagram(entity);
		}
		EOEntityDiagramDimension dimension = new EOEntityDiagramDimension(100, 100, 200, 100);
		entityClassDiagram.getClassDiagramDimensions().put(getName(), dimension);
		super.addEntityToDiagram(entityClassDiagram);
	}
	
	@Override
	public void saveToFile(File modelFolder) throws PropertyListParserException, IOException {
		super.saveToFile(modelFolder, E_DiagramType.CLASSDIAGRAM);
	}
	
	/**
	 * Creates a new object of {@code EOClassDiagram} and returns it.
	 */
	@Override
	protected AbstractDiagram createDiagram(String name) {
		return new EOClassDiagram(name);
	}

	@Override
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	@Override
	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (_getModelParent() != null) {
			_getModelParent().classdiagramChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return ((_getModelParent() == null) ? "?" : _getModelParent().getFullyQualifiedName()) + "/classdiagram: " + getName();
	}

	@Override
	public EOModelObject<EOClassDiagramCollection> _cloneModelObject() {
		EOClassDiagram erdiagram = (EOClassDiagram)cloneDiagram();
		return erdiagram;
	}

	@Override
	public Class<EOClassDiagramCollection> _getModelParentType() {
		return EOClassDiagramCollection.class;
	}

	/**
	 * Removes this from its parent.
	 */
	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException {
		_getModelParent().removeClassDiagram(this);
	}

	/**
	 * Adds this to the given modelParent.
	 */
	@Override
	public void _addToModelParent(EOClassDiagramCollection modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedDiagramName(getName()));
		}
		modelParent.addClassDiagram(this);
	}

	@Override
	public SimpleDiagram drawDiagram() {
		SimpleDiagram myClassDiagram = new SimpleDiagram();
		HashMap<String, DiagramNode> entityNodeMap = new HashMap<String, DiagramNode>();
		
		for (EOEntityDiagram entityClassDiagram : getDiagramEntities()) {
			DiagramNode entityNode = entityClassDiagram.draw(getName(), E_DiagramType.CLASSDIAGRAM);
			EOEntity entity = entityClassDiagram.getEntity();
			entityNodeMap.put(entity.getName(), entityNode);
		}
		
		for (DiagramNode node : entityNodeMap.values()) {
			EOEntity nodeEntity = node.getEntityDiagram().getEntity();
			
			// inheritance handle
			if (nodeEntity.isInherited() && entityNodeMap.get(nodeEntity.getParent().getName()) != null) {
				int sourceToTargetCardinality = 0;
				int targetToSourceCardinality = 0;
				
				sourceToTargetCardinality = DiagramConnection.EXTENDS;
				targetToSourceCardinality = DiagramConnection.NONE;
				
				DiagramConnection conn = new DiagramConnection(E_DiagramType.CLASSDIAGRAM);
				conn.connect(node, entityNodeMap.get(nodeEntity.getParent().getName()));
				conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);

				myClassDiagram.addChildElement(conn);
				
				node.removeParentAttributes(nodeEntity.getParent().getAttributes());
			}
			
			for (EORelationship relationship : node.getRelationshipsList()) {

				if (getEntities().contains(relationship.getDestination())) {
					EOEntity sourceEntity = relationship.getEntity();
					EOEntity destinationEntity = relationship.getDestination();
					Iterator<EORelationship> destinationRelationshipIterator = destinationEntity.getRelationships().iterator();

					int sourceToTargetCardinality = 0;
					int targetToSourceCardinality = 0;
					
					if (destinationEntity.isInherited()) {
						continue;
					}
					
					// Hier werden die Kardinalitäten erstellt..
					if (destinationEntity.getRelationships().isEmpty()) {
						sourceToTargetCardinality = DiagramConnection.TOONE + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
						targetToSourceCardinality = DiagramConnection.NONE;
					} else {
						while (destinationRelationshipIterator.hasNext()) {
							EORelationship destinationRelationship = destinationRelationshipIterator.next();

							if (destinationRelationship.getDestination() == sourceEntity) {
								if (relationship.isToMany()) {
									if (destinationRelationship.isToMany()) {
										sourceToTargetCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
										targetToSourceCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
									} else {
										sourceToTargetCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
										targetToSourceCardinality = DiagramConnection.TOONE + (destinationRelationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
									}
								}
							} else {
								sourceToTargetCardinality = DiagramConnection.TOONE + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
								targetToSourceCardinality = DiagramConnection.NONE;
							}
						} 
					}
					// TODO rekursive beziehungen werden hier einfach uebersprungen, hier sollte das irgendwie gehandelt werden.
					if (node != entityNodeMap.get(relationship.getDestination().getName()) && !hasConnection(entityNodeMap.get(sourceEntity.getName()), entityNodeMap.get(destinationEntity.getName()))) {
						DiagramConnection conn = new DiagramConnection(E_DiagramType.CLASSDIAGRAM);
						conn.connect(node, entityNodeMap.get(destinationEntity.getName()));
						conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);

						myClassDiagram.addChildElement(conn);
					}
				}

			}
			// add nodes to mindMap
			myClassDiagram.addChildElement(node);
		}
		
		return myClassDiagram;
	}
}
