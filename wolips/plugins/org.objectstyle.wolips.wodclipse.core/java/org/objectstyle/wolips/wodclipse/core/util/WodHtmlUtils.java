package org.objectstyle.wolips.wodclipse.core.util;

import java.util.Map;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.SimpleWodBinding;
import org.objectstyle.wolips.wodclipse.core.model.SimpleWodElement;
import org.objectstyle.wolips.wodclipse.core.preferences.TagShortcut;

public class WodHtmlUtils {
  public static Pattern WEBOBJECTS_PATTERN;

  static {
    StringBuffer patterns = new StringBuffer();
    patterns.append("<webobjects{0,1}\\s+name\\s*=\\s*\"{0,1}([^>\"/\\s]+)\"{0,1}\\s*/{0,1}>");
    patterns.append("|");
    patterns.append("<wo\\s+name\\s*=\\s*\"{0,1}([^>\"/\\s]+)\"{0,1}\\s*/{0,1}>");
    WodHtmlUtils.WEBOBJECTS_PATTERN = Pattern.compile(patterns.toString(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  }

  public static boolean isInline(String tagName) {
    boolean isWOTag = false;
    if (tagName != null) {
      String lowercaseTagName = tagName.toLowerCase();
      if (lowercaseTagName.startsWith("wo:")) {
        isWOTag = true;
      }
    }
    return isWOTag;
  }

  public static boolean isWOTag(String tagName) {
    boolean isWOTag = false;
    if (tagName != null) {
      String lowercaseTagName = tagName.trim().toLowerCase();
      if (lowercaseTagName.startsWith("webobject") || lowercaseTagName.equals("wo") || lowercaseTagName.startsWith("wo ") || lowercaseTagName.startsWith("wo:")) {
        isWOTag = true;
      }
    }
    return isWOTag;
  }

  public static IFile getHtmlFileForWodFilePath(IPath wodFilePath) {
    IPath templatePath = wodFilePath.removeFileExtension().addFileExtension("html");
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(templatePath);
    return file;
  }

  /**
   * Returns the line number from the offset.
   * 
   * @param offset the offset
   * @return the line number.
   * 
   * Needs to be offset+1 in the substring to make sure 
   * that the text is included in the substring.  Otherwise 
   * an offset at the start of the line is not included.
   */
  public static int getLineAtOffset(String contents, int offset) {
    int lineCount = 1;
    for (int i = 0; i < offset + 1; i++) {
      char ch = contents.charAt(i);
      if (ch == '\n') {
        lineCount++;
      }
    }
    return lineCount;
  }

  public static String toBindingValue(String value, boolean wo54) {
    String bindingValue = value;
    if (bindingValue.startsWith("$")) {
      bindingValue = bindingValue.substring(1);
    }
    else if (wo54 && bindingValue.startsWith("[") && bindingValue.endsWith("]")) {
      bindingValue = bindingValue.substring(1, bindingValue.length() - 1);
    }
    else {
      bindingValue = "\"" + bindingValue + "\"";
    }
    return bindingValue;
  }
  
  public static SimpleWodElement toWodElement(FuzzyXMLElement element, boolean wo54, WodParserCache cache) {
    String elementName = element.getName();
    String namespaceElementName = elementName.substring("wo:".length()).trim();
    int elementTypePosition = element.getOffset() + element.getNameOffset() + "wo:".length() + 1;
    int elementTypeLength = namespaceElementName.length();

    TagShortcut matchingTagShortcut = null;
    for (TagShortcut tagShortcut : cache.getTagShortcuts()) {
      if (namespaceElementName.equalsIgnoreCase(tagShortcut.getShortcut())) {
        matchingTagShortcut = tagShortcut;
      }
    }
    if (matchingTagShortcut != null) {
      namespaceElementName = matchingTagShortcut.getActual();
    }

    SimpleWodElement wodElement = new SimpleWodElement("_temp", namespaceElementName);
    wodElement.setElementTypePosition(new Position(elementTypePosition, elementTypeLength));
    wodElement.setTemporary(true);

    if (matchingTagShortcut != null) {
      for (Map.Entry<String, String> shortcutAttribute : matchingTagShortcut.getAttributes().entrySet()) {
        String value = WodHtmlUtils.toBindingValue(shortcutAttribute.getValue(), wo54);
        SimpleWodBinding wodBinding = new SimpleWodBinding(shortcutAttribute.getKey(), value);
        wodElement.addBinding(wodBinding);
      }
    }

    FuzzyXMLAttribute[] attributes = element.getAttributes();
    for (FuzzyXMLAttribute attribute : attributes) {
      String name = attribute.getName();
      String originalValue = attribute.getValue();
      String value = WodHtmlUtils.toBindingValue(originalValue, wo54);
      SimpleWodBinding wodBinding = new SimpleWodBinding(name, value);
      wodElement.addBinding(wodBinding);
    }
    return wodElement;
  }

}
