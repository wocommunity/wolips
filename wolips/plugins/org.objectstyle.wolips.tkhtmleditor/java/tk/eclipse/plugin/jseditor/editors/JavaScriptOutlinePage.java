package tk.eclipse.plugin.jseditor.editors;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * ContentOutlinePage implementation for JavaScriptEditor.
 * 
 * @see tk.eclipse.plugin.jseditor.editors.JavaScriptEditor
 * @author Naoki Takezoe
 */
public class JavaScriptOutlinePage extends ContentOutlinePage {
	
	private JavaScriptModel model;
	private JavaScriptEditor editor;
	
	public JavaScriptOutlinePage(JavaScriptEditor editor) {
		super();
		this.editor = editor;
	}
	
	@Override
  public void createControl(Composite parent) {
		super.createControl(parent);
		model = new JavaScriptModel(editor.getDocumentProvider().getDocument(editor.getEditorInput()).get());
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new JavaScriptContentProvider());
		viewer.setLabelProvider(new JavaScriptLabelProvider());
		viewer.addSelectionChangedListener(new JavaScriptSelectionChangedListener());
		viewer.setInput(model);
		update();
	}
	
	public void update(){
		try {
			model.update(editor.getDocumentProvider().getDocument(editor.getEditorInput()).get());
			getTreeViewer().refresh();
		} catch(Throwable t){
		}
	}
	
	/** ITreeContentProvider implementation for JavaScriptOutlinePage. */
	private class JavaScriptContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof JavaScriptContext){
				return ((JavaScriptContext)parentElement).getChildren();
			}
			return new Object[0];
		}
		public Object getParent(Object element) {
			if(element instanceof JavaScriptContext){
				return ((JavaScriptContext)element).getParent();
			}
			return null;
		}
		public boolean hasChildren(Object element) {
			if(getChildren(element).length==0){
				return false;
			} else {
				return true;
			}
		}
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	/** ISelectionChangedListener implementation for JavaScriptOutlinePage. */
	private class JavaScriptSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection sel = (IStructuredSelection)event.getSelection();
			JavaScriptElement func = (JavaScriptElement)sel.getFirstElement();
			if(func!=null){
				editor.selectAndReveal(func.getOffset(), 0);
			}
		}
	}
	
	/** LabelProvider implementation for JavaScriptOutlinePage. */
	private class JavaScriptLabelProvider extends LabelProvider {
		@Override
    public Image getImage(Object element){
			if(element instanceof JavaScriptFunction){
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_FUNCTION);
			}
			if(element instanceof JavaScriptVariable){
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_VARIABLE);
			}
			return null;
		}
	}
}
