package ch.rucotec.gef.diagram.visuals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.E_DiagramType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * This Class is responsible for the visual effects of a {@link DiagramNode}.
 * 
 * @author Savas Celik
 * <br/>(this was originally documented by GEF)
 */
public class DiagramNodeVisual extends Region {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------

    private static final double HORIZONTAL_PADDING = 20d;
    private static final double VERTICAL_PADDING = 10d;
    private static final double VERTICAL_SPACING = 5d;
    private static final String GENERIC_RECORD = "EOGenericRecord";
    
    private DiagramNode myNode;
    private List<EOAttribute> attributeList;
    private GridPane gridPane = new GridPane();
    private Double minWidth;
    private Text titleText;
    private GeometryNode<Rectangle> shape;
    private VBox labelVBox;
    private Set<EOAttribute> foreignKeyAttributes = new HashSet<EOAttribute>();
    
//    private final ObservableList<TableAttribute> data = FXCollections.observableArrayList();
    
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
    
    public DiagramNodeVisual(DiagramNode node) {
    	this.myNode = node;
    	this.attributeList = node.getAttributeList();
    	
    	if (node.getDiagramType() == E_DiagramType.ERDIAGRAM) {
    		createVisualForERDiagram();
    	} else if (node.getDiagramType() == E_DiagramType.CLASSDIAGRAM) {
    		createClassDiagram();
    	}
    }
    
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------

    /**
     * This method creates all the elements for the entity relationship diagram and places them into the gridpane.
     */
    public void initAttributeListForERDiagram() {
    	initForeignKeys();
    	Label lblAttributeName = null;
    	Label lblAttributeAllowsNull = null;
    	int pkCount = 0;
    	
    	for (EOAttribute pkAttribute : attributeList) {
    		if (pkAttribute.isPrimaryKey() && !pkAttribute.isInherited()) {
	    		lblAttributeName = new Label(pkAttribute.getColumnName());
				
				if (pkAttribute.isAllowsNull() != null) {
					lblAttributeAllowsNull = new Label(pkAttribute.isAllowsNull() ? "O" : "Ø");
				} else {
					lblAttributeAllowsNull = new Label("Ø");
				}
				
				lblAttributeName.setFont(Font.font(lblAttributeName.getFont().getFamily(), FontWeight.EXTRA_BOLD, 16));
				lblAttributeName.setUnderline(true);
				gridPane.add(lblAttributeName, 0, pkCount);
				gridPane.add(lblAttributeAllowsNull, 1, pkCount);
				pkCount++;
    		}
    	}
    	
		for (int i = 0; i < attributeList.size(); i++) {
			EOAttribute attribute = attributeList.get(i);
			if (!attribute.isPrimaryKey()) {
				lblAttributeName = new Label(attribute.getColumnName());
				if (lblAttributeName.getText() == null && attribute.isInherited()) {
					EOEntity parentEntity = attribute.getEntity().getParent();
					lblAttributeName.setText(parentEntity.getAttributeNamed(attribute.getName()).getColumnName());
				}
				if (attribute.isAllowsNull() != null) {
					lblAttributeAllowsNull = new Label(attribute.isAllowsNull() ? "O" : "Ø");
				} else {
					lblAttributeAllowsNull = new Label("Ø");
				}
				
				if(foreignKeyAttributes.contains(attribute)) {
					lblAttributeName.setFont(Font.font(lblAttributeName.getFont().getFamily(), FontPosture.ITALIC, lblAttributeName.getFont().getSize()));
					lblAttributeName.setStyle("-fx-border-width: 0 0 1.5 0; -fx-border-style: hidden hidden dashed hidden;");
				} else {
					lblAttributeName.setUnderline(false);
				}
				gridPane.add(lblAttributeName, 0, pkCount+i);
				gridPane.add(lblAttributeAllowsNull, 1, pkCount+i);
			}
		}
	}
    
    private void initForeignKeys() {
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
    
    /**
     * This method creates all the elements for the class diagram and places them into the gridpane.
     */
    public void initAttributeListForClassDiagram() {
    	Label lblAttributeName = null;
    	Label lblRelationshipName = null;
    	List<EORelationship> relationshipList = myNode.getRelationshipsList();
    	
    	// Adding Attributes to the GridPane
    	for (int i = 0; i < attributeList.size(); i++) {
			EOAttribute attribute = attributeList.get(i);
			
			if (attribute.isClassProperty()) { 
				lblAttributeName = new Label(attribute.getName() + " : " + attribute.getJavaClassName());
				
				gridPane.add(new Label("-"), 0, i+1);
				gridPane.add(lblAttributeName, 1, i+1);
			}
		}
    	
    	// Adding Relationships to the GridPane
    	for (int i = 0; i < relationshipList.size(); i++) {
    		EORelationship relationship = relationshipList.get(i);
    		if (relationship.getDestination().getClassNameWithoutPackage().equals(GENERIC_RECORD)) {
    			continue;
    		}
    		if (relationship.isToMany() != null && relationship.isToMany()) {
    			lblRelationshipName = new Label(relationship.getName() + " : " + relationship.getDestination().getClassNameWithoutPackage()+"[]");
    		} else if (relationship.isToOne()) {
    			lblRelationshipName = new Label(relationship.getName() + " : " + relationship.getDestination().getClassNameWithoutPackage());
    		}
    		
    		if (lblAttributeName != null) {
	    		gridPane.add(new Label("-"), 0, GridPane.getRowIndex(lblAttributeName) + i + 1);
	    		gridPane.add(lblRelationshipName, 1, GridPane.getRowIndex(lblAttributeName) + i + 1);
    		} else {
    			gridPane.add(new Label("-"), 0, 10 + i + 1);
	    		gridPane.add(lblRelationshipName, 1, 10 + i + 1);
    		}
    	}
    }
    
    /**
     * Creates the necessary elements for an Entity Relationship Diagram.
     */
    public void createVisualForERDiagram() {
    	// create background shape
        shape = new GeometryNode<>(new Rectangle(0, 0, 70, 30));
        shape.setFill(Color.LIGHTGREEN);
        shape.setStroke(Color.BLACK);
        shape.setStrokeType(StrokeType.INSIDE);
        
        // create vertical box for title and description
        labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));

