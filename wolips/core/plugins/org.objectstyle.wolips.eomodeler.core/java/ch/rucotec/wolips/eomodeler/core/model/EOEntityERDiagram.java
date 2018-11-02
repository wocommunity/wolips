package ch.rucotec.wolips.eomodeler.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import javafx.scene.paint.Color;

public class EOEntityERDiagram extends AbstractEOEntityDiagram{

	public EOEntityERDiagram(EOEntity entity, List diagramList, EOERDiagramGroup group) {
		super(entity, diagramList, group);
	}
	
	public EOEntityERDiagram(EOEntity entity, AbstractDiagramGroup group) {  
		super(entity, group);
	}
	
	@Override
	public EOModelMap toMap(EOModelMap entityMap) {
		EOModelMap entityMapWithDiagrams = entityMap;
		entityMapWithDiagrams.remove("ERDiagrams");
		List<Map> diagrams = new LinkedList<Map>();
		HashMap<String, EOEntityDiagramDimension> diagramDimensions = getDiagramDimensions();
		Iterator<String> diagramIterator = diagramDimensions.keySet().iterator();
		while (diagramIterator.hasNext()) {
			EOModelMap entityDiagramMap = new EOModelMap();
			String diagramName = diagramIterator.next();
			EOEntityDiagramDimension dimension = diagramDimensions.get(diagramName);
			entityDiagramMap.setInteger("xPos", dimension.getxPos().intValue());
			entityDiagramMap.setInteger("yPos", dimension.getyPos().intValue());
			entityDiagramMap.setInteger("width", dimension.getWidth().intValue());
			entityDiagramMap.setInteger("height", dimension.getHeight().intValue());
			entityDiagramMap.setString("diagramName", diagramName, true);
			diagrams.add(entityDiagramMap);
		}
		entityMapWithDiagrams.setList("ERDiagrams", diagrams, true);
		return entityMapWithDiagrams;
	}
	
//	@Override
//	public EOModelMap removeFromEntityPlist(EOModelMap entityMap) {
////		EOModelMap entityMapWithoutDiagrams = entityMap;
////		List diagramList = entityMapWithoutDiagrams.getList("ERDiagrams");
////		for (int index = 0; index < diagramList.size(); index++) {
////			Object digram = diagramList.get(index);
////			if (digram instanceof Map) {
////				Map diagramMap = (Map)digram;
////				String diagramName = (String)diagramMap.get("diagramName");
////				if (diagramName.equals(getSelectedDiagramName())) {
////					
////				}
////			}
////		}
//		getDiagramDimensions().remove(getSelectedDiagramName());
//	}

	@Override
	public DiagramNode draw(String selectedDiagramName) {
		setSelectedDiagramName(selectedDiagramName);
		DiagramNode entityNode = new DiagramNode(this, selectedDiagramName);
		List<EOAttribute> attributeList = new ArrayList<EOAttribute>();
		List<EORelationship> relationshipsList = new ArrayList<EORelationship>();
		relationshipsList.addAll(getEntity().getRelationships());
		
		Iterator<EOAttribute> attributeIterator = getEntity().getAttributes().iterator();
		while (attributeIterator.hasNext()) {
			EOAttribute attribute = attributeIterator.next();
			attributeList.add(attribute);
		}
		
		double xPos = getDiagramDimensionForKey(selectedDiagramName).getxPos();
		double yPos = getDiagramDimensionForKey(selectedDiagramName).getyPos();
		double width = getDiagramDimensionForKey(selectedDiagramName).getWidth();
		double height = getDiagramDimensionForKey(selectedDiagramName).getHeight();
		
		entityNode.setAttributeList(attributeList);
		entityNode.setRelationshipsList(relationshipsList);
		entityNode.setTitle(getEntity().getExternalName());
		entityNode.setDescription("Beschreibung");
		entityNode.setColor(Color.AZURE);
		entityNode.setBounds(new Rectangle(xPos, yPos, width, height));
		
		return entityNode;
	}

}
