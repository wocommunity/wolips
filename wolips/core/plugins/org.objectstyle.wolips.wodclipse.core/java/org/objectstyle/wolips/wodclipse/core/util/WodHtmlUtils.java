package org.objectstyle.wolips.wodclipse.core.util;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class WodHtmlUtils {
  public static Pattern WEBOBJECTS_PATTERN;

  static {
    StringBuffer patterns = new StringBuffer();
    patterns.append("<webobjects{0,1}\\s+name\\s*=\\s*\"{0,1}([^>\"/\\s]+)\"{0,1}\\s*/{0,1}>");
    patterns.append("|");
    patterns.append("<wo\\s+name\\s*=\\s*\"{0,1}([^>\"/\\s]+)\"{0,1}\\s*/{0,1}>");
    WodHtmlUtils.WEBOBJECTS_PATTERN = Pattern.compile(patterns.toString(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  }

  public static boolean isWOTag(String tagName) {
    boolean isWOTag = false;
    if (tagName != null) {
      String lowercaseTagName = tagName.toLowerCase();
      if (lowercaseTagName.startsWith("webobject") || lowercaseTagName.equals("wo") || lowercaseTagName.startsWith("wo:")) {
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

}
