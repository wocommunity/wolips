package org.objectstyle.wolips.htmlpreview.editor.tags;

import java.util.Stack;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.htmlpreview.editor.TagDelegate;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WOGenericContainerTagDelegate extends TagDelegate {

	@Override
	public void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes) {
		IWodBinding elementNameBinding = wodElement.getBindingNamed("elementName");
		String elementName;
		if (elementNameBinding != null && !elementNameBinding.isKeyPath()) {
			elementName = elementNameBinding.getValue().replaceAll("\"", "");
		} else {
			elementName = "div";
		}
		htmlBuffer.append("<" + elementName + ">");
		xmlElement.toXMLString(renderContext, htmlBuffer);
		htmlBuffer.append("</" + elementName + ">");
	}
}
