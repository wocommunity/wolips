package ch.rucotec.wolips.eomodeler.core.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * This class provides the {@link PropertyChangeSupport} for the Diagramitems
 *
 * <br/>(documented by GEF)
 */
public class AbstractDiagramItem implements Serializable {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
    /**
     * Generated UUID
     */
    private static final long serialVersionUID = -2558628513984118991L;

    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}