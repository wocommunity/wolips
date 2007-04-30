package tk.eclipse.plugin.jseditor.editors;

import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import tk.eclipse.plugin.htmleditor.ColorProvider;
import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.editors.FoldingInfo;
import tk.eclipse.plugin.htmleditor.editors.SoftTabVerifyListener;

/**
 * The JavaScript editor.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptOutlinePage
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptConfiguration
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptAssistProcessor
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptCharacterPairMatcher
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptHyperlinkDetector
 */
public class JavaScriptEditor extends TextEditor {

	private ColorProvider colorProvider;
	private SoftTabVerifyListener softTabListener;
	private JavaScriptOutlinePage outline;
	private JavaScriptCharacterPairMatcher pairMatcher;
	private ProjectionSupport fProjectionSupport;
	private JavaScriptHyperlinkDetector hyperlinkDetector;
	
	public static final String GROUP_JAVASCRIPT = "_javascript";
	public static final String ACTION_COMMENT = "_comment";
	
	/**
	 * The constructor.
	 */
	public JavaScriptEditor(){
		super();
		colorProvider = HTMLPlugin.getDefault().getColorProvider();
		setSourceViewerConfiguration(new JavaScriptConfiguration(colorProvider));
		setPreferenceStore(new ChainedPreferenceStore(
				new IPreferenceStore[]{
						getPreferenceStore(),
						HTMLPlugin.getDefault().getPreferenceStore()
				}));
		
		outline = new JavaScriptOutlinePage(this);
		
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		softTabListener = new SoftTabVerifyListener();
		softTabListener.setUseSoftTab(store.getBoolean(HTMLPlugin.PREF_USE_SOFTTAB));
		softTabListener.setSoftTabWidth(store.getInt(HTMLPlugin.PREF_SOFTTAB_WIDTH));
		
		setAction(ACTION_COMMENT,new CommentAction());
	}
	
	protected void updateSelectionDependentActions() {
		super.updateSelectionDependentActions();
		ITextSelection sel = (ITextSelection)getSelectionProvider().getSelection();
		if(sel.getText().equals("")){
			getAction(ACTION_COMMENT).setEnabled(false);
		} else {
			getAction(ACTION_COMMENT).setEnabled(true);
		}
	}
	
	protected ISourceViewer createSourceViewer(Composite parent,IVerticalRuler ruler, int styles) {
		ISourceViewer viewer= new ProjectionViewer(parent, ruler, fOverviewRuler, true, styles); 
		getSourceViewerDecorationSupport(viewer);
		viewer.getTextWidget().addVerifyListener(softTabListener);
		return viewer; 
	}
	
