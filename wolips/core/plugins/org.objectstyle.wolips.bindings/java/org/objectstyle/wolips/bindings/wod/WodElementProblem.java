package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class WodElementProblem extends WodProblem implements IWodElementProblem {
  private IWodElement _element;

  public WodElementProblem(IWodElement element, String message, Position position, int lineNumber, boolean warning) {
    super(message, position, lineNumber, warning);
    _element = element;
  }

  public IWodElement getElement() {
    return _element;
  }
}
