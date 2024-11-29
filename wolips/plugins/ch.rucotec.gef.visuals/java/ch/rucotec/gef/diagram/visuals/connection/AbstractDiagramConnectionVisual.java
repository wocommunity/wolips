package ch.rucotec.gef.diagram.visuals.connection;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.StraightRouter;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;

/**
 * This Class is responsible for the visual effects of a {@link DiagramConnection}.
 * 
 * @author Savas Celik
 *
 */
public abstract class AbstractDiagramConnectionVisual extends Connection {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
    
    private StraightRouter straightRouter = new StraightRouter();
    private OrthogonalRouter orthogonalRouter = new OrthogonalRouter();
    private boolean orthogonal = true;
    private DiagramConnection diagramCon;
    
    
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
    
    /**
     * Constructor which generates the visual elements for a {@link DiagramConnection}
     * @param diagramCon - which describes a {@link DiagramConnection} object.
     */
    public AbstractDiagramConnectionVisual(DiagramConnection diagramCon) {
    	int sourceToTargetCardinality = diagramCon.getSourceToTargetCardinality();
    	int targetToSourceCardinality = diagramCon.getTargetToSourceCardinality();
    	this.diagramCon = diagramCon;
    	
    	decorateConnection(sourceToTargetCardinality, targetToSourceCardinality);
    	setRouter(getRouter(orthogonal));
    }
    
	//---------------------------------------------------------------------------
	// ### Abstract Methods
	//---------------------------------------------------------------------------
    
    /**
     * This method decorates the start and the ending of a connection with the given cardinalities. 
     * 
     * @param sourceToTargetCardinality - the cardinality which describes the relationship from source to target.
     * @param targetToSourceCardinality - the cardinality which describes the relationship from target to source.
     */
    public abstract void decorateConnection(int sourceToTargetCardinality, int targetToSourceCardinality);
    
    /**
     * This method is always called if the connection's length or direction is changed.
     */
    public abstract void refreshDecoration();
    
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
    
    /**
     * this method is always called when the connection needs to be refreshed.
     */
    @Override
    protected void refresh() {
    	refreshDecoration();
    	super.refresh();
    }
    
    /**
     * With this method you can choose between orthogonalRouter or straightRouter.
     * 
     * @param isOrthogonal - if true the connection is Orthogonal else it's straight.
     * @return a IConnectionRouter
     */
    public IConnectionRouter getRouter(boolean isOrthogonal) {
    	return isOrthogonal ? orthogonalRouter : straightRouter;
    }
    
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
    
    public DiagramConnection getDiagramCon() {
		return diagramCon;
	}
}