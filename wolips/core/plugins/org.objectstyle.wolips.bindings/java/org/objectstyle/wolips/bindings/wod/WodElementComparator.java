package org.objectstyle.wolips.bindings.wod;

import java.util.Comparator;


public class WodElementComparator implements Comparator<IWodElement> {

  public int compare(IWodElement element1, IWodElement element2) {
    String name1 = element1.getElementName();
    String name2 = element2.getElementName();
    return name1.compareTo(name2);
  }

}
