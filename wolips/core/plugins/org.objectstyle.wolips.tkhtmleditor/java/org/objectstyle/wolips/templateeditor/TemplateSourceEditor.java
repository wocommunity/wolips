package org.objectstyle.wolips.templateeditor;

import java.io.IOException;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.IWOEditor;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;

public class TemplateSourceEditor extends HTMLSourceEditor implements IWOEditor {
  private TemplateOutlinePage _templateOutlinePage;
  private WodParserCache _cache;

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
  public void createPartControl(Composite parent) {
    Composite templateParent = new Composite(parent, SWT.NONE);
    GridLayout templateLayout = new GridLayout();
    templateParent.setLayout(templateLayout);

    Composite editorParent = new Composite(templateParent, SWT.NONE);
    editorParent.setLayoutData(new GridData(GridData.FILL_BOTH));
    editorParent.setLayout(new FillLayout());
    super.createPartControl(editorParent);

    /*
    Composite breadcrumb = new Composite(templateParent, SWT.NONE);
    breadcrumb.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GREEN));
    breadcrumb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    RowLayout breadcrumbLayout = new RowLayout();
    breadcrumbLayout.wrap = false;
    breadcrumbLayout.pack = false;
    breadcrumbLayout.justify = false;
    breadcrumbLayout.type = SWT.HORIZONTAL;
    breadcrumbLayout.marginLeft = 5;
    breadcrumbLayout.marginTop = 5;
    breadcrumbLayout.marginRight = 5;
    breadcrumbLayout.marginBottom = 5;
    breadcrumbLayout.spacing = 0;
    breadcrumb.setLayout(breadcrumbLayout);

    Label b1 = new Label(breadcrumb, SWT.NONE);
    b1.setText("Breadcrumb 1");
    Label bw = new Label(breadcrumb, SWT.NONE);
    bw.setText("Breadcrumb 2");
    */
  }

  @Override
  protected void handleCursorPositionChanged() {
    super.handleCursorPositionChanged();
  }

  @Override
  public void dispose() {
    try {
      WodParserCache cache = TemplateSourceEditor.this.getParserCache();
      cache.setHtmlDocument(null);
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
      cache.setHtmlDocument(getDocumentProvider().getDocument(getEditorInput()));
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

      ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
        public void run(IProgressMonitor monitor) {
          try {
            WodParserCache cache = TemplateSourceEditor.this.getParserCache();
            cache.parseHtmlAndWodIfNecessary();
            cache.validate();
          }
          catch (Exception ex) {
            Activator.getDefault().log(ex);
          }
        }
      }, null);
    }
    catch (Exception ex) {
      HTMLPlugin.logException(ex);
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

  public IWodElement getSelectedElement() throws CoreException, LocateException, IOException {
    IWodElement wodElement = null;
    WodParserCache cache = getParserCache();
    ISelection realSelection = getSelectionProvider().getSelection();
    if (realSelection instanceof ITextSelection) {
      ITextSelection textSelection = (ITextSelection) realSelection;
      FuzzyXMLDocument doc;
      if (isDirty()) {
        boolean wo54 = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY);
        FuzzyXMLParser parser = new FuzzyXMLParser(wo54, true);
        doc = parser.parse(getHTMLSource());
      }
      else {
        doc = cache.getHtmlXmlDocument();
      }
      FuzzyXMLElement element = doc.getElementByOffset(textSelection.getOffset());
      if (element != null) {
        wodElement = WodHtmlUtils.getWodElement(element, false, cache);
      }
    }
    else if (realSelection instanceof IStructuredSelection) {
      IStructuredSelection structuredSelection = (IStructuredSelection) realSelection;
      Object obj = structuredSelection.getFirstElement();
      if (obj instanceof FuzzyXMLElement) {
        FuzzyXMLElement element = (FuzzyXMLElement) obj;
        wodElement = WodHtmlUtils.getWodElement(element, false, cache);
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

  public int widgetOffset2ModelOffset(int widgetOffset) {
    return AbstractTextEditor.widgetOffset2ModelOffset(getSourceViewer(), widgetOffset);
  }

  public Point getTagSelectionAtOffset(int modelOffset) throws BadLocationException, CoreException, LocateException, IOException {
    Point selection = null;

    WodParserCache cache = getParserCache();
    FuzzyXMLDocument xmlDocument = cache.getHtmlXmlDocument();
    if (xmlDocument != null) {
      FuzzyXMLElement selectedElement = xmlDocument.getElementByOffset(modelOffset);
      if (selectedElement != null) {
        IDocument doc = cache.getHtmlDocument();

        int openTagOffset = selectedElement.getOffset();
        int openTagLength = selectedElement.getOpenTagLength() + 2;
        int openTagEndOffset = openTagOffset + openTagLength;
        if (selectedElement.hasCloseTag()) {
          int closeTagOffset = selectedElement.getCloseTagOffset();
          int closeTagEndOffset = closeTagOffset + selectedElement.getCloseTagLength();
          //if (modelOffset > openTagEndOffset && modelOffset < selectedElement.getCloseTagOffset()) {
          if ((modelOffset >= openTagOffset && modelOffset < openTagEndOffset) || (modelOffset >= closeTagOffset && modelOffset < closeTagEndOffset)) {
            IRegion lineRegion = doc.getLineInformationOfOffset(openTagEndOffset);
            int lineEndOffset = lineRegion.getOffset() + lineRegion.getLength();
            if (openTagEndOffset == lineEndOffset) {
              openTagEndOffset++;
              openTagLength++;
            }
            selection = new Point(openTagOffset, selectedElement.getLength());
          }
          else {
            selection = new Point(modelOffset, 0);
          }
        }
        else {
          selection = new Point(selectedElement.getOffset(), selectedElement.getLength());
        }
      }
    }

    return selection;
  }

}
