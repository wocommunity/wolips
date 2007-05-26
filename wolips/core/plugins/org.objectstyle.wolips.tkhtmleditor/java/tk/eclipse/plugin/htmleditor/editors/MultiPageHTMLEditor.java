package tk.eclipse.plugin.htmleditor.editors;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * The tabbed style HTML editor.
 * 
 * @author Naoki Takezoe
 */
public class MultiPageHTMLEditor extends MultiPageEditorPart implements IResourceChangeListener, HTMLEditorPart {

	/** HTML source editor */
	private HTMLSourceEditor _editor;
	/** Browser widget for preview */
	private Browser _browser;
	/** wrapper */
	private HTMLEditor _wrapper;
	
	public MultiPageHTMLEditor(HTMLEditor wrapper,HTMLSourceEditor editor) {
		super();
		this._wrapper = wrapper;
		this._editor  = editor;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	public Browser getBrowser() {
		return _browser;
	}
	
	public HTMLSourceEditor getSourceEditor() {
		return _editor;
	}
	
//	protected IEditorSite createSite(IEditorPart editor) {
//		return new SourceEditorSite(this,editor,getEditorSite());
//	}
	
	private void createPage0() {
		try {
			int index = addPage(_editor, getEditorInput());
			setPageText(index, HTMLPlugin.getResourceString("MultiPageHTMLEditor.Source")); //$NON-NLS-1$
			setPartName(getEditorInput().getName());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
				"Error creating nested text editor",null,e.getStatus()); //$NON-NLS-1$
		}
	}
	
	private void createPage1() {
		if(isFileEditorInput()){
			_browser = new Browser(getContainer(),SWT.NONE);
			int index = addPage(_browser);
			setPageText(index, HTMLPlugin.getResourceString("MultiPageHTMLEditor.Preview")); //$NON-NLS-1$
		}
	}

	@Override
  protected void createPages() {
		createPage0();
		createPage1();
	}
	
	@Override
  public void dispose() {
		// テンポラリファイルがあったら削除する
		if(isFileEditorInput()){
			File tmpFile = _editor.getTempFile();
			if(tmpFile.exists()){
				tmpFile.delete();
			}
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	
	@Override
  public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	
	@Override
  public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
	}
	
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	
	@Override
  public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);
	}
	
	public boolean isFileEditorInput(){
		return _editor.isFileEditorInput();
	}
	
	@Override
  public boolean isSaveAsAllowed() {
		return true;
	}
	
	@Override
  protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if(newPageIndex==1){
		    _wrapper.updatePreview();
		}
	}
	
	/** Change to the source editor, and move calet to the specified offset. */
	public void setOffset(int offset){
		setActivePage(0);
		_editor.selectAndReveal(offset,0);
	}
	
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.POST_CHANGE){
			final IEditorInput input = _editor.getEditorInput();
			if(input instanceof IFileEditorInput){
				Display.getDefault().asyncExec(new Runnable(){
					public void run(){
						IFile file = ((IFileEditorInput)input).getFile();
						if(!file.exists()){
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							page.closeEditor(MultiPageHTMLEditor.this, false);
						} else if(!getPartName().equals(file.getName())){
							setPartName(file.getName());
						}							
					}        
				});
			}
		}
	}
	
	@Override
  public Object getAdapter(Class adapter) {
		return _editor.getAdapter(adapter);
	}
	
	@Override
  protected void firePropertyChange(int propertyId) {
		super.firePropertyChange(propertyId);
		_wrapper.firePropertyChange2(propertyId);
	}
	
//	/** IEditorSite for the source editor. */
//	private static class SourceEditorSite extends MultiPageEditorSite {
//		
//		private HTMLSourceEditor editor = null;
//		private IEditorSite site;
//		private ArrayList menuExtenders;
//		
//		public SourceEditorSite(MultiPageEditorPart multiPageEditor,IEditorPart editor,IEditorSite site) {
//			super(multiPageEditor, editor);
//			this.site = site;
//			this.editor = (HTMLSourceEditor)editor;
//		}
//		
//		public IEditorActionBarContributor getActionBarContributor() {
//			return site.getActionBarContributor();
//		}
//		
//		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider) {
//			if(editor != null){
//				if (menuExtenders == null) {
//					menuExtenders = new ArrayList(1);
//				}
//				menuExtenders.add(new PopupMenuExtender(menuId, menuManager, selectionProvider, editor));
//			}
//		}
//		
//		public void dispose(){
//			if (menuExtenders != null) {
//				for (int i = 0; i < menuExtenders.size(); i++) {
//					((PopupMenuExtender)menuExtenders.get(i)).dispose();
//				}
//				menuExtenders = null;
//			}
//			super.dispose();
//		}
//	}

}
