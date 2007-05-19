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

    //    JSPConfiguration config = (JSPConfiguration) getSourceViewerConfiguration();
    //    JSPDirectiveAssistProcessor directiveProcessor = config.getDirectiveAssistProcessor();
    //    directiveProcessor.update(this);
    //    JSPScriptletAssistProcessor scriptletProcessor = config.getScriptletAssistProcessor();
    //    scriptletProcessor.update(this);

    //    IEditorInput input = getEditorInput();
    //    if (input instanceof IFileEditorInput) {
    //      JSPAutoEditStrategy autoEdit = (JSPAutoEditStrategy) config.getAutoEditStrategy();
    //      autoEdit.setFile(((IFileEditorInput) input).getFile());
    //    }
  }

  //  /** The action to comment JSP */
  //  private class JSPCommentAction extends Action {
  //
  //    public JSPCommentAction() {
  //      super(HTMLPlugin.getResourceString("HTMLEditor.JSPCommentAction"));
  //      setEnabled(false);
  //      setAccelerator(SWT.CTRL | SWT.ALT | '/');
  //    }
  //
  //    public void run() {
  //      ITextSelection sel = (ITextSelection) getSelectionProvider().getSelection();
  //      IDocument doc = getDocumentProvider().getDocument(getEditorInput());
  //      String text = sel.getText().trim();
  //      try {
  //        if (text.startsWith("<%--") && text.endsWith("--%>")) {
  //          text = sel.getText().replaceAll("<%--|--%>", "");
  //          doc.replace(sel.getOffset(), sel.getLength(), text);
  //        }
  //        else {
  //          doc.replace(sel.getOffset(), sel.getLength(), "<%--" + sel.getText() + "--%>");
  //        }
  //      }
  //      catch (BadLocationException e) {
  //        HTMLPlugin.logException(e);
  //      }
  //    }
  //  }

  //  /** An action to toggle breakpoints */
  //  private class ToggleBreakPointAction extends Action {
  //
  //    public ToggleBreakPointAction() {
  //      super(HTMLPlugin.getResourceString("JSPEditor.ToggleBreakPoint"));
  //      setEnabled(true);
  //    }
  //
  //    public void run() {
  //      IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
  //      IBreakpoint[] breakpoints = manager.getBreakpoints();
  //      IEditorInput input = getEditorInput();
  //      IResource resource = (IResource) input.getAdapter(IFile.class);
  //      if (resource == null) {
  //        resource = (IResource) input.getAdapter(IResource.class);
  //      }
  //      int lineNumber = getVerticalRuler().getLineOfLastMouseButtonActivity() + 1;
  //      for (int i = 0; i < breakpoints.length; i++) {
  //        if (breakpoints[i] instanceof IJavaStratumLineBreakpoint) {
  //          IJavaStratumLineBreakpoint breakpoint = (IJavaStratumLineBreakpoint) breakpoints[i];
  //          if (breakpoint.getMarker().getResource().equals(resource)) {
  //            try {
  //              if (breakpoint.getLineNumber() == lineNumber) {
  //                breakpoint.delete();
  //                return;
  //              }
  //            }
  //            catch (Exception ex) {
  //              HTMLPlugin.logException(ex);
  //            }
  //          }
  //        }
  //      }
  //      try {
  //        // TODO set a valid position!!
  //        int pos = getValidPosition(getViewer().getDocument(), lineNumber);
  //        System.out.println(pos);
  //        JDIDebugModel.createStratumBreakpoint(resource, "JSP", resource.getName(), null, "*", lineNumber, pos, pos, 0, true, null);
  //      }
  //      catch (Exception ex) {
  //        HTMLPlugin.logException(ex);
  //      }
  //    }
  //
  //    /**
  //     * Finds a valid position somewhere on lineNumber in document, idoc, where
  //     * a breakpoint can be set and returns that position. -1 is returned if a
  //     * position could not be found.
  //     * 
  //     * @param idoc
  //     * @param editorLineNumber
  //     * @return position to set breakpoint or -1 if no position could be found
  //     */
  //    private int getValidPosition(IDocument idoc, int editorLineNumber) {
  //      int result = -1;
  //      if (idoc != null) {
  //
  //        int startOffset = 0;
  //        int endOffset = 0;
  //        try {
  //          IRegion line = idoc.getLineInformation(editorLineNumber - 1);
  //          startOffset = line.getOffset();
  //          endOffset = Math.max(line.getOffset(), line.getOffset() + line.getLength());
  //
  //          String lineText = idoc.get(startOffset, endOffset - startOffset).trim();
  //
  //          // blank lines or lines with only an open or close brace or
  //          // scriptlet tag cannot have a breakpoint
  //          if (lineText.equals("") || lineText.equals("{") || lineText.equals("}") || lineText.equals("<%")) {
  //            result = -1;
  //          }
  //          else {
  //            // get all partitions for current line
  //            ITypedRegion[] partitions = null;
  //            partitions = idoc.computePartitioning(startOffset, endOffset - startOffset);
  //
  //            for (int i = 0; i < partitions.length; ++i) {
  //              String type = partitions[i].getType();
  //              // if found jsp java content, jsp directive tags,
  //              // custom
  //              // tags,
  //              // return that position
  //              if (type == HTMLPartitionScanner.HTML_COMMENT || type == HTMLPartitionScanner.HTML_DIRECTIVE) {
  //                result = partitions[i].getOffset();
  //              }
  //            }
  //          }
  //        }
  //        catch (BadLocationException e) {
  //          result = -1;
  //        }
  //      }
  //      return result;
  //    }
  //  }

}
