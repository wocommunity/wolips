package org.objectstyle.wolips.templateeditor;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.revisions.RevisionInformation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.ui.texteditor.ITextEditorExtension3;
import org.eclipse.ui.texteditor.ITextEditorExtension4;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.components.editor.ComponentEditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IHtmlDocumentProvider;
import org.objectstyle.wolips.components.editor.IWebobjectTagListener;
import org.objectstyle.wolips.editors.contentdescriber.ContentDescriberWO;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.ITextWOEditor;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLEditor;
import tk.eclipse.plugin.htmleditor.editors.HTMLEditorPart;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

public class TemplateEditor extends HTMLEditor implements IEmbeddedEditor, IHtmlDocumentProvider, IWebobjectTagListener, ITextEditor, IReusableEditor, ITextEditorExtension, ITextEditorExtension2, ITextEditorExtension3, ITextEditorExtension4, INavigationLocationProvider, ISaveablesSource, IPersistableEditor, ITextWOEditor {
  public static final String BINDING_HOVER_ANNOTATION = "org.objectstyle.wolips.tkhtmleditor.bindingHover";

  private TemplateConfiguration _configuration;
  private ComponentEditorInteraction _editorInteraction;

  public TemplateEditor() {
    super();
    ContentDescriberWO.ANSWER = IContentDescriber.VALID;
  }

  public WodParserCache getParserCache() throws CoreException, LocateException {
    return getSourceEditor().getParserCache();
  }

  public IWodElement getSelectedElement(boolean resolveWodElement, boolean refreshModel) throws Exception {
    return getSourceEditor().getSelectedElement(resolveWodElement, refreshModel);
  }

  public IWodElement getWodElementAtPoint(Point point, boolean resolveWodElement, boolean refreshModel) throws Exception {
    return getSourceEditor().getWodElementAtPoint(point, resolveWodElement, refreshModel);
  }

  public ISourceViewer getWOSourceViewer() {
    return getSourceEditor().getViewer();
  }

  public StyledText getWOEditorControl() {
    return getSourceEditor().getWOEditorControl();
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    TemplateTripleClickAdapter tripleClickAdapter = new TemplateTripleClickAdapter(this);
    getSourceEditor().getViewer().getTextWidget().addMouseListener(tripleClickAdapter);
    getSourceEditor().getViewer().getTextWidget().addMouseMoveListener(tripleClickAdapter);
  }

  @Override
  public void setInput(IEditorInput input) {
    super.setInput(input);
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
    getSourceEditor().initEditorInteraction(initEditorInteraction);
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
    if (_editorInteraction == null || _editorInteraction.embeddedEditorWillSave(monitor)) {
      super.doSave(monitor);
      boolean autoBuild = ResourcesPlugin.getPlugin().getPluginPreferences().getDefaultBoolean(ResourcesPlugin.PREF_AUTO_BUILDING);
      if (_editorInteraction != null) {
        _editorInteraction.fireWebObjectChanged();
      }
    }
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

  public void close(boolean save) {
    getSourceEditor().close(save);
  }

  public void doRevertToSaved() {
    getSourceEditor().doRevertToSaved();
  }

  public IAction getAction(String actionId) {
    return getSourceEditor().getAction(actionId);
  }

  public IDocumentProvider getDocumentProvider() {
    return getSourceEditor().getDocumentProvider();
  }

  public IRegion getHighlightRange() {
    return getSourceEditor().getHighlightRange();
  }

  public ISelectionProvider getSelectionProvider() {
    return getSourceEditor().getSelectionProvider();
  }

  public boolean isEditable() {
    return getSourceEditor().isEditable();
  }

  public void removeActionActivationCode(String actionId) {
    getSourceEditor().removeActionActivationCode(actionId);
  }

  public void resetHighlightRange() {
    getSourceEditor().resetHighlightRange();
  }

  public void selectAndReveal(int offset, int length) {
    getSourceEditor().selectAndReveal(offset, length);
  }

  public void setAction(String actionID, IAction action) {
    getSourceEditor().setAction(actionID, action);
  }

  public void setActionActivationCode(String actionId, char activationCharacter, int activationKeyCode, int activationStateMask) {
    getSourceEditor().setActionActivationCode(actionId, activationCharacter, activationKeyCode, activationStateMask);
  }

  public void setHighlightRange(int offset, int length, boolean moveCursor) {
    getSourceEditor().setHighlightRange(offset, length, moveCursor);
  }

  public void showHighlightRangeOnly(boolean showHighlightRangeOnly) {
    getSourceEditor().showHighlightRangeOnly(showHighlightRangeOnly);
  }

  public boolean showsHighlightRangeOnly() {
    return getSourceEditor().showsHighlightRangeOnly();
  }

  public void addRulerContextMenuListener(IMenuListener listener) {
    getSourceEditor().addRulerContextMenuListener(listener);
  }

  public boolean isEditorInputReadOnly() {
    return getSourceEditor().isEditorInputReadOnly();
  }

  public void removeRulerContextMenuListener(IMenuListener listener) {
    getSourceEditor().removeRulerContextMenuListener(listener);
  }

  public void setStatusField(IStatusField field, String category) {
    getSourceEditor().setStatusField(field, category);
  }

  public boolean isEditorInputModifiable() {
    return getSourceEditor().isEditorInputModifiable();
  }

  public boolean validateEditorInputState() {
    return getSourceEditor().validateEditorInputState();
  }

  public InsertMode getInsertMode() {
    return getSourceEditor().getInsertMode();
  }

  public boolean isChangeInformationShowing() {
    return getSourceEditor().isChangeInformationShowing();
  }

  public void setInsertMode(InsertMode mode) {
    getSourceEditor().setInsertMode(mode);
  }

  public void showChangeInformation(boolean show) {
    getSourceEditor().showChangeInformation(show);
  }

  public Annotation gotoAnnotation(boolean forward) {
    return getSourceEditor().gotoAnnotation(forward);
  }

  public void showRevisionInformation(RevisionInformation info, String quickDiffProviderId) {
    getSourceEditor().showRevisionInformation(info, quickDiffProviderId);
  }

  public INavigationLocation createEmptyNavigationLocation() {
    return getSourceEditor().createEmptyNavigationLocation();
  }

  public INavigationLocation createNavigationLocation() {
    return getSourceEditor().createNavigationLocation();
  }

  public Saveable[] getActiveSaveables() {
    return getSourceEditor().getActiveSaveables();
  }

  public Saveable[] getSaveables() {
    return getSourceEditor().getSaveables();
  }

  public void restoreState(IMemento memento) {
    getSourceEditor().restoreState(memento);
  }

  public void saveState(IMemento memento) {
    getSourceEditor().saveState(memento);
  }
}
