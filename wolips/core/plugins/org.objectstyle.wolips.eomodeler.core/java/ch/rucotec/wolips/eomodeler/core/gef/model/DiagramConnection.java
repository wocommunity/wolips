package ch.rucotec.wolips.eomodeler.core.gef.model;

public class DiagramConnection extends AbstractDiagramItem {
	
	public static final int TOONE = 10;
	public static final int TOMANY = 20;
	public static final int OPTIONAL = 1;
	
	private int sourceToTargetCardinality;
	private int targetToSourceCardinality;
	private DiagramType diagramType;
//	private DiagramConnectionPart part;
	
	public DiagramConnection(DiagramType diagramType) {
		this.diagramType = diagramType;
	}
	
	public DiagramConnection() {

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
	
//	public DiagramConnectionPart getPart() {
//		return part;
//	}
//
//	public void setPart(DiagramConnectionPart part) {
//		this.part = part;
//	}


	/**
     * Generated UUID
     */
    private static final long serialVersionUID = 6065237357753406466L;

    private DiagramNode source;
    private DiagramNode target;
    private boolean connected;

    public void connect(DiagramNode source, DiagramNode target) {
        if (source == null || target == null || source == target) {
            throw new IllegalArgumentException();
        }
        disconnect();
        this.source = source;
        this.target = target;
        reconnect();
    }

    public void disconnect() {
        if (connected) {
            source.removeOutgoingConnection(this);
            target.removeIncomingConnection(this);
            connected = false;
        }
    }

    public DiagramNode getSource() {
        return source;
    }

    public DiagramNode getTarget() {
        return target;
    }

    public void reconnect() {
        if (!connected) {
            source.addOutgoingConnection(this);
            target.addIncomingConnection(this);
            connected = true;
        }
    }

    public void setSource(DiagramNode source) {
        this.source = source;
    }

    public void setTarget(DiagramNode target) {
        this.target = target;
    }
}
