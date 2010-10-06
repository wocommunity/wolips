package org.objectstyle.wolips.wodclipse.core.util;

import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WodHtmlUtils {
  public static Pattern WEBOBJECTS_PATTERN;

  static {
    StringBuffer patterns = new StringBuffer();
    patterns.append("<webobjects{0,1}\\s+name\\s*=\\s*\"{0,1}([^>\"/\\s]+)\"{0,1}\\s*/{0,1}>");
    patterns.append("|");
    patterns.append("<wo\\s+name\\s*=\\s*\"{0,1}([^>\"/\\s]+)\"{0,1}\\s*/{0,1}>");
    WodHtmlUtils.WEBOBJECTS_PATTERN = Pattern.compile(patterns.toString(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  }

  public static boolean isInline(FuzzyXMLElement element) {
    return element != null && WodHtmlUtils.isInline(element.getName());
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

  public static boolean isWOTag(FuzzyXMLElement element) {
    return element != null && WodHtmlUtils.isWOTag(element.getName());
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

  public static class BindingValue {
    private String _valueNamespace;
    private String _value;
    private boolean _literal;

    public BindingValue(String valueNamespace, String value, boolean literal) {
      _valueNamespace = valueNamespace;
      _value = value;
      _literal = literal;
    }

    public String getValue() {
      return _value;
    }

    public String getValueNamespace() {
      return _valueNamespace;
    }

    public boolean isLiteral() {
      return _literal;
    }
  }

  public static BindingValue toBindingValue(String rawValue, String inlineBindingPrefix, String inlineBindingSuffix) {
    String valueNamespace = null;
    String value = rawValue;
    boolean literal;
    if (value.startsWith(inlineBindingPrefix) && (inlineBindingSuffix.length() == 0 || value.endsWith(inlineBindingSuffix))) {
      value = value.substring(inlineBindingPrefix.length(), value.length() - inlineBindingSuffix.length());
      int colonIndex = value.indexOf(':');
      if (colonIndex != -1) {
        valueNamespace = value.substring(0, colonIndex).trim();
        value = value.substring(colonIndex + 1).trim();
      }
      literal = false;
    }
    else {
      value = "\"" + value + "\"";
      literal = true;
    }
    return new BindingValue(valueNamespace, value, literal);
  }

  /**
   * If the element is inline bindings, create a SimpleWodElement.  If the element is not inline, then
   * return the corresponding WOD element entry.
   * 
   * @param element the XML element to process
   * @param buildProperties the build properties for this project
   * @param resolveWodElement if true, webobject tags will resolve to their DocumentWodElement
   * @param cache the WodParserCache
   * @return an IWodElement corresponding to the node
   * @throws Exception 
   */
  public static IWodElement getWodElement(FuzzyXMLElement element, BuildProperties buildProperties, boolean resolveWodElement, WodParserCache cache) throws Exception {
    IWodElement wodElement;
    if (WodHtmlUtils.isWOTag(element)) {
      if (WodHtmlUtils.isInline(element.getName()) || !resolveWodElement) {
        wodElement = new FuzzyXMLWodElement(element, buildProperties);
      }
      else {
        String elementName = element.getAttributeValue("name");
        if (cache != null && cache.getWodEntry() != null && cache.getWodEntry().getModel() != null) {
          wodElement = cache.getWodEntry().getModel().getElementNamed(elementName);
        }
        else {
          wodElement = null;
        }
      }
    }
    else {
      wodElement = null;
    }
    return wodElement;
  }
}
