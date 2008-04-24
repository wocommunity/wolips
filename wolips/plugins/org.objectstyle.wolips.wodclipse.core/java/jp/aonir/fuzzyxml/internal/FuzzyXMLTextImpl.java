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
    String value = _value;
    if (renderContext.isTrim()) {
      if (value.trim().length() == 0) {
        return;
      }
      value = value.replaceFirst("[\r\n\t ]+$", " ");
      String replace = "";
      if (xmlBuffer.length() >= 1 && !Character.isWhitespace(xmlBuffer.charAt(xmlBuffer.length()-1))) {
        replace = " ";
      }
      value = value.replaceFirst("^[\r\n\t ]+", replace);
    }
    if (_escape) {
      boolean isHTML = renderContext.isHtml();
      value = FuzzyXMLUtil.escape(value, isHTML);
    }
    xmlBuffer.append(value);
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

  @Override
  public boolean isNonBreaking() {
    if (isHidden())
      return false;

    boolean result;
    String value = getValue().trim();
    
    result = FuzzyXMLUtil.isAllWhitescape(value) || FuzzyXMLUtil.getSpaceIndex(value) == -1;
//    System.out.println(result?"Non breaking " +this:"    Breaking " +this);

    return result;
  }
  
  @Override
  public boolean isHidden() {
    boolean result = _value == null || (FuzzyXMLUtil.isAllWhitescape(_value));
    return result;
  }
  
  @Override
  public boolean hasLineBreaks() {
    return _value != null && _value.trim().contains("\n");
  }
}
