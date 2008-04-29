package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLPreImpl extends FuzzyXMLElementImpl {

  private String _value;
  
  public FuzzyXMLPreImpl(String value) {
    this(null, value, -1, -1);
  }

  public FuzzyXMLPreImpl(FuzzyXMLNode parent, String value, int offset, int length) {
    super(parent, "pre", offset, length, -1);
    this._value = value;
  }

  @Override
  public String getValue() {
    return this._value;
  }
  
  @Override
  public String getValue(RenderContext renderContext, StringBuffer xmlBuffer) {
    return getValue();
  }
  
  @Override
  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    boolean renderSurroundingTags = true;
    
    RenderDelegate delegate = renderContext.getDelegate();
    if (delegate != null) {
      renderSurroundingTags = delegate.beforeOpenTag(this, renderContext, xmlBuffer);
    }
    String tagName = getName();
    if (renderContext.isLowercaseTags() && FuzzyXMLUtil.isAllUppercase(tagName)) {
      tagName = tagName.toLowerCase();
    }
    if (renderSurroundingTags) {
      xmlBuffer.append("<" + tagName + ">");
      if (delegate != null)
        delegate.afterOpenTag(this, renderContext, xmlBuffer);
    }
    
    xmlBuffer.append(getValue());
    
    if (renderSurroundingTags) {
      if (delegate != null)
        delegate.beforeCloseTag(this, renderContext, xmlBuffer);      
      xmlBuffer.append("</" + tagName + ">");
      if (delegate != null)
        delegate.afterCloseTag(this, renderContext, xmlBuffer);
    }
  }

  @Override
  public String toString() {
    return "PRE: " + getValue();
  }
  
}
