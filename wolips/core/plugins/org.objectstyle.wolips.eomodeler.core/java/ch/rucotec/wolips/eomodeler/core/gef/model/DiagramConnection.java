package ch.rucotec.wolips.eomodeler.core.gef.model;

/**
 * Used to connect {@link DiagramNode} with each other and set their cardinalities.
 * 
 * <p>Example code:</p>
 * <pre>
 * <code>
 * DiagramConnection dConnection = new DiagramConnection(DiagramType.CLASSDIAGRAM);<br/>
 * dConnection.connect(diagramNode1, diagramNode2);
 * </code>
 * </pre>
 * @author celik
 *
 */
public class DiagramConnection extends AbstractDiagramItem {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
    private static final long serialVersionUID = 6065237357753406466L;
	public static final int TOONE = 10;
	public static final int TOMANY = 20;
	public static final int OPTIONAL = 1;

	private int sourceToTargetCardinality;
	private int targetToSourceCardinality;
	private DiagramType diagramType;
    private DiagramNode source;
    private DiagramNode target;
    private boolean connected;
    
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
    
    /**
     * Generates a DiagramConnection for the given {@link DiagramType} the Type is needed
     * to specify how the cardinalities look like.
     * @param diagramType
     */
	public DiagramConnection(DiagramType diagramType) {
		this.diagramType = diagramType;
	}
	
	public DiagramConnection() {

	}

	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	/**
	 * Connects two {@link DiagramNode}s.
	 * @param source - source DiagramNode
	 * @param target - target DiagramNode.
	 */
    public void connect(DiagramNode source, DiagramNode target) {
        if (source == null || target == null || source == target) {
            throw new IllegalArgumentException();
        }
        disconnect();
        this.source = source;
        this.target = target;
        reconnect();
    }

    /**
     * Disconnects the {@link DiagramNode}s.
     */
    public void disconnect() {
        if (connected) {
            source.removeOutgoingConnection(this);
            target.removeIncomingConnection(this);
            connected = false;
        }
    }
    
    public void reconnect() {
        if (!connected) {
            source.addOutgoingConnection(this);
            target.addIncomingConnection(this);
            connected = true;
        }
    }

	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------
    
    public DiagramNode getSource() {
        return source;
    }

    public DiagramNode getTarget() {
        return target;
    }
    
    public void setSource(DiagramNode source) {
        this.source = source;
    }

    public void setTarget(DiagramNode target) {
        this.target = target;
    }
    
    public void setCardinalities(int sourceToTargetCardinality, int targetToSourceCardinality) {
		this.sourceToTargetCardinality = sourceToTargetCardinality;
		this.targetToSourceCardinality = targetToSourceCardinality;
	}

    public int getSourceToTargetCardinality() {
		return sourceToTargetCardinality;
	}

	public int getTargetToSourceCardinality() {
		return targetToSourceCardinality;
	}


	public DiagramType getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}
}
