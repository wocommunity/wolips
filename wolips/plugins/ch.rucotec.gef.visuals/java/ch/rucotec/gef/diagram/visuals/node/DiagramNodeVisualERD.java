package ch.rucotec.gef.diagram.visuals.node;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DiagramNodeVisualERD extends AbstractDiagramNodeVisual{

	public DiagramNodeVisualERD(DiagramNode node) {
		super(node);
	}
	
	@Override
	public void initAttributeList() {
    	initForeignKeys();
    	Label lblAttributeName = null;
    	Label lblAttributeAllowsNull = null;
    	int pkCount = 0;
    	
    	for (EOAttribute pkAttribute : getAttributeList()) {
    		if (pkAttribute.isPrimaryKey() && !pkAttribute.isInherited()) {
	    		lblAttributeName = new Label(pkAttribute.getColumnName());
				
				if (pkAttribute.isAllowsNull() != null) {
					lblAttributeAllowsNull = new Label(pkAttribute.isAllowsNull() ? "O" : "Ø");
				} else {
					lblAttributeAllowsNull = new Label("Ø");
				}
				
				lblAttributeName.setFont(Font.font(lblAttributeName.getFont().getFamily(), FontWeight.EXTRA_BOLD, 16));
				lblAttributeName.setUnderline(true);
				getGridPane().add(lblAttributeName, 0, pkCount);
				getGridPane().add(lblAttributeAllowsNull, 1, pkCount);
				pkCount++;
    		}
    	}
    	
		for (int i = 0; i < getAttributeList().size(); i++) {
			EOAttribute attribute = getAttributeList().get(i);
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
				
				if(getForeignKeyAttributes().contains(attribute)) {
					lblAttributeName.setFont(Font.font(lblAttributeName.getFont().getFamily(), FontPosture.ITALIC, lblAttributeName.getFont().getSize()));
					lblAttributeName.setStyle("-fx-border-width: 0 0 1.5 0; -fx-border-style: hidden hidden dashed hidden;");
				} else {
					lblAttributeName.setUnderline(false);
				}
				getGridPane().add(lblAttributeName, 0, pkCount+i);
				getGridPane().add(lblAttributeAllowsNull, 1, pkCount+i);
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

        ColumnConstraints colmn = new ColumnConstraints();
        colmn.setHgrow(Priority.SOMETIMES);
        colmn.setMinWidth(100);
        colmn.setPrefWidth(100);
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
        
        Separator separator = new Separator();
		separator.setEffect(new InnerShadow());
		separator.setPadding(new Insets(0, -HORIZONTAL_PADDING, 0, -HORIZONTAL_PADDING));
		separator.setMinWidth(1);

        // vertically lay out title and description
        labelVBox.getChildren().addAll(title, separator, getGridPane());

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
