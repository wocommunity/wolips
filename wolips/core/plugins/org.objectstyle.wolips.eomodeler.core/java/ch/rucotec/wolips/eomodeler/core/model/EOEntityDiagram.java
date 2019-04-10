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

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.E_DiagramType;
import javafx.scene.paint.Color;

/**
 * This class makes an entity to an entity diagram, every entity diagram have their own list of
 * diagram dimensions which is a {@link HashMap} and the "Key" String is the name of the {@link AbstractDiagram} the entity diagram needs to be shown.
 * 
 * @author Savas Celik
 *
 */
public class EOEntityDiagram {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private EOEntity myEntity;
	protected HashMap<String, EOEntityDiagramDimension> loadedDiagramDimension;
	protected HashMap<String, EOEntityDiagramDimension> myERDiagramDimensions;
	protected HashMap<String, EOEntityDiagramDimension> myClassDiagramDimensions;
	private String mySelectedDiagramName;
	private Map<E_DiagramType, AbstractDiagramCollection> myCollections = new HashMap<E_DiagramType, AbstractDiagramCollection>();
	
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
	public EOEntityDiagram(EOEntity entity) {
		myEntity = entity;
		myERDiagramDimensions = new HashMap<String, EOEntityDiagramDimension>();
		myClassDiagramDimensions = new HashMap<String, EOEntityDiagramDimension>();
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
	public EOEntityDiagram(EOEntity entity, List diagramList) {
		myEntity = entity;
		myERDiagramDimensions = new HashMap<String, EOEntityDiagramDimension>();
		myClassDiagramDimensions = new HashMap<String, EOEntityDiagramDimension>();
		loadedDiagramDimension = new HashMap<String, EOEntityDiagramDimension>();
		
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
				loadedDiagramDimension.put(diagramName, erdDimension);
			}
		}
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	public void putCollection(E_DiagramType diagramType, AbstractDiagramCollection collection) {
		if (!myCollections.containsKey(diagramType)) {
			myCollections.put(diagramType, collection);
		}
	}
	
	public void addDiagramDimensions(List diagramList, E_DiagramType diagramType) {
		Map<String, EOEntityDiagramDimension> entityDiagramDiemensions = generateEntityDiagramDimensions(diagramList);
		
		if (diagramType == E_DiagramType.CLASSDIAGRAM) {
			myClassDiagramDimensions.putAll(entityDiagramDiemensions);
		} else {
			myERDiagramDimensions.putAll(entityDiagramDiemensions);
		}
	}
	
	private Map<String, EOEntityDiagramDimension> generateEntityDiagramDimensions(List diagramList) {
		Map<String, EOEntityDiagramDimension> generatedEntityDiagramDimensionMap = new HashMap<String, EOEntityDiagramDimension>();
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
				generatedEntityDiagramDimensionMap.put(diagramName, erdDimension);
			}
		}
		return generatedEntityDiagramDimensionMap;
	}
	
	/**
	 * This method returns a {@link DiagramNode} which is the visual element 
	 * for the diagram.
	 * 
	 * @param selectedDiagramName
	 * @return - a {@link DiagramNode}
	 */
	public DiagramNode draw(String selectedDiagramName, E_DiagramType diagramType) {
		
		if (diagramType == E_DiagramType.CLASSDIAGRAM) {
			return drawClassDiagram(selectedDiagramName);
		}
		return drawERDiagram(selectedDiagramName);
	}
	
	private DiagramNode drawERDiagram(String selectedDiagramName) {
		setSelectedDiagramName(selectedDiagramName);
		DiagramNode entityNode = new DiagramNode(this, selectedDiagramName, E_DiagramType.ERDIAGRAM);
		
		double xPos = getERDiagramDimensionForKey(selectedDiagramName).getxPos();
		double yPos = getERDiagramDimensionForKey(selectedDiagramName).getyPos();
		double width = getERDiagramDimensionForKey(selectedDiagramName).getWidth();
		double height = getERDiagramDimensionForKey(selectedDiagramName).getHeight();
		
		entityNode.setTitle(getEntity().getExternalName());
		entityNode.setColor(Color.AZURE);
		entityNode.setBounds(new Rectangle(xPos, yPos, width, height));
		
		return entityNode;
	}
	
	private DiagramNode drawClassDiagram(String selectedDiagramName) {
		setSelectedDiagramName(selectedDiagramName);
		DiagramNode entityNode = new DiagramNode(this, selectedDiagramName, E_DiagramType.CLASSDIAGRAM);
		
		double xPos = getClassDiagramDimensionForKey(selectedDiagramName).getxPos();
		double yPos = getClassDiagramDimensionForKey(selectedDiagramName).getyPos();
		double width = getClassDiagramDimensionForKey(selectedDiagramName).getWidth();
		double height = getClassDiagramDimensionForKey(selectedDiagramName).getHeight();
		
		entityNode.setTitle(getEntity().getClassNameWithoutPackage());
		entityNode.setColor(Color.AZURE);
		entityNode.setBounds(new Rectangle(xPos, yPos, width, height));
		
		return entityNode;
	}
	
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
		List<Map> classDiagramList = toMapClassDiagram();
		List<Map> erDiagramList = toMapERDiagram();
		
		entityMap.setList("ClassDiagrams", classDiagramList, true);
		entityMap.setList("ERDiagrams", erDiagramList, true);
		
		WOLPropertyListSerialization.propertyListToFile("Entity Modeler v" + EOModel.CURRENT_VERSION, entityFile, entityMap);
		
		// To prevend that the "Model Changed on Disk" information pops up.
		myEntity.setLastModified(new EOLastModified(entityFile));
	}
	
	public List<Map> toMapClassDiagram() {
		List<Map> classDiagrams = new LinkedList<Map>();
		Iterator<String> diagramIterator = myClassDiagramDimensions.keySet().iterator();
		while (diagramIterator.hasNext()) {
			EOModelMap entityDiagramMap = new EOModelMap();
			String diagramName = diagramIterator.next();
			EOEntityDiagramDimension dimension = myClassDiagramDimensions.get(diagramName);
			entityDiagramMap.setInteger("xPos", dimension.getxPos().intValue());
			entityDiagramMap.setInteger("yPos", dimension.getyPos().intValue());
			entityDiagramMap.setInteger("width", dimension.getWidth().intValue());
			entityDiagramMap.setInteger("height", dimension.getHeight().intValue());
			entityDiagramMap.setString("diagramName", diagramName, true);
			classDiagrams.add(entityDiagramMap);
		}
		return classDiagrams;
	}
	
	public List<Map> toMapERDiagram() {
		List<Map> ERDiagrams = new LinkedList<Map>();
		Iterator<String> diagramIterator = myERDiagramDimensions.keySet().iterator();
		while (diagramIterator.hasNext()) {
			EOModelMap entityDiagramMap = new EOModelMap();
			String diagramName = diagramIterator.next();
			EOEntityDiagramDimension dimension = myERDiagramDimensions.get(diagramName);
			entityDiagramMap.setInteger("xPos", dimension.getxPos().intValue());
			entityDiagramMap.setInteger("yPos", dimension.getyPos().intValue());
			entityDiagramMap.setInteger("width", dimension.getWidth().intValue());
			entityDiagramMap.setInteger("height", dimension.getHeight().intValue());
			entityDiagramMap.setString("diagramName", diagramName, true);
			ERDiagrams.add(entityDiagramMap);
		}
		return ERDiagrams;
	}
	
	/**
	 * Removes a diagram from the entity.plist.
	 * 
	 * @param selectedDiagramName
	 */
	public void removeFromEntityPlist(String selectedDiagramName, E_DiagramType diagramType) {
		if (diagramType == E_DiagramType.CLASSDIAGRAM) {
			removeClassDiagramFromEntityPlist(selectedDiagramName);
		} else {
			removeERDiagramFromEntityPlist(selectedDiagramName);
		}
	}
	
	public void removeERDiagramFromEntityPlist(String selectedDiagramName) {
		myERDiagramDimensions.remove(selectedDiagramName);
	}
	
	public void removeClassDiagramFromEntityPlist(String selectedDiagramName) {
		myClassDiagramDimensions.remove(selectedDiagramName);
	}
	
	/**
	 * Generates a {@link EOModelMap} with the information which describes in which diagram and on 
	 * which position this EOEntityDiagram is.
	 * 
	 * @param entityMap
	 * @return a {@link EOModelMap}
	 */
