package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLStyleImpl extends FuzzyXMLElementImpl {

  public FuzzyXMLStyleImpl(FuzzyXMLNode parent, String name, int offset, int length, int nameOffset) {
    super(parent, name, offset, length, nameOffset);
  }
  
  @Override
  public String getValue(RenderContext renderContext, StringBuffer xmlBuffer) {
    RenderContext rc = renderContext.clone();
    rc.setTrim(false);
    rc.setDelegate(new AbstractRenderDelegate() {});
    String contents = FuzzyXMLUtil.decode(super.getValue(rc, xmlBuffer), rc.isHtml());
    if (renderContext.shouldFormat()) {
      //TODO: Replace blockIndent with a wotag aware CSS formatter
      contents = FuzzyXMLUtil.blockIndent(renderContext, contents);
    }
    return contents;
  }

  @Override
  public boolean isNonBreaking() {
    return false;
  }
}
