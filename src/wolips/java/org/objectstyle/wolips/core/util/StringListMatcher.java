/*
 * Created on 27.07.2003
 *
 */
package org.objectstyle.wolips.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Harald Niesche
 *
 */
public class StringListMatcher implements IStringMatcher {

  public StringListMatcher (String patterns[]) {
    set(patterns);
  }

  public StringListMatcher (String patterns) {
    set(StringUtilities.smartSplit(patterns,','));
  }

  private StringMatcher _getMatcher (String pattern) {
    // we might wanna have a way of creating case-sensitive matchers
    return new StringMatcher(pattern, true, false);
  }

  public void set(String patterns[]) {
    clear();
    for (int i = 0; i < patterns.length; ++i) {
      _matchers.add(_getMatcher(patterns[i]));
    }
  }

  private void clear() {
    _matchers.clear();
  }

  public boolean isEmpty () {
    return _matchers.isEmpty();
  }

  public boolean match (String txt) {
    Iterator iter = _matchers.iterator();
    while (iter.hasNext()) {
      StringMatcher sm = (StringMatcher)iter.next();
      if (sm.match(txt)) {
        return true;
      }
    }
    return false;
  }

  List _matchers = new ArrayList();
}
