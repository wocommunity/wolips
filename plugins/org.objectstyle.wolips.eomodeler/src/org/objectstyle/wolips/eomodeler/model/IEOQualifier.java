package org.objectstyle.wolips.eomodeler.model;

import java.util.List;

public interface IEOQualifier {
  public void loadFromMap(EOModelMap _map);

  public EOModelMap toMap();

  public void verify(List _failures);
}