//	public abstract EOModelMap toMap(EOModelMap entityMap);
	
	/**
	 * Changes the dimension of the EOEntityDiagram on the diagram.
	 * 
	 * @param selectedDiagramName
	 * @param dimension
	 */
	public void positionsChanged(String selectedDiagramName, Rectangle dimension, E_DiagramType diagramType) {
		if (diagramType == E_DiagramType.CLASSDIAGRAM) {
			positionsChangedClassDiagram(selectedDiagramName, dimension);
		} else {
			positionsChangedERDiagram(selectedDiagramName, dimension);
		}
		myCollections.get(diagramType).setModelDirty(true);
	}
	
	private void positionsChangedERDiagram(String selectedDiagramName, Rectangle dimension) {
		EOEntityDiagramDimension entityDimension = getERDiagramDimensionForKey(selectedDiagramName);
		entityDimension.setxPos(dimension.getX());
		entityDimension.setyPos(dimension.getY());
		entityDimension.setWidth(dimension.getWidth());
		entityDimension.setHeight(dimension.getHeight());
	}
	
	public void positionsChangedClassDiagram(String selectedDiagramName, Rectangle dimension) {
		EOEntityDiagramDimension entityDimension = getClassDiagramDimensionForKey(selectedDiagramName);
		entityDimension.setxPos(dimension.getX());
		entityDimension.setyPos(dimension.getY());
		entityDimension.setWidth(dimension.getWidth());
		entityDimension.setHeight(dimension.getHeight());
	}
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
	
	public EOEntity getEntity() {
		return myEntity;
	}
	
	public EOEntityDiagramDimension getERDiagramDimensionForKey(String key) {
		return myERDiagramDimensions.get(key);
	}
	
	public EOEntityDiagramDimension getClassDiagramDimensionForKey(String key) {
		return myClassDiagramDimensions.get(key);
	}

	
//	public EOEntityDiagramDimension getDiagramDimensionForKey(String key) {
//		return myDiagramDimensions.get(key);
//	}
	
//	public HashMap<String, EOEntityDiagramDimension> getDiagramDimensions() {
//		return myDiagramDimensions;
//	}

	public HashMap<String, EOEntityDiagramDimension> getERDiagramDimensions() {
		return myERDiagramDimensions;
	}

	public HashMap<String, EOEntityDiagramDimension> getClassDiagramDimensions() {
		return myClassDiagramDimensions;
	}

	public String getSelectedDiagramName() {
		return mySelectedDiagramName;
	}

	public void setSelectedDiagramName(String mySelectedDiagramName) {
		this.mySelectedDiagramName = mySelectedDiagramName;
	}
}
