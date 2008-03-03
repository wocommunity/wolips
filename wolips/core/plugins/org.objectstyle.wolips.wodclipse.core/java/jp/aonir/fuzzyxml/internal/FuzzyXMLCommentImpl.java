package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLCommentImpl extends AbstractFuzzyXMLNode implements FuzzyXMLComment {

  private String _value;

  public FuzzyXMLCommentImpl(String value) {
    super();
    this._value = value;
  }

  public FuzzyXMLCommentImpl(FuzzyXMLNode parent, String value, int offset, int length) {
    super(parent, offset, length);
    this._value = value;
  }

  public String getValue() {
    return _value;
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    boolean isHTML = renderContext.isHtml();
    xmlBuffer.append("<!--");
    String commentString = FuzzyXMLUtil.escape(getValue(), isHTML);
    if (commentString != null) {
      if (!commentString.startsWith(" ")) {
        xmlBuffer.append(" ");
      }
      xmlBuffer.append(commentString);
      if (!commentString.endsWith(" ")) {
        xmlBuffer.append(" ");
      }
    }
    xmlBuffer.append("-->");
    if (renderContext.isShowNewlines()) {
      xmlBuffer.append("\n");
    }
  }

  @Override
  public String toString() {
    return "comment: " + getValue();
  }
}
