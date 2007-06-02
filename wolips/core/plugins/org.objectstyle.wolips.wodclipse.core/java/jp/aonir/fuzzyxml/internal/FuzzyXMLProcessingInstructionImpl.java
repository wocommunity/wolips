package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLProcessingInstruction;

public class FuzzyXMLProcessingInstructionImpl extends AbstractFuzzyXMLNode implements FuzzyXMLProcessingInstruction {

  private String _name;
  private String _data;

  public FuzzyXMLProcessingInstructionImpl(String name, String data) {
    super();
    this._name = name;
    this._data = data;
  }

  public FuzzyXMLProcessingInstructionImpl(FuzzyXMLElement parent, String name, String data, int offset, int length) {
    super(parent, offset, length);
    this._name = name;
    this._data = data;
  }

  public String getData() {
    return _data;
  }

  public String getName() {
    return _name;
  }

  public void setData(String data) {
    int length = this._data.length();
    this._data = data;
    // 更新イベントを発火
    fireModifyEvent(toXMLString(new RenderContext(false)), getOffset(), getLength());
    // 位置情報を更新
    appendOffset((FuzzyXMLElement) getParentNode(), getOffset(), data.length() - length);
  }

  @Override
  public String toString() {
    return "PI: " + _name;
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    xmlBuffer.append("<?" + _name + " " + _data + "?>");
  }

}
