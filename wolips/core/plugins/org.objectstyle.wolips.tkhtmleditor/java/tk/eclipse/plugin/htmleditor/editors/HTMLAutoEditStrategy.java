package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * Provdides auto inserting for HTML.
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class HTMLAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	
	private String charset = System.getProperty("file.encoding");
	
	public void setFile(IFile file){
		try {
			this.charset = file.getCharset();
		} catch(CoreException e){
			HTMLPlugin.logException(e);
		}
	}
	
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		try {
			if("-".equals(c.text) && c.offset >= 3 && d.get(c.offset - 3, 3).equals("<!-")){
				c.text = "-  -->";
				c.shiftsCaret = false;
				c.caretOffset = c.offset + 2;
				c.doit = false;	
				return;
			}
			if("[".equals(c.text) && c.offset >= 2 && d.get(c.offset - 2, 2).equals("<!")){
				c.text = "[CDATA[]]>";
				c.shiftsCaret = false;
				c.caretOffset = c.offset + 7;
				c.doit = false;	
				return;
			}
			if("l".equals(c.text) && c.offset >= 4 && d.get(c.offset - 4, 4).equals("<?xm")){
				c.text = "l version = \"1.0\" encoding = \"" + charset + "\"?>";
				return;
			}
		} catch (BadLocationException e) {
			HTMLPlugin.logException(e);
		}
		super.customizeDocumentCommand(d, c);
	}
	
}
