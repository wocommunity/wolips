package tk.eclipse.plugin.htmleditor.editors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.views.IPaletteTarget;

/**
 * This is the HTML editor that supports tabbed and split style.
 * <p>
 * In the tabbed style, this editor uses MultiPageHTMLEditor,
 * and in the split style, uses SplitPageHTMLEditor.
 * And this class transfers the call of most methods to them.
 * 
 * @see tk.eclipse.plugin.htmleditor.editors.MultiPageHTMLEditor
 * @see tk.eclipse.plugin.htmleditor.editors.SplitPageHTMLEditor
 */
public class HTMLEditor extends EditorPart implements IPaletteTarget {

  protected EditorPart _editor;
  protected File _prevTempFile = null;

  public HTMLEditor() {
    super();
    IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
    String type = store.getString(HTMLPlugin.PREF_EDITOR_TYPE);
    if (type.equals("horizontal")) {
      _editor = new SplitPageHTMLEditor(this, true, createHTMLSourceEditor(getSourceViewerConfiguration()));
    }
    else if (type.equals("vertical")) {
      _editor = new SplitPageHTMLEditor(this, false, createHTMLSourceEditor(getSourceViewerConfiguration()));
    }
    else if (type.equals("tab")) {
      _editor = new MultiPageHTMLEditor(this, createHTMLSourceEditor(getSourceViewerConfiguration()));
    }
    else {
      _editor = createHTMLSourceEditor(getSourceViewerConfiguration());
      _editor.addPropertyListener(new IPropertyListener() {
        public void propertyChanged(Object source, int propertyId) {
          firePropertyChange(propertyId);
        }
      });
    }
  }

  protected HTMLConfiguration getSourceViewerConfiguration() {
    return new HTMLConfiguration(HTMLPlugin.getDefault().getColorProvider());
  }

  protected HTMLSourceEditor createHTMLSourceEditor(HTMLConfiguration config) {
    return new HTMLSourceEditor(config);
  }

  public HTMLSourceEditor getPaletteTarget() {
    if (_editor instanceof HTMLSourceEditor) {
      return (HTMLSourceEditor) _editor;
    }
    else {
      return ((HTMLEditorPart) _editor).getSourceEditor();
    }
  }

  /**
   * Update preview
   */
  public void updatePreview() {
    if (!(_editor instanceof HTMLEditorPart)) {
      return;
    }
    try {
      if (!((HTMLEditorPart) _editor).isFileEditorInput()) {
        return;
      }

      // write to temporary file
      HTMLEditorPart editor = (HTMLEditorPart) this._editor;
      IFileEditorInput input = (IFileEditorInput) this._editor.getEditorInput();
      String charset = input.getFile().getCharset();
      String html = editor.getSourceEditor().getDocumentProvider().getDocument(input).get();
      // replace JSP part
      //html = HTMLUtil.convertJSP(html);

      File tmpFile = editor.getSourceEditor().getTempFile();
      FileOutputStream out = new FileOutputStream(tmpFile);
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, charset), true);
      pw.write(html);
      pw.close();

      if (_prevTempFile != null && _prevTempFile.equals(tmpFile)) {
        editor.getBrowser().refresh();
      }
      else {
        if (_prevTempFile != null) {
          _prevTempFile.delete();
        }
        _prevTempFile = tmpFile;
        editor.getBrowser().setUrl("file://" + tmpFile.getAbsolutePath()); //$NON-NLS-1$
      }
    }
    catch (Exception ex) {
      HTMLPlugin.logException(ex);
    }
  }

  @Override
  public void createPartControl(Composite parent) {
    _editor.createPartControl(parent);
  }

  @Override
  public void dispose() {
    _editor.dispose();
    super.dispose();
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    _editor.doSave(monitor);
    //updateFlag = true;
  }

  @Override
  public void doSaveAs() {
    _editor.doSaveAs();
    //updateFlag = true;
  }

  //	public boolean equals(Object arg0) {
  //		return editor.equals(arg0);
  //	}
  @Override
  public Object getAdapter(Class adapter) {
    return _editor.getAdapter(adapter);
  }

  @Override
  public String getContentDescription() {
    return _editor.getContentDescription();
  }

  @Override
  public IEditorInput getEditorInput() {
    return _editor.getEditorInput();
  }

  @Override
  public IEditorSite getEditorSite() {
    return _editor.getEditorSite();
  }

  @Override
  public String getPartName() {
    return _editor.getPartName();
  }

  @Override
  public IWorkbenchPartSite getSite() {
    return _editor.getSite();
  }

  @Override
  public String getTitle() {
    return _editor.getTitle();
  }

  @Override
  public Image getTitleImage() {
    return _editor.getTitleImage();
  }

  @Override
  public String getTitleToolTip() {
    return _editor.getTitleToolTip();
  }

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    _editor.init(site, input);
  }

  @Override
  public boolean isDirty() {
    return _editor.isDirty();
  }

  @Override
  public boolean isSaveAsAllowed() {
    return _editor.isSaveAsAllowed();
  }

  @Override
  public boolean isSaveOnCloseNeeded() {
    return _editor.isSaveOnCloseNeeded();
  }

  @Override
  public void setFocus() {
    _editor.setFocus();
  }

  @Override
  public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
    _editor.setInitializationData(config, propertyName, data);
  }

  @Override
  public void showBusy(boolean busy) {
    _editor.showBusy(busy);
  }

  /** change to the source editor, and move calet to the specified offset. */
  public void setOffset(int offset) {
    if (_editor instanceof SplitPageHTMLEditor) {
      ((SplitPageHTMLEditor) _editor).setOffset(offset);
    }
    else if (_editor instanceof MultiPageHTMLEditor) {
      ((MultiPageHTMLEditor) _editor).setOffset(offset);
    }
    else if (_editor instanceof HTMLSourceEditor) {
      ((HTMLSourceEditor) _editor).selectAndReveal(offset, 0);
    }
  }

  public void firePropertyChange2(int propertyId) {
    super.firePropertyChange(propertyId);
  }

}
