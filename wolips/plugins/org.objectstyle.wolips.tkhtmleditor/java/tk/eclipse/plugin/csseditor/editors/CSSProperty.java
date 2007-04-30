package tk.eclipse.plugin.csseditor.editors;

import java.util.Set;

public class CSSProperty {
  private String _name;
  private CSSEnumValueType _valueType;

  public CSSProperty(String name) {
    _name = name;
    _valueType = new CSSEnumValueType();
  }

  public String getName() {
    return _name;
  }

  public void addValueType(String literalValueType) {
    _valueType.addValueType(literalValueType);
  }

  public void addValueType(CSSValueType valueType) {
    _valueType.addValueType(valueType);
  }

  public CSSEnumValueType getValueType() {
    return _valueType;
  }

  public void fillInProposals(String token, Set<String> proposals) {
    _valueType.fillInProposals(token, proposals);
  }
}
