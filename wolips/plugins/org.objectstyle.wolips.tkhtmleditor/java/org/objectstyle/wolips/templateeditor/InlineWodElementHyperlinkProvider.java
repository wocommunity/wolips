package org.objectstyle.wolips.templateeditor;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.WodBindingValueHyperlink;
import org.objectstyle.wolips.wodclipse.core.document.WodElementTypeHyperlink;
import org.objectstyle.wolips.wodclipse.core.util.FuzzyXMLWodElement;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.IHyperlinkProvider;
import tk.eclipse.plugin.htmleditor.editors.HTMLHyperlinkInfo;

public class InlineWodElementHyperlinkProvider implements IHyperlinkProvider {
  public HTMLHyperlinkInfo getHyperlinkInfo(IFile file, FuzzyXMLDocument doc, FuzzyXMLElement element, String attrName, String attrValue, int offset) {
    HTMLHyperlinkInfo hyperlinkInfo = null;
    try {
      if (WodHtmlUtils.isWOTag(element.getName()) && WodHtmlUtils.isInline(element.getName())) {
        BuildProperties buildProperties = (BuildProperties)file.getProject().getAdapter(BuildProperties.class);
        if (attrName == null) {
          WodParserCache cache = WodParserCache.parser(file);
          SimpleWodElement wodElement = new FuzzyXMLWodElement(element, buildProperties);
          if (wodElement.isTypeWithin(new Region(offset, 0))) {
            hyperlinkInfo = new HTMLHyperlinkInfo();
            hyperlinkInfo.setOffset(wodElement.getElementTypePosition().getOffset());
            hyperlinkInfo.setLength(wodElement.getElementTypePosition().getLength());
            hyperlinkInfo.setObject(WodElementTypeHyperlink.toElementTypeHyperlink(wodElement, cache));
          }
        }
        else {
          WodParserCache cache;
          cache = WodParserCache.parser(file);
          SimpleWodElement wodElement = new FuzzyXMLWodElement(element, buildProperties);
          IWodBinding wodBinding = wodElement.getBindingNamed(attrName);
          if (wodBinding != null) {
            Position valuePosition = wodBinding.getValuePosition();
            if (valuePosition != null) {
              hyperlinkInfo = new HTMLHyperlinkInfo();
              hyperlinkInfo.setOffset(valuePosition.getOffset());
              hyperlinkInfo.setLength(valuePosition.getLength());
              hyperlinkInfo.setObject(WodBindingValueHyperlink.toBindingValueHyperlink(wodElement, attrName, cache));
            }
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return hyperlinkInfo;
  }
}