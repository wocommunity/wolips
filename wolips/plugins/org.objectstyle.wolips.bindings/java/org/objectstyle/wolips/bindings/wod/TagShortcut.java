package org.objectstyle.wolips.bindings.wod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagShortcut {
  private String _shortcut;
  private String _actual;
  private Map<String, String> _attributes;

  public TagShortcut(String shortcut, String actual) {
    this(shortcut, actual, new HashMap<String, String>());
  }

  public TagShortcut(String shortcut, String actual, String attributesStr) {
    this(shortcut, actual, new HashMap<String, String>());
    setAttributesAsString(attributesStr);
  }

  public TagShortcut(String shortcut, String actual, Map<String, String> attributes) {
    _shortcut = shortcut;
    _actual = actual;
    _attributes = attributes;
  }

  public String getShortcut() {
    return _shortcut;
  }

  public void setShortcut(String shortcut) {
    _shortcut = shortcut;
  }

  public String getActual() {
    return _actual;
  }

  public void setActual(String actual) {
    _actual = actual;
  }

  public Map<String, String> getAttributes() {
    return _attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    _attributes = attributes;
  }

  public void setAttributesAsString(String attributesStr) {
    _attributes.clear();
    String[] attributesSplit = attributesStr.split(",");
    for (int attributeNum = 0; attributeNum < attributesSplit.length; attributeNum++) {
      String[] kvPair = attributesSplit[attributeNum].split("=");
      if (kvPair.length == 2) {
        _attributes.put(kvPair[0].trim(), kvPair[1].trim());
      }
    }
  }

  public String getAttributesAsString() {
    StringBuffer attributesBuffer = new StringBuffer();
    for (Map.Entry<String, String> attribute : _attributes.entrySet()) {
      attributesBuffer.append(attribute.getKey());
      attributesBuffer.append("=");
      attributesBuffer.append(attribute.getValue());
      attributesBuffer.append(",");
    }
    if (attributesBuffer.length() > 0) {
      attributesBuffer.setLength(attributesBuffer.length() - 1);
    }
    return attributesBuffer.toString();
  }

  @Override
  public TagShortcut clone() {
    return new TagShortcut(getShortcut(), getActual(), new HashMap<String, String>(_attributes));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TagShortcut) {
      TagShortcut shortcut = (TagShortcut) obj;
      return getShortcut().equals(shortcut.getShortcut()) && getActual().equals(shortcut.getActual()) && getAttributes().equals(shortcut.getAttributes());
    }
    return false;
  }

  public static boolean hasChange(List<TagShortcut> tags1, List<TagShortcut> tags2) {
    if (tags1.size() != tags2.size()) {
      return true;
    }
    for (int i = 0; i < tags1.size(); i++) {
      TagShortcut tag1 = tags1.get(i);
      TagShortcut tag2 = tags2.get(i);
      if (!tag1.equals(tag2)) {
        return true;
      }
    }
    return false;
  }

  public static List<TagShortcut> fromPreferenceString(String value) {
    List<TagShortcut> list = new ArrayList<TagShortcut>();
    if (value != null) {
      String[] values = value.split("\n");
      for (int i = 0; i < values.length; i++) {
        String[] split = values[i].split("\t");
        if (split.length >= 2) {
          String shortcut = split[0];
          String actual = split[1];
          HashMap<String, String> attributes = new HashMap<String, String>();
          for (int attributeNum = 2; attributeNum < split.length; attributeNum += 2) {
            attributes.put(split[attributeNum], split[attributeNum + 1]);
          }
          list.add(new TagShortcut(shortcut, actual, attributes));
        }
      }
    }
    return list;
  }

  public static String toPreferenceString(List<TagShortcut> list) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      TagShortcut tag = list.get(i);
      sb.append(tag.getShortcut());
      sb.append("\t");
      sb.append(tag.getActual());
      for (Map.Entry<String, String> entry : tag.getAttributes().entrySet()) {
        sb.append("\t");
        sb.append(entry.getKey());
        sb.append("\t");
        sb.append(entry.getValue());
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
