package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.api.Validation;

public class ApiElementValidationProblem extends WodElementProblem {
  private Validation _validation;
  
  public ApiElementValidationProblem(Validation validation, Position position, int lineNumber, boolean warning, String relatedToFileNames) {
    super(validation.getMessage(), position, lineNumber, warning, relatedToFileNames);
    _validation = validation;
  }

  public ApiElementValidationProblem(Validation validation, Position position, int lineNumber, boolean warning, String[] relatedToFileNames) {
    super(validation.getMessage(), position, lineNumber, warning, relatedToFileNames);
    _validation = validation;
  }

  public Validation getValidation() {
    return _validation;
  }
}
