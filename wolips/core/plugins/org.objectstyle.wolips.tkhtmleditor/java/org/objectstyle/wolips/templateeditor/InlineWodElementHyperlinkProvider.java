package org.objectstyle.wolips.templateeditor;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.SimpleWodElement;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.IHyperlinkProvider;
import tk.eclipse.plugin.htmleditor.editors.HTMLHyperlinkInfo;

public class InlineWodElementHyperlinkProvider implements IHyperlinkProvider {
  public HTMLHyperlinkInfo getHyperlinkInfo(IFile file, FuzzyXMLDocument doc, FuzzyXMLElement element, String attrName, String attrValue, int offset) {
    HTMLHyperlinkInfo hyperlinkInfo = null;
    try {
      if (WodHtmlUtils.isWOTag(element.getName()) && WodHtmlUtils.isInline(element.getName())) {
        boolean wo54 = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY);
        WodParserCache cache;
        cache = WodParserCache.parser(file);
        SimpleWodElement wodElement = WodHtmlUtils.toWodElement(element, wo54, cache);
        hyperlinkInfo = new HTMLHyperlinkInfo();
        hyperlinkInfo.setOffset(wodElement.getElementTypePosition().getOffset());
        hyperlinkInfo.setLength(wodElement.getElementTypePosition().getLength());
        hyperlinkInfo.setObject(wodElement.toWodHyperlink(cache));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return hyperlinkInfo;
  }
}