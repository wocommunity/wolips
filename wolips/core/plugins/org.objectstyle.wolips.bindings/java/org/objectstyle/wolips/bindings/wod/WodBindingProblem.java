package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.IApiBinding;

public abstract class WodBindingProblem extends WodProblem implements IWodElementProblem {
  private IWodElement _element;
  private IApiBinding _binding;
  private String _bindingName;

  public WodBindingProblem(IWodElement element, IApiBinding binding, String bindingName, String message, Position position, int lineNumber, boolean warning) {
    super(message, position, lineNumber, warning);
    _element = element;
    _binding = binding;
    _bindingName = bindingName;
  }
  
  public IWodElement getElement() {
    return _element;
  }

  public IApiBinding getBinding() {
    return _binding;
  }

  public String getBindingName() {
    return _bindingName;
  }
}
