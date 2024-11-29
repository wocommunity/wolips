package ch.rucotec.gef.diagram.parts;

import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;
import javafx.scene.Node;

/**
 * The {@link DiagramPartsFactory} creates a Part for the mind map models, based
 * on the type of the model instance.
 * <br/>(documented by GEF)
 */
public class DiagramPartsFactory implements IContentPartFactory {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
    @Inject
    private Injector injector;

	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
    
    @Override
    public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
        if (content == null) {
            throw new IllegalArgumentException("Content must not be null!");
        }

        if (content instanceof SimpleDiagram) {
            return injector.getInstance(SimpleDiagramPart.class);
        } else if (content instanceof DiagramNode) {
            return injector.getInstance(DiagramNodePart.class);
        } else if (content instanceof DiagramConnection) {
            return injector.getInstance(DiagramConnectionPart.class);
        } else {
            throw new IllegalArgumentException("Unknown content type <" + content.getClass().getName() + ">");
        }
    }
}