/*
 * Created on 28.07.2003
 *
 */
package org.objectstyle.wolips.core.util;

/**
 * @author Harald Niesche
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class ExcludeIncludeMatcher implements IStringMatcher {
  public ExcludeIncludeMatcher (String excludes[], String includes[]) {
    _excludes = new StringListMatcher (excludes);
    _includes = new StringListMatcher (includes);
  }

  public ExcludeIncludeMatcher (String excludes, String includes) {
    _excludes = new StringListMatcher (excludes);
    _includes = new StringListMatcher (includes);
  }

  public boolean match (String txt) {
    if (_excludes.match(txt)) {
      return false;
    }
    if (_includes.match(txt)) {
      return true;
    }
    return false;
  }
  
  StringListMatcher _excludes;
  StringListMatcher _includes;
}
