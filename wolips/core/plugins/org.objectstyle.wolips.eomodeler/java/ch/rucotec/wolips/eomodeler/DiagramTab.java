package ch.rucotec.wolips.eomodeler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class DiagramTab extends MyFXViewPart implements ISelectionProvider {

	@Override
	protected Scene createFxScene() {
		AnchorPane pane = new AnchorPane();
		Scene myScene = new Scene(pane);
		Label lblError = new Label("There was a problem while generating the diagram");
		pane.getChildren().add(lblError);
		return myScene;
	}

	@Override
	protected void setFxFocus() {
		//super.setFocus();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createPartControl(parent);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		super.setFocus();
	}
}