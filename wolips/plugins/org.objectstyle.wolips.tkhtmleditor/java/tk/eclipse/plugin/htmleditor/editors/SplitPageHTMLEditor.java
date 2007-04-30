package tk.eclipse.plugin.htmleditor.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.PopupMenuExtender;
import org.eclipse.ui.part.EditorPart;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/** The split style HTML editor. */
public class SplitPageHTMLEditor extends EditorPart implements IResourceChangeListener,HTMLEditorPart {
	
	/** HTML source editor */
	private HTMLSourceEditor editor;
	/** Browser widget for preview */
	private Browser browser;
	/** wrapper */
	private HTMLEditor wrapper;
	/** horizontal split or vertical split */
	private boolean isHorizontal;
	/** EditorSite */
	private SplitEditorSite site;
	
	
	public SplitPageHTMLEditor(HTMLEditor wrapper,boolean isHorizontal,HTMLSourceEditor editor) {
		super();
		this.wrapper = wrapper;
		this.isHorizontal = isHorizontal;
		this.editor = editor;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	public Browser getBrowser() {
		return browser;
	}
	
	public HTMLSourceEditor getSourceEditor() {
		return editor;
	}
	
	public void doSave(IProgressMonitor monitor) {
		editor.doSave(monitor);
		wrapper.updatePreview();
	}

	public void doSaveAs() {
		editor.doSaveAs();
		setInput(editor.getEditorInput());
		setPartName(getEditorInput().getName());
		wrapper.updatePreview();
	}

	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getName());
	}

	public boolean isDirty() {
		if(editor!=null){
			return editor.isDirty();
		}
		return false;
	}

	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public void dispose() {		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		site.dispose();
		super.dispose();
	}
	
	public void createPartControl(Composite parent) {
		try {
			// Don't split when EditorInput isn't IFileEditorInput
			if(!(getEditorInput() instanceof IFileEditorInput)){
				editor.init(getEditorSite(), getEditorInput());
				editor.addPropertyListener(new IPropertyListener() {
					public void propertyChanged(Object source, int propertyId) {
						firePropertyChange(propertyId);
					}
				});
				editor.createPartControl(parent);
				return;
			}
			
			SashForm sash = null;
			if(isHorizontal){
				sash = new SashForm(parent,SWT.VERTICAL);
			} else {
				sash = new SashForm(parent,SWT.HORIZONTAL);
			}
			site = new SplitEditorSite(editor, getEditorSite());
			editor.init(site, getEditorInput());
			editor.addPropertyListener(new IPropertyListener() {
				public void propertyChanged(Object source, int propertyId) {
					firePropertyChange(propertyId);
				}
			});
			editor.createPartControl(sash);
			browser = new Browser(sash,SWT.NONE);
			wrapper.updatePreview();
		} catch (PartInitException e) {
			HTMLPlugin.logException(e);
			ErrorDialog.openError(getSite().getShell(),
				"Error creating nested text editor",null,e.getStatus()); //$NON-NLS-1$
		}
	}

	public void setFocus() {
		editor.setFocus();
	}
	
	public void gotoMarker(IMarker marker) {
		IDE.gotoMarker(editor, marker);
	}
	
	public void setOffset(int offset){
		editor.selectAndReveal(offset,0);
	}
	
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.POST_CHANGE){
			final IEditorInput input = editor.getEditorInput();
			if(input instanceof IFileEditorInput){
				Display.getDefault().asyncExec(new Runnable(){
					public void run(){
						IFile file = ((IFileEditorInput)input).getFile();
						if(!file.exists()){
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							page.closeEditor(SplitPageHTMLEditor.this, false);
						} else if(!getPartName().equals(file.getName())){
							setPartName(file.getName());
						}							
					}        
				});
			}
		}
	}
	
	public boolean isFileEditorInput(){
		return editor.isFileEditorInput();
	}
	
	public Object getAdapter(Class adapter) {
		return editor.getAdapter(adapter);
	}
	
	protected void firePropertyChange(int propertyId) {
		super.firePropertyChange(propertyId);
		wrapper.firePropertyChange2(propertyId);
	}
	
	/**
	 * An implementaion of IEditorSite for the split editor.
	 */
	private static class SplitEditorSite implements IEditorSite {
		
		private HTMLSourceEditor editor;
		private IEditorSite site;
		private ArrayList menuExtenders;

		
		public SplitEditorSite(HTMLSourceEditor editor, IEditorSite site){
			this.editor = editor;
			this.site = site;
		}
		
		public IEditorActionBarContributor getActionBarContributor() {
			return site.getActionBarContributor();
		}
		
		public IActionBars getActionBars() {
			return site.getActionBars();
		}
		
		public String getId() {
			return site.getId();
		}
		
		public IKeyBindingService getKeyBindingService() {
			return site.getKeyBindingService();
		}
		
		public String getPluginId() {
			return site.getPluginId();
		}
		
		public String getRegisteredName() {
			return site.getRegisteredName();
		}
		
		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
			site.registerContextMenu(menuManager, selectionProvider);
		}
		
		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider) {
			if (menuExtenders == null) {
				menuExtenders = new ArrayList(1);
			}
			menuExtenders.add(new PopupMenuExtender(menuId, menuManager, selectionProvider, editor));
		}
		
		public IWorkbenchPage getPage() {
			return site.getPage();
		}
		
		public ISelectionProvider getSelectionProvider() {
			return site.getSelectionProvider();
		}
		
		public Shell getShell() {
			return site.getShell();
		}
		
		public IWorkbenchWindow getWorkbenchWindow() {
			return site.getWorkbenchWindow();
		}
		
		public void setSelectionProvider(ISelectionProvider provider) {
			site.setSelectionProvider(provider);
		}
		
		public Object getAdapter(Class adapter) {
			return site.getAdapter(adapter);
		}
		
		public void dispose() {
			if (menuExtenders != null) {
				for (int i = 0; i < menuExtenders.size(); i++) {
					((PopupMenuExtender)menuExtenders.get(i)).dispose();
				}
				menuExtenders = null;
			}
		}
		
		// for Eclipse 3.1
		
		public IWorkbenchPart getPart() {
			return editor;
		}

		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
			this.registerContextMenu(menuManager, selectionProvider);
		}

		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
			this.registerContextMenu(menuId, menuManager, selectionProvider);
		}
		
		// for Eclipse 3.2
		
		public Object getService(Class api) {
			return null;
		}

		public boolean hasService(Class api) {
			return false;
		}
	}
}
