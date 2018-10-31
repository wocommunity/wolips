package ch.rucotec.gef.diagram;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Guice;
import com.sun.javafx.stage.EmbeddedWindow;

import ch.rucotec.gef.diagram.models.ItemCreationModel;
import ch.rucotec.gef.diagram.models.ItemCreationModel.Type;
import ch.rucotec.gef.diagram.visuals.DiagramNodeVisual;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;
import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagramExampleFactory;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramGroup;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Entry point for our Simple Mind Map Editor, creating and rendering a JavaFX
 * Window.
 *
 */
public class SimpleDiagramApplication{

	private SimpleDiagram diagram;
	private EmbeddedWindow primaryStage;
	private HistoricizingDomain domain;
	private SimpleDiagramExampleFactory fac = new SimpleDiagramExampleFactory();
	
	/**
	 * Returns the content viewer of the domain
	 *
	 * @return
	 */
	private IViewer getContentViewer() {
		return domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
	}

	/**
	 * Creating JavaFX widgets and set them to the stage.
	 */
	private void hookViewers() {
		//Scene scene = new Scene(getContentViewer().getCanvas());

		// creating parent pane for Canvas and button pane
		BorderPane pane = new BorderPane();

		pane.setTop(createButtonBar());
		pane.setCenter(getContentViewer().getCanvas());
		//pane.setRight(createToolPalette());

		pane.setMinWidth(800);
		pane.setMinHeight(600);

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);

		//primaryStage.setScene(scene);
	}

	/**
	 * Creates the example mind map and sets it as content to the viewer.
	 */
	private void populateViewerContents() {

		if (diagram == null) {
//			diagram = fac.createSingleNodeExample();
			diagram = new SimpleDiagram();
		}
		IViewer viewer = getContentViewer();
		viewer.getContents().setAll(diagram);
	}

	public void start(EmbeddedWindow stage) throws Exception {
		SimpleDiagramModule module = new SimpleDiagramModule();
		this.primaryStage = stage;
		// create domain using guice
		this.domain = (HistoricizingDomain) Guice.createInjector(module).getInstance(IDomain.class);

		// create viewers
		hookViewers();

		// activate domain
		domain.activate();

		// load contents
		populateViewerContents();

		// set-up stage
//		primaryStage.setResizable(true);
//		primaryStage.setTitle("GEF Simple Mindmap");
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	/**
	 * Creates the undo/redo buttons
	 * 
	 * @return
	 */
	private Node createButtonBar() {
		Button undoButton = new Button("Undo");
		undoButton.setDisable(true);
		undoButton.setOnAction((e) -> {
			try {
				domain.getOperationHistory().undo(domain.getUndoContext(), null, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		Button redoButton = new Button("Redo");
		redoButton.setDisable(true);
		redoButton.setOnAction((e) -> {
			try {
				domain.getOperationHistory().redo(domain.getUndoContext(), null, null);
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});

		// add listener to operation history in our domain 
		// and enable/disable buttons
		domain.getOperationHistory().addOperationHistoryListener((e) -> {
			IUndoContext ctx = domain.getUndoContext();
			undoButton.setDisable(!e.getHistory().canUndo(ctx));
			redoButton.setDisable(!e.getHistory().canRedo(ctx));
		});
		
		// Zoom handling
		/* TODO
		 * ((InfiniteCanvas)getContentViewer().getCanvas()).getContentTransform().appendScale(
					deltaY < 0 ? 0.9 : 1.1,
					deltaY < 0 ? 0.9 : 1.1);
		 */
		Button zoomInButton = new Button("Zoom +");
		zoomInButton.setOnAction(e -> {
			((InfiniteCanvas)getContentViewer().getCanvas()).getContentTransform().appendScale(
					1.1,
					1.1);
		});
		
		Button zoomOutButton = new Button("Zoom -");
		zoomOutButton.setOnAction(e -> {
			((InfiniteCanvas)getContentViewer().getCanvas()).getContentTransform().appendScale(
					0.9,
					0.9);
		});

		return new HBox(10, undoButton, redoButton, zoomInButton, zoomOutButton);
	}

	/**
	 * Creates the tooling buttons to create new elements
	 * @return
	 */
	private Node createToolPalette() {
		ItemCreationModel creationModel = getContentViewer().getAdapter(ItemCreationModel.class);

		DiagramNodeVisual graphic = new DiagramNodeVisual(new ArrayList<>());
		graphic.setTitle("New Node");

		// the toggleGroup makes sure, we only select one 
		ToggleGroup toggleGroup = new ToggleGroup();

		ToggleButton createNode = new ToggleButton("", graphic);
		createNode.setToggleGroup(toggleGroup);
		createNode.setMaxWidth(Double.MAX_VALUE);
		createNode.selectedProperty().addListener((e, oldVal, newVal) -> {
			creationModel.setType(newVal ? Type.Node : Type.None);
		});

		ToggleButton createConn = new ToggleButton("New Connection");
		createConn.setToggleGroup(toggleGroup);
		createConn.setMaxWidth(Double.MAX_VALUE);
		createConn.setMinHeight(50);
		createConn.selectedProperty().addListener((e, oldVal, newVal) -> {
			creationModel.setType(newVal ? Type.Connection : Type.None);
		});

		// now listen to changes in the model, and deactivate buttons, if necessary
		creationModel.getTypeProperty().addListener((e, oldVal, newVal) -> {
			if (Type.None == newVal) {
				// unselect the toggle button
				Toggle selectedToggle = toggleGroup.getSelectedToggle();
				if (selectedToggle != null) {
					selectedToggle.setSelected(false);
				}
			}
		});

		return new VBox(20, createNode, createConn);
	}
	
	public void generateErd(Object _model) {
		diagram = fac.createErd(_model);
		populateViewerContents();
	}
	
	public void generateDiagram(Object selectedDiagram) {
		if (selectedDiagram instanceof EOERDiagram) {
			diagram = ((EOERDiagram) selectedDiagram).drawDiagram();
		}
		populateViewerContents(); // Refresh content
	}

	public IUndoContext getUndoContext() {
		return domain.getUndoContext();
	}
	
}