        ColumnConstraints colmn = new ColumnConstraints();
        colmn.setHgrow(Priority.SOMETIMES);
        colmn.setMinWidth(100);
        colmn.setPrefWidth(100);
        gridPane.getColumnConstraints().add(colmn);
        gridPane.getColumnConstraints().add(new ColumnConstraints());
//        gridPane.getColumnConstraints().add(new ColumnConstraints(30, 30, 30));
        
        
        // ensure shape and labels are resized to fit this visual
        shape.prefWidthProperty().bind(widthProperty());
        shape.prefHeightProperty().bind(heightProperty());
        labelVBox.prefWidthProperty().bind(widthProperty());
        labelVBox.prefHeightProperty().bind(heightProperty());

        // create title text
        titleText = new Text();
        titleText.setTextOrigin(VPos.TOP);
        titleText.setFont(new Font(15));
        
        Separator separator = new Separator();
		separator.setEffect(new InnerShadow());
		separator.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator.setMinWidth(1);

        // vertically lay out title and description
        labelVBox.getChildren().addAll(titleText,separator, gridPane);

        // ensure title is always visible (see also #computeMinWidth(double) and
        // #computeMinHeight(double) methods)
        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        // wrap shape and VBox in Groups so that their bounds-in-parent is
        // considered when determining the layout-bounds of this visual
        initAttributeListForERDiagram();
        getChildren().addAll(new Group(shape), new Group(labelVBox));
    }
    
    /**
     * Creates the necessary elements for a Class Diagram.
     */
    public void createClassDiagram() {
    	// create background shape
        shape = new GeometryNode<>(new Rectangle(0, 0, 70, 30));
        shape.setFill(Color.LIGHTGREEN);
        shape.setStroke(Color.BLACK);
        shape.setStrokeType(StrokeType.INSIDE);
        
        // create vertical box for title and description
        labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));
        
        // hier werden die Koordinaten updated. Das brauch ich hier damit ich die Kardinalität der Beiziehung richtig plazieren kann.
        labelVBox.setOnMouseDragged(e -> updateNodeBounds());
        
        ColumnConstraints colmn = new ColumnConstraints();
        colmn.setHgrow(Priority.SOMETIMES);
        colmn.setMinWidth(10);
        colmn.setPrefWidth(10);
        colmn.setMaxWidth(10);
        gridPane.getColumnConstraints().add(colmn);
        gridPane.getColumnConstraints().add(new ColumnConstraints());
        
        
        // ensure shape and labels are resized to fit this visual
        shape.prefWidthProperty().bind(widthProperty());
        shape.prefHeightProperty().bind(heightProperty());
        labelVBox.prefWidthProperty().bind(widthProperty());
        labelVBox.prefHeightProperty().bind(heightProperty());

        // create title text
        titleText = new Text();
        titleText.setTextOrigin(VPos.TOP);
        titleText.setFont(new Font(15));
        
        InnerShadow innerShadow = new InnerShadow();
        Separator separator = new Separator();
		separator.setEffect(innerShadow);
		separator.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator.setMinWidth(1);
        
        Separator separator2 = new Separator();
		separator2.setEffect(innerShadow);
		separator2.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator2.setMinWidth(1);
		
        // vertically lay out title and description
        labelVBox.getChildren().addAll(titleText, separator, gridPane, separator2);

        // ensure title is always visible (see also #computeMinWidth(double) and
        // #computeMinHeight(double) methods)
        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        // wrap shape and VBox in Groups so that their bounds-in-parent is
        // considered when determining the layout-bounds of this visual
        initAttributeListForClassDiagram();
        getChildren().addAll(new Group(shape), new Group(labelVBox));
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
        return Math.max(minWidth+52, titleText.getLayoutBounds().getWidth() + HORIZONTAL_PADDING * 2);
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
    
    private void updateNodeBounds() {
    	myNode.setBounds(new Rectangle(getBoundsInParent().getMinX(), getBoundsInParent().getMinY(), getBoundsInParent().getWidth(), getBoundsInParent().getHeight()));
    }

    //---------------------------------------------------------------------------
    // ### Basic Accessors
    //---------------------------------------------------------------------------

    public GeometryNode<?> getGeometryNode() {
        return shape;
    }

    public Text getTitleText() {
        return titleText;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public void setTitle(String title) {
        this.titleText.setText(title);
    }
    
    public List<EOAttribute> getAttributeList() {
		return attributeList;
	}
}