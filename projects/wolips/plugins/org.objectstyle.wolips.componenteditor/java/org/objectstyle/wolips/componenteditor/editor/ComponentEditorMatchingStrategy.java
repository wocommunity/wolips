package org.objectstyle.wolips.componenteditor.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.components.input.ComponentEditorInput;

public class ComponentEditorMatchingStrategy implements IEditorMatchingStrategy {

	public boolean matches(IEditorReference editorReference,
			IEditorInput editorInput) {
		String editorID = editorReference.getId();
		if(editorID == null) {
			return false;
		}
		if(!editorID.equals(ComponenteditorPlugin.ComponentEditorID)) {
			return false;
		}
		if (!(editorInput instanceof FileEditorInput)) {
			return false;
		}
		IFile inputFile = ResourceUtil.getFile(editorInput);
		if (inputFile == null) {
			return false;
		}
		String extension = inputFile.getFileExtension();
		if (extension == null) {
			return false;
		}
		if (!("java".equalsIgnoreCase(extension)
				|| "wod".equalsIgnoreCase(extension)
				|| "html".equalsIgnoreCase(extension)
				|| "woo".equalsIgnoreCase(extension)
				|| "api".equalsIgnoreCase(extension) || "tiff"
				.equalsIgnoreCase(extension))) {
			return false;
		}
		IEditorInput editorReferenceEditorInput = null;
		try {
			//expensive call it as late as possible
			editorReferenceEditorInput = editorReference.getEditorInput();
		} catch (PartInitException e) {
			ComponenteditorPlugin.getDefault().log(e);
		}
		if(editorReferenceEditorInput == null) {
			return false;
		}
		if(!(editorReferenceEditorInput instanceof ComponentEditorInput)) {
			return false;
		}
		ComponentEditorInput componentEditorInput = (ComponentEditorInput)editorReferenceEditorInput;
		IEditorInput[] editorInputArray = componentEditorInput.getInput();
		for (int i = 0; i < editorInputArray.length; i++) {
			IFile inputFileFromEditor = ResourceUtil.getFile(editorInputArray[i]);
			if(inputFileFromEditor == null) {
				continue;
			}
			if(inputFileFromEditor.equals(inputFile)) {
				return true;
			}
			
		}
		return false;
	}

}
