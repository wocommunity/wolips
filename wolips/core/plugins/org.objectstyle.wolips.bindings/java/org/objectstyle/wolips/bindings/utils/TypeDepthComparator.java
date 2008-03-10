package org.objectstyle.wolips.bindings.utils;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.bindings.wod.TypeCache;

public class TypeDepthComparator implements Comparator<IType> {
  private TypeCache _cache;

  public TypeDepthComparator(TypeCache cache) {
    _cache = cache;
  }

  public int compare(IType o1, IType o2) {
    int comparison = 0;
    try {
      if (o1 == null) {
        if (o2 == null) {
          comparison = 0;
        }
        else {
          comparison = -1;
        }
      }
      else if (o2 == null) {
        comparison = 1;
      }
      else {
        List<IType> o1Types = _cache.getSupertypesOf(o1);
        List<IType> o2Types = _cache.getSupertypesOf(o2);
        if (o1Types.size() == o2Types.size()) {
          comparison = 0;
        }
        else if (o1Types.size() < o2Types.size()) {
          comparison = 1;
        }
        else {
          comparison = -1;
        }
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    return comparison;
  }
}
