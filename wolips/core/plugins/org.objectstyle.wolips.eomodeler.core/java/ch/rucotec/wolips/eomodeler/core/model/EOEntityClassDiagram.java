package ch.rucotec.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import javafx.scene.paint.Color;

/**
 * Represents an entity as an element of a class diagram every entity in a class diagram has a
 * {@code EOEntityClassDiagram} which knows all their positions and diagram names.
 * 
 * @author Savas Celik
 * @see AbstractEOEntityDiagram
 */
public class EOEntityClassDiagram extends AbstractEOEntityDiagram {

	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	public EOEntityClassDiagram(EOEntity entity, List diagramList, EOClassDiagramCollection group) {
		super(entity, diagramList, group);
	}
	
	public EOEntityClassDiagram(EOEntity entity, EOClassDiagramCollection group) {  
		super(entity, group);
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	@Override
	public EOModelMap toMap(EOModelMap entityMap) {
		EOModelMap entityMapWithDiagrams = entityMap;
		entityMapWithDiagrams.remove("ClassDiagrams");
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
		setSelectedDiagramName(selectedDiagramName);
		DiagramNode entityNode = new DiagramNode(this, selectedDiagramName);
		
		double xPos = getDiagramDimensionForKey(selectedDiagramName).getxPos();
		double yPos = getDiagramDimensionForKey(selectedDiagramName).getyPos();
		double width = getDiagramDimensionForKey(selectedDiagramName).getWidth();
		double height = getDiagramDimensionForKey(selectedDiagramName).getHeight();
		
		entityNode.setTitle(getEntity().getClassNameWithoutPackage());
		entityNode.setColor(Color.AZURE);
		entityNode.setBounds(new Rectangle(xPos, yPos, width, height));
		
		return entityNode;
	}

}
