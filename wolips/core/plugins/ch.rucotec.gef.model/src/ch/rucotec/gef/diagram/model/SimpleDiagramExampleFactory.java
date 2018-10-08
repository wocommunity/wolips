package ch.rucotec.gef.diagram.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import javafx.scene.paint.Color;

public class SimpleDiagramExampleFactory {

	private static final double WIDTH = 150;

	public SimpleDiagram createComplexExample() {
		SimpleDiagram mindMap = new SimpleDiagram();

		DiagramNode center = new DiagramNode();
		center.setTitle("The Core Idea");
		center.setDescription("This is my Core idea");
		center.setColor(Color.GREENYELLOW);
		center.setBounds(new Rectangle(250, 50, WIDTH, 100));

		mindMap.addChildElement(center);

		DiagramNode child = null;
		for (int i = 0; i < 5; i++) {
			child = new DiagramNode();
			child.setTitle("Association #" + i);
			child.setDescription("I just realized, this is related to the core idea!");
			child.setColor(Color.ALICEBLUE);

			child.setBounds(new Rectangle(50 + (i * 200), 250, WIDTH, 100));
			mindMap.addChildElement(child);

			DiagramConnection conn = new DiagramConnection();
			conn.connect(center, child);
			mindMap.addChildElement(conn);
		}

		DiagramNode child2 = new DiagramNode();
		child2.setTitle("Association #4-2");
		child2.setDescription("I just realized, this is related to the last idea!");
		child2.setColor(Color.LIGHTGRAY);
		child2.setBounds(new Rectangle(250, 550, WIDTH, 100));
		mindMap.addChildElement(child2);

		DiagramConnection conn = new DiagramConnection();
		conn.connect(child, child2);
		mindMap.addChildElement(conn);

		return mindMap;
	}

	public SimpleDiagram createSingleNodeExample() {
		SimpleDiagram mindMap = new SimpleDiagram();

		DiagramNode center = new DiagramNode();
		List<EOAttribute> attributeList = new ArrayList<EOAttribute>();
		center.setAttributeList(attributeList);
		center.setTitle("Test Entity");
		center.setDescription("Das Hier ist ein test Entity");
		center.setColor(Color.GREENYELLOW);
		center.setBounds(new Rectangle(20, 50, WIDTH, 100));

		mindMap.addChildElement(center);

		return mindMap;
	}

	public SimpleDiagram createErd(Object model) {
		SimpleDiagram myERD = new SimpleDiagram();

		Object[] entitiesObjectArray = ((EOModel) model).getEntities().toArray();
		Iterator<EOEntity> entityIterator = ((EOModel) model).getEntities().iterator();

		DiagramNode center = null;
		HashMap<String, DiagramNode> entityNodeMap = new HashMap<String, DiagramNode>();

		while (entityIterator.hasNext()) {
			EOEntity entity = entityIterator.next();

			center = new DiagramNode();
			List<EOAttribute> attributeList = new ArrayList<EOAttribute>();
			List<EORelationship> relationshipsList = new ArrayList<EORelationship>();
			//	NotificationMap<Object, Object> userInfo = new NotificationMap<>();
			//	entity.setUserInfo(userInfo);
			relationshipsList.addAll(entity.getRelationships());

			Iterator<EOAttribute> attributeIterator = entity.getAttributes().iterator();

			while (attributeIterator.hasNext()) {
				EOAttribute attribute = attributeIterator.next();
				attributeList.add(attribute);
			}

			center.setAttributeList(attributeList);
			center.setRelationshipsList(relationshipsList);

			center.setTitle(entity.getExternalName());
			center.setDescription("Beschreibung");
			center.setColor(Color.AZURE);
			center.setBounds(new Rectangle(20, 50, WIDTH, 100));
			entityNodeMap.put(entity.getName(), center);
		}


		for (DiagramNode node : entityNodeMap.values()) {
			for (EORelationship relationship : node.getRelationshipsList()) {
				boolean manyToManyConnection = false;
				//	List<MindMapConnection> connectionList = node.getIncomingConnections();


				if (relationship.isToMany()) {
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
								DiagramConnection conn = new DiagramConnection();
								conn.connect(node, entityNodeMap.get(relationship.getDestination().getName()));

								conn.setCardinalities(sourceToTargetCardinality, targetToSourceCardinality);

								System.out.println("sourceEntity = " + sourceEntity.getName() + " is optional = " + relationship.isOptional() + "[ to Entity = " + relationship.getDestination().getName() + " ]");
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