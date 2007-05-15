package org.objectstyle.wolips.wodclipse.core.refactoring;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.util.NodeFilter;

import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class NamedWebobjectTagFilter implements NodeFilter {
  private String _name;

  public NamedWebobjectTagFilter(String name) {
    _name = name;
  }

  public boolean filter(FuzzyXMLNode node) {
    boolean matches = false;
    if (node instanceof FuzzyXMLElement) {
      FuzzyXMLElement element = (FuzzyXMLElement) node;
      String elementTagName = element.getName();
      if (WodHtmlUtils.isWOTag(elementTagName) && !WodHtmlUtils.isInline(elementTagName)) {
        String woTagName = element.getAttributeValue("name");
        if (_name.equals(woTagName)) {
          matches = true;
        }
      }
    }
    return matches;
  }

}
