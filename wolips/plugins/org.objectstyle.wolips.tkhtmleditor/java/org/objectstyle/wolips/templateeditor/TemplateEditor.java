package org.objectstyle.wolips.templateeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.components.editor.ComponentEditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IHtmlDocumentProvider;
import org.objectstyle.wolips.components.editor.IWebobjectTagListener;
import org.objectstyle.wolips.editors.contentdescriber.ContentDescriberWO;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLEditor;
import tk.eclipse.plugin.htmleditor.editors.HTMLEditorPart;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

public class TemplateEditor extends HTMLEditor implements IEmbeddedEditor, IHtmlDocumentProvider, IWebobjectTagListener {
  private TemplateConfiguration _configuration;
  private ComponentEditorInteraction _editorInteraction;

  public TemplateEditor() {
    super();
    ContentDescriberWO.ANSWER = IContentDescriber.VALID;
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    TemplateTripleClickAdapter tripleClickAdapter = new TemplateTripleClickAdapter(this);
    getSourceEditor().getViewer().getTextWidget().addMouseListener(tripleClickAdapter);
    getSourceEditor().getViewer().getTextWidget().addMouseMoveListener(tripleClickAdapter);
  }

  public TemplateSourceEditor getSourceEditor() {
    TemplateSourceEditor sourceEditor;
    if (this._editor instanceof TemplateSourceEditor) {
      sourceEditor = (TemplateSourceEditor) this._editor;
    }
    else {
      HTMLEditorPart htmlEditorPart = (HTMLEditorPart) this._editor;
      sourceEditor = (TemplateSourceEditor) htmlEditorPart.getSourceEditor();
    }
    return sourceEditor;
  }

  public void initEditorInteraction(ComponentEditorInteraction initEditorInteraction) {
    this._editorInteraction = initEditorInteraction;
    getSourceEditor().getSelectionProvider().addSelectionChangedListener(new TemplateOutlineSelectionHandler(this, _editorInteraction));
    _editorInteraction.setHtmlDocumentProvider(this);
    _editorInteraction.addWebObjectTagListener(this);
  }

  public IDocument getHtmlEditDocument() {
    IDocument htmlDocument = getSourceEditor().getDocumentProvider().getDocument(getEditorInput());
    return htmlDocument;
  }

  public ComponentEditorInteraction getEditorInteraction() {
    return _editorInteraction;
  }

  @Override
  protected HTMLSourceEditor createHTMLSourceEditor(HTMLConfiguration config) {
    ContentDescriberWO.ANSWER = IContentDescriber.INVALID;
    return new TemplateSourceEditor(config);
  }

  @Override
  protected HTMLConfiguration getSourceViewerConfiguration() {
    if (_configuration == null) {
      _configuration = new TemplateConfiguration(HTMLPlugin.getDefault().getColorProvider());
    }
    return _configuration;
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    super.doSave(monitor);
    _editorInteraction.fireWebObjectChanged();
  }
  
  public void webObjectChanged() {
    getSourceEditor().getTemplateOutlinePage().update();
  }
  
  public void webObjectTagSelected(String name) {
    // DO NOTHING
  }

  /**
   * Update preview.
   */
  @Override
  public void updatePreview() {
    //    if (!(editor instanceof HTMLEditorPart)) {
    //      return;
    //    }
    //    try {
    //      if (!((HTMLEditorPart) editor).isFileEditorInput()) {
    //        return;
    //      }
    //      // write to temporary file
    //      HTMLEditorPart editor = (HTMLEditorPart) this.editor;
    //      IFileEditorInput input = (IFileEditorInput) this.editor.getEditorInput();
    //      String charset = input.getFile().getCharset();
    //      String html = editor.getSourceEditor().getDocumentProvider().getDocument(input).get();
    //      // replace JSP parts
    //      html = JSPPreviewConverter.convertJSP((IFileEditorInput) getEditorInput(), html);
    //
    //      File tmpFile = editor.getSourceEditor().getTempFile();
    //      FileOutputStream out = new FileOutputStream(tmpFile);
    //      PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, charset), true);
    //      pw.write(html);
    //      pw.close();
    //
    //      if (prevTempFile != null && prevTempFile.equals(tmpFile)) {
    //        editor.getBrowser().refresh();
    //      }
    //      else {
    //        if (prevTempFile != null) {
    //          prevTempFile.delete();
    //        }
    //        prevTempFile = tmpFile;
    //        editor.getBrowser().setUrl("file://" + tmpFile.getAbsolutePath()); //$NON-NLS-1$
    //      }
    //    }
    //    catch (Exception ex) {
    //      HTMLPlugin.logException(ex);
    //      //ex.printStackTrace();
    //    }
  }
}
