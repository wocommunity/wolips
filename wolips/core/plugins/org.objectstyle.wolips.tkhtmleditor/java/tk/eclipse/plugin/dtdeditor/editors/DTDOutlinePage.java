package tk.eclipse.plugin.dtdeditor.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.aonir.fuzzyxml.internal.FuzzyXMLUtil;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.IHTMLOutlinePage;

/**
 * IContentOutlinePage implementation for DTDEditor.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.dtdeditor.editors.DTDEditor
 */
public class DTDOutlinePage extends ContentOutlinePage implements IHTMLOutlinePage {
	
	private DTDEditor editor;
	private DTDRootNode root;
	
	public DTDOutlinePage(DTDEditor editor){
		this.editor = editor;
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		if(root==null){
			root = new DTDRootNode();
		}
		viewer.setContentProvider(new DTDContentProvider());
		viewer.setLabelProvider(new DTDLabelProvider());
		viewer.setInput(root);
		viewer.addSelectionChangedListener(new DTDSelectionChangedListener());
		
		update();
	}
	
	
	public void update() {
		String source = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		source = FuzzyXMLUtil.comment2space(source, true);
		
		if(root==null){
			root = new DTDRootNode();
		}
		root.clear();
		
		int index = 0;
		int last = 0;
		
		Map attrMap = new HashMap();
		Map elementMap = new HashMap();
		
		while((index = source.indexOf("<!", last))>=0){
			if(source.startsWith("<!ELEMENT",index)){
				String text = source.substring(index, source.indexOf(">", index)+1);
				text = text.replaceAll("\\s+"," ");
				String[] dim = text.split(" ");
				DTDNode node = new DTDNode(index, text, HTMLPlugin.ICON_ELEMENT);
				elementMap.put(dim[1], node);
				root.add(node);
			}
			else if(source.startsWith("<!ATTLIST",index)){
				String text = source.substring(index, source.indexOf(">", index)+1);
				text = text.replaceAll("\\s+"," ");
				String[] dim = text.split(" ");
				List list = (List)attrMap.get(dim[1]);
				if(list==null){
					list = new ArrayList();
					attrMap.put(dim[1], list);
				}
				list.add(new DTDNode(index, text, HTMLPlugin.ICON_ATTLIST));
			}
			else if(source.startsWith("<!ENTITY",index)){
				String text = source.substring(index, source.indexOf(">", index)+1);
				text = text.replaceAll("\\s+"," ");
				root.add(new DTDNode(index, text, HTMLPlugin.ICON_ENTITY));
			}
			else if(source.startsWith("<!NOTATION",index)){
				String text = source.substring(index, source.indexOf(">", index)+1);
				text = text.replaceAll("\\s+"," ");
				root.add(new DTDNode(index, text, HTMLPlugin.ICON_NOTATE));
			}
//			else if(source.startsWith("<!--", index)){
//				root.add(new DTDNode(index, "#comment", HTMLPlugin.ICON_COMMENT));
//			}
			last = index + 2;
		}
		
		// put ATTLIST to ELEMENT
		for(Iterator ite = attrMap.entrySet().iterator(); ite.hasNext();){
			Map.Entry entry = (Map.Entry)ite.next();
			String key = (String)entry.getKey();
			List attrs = (List)entry.getValue();
			DTDNode element = (DTDNode)elementMap.get(key);
			for(int i=0;i<attrs.size();i++){
				DTDNode attr = (DTDNode)attrs.get(i);
				if(element==null){
					root.add(attr);
				} else {
					element.addChild(attr);
				}
			}
		}
		
		TreeViewer viewer = getTreeViewer();
		if(viewer!=null){
			viewer.refresh();
		}
	}
	
	/** The root node of this outline page. */
	private class DTDRootNode {
		
		private List children = new ArrayList();
		
		public void add(DTDNode node){
			children.add(node);
		}
		
		public DTDNode[] getChildren(){
			return (DTDNode[])this.children.toArray(new DTDNode[this.children.size()]);
		}
		
		public void clear(){
			this.children.clear();
		}
		
	}
	
	/** The node class for DTD elements. */
	private class DTDNode {
		
		private int position;
		private String text;
		private String image;
		private List children = new ArrayList();
		private DTDNode parent;
		
		public DTDNode(int position, String text, String image){
			this.position = position;
			this.text = text;
			this.image = image;
		}
		
		public int getPosition(){
			return this.position;
		}
		
		public void addChild(DTDNode node){
			this.children.add(node);
		}
		
		public DTDNode[] getChildren(){
			return (DTDNode[])this.children.toArray(new DTDNode[this.children.size()]);
		}
		
		public void setParent(DTDNode parent){
			this.parent = parent;
		}
		
		public DTDNode getParent(){
			return this.parent;
		}
		
		public String toString(){
			return this.text;
		}
		
		public String getImage(){
			return this.image;
		}
	}
	
	/** ContentProvider of HTMLOutlinePage. */
	private class DTDContentProvider implements ITreeContentProvider {
		
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof DTDRootNode){
				return ((DTDRootNode)parentElement).getChildren();
				
			} else if(parentElement instanceof DTDNode){
				return ((DTDNode)parentElement).getChildren();
			}
			return new Object[0];
		}
		
		public Object getParent(Object element) {
			if(element instanceof DTDNode){
				DTDNode parent = ((DTDNode)element).getParent();
				if(parent==null){
					return root;
				} else {
					return parent;
				}
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
		
		public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {
		}
	}
	
	/** LabelProvider of HTMLOutlinePage */
	private class DTDLabelProvider extends LabelProvider {
		
		public Image getImage(Object element) {
			if(element instanceof DTDNode){
				return HTMLPlugin.getDefault().getImageRegistry().get(((DTDNode)element).getImage());
			}
			return null;
		}
		
	}
	
	/** This listener is called when selection of TreeViewer is changed. */
	private class DTDSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection sel = (IStructuredSelection)event.getSelection();
			Object element = sel.getFirstElement();
			if(element instanceof DTDNode){
				int offset = ((DTDNode)element).getPosition();
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart editorPart = page.getActiveEditor();
				if(editorPart instanceof DTDEditor){
					((DTDEditor)editorPart).selectAndReveal(offset, 0);
				}
			}
		}
	}
	
}
