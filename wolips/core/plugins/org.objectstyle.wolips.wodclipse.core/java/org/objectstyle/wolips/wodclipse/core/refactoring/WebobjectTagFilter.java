package org.objectstyle.wolips.wodclipse.core.refactoring;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.util.NodeFilter;

import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class WebobjectTagFilter implements NodeFilter {
  private boolean _findWod;
  private boolean _findInline;

  public WebobjectTagFilter(boolean findWod, boolean findInline) {
    _findWod = findWod;
    _findInline = findInline;
  }

  public boolean filter(FuzzyXMLNode node) {
    boolean matches = false;
    if (node instanceof FuzzyXMLElement) {
      FuzzyXMLElement element = (FuzzyXMLElement) node;
      String elementTagName = element.getName();
      if (WodHtmlUtils.isWOTag(elementTagName)) {
        boolean inline = WodHtmlUtils.isInline(elementTagName);
        boolean wod = !inline;
        if (inline == _findInline && wod == _findWod) {
          matches = matches(element, inline);
        }
      }
    }
    return matches;
  }

  protected boolean matches(FuzzyXMLElement element, boolean inline) {
    return true;
  }

}
