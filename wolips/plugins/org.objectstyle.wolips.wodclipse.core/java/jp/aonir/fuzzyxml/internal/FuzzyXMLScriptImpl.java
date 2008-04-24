package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

public class FuzzyXMLScriptImpl extends FuzzyXMLElementImpl {
  
  public FuzzyXMLScriptImpl(FuzzyXMLNode parent, String name, int offset,
      int length, int nameOffset) {
    super(parent, name, offset, length, nameOffset);
  }

  @Override
  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    
    FuzzyXMLNode[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i] instanceof FuzzyXMLText) {
        ((FuzzyXMLText)children[i]).setEscape(false);
      }
    }
    super.toXMLString(renderContext, xmlBuffer);
  }
  
  @Override
  public boolean isNonBreaking() {
    return false;
  }
}
