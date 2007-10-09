package org.objectstyle.wolips.bindings.wod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HtmlElementCache {
	private Map<String, HtmlElementName> _htmlElementCache;

	public HtmlElementCache() {
		_htmlElementCache = new HashMap<String, HtmlElementName>();
	}

	public void addHtmlElement(HtmlElementName htmlElement) {
		_htmlElementCache.put(htmlElement.getName(), htmlElement);
	}
	
	public boolean containsElementNamed(String name) {
		return _htmlElementCache.containsKey(name);
	}
	
	public Set<String> elementNames() {
	  return _htmlElementCache.keySet();
	}
	
	public void clearCache() {
		_htmlElementCache.clear();
	}
}
