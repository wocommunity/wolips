package org.objectstyle.wolips.eomodeler.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class EOAggregateQualifier implements IEOQualifier {
  private String myClassName;
  private List myQualifiers;

  public EOAggregateQualifier(String _className) {
    myClassName = _className;
    myQualifiers = new LinkedList();
  }

  public String getClassName() {
    return myClassName;
  }

  public void addQualifier(IEOQualifier _qualifier) {
    myQualifiers.add(_qualifier);
  }

  public void removeQualifier(IEOQualifier _qualifier) {
    myQualifiers.remove(_qualifier);
  }

  public void clearQualifiers() {
    myQualifiers.clear();
  }

  public List getQualifiers() {
    return myQualifiers;
  }

  public void loadFromMap(EOModelMap _map) {
    List qualifiers = _map.getList("qualifiers");
    if (qualifiers != null) {
      Iterator qualifiersIter = qualifiers.iterator();
      while (qualifiersIter.hasNext()) {
        Map qualifierMap = (Map) qualifiersIter.next();
        IEOQualifier qualifier = EOQualifierFactory.qualifierForMap(new EOModelMap(qualifierMap));
        addQualifier(qualifier);
      }
    }
  }

  public EOModelMap toMap() {
    EOModelMap qualifierMap = new EOModelMap();
    qualifierMap.setString("class", myClassName, true);
    List qualifiers = new LinkedList();
    Iterator qualifiersIter = myQualifiers.iterator();
    while (qualifiersIter.hasNext()) {
      IEOQualifier qualifier = (IEOQualifier) qualifiersIter.next();
      EOModelMap qualfierMap = qualifier.toMap();
      qualifiers.add(qualifierMap);
    }
    qualifierMap.setList("qualifiers", qualifiers);
    return qualifierMap;
  }

  public void verify(List _failures) {
    Iterator qualifiersIter = myQualifiers.iterator();
    while (qualifiersIter.hasNext()) {
      IEOQualifier qualifier = (IEOQualifier) qualifiersIter.next();
      qualifier.verify(_failures);
    }    
  }
}
