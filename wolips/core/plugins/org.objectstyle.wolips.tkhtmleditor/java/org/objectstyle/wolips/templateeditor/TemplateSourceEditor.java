package org.objectstyle.wolips.templateeditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;

public class TemplateSourceEditor extends HTMLSourceEditor {
  private TemplateOutlinePage _templateOutlinePage;

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
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
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
    IFileEditorInput input = (IFileEditorInput) getEditorInput();
    IFile inputFile = input.getFile();
    WodParserCache cache = WodParserCache.parser(inputFile);
    return cache;
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
}
