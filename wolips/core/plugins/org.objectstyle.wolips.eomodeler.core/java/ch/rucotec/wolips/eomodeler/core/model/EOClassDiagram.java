package ch.rucotec.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramType;
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
	
	@Override
	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		String name = _name;
		if (name == null || name.isEmpty()) {
			return;
		}
		if (_getModelParent() != null) {
			_getModelParent().checkForDuplicateClassDiagramName(this, name, null);
		}
		super.setName(name, _fireEvents);
	}

	/**
	 * Finds the {@link EOEntityClassDiagram} for the given entity.
	 * If there is none than one is created and is handed over to the 
	 * {@code addEntityToDiagram(AbstractEOEntityDiagram entityDiagram)}
	 * 
	 */
	@Override
	public void addEntityToDiagram(EOEntity entity) {
		EOEntityClassDiagram entityClassDiagram = (EOEntityClassDiagram) getEntityDiagramWithEntity(entity);
		if (entityClassDiagram == null) {
			entityClassDiagram = new EOEntityClassDiagram(entity, _getModelParent());
		}
		EOEntityDiagramDimension dimension = new EOEntityDiagramDimension(100, 100, 200, 100);
		entityClassDiagram.getDiagramDimensions().put(getName(), dimension);
		super.addEntityToDiagram(entityClassDiagram);
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
		
		for (AbstractEOEntityDiagram entityClassDiagram : getDiagramEntities()) {
			DiagramNode entityNode = entityClassDiagram.draw(getName());
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
				
				DiagramConnection conn = new DiagramConnection(DiagramType.CLASSDIAGRAM);
				conn.connect(node, entityNodeMap.get(nodeEntity.getParent().getName()));
				conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);

				myClassDiagram.addChildElement(conn);
				
				node.removeParentAttributes(nodeEntity.getParent().getAttributes());
			}
			
			for (EORelationship relationship : node.getRelationshipsList()) {
				boolean manyToManyConnection = false;
				//	List<MindMapConnection> connectionList = node.getIncomingConnections();


				if (relationship.isToMany() && getEntities().contains(relationship.getDestination())) {
					EOEntity sourceEntity = relationship.getEntity();
					EOEntity destinationEntity = relationship.getDestination();
					Iterator<EORelationship> destinationRelationshipIterator = destinationEntity.getRelationships().iterator();

					int sourceToTargetCardinality = 0;
					int targetToSourceCardinality = 0;
					
					if (destinationEntity.isInherited()) {
						continue;
					}

					// löst das Many to Many Problem und fügt die notwendigen Kardinalitäten ein.
					while (destinationRelationshipIterator.hasNext()) {
						EORelationship destinationRelationship = destinationRelationshipIterator.next();

						if (destinationRelationship.getDestination() == sourceEntity) {
							// Hier werden die Kardinalitäten erstellt..
							if (destinationRelationship.isToMany()) {
								manyToManyConnection = false;
								sourceToTargetCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
								targetToSourceCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
							} else {
								manyToManyConnection = false;
								sourceToTargetCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
								targetToSourceCardinality = DiagramConnection.TOONE + (destinationRelationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
							}

							if (!manyToManyConnection) {
								// TODO rekursive beziehungen werden hier einfach uebersprungen, hier sollte das irgendwie gehandelt werden.
								if (node != entityNodeMap.get(relationship.getDestination().getName())) {
									DiagramConnection conn = new DiagramConnection(DiagramType.CLASSDIAGRAM);
									conn.connect(node, entityNodeMap.get(relationship.getDestination().getName()));
									conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);
	
									myClassDiagram.addChildElement(conn);
								}
							}
						}
					}

				}

			}
			// add nodes to mindMap
			myClassDiagram.addChildElement(node);
		}
		
		return myClassDiagram;
	}
}
