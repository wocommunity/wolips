package org.objectstyle.wolips.htmlpreview.editor.tags;

import java.util.Stack;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.htmlpreview.editor.TagDelegate;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class ERXStyleSheetTagDelegate extends TagDelegate {

	@Override
	public void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes) {
		String cssUrl = TagDelegate.getResourceUrl("framework", "filename", "href", wodElement, caches);
		htmlBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssUrl + "\"");
		String media = wodElement.getBindingValue("media");
		if (media != null) {
			htmlBuffer.append(" media=\"" + media + "\"");
		}
		htmlBuffer.append("/>");
	}

}
