package org.objectstyle.wolips.wooeditor.plisteditor;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.ui.editors.text.TextEditor;

public class PlistEditor extends TextEditor {

    private IColorManager colorManager;

	public PlistEditor() {
		super();
        colorManager = JavaUI.getColorManager();
        setSourceViewerConfiguration(new PlistConfiguration(colorManager));
        setDocumentProvider(new PlistDocumentProvider());
	}

	public void dispose() {
        colorManager.dispose();
		super.dispose();
	}

}
