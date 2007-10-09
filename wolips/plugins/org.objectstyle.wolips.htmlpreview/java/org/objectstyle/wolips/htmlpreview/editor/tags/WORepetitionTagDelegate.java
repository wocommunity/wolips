package org.objectstyle.wolips.htmlpreview.editor.tags;

import java.util.Stack;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.htmlpreview.editor.TagDelegate;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WORepetitionTagDelegate extends TagDelegate {
	private boolean _cssAdded;

	@Override
	public void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes) {
		IWodBinding listBinding = wodElement.getBindingNamed("list");
		String listName;
		if (listBinding != null) {
			listName = listBinding.getValue();
		} else {
			listName = "WORepetition";
		}
		htmlBuffer.append("<span class = \"wodclipse_block wodclipse_WORepetition\"><span class = \"wodclipse_tag wodclipse_open_tag\">[loop " + listName + "]</span>");
		xmlElement.toXMLString(renderContext, htmlBuffer);
		htmlBuffer.append("<span class = \"wodclipse_tag wodclipse_close_tag\">[/loop " + listName + "]</span></span>");
		
		if (!_cssAdded) {
			cssBuffer.append("span.wodclipse_WORepetition {");
			cssBuffer.append("  /*border: 1px dashed green;*/");
			cssBuffer.append("}");
			cssBuffer.append("span.wodclipse_WORepetition span.wodclipse_tag {");
			cssBuffer.append("  color: green;");
			cssBuffer.append("}");
			_cssAdded = true;
		}
	}

	@Override
	public void reset() {
		_cssAdded = false;
	}

}
