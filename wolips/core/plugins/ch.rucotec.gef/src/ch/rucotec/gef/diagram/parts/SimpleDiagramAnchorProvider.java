package ch.rucotec.gef.diagram.parts;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import ch.rucotec.gef.diagram.visuals.DiagramConnectionVisual;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;

/**
 * The {@link SimpleDiagramAnchorProvider} create an anchor for a
 * {@link DiagramConnectionVisual}.
 *
 * It is bound to an {@link DiagramNodePart} and will be called in the
 * {@link DiagramConnectionPart}.
 *
 */
public class SimpleDiagramAnchorProvider extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>>
        implements Provider<IAnchor> {

    // the anchor in case we already created one
    private DynamicAnchor anchor;

    @Override
    public ReadOnlyObjectProperty<IVisualPart<? extends Node>> adaptableProperty() {
        return null;
    }

    @Override
    public IAnchor get() {
        if (anchor == null) {
            // get the visual from the host (DiagramNodePart)
            Node anchorage = getAdaptable().getVisual();
            // create a new anchor instance
            anchor = new DynamicAnchor(anchorage);

            // binding the anchor reference to an object binding, which
            // recalculates the geometry when the layout bounds of
            // the anchorage are changing
            anchor.getComputationParameter(AnchorageReferenceGeometry.class).bind(new ObjectBinding<IGeometry>() {
                {
                    bind(anchorage.layoutBoundsProperty());
                }

                @Override
                protected IGeometry computeValue() {
                    @SuppressWarnings("serial")
                    // get the registered geometry provider from the host
                    Provider<IGeometry> geomProvider = getAdaptable().getAdapter(new TypeToken<Provider<IGeometry>>() {
                    });
                    return geomProvider.get();
                }
            });
        }
        return anchor;
    }
}