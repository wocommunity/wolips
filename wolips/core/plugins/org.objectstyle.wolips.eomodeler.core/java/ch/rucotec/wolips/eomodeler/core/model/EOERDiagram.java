package ch.rucotec.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.Messages;
import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramType;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;

public class EOERDiagram extends AbstractDiagram<EOERDiagramCollection>{
	
	public EOERDiagram(String name) {
		super(name);
	}
	
	@Override
	public void addEntityToDiagram(EOEntity entity) {
		EOEntityERDiagram entityERDiagram = (EOEntityERDiagram) getEntityDiagramWithEntity(entity);
		if (entityERDiagram == null) {
			entityERDiagram = new EOEntityERDiagram(entity, _getModelParent());
		}
		EOEntityDiagramDimension dimension = new EOEntityDiagramDimension(100, 100, 100, 100);
		entityERDiagram.getDiagramDimensions().put(getName(), dimension);
		super.addEntityToDiagram(entityERDiagram);
	}
	
	@Override
	protected AbstractDiagram createDiagram(String name) {
		return new EOERDiagram(name);
	}

	public void setName(String _name) throws DuplicateNameException {
		setName(_name, true);
	}
	
	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		String name = _name;
		if (name == null || name.isEmpty()) {
			return;
//			name = _getModelParent().findUnusedERDiagramName(Messages.getString("AbstractDiagram.noBlankDiagramNames"));
		}
		if (_getModelParent() != null) {
			_getModelParent().checkForDuplicateERDiagramName(this, name, null);
		}
		super.setName(name, _fireEvents);
	}
	
	@Override
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	@Override
	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (_getModelParent() != null) {
			_getModelParent().erdiagramChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return ((_getModelParent() == null) ? "?" : _getModelParent().getFullyQualifiedName()) + "/erdiagram: " + getName();
	}

	@Override
	public EOERDiagram _cloneModelObject() {
		EOERDiagram erdiagram = (EOERDiagram)cloneDiagram();
		return erdiagram;
	}

	@Override
	public Class<EOERDiagramCollection> _getModelParentType() {
		return EOERDiagramCollection.class;
	}

	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException {
		_getModelParent().removeERDiagram(this);
	}

	@Override
	public void _addToModelParent(EOERDiagramCollection modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedDiagramName(getName()));
		}
		modelParent.addERDiagram(this);
	}
	
	// Drawings
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
							manyToManyConnection = true;
						} else {
							manyToManyConnection = false;
							// Hier werden die Kardinalitäten erstellt..
							sourceToTargetCardinality = DiagramConnection.TOMANY + (relationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
							targetToSourceCardinality = DiagramConnection.TOONE + (destinationRelationship.isOptional() ? DiagramConnection.OPTIONAL : 0);
						}

						if (!manyToManyConnection) {
							// TODO rekursive beziehungen werden hier einfach uebersprungen, hier sollte das irgendwie gehandelt werden.
							if (node != entityNodeMap.get(relationship.getDestination().getName())) {
								DiagramConnection conn = new DiagramConnection(DiagramType.ERDIAGRAM);
								conn.connect(node, entityNodeMap.get(relationship.getDestination().getName()));
								conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);
	
								myERD.addChildElement(conn);
							}
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
