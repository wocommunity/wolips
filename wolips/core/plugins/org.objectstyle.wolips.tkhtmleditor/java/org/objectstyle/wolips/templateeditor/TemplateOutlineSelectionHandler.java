package org.objectstyle.wolips.templateeditor;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.objectstyle.wolips.components.editor.ComponentEditorInteraction;

public class TemplateOutlineSelectionHandler implements ISelectionChangedListener {
  private TemplateEditor _editor;
  private ComponentEditorInteraction _editorInteraction;

  public TemplateOutlineSelectionHandler(TemplateEditor editor, ComponentEditorInteraction editorInteraction) {
    _editor = editor;
    _editorInteraction = editorInteraction;
  }

  public void selectionChanged(SelectionChangedEvent event) {
      ITextSelection textSelection = (ITextSelection) event.getSelection();
      if (textSelection != null && textSelection.getLength() > 0) {
        int offset = textSelection.getOffset();
        _editor.selectionChangedToOffset(offset);
      }
  }
}