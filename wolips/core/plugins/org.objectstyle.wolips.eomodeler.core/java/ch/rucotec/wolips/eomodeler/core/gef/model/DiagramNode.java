package ch.rucotec.wolips.eomodeler.core.gef.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import com.google.common.collect.Lists;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.AbstractEOEntityDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOEntityERDiagram;
import javafx.scene.paint.Color;

public class DiagramNode extends AbstractDiagramItem implements Serializable {

    /**
     * Generated UUID
     */
    private static final long serialVersionUID = 8875579454539897410L;

    public static final String PROP_TITLE = "title";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_COLOR = "color";
    public static final String PROP_BOUNDS = "bounds";
    public static final String PROP_INCOMING_CONNECTIONS = "incomingConnections";
    public static final String PROP_OUTGOGING_CONNECTIONS = "outgoingConnections";
    
    // TODO: Savas hinzugefügt
    
    public static final String PROP_ATTRIBUTELIST = "attributeList";
    private List<EOAttribute> attributeList;
    private AbstractEOEntityDiagram entityDiagram;
    private String selectedDiagram;

    public DiagramNode() {
    }
    
    public DiagramNode(AbstractEOEntityDiagram entityDiagram, String selectedDiagram) {
    	this.entityDiagram = entityDiagram;
    	this.selectedDiagram = selectedDiagram;
    }
    
    
//    public void addAttribute(String name) {
//    	attributeList.add(name);
//    	pcs.firePropertyChange(PROP_ATTRIBUTELIST, null, name);
//    }
    
    public List<EOAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<EOAttribute> attributeList) {
		pcs.firePropertyChange(PROP_ATTRIBUTELIST, this.attributeList, (this.attributeList = attributeList));
	}
	
    public static final String PROP_RELATIONSHIPSLIST = "relationshipsList";
    private List<EORelationship> relationshipsList;

//    public void addAttribute(String name) {
//    	attributeList.add(name);
//    	pcs.firePropertyChange(PROP_ATTRIBUTELIST, null, name);
//    }
    
    public List<EORelationship> getRelationshipsList() {
		return relationshipsList;
	}

	public void setRelationshipsList(List<EORelationship> relationshipsList) {
		pcs.firePropertyChange(PROP_RELATIONSHIPSLIST, this.relationshipsList, (this.relationshipsList = relationshipsList));
	}

	/**
     * The title of the node
     */
    private String title;

    /**
     * he description of the node, which is optional
     */
    private String description;

    /**
     * The background color of the node
     */
    private Color color;

    /**
     * The size and position of the visual representation
     */
    private Rectangle bounds;

    private List<DiagramConnection> incomingConnections = Lists.newArrayList();
    private List<DiagramConnection> outgoingConnections = Lists.newArrayList();

    public void addIncomingConnection(DiagramConnection conn) {
        incomingConnections.add(conn);
        pcs.firePropertyChange(PROP_INCOMING_CONNECTIONS, null, conn);
    }

    public void addOutgoingConnection(DiagramConnection conn) {
        outgoingConnections.add(conn);
        pcs.firePropertyChange(PROP_OUTGOGING_CONNECTIONS, null, conn);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
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
    		((EOEntityERDiagram) entityDiagram).refresh(selectedDiagram, bounds);
    	}
        pcs.firePropertyChange(PROP_BOUNDS, this.bounds, (this.bounds = bounds.getCopy()));
    }

    public void setColor(Color color) {
        pcs.firePropertyChange(PROP_COLOR, this.color, (this.color = color));
    }

    public void setDescription(String description) {
        pcs.firePropertyChange(PROP_DESCRIPTION, this.description, (this.description = description));
    }

    public void setTitle(String title) {
        pcs.firePropertyChange(PROP_TITLE, this.title, (this.title = title));
    }
} 