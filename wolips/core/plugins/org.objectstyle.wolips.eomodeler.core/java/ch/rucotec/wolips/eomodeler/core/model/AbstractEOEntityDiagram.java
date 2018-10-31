package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOLastModified;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;

import com.webobjects.foundation.NSDictionary;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;

public abstract class AbstractEOEntityDiagram {
	
	private EOEntity myEntity;
	private EOModelMap myEntityMap;
	private HashMap<String, EOEntityDiagramDimension> myDiagramDimensions;
	private EOERDiagramGroup myGroup;
	
	public AbstractEOEntityDiagram(EOEntity entity, List diagramList, EOERDiagramGroup group) {
		myEntity = entity;
		myEntityMap = entity._getEntityMap();
		myDiagramDimensions = new HashMap<String, EOEntityDiagramDimension>();
		myGroup = group;
		
		for (int i = 0; i < diagramList.size(); i++) {
			Object digram = diagramList.get(i);
			if (digram instanceof Map) {
				Map diagramMap = (Map)digram;
				String diagramName = (String)diagramMap.get("diagramName");
				int xPos = Integer.parseInt((String)diagramMap.get("xPos"));
				int yPos = Integer.parseInt((String)diagramMap.get("yPos"));
				int width = Integer.parseInt((String)diagramMap.get("width"));
				int height = Integer.parseInt((String)diagramMap.get("height"));
				
				EOEntityDiagramDimension erdDimension = new EOEntityDiagramDimension(xPos, yPos, width, height);
				myDiagramDimensions.put(diagramName, erdDimension);
			}
		}
		
		
//		Iterator<?> diagramKeyIterator = diagramMap.keySet().iterator();
//		while (diagramKeyIterator.hasNext()) {
//			String erdName = String.valueOf(diagramKeyIterator.next());
//			Object erdProperties = diagramMap.get(erdName);
//			if (erdProperties instanceof Map) {
//				int xPos = Integer.parseInt(((String)((Map) erdProperties).get("xPos")));
//				int yPos = Integer.parseInt(((String)((Map) erdProperties).get("yPos")));
//				int width = Integer.parseInt(((String)((Map) erdProperties).get("width")));
//				int height = Integer.parseInt(((String)((Map) erdProperties).get("height")));
//				
//				EOEntityDiagramDimension erdDimension = new EOEntityDiagramDimension(xPos, yPos, width, height);
//				
//				myDiagramDimensions.put(erdName, erdDimension);
//			}
//		}
		
	}
	
	public abstract DiagramNode draw(String selectedDiagramName);
	
	public void saveToFile(File modelFolder) throws PropertyListParserException, IOException {
		EOEntity entity = getEntity();
		String entityName = entity.getName();
		File entityFile = new File(modelFolder, entityName + ".plist");
		EOModelMap entityMap = entity.toEntityMap();
		EOModelMap entityMapWithDiagrams = toMap(entityMap);
		WOLPropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, entityFile, entityMapWithDiagrams);
		
		myEntity.setLastModified(new EOLastModified(entityFile));
	}
	
	public abstract EOModelMap toMap(EOModelMap entityMap);
	
	public void refresh(String selectedDiagramName, Rectangle dimension) {
		EOEntityDiagramDimension entityDimension = getDiagramDimensionForKey(selectedDiagramName);
		entityDimension.setxPos(dimension.getX());
		entityDimension.setyPos(dimension.getY());
		entityDimension.setWidth(dimension.getWidth());
		entityDimension.setHeight(dimension.getHeight());
		myGroup.setModelDirty(true);
	}
	
	public EOEntity getEntity() {
		return myEntity;
	}

	public EOModelMap getEntityMap() {
		return myEntityMap;
	}

	public EOEntityDiagramDimension getDiagramDimensionForKey(String key) {
		return myDiagramDimensions.get(key);
	}
	
	public HashMap<String, EOEntityDiagramDimension> getDiagramDimensions() {
		return myDiagramDimensions;
	}
	
}
