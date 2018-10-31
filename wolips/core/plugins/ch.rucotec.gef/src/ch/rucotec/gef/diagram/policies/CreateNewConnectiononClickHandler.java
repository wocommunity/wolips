package ch.rucotec.gef.diagram.policies;



import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.operations.ChangeSelectionOperation;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.reflect.TypeToken;

import ch.rucotec.gef.diagram.models.ItemCreationModel;
import ch.rucotec.gef.diagram.models.ItemCreationModel.Type;
import ch.rucotec.gef.diagram.parts.DiagramNodePart;
import ch.rucotec.gef.diagram.parts.SimpleDiagramPart;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramConnection;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The handler to create a new node using the {@link ItemCreationModel}
 *
 */
public class CreateNewConnectiononClickHandler extends AbstractHandler implements IOnClickHandler {

	@SuppressWarnings("serial")
	@Override
	public void click(MouseEvent e) {
		if (!e.isPrimaryButtonDown()) {
			return; 
		}

		IViewer viewer = getHost().getRoot().getViewer();
		ItemCreationModel creationModel = viewer.getAdapter(ItemCreationModel.class);
		if (creationModel.getType()!=Type.Connection) {
			return; // don't want to create a connection
		}

		if (creationModel.getSource()==null) {
			// the host is the source
			creationModel.setSource((DiagramNodePart) getHost());
			return; // wait for the next click
		}

		// okay, we have a pair
		DiagramNodePart source = creationModel.getSource();
		DiagramNodePart target = (DiagramNodePart) getHost();

		// check if valid
		if (source == target) {
			return;
		}

		IVisualPart<? extends Node> part = getHost().getRoot().getChildrenUnmodifiable().get(0);
		if (part instanceof SimpleDiagramPart) {
			DiagramConnection newConn = new DiagramConnection();
			newConn.connect(source.getContent(), target.getContent());

			// GEF provides the CreatePolicy and operations to add a new element
			// to the model
			IRootPart<? extends Node> root = getHost().getRoot();
			// get the policy bound to the IRootPart
			CreationPolicy creationPolicy = root.getAdapter(new TypeToken<CreationPolicy>() {
			});
			// initialize the policy
			init(creationPolicy);
			// create a IContentPart for our new model. We don't use the
			// returned content-part
			creationPolicy.create(newConn, (SimpleDiagramPart) part,
					HashMultimap.<IContentPart<? extends Node>, String>create());
			// execute the creation
			commit(creationPolicy);
			// select target node
			try {
				viewer.getDomain().execute(new ChangeSelectionOperation(viewer, Collections.singletonList(target)),
						null);
			} catch (ExecutionException e1) {
			}
		}

		// finally reset creationModel
		creationModel.setSource(null);
		creationModel.setType(Type.None);
	}

}