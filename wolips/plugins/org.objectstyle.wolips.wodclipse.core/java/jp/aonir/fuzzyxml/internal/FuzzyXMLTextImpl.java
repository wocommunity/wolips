package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

public class FuzzyXMLTextImpl extends AbstractFuzzyXMLNode implements FuzzyXMLText {

  private String _value;
  private boolean _escape = true;

  public FuzzyXMLTextImpl(String value) {
    this(null, value, -1, -1);
  }

  public FuzzyXMLTextImpl(FuzzyXMLNode parent, String value, int offset, int length) {
    super(parent, offset, length);
    this._value = value;
  }

  public String getValue() {
    if (getDocument() == null) {
      return FuzzyXMLUtil.decode(_value, false);
    }
    return FuzzyXMLUtil.decode(_value, getDocument().isHTML());
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    String value = getValue();
    boolean showText = true;
    if (renderContext.isTrim()) {
      value = value.trim();
      if (value.length() == 0) {
        showText = false;
      }
    }
    if (showText) {
      if (_escape) {
        boolean isHTML = renderContext.isHtml();
        xmlBuffer.append(FuzzyXMLUtil.escape(value, isHTML));
      }
      else {
        xmlBuffer.append(value);
      }
    }
  }

  @Override
  public String toString() {
    return "text: " + getValue();
  }

  public void setEscape(boolean escape) {
    this._escape = escape;
  }

  public boolean isEscape() {
    return this._escape;
  }

}
