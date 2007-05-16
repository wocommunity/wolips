package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class EOModelEditorInput extends FileEditorInput {
	public EOModelEditorInput(IEditorInput input) {
		super(((FileEditorInput) input).getFile());
	}

	public EOModelEditorInput(IFile file) {
		super(file);
	}
}
