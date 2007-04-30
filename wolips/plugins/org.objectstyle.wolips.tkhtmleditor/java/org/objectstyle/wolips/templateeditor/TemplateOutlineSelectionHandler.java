package org.objectstyle.wolips.templateeditor;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.objectstyle.wolips.components.editor.EditorInteraction;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class TemplateOutlineSelectionHandler implements ISelectionChangedListener {
  private TemplateEditor _editor;
  private EditorInteraction _editorInteraction;

  public TemplateOutlineSelectionHandler(TemplateEditor editor, EditorInteraction editorInteraction) {
    _editor = editor;
    _editorInteraction = editorInteraction;
  }

  public void selectionChanged(SelectionChangedEvent event) {
    TemplateSourceEditor sourceEditor = _editor.getSourceEditor();
    if (sourceEditor != null) {
      TemplateOutlinePage outlinePage = sourceEditor.getTemplateOutlinePage();
      FuzzyXMLDocument xmlDocument = outlinePage.getDoc();
      if (xmlDocument != null) {
        ITextSelection textSelection = (ITextSelection) event.getSelection();
        if (textSelection != null) {
          int offset = textSelection.getOffset();
          FuzzyXMLElement selectedElement = xmlDocument.getElementByOffset(offset);
          if (selectedElement != null) {
            String tagName = selectedElement.getName();
            if (WodHtmlUtils.isWOTag(tagName)) {
              String woElementName = selectedElement.getAttributeValue("name");
              if (woElementName != null && woElementName.length() > 0) {
                _editorInteraction.fireWebobjectTagChanged(woElementName);
              }
            }
          }
        }
      }
    }
  }
}