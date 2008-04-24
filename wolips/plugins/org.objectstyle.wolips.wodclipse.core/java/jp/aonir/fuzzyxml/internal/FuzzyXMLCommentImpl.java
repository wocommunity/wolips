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
    boolean renderSurroundingTags = true;
    
    RenderDelegate delegate = renderContext.getDelegate();
    if (delegate != null) {
      renderSurroundingTags = delegate.beforeOpenTag(this, renderContext, xmlBuffer);
    }
    
    if (renderSurroundingTags) {
      xmlBuffer.append("<!--");
      
      String commentString = getValue();//FuzzyXMLUtil.escape(getValue(), isHTML);
      if (commentString != null) {
        if (!commentString.startsWith(" ")) {
          xmlBuffer.append(" ");
        }
        if (renderContext.shouldFormat()) {
          StringBuffer indent = new StringBuffer();
          renderContext.appendIndent(indent);
          if (commentString.contains("\n")) {
            commentString = commentString.replaceAll("\n\\s*", "\n"+indent.toString());
          }
        }
        xmlBuffer.append(commentString);
        if (!commentString.endsWith(" ")) {
          xmlBuffer.append(" ");
        }
      }
      xmlBuffer.append("-->");
      if (delegate != null) {
        delegate.afterCloseTag(this, renderContext, xmlBuffer);
      }
    }
  }

  @Override
  public String toString() {
    return "comment: " + getValue();
  }
}
