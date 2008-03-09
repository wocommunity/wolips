package org.objectstyle.wolips.templateeditor;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.ITextWOEditor;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;

public class TemplateSourceEditor extends HTMLSourceEditor implements ITextWOEditor {
  private TemplateOutlinePage _templateOutlinePage;
  private WodParserCache _cache;
  private TemplateBreadcrumb _breadcrumb;
  private boolean _cacheOutOfSync;

  public TemplateSourceEditor(HTMLConfiguration config) {
    super(config);
    //    setAction(ACTION_JSP_COMMENT, new JSPCommentAction());
    //    setAction(ACTION_TOGGLE_BREAKPOINT, new ToggleBreakPointAction());
  }

  @Override
  protected void initializeKeyBindingScopes() {
    setKeyBindingScopes(new String[] { "org.objectstyle.wolips.componenteditor.componentEditorScope" }); //$NON-NLS-1$
  }

  @Override
  protected void doSetInput(IEditorInput input) throws CoreException {
    super.doSetInput(input);
    _cache = null;
  }

  @Override
  public MarkerAnnotationPreferences getAnnotationPreferences() {
    return super.getAnnotationPreferences();
  }

  @Override
  public AnnotationPreferenceLookup getAnnotationPreferenceLookup() {
    return super.getAnnotationPreferenceLookup();
  }

  @Override
  public void createPartControl(Composite parent) {
    Composite templateParent = new Composite(parent, SWT.NONE);
    GridLayout templateLayout = new GridLayout();
    templateLayout.marginTop = 0;
    templateLayout.marginLeft = 0;
    templateLayout.marginBottom = 0;
    templateLayout.marginRight = 0;
    templateLayout.marginHeight = 0;
    templateLayout.marginWidth = 0;
    templateLayout.verticalSpacing = 0;
    templateLayout.horizontalSpacing = 0;
    templateParent.setLayout(templateLayout);

    Composite editorParent = new Composite(templateParent, SWT.NONE);
    editorParent.setLayoutData(new GridData(GridData.FILL_BOTH));
    FillLayout editorLayout = new FillLayout();
    editorLayout.marginHeight = 0;
    editorLayout.marginWidth = 0;
    editorParent.setLayout(editorLayout);
    super.createPartControl(editorParent);

    _breadcrumb = new TemplateBreadcrumb(this, templateParent, SWT.NONE);
    _breadcrumb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    getViewer().addTextListener(new ITextListener() {
      public void textChanged(TextEvent event) {
        setCacheOutOfSync(true);
      }
    });

  }

  protected void setCacheOutOfSync(boolean cacheOutOfSync) {
    _cacheOutOfSync = cacheOutOfSync;
  }

  @Override
  protected void handleCursorPositionChanged() {
    super.handleCursorPositionChanged();
  }

  public Point getSelectedRange() {
    return getSourceViewer().getSelectedRange();
  }

