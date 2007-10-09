package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class WodBindingProblem extends WodProblem {
  private String _bindingName;

  public WodBindingProblem(String bindingName, String message, Position position, int lineNumber, boolean warning, String relatedToFileNames) {
    super(message, position, lineNumber, warning, relatedToFileNames);
    _bindingName = bindingName;
  }

  public WodBindingProblem(String bindingName, String message, Position position, int lineNumber, boolean warning, String[] relatedToFileNames) {
    super(message, position, lineNumber, warning, relatedToFileNames);
    _bindingName = bindingName;
  }

  public String getBindingName() {
    return _bindingName;
  }
}
