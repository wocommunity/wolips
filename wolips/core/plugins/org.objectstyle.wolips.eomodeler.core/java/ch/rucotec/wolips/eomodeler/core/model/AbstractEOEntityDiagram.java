package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOLastModified;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;

/**
 * This class makes an entity to an entity diagram, every entity diagram have their own list of
 * diagram dimensions which is a {@link HashMap} and the "Key" String is the name of the {@link AbstractDiagram} the entity diagram needs to be shown.
 * 
 * @author celik
 *
 */
public abstract class AbstractEOEntityDiagram {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private EOEntity myEntity;
	private HashMap<String, EOEntityDiagramDimension> myDiagramDimensions;
	private AbstractDiagramCollection myGroup;
	private String mySelectedDiagramName;
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * Creates an EOEntityDiagram ({@link EOEntityERDiagram} or {@link EOClassDiagram})
	 * for the given entity.
	 * 
	 * @param entity
	 * @param group
	 */
	public AbstractEOEntityDiagram(EOEntity entity, AbstractDiagramCollection group) {
		myEntity = entity;
		myDiagramDimensions = new HashMap<String, EOEntityDiagramDimension>();
		myGroup = group;
	}
	
	/**
	 * Creates an EOEntityDiagram ({@link EOEntityERDiagram} or {@link EOClassDiagram}) 
	 * for the given entity, reads out the given List (which must be an instanceof {@link Map})
	 * the List has information about where the position of the entity diagram is and for which
	 * diagram.
	 * 
	 * @param entity
	 * @param diagramList
	 * @param group
	 */
	public AbstractEOEntityDiagram(EOEntity entity, List diagramList, AbstractDiagramCollection group) {
		myEntity = entity;
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
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	/**
	 * This method returns a {@link DiagramNode} which is the visual element 
	 * for the diagram.
	 * 
	 * @param selectedDiagramName
	 * @return - a {@link DiagramNode}
	 */
	public abstract DiagramNode draw(String selectedDiagramName);
	
	/**
	 * Saves all the information into the responsible entity.plist
	 * 
	 * @param modelFolder
	 * @throws PropertyListParserException
	 * @throws IOException
	 */
	public void saveToFile(File modelFolder) throws PropertyListParserException, IOException {
		EOEntity entity = getEntity();
		String entityName = entity.getName();
		File entityFile = new File(modelFolder, entityName + ".plist");
		EOModelMap entityMap = entity.toEntityMap();
		EOModelMap entityMapWithDiagrams = toMap(entityMap);
		WOLPropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, entityFile, entityMapWithDiagrams);
		
		myEntity.setLastModified(new EOLastModified(entityFile));
	}
	
	/**
	 * Removes a diagram from the entity.plist.
	 * 
	 * @param selectedDiagramName
	 */
	public void removeFromEntityPlist(String selectedDiagramName) {
		myDiagramDimensions.remove(selectedDiagramName);
	}
	
	/**
	 * Generates a {@link EOModelMap} with the information which describes in which diagram and on 
	 * which position this EOEntityDiagram is.
	 * 
	 * @param entityMap
	 * @return a {@link EOModelMap}
	 */
	public abstract EOModelMap toMap(EOModelMap entityMap);
	
	/**
	 * Changes the dimension of the EOEntityDiagram on the diagram.
	 * 
	 * @param selectedDiagramName
	 * @param dimension
	 */
	public void positionsChanged(String selectedDiagramName, Rectangle dimension) {
		EOEntityDiagramDimension entityDimension = getDiagramDimensionForKey(selectedDiagramName);
		entityDimension.setxPos(dimension.getX());
		entityDimension.setyPos(dimension.getY());
		entityDimension.setWidth(dimension.getWidth());
		entityDimension.setHeight(dimension.getHeight());
		myGroup.setModelDirty(true);
	}
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
	
	public EOEntity getEntity() {
		return myEntity;
	}

	public EOEntityDiagramDimension getDiagramDimensionForKey(String key) {
		return myDiagramDimensions.get(key);
	}
	
	public HashMap<String, EOEntityDiagramDimension> getDiagramDimensions() {
		return myDiagramDimensions;
	}

	public String getSelectedDiagramName() {
		return mySelectedDiagramName;
	}

	public void setSelectedDiagramName(String mySelectedDiagramName) {
		this.mySelectedDiagramName = mySelectedDiagramName;
	}
}
