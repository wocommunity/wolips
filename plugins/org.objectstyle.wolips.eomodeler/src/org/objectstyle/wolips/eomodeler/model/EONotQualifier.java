package org.objectstyle.wolips.eomodeler.model;

import java.util.List;
import java.util.Map;

public class EONotQualifier implements IEOQualifier {
  private IEOQualifier myQualifier;

  public void setQualifier(IEOQualifier _qualifier) {
    myQualifier = _qualifier;
  }

  public IEOQualifier getQualifier() {
    return myQualifier;
  }

  public void loadFromMap(EOModelMap _map) {
    Map qualifierMap = _map.getMap("qualifier");
    if (qualifierMap != null) {
      myQualifier = EOQualifierFactory.qualifierForMap(new EOModelMap(qualifierMap));
    }
  }

  public EOModelMap toMap() {
    EOModelMap qualifierMap = new EOModelMap();
    qualifierMap.setString("class", "EONotQualifier", true);
    if (myQualifier == null) {
      qualifierMap.setMap("qualifier", null);
    }
    else {
      EOModelMap notMap = myQualifier.toMap();
      qualifierMap.setMap("qualifier", notMap);
    }
    return qualifierMap;
  }

  public void verify(List _failures) {
    if (myQualifier != null) {
      myQualifier.verify(_failures);
    }
  }
}
