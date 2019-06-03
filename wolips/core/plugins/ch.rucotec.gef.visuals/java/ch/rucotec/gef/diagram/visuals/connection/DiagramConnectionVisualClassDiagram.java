package ch.rucotec.gef.diagram.visuals.connection;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DiagramConnectionVisualClassDiagram extends AbstractDiagramConnectionVisual {
	
	/**
   	 * Creates a {@link Polygon} for a Extends cardinality look alike symbol.
   	 * 
   	 <PRE>
   		+----------------+  
		|       ,,-.     | 
		|      /   |     | 
		|     /    |     | 
		|    /     |     | 
		|    \     |     | 
		|     \    |     | 
		|      \   |     |  
		|       ``-'     |	 
		+----------------+
        </PRE>
   	 * 
   	 * @author Savas Celik
   	 *
   	 */
    public static class Extends extends Polygon{
    	public Extends() {
			super(0,0,15,15,15,-15,0,0);
			setFill(Color.WHITE);
			setStroke(Color.BLACK);
		}
    }
    
    /**
     * Creates a {@link Polyline} for an Unidirectional association look alike symbol.
     * 
     * 	 <PRE>
   	 	+----------------+  
		|       ,        | 
		|      /         | 
		|     /          | 
		|    /           | 
		|    \           | 
		|     \          | 
		|      \         |  
		|       `        |	 
		+----------------+
     *   </PRE>
     *   
   	 * @author Savas Celik
     *
     */
    public static class Arrow extends Polyline{
    	public Arrow() {
			super(0,0,11,11,0,0,11,-11);
		}
    }
	
    private Text targetToSource;
    private Text sourceToTarget;

	public DiagramConnectionVisualClassDiagram(DiagramConnection diagramCon) {
		super(diagramCon);
	}

	@Override
	public void decorateConnection(int sourceToTargetCardinality, int targetToSourceCardinality) {
    	
    	if (sourceToTargetCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
    		sourceToTarget = new Text("0..*");
			setEndDecoration(new HBox(sourceToTarget));
    	} else if (sourceToTargetCardinality == DiagramConnection.TOMANY) {
    		sourceToTarget = new Text("1..*");
			setEndDecoration(new HBox(sourceToTarget));
    	} else if (sourceToTargetCardinality == DiagramConnection.EXTENDS) {
    		// generates the inheritance "<I" symbol
    		Extends extending = new Extends();
    		setEndDecoration(extending);
    	} else if (sourceToTargetCardinality == DiagramConnection.TOONE) {
    		sourceToTarget = new Text("1");
			setEndDecoration(new HBox(sourceToTarget));
    	} else if (sourceToTargetCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
    		sourceToTarget = new Text("0..1");
			setEndDecoration(new HBox(sourceToTarget));
    	}
    	
    	if (targetToSourceCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
			targetToSource = new Text("0..*");
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == DiagramConnection.TOMANY) {
    		targetToSource = new Text("1..*");
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
    		targetToSource = new Text("0..1");
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == DiagramConnection.TOONE) {
    		targetToSource = new Text("1");
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == DiagramConnection.NONE) {
    		targetToSource = new Text("");
    	}
    	
    	// Unidirectional
    	if (sourceToTargetCardinality == DiagramConnection.TOONE && targetToSourceCardinality == DiagramConnection.NONE) {
    		sourceToTarget = new Text("1");
			Arrow arrow = new Arrow();
			arrow.setTranslateY(-12.2);
			arrow.setTranslateX(-1);
			setEndDecoration(new HBox(arrow, sourceToTarget));
    	}
    	if (sourceToTarget != null) {
    		sourceToTarget.setTranslateX(10);
    		sourceToTarget.setFont(new Font(16));
    	}
    	if (targetToSource != null) {
    		targetToSource.setTranslateX(10);
    		targetToSource.setFont(new Font(16));
    	}
    }

	/**
     * this methode makes sure that the cardinalities are always readable.
     */
	@Override
	public void refreshDecoration() {
    	if (getDiagramCon() != null && targetToSource != null && sourceToTarget!= null) {
    		int endPointX = (int)getEndPoint().x;
    		int endPointY = (int)getEndPoint().y;
    		int targetX = (int) getDiagramCon().getTarget().getBounds().getX();
    		int targetY = (int)getDiagramCon().getTarget().getBounds().getY();
    		int targetWidth = (int)getDiagramCon().getTarget().getBounds().getWidth();
    		int targetHeight = (int)getDiagramCon().getTarget().getBounds().getHeight();
    		
    		int startPointX = (int)getStartPoint().x;
    		int startPointY = (int)getStartPoint().y;
    		int sourceX = (int) getDiagramCon().getSource().getBounds().getX();
    		int sourceY = (int)getDiagramCon().getSource().getBounds().getY();
    		int sourceWidth = (int)getDiagramCon().getSource().getBounds().getWidth();
    		int sourceHeight = (int)getDiagramCon().getSource().getBounds().getHeight();
    		
    		int paddingSize = 10;
    		
    		if (startPointX <= sourceX && startPointY <= (sourceY + sourceHeight)) {
	    		// links
	    		targetToSource.setRotate(180);
	    		if (!targetToSource.getText().equals("1")) {
		    		targetToSource.setTranslateY(0);
		    		targetToSource.setTranslateX(paddingSize);
	    		}
//	    		System.out.println("links");
	    	} else if (startPointX <= (sourceX + sourceWidth) && startPointX >= sourceX && startPointY <= sourceY) {
	    		// oben
	    		targetToSource.setRotate(90);
	    		if (!targetToSource.getText().equals("1")) {
	    			targetToSource.setTranslateY(paddingSize);
	    			targetToSource.setTranslateX(0);
	    		}
//	    		System.out.println("oben");
	    	} else if (startPointX >= (sourceX + sourceWidth) && startPointY >= sourceY && startPointY <= (sourceY + sourceHeight)) {
	    		// rechts
	    		targetToSource.setRotate(0);
	    		targetToSource.setTranslateY(0);
	    		targetToSource.setTranslateX(paddingSize);
//	    		System.out.println("rechts");
	    	} else if (startPointX >= sourceX && startPointX <= (sourceX + sourceWidth) && startPointY+1 >= (sourceY + sourceHeight)) {
	    		// unten
	    		targetToSource.setRotate(270);
	    		if (!targetToSource.getText().equals("1")) {
		    		targetToSource.setTranslateY(paddingSize);
		    		targetToSource.setTranslateX(0);
	    		}
//	    		System.out.println("unten");
	    	}
	    	
	    	if (endPointX <= targetX && endPointY <= (targetY + targetHeight)) {
	    		// links
	    		sourceToTarget.setRotate(180);
	    		if (!sourceToTarget.getText().equals("1")) {
		    		sourceToTarget.setTranslateY(0);
		    		sourceToTarget.setTranslateX(paddingSize);
	    		}
//	    		System.out.println("links");
	    	} else if (endPointX <= (targetX + targetWidth) && endPointX >= targetX && endPointY <= targetY) {
	    		// oben
	    		sourceToTarget.setRotate(90);
	    		if (!sourceToTarget.getText().equals("1")) {
	    			sourceToTarget.setTranslateY(paddingSize);
	    			sourceToTarget.setTranslateX(0);
	    		}
//	    		System.out.println("oben");
	    	} else if (endPointX >= (targetX + targetWidth) && endPointY >= targetY && endPointY <= (targetY + targetHeight)) {
	    		// rechts
	    		sourceToTarget.setRotate(0);
	    		sourceToTarget.setTranslateY(0);
	    		sourceToTarget.setTranslateX(paddingSize);
//	    		System.out.println("rechts");
	    	} else if (endPointX >= targetX && endPointX <= (targetX + targetWidth) && endPointY >= (targetY + targetHeight)) {
	    		// unten
	    		sourceToTarget.setRotate(270);
	    		if (!sourceToTarget.getText().equals("1")) {
		    		sourceToTarget.setTranslateY(paddingSize);
		    		sourceToTarget.setTranslateX(0);
	    		}
//	    		System.out.println("unten");
	    	}
    	}
	}
	
}
