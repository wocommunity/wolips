package ch.rucotec.gef.diagram.visuals.node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import javafx.geometry.Orientation;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * This Class is responsible for the visual effects of a {@link DiagramNode}.
 * 
 * @author Savas Celik
 * <br/>(this was originally documented by GEF)
 */
public abstract class AbstractDiagramNodeVisual extends Region {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------

	public static final double HORIZONTAL_PADDING = 20d;
	public static final double VERTICAL_PADDING = 10d;
	public static final double VERTICAL_SPACING = 5d;
	public static final String GENERIC_RECORD = "EOGenericRecord";
    
	private DiagramNode myNode;
    private List<EOAttribute> attributeList;
    private GridPane gridPane = new GridPane();
    private Double minWidth;
    private Text title;
    private GeometryNode<?> shape;
    private VBox labelVBox;
    private Set<EOAttribute> foreignKeyAttributes = new HashSet<EOAttribute>();
    
//    private final ObservableList<TableAttribute> data = FXCollections.observableArrayList();
    
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
    
    public AbstractDiagramNodeVisual(DiagramNode node) {
    	this.myNode = node;
    	this.attributeList = node.getAttributeList();
    	
    	createVisual();
    }
    
	//---------------------------------------------------------------------------
	// ### Abstract Methods
	//---------------------------------------------------------------------------
    
    /**
     * This method creates all the elements for the attributes.
     */
    public abstract void initAttributeList();
    
    /**
     * Creates the necessary elements for an Diagram.
     */
    public abstract void createVisual();
    
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------

    protected void initForeignKeys() {
    	EOEntity entity = myNode.getEntityDiagram().getEntity();
    	Set<EORelationship> toOneRealationships = entity.getToOneRelationships();
		for (EORelationship relationship : toOneRealationships) {
			for (EOAttribute attribut : attributeList) {
				if (relationship.getRelatedAttributes().contains(attribut)) {
					foreignKeyAttributes.add(attribut);
				}
			}
		}
    }

    @Override
    public double computeMinHeight(double width) {
        // ensure title is always visible
        // descriptionFlow.minHeight(width) +
        // titleText.getLayoutBounds().getHeight() + VERTICAL_PADDING * 2;
        return labelVBox.minHeight(width);
    }

    @Override
    public double computeMinWidth(double height) {
        // ensure title is always visible
//        return titleText.getLayoutBounds().getWidth() + HORIZONTAL_PADDING * 2;
    	if (minWidth == null) {
    		minWidth = new Double(0);
    		gridPane.getColumnConstraints().forEach(e -> minWidth += e.getPrefWidth());
    	}
        return Math.max(minWidth+52, title.getLayoutBounds().getWidth() + HORIZONTAL_PADDING * 2);
    }

    @Override
    protected double computePrefHeight(double width) {
        return minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return minWidth(height);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }
    
    //---------------------------------------------------------------------------
    // ### Local Helpers
    //---------------------------------------------------------------------------
    
    protected void updateNodeBounds() {
    	myNode.setBounds(new Rectangle(getBoundsInParent().getMinX(), getBoundsInParent().getMinY(), getBoundsInParent().getWidth(), getBoundsInParent().getHeight()));
    }

    //---------------------------------------------------------------------------
    // ### Basic Accessors
    //---------------------------------------------------------------------------

    public GeometryNode<?> getGeometryNode() {
        return shape;
    }
    
	public void setGeometryNode(GeometryNode<?> shape) {
		this.shape = shape;
	}

    public Text getTitle() {
        return title;
    }
    
	public void setTitle(Text title) {
		this.title = title;
	}

    public void setColor(Color color) {
        shape.setFill(color);
    }
    
    public List<EOAttribute> getAttributeList() {
		return attributeList;
	}

	public DiagramNode getMyNode() {
		return myNode;
	}

	public void setMyNode(DiagramNode myNode) {
		this.myNode = myNode;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}

	public VBox getLabelVBox() {
		return labelVBox;
	}

	public void setLabelVBox(VBox labelVBox) {
		this.labelVBox = labelVBox;
	}

	public Set<EOAttribute> getForeignKeyAttributes() {
		return foreignKeyAttributes;
	}

	public void setForeignKeyAttributes(Set<EOAttribute> foreignKeyAttributes) {
		this.foreignKeyAttributes = foreignKeyAttributes;
	}

	public void setAttributeList(List<EOAttribute> attributeList) {
		this.attributeList = attributeList;
	}
    
}