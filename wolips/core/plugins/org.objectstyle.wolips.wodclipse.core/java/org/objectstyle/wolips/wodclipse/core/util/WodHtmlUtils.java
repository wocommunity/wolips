package org.objectstyle.wolips.wodclipse.core.util;

import java.io.IOException;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.objectstyle.wolips.bindings.wod.IWodElement;
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

  /**
   * If the element is inline bindings, create a SimpleWodElement.  If the element is not inline, then
   * return the corresponding WOD element entry.
   * 
   * @param element the XML element to process
   * @param wo54 whether or not the node should be processed in WO 5.4 mode
   * @param cache the WodParserCache
   * @return an IWodElement corresponding to the node
   * @throws CoreException if the wod element cannot be processed 
   * @throws IOException if the wod element cannot be processed
   */
  public static IWodElement getWodElement(FuzzyXMLElement element, boolean wo54, WodParserCache cache) throws CoreException, IOException {
    IWodElement wodElement;
    if (WodHtmlUtils.isWOTag(element.getName())) {
      if (WodHtmlUtils.isInline(element.getName())) {
        wodElement = new FuzzyXMLWodElement(element, wo54);
      }
      else {
        String elementName = element.getAttributeValue("name");
        wodElement = cache.getWodModel().getElementNamed(elementName);
      }
    }
    else {
      wodElement = null;
    }
    return wodElement;
  }
}
