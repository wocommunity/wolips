package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.util.HashSet;
import java.util.Set;

import org.objectstyle.wolips.baseforplugins.util.StringUtilities;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;

public class ElementRename {
  private String _oldName;
  private String _newName;

  public ElementRename(String oldName, String newName) {
    _oldName = oldName;
    _newName = newName;
  }

  public String getOldName() {
    return _oldName;
  }

  public String getNewName() {
    return _newName;
  }

  public static ElementRename newUniqueName(IWodModel wodModel, IWodElement wodElement, boolean forceRename) {
    return ElementRename.newUniqueName(wodModel, wodElement, new HashSet<String>(), forceRename);
  }

  public static ElementRename newUniqueName(IWodModel wodModel, IWodElement wodElement, Set<String> elementNames, boolean forceRename) {
    ElementRename elementRename = null;
    String typeName = wodElement.getElementType();
    String shortTypeName = typeName;
    if (typeName.startsWith("WO")) {
      shortTypeName = typeName.substring("WO".length());
    }

    String elementName = wodElement.getElementName();
    if (forceRename || (elementName.startsWith(shortTypeName) && StringUtilities.isDigitsOnly(elementName.substring(shortTypeName.length())))) {
      String newName = null;
      boolean forceAppendNumber = false;

      if ("WOConditional".equals(typeName)) {
        String conditionValue = (String) wodElement.getBindingsMap().get("condition");
        String negateValue = (String) wodElement.getBindingsMap().get("negate");
        String prefix = null;
        if (negateValue != null && (negateValue.equalsIgnoreCase("true") || negateValue.equalsIgnoreCase("yes"))) {
          prefix = "Not";
        }
        newName = newNameFromBindingValue(prefix, conditionValue, null);
      }
      else if ("WOString".equals(typeName)) {
        String value = (String) wodElement.getBindingsMap().get("value");
        newName = newNameFromBindingValue(null, value, null);
      }
      else if ("WOSubmitButton".equals(typeName)) {
        String action = (String) wodElement.getBindingsMap().get("action");
        if (action != null) {
          newName = newNameFromBindingValue(null, action + "Button", null);
        }
        else {
          newName = "Button";
        }
      }
      else if ("WOHyperlink".equals(typeName)) {
        String action = (String) wodElement.getBindingsMap().get("action");
        if (action != null) {
          newName = newNameFromBindingValue(null, action + "Link", null);
        }
        else {
          newName = "Link";
        }
      }
      else if ("WORepetition".equals(typeName)) {
        String list = (String) wodElement.getBindingsMap().get("list");
        newName = newNameFromBindingValue(null, list, null);
      }
      else if ("WOGenericContainer".equals(typeName) || "WOGenericElement".equals(typeName)) {
        String genericElementName = (String) wodElement.getBindingsMap().get("elementName");
        newName = newNameFromBindingValue(null, genericElementName, null);
        forceAppendNumber = true;
      }
      else if (forceRename) {
        newName = typeName;
      }

      if (newName != null) {
        String uniqueNewName = newName;
        int counter = 1;
        while ((counter == 1 && forceAppendNumber) || wodModel.getElementNamed(uniqueNewName) != null || elementNames.contains(uniqueNewName)) {
          uniqueNewName = newName + String.valueOf(counter++);
        }
        elementRename = new ElementRename(elementName, uniqueNewName);
        elementNames.add(uniqueNewName);
      }
    }
    return elementRename;
  }

  public static String newNameFromBindingValue(String prefix, String value, String suffix) {
    String newName = null;
    if (value != null && !value.startsWith("\"~")) {
      value = value.replaceAll(" ", "_");
      value = value.replaceAll("[\"^]", "");
      value = value.replaceAll("[@,]", ".");
      String[] keys = value.split("\\.");
      StringBuffer newNameBuffer = new StringBuffer();
      if (prefix != null) {
        newNameBuffer.append(prefix);
      }
      for (int i = 0; i < keys.length; i++) {
        if (keys[i].length() > 0) {
          newNameBuffer.append(keys[i].substring(0, 1).toUpperCase());
          newNameBuffer.append(keys[i].substring(1));
        }
      }
      if (suffix != null) {
        newNameBuffer.append(suffix);
      }
      newName = newNameBuffer.toString();
    }
    return newName;
  }
}