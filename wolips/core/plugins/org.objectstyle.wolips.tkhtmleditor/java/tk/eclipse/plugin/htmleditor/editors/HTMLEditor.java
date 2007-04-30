package tk.eclipse.plugin.htmleditor.editors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.views.IPaletteTarget;

/**
 * This is the HTML editor that supports tabbed and split style.
 * <p>
 * In the tabbed style, this editor uses MultiPageHTMLEditor,
 * and in the split style, uses SplitPageHTMLEditor.
 * And this class transfers the call of most methods to them.
 * 
 * @see tk.eclipse.plugin.htmleditor.editors.MultiPageHTMLEditor
 * @see tk.eclipse.plugin.htmleditor.editors.SplitPageHTMLEditor
 */
public class HTMLEditor extends EditorPart implements IPaletteTarget {
	
	protected EditorPart editor;
	protected File prevTempFile = null;
	
	public HTMLEditor() {
		super();
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String type = store.getString(HTMLPlugin.PREF_EDITOR_TYPE);
		if(type.equals("horizontal")){
			editor = new SplitPageHTMLEditor(this,true,createHTMLSourceEditor(getSourceViewerConfiguration()));
		} else if(type.equals("vertical")){
			editor = new SplitPageHTMLEditor(this,false,createHTMLSourceEditor(getSourceViewerConfiguration()));
		} else if(type.equals("tab")){
			editor = new MultiPageHTMLEditor(this,createHTMLSourceEditor(getSourceViewerConfiguration()));
		} else {
			editor = createHTMLSourceEditor(getSourceViewerConfiguration());
			editor.addPropertyListener(new IPropertyListener() {
				public void propertyChanged(Object source, int propertyId) {
					firePropertyChange(propertyId);
				}
			});
		}
	}
	
	protected HTMLConfiguration getSourceViewerConfiguration(){
	    return new HTMLConfiguration(HTMLPlugin.getDefault().getColorProvider());
	}
	
	protected HTMLSourceEditor createHTMLSourceEditor(HTMLConfiguration config){
		return new HTMLSourceEditor(config);
	}
	
	public HTMLSourceEditor getPaletteTarget() {
		if(editor instanceof HTMLSourceEditor){
			return (HTMLSourceEditor)editor;
		} else {
			return ((HTMLEditorPart)editor).getSourceEditor();
		}
	}
	
	/**
	 * Update preview
	 */
	public void updatePreview(){
		if(!(editor instanceof HTMLEditorPart)){
			return;
		}
	    try {
			if(!((HTMLEditorPart)editor).isFileEditorInput()){
				return;
			}
			
			// write to temporary file
		    HTMLEditorPart editor = (HTMLEditorPart)this.editor;
		    IFileEditorInput input = (IFileEditorInput)this.editor.getEditorInput();
		    String charset = input.getFile().getCharset();
			String html    = editor.getSourceEditor().getDocumentProvider().getDocument(input).get();
			// replace JSP part
			//html = HTMLUtil.convertJSP(html);
			
			File tmpFile = editor.getSourceEditor().getTempFile();
			FileOutputStream out = new FileOutputStream(tmpFile);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, charset), true); 
			pw.write(html);
			pw.close();
			
			if(prevTempFile!=null && prevTempFile.equals(tmpFile)){
				editor.getBrowser().refresh();
			} else {
				if(prevTempFile!=null){
					prevTempFile.delete();
				}
				prevTempFile = tmpFile;
				editor.getBrowser().setUrl("file://" + tmpFile.getAbsolutePath()); //$NON-NLS-1$
			}
	    } catch(Exception ex){
	    	HTMLPlugin.logException(ex);
	    }
	}
	
	public void createPartControl(Composite parent) {
		editor.createPartControl(parent);
	}
	
	public void dispose() {
		editor.dispose();
		super.dispose();
	}
	public void doSave(IProgressMonitor monitor) {
		editor.doSave(monitor);
		//updateFlag = true;
	}
	public void doSaveAs() {
		editor.doSaveAs();
		//updateFlag = true;
	}
  
//	public boolean equals(Object arg0) {
//		return editor.equals(arg0);
//	}
	public Object getAdapter(Class adapter) {
		return editor.getAdapter(adapter);
	}
	public String getContentDescription() {
		return editor.getContentDescription();
	}
	public IEditorInput getEditorInput() {
		return editor.getEditorInput();
	}
	public IEditorSite getEditorSite() {
		return editor.getEditorSite();
	}
	public String getPartName() {
		return editor.getPartName();
	}
	public IWorkbenchPartSite getSite() {
		return editor.getSite();
	}
	public String getTitle() {
		return editor.getTitle();
	}
	public Image getTitleImage() {
		return editor.getTitleImage();
	}
	public String getTitleToolTip() {
		return editor.getTitleToolTip();
	}
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		editor.init(site, input);
	}
	public boolean isDirty() {
		return editor.isDirty();
	}
	public boolean isSaveAsAllowed() {
		return editor.isSaveAsAllowed();
	}
	public boolean isSaveOnCloseNeeded() {
		return editor.isSaveOnCloseNeeded();
	}
	public void setFocus() {
		editor.setFocus();
	}
	public void setInitializationData(IConfigurationElement config,String propertyName, Object data){
		editor.setInitializationData(config, propertyName, data);
	}
	public void showBusy(boolean busy) {
		editor.showBusy(busy);
	}
	/** change to the source editor, and move calet to the specified offset. */
	public void setOffset(int offset){
		if(editor instanceof SplitPageHTMLEditor){
			((SplitPageHTMLEditor)editor).setOffset(offset);
		} else if(editor instanceof MultiPageHTMLEditor){
			((MultiPageHTMLEditor)editor).setOffset(offset);
		} else if(editor instanceof HTMLSourceEditor){
			((HTMLSourceEditor)editor).selectAndReveal(offset, 0);
		}
	}
	
	public void firePropertyChange2(int propertyId) {
		super.firePropertyChange(propertyId);
	}
	
}
