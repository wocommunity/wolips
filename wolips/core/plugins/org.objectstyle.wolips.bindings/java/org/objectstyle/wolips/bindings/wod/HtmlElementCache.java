package org.objectstyle.wolips.bindings.wod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HtmlElementCache {
  private Map<String, List<HtmlElementName>> _htmlElementCache;

  public HtmlElementCache() {
    _htmlElementCache = new HashMap<String, List<HtmlElementName>>();
  }

  public void addHtmlElement(HtmlElementName htmlElement) {
    List<HtmlElementName> htmlElementNames = _htmlElementCache.get(htmlElement.getName());
    if (htmlElementNames == null) {
      htmlElementNames = new LinkedList<HtmlElementName>();
      _htmlElementCache.put(htmlElement.getName(), htmlElementNames);
    }
    htmlElementNames.add(htmlElement);
  }

  public boolean containsElementNamed(String name) {
    List<HtmlElementName> htmlElementNames = _htmlElementCache.get(name);
    return htmlElementNames != null && htmlElementNames.size() > 0;
  }

  public List<HtmlElementName> getHtmlElementNames(String name) {
    return _htmlElementCache.get(name);
  }

  public Set<String> elementNames() {
    return _htmlElementCache.keySet();
  }

  public void clearCache() {
    _htmlElementCache.clear();
  }
}
