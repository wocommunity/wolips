package tk.eclipse.plugin.csseditor.editors;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CSSEnumValueType extends CSSValueType {
  private List<CSSValueType> _valueTypes;

  public CSSEnumValueType() {
    _valueTypes = new LinkedList<CSSValueType>();
  }

  public CSSEnumValueType(String... valueTypeLiterals) {
    this();
    for (String valueTypeLiteral : valueTypeLiterals) {
      addValueType(valueTypeLiteral);
    }
  }
  
  public CSSEnumValueType(CSSValueType... valueTypes) {
    this();
    for (CSSValueType valueType : valueTypes) {
      addValueType(valueType);
    }
  }

  public CSSEnumValueType(CSSProperty... valueProperties) {
    this();
    for (CSSProperty valueProperty : valueProperties) {
      addValueType(new CSSPropertyValueType(valueProperty));
    }
  }

  public void addValueType(String literalValueType) {
    addValueType(new CSSLiteralValueType(literalValueType));
  }

  public void addValueType(CSSValueType valueType) {
    _valueTypes.add(valueType);
  }

  public List<CSSValueType> getValueTypes() {
    return _valueTypes;
  }

  @Override
  public void fillInProposals(String token, Set<String> proposals) {
    for (CSSValueType valueType : _valueTypes) {
      valueType.fillInProposals(token, proposals);
    }
  }
}
