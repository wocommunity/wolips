package org.objectstyle.wolips.wodclipse.core.refactoring;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

public class NamedWebobjectTagFilter extends WebobjectTagFilter {
  private String _name;

  public NamedWebobjectTagFilter(String name) {
    super(true, false);
    _name = name;
  }

  @Override
  protected boolean matches(FuzzyXMLElement element, boolean inline) {
    boolean matches = false;
    String woTagName = element.getAttributeValue("name");
    if (_name.equals(woTagName)) {
      matches = true;
    }
    return matches;
  }

}
