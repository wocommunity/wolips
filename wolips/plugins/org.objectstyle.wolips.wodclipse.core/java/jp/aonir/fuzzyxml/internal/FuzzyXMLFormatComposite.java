package jp.aonir.fuzzyxml.internal;

import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLFormat;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

public class FuzzyXMLFormatComposite implements FuzzyXMLNode, FuzzyXMLFormat {
  private final FuzzyXMLNode delegate; 

  public FuzzyXMLFormatComposite(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      delegate = ((FuzzyXMLFormatComposite)node).getDelegate();
    else
      delegate = node;
  }
  
  public boolean isNonBreaking() {
    return isNonBreaking(delegate);
  }
  
  public boolean isBreaking() {
    return isBreaking(delegate);
  }
  
  public boolean isHidden() {
    return isHidden(delegate);
  }
  
  public boolean hasCloseTag() {
    if (delegate != null)
      return hasCloseTag(delegate);
    return false;
  }
  
  public FuzzyXMLNode getDelegate() {
    return delegate;
  }
  
  public int getLength() {
    if (delegate != null)
      return getLength();
    return 0;
  }

  public int getOffset() {
    if (delegate != null)
      return delegate.getOffset();
    return 0;
  }

  public FuzzyXMLFormatComposite getParentNode() {
    if (delegate != null)
      return new FuzzyXMLFormatComposite(delegate.getParentNode());
    return null;
  }

  public FuzzyXMLFormatComposite parentNode() {
    return getParentNode();
  }
  
  public String toXMLString(RenderContext renderContext) {
    if (delegate != null)
      return delegate.toXMLString(renderContext);
    return "";
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    if (delegate != null)
      delegate.toXMLString(renderContext, xmlBuffer);
  }
  
  public boolean isText() {
    return isText(delegate);
  }
  
  public boolean isElement() {
    return isElement(delegate);
  }
  
  public boolean hasNonBreakingStart() {
    return hasNonBreakingStart(delegate);
  }
  
  public boolean hasBreakingStart() {
    return hasBreakingStart(delegate);
  }
  
  public boolean hasNonBreakingEnd() {
    return hasNonBreakingEnd(delegate);
  }
  
  public boolean hasBreakingEnd() {
    return hasBreakingEnd(delegate);
  }
  
  public boolean isMultiLine() {
    return isMultiLine(delegate);
  }
  
  public boolean hasLineBreaks() {
    return hasLineBreaks(delegate);
  }
  
  public boolean isCode() {
    return isCode(delegate);
  }
  
  public boolean isComment() {
    return isComment(delegate);
  }
  
  public String getValue() {
    return getValue(delegate);
  }
  
  public String getName() {
    return getName(delegate);
  }
  
  public String toString() {
    if (delegate != null)
      return delegate.toString();
    return "";
  }
  
  public boolean isWOTag() {
    return isWOTag(delegate);
  }
  
  public boolean isDocumentRoot() {
    return isDocumentRoot(delegate);
  }
  
  public boolean hasChildren() {
    return hasChildren(delegate);
  }
  
  /** Utility methods **/
  
  public static boolean isNonBreaking(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isNonBreaking(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLFormat)
      return ((FuzzyXMLFormat)node).isNonBreaking();
    return false;
  }
  
  public static boolean isBreaking(FuzzyXMLNode node) {
    return !isNonBreaking(node);
  }
    
  public static boolean isHidden(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isHidden(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLFormat && ((FuzzyXMLFormat)node).isHidden()) {
      return true;
    }
    return false;
  }
  
  public static boolean hasNonBreakingStart(String value) {
    char ch = value.charAt(0);
    return !(Character.isWhitespace(ch));
  }

  public static boolean hasBreakingStart(String value) {
    return !hasNonBreakingStart(value);
  }

  public static boolean hasNonBreakingEnd(String value) {
    if (value == null || value.equals(""))
      return true;
    char ch = value.charAt(value.length()-1);
    return !(Character.isWhitespace(ch));
  }

  public static boolean hasBreakingEnd(String value) {
    return !hasNonBreakingEnd(value);
  }

  public static boolean hasNonBreakingEnd(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return hasNonBreakingEnd(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLText)
      return hasNonBreakingEnd(((FuzzyXMLText)node).getValue());
    return true;
  }
  
  public static boolean hasBreakingEnd(FuzzyXMLNode node) {
    return !hasNonBreakingEnd(node);
  }
  
  public static boolean hasNonBreakingStart(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return hasNonBreakingStart(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLText)
      return hasNonBreakingStart(((FuzzyXMLText)node).getValue());
    return true;
  }

  public static boolean hasBreakingStart(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return hasBreakingStart(((FuzzyXMLFormatComposite)node).getDelegate());
    return !hasNonBreakingStart(node);
  }
  
  public static boolean isElement(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isElement(((FuzzyXMLFormatComposite)node).getDelegate());
    return node instanceof FuzzyXMLElement;
  }
  
  public static boolean isText(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isText(((FuzzyXMLFormatComposite)node).getDelegate());
    return node instanceof FuzzyXMLText;
  }
  
  public static boolean hasCloseTag(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return hasCloseTag(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLElement)
      return ((FuzzyXMLElement)node).hasCloseTag();
    return false;
  }
  
  public static boolean isMultiLine(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isMultiLine(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLText) {
      String value = ((FuzzyXMLText)node).getValue();
      return value.contains("\n");
    }
    return false;
  }
  
  public static boolean hasLineBreaks(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return hasLineBreaks(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLText) {
      return ((FuzzyXMLText)node).hasLineBreaks();
    }
    return false;
  }

  public static boolean isCode(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isCode(((FuzzyXMLFormatComposite)node).getDelegate());
    FuzzyXMLNode parent = node.getParentNode();
    if (parent != null && parent instanceof FuzzyXMLElement) {
      String type = ((FuzzyXMLElement)parent).getName().toLowerCase();

      if (type.equals("style") || type.equals("script"))
        return true;
    }
    return false;
  }
  
  public static boolean isComment(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isComment(((FuzzyXMLFormatComposite)node).getDelegate());
    return node instanceof FuzzyXMLComment;
  }
  
  public static String getValue(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return getValue(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLText)
      return ((FuzzyXMLText)node).getValue();
    if (node instanceof FuzzyXMLComment)
      return ((FuzzyXMLComment)node).getValue();
    return null;
  }
  
  public static String getName(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return getName(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLElement)
      return ((FuzzyXMLElement)node).getName();
    return "";
  }
  
  public static boolean isWOTag(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isWOTag(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLElement)
      return WodHtmlUtils.isWOTag(getName(node).toLowerCase());
    return false;
  }
  
  public static boolean isDocumentRoot(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return isDocumentRoot(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLElement && node.getParentNode() == null)
      return true;
    return false;
  }
  
  public static boolean hasChildren(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return hasChildren(((FuzzyXMLFormatComposite)node).getDelegate());
    if (node instanceof FuzzyXMLElement)
      return ((FuzzyXMLElement)node).hasChildren();
    return false;
  }
}
