package org.objectstyle.wolips.wodclipse.wod.model;

import org.objectstyle.wolips.wodclipse.wod.parser.RulePosition;

public class WodProblem {
  private IWodModel myModel;
  private String myMessage;
  private RulePosition myCurrentRulePosition;
  private RulePosition myPreviousRulePosition;

  public WodProblem(IWodModel _model, String _message, RulePosition _currentRulePosition, RulePosition _previousRulePosition) {
    myModel = _model;
    myMessage = _message;
    myCurrentRulePosition = _currentRulePosition;
    myPreviousRulePosition = _previousRulePosition;
  }

  public String getMessage() {
    return myMessage;
  }

  public IWodModel getModel() {
    return myModel;
  }

  public RulePosition getCurrentRulePosition() {
    return myCurrentRulePosition;
  }

  public RulePosition getPreviousRulePosition() {
    return myPreviousRulePosition;
  }
}