	protected final void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		menu.add(new Separator(GROUP_JAVASCRIPT));
		addAction(menu, GROUP_JAVASCRIPT, ACTION_COMMENT);
	}
	
	protected void doSetInput(IEditorInput input) throws CoreException {
		if(input instanceof IFileEditorInput){
			setDocumentProvider(new JavaScriptTextDocumentProvider());
		} else if(input instanceof IStorageEditorInput){
			setDocumentProvider(new JavaScriptFileDocumentProvider());
		} else {
			setDocumentProvider(new JavaScriptTextDocumentProvider());
		}
		super.doSetInput(input);
	}
	
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		update();
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
		fProjectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors()); 
		fProjectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
		updateFolding();
		
		StyledText widget = viewer.getTextWidget();
		widget.setTabs(
				getPreferenceStore().getInt(
						AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH));
		widget.addVerifyListener(softTabListener);
		
		ITextViewerExtension2 extension= (ITextViewerExtension2) getSourceViewer();
		pairMatcher = new JavaScriptCharacterPairMatcher();
		pairMatcher.setEnable(getPreferenceStore().getBoolean(HTMLPlugin.PREF_PAIR_CHAR));
		MatchingCharacterPainter painter = new MatchingCharacterPainter(getSourceViewer(), pairMatcher);
		painter.setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		extension.addPainter(painter);
		
		hyperlinkDetector = new JavaScriptHyperlinkDetector();
		viewer.setHyperlinkDetectors(new IHyperlinkDetector[]{hyperlinkDetector}, SWT.CTRL);
		
		update();
	}
	
	/**
	 * Updates internal status.
	 * 
	 * <ol>
	 *   <li>updates the outline view</li>
	 *   <li>updates the foldings</li>
	 *   <li>validates contents if the editor input is <code>IFileEditorInput</code></li>
	 *   <li>updates assist information if the editor input is <code>IFileEditorInput</code></li>
	 *   <li>updates hyperlink information if the editor input is <code>IFileEditorInput</code></li>
	 * </ol>
	 */
	protected void update(){
		outline.update();
		updateFolding();
		
		if(getEditorInput() instanceof IFileEditorInput){
			doValidate();
			
			IFileEditorInput input = (IFileEditorInput)getEditorInput();
			JavaScriptConfiguration config = (JavaScriptConfiguration)getSourceViewerConfiguration();
			config.getAssistProcessor().update(input.getFile());
			hyperlinkDetector.update(input.getFile());
		}
	}
	
	/**
	 * Invokes <code>JavaScriptValidator</code> to validate editing source code.
	 * 
	 * @see JavaScriptValidator
	 */
	protected void doValidate(){
		try {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						IFileEditorInput input = (IFileEditorInput)getEditorInput();
						new JavaScriptValidator(input.getFile()).doValidate();
					} catch(Exception ex){
					}
				}
			},null);
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}
	
	public void dispose() {
		if(getEditorInput() instanceof IFileEditorInput){
			try {
				ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						try {
							IFileEditorInput input = (IFileEditorInput)getEditorInput();
							HTMLProjectParams params = new HTMLProjectParams(input.getFile().getProject());
							if(params.getRemoveMarkers()){
								input.getFile().deleteMarkers(IMarker.PROBLEM,false,0);
							}
						} catch(Exception ex){
						}
					}
				}, null);
			} catch(Exception ex){
			}
		}
		
		pairMatcher.dispose();
		super.dispose();
	}
	
	public void doSaveAs() {
		super.doSaveAs();
		update();
	}
	
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return super.affectsTextPresentation(event) || colorProvider.affectsTextPresentation(event);
	}
	
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event){
		colorProvider.handlePreferenceStoreChanged(event);
//		updateAssistProperties(event);
		
		String key = event.getProperty();
		if(key.equals(HTMLPlugin.PREF_PAIR_CHAR)){
			boolean enable = ((Boolean)event.getNewValue()).booleanValue();
			pairMatcher.setEnable(enable);
		}
		
		super.handlePreferenceStoreChanged(event);
		softTabListener.preferenceChanged(event);
	}	
	
	protected void createActions() {
	    super.createActions();
	    // Add a content assist action
	    IAction action = new ContentAssistAction(
	    		HTMLPlugin.getDefault().getResourceBundle(),"ContentAssistProposal", this);
	    action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
	    setAction("ContentAssistProposal", action);
	}
	
	public Object getAdapter(Class adapter){
		if(IContentOutlinePage.class.equals(adapter)){
			return outline;
		}
		return super.getAdapter(adapter);
	}
	
	/**
	 * Update folding informations.
	 */
	private void updateFolding(){
		try {
			ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
			if(viewer==null){
				return;
			}
			ProjectionAnnotationModel model = viewer.getProjectionAnnotationModel();
			if(model==null){
				return;
			}
			
			IDocument doc = getDocumentProvider().getDocument(getEditorInput());
			String source = doc.get();
			
			ArrayList list = new ArrayList();
			Stack stack = new Stack();
			FoldingInfo prev = null;
			char quote = 0;
			boolean escape = false;
			
			for(int i=0;i<source.length();i++){
				char c = source.charAt(i);
				// skip string
				if(quote!=0 && escape==true){
					escape = false;
				} else if((prev==null || !prev.getType().equals("comment")) && (c=='"' || c=='\'')){
					if(quote==0){
						quote = c;
					} else if(quote == c){
						quote = 0;
					}
				} else if(quote!=0 && (c=='\\')){
					escape = true;
				} else if(quote!=0 && (c=='\n' || c=='\r')){
					quote = 0;
				// start comment
				} else if(c=='/' && source.length() > i+1 && quote==0){
					if(source.charAt(i+1)=='*'){
						prev = new FoldingInfo(i,-1,"comment");
						stack.push(prev);
						i++;
					}
				// end comment
				} else if(c=='*' && source.length() > i+1 && !stack.isEmpty() && quote==0){
					if(source.charAt(i+1)=='/' && prev.getType().equals("comment")){
						FoldingInfo info = (FoldingInfo)stack.pop();
						if(doc.getLineOfOffset(info.getStart())!=doc.getLineOfOffset(i)){
							list.add(new FoldingInfo(info.getStart(), i+2 + FoldingInfo.countUpLineDelimiter(source, i+2), "comment"));
						}
						prev = stack.isEmpty() ? null : (FoldingInfo)stack.get(stack.size()-1);
						i++;
					}
				// open blace
				} else if(c=='{' && quote==0){
					if(prev==null || !prev.getType().equals("comment")){
						if(findFunction(source, i)){
							prev = new FoldingInfo(i, -1, "function");
						} else {
							prev = new FoldingInfo(i, -1, "blace");
						}
						stack.push(prev);
					}
				// close blace
				} else if(c=='}' && prev!=null && !prev.getType().equals("comment") && quote==0){
					FoldingInfo info = (FoldingInfo)stack.pop();
					if(info.getType().equals("function") && doc.getLineOfOffset(info.getStart())!=doc.getLineOfOffset(i)){
						list.add(new FoldingInfo(info.getStart(), i+2 + FoldingInfo.countUpLineDelimiter(source, i+2), "function"));
					}
					prev = stack.isEmpty() ? null : (FoldingInfo)stack.get(stack.size()-1);
				}
			}
			
			FoldingInfo.applyModifiedAnnotations(model, list);
			
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}
	
	private boolean findFunction(String text, int pos){
		text = text.substring(0, pos);
		int index1 = text.lastIndexOf("function");
		int index2 = text.lastIndexOf("{");
		if(index1==-1){
			return false;
		} else if(index1 > index2){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * The action to toggle comment out selection range.
	 */
	private class CommentAction extends Action {
		
		public CommentAction(){
			super(HTMLPlugin.getResourceString("JavaScriptEditor.CommentAction"));
			setEnabled(false);
			setAccelerator(SWT.CTRL | '/');
		}
		
		public void run() {
			ITextSelection sel = (ITextSelection)getSelectionProvider().getSelection();
			IDocument doc = getDocumentProvider().getDocument(getEditorInput());
			String text = sel.getText();
			text = text.replaceAll("[\r\n \t]+$", ""); // rtrim
			int length = text.length();
			try {
				if(text.startsWith("//")){
					text = text.replaceAll("(^|\r\n|\r|\n)//","$1");
				} else {
					text = text.replaceAll("(^|\r\n|\r|\n)","$1//");
				}
				doc.replace(sel.getOffset(), length, text);
			} catch (BadLocationException e) {
				HTMLPlugin.logException(e);
			}
		}
	}	
	
}
