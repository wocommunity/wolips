package org.objectstyle.wolips.core.resources.types.api;

import java.util.Map;

public interface IValidation {
  public IValidation[] getValidationChildren(boolean _includeUnevaluatableChildren);
  public boolean isAffectedByBindingNamed(String bindingName);
  public boolean evaluate(Map _bindings);
}
