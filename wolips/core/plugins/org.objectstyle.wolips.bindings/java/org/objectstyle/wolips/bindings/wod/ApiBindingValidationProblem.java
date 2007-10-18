package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.Binding;

public class ApiBindingValidationProblem extends WodBindingProblem {
  private Binding _binding;

  public ApiBindingValidationProblem(Binding binding, Position position, int lineNumber, boolean warning) {
    super(binding.getName(), "Binding '" + binding.getName() + "' is required for " + binding.getElement().getClassName() + ".", position, lineNumber, warning);
    _binding = binding;
  }

  public Binding getBinding() {
    return _binding;
  }
}
