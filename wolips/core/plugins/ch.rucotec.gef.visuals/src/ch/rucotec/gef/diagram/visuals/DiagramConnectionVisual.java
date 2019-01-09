package ch.rucotec.gef.diagram.visuals;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.StraightRouter;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramType;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DiagramConnectionVisual extends Connection {

    public static class ArrowHead extends Polygon {
        public ArrowHead() {
            super(0, 0, 10, 3, 10, -3);
        }
    }
    
    public static class CrowFoot extends Polyline {
    	public CrowFoot() {
    		super(  0, 7,  
    				0, 0,  
    				-14, 7,  
    				0, 0,
    				-10, 0,
    				0, 0,
    				-14, -7,
    				-14, -7,  
    				0, 0,
    				0, -7
    				);
    	}
    }
    
    public static class One extends Polyline {
    	public One() {
			super(0,10,0,-10);
		}
    }
    
    public static class Many extends Polyline {
    	public Many() {
    		super(-14,7, 0,0, -14,0, 0,0, -14,-7);
		}
    }
    
    public static class Zero extends Circle {
    	public Zero() {
    		super(7);
		}
    }
    
    private StraightRouter straightRouter = new StraightRouter();
    private OrthogonalRouter orthogonalRouter = new OrthogonalRouter();
    private boolean orthogonal = true;
    private DiagramConnection diagramCon;
    private Text targetToSource;
    private Text sourceToTarget;
    
    
//    public DiagramConnectionVisual() {
//        ArrowHead endDecoration = new ArrowHead();
//
//        endDecoration.setFill(Color.BLACK);
//        setEndDecoration(endDecoration);
//    }
    
    public DiagramConnectionVisual(DiagramConnection diagramCon) {
    	int sourceToTargetCardinality = diagramCon.getSourceToTargetCardinality();
    	int targetToSourceCardinality = diagramCon.getTargetToSourceCardinality();
    	DiagramType diagramType = diagramCon.getDiagramType();
    	this.diagramCon = diagramCon;
    	
    	if (diagramType == DiagramType.ERDIAGRAM) {
    		setERDiagramConnection(sourceToTargetCardinality, targetToSourceCardinality);
    	} else if (diagramType == DiagramType.CLASSDIAGRAM) {
    		setClassDiagramConnection(sourceToTargetCardinality, targetToSourceCardinality);
    	}
    	
    	setRouter(getRouter(orthogonal));
    }
   @Override
   protected void refresh() {
	   refreshDecoration();
	   super.refresh();
   }
    private void setERDiagramConnection(int sourceToTargetCardinality, int targetToSourceCardinality) {
    	if (sourceToTargetCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
    		// this generates this ">O" symbol.
    		Zero zero = new Zero();
    		Many many = new Many();
    		zero.setTranslateX(8);
    		many.setStrokeWidth(0.3);
        	Shape ZeroOrMany = Shape.union(zero, many);
        	ZeroOrMany.setFill(Color.TRANSPARENT);
        	ZeroOrMany.setStroke(Color.BLACK);
        	setEndDecoration(ZeroOrMany);
    	} else if (sourceToTargetCardinality == DiagramConnection.TOMANY) {
    		// this generates this ">|" symbol
    		One one = new One();
    		Many many = new Many();
    		Shape OneOrMany = Shape.union(one, many);
    		setEndDecoration(OneOrMany);
    	} else {
    		// this generates no symbol
    		setEndDecoration(new HBox());
    	}
    	
    	if (targetToSourceCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
    		// this generates this "|O" symbol
    		Zero zero = new Zero();
    		One one = new One();
    		zero.setFill(Color.TRANSPARENT);
    		zero.setStroke(Color.BLACK);
    		one.setTranslateX(-10);
    		one.setTranslateY(-11);
    		zero.setTranslateX(10);
    		zero.setTranslateY(-7);
    		HBox ZeroOrOne = new HBox(zero, one);
    		setStartDecoration(ZeroOrOne);
    	} else if (targetToSourceCardinality == DiagramConnection.TOONE) {
    		// this generates this "||" symbol
    		One one = new One();
    		One one2 = new One();
    		one.setTranslateX(5);
    		one2.setTranslateX(10);
    		one.setTranslateY(-11);
    		one2.setTranslateY(-11);
    		HBox oneOnlyOne = new HBox(one,one2);
    		setStartDecoration(oneOnlyOne);
    	} else {
    		// this generates no symbol
    		setStartDecoration(new HBox());
    	}
    }
    
    private void setClassDiagramConnection(int sourceToTargetCardinality, int targetToSourceCardinality) {
    	
    	if (sourceToTargetCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
    		sourceToTarget = new Text("0..*");
			sourceToTarget.setTranslateX(10);
			sourceToTarget.setFont(new Font(16));
			setEndDecoration(new HBox(sourceToTarget));
    	} else if (sourceToTargetCardinality == DiagramConnection.TOMANY) {
    		sourceToTarget = new Text("1..*");
			sourceToTarget.setTranslateX(10);
			sourceToTarget.setFont(new Font(16));
			setEndDecoration(new HBox(sourceToTarget));
    	} else {
    		sourceToTarget = new Text("");
    	}
    	
    	if (targetToSourceCardinality == (DiagramConnection.TOMANY | DiagramConnection.OPTIONAL)) {
			targetToSource = new Text("0..*");
			targetToSource.setTranslateX(10);
			targetToSource.setFont(new Font(16));
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == DiagramConnection.TOMANY) {
    		targetToSource = new Text("1..*");
			targetToSource.setTranslateX(10);
			targetToSource.setFont(new Font(16));
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == (DiagramConnection.TOONE | DiagramConnection.OPTIONAL)) {
    		targetToSource = new Text("0..1");
			targetToSource.setTranslateX(10);
			targetToSource.setFont(new Font(16));
			setStartDecoration(new HBox(targetToSource));
    	} else if (targetToSourceCardinality == DiagramConnection.TOONE) {
    		targetToSource = new Text("1");
			targetToSource.setTranslateX(10);
			targetToSource.setFont(new Font(16));
			setStartDecoration(new HBox(targetToSource));
    	} else {
    		targetToSource = new Text("");
    	}
    	
    }
    
    public void refreshDecoration() {
    	if (targetToSource != null && sourceToTarget!= null) {
    		int endPointX = (int)getEndPoint().x;
    		int endPointY = (int)getEndPoint().y;
    		int targetX = (int) diagramCon.getTarget().getBounds().getX();
    		int targetY = (int)diagramCon.getTarget().getBounds().getY();
    		int targetWidth = (int)diagramCon.getTarget().getBounds().getWidth();
    		int targetHeight = (int)diagramCon.getTarget().getBounds().getHeight();
    		
    		int startPointX = (int)getStartPoint().x;
    		int startPointY = (int)getStartPoint().y;
    		int sourceX = (int) diagramCon.getSource().getBounds().getX();
    		int sourceY = (int)diagramCon.getSource().getBounds().getY();
    		int sourceWidth = (int)diagramCon.getSource().getBounds().getWidth();
    		int sourceHeight = (int)diagramCon.getSource().getBounds().getHeight();
	    	
//    		System.out.println(diagramCon.getSource().getTitle());
    		
    		if (startPointX <= sourceX && startPointY <= (sourceY + sourceHeight)) {
	    		// links
	    		targetToSource.setRotate(180);
	    		if (!targetToSource.getText().equals("1")) {
		    		targetToSource.setTranslateY(0);
		    		targetToSource.setTranslateX(10);
	    		}
//	    		System.out.println("links");
	    	} else if (startPointX <= (sourceX + sourceWidth) && startPointX >= sourceX && startPointY <= sourceY) {
	    		//oben
	    		targetToSource.setRotate(90);
	    		if (!targetToSource.getText().equals("1")) {
	    			targetToSource.setTranslateY(10);
	    			targetToSource.setTranslateX(0);
	    		}
//	    		System.out.println("oben");
	    	} else if (startPointX >= (sourceX + sourceWidth) && startPointY >= sourceY && startPointY <= (sourceY + sourceHeight)) {
	    		// rechts
	    		targetToSource.setRotate(0);
	    		targetToSource.setTranslateY(0);
	    		targetToSource.setTranslateX(10);
//	    		System.out.println("rechts");
	    	} else if (startPointX >= sourceX && startPointX <= (sourceX + sourceWidth) && startPointY+1 >= (sourceY + sourceHeight)) {
	    		// unten
	    		targetToSource.setRotate(270);
	    		if (!targetToSource.getText().equals("1")) {
	    		targetToSource.setTranslateY(10);
	    		targetToSource.setTranslateX(0);
    		}
//	    		System.out.println("unten");
	    	}
	    	
	    	if (endPointX <= targetX && endPointY <= (targetY + targetHeight)) {
	    		// links
	    		sourceToTarget.setRotate(180);
	    		if (!sourceToTarget.getText().equals("1")) {
		    		sourceToTarget.setTranslateY(0);
		    		sourceToTarget.setTranslateX(10);
	    		}
//	    		System.out.println("links");
	    	} else if (endPointX <= (targetX + targetWidth) && endPointX >= targetX && endPointY <= targetY) {
	    		//oben
	    		sourceToTarget.setRotate(90);
	    		if (!sourceToTarget.getText().equals("1")) {
	    			sourceToTarget.setTranslateY(10);
	    			sourceToTarget.setTranslateX(0);
	    		}
//	    		System.out.println("oben");
	    	} else if (endPointX >= (targetX + targetWidth) && endPointY >= targetY && endPointY <= (targetY + targetHeight)) {
	    		// rechts
	    		sourceToTarget.setRotate(0);
	    		sourceToTarget.setTranslateY(0);
	    		sourceToTarget.setTranslateX(10);
//	    		System.out.println("rechts");
	    	} else if (endPointX >= targetX && endPointX <= (targetX + targetWidth) && endPointY >= (targetY + targetHeight)) {
	    		// unten
	    		sourceToTarget.setRotate(270);
	    		if (!sourceToTarget.getText().equals("1")) {
	    		sourceToTarget.setTranslateY(10);
	    		sourceToTarget.setTranslateX(0);
    		}
//	    		System.out.println("unten");
	    	}
    	}
    }
    
    public IConnectionRouter getRouter(boolean isOrthogonal) {
    	return isOrthogonal ? orthogonalRouter : straightRouter;
    }
    
    public DiagramConnection getDiagramCon() {
		return diagramCon;
	}
}