package ch.rucotec.gef.diagram.visuals;

import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.StraightRouter;

import ch.rucotec.gef.diagram.model.DiagramConnection;
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
    private Text toOne;
    private Text toMany;
    
    public DiagramConnectionVisual() {
        ArrowHead endDecoration = new ArrowHead();
        
        endDecoration.setFill(Color.BLACK);
//        Text TODO
        List<String> list = Font.getFamilies();
        
        Text toOne =  new Text("||");
        toOne.setFont(new Font(list.get(184),23));
        toOne.setTranslateY(toOne.getTranslateY()-13);
        toOne.setTranslateX(toOne.getTranslateX()+5);
        
        Text toMany =  new Text(">|");
        fontnumber = 0;
        // 33, 
        toMany.setFont(new Font(list.get(184),23));
        toMany.setOnMouseClicked( e-> {
        	fontnumber++;
        	toMany.setFont(new Font(list.get(fontnumber),23));
        	System.out.println(list.get(fontnumber) + " " + fontnumber);
        	orthogonal = !orthogonal;
        	setRouter(getRouter(orthogonal));
        });
        
        toMany.setTranslateY(toMany.getTranslateY()-15);
        
        
        HBox hboxStart = new HBox();
        hboxStart.getChildren().addAll(toOne);
        
        HBox hboxEnd = new HBox();
        hboxEnd.getChildren().addAll(toMany);
        
        setStartDecoration(hboxStart);
        setEndDecoration(hboxEnd);
        setRouter(getRouter(orthogonal));
        
    }
    
    public void temp() {
    	 List<String> list = Font.getFamilies();
         
//       Text toOne =  new Text("||");
       toOne.setFont(new Font(list.get(184),23));
       toOne.setTranslateY(toOne.getTranslateY()-13);
       toOne.setTranslateX(toOne.getTranslateX()+5);
       
//       Text toMany =  new Text(">|");
       fontnumber = 0;
       // 33, 
       toMany.setFont(new Font(list.get(184),23));
       toMany.setOnMouseClicked( e-> {
       	fontnumber++;
       	toMany.setFont(new Font(list.get(fontnumber),23));
       	System.out.println(list.get(fontnumber) + " " + fontnumber);
       	orthogonal = !orthogonal;
       	setRouter(getRouter(orthogonal));
       });
       
       toMany.setTranslateY(toMany.getTranslateY()-15);
       
       
       HBox hboxStart = new HBox();
       hboxStart.getChildren().addAll(toOne);
       
       HBox hboxEnd = new HBox();
       hboxEnd.getChildren().addAll(toMany);
       
       setStartDecoration(hboxStart);
       setEndDecoration(hboxEnd);
       setRouter(getRouter(orthogonal));
    }
    
    public DiagramConnectionVisual(int sourceToTargetCardinality, int targetToSourceCardinality) {
        if (sourceToTargetCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
        	toMany =  new Text(">O");
        } else if (sourceToTargetCardinality == DiagramConnection.TOMANY) {
        	toMany =  new Text(">|");
        } else if (sourceToTargetCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
        	toMany =  new Text("|O");
        } else if (sourceToTargetCardinality == DiagramConnection.TOONE) {
        	toMany =  new Text("||");
        }
        
        if (targetToSourceCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
        	toOne =  new Text(">O");
        } else if (targetToSourceCardinality == DiagramConnection.TOMANY) {
        	toOne =  new Text(">|");
        } else if (targetToSourceCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
        	toOne =  new Text("|O");
        } else if (targetToSourceCardinality == DiagramConnection.TOONE) {
        	toOne =  new Text("||");
        }
        
        temp();
    }
    
    public IConnectionRouter getRouter(boolean isOrthogonal) {
    	return isOrthogonal ? orthogonalRouter : straightRouter;
    }
}