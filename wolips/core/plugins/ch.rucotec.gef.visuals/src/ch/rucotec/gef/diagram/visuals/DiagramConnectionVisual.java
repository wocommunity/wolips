package ch.rucotec.gef.diagram.visuals;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.StraightRouter;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

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
    
    
//    public DiagramConnectionVisual() {
//        ArrowHead endDecoration = new ArrowHead();
//
//        endDecoration.setFill(Color.BLACK);
//        setEndDecoration(endDecoration);
//    }
    
    public DiagramConnectionVisual(int sourceToTargetCardinality, int targetToSourceCardinality) {
    	
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
    	
    	setRouter(getRouter(orthogonal));
    }
    
    public IConnectionRouter getRouter(boolean isOrthogonal) {
    	return isOrthogonal ? orthogonalRouter : straightRouter;
    }
}