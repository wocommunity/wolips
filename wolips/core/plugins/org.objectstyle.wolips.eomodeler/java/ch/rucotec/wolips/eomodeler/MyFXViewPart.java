package ch.rucotec.wolips.eomodeler;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

import com.sun.javafx.stage.EmbeddedWindow;

import ch.rucotec.gef.diagram.SimpleDiagramApplication;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class creates an EditorPart providing a JavaFX canvas to be able to draw on it.
 * 
 * @author Savas Celik
 *
 */
public abstract class MyFXViewPart extends EditorPart {
	
	private FXCanvas canvas;
	private SimpleDiagramApplication gefApplication;
	private EOModel myModel;

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FXCanvas(parent, SWT.NONE);
		canvas.setScene(errorFxScene());
		
		try {
			// since the FXCanvas its stage is not public i have to get it via reflection.
			Class canvasClass = Class.forName("javafx.embed.swt.FXCanvas");
			Field stageField = canvasClass.getDeclaredField("stage");
			stageField.setAccessible(true);
			EmbeddedWindow stage = (EmbeddedWindow) stageField.get(canvas);
			gefApplication = new SimpleDiagramApplication(stage);
			gefApplication.start();

		} catch (Exception e) {
			System.err.println("Error probably in MyFXViewPart: " + e.getMessage());
		}

	}
	
	/**
	 * This method is here to define what {@link Scene} should be shown if the GEF-Application
	 * fails.
	 * @return Scene - with the error Scene.
	 */
	protected abstract Scene errorFxScene();

	/**
	 * Sets the focus on the canvas.
	 */
	@Override
	public void setFocus() {
		canvas.setFocus();
	}

	public SimpleDiagramApplication getGefApplication() {
		return gefApplication;
	}

	public Stage getStage() {
		return (Stage) canvas.getScene().getWindow();
	}

	public void setModel(EOModel _model) {
		myModel = _model;
	}
	
	public void setSelectedDiagram (AbstractDiagram selectedDiagram) {
		gefApplication.setDiagram(selectedDiagram);
	}
	
	public EOModel getModel() {
		return myModel;
	}
}
