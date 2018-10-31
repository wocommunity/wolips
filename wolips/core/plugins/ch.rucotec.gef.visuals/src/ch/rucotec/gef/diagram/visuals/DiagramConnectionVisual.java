package ch.rucotec.gef.diagram.visuals;

import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.StraightRouter;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DiagramConnectionVisual extends Connection {

    public static class ArrowHead extends Polygon {
        public ArrowHead() {
            super(0, 0, 10, 3, 10, -3);
        }
    }
    
    private int fontnumber;
    private StraightRouter straightRouter = new StraightRouter();
    private OrthogonalRouter orthogonalRouter = new OrthogonalRouter();
    private boolean orthogonal = true;
    private Text targetCardinality;
    private Text sourceCardinality;
    
    public DiagramConnectionVisual() {
        ArrowHead endDecoration = new ArrowHead();

        endDecoration.setFill(Color.BLACK);
        setEndDecoration(endDecoration);
    }
    
    public DiagramConnectionVisual(int sourceToTargetCardinality, int targetToSourceCardinality) {
    	if (sourceToTargetCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
    		sourceCardinality =  new Text(">O");
    	} else if (sourceToTargetCardinality == DiagramConnection.TOMANY) {
    		sourceCardinality =  new Text(">|");
    	} else if (sourceToTargetCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
    		sourceCardinality =  new Text("|O");
    	} else if (sourceToTargetCardinality == DiagramConnection.TOONE) {
    		sourceCardinality =  new Text("||");
    	}
    	
    	if (targetToSourceCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
    		targetCardinality =  new Text(">O");
    	} else if (targetToSourceCardinality == DiagramConnection.TOMANY) {
    		targetCardinality =  new Text(">|");
    	} else if (targetToSourceCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
    		targetCardinality =  new Text("|O");
    	} else if (targetToSourceCardinality == DiagramConnection.TOONE) {
    		targetCardinality =  new Text("||");
    	}
    	
    	addCardinalities();
    }
    
    private void addCardinalities() {
    	 List<String> list = Font.getFamilies();
         
       targetCardinality.setFont(new Font(list.get(184),23));
       targetCardinality.setTranslateY(targetCardinality.getTranslateY()-13);
       targetCardinality.setTranslateX(targetCardinality.getTranslateX()+5);
       
       fontnumber = 0;
       // 33, 
       sourceCardinality.setFont(new Font(list.get(184),23));
       sourceCardinality.setOnMouseClicked( e-> {
       	fontnumber++;
       	sourceCardinality.setFont(new Font(list.get(fontnumber),23));
       	System.out.println(list.get(fontnumber) + " " + fontnumber);
       	orthogonal = !orthogonal;
       	setRouter(getRouter(orthogonal));
       });
       
       sourceCardinality.setTranslateY(sourceCardinality.getTranslateY()-15);
       
       
       HBox hboxStart = new HBox();
       hboxStart.getChildren().addAll(targetCardinality);
       
       HBox hboxEnd = new HBox();
       hboxEnd.getChildren().addAll(sourceCardinality);
       
       setStartDecoration(hboxStart);
       setEndDecoration(hboxEnd);
       setRouter(getRouter(orthogonal));
    }
    
    public IConnectionRouter getRouter(boolean isOrthogonal) {
    	return isOrthogonal ? orthogonalRouter : straightRouter;
    }
}