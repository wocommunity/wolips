package org.objectstyle.wolips.htmlpreview.editor.tags;

import java.util.Stack;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.htmlpreview.editor.TagDelegate;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WOConditionalTagDelegate extends TagDelegate {
	private boolean _cssAdded;

	@Override
	public void renderNode(IWodElement wodElement, FuzzyXMLElement xmlElement, RenderContext renderContext, StringBuffer htmlBuffer, StringBuffer cssBuffer, Stack<WodParserCache> caches, Stack<FuzzyXMLNode> nodes) {
		IWodBinding conditionBinding = wodElement.getBindingNamed("condition");
		String conditionName;
		if (conditionBinding != null) {
			conditionName = conditionBinding.getValue();
		} else {
			conditionName = "WOConditional";
		}
		IWodBinding negateBinding = wodElement.getBindingNamed("negate");
		if (negateBinding != null && ("true".equalsIgnoreCase(negateBinding.getValue()) || "yes".equalsIgnoreCase(negateBinding.getValue()))) {
			conditionName = "!" + conditionName;
		}
		htmlBuffer.append("<span class = \"wodclipse_block wodclipse_WOConditional\"><span class = \"wodclipse_tag wodclipse_open_tag\">[if " + conditionName + "]</span>");
		xmlElement.toXMLString(renderContext, htmlBuffer);
		htmlBuffer.append("<span class = \"wodclipse_tag wodclipse_close_tag\">[/if " + conditionName + "]</span></span>");

		if (!_cssAdded) {
			cssBuffer.append("span.wodclipse_WOConditional {");
			cssBuffer.append("  /*border: 1px dashed blue;*/");
			cssBuffer.append("}");
			cssBuffer.append("span.wodclipse_WOConditional span.wodclipse_tag {");
			cssBuffer.append("  color: blue;");
			cssBuffer.append("}");
			_cssAdded = true;
		}
	}

	@Override
	public void reset() {
		_cssAdded = false;
	}
}
