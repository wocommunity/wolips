package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLPreImpl extends FuzzyXMLElementImpl {

  private String _value;
  
  public FuzzyXMLPreImpl(String value) {
    this(null, value, -1, -1);
  }

  public FuzzyXMLPreImpl(FuzzyXMLNode parent, String value, int offset, int length) {
    super(parent, "pre", offset, length, -1);
    _value = value;
  }

  @Override
  public String getValue() {
    return _value;
  }
  
  @Override
  public String getValue(RenderContext renderContext, StringBuffer xmlBuffer) {
    return getValue();
  }

  @Override
  public String toString() {
    return "pre: " + getValue();
  }
  
}
