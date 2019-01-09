package ch.rucotec.gef.diagram.visuals;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import com.google.common.collect.Lists;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DiagramNodeVisual extends Region {

    private static final double HORIZONTAL_PADDING = 20d;
    private static final double VERTICAL_PADDING = 10d;
    private static final double VERTICAL_SPACING = 5d;
    private Double minWidth;

    private Text titleText;
    private TextFlow descriptionFlow;
    private Text descriptionText;
    private GeometryNode<Rectangle> shape;
    private VBox labelVBox;
    
    //SAVAS: Das hier muss noch angepasst werden
    private DiagramNode myNode;
    private List<EOAttribute> attributeList;
    private GridPane gridPane = new GridPane();
    
//    private final ObservableList<TableAttribute> data = FXCollections.observableArrayList();

    public void initAttributeListForERDiagram() {
    	Label lblAttributeName = null;
    	Label lblAttributeAllowsNull = null;
    	List<EOAttribute> attributeListFilteredPK = attributeList;
    	
		for (int i = 0; i < attributeListFilteredPK.size(); i++) {
			EOAttribute attribute = attributeListFilteredPK.get(i);
			
			lblAttributeName = new Label(attribute.getName());
			lblAttributeName.setPadding(new Insets(0, 0, 0, 10));
			
			if (attribute.isAllowsNull() != null) {
				lblAttributeAllowsNull = new Label(attribute.isAllowsNull() ? "O" : "Ø");
			} else {
				lblAttributeAllowsNull = new Label("Ø");
			}
			
			if (attribute.isPrimaryKey()) {
				lblAttributeName.setFont(Font.font(lblAttributeName.getFont().getFamily(), FontWeight.EXTRA_BOLD, 16));
				lblAttributeName.setUnderline(true);
				attributeListFilteredPK.remove(i);
				i--;
				gridPane.add(lblAttributeName, 0, 1);
				gridPane.add(lblAttributeAllowsNull, 1, 1);
			} else {
				lblAttributeName.setUnderline(false);
				gridPane.add(lblAttributeName, 0, i+2);
				gridPane.add(lblAttributeAllowsNull, 1, i+2);
			}
		}
	}
    
    public void initAttributeListForClassDiagram() {
    	Label lblAttributeName = null;
    	Label lblRelationshipName = null;
    	List<EORelationship> relationshipList = myNode.getRelationshipsList();
    	
    	for (int i = 0; i < attributeList.size(); i++) {
			EOAttribute attribute = attributeList.get(i);
			
			if (attribute.isClassProperty()) { 
				lblAttributeName = new Label(attribute.getName() + " : " + attribute.getJavaClassName());
				
				gridPane.add(new Label("-"), 0, i+1);
				gridPane.add(lblAttributeName, 1, i+1);
			}
		}
    	
    	for (int i = 0; i < relationshipList.size(); i++) {
    		EORelationship relationship = relationshipList.get(i);
    		if (relationship.isToMany()) {
    			lblRelationshipName = new Label(relationship.getName() + " : " + relationship.getDestination().getClassNameWithoutPackage()+"[]");
    		} else if (relationship.isToOne()) {
    			lblRelationshipName = new Label(relationship.getName() + " : " + relationship.getDestination().getClassNameWithoutPackage());
    		}
    		
    		gridPane.add(new Label("-"), 0, GridPane.getRowIndex(lblAttributeName) + i + 1);
    		gridPane.add(lblRelationshipName, 1, GridPane.getRowIndex(lblAttributeName) + i + 1);
    	}
    }

	public List<EOAttribute> getAttributeList() {
		return attributeList;
	}

//	public DiagramNodeVisual() {
//        // create background shape
////        shape = new GeometryNode<>(new RoundedRectangle(0, 0, 70, 30, 8, 8));
//        shape.setFill(Color.LIGHTGREEN);
//        shape.setStroke(Color.BLACK);
//
//        // create vertical box for title and description
//        labelVBox = new VBox(VERTICAL_SPACING);
//        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));
//
//        // ensure shape and labels are resized to fit this visual
//        shape.prefWidthProperty().bind(widthProperty());
//        shape.prefHeightProperty().bind(heightProperty());
//        labelVBox.prefWidthProperty().bind(widthProperty());
//        labelVBox.prefHeightProperty().bind(heightProperty());
//
//        // create title text
//        titleText = new Text();
//        titleText.setTextOrigin(VPos.TOP);
//
//        // create description text
//        descriptionText = new Text();
//        descriptionText.setTextOrigin(VPos.TOP);
//
//        // use TextFlow to enable wrapping of the description text within the
//        // label bounds
//        descriptionFlow = new TextFlow(descriptionText);
//        // only constrain the width, so that the height is computed in
//        // dependence on the width
//        descriptionFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));
//
//        // vertically lay out title and description
//        labelVBox.getChildren().addAll(titleText, descriptionFlow);
//
//        // ensure title is always visible (see also #computeMinWidth(double) and
//        // #computeMinHeight(double) methods)
//        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
//
//        // wrap shape and VBox in Groups so that their bounds-in-parent is
//        // considered when determining the layout-bounds of this visual
//        getChildren().addAll(new Group(shape), new Group(labelVBox));
//    }
    
    public DiagramNodeVisual(DiagramNode node, DiagramType diagramTyp) {
    	this.myNode = node;
    	this.attributeList = node.getAttributeList();
    	if (diagramTyp == DiagramType.ERDIAGRAM) {
    		createERDiagram();
    	} else if (diagramTyp == DiagramType.CLASSDIAGRAM) {
    		createClassDiagram();
    	}
    }
    
    private void updateNodeBounds() {
    	myNode.setBounds(new Rectangle(getBoundsInParent().getMinX(), getBoundsInParent().getMinY(), getBoundsInParent().getWidth(), getBoundsInParent().getHeight()));
    }
    
    public void createERDiagram() {
    	// create background shape
        shape = new GeometryNode<>(new Rectangle(0, 0, 70, 30));
        shape.setFill(Color.LIGHTGREEN);
        shape.setStroke(Color.BLACK);
        shape.setStrokeType(StrokeType.INSIDE);
        
        // create vertical box for title and description
        labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));

        // TODO Savas activate GC
        labelVBox.setOnMouseReleased(e -> {System.gc();});
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
        Font font = new Font(15);
        titleText.setFont(font);
        
        InnerShadow innerShadow = new InnerShadow();
        
        Separator separator2 = new Separator();
		separator2.setEffect(innerShadow);
		separator2.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator2.setMinWidth(1);
        

        // create description text
        descriptionText = new Text();
        descriptionText.setTextOrigin(VPos.TOP);

        // use TextFlow to enable wrapping of the description text within the
        // label bounds
        descriptionFlow = new TextFlow(descriptionText);
        // only constrain the width, so that the height is computed in
        // dependence on the width
        descriptionFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));
        

        // vertically lay out title and description
        labelVBox.getChildren().addAll(titleText,separator2, gridPane);

        // ensure title is always visible (see also #computeMinWidth(double) and
        // #computeMinHeight(double) methods)
        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        // wrap shape and VBox in Groups so that their bounds-in-parent is
        // considered when determining the layout-bounds of this visual
        initAttributeListForERDiagram();
        getChildren().addAll(new Group(shape), new Group(labelVBox));
    }
    
    public void createClassDiagram() {
    	// create background shape
        shape = new GeometryNode<>(new Rectangle(0, 0, 70, 30));
        shape.setFill(Color.LIGHTGREEN);
        shape.setStroke(Color.BLACK);
        shape.setStrokeType(StrokeType.INSIDE);
        
        // create vertical box for title and description
        labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));
        
        // TODO Savas activate GC
        labelVBox.setOnMouseReleased(e -> {System.gc();});
        
        // XXX hier werden die Koordinaten updated. Das brauch ich hier damit ich die Kardinalität der Beiziehung richtig plaziere.
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
        Font font = new Font(15);
        titleText.setFont(font);
        
        InnerShadow innerShadow = new InnerShadow();
        
        Separator separator = new Separator();
		separator.setEffect(innerShadow);
		separator.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator.setMinWidth(1);
        
        Separator separator2 = new Separator();
		separator2.setEffect(innerShadow);
		separator2.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator2.setMinWidth(1);
        

        // create description text
        descriptionText = new Text();
        descriptionText.setTextOrigin(VPos.TOP);

        // use TextFlow to enable wrapping of the description text within the
        // label bounds
        descriptionFlow = new TextFlow(descriptionText);
        // only constrain the width, so that the height is computed in
        // dependence on the width
        descriptionFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));
        

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

    public Text getDescriptionText() {
        return descriptionText;
    }

    public GeometryNode<?> getGeometryNode() {
        return shape;
    }

    public Text getTitleText() {
        return titleText;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public void setDescription(String description) {
        this.descriptionText.setText(description);
    }

    public void setTitle(String title) {
        this.titleText.setText(title);
    }
}