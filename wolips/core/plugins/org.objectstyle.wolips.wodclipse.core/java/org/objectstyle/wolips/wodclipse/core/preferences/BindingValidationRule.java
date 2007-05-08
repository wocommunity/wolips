package org.objectstyle.wolips.wodclipse.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.wodclipse.core.Activator;

public class BindingValidationRule {
  private String _typeRegex;
  private String _validBindingRegex;

  public BindingValidationRule(String typeRegex, String validBindingRegex) {
    _typeRegex = typeRegex;
    _validBindingRegex = validBindingRegex;
  }
  
  public String getTypeRegex() {
    return _typeRegex;
  }
  
  public void setTypeRegex(String typeRegex) {
    _typeRegex = typeRegex;
  }
  
  public String getValidBindingRegex() {
    return _validBindingRegex;
  }
  
  public void setValidBindingRegex(String validBindingRegex) {
    _validBindingRegex = validBindingRegex;
  }
  
  @Override
  protected BindingValidationRule clone() {
    return new BindingValidationRule(getTypeRegex(), getValidBindingRegex());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BindingValidationRule) {
      BindingValidationRule shortcut = (BindingValidationRule) obj;
      return getTypeRegex().equals(shortcut.getTypeRegex()) && getValidBindingRegex().equals(shortcut.getValidBindingRegex());
    }
    return false;
  }

  public static boolean hasChange(List<BindingValidationRule> rules1, List<BindingValidationRule> rules2) {
    if (rules1.size() != rules2.size()) {
      return true;
    }
    for (int i = 0; i < rules1.size(); i++) {
      BindingValidationRule rule1 = rules1.get(i);
      BindingValidationRule rule2 = rules2.get(i);
      if (!rule1.equals(rule2)) {
        return true;
      }
    }
    return false;
  }

  public static String _loadFromPreference(boolean defaults) {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    String value = null;
    if (defaults) {
      value = store.getDefaultString(PreferenceConstants.BINDING_VALIDATION_RULES_KEY);
    }
    else {
      value = store.getString(PreferenceConstants.BINDING_VALIDATION_RULES_KEY);
    }
    return value;
  }

  public static List<BindingValidationRule> loadFromPreference(boolean defaults) {
    String value = BindingValidationRule._loadFromPreference(defaults);
    List<BindingValidationRule> list = new ArrayList<BindingValidationRule>();
    if (value != null) {
      String[] values = value.split("\n");
      for (int i = 0; i < values.length; i++) {
        String[] split = values[i].split("\t");
        if (split.length >= 2) {
          String typeRegex = split[0];
          String validBindingRegex = split[1];
          list.add(new BindingValidationRule(typeRegex, validBindingRegex));
        }
      }
    }
    return list;
  }

  public static void saveToPreference(List<BindingValidationRule> list) {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      BindingValidationRule rules = list.get(i);
      sb.append(rules.getTypeRegex());
      sb.append("\t");
      sb.append(rules.getValidBindingRegex());
      sb.append("\n");
    }
    store.setValue(PreferenceConstants.BINDING_VALIDATION_RULES_KEY, sb.toString());
  }

  public static void saveDefaultToPreference(List<BindingValidationRule> list) {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      BindingValidationRule rule = list.get(i);
      sb.append(rule.getTypeRegex());
      sb.append("\t");
      sb.append(rule.getValidBindingRegex());
      sb.append("\n");
    }
    store.setDefault(PreferenceConstants.TAG_SHORTCUTS_KEY, sb.toString());
  }
}