  @Override
  public void dispose() {
    try {
      WodParserCache cache = getParserCache();
      cache.getHtmlEntry().setDocument(null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    super.dispose();

  }

  @Override
  public void update() {
    try {
      WodParserCache cache = TemplateSourceEditor.this.getParserCache();
      cache.getHtmlEntry().setDocument(getDocumentProvider().getDocument(getEditorInput()));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    super.update();
  }

  @Override
  protected void doValidate() {
    //    if(!isFileEditorInput()){
    //      return;
    //    }
    try {
      String[] natureIds = HTMLPlugin.getDefault().getNoValidationNatureId();
      IFile file = ((IFileEditorInput) getEditorInput()).getFile();
      for (int i = 0; i < natureIds.length; i++) {
        if (file.getProject().hasNature(natureIds[i])) {
          return;
        }
      }

      boolean autoBuild = ResourcesPlugin.getPlugin().getPluginPreferences().getDefaultBoolean(ResourcesPlugin.PREF_AUTO_BUILDING);
      if (!autoBuild) {
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
          public void run(IProgressMonitor monitor) {
            _validate();
          }
        }, null);
      }
    }
    catch (Exception ex) {
      HTMLPlugin.logException(ex);
    }
  }

  protected void _validate() {
    try {
      WodParserCache cache = TemplateSourceEditor.this.getParserCache();
      cache.parse();
      cache.validate();
      if (_breadcrumb != null) {
        _breadcrumb.updateBreadcrumb();
      }
    }
    catch (Exception ex) {
      Activator.getDefault().log(ex);
    }
  }

  public WodParserCache getParserCache() throws CoreException, LocateException {
    if (_cache == null) {
      IFileEditorInput input = (IFileEditorInput) getEditorInput();
      IFile inputFile = input.getFile();
      _cache = WodParserCache.parser(inputFile);
    }
    return _cache;
  }

  @Override
  protected void handleElementContentReplaced() {
    super.handleElementContentReplaced();
  }

  public FuzzyXMLDocument getHtmlXmlDocument(boolean refreshModel) throws Exception {
    FuzzyXMLDocument doc;
    if (refreshModel || isDirty()) {
      boolean wo54 = org.objectstyle.wolips.bindings.Activator.getDefault().isWO54();
      FuzzyXMLParser parser = new FuzzyXMLParser(wo54, true);
      doc = parser.parse(getHTMLSource());
      getParserCache().getHtmlEntry().setModel(doc);
      setCacheOutOfSync(false);
    }
    else {
      doc = getParserCache().getHtmlEntry().getModel();
    }
    return doc;
  }

  public IWodElement getSelectedElement(boolean resolveWodElement, boolean refreshModel) throws Exception {
    IWodElement wodElement = null;
    WodParserCache cache = getParserCache();
    if (getSelectionProvider() != null) {
      boolean wo54 = org.objectstyle.wolips.bindings.Activator.getDefault().isWO54();
      ISelection realSelection = getSelectionProvider().getSelection();
      if (realSelection instanceof ITextSelection) {
        ITextSelection textSelection = (ITextSelection) realSelection;
        FuzzyXMLDocument document = getHtmlXmlDocument(refreshModel);
        if (document != null) {
          FuzzyXMLElement element = document.getElementByOffset(textSelection.getOffset());
          wodElement = WodHtmlUtils.getWodElement(element, wo54, resolveWodElement, cache);
        }
      }
      else if (realSelection instanceof IStructuredSelection) {
        IStructuredSelection structuredSelection = (IStructuredSelection) realSelection;
        Object obj = structuredSelection.getFirstElement();
        if (obj instanceof FuzzyXMLElement) {
          FuzzyXMLElement element = (FuzzyXMLElement) obj;
          wodElement = WodHtmlUtils.getWodElement(element, wo54, resolveWodElement, cache);
        }
      }
    }
    return wodElement;
  }

  public TemplateOutlinePage getTemplateOutlinePage() {
    if (_templateOutlinePage == null) {
      _templateOutlinePage = (TemplateOutlinePage) createOutlinePage();
    }
    return _templateOutlinePage;
  }

  @Override
  protected IHTMLOutlinePage createOutlinePage() {
    _templateOutlinePage = new TemplateOutlinePage(this);
    return _templateOutlinePage;
  }

  //  public String getHTMLSource(){
  //    String source = super.getHTMLSource();
  //    source = HTMLUtil.scriptlet2space(source,false);
  //    return source;
  //  }

  @Override
  protected void addContextMenuActions(IMenuManager menu) {
    super.addContextMenuActions(menu);
    //addAction(menu, GROUP_HTML, ACTION_JSP_COMMENT);
  }

  @Override
  protected void updateSelectionDependentActions() {
    super.updateSelectionDependentActions();
    //    if (sel.getText().equals("")) {
    //      getAction(ACTION_JSP_COMMENT).setEnabled(false);
    //    }
    //    else {
    //      getAction(ACTION_JSP_COMMENT).setEnabled(true);
    //    }
  }

  @Override
  protected void rulerContextMenuAboutToShow(IMenuManager menu) {
    super.rulerContextMenuAboutToShow(menu);
  }

  /**
   * Update informations about code-completion.
   */
  @Override
  protected void updateAssist() {
    super.updateAssist();
  }

  public int getOffsetAtPoint(Point point) {
    StyledText st = getViewer().getTextWidget();
    int modelOffset;
    if (!st.getBounds().contains(point)) {
      modelOffset = -1;
    }
    else {
      try {
        int offset = st.getOffsetAtLocation(point);
        modelOffset = AbstractTextEditor.widgetOffset2ModelOffset(getSourceViewer(), offset);
      }
      catch (IllegalArgumentException e) {
        modelOffset = -1;
      }
    }
    return modelOffset;
  }

  public ISourceViewer getWOSourceViewer() {
    return getViewer();
  }

  public StyledText getWOEditorControl() {
    return getViewer().getTextWidget();
  }

  public IWodElement getWodElementAtPoint(Point point, boolean resolveWodElement, boolean refreshModel) throws Exception {
    IWodElement wodElement = null;
    FuzzyXMLElement element = getElementAtPoint(point, refreshModel);
    if (WodHtmlUtils.isWOTag(element)) {
      WodParserCache cache = getParserCache();
      boolean wo54 = org.objectstyle.wolips.bindings.Activator.getDefault().isWO54();
      wodElement = WodHtmlUtils.getWodElement(element, wo54, resolveWodElement, cache);
    }
    return wodElement;
  }

  public FuzzyXMLElement getElementAtPoint(Point point, boolean refreshModel) throws Exception {
    int offset = getOffsetAtPoint(point);
    FuzzyXMLElement element = getElementAtOffset(offset, refreshModel);
    return element;
  }

  public FuzzyXMLElement getElementAtOffset(int offset, boolean refreshModel) throws Exception {
    FuzzyXMLElement element = null;
    if (offset >= 0) {
      FuzzyXMLDocument xmlDocument = getHtmlXmlDocument(refreshModel);
      if (xmlDocument != null) {
        element = xmlDocument.getElementByOffset(offset);
      }
    }
    return element;
  }

  public IRegion getSelectionRegionAtPoint(Point point, boolean regionForInsert, boolean refreshModel) throws Exception {
    int offset = getOffsetAtPoint(point);
    return getSelectionRegionAtOffset(offset, regionForInsert, refreshModel);
  }

  public IRegion getSelectionRegionForElementAtPoint(FuzzyXMLElement element, Point point, boolean regionForInsert) throws Exception {
    int offset = getOffsetAtPoint(point);
    return getSelectionRegionForElementAtOffset(element, offset, regionForInsert);
  }

  public IRegion getSelectionRegionAtOffset(int offset, boolean regionForInsert, boolean refreshModel) throws Exception {
    FuzzyXMLElement element = getElementAtOffset(offset, refreshModel);
    IRegion region = getSelectionRegionForElementAtOffset(element, offset, regionForInsert);
    return region;
  }

  public IRegion getSelectionRegionForElementAtOffset(FuzzyXMLElement element, int offset, boolean regionForInsert) throws Exception {
    IRegion region = null;
    if (element != null) {
      IDocument doc = getParserCache().getHtmlEntry().getDocument();
      region = element.getRegionAtOffset(offset, doc, regionForInsert);
    }
    return region;
  }

  @Override
  protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer) {
    if (fSourceViewerDecorationSupport == null) {
      fSourceViewerDecorationSupport = new TemplateSourceViewerDecorationSupport(viewer, getOverviewRuler(), getAnnotationAccess(), getSharedColors());
      configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
    }
    return fSourceViewerDecorationSupport;
  }

}
