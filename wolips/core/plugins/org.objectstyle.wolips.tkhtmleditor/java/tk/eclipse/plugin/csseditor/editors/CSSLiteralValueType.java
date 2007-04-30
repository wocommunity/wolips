package tk.eclipse.plugin.csseditor.editors;

import java.util.Set;

public class CSSLiteralValueType extends CSSValueType {
  public String _value;

  public CSSLiteralValueType(String value) {
    _value = value;
  }

  public String getValue() {
    return _value;
  }

  @Override
  public void fillInProposals(String token, Set<String> proposals) {
    if (_value.startsWith(token.toLowerCase())) {
      proposals.add(_value);
    }
  }
}
