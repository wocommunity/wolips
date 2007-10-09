package org.objectstyle.wolips.templateeditor;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.WodHyperlink;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.IHyperlinkProvider;
import tk.eclipse.plugin.htmleditor.editors.HTMLHyperlinkInfo;

public class InlineWodElementHyperlinkProvider implements IHyperlinkProvider {
  public HTMLHyperlinkInfo getHyperlinkInfo(IFile file, FuzzyXMLDocument doc, FuzzyXMLElement element, String attrName, String attrValue, int offset) {
    HTMLHyperlinkInfo hyperlinkInfo = null;
    try {
      if (WodHtmlUtils.isWOTag(element.getName()) && WodHtmlUtils.isInline(element.getName())) {
        boolean wo54 = Activator.getDefault().isWO54();
        WodParserCache cache;
        cache = WodParserCache.parser(file);
        SimpleWodElement wodElement = WodHtmlUtils.toWodElement(element, wo54, cache.getApiCache());
        hyperlinkInfo = new HTMLHyperlinkInfo();
        hyperlinkInfo.setOffset(wodElement.getElementTypePosition().getOffset());
        hyperlinkInfo.setLength(wodElement.getElementTypePosition().getLength());
        hyperlinkInfo.setObject(WodHyperlink.toWodHyperlink(wodElement, cache));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return hyperlinkInfo;
  }
}