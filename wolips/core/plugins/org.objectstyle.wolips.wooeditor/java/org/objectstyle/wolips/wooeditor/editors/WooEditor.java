package org.objectstyle.wolips.wooeditor.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class WooEditor extends TextEditor {

	private ColorManager colorManager;

	public WooEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
