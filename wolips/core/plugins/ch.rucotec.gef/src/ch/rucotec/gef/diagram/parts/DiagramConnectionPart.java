package ch.rucotec.gef.diagram.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import ch.rucotec.gef.diagram.visuals.DiagramConnectionVisual;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramType;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;

/**
 * The mind map connection part is used the controller for th
 * {@link DiagramConnection}. It create the {@link DiagramConnectionVisual}
 * including the anchors for the connection.
 *
 */
public class DiagramConnectionPart extends AbstractContentPart<Connection> {

    private static final String START_ROLE = "START";
    private static final String END_ROLE = "END";

    @Override
    protected void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
        // find a anchor provider, which must be registered in the module
        // be aware to use the right interfaces (Provider is used a lot)
        @SuppressWarnings("serial")
        Provider<? extends IAnchor> adapter = anchorage
                .getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
                }));
        if (adapter == null) {
            throw new IllegalStateException("No adapter  found for <" + anchorage.getClass() + "> found.");
        }
        IAnchor anchor = adapter.get();
//        System.out.println(anchorage);
        if (role.equals(START_ROLE)) {
            getVisual().setStartAnchor(anchor);
        } else if (role.equals(END_ROLE)) {
            getVisual().setEndAnchor(anchor);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Override
    protected Connection doCreateVisual() {
    	DiagramConnection diagramCon = getContent();
//    	diagramCon.setPart(this);
        return new DiagramConnectionVisual(diagramCon);
    }

    @Override
    protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
        if (role.equals(START_ROLE)) {
            getVisual().setStartPoint(getVisual().getStartPoint());
        } else if (role.equals(END_ROLE)) {
            getVisual().setEndPoint(getVisual().getEndPoint());
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Override
    protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
        SetMultimap<Object, String> anchorages = HashMultimap.create();

        anchorages.put(getContent().getSource(), START_ROLE);
        anchorages.put(getContent().getTarget(), END_ROLE);

        return anchorages;
    }

    @Override
    protected List<? extends Object> doGetContentChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void doRefreshVisual(Connection visual) {
    	if(visual instanceof DiagramConnectionVisual) {
    		DiagramConnectionVisual connVisual = (DiagramConnectionVisual) visual;
    		if (connVisual.getDiagramCon() != null && connVisual.getDiagramCon().getDiagramType() == DiagramType.CLASSDIAGRAM) {
    			connVisual.refreshDecoration();
    		}
    	}
    }

    @Override
    public DiagramConnection getContent() {
        return (DiagramConnection) super.getContent();
    }
}