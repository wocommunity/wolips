package tk.eclipse.plugin.csseditor.editors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CSSCompositeValueType extends CSSValueType {
  private List<CSSValueType> _valueTypes;

  public CSSCompositeValueType(CSSValueType... valueTypes) {
    _valueTypes = new LinkedList<CSSValueType>();
    for (CSSValueType valueType : valueTypes) {
      _valueTypes.add(valueType);
    }
  }

  public List<CSSValueType> getValueTypes() {
    return _valueTypes;
  }

  @Override
  public void fillInProposals(String token, Set<String> proposals) {
    int lastSpaceIndex = token.lastIndexOf(' ');
    String start;
    String lastToken;
    if (lastSpaceIndex == -1) {
      start = "";
      lastToken = token;
    }
    else {
      start = token.substring(0, lastSpaceIndex + 1);
      lastToken = token.substring(lastSpaceIndex + 1);
    }
    String[] tokens = token.split("\\s+");
    int tokenCount = tokens.length;
    if (tokenCount < _valueTypes.size()) {
      Set<String> partialProposals = new HashSet<String>();
      _valueTypes.get(tokenCount).fillInProposals(lastToken, partialProposals);
      for (String partialProposal : partialProposals) {
        proposals.add(start + partialProposal);
      }
    }
  }
}
