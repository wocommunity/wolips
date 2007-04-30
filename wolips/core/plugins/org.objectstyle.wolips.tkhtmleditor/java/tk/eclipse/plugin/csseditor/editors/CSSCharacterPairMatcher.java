package tk.eclipse.plugin.csseditor.editors;

import org.eclipse.jface.text.IDocument;

import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.editors.AbstractCharacterPairMatcher;

/**
 * @author Naoki Takezoe
 */
public class CSSCharacterPairMatcher extends AbstractCharacterPairMatcher {

	public CSSCharacterPairMatcher() {
		addBlockCharacter('{', '}');
	}
	
	protected String getSource(IDocument document){
		return HTMLUtil.cssComment2space(document.get());
	}

}
