package ch.rucotec.gef.diagram.visuals.node;

import java.util.List;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DiagramNodeVisualClassDiagram extends AbstractDiagramNodeVisual {

	public DiagramNodeVisualClassDiagram(DiagramNode node) {
		super(node);
	}

	@Override
	public void initAttributeList() {
    	Label lblAttributeName = null;
    	Label lblRelationshipName = null;
    	List<EORelationship> relationshipList = getMyNode().getRelationshipsList();
    	
    	// Adding Attributes to the GridPane
    	for (int i = 0; i < getAttributeList().size(); i++) {
			EOAttribute attribute = getAttributeList().get(i);
			
			if (attribute.isClassProperty()) { 
				lblAttributeName = new Label(attribute.getName() + " : " + attribute.getJavaClassName());
				
				getGridPane().add(new Label("-"), 0, i+1);
				getGridPane().add(lblAttributeName, 1, i+1);
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
    			getGridPane().add(new Label("-"), 0, GridPane.getRowIndex(lblAttributeName) + i + 1);
    			getGridPane().add(lblRelationshipName, 1, GridPane.getRowIndex(lblAttributeName) + i + 1);
    		} else {
    			getGridPane().add(new Label("-"), 0, 10 + i + 1);
    			getGridPane().add(lblRelationshipName, 1, 10 + i + 1);
    		}
    	}
    }

	@Override
	public void createVisual() {
    	// create background shape
		GeometryNode<?> shape = new GeometryNode<>(new Rectangle(0, 0, 70, 30));
        shape.setFill(Color.LIGHTGREEN);
        shape.setStroke(Color.BLACK);
        shape.setStrokeType(StrokeType.INSIDE);
        
        // create vertical box for title and description
        VBox labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));
        
        // hier werden die Koordinaten geupdatet. Das brauch ich hier damit ich die Kardinalität der Beiziehung richtig plazieren kann.
        labelVBox.setOnMouseDragged(e -> updateNodeBounds());
        
        ColumnConstraints colmn = new ColumnConstraints();
        colmn.setHgrow(Priority.SOMETIMES);
        colmn.setMinWidth(10);
        colmn.setPrefWidth(10);
        colmn.setMaxWidth(10);
        getGridPane().getColumnConstraints().add(colmn);
        getGridPane().getColumnConstraints().add(new ColumnConstraints());
        
        
        // ensure shape and labels are resized to fit this visual
        shape.prefWidthProperty().bind(widthProperty());
        shape.prefHeightProperty().bind(heightProperty());
        labelVBox.prefWidthProperty().bind(widthProperty());
        labelVBox.prefHeightProperty().bind(heightProperty());

        // create title text
        Text title = new Text();
        title.setTextOrigin(VPos.TOP);
        title.setFont(new Font(15));
        
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
        labelVBox.getChildren().addAll(title, separator, getGridPane(), separator2);

        // ensure title is always visible (see also #computeMinWidth(double) and
        // #computeMinHeight(double) methods)
        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        initAttributeList();
        setGeometryNode(shape);
        setLabelVBox(labelVBox);
        setTitle(title);
        
        // wrap shape and VBox in Groups so that their bounds-in-parent is
        // considered when determining the layout-bounds of this visual
        getChildren().addAll(new Group(shape), new Group(labelVBox));
    }

}
