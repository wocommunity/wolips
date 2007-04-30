package tk.eclipse.plugin.csseditor.editors;

import java.util.Set;

public class CSSSizeValueType extends CSSValueType {
  @Override
  public void fillInProposals(String token, Set<String> proposals) {
    StringBuffer numberBuffer = new StringBuffer();
    StringBuffer typeBuffer = new StringBuffer();
    int state = -1;
    for (int i = 0; i < token.length(); i++) {
      char ch = token.charAt(i);
      if (Character.isWhitespace(ch)) {
        state = -1;
        break;
      }
      if (state == -1) {
        if (Character.isDigit(ch)) {
          state = 0;
        }
      }
      if (state == 0) {
        if (Character.isDigit(ch)) {
          numberBuffer.append(ch);
        }
        else {
          state = 1;
        }
      }
      if (state == 1) {
        typeBuffer.append(ch);
      }
    }
    if (state == -1 && token.length() == 0) {
      proposals.add("##em");
      proposals.add("##px");
      proposals.add("##pt");
      proposals.add("##%");
    }
    else if (state == 0) {
      proposals.add(numberBuffer + "em");
      proposals.add(numberBuffer + "px");
      proposals.add(numberBuffer + "pt");
      proposals.add(numberBuffer + "%");
    }
    else if (state == 1) {
      String type = typeBuffer.toString();
      if (type.equalsIgnoreCase("e")) {
        proposals.add(numberBuffer + "em");
      }
      else if (type.equalsIgnoreCase("p")) {
        proposals.add(numberBuffer + "px");
        proposals.add(numberBuffer + "pt");
      }
    }
  }
}
