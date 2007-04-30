package tk.eclipse.plugin.jspeditor.editors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLConfiguration;
import tk.eclipse.plugin.htmleditor.editors.HTMLEditor;
import tk.eclipse.plugin.htmleditor.editors.HTMLEditorPart;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;

/**
 * JSP editor.
 */
public class JSPEditor extends HTMLEditor {
	
	private JSPConfiguration configuration;
	
	public JSPEditor() {
		super();
	}
	
	protected HTMLSourceEditor createHTMLSourceEditor(HTMLConfiguration config) {
		return new JSPSourceEditor(config);
	}
	
	protected HTMLConfiguration getSourceViewerConfiguration() {
		if(configuration==null){
			configuration = new JSPConfiguration(HTMLPlugin.getDefault().getColorProvider());
		}
		return configuration;
	}
	
	/**
	 * Update preview.
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
			// replace JSP parts
			html = JSPPreviewConverter.convertJSP((IFileEditorInput)getEditorInput(),html);
			
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
			//ex.printStackTrace();
		}
	}
}
