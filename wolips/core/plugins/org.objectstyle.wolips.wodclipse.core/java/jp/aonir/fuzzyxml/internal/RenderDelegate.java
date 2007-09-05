package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public interface RenderDelegate {
  public boolean renderNode(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer);
  
  public boolean beforeOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer);
  
  public void afterOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer);
  
  public void beforeCloseTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer);
  
  public void afterCloseTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer);
}
