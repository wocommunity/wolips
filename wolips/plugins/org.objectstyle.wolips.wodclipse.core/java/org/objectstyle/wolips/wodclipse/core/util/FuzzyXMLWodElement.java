package org.objectstyle.wolips.wodclipse.core.util;

import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.wod.SimpleWodBinding;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.bindings.wod.TagShortcut;

public class FuzzyXMLWodElement extends SimpleWodElement {
  private FuzzyXMLElement _xmlElement;

  public FuzzyXMLWodElement(FuzzyXMLElement element, boolean wo54) {
    String elementName = element.getName();
    String namespaceElementName = elementName.substring("wo:".length()).trim();
    int elementTypePosition = element.getOffset() + element.getNameOffset() + "wo:".length() + 1;
    int elementTypeLength = namespaceElementName.length();

    TagShortcut matchingTagShortcut = null;
    for (TagShortcut tagShortcut : ApiCache.getTagShortcuts()) {
      if (namespaceElementName.equalsIgnoreCase(tagShortcut.getShortcut())) {
        matchingTagShortcut = tagShortcut;
      }
    }
    if (matchingTagShortcut != null) {
      namespaceElementName = matchingTagShortcut.getActual();
    }

    _setElementName("_temp");
    _setElementType(namespaceElementName);
    setElementTypePosition(new Position(elementTypePosition, elementTypeLength));
    setTemporary(true);

    if (matchingTagShortcut != null) {
      for (Map.Entry<String, String> shortcutAttribute : matchingTagShortcut.getAttributes().entrySet()) {
        String value = WodHtmlUtils.toBindingValue(shortcutAttribute.getValue(), wo54);
        SimpleWodBinding wodBinding = new SimpleWodBinding(shortcutAttribute.getKey(), value);
        addBinding(wodBinding);
      }
    }

    FuzzyXMLAttribute[] attributes = element.getAttributes();
    for (FuzzyXMLAttribute attribute : attributes) {
      String name = attribute.getName();
      String originalValue = attribute.getValue();
      String value = WodHtmlUtils.toBindingValue(originalValue, wo54);
      SimpleWodBinding wodBinding = new SimpleWodBinding(name, value, new Position(element.getOffset() + attribute.getNameOffset() + 1, attribute.getNameLength()), new Position(element.getOffset() + attribute.getValueDataOffset() + 1, attribute.getValueDataLength()), -1);
      addBinding(wodBinding);
    }
  }

  public FuzzyXMLElement getXmlElement() {
    return _xmlElement;
  }
}
