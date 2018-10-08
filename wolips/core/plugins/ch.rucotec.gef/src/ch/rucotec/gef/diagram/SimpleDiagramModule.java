package ch.rucotec.gef.diagram;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.gestures.ScrollGesture;
import org.eclipse.gef.mvc.fx.handlers.FocusAndSelectOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.handlers.ResizeTranslateFirstAnchorageOnHandleDragHandler;
import org.eclipse.gef.mvc.fx.handlers.TranslateSelectedOnDragHandler;
import org.eclipse.gef.mvc.fx.handlers.ZoomOnPinchSpreadHandler;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.SquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeOutlineProvider;

import com.google.inject.multibindings.MapBinder;

import ch.rucotec.gef.diagram.models.ItemCreationModel;
import ch.rucotec.gef.diagram.parts.DiagramNodePart;
import ch.rucotec.gef.diagram.parts.DiagramPartsFactory;
import ch.rucotec.gef.diagram.parts.SimpleDiagramAnchorProvider;
import ch.rucotec.gef.diagram.policies.CreateNewConnectiononClickHandler;
import ch.rucotec.gef.diagram.policies.CreateNewNodeOnClickHandler;

/**
 * The Guice Module to configure our parts and behaviors.
 *
 */
public class SimpleDiagramModule extends MvcFxModule {

	@Override
	protected void bindIContentPartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// bind DiagramPartsFactory adapter to the content viewer
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DiagramPartsFactory.class);
	}

	/**
	 *
	 * @param adapterMapBinder
	 */
	protected void bindDiagramNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// bind anchor provider used to create the connection anchors
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SimpleDiagramAnchorProvider.class);

		// bind a geometry provider, which is used in our anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShapeOutlineProvider.class);

		// provides a hover feedback to the shape, used by the HoverBehavior
		AdapterKey<?> role = AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeOutlineProvider.class);

		// provides a selection feedback to the shape
		role = AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeBoundsProvider.class);

		// support moving nodes via mouse drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedOnDragHandler.class);

		
		// specify the factory to create the geometry object for the selection
		// handles
		role = AdapterKey.role(DefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeBoundsProvider.class);

		// support resizing nodes
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizePolicy.class);
		
		// bind create connection handler
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewConnectiononClickHandler.class);
	
	}

    /**
     * Binds the parts of the selection handles (the squares in the corner) to
     * policies
     *
     * @param adapterMapBinder
     */
    protected void bindSquareSegmentHandlePartPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
        adapterMapBinder.addBinding(AdapterKey.defaultRole())
                .to(ResizeTranslateFirstAnchorageOnHandleDragHandler.class);
    }

    @Override
    protected void configure() {
        // start the default configuration
        super.configure();

        bindDiagramNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), DiagramNodePart.class));

        // with this binding we create the handles
        bindSquareSegmentHandlePartPartAdapter(
                AdapterMaps.getAdapterMapBinder(binder(), SquareSegmentHandlePart.class));
    }

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);

		// binding the HoverOnHoverPolicy to every part
		// if a mouse is moving above a part it is set i the HoverModel
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);

		// add the focus and select policy to every part, listening to clicks
		// and changing the focus and selection model
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusAndSelectOnClickHandler.class);
	}

	@Override
	protected void bindIRootPartAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIRootPartAdaptersForContentViewer(adapterMapBinder);

		// binding a Hover Behavior to the root part. it will react to
		// HoverModel changes and render the hover part
//		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverBehavior.class);
		
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateNewNodeOnClickHandler.class);
	}
	
	@Override
	protected void bindIViewerAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIViewerAdaptersForContentViewer(adapterMapBinder);
		// bind the model to the content viewer
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ItemCreationModel.class);
	}
}