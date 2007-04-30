package org.objectstyle.wolips.wodclipse.core.completion;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;
import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.core.preferences.TagShortcut;

public class WodParserCacheContext {
  private Map _elementNameToTypeCache;
  private Map _elementTypeToWoCache;
  private String _tagShortcutsStr;
  private List<TagShortcut> _tagShortcuts;

  public WodParserCacheContext() {
    clearCache();
  }

  public synchronized void clearCache() {
    _elementNameToTypeCache = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.WEAK);
    _elementTypeToWoCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);
  }

  public synchronized void clearCache(String elementName) {
    IType elementType = (IType) _elementNameToTypeCache.remove(elementName);
    if (elementType != null) {
      _elementTypeToWoCache.remove(elementType);
    }
  }

  public synchronized void clearCache(IType elementType) {
    Wo wo = (Wo) _elementTypeToWoCache.remove(elementType);
    String elementName = elementType.getElementName();
    if (elementName != null) {
      _elementNameToTypeCache.remove(elementName);
    }
  }

  public Map getElementNameToTypeCache() {
    return _elementNameToTypeCache;
  }

  public Map getElementTypeToWoCache() {
    return _elementTypeToWoCache;
  }

  public synchronized List<TagShortcut> getTagShortcuts() {
    String tagShortcutsStr = TagShortcut._loadFromPreference(false);
    if ((tagShortcutsStr == null && _tagShortcutsStr != null) || (tagShortcutsStr != null && !tagShortcutsStr.equals(_tagShortcutsStr))) {
      _tagShortcutsStr = tagShortcutsStr;
      _tagShortcuts = TagShortcut.loadFromPreference(false);
    }
    return _tagShortcuts;
  }
}
