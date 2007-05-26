package tk.eclipse.plugin.jspeditor.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.editors.HTMLAutoEditStrategy;

/**
 * Provides auto inserting for JSPs.
 * 
 * <ul>
 *   <li><strong>${</strong> would be <strong>${}</strong></li>
 *   <li><strong>&lt%--</strong> would be <strong>&lt;%--  --%&gt;</strong></li>
 *   <li><del><strong>&lt;%</strong> would be <strong>&lt;% %&gt;</strong></del></li>
 * </ul>
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class JSPAutoEditStrategy extends HTMLAutoEditStrategy {
	
	@Override
  public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		try {
//			if("%".equals(c.text) && c.offset > 0){
//				if(d.getChar(c.offset - 1) == '<'){
//					c.text = "% %>";
//					c.shiftsCaret = false;
//					c.caretOffset = c.offset + 1;
//					c.doit = false;	
//					return;
//				}
//			}
			if("{".equals(c.text) && c.offset > 0){
				if(d.getChar(c.offset - 1) == '$'){
					c.text = "{}";
					c.shiftsCaret = false;
					c.caretOffset = c.offset + 1;
					c.doit = false;	
					return;
				}
			}
			if("-".equals(c.text) && c.offset >= 3 && d.get(c.offset - 3, 3).equals("<%-")){
				c.text = "-  --%>";
				c.shiftsCaret = false;
				c.caretOffset = c.offset + 2;
				c.doit = false;	
				return;
			}
		} catch (BadLocationException e) {
			HTMLPlugin.logException(e);
		}
		super.customizeDocumentCommand(d, c);
	}

}
