package org.objectstyle.wolips.bindings.api;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.bindings.wod.BindingValidationRule;
import org.objectstyle.wolips.bindings.wod.TagShortcut;
import org.objectstyle.wolips.core.resources.types.LimitedLRUCache;

public class ApiCache {
  private Map<String, String> _elementNameToTypeCache;
  private Map<String, Wo> _elementTypeToApiCache;
  private Map<String, Boolean> _elementTypeToApiMissingCache;
  private String _tagShortcutsStr;
  private String _bindingValidationRulesStr;
  private List<TagShortcut> _tagShortcuts;
  private List<BindingValidationRule> _bindingValidationRules;

  public ApiCache() {
    clearCache();
  }

  public synchronized void clearCache() {
    _elementNameToTypeCache = new LimitedLRUCache<String, String>(100);
    _elementTypeToApiCache = new LimitedLRUCache<String, Wo>(100);
    _elementTypeToApiMissingCache = new LimitedLRUCache<String, Boolean>(100);
  }

  public synchronized void clearCacheForElementNamed(String elementName) {
    String elementTypeName = _elementNameToTypeCache.remove(elementName);
    if (elementTypeName != null) {
      clearCacheForElementTypeNamed(elementTypeName);
    }
  }

  public synchronized void clearCache(IType elementType) {
    clearApiForElementType(elementType);
    String elementName = elementType.getElementName();
    if (elementName != null) {
      _elementNameToTypeCache.remove(elementName);
    }
  }
  
  public synchronized void clearCacheForElementTypeNamed(String elementTypeName) {
    clearApiForElementTypeName(elementTypeName);
    _elementNameToTypeCache.remove(elementTypeName);
  }
  
  public void clearApiForElementType(IType type) {
    _elementTypeToApiMissingCache.remove(type);
    _elementTypeToApiCache.remove(type);
  }
  
  public void clearApiForElementTypeName(String elementTypeName) {
    _elementTypeToApiMissingCache.remove(elementTypeName);
    _elementTypeToApiCache.remove(elementTypeName);
  }

  public Boolean apiMissingForElementType(IType type) {
    Boolean missing = _elementTypeToApiMissingCache.get(type);
    return missing;
  }

  public void setApiMissingForElementType(boolean missing, IType type) {
    if (missing) {
      _elementTypeToApiMissingCache.put(type.getFullyQualifiedName(), Boolean.TRUE);
    }
    else {
      _elementTypeToApiMissingCache.put(type.getFullyQualifiedName(), Boolean.FALSE);
    }
  }

  public Wo getApiForType(IType type) {
    Wo wo = _elementTypeToApiCache.get(type);
    return wo;
  }

  public void setApiForType(Wo wo, IType type) {
    _elementTypeToApiCache.put(type.getFullyQualifiedName(), wo);
    setApiMissingForElementType(false, type);
  }
  
  public String getElementTypeNamed(String elementName) {
    return _elementNameToTypeCache.get(elementName);
  }
  
  public void setElementTypeForName(IType elementType, String elementName) {
    _elementNameToTypeCache.put(elementName, elementType.getFullyQualifiedName());
  }

  public TagShortcut getTagShortcutNamed(String shortcut) {
    TagShortcut matchingTagShortcut = null;
    for (TagShortcut tagShortcut : getTagShortcuts()) {
      if (matchingTagShortcut == null && tagShortcut.getShortcut().equalsIgnoreCase(shortcut)) {
        matchingTagShortcut = tagShortcut;
      }
    }
    return matchingTagShortcut;
  }

  public synchronized List<TagShortcut> getTagShortcuts() {
    String tagShortcutsStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TAG_SHORTCUTS_KEY);
    if ((tagShortcutsStr == null && _tagShortcutsStr != null) || (tagShortcutsStr != null && !tagShortcutsStr.equals(_tagShortcutsStr))) {
      _tagShortcutsStr = tagShortcutsStr;
      _tagShortcuts = TagShortcut.fromPreferenceString(tagShortcutsStr);
    }
    return _tagShortcuts;
  }

  public synchronized List<BindingValidationRule> getBindingValidationRules() {
    String bindingValidationRulesStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.BINDING_VALIDATION_RULES_KEY);
    if ((bindingValidationRulesStr == null && _bindingValidationRulesStr != null) || (bindingValidationRulesStr != null && !bindingValidationRulesStr.equals(_bindingValidationRulesStr))) {
      _bindingValidationRulesStr = bindingValidationRulesStr;
      _bindingValidationRules = BindingValidationRule.fromPreferenceString(bindingValidationRulesStr);
    }
    return _bindingValidationRules;
  }
}
