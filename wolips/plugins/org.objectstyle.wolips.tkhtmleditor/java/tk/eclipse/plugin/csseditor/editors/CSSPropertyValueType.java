package tk.eclipse.plugin.csseditor.editors;

import java.util.Set;

public class CSSPropertyValueType extends CSSValueType {
  private CSSProperty _property;

  public CSSPropertyValueType(CSSProperty property) {
    _property = property;
  }

  public CSSProperty getProperty() {
    return _property;
  }

  @Override
  public void fillInProposals(String token, Set<String> proposals) {
    _property.fillInProposals(token, proposals);
  }
}
