package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class TagNameDetector implements IWordDetector {
  public boolean isWordPart(char c) {
    return false;
  }

  public boolean isWordStart(char c) {
    return false;
  }

}
