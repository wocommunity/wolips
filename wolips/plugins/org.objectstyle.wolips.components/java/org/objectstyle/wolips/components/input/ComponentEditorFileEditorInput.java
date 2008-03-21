package org.objectstyle.wolips.components.input;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;

public class ComponentEditorFileEditorInput extends FileEditorInput implements IPersistableElement {

	private ComponentEditorInput componentEditorInput;

	public ComponentEditorFileEditorInput(IFile file) {
		super(file);
	}

	public ComponentEditorInput getComponentEditorInput() {
		return componentEditorInput;
	}

	public void setComponentEditorInput(ComponentEditorInput componentEditorInput) {
		this.componentEditorInput = componentEditorInput;
	}

	public String getFactoryId() {
		return this.componentEditorInput.getFactoryId();
	}

	public void saveState(IMemento memento) {
		this.componentEditorInput.saveState(memento);
	}

	public IPersistableElement getPersistable() {
		return this.componentEditorInput == null ? null : this.componentEditorInput.getPersistable();
	}
}
