package org.objectstyle.wolips.eomodeler.model;

public class EOQualifierFactory {
  public static IEOQualifier qualifierForMap(EOModelMap _qualifierMap) {
    String className = _qualifierMap.getString("class", true);
    IEOQualifier qualifier = qualifierForClass(className);
    qualifier.loadFromMap(_qualifierMap);
    return qualifier;
  }

  public static IEOQualifier qualifierForClass(String _className) {
    try {
      String qualifiedClassName = "org.objectstyle.wolips.eomodeler.model." + _className;
      Class clazz = Class.forName(qualifiedClassName);
      IEOQualifier qualifier = (IEOQualifier) clazz.newInstance();
      return qualifier;
    }
    catch (Throwable t) {
      throw new RuntimeException("Failed to create a qualifier for '" + _className + "'.", t);
    }
  }
}
