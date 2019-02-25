package ch.rucotec.wolips.eomodeler.core.gef.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import com.google.common.collect.Lists;

import ch.rucotec.wolips.eomodeler.core.model.AbstractEOEntityDiagram;
import javafx.scene.paint.Color;

/**
 * This class is used to make a Node for a Diagram.
 * A node is in this case a rectecular element in our Diagram.
 * The look of the our Node (rectecular element) is treated in the DiagramNodeVisual class.
 * 
 * @author Savas Celik
 * <br/>(this was originally documented by GEF)
 */
public class DiagramNode extends AbstractDiagramItem implements Serializable {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------

	private static final long serialVersionUID = 8875579454539897410L;

	public static final String PROP_TITLE = "title";
	public static final String PROP_COLOR = "color";
	public static final String PROP_BOUNDS = "bounds";
	public static final String PROP_INCOMING_CONNECTIONS = "incomingConnections";
	public static final String PROP_OUTGOGING_CONNECTIONS = "outgoingConnections";
	public static final String PROP_ATTRIBUTELIST = "attributeList";
	public static final String PROP_RELATIONSHIPSLIST = "relationshipsList";

	private String title;
	private Color color;
	private Rectangle bounds;
	private AbstractEOEntityDiagram entityDiagram;
	private String selectedDiagram;
	private List<DiagramConnection> incomingConnections = Lists.newArrayList();
	private List<DiagramConnection> outgoingConnections = Lists.newArrayList();
	private List<EOAttribute> attributeList;
	private List<EORelationship> relationshipsList;

	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------

	public DiagramNode() {
	}

	/**
	 * Creates a node for the diagram, a node is in this case a rectecular element.
	 * 
	 * @param entityDiagram
	 * @param selectedDiagram
	 */
	public DiagramNode(AbstractEOEntityDiagram entityDiagram, String selectedDiagram) {
		this.entityDiagram = entityDiagram;
		this.selectedDiagram = selectedDiagram;
		setAttributeList(new ArrayList<EOAttribute>(entityDiagram.getEntity().getAttributes()));
		setRelationshipsList(new ArrayList<EORelationship>(entityDiagram.getEntity().getRelationships()));
	}
	
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	public void removeRelationshipToParent() {
		for (int i = 0; i < relationshipsList.size(); i++) {
			EORelationship relationship = relationshipsList.get(i);
			if (relationship.getDestination() == entityDiagram.getEntity().getParent()) {
				relationshipsList.remove(relationship);
			}
		}
	}
	
	public void removeParentAttributes(Set<EOAttribute> pkAttributes) {
		for (EOAttribute pkAttribute : pkAttributes) {
			for (int i = 0; i < attributeList.size(); i++) {
				EOAttribute attribute = attributeList.get(i);
				if (attribute.getName().equals(pkAttribute.getName())) {
					attributeList.remove(attribute);
				}
			}
		}
	}
	
	
	
	public void addChildrenRelationships(Set<EORelationship> relationships) {
		relationshipsList.addAll(relationships);
	}
	
	public void addChildrenAttributes(Set<EOAttribute> attributes) {
		// adding every attribute except PK and inherited attributes from children.
		for (EOAttribute attribute : attributes) {
			if (!attribute.isPrimaryKey() && !attribute.isInherited()) {
				attributeList.add(attribute);
			}
		}
	}

	public void setAttributeList(List<EOAttribute> attributeList) {
		pcs.firePropertyChange(PROP_ATTRIBUTELIST, this.attributeList, (this.attributeList = attributeList));
	}

	public void setRelationshipsList(List<EORelationship> relationshipsList) {
		pcs.firePropertyChange(PROP_RELATIONSHIPSLIST, this.relationshipsList, (this.relationshipsList = relationshipsList));
	}

	public void addIncomingConnection(DiagramConnection conn) {
		incomingConnections.add(conn);
		pcs.firePropertyChange(PROP_INCOMING_CONNECTIONS, null, conn);
	}

	public void addOutgoingConnection(DiagramConnection conn) {
		outgoingConnections.add(conn);
		pcs.firePropertyChange(PROP_OUTGOGING_CONNECTIONS, null, conn);
	}

	public void removeIncomingConnection(DiagramConnection conn) {
		incomingConnections.remove(conn);
		pcs.firePropertyChange(PROP_INCOMING_CONNECTIONS, conn, null);
	}

	public void removeOutgoingConnection(DiagramConnection conn) {
		outgoingConnections.remove(conn);
		pcs.firePropertyChange(PROP_OUTGOGING_CONNECTIONS, conn, null);
	}

	public void setBounds(Rectangle bounds) {
		if (this.bounds != null && entityDiagram != null && !this.bounds.equals(bounds)) { 
			entityDiagram.positionsChanged(selectedDiagram, bounds); // hier wird dem EOEntityDiagram mitgeteilt, dass sich die Position geändert hat.
		}
		pcs.firePropertyChange(PROP_BOUNDS, this.bounds, (this.bounds = bounds.getCopy()));
	}

	public void setColor(Color color) {
		pcs.firePropertyChange(PROP_COLOR, this.color, (this.color = color));
	}

	public void setTitle(String title) {
		pcs.firePropertyChange(PROP_TITLE, this.title, (this.title = title));
	}
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------

	public Rectangle getBounds() {
		return bounds;
	}

	public Color getColor() {
		return color;
	}

	public List<DiagramConnection> getIncomingConnections() {
		return incomingConnections;
	}

	public List<DiagramConnection> getOutgoingConnections() {
		return outgoingConnections;
	}

	public String getTitle() {
		return title;
	}

	public AbstractEOEntityDiagram getEntityDiagram() {
		return entityDiagram;
	}

	public List<EOAttribute> getAttributeList() {
		return attributeList;
	}

	public List<EORelationship> getRelationshipsList() {
		return relationshipsList;
	}
} 