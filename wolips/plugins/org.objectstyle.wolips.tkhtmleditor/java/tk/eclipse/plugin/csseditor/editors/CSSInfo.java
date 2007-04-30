package tk.eclipse.plugin.csseditor.editors;

/**
 * @author Naoki Takezoe
 */
public class CSSInfo {
  private String replaceString;
  private String displayString;

  public CSSInfo(String replaceString) {
    this(replaceString, replaceString);
  }

  public CSSInfo(String replaceString, String displayString) {
    this.replaceString = replaceString;
    this.displayString = displayString;
  }

  public String getDisplayString() {
    return displayString;
  }

  public String getReplaceString() {
    return replaceString;
  }
}
