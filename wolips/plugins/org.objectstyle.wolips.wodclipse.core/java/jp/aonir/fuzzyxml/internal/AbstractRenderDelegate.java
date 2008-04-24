package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public abstract class AbstractRenderDelegate implements RenderDelegate {

  public void afterCloseTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
  }

  public void afterOpenTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
  }

  public void afterRender(RenderContext renderContext, StringBuffer xmlBuffer) {
  }

  public void beforeCloseTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
  }

  public boolean beforeOpenTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    return true;
  }

  public void beforeRender(RenderContext renderContext, StringBuffer xmlBuffer) {
  }

  public boolean renderNode(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    return true;
  }

}
