package tk.eclipse.plugin.jseditor.editors;

import org.eclipse.jface.text.IDocument;

import tk.eclipse.plugin.htmleditor.editors.AbstractCharacterPairMatcher;

public class JavaScriptCharacterPairMatcher extends AbstractCharacterPairMatcher { //implements ICharacterPairMatcher {
	
	public JavaScriptCharacterPairMatcher() {
		addQuoteCharacter('\'');
		addQuoteCharacter('"');
		addBlockCharacter('{', '}');
		addBlockCharacter('(', ')');
	}
	
	protected String getSource(IDocument doc){
		return JavaScriptUtil.removeComments(doc.get());
	}
	
}
