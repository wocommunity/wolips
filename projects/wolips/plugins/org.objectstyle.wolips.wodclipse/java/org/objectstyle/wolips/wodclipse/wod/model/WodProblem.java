package org.objectstyle.wolips.wodclipse.wod.model;

import org.eclipse.jface.text.Position;

public class WodProblem {
  private IWodModel myModel;
  private String myMessage;
  private Position myPosition;
  private boolean myWarning;

  public WodProblem(IWodModel _model, String _message, Position _position, boolean _warning) {
    myModel = _model;
    myMessage = _message;
    myPosition = _position;
    myWarning = _warning;
  }

  public String getMessage() {
    return myMessage;
  }

  public IWodModel getModel() {
    return myModel;
  }

  public Position getPosition() {
    return myPosition;
  }
  
  public boolean isWarning() {
    return myWarning;
  }
}
