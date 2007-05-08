package org.objectstyle.wolips.wodclipse.core.model;

import java.util.Comparator;

public class WodBindingComparator implements Comparator<IWodBinding> {

  public int compare(IWodBinding binding1, IWodBinding binding2) {
    String name1 = binding1.getName();
    String name2 = binding2.getName();
    return name1.compareTo(name2);
  }

}
