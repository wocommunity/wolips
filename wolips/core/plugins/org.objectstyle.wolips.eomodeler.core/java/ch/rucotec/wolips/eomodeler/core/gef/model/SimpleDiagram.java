package ch.rucotec.wolips.eomodeler.core.gef.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * The SimpleDiagram contains the list of children, be it nodes or connection.
 */
public class SimpleDiagram extends AbstractDiagramItem {

    /**
     * Generated UUID
     */
    private static final long serialVersionUID = 4667064215236604843L;

    public static final String PROP_CHILD_ELEMENTS = "childElements";

    private List<AbstractDiagramItem> childElements = Lists.newArrayList();

    public void addChildElement(AbstractDiagramItem node) {
        childElements.add(node);
        pcs.firePropertyChange(PROP_CHILD_ELEMENTS, null, node);
    }

    public void addChildElement(AbstractDiagramItem node, int idx) {
        childElements.add(idx, node);
        pcs.firePropertyChange(PROP_CHILD_ELEMENTS, null, node);
    }

    public List<AbstractDiagramItem> getChildElements() {
        return childElements;
    }

    public void removeChildElement(AbstractDiagramItem node) {
        childElements.remove(node);
        pcs.firePropertyChange(PROP_CHILD_ELEMENTS, node, null);
    }
}