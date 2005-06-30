package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class WODEditor extends TextEditor {
  public WODEditor() {
    setSourceViewerConfiguration(new WODSourceViewerConfiguration(this));
  }
}
