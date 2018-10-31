package ch.rucotec.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;

public class EOEntityClassDiagram extends AbstractEOEntityDiagram {

	public EOEntityClassDiagram(EOEntity entity, List diagramList, EOERDiagramGroup group) {
		super(entity, diagramList, group);
	}
	
	@Override
	public EOModelMap toMap(EOModelMap entityMap) {
		EOModelMap entityMapWithDiagrams = entityMap;
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
		entityMapWithDiagrams.setList("ClassDiagrams", diagrams, true);
		return entityMapWithDiagrams;
	}

	@Override
	public DiagramNode draw(String selectedDiagramName) {
		System.out.println("Hier wird ein KlassenDiagram Entity gezeichnet");
		return null;
	}

}
