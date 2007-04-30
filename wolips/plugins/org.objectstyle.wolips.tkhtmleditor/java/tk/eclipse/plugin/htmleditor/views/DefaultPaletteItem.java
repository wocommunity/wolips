package tk.eclipse.plugin.htmleditor.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

/**
 * A default implementation of IPaletteItem.
 * This palette item inserts simple text.
 */
public class DefaultPaletteItem implements IPaletteItem {

  private String _name;
  private ImageDescriptor _image;
  private String _content;

  /**
   * The constructor.
   * 
   * @param name     item name
   * @param image    icon
   * @param content  insert text
   */
  public DefaultPaletteItem(String name, ImageDescriptor image, String content) {
    _name = name;
    _image = image;
    _content = content;
  }

  public ImageDescriptor getImageDescriptor() {
    return _image;
  }

  public String getLabel() {
    return _name;
  }

  public String getContent() {
    return this._content;
  }

  public void execute(HTMLSourceEditor editor) {
    IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
    ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
    try {
      String content;
      int explicitCursorOffset = _content.indexOf("${cursor}");
      if (explicitCursorOffset != -1) {
        content = _content.substring(0, explicitCursorOffset) + _content.substring(explicitCursorOffset + "${cursor}".length());
      }
      else {
        content = _content;
      }
      int defaultCursorOffset = content.length();
      if (content.indexOf("></") != -1) {
        defaultCursorOffset = content.indexOf("></") + 1;
      }
      
      int cursorOffset;
      int variableOffset = content.indexOf("${selection}");
      if (variableOffset != -1) {
        doc.replace(sel.getOffset(), 0, content.substring(0, variableOffset));
        doc.replace(sel.getOffset() + variableOffset + sel.getLength(), 0, content.substring(variableOffset + "${selection}".length()));
        if (explicitCursorOffset == -1) {
          cursorOffset = defaultCursorOffset;
        }
        else if (explicitCursorOffset < variableOffset) {
          cursorOffset = explicitCursorOffset;
        }
        else {
          cursorOffset = explicitCursorOffset + sel.getLength() - "${selection}".length();
        }
      }
      else {
        doc.replace(sel.getOffset(), sel.getLength(), content);
        if (explicitCursorOffset == -1) {
          cursorOffset = defaultCursorOffset;
        }
        else {
          cursorOffset = explicitCursorOffset;
        }
      }
      editor.selectAndReveal(sel.getOffset() + cursorOffset, 0);
    }
    catch (Exception ex) {
      HTMLPlugin.logException(ex);
    }
  }
}
