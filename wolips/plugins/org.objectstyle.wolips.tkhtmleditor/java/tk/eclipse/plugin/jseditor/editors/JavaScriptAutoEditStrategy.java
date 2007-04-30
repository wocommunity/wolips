package tk.eclipse.plugin.jseditor.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

/**
 * Provides auto inserting for JavaScript.
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class JavaScriptAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		if(command.text.equals("{")){
			append(command, "}", 1);
			return;
		}
		if(command.text.equals("(")){
			append(command, ")", 1);
			return;
		}
		if(command.text.equals("\"")){
			append(command, "\"", 1);
			return;
		}
		if(command.text.equals("'")){
			append(command, "'", 1);
			return;
		}
		try {
			if(command.text.equals("*") && command.offset > 0 && document.getChar(command.offset - 1) == '/'){
				append(command, "  */", 2);
				return;
			}
		} catch(BadLocationException e){
		}
		super.customizeDocumentCommand(document, command);
	}
	
	private void append(DocumentCommand command, String append, int position){
		command.text = command.text + append;
		command.doit = false;
		command.shiftsCaret = false;
		command.caretOffset = command.offset + position;
	}
	
	
}
