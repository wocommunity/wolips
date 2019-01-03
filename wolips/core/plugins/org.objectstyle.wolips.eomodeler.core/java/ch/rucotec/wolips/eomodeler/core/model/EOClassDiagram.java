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
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;

public class EOClassDiagram extends AbstractDiagram<EOClassDiagramCollection>{
	
	public EOClassDiagram(String name) {
		super(name);
	}

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
		super.setName(name, _fireEvents);
	}

	@Override
	public void addEntityToDiagram(EOEntity entity) {
		EOEntityClassDiagram entityClassDiagram = (EOEntityClassDiagram) getEntityDiagramWithEntity(entity);
		if (entityClassDiagram == null) {
			entityClassDiagram = new EOEntityClassDiagram(entity, _getModelParent());
		}
		EOEntityDiagramDimension dimension = new EOEntityDiagramDimension(100, 100, 100, 100);
		entityClassDiagram.getDiagramDimensions().put(getName(), dimension);
		super.addEntityToDiagram(entityClassDiagram);
	}

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

	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException {
		_getModelParent().removeClassDiagram(this);
	}

	@Override
	public void _addToModelParent(EOClassDiagramCollection modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedDiagramName(getName()));
		}
		modelParent.addClassDiagram(this);
	}

	@Override
	public SimpleDiagram drawDiagram() {
		SimpleDiagram myERD = new SimpleDiagram();
		HashMap<String, DiagramNode> entityNodeMap = new HashMap<String, DiagramNode>();
		
		for (AbstractEOEntityDiagram entityERD : getDiagramEntities()) {
			DiagramNode entityNode = entityERD.draw(getName());
			EOEntity entity = entityERD.getEntity();
			entityNodeMap.put(entity.getName(), entityNode);
		}
		
		for (DiagramNode node : entityNodeMap.values()) {
			for (EORelationship relationship : node.getRelationshipsList()) {
				boolean manyToManyConnection = false;
				//	List<MindMapConnection> connectionList = node.getIncomingConnections();


				if (relationship.isToMany() && getEntities().contains(relationship.getDestination())) {
					EOEntity sourceEntity = relationship.getEntity();
					EOEntity destinationEntity = relationship.getDestination();
					Iterator<EORelationship> destinationRelationshipIterator = destinationEntity.getRelationships().iterator();

					int sourceToTargetCardinality = 0;
					int targetToSourceCardinality = 0;

					// löst das Many to Many Problem und fügt die notwendigen Kardinalitäten ein.
					while (destinationRelationshipIterator.hasNext()) {
						EORelationship destinationRelationship = destinationRelationshipIterator.next();

						if (destinationRelationship.getDestination() == sourceEntity) {
							if (destinationRelationship.isToMany()) {
								manyToManyConnection = false;
								sourceToTargetCardinality = 1337;
								targetToSourceCardinality = 1337;
							} else {
								manyToManyConnection = false;
								// Hier werden die Kardinalitäten erstellt..
								sourceToTargetCardinality = 1337;
								targetToSourceCardinality = 1337;
							}

							if (!manyToManyConnection) {
								DiagramConnection conn = new DiagramConnection();
								conn.connect(node, entityNodeMap.get(relationship.getDestination().getName()));
								conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);

								myERD.addChildElement(conn);
							}
						}
					}

				}

			}
			// add nodes to mindMap
			myERD.addChildElement(node);
		}
		
		return myERD;
	}

}
