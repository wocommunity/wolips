package ch.rucotec.gef.diagram;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Guice;
import com.sun.javafx.stage.EmbeddedWindow;

import ch.rucotec.wolips.eomodeler.core.gef.model.SimpleDiagram;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Entry point for our SimpleDiagram, creating and rendering a JavaFX
 * Window.
 *
 */
@SuppressWarnings("restriction")
public class SimpleDiagramApplication {

	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private SimpleDiagram diagram;
	private EmbeddedWindow primaryStage;
	private HistoricizingDomain domain;

	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor for the JavaFx GEF-Application needs an {@link EmbeddedWindow} which is
	 * used as the primaryStage.
	 * @param stage - the primaryStage.
	 */
	public SimpleDiagramApplication(EmbeddedWindow stage) {
		this.primaryStage = stage;
	}

	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
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

		pane.setMinWidth(800);
		pane.setMinHeight(600);

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
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

	public void start() throws Exception {
		SimpleDiagramModule module = new SimpleDiagramModule();
		// create domain using guice
		this.domain = (HistoricizingDomain) Guice.createInjector(module).getInstance(IDomain.class);

		// create viewers
		hookViewers();

		// activate domain
		domain.activate();

		// load contents
		populateViewerContents();

		// set-up stage
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	/**
	 * Creates the undo/redo and Zoom -/Zoom + buttons
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
	 * This sets the diagram and refreshes the content of the viewer.
	 * @param selectedDiagram
	 */
	public void setDiagram(AbstractDiagram selectedDiagram) {
		diagram = selectedDiagram.drawDiagram();
		populateViewerContents(); // Refresh content
	}

	public IUndoContext getUndoContext() {
		return domain.getUndoContext();
	}

}