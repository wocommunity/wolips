package ch.rucotec.gef.diagram;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
	private Stage exportDialog = new Stage();
	private boolean canExportDialogBeClossed = true;
	private final String noPathSelected = "No Directory selected - Please click Browse...";

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
		
		exportDialog.setResizable(false);
		exportDialog.centerOnScreen();
		exportDialog.setTitle("Export Diagram");
		exportDialog.initOwner(primaryStage);
		exportDialog.initModality(Modality.APPLICATION_MODAL); 
		exportDialog.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (exportDialog.isShowing() && !newValue && canExportDialogBeClossed) {
					exportDialog.close();
				}
			}
		});
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
		
		Image zoomInImage = new Image(getClass().getResourceAsStream("/icons/zoomIn.png"));
		Button zoomInButton = new Button();
		zoomInButton.setGraphic(new ImageView(zoomInImage));
		zoomInButton.setOnAction(e -> {
			((InfiniteCanvas)getContentViewer().getCanvas()).getContentTransform().appendScale(
					1.1,
					1.1);
		});

		Image zoomOutImage = new Image(getClass().getResourceAsStream("/icons/zoomOut.png"));
		Button zoomOutButton = new Button();
		zoomOutButton.setGraphic(new ImageView(zoomOutImage));
		zoomOutButton.setOnAction(e -> {
			((InfiniteCanvas)getContentViewer().getCanvas()).getContentTransform().appendScale(
					0.9,
					0.9);
		});
		
		Button downloadDiagram = new Button("Export Diagram...");
		downloadDiagram.setOnAction((e) -> {
			exportDialog.setScene(exportDialogScene());
			exportDialog.showAndWait();
		});
		
		return new HBox(10, undoButton, redoButton, zoomInButton, zoomOutButton, downloadDiagram);
	}
	
	public Scene exportDialogScene() {
		AnchorPane aPane = new AnchorPane();
		aPane.setPrefSize(575, 275);
		
		
		GridPane gridPane = new GridPane();
		gridPane.setPrefSize(355.0, 209.0);
		
		ColumnConstraints colmn = new ColumnConstraints();
        colmn.setHgrow(Priority.SOMETIMES);
        colmn.setMinWidth(10);
        colmn.setPrefWidth(98);
        colmn.setMaxWidth(296);
        gridPane.getColumnConstraints().add(colmn);
        
        ColumnConstraints colmn1 = new ColumnConstraints();
        colmn1.setHgrow(Priority.SOMETIMES);
        colmn1.setMinWidth(10);
        colmn1.setPrefWidth(259);
        colmn1.setMaxWidth(527);
        gridPane.getColumnConstraints().add(colmn1);
        
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.SOMETIMES);
        row.setPrefHeight(228);
        gridPane.getRowConstraints().add(row);
        
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.SOMETIMES);
        row1.setPrefHeight(228);
        gridPane.getRowConstraints().add(row1);
        
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.SOMETIMES);
        row2.setPrefHeight(228);
        gridPane.getRowConstraints().add(row2);
        
		AnchorPane.setLeftAnchor(gridPane, 8.0);
		AnchorPane.setTopAnchor(gridPane, 8.0);
		AnchorPane.setRightAnchor(gridPane, 8.0);
		
		Text fileNameText = new Text("Filename");
		Text resolutionText = new Text("Resolution");
		Text saveText = new Text("Save Location");
		Text filePathText = new Text(noPathSelected);
		
		
		TextField txtField = new TextField("Diagram.png");
		
		ScrollPane scrollPane = new ScrollPane(filePathText);
		scrollPane.setMaxHeight(1.0);
		
		Slider slider = new Slider(1, 10, 1);
		slider.prefWidth(404.0);
		slider.setBlockIncrement(1);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setShowTickLabels(true);
		slider.setSnapToTicks(true);
		slider.setPadding(new Insets(10, 0, 0, 0));
		slider.setValue(2);
		
		Button browseBtn = new Button("Browse...");
		GridPane.setMargin(browseBtn, new Insets(80, 0, 0, 0));
		browseBtn.setOnAction( (e) -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			canExportDialogBeClossed = false;
			File selectedDirectory = directoryChooser.showDialog(exportDialog);
			canExportDialogBeClossed = true;
			
			if(selectedDirectory != null){
				filePathText.setText(selectedDirectory.getAbsolutePath());
			}
		});
		
		gridPane.add(fileNameText, 0, 0);
		gridPane.add(resolutionText, 0, 1);
		gridPane.add(saveText, 0, 2);
		gridPane.add(txtField, 1, 0);
		gridPane.add(slider, 1, 1);
		gridPane.add(scrollPane, 1, 2);
		gridPane.add(browseBtn, 1, 2);
		
		ButtonBar buttonBar = new ButtonBar();
		Button cancelBtn = new Button("Cancel");
		Button exportBtn = new Button("Export");
		ButtonBar.setButtonData(cancelBtn, ButtonData.CANCEL_CLOSE);
		ButtonBar.setButtonData(exportBtn, ButtonData.OK_DONE);
		buttonBar.getButtons().addAll(cancelBtn, exportBtn);
		AnchorPane.setBottomAnchor(buttonBar, 8.0);
		AnchorPane.setRightAnchor(buttonBar, 8.0);
		
		cancelBtn.setOnAction( (event) -> {
			exportDialog.close();
		});
		
		exportBtn.setOnAction( (event) -> {
			String filePath = filePathText.getText();
			String diagramName = txtField.getText();
			exportButtonAction(filePath, diagramName, (int)slider.getValue());
		});
		
		aPane.getChildren().addAll(gridPane, buttonBar);
		return new Scene(aPane);
	}
	
	private void exportButtonAction(String filePath, String diagramName, int resolution) {
		
		try {
			if (diagramName.isEmpty()) {
				throw new Exception("Please provide a file name");
			}

			if (!diagramName.endsWith(".png")) {
				diagramName += ".png";
			}

			File destinationFile = new File(filePath);
			if (!destinationFile.isDirectory()) {
				throw new Exception("Please select an existing directory");
			}
		
			InfiniteCanvas sourceCanvas = ((InfiniteCanvas)getContentViewer().getCanvas());
			
			Bounds bounds = sourceCanvas.getScrollableBounds();
			
			int scale = resolution;
			int minX = scale * (int)bounds.getMinX();
			int minY = scale * (int)bounds.getMinY();
			int maxX = scale * (int)bounds.getMaxX();
			int maxY = scale * (int)bounds.getMaxY();
			
			WritableImage resultingImage = new WritableImage(maxX - minX, maxY - minY);

			// the original code to dump the contents of the InfiniteCanvas into a png file crashes for larger models
			// this seems to be a memory bug in JavaFX
			// There is a discussion on https://stackoverflow.com/questions/49260841/how-to-work-around-unrecognized-image-loader-null-in-javafx
			//     "I encountered the same problem when trying to make a snapshot from a large node. 
			//      In my case scaling was not a possibility since I needed a high resolution image. 
			//      To solve the problem I made multiple snapshots from subsections of the node and 
			//      stitched them together afterwards."
			
			int tileSize = 500;

			int leftOffset = minX;
			while (leftOffset < maxX) {

				int tileWidth = maxX - leftOffset > tileSize ? tileSize : maxX - leftOffset;

				int topOffset = minY;
				while (topOffset < maxY) {
					int tileHeight =  maxY - topOffset > tileSize ? tileSize : maxY - topOffset;

					SnapshotParameters params = new SnapshotParameters();
					params.setViewport(new Rectangle2D(leftOffset, topOffset, tileWidth, tileHeight));
					params.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
					final CompletableFuture<Image> future = new CompletableFuture<>();
					
					WritableImage tile = sourceCanvas.getContentGroup().snapshot(params, null);
					resultingImage.getPixelWriter().setPixels(leftOffset - minX, topOffset - minY, tileWidth, tileHeight, tile.getPixelReader(), 0, 0);

					topOffset += tileHeight;
				}

				leftOffset += tileWidth; 
			}

			File file = new File(filePath,diagramName);
			ImageIO.write(SwingFXUtils.fromFXImage(resultingImage, null), "png", file);

		}
		catch(Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Sorry, there is an error!");
			alert.setHeaderText(null);
			alert.setContentText(ex.getMessage());
			canExportDialogBeClossed = false;
			alert.showAndWait();
			canExportDialogBeClossed = true;
		}

		exportDialog.close();
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