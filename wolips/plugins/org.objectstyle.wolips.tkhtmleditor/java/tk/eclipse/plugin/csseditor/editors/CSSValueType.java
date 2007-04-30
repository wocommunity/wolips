package tk.eclipse.plugin.csseditor.editors;

import java.util.Set;

public abstract class CSSValueType {
  public abstract void fillInProposals(String token, Set<String> proposals);
}
