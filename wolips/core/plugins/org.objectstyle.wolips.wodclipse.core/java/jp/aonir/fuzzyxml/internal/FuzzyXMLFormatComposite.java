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
  
  public boolean isScript() {
    return isScript(delegate);
  }
  
  public boolean isStyle() {
    return isStyle(delegate);
  }
  
  /** Utility methods **/
  
  public static boolean isNonBreaking(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    return (_node instanceof FuzzyXMLFormat && ((FuzzyXMLFormat)_node).isNonBreaking());
  }
  
  public static boolean isBreaking(FuzzyXMLNode node) {
    return !isNonBreaking(node);
  }
    
  public static boolean isHidden(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    return (_node instanceof FuzzyXMLFormat && ((FuzzyXMLFormat)_node).isHidden());
  }
  
  public static boolean hasNonBreakingStart(String value) {
    if (value == null || value.equals(""))
      return true;
    return !(Character.isWhitespace(value.charAt(0)));
  }

  public static boolean hasBreakingStart(String value) {
    return !hasNonBreakingStart(value);
  }

  public static boolean hasNonBreakingEnd(String value) {
    if (value == null || value.equals(""))
      return true;
    return !(Character.isWhitespace(value.charAt(value.length()-1)));
  }

  public static boolean hasBreakingEnd(String value) {
    return !hasNonBreakingEnd(value);
  }

  public static boolean hasNonBreakingEnd(FuzzyXMLNode node) {
    if (unwrap(node) instanceof FuzzyXMLComment)
      return true;
    return hasNonBreakingEnd(getValue(node));
  }
  
  public static boolean hasBreakingEnd(FuzzyXMLNode node) {
    return !hasNonBreakingEnd(node);
  }
  
  public static boolean hasNonBreakingStart(FuzzyXMLNode node) {
    if (unwrap(node) instanceof FuzzyXMLComment)
      return true;
    return hasNonBreakingStart(getValue(node));
  }

  public static boolean hasBreakingStart(FuzzyXMLNode node) {
    return !hasNonBreakingStart(node);
  }
  
  public static boolean isElement(FuzzyXMLNode node) {
    return unwrap(node) instanceof FuzzyXMLElement;
  }
  
  public static boolean isText(FuzzyXMLNode node) {
    return unwrap(node) instanceof FuzzyXMLText;
  }
  
  public static boolean hasCloseTag(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    return (_node instanceof FuzzyXMLElement && ((FuzzyXMLElement)_node).hasCloseTag());
  }
  
  public static boolean isMultiLine(FuzzyXMLNode node) {
    return getValue(node).contains("\n");
  }
  
  public static boolean hasLineBreaks(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    if (_node instanceof FuzzyXMLText) {
      return ((FuzzyXMLText)_node).hasLineBreaks();
    }
    return getValue(_node).contains("\n");
  }

  public static boolean isCode(FuzzyXMLNode node) {
    return (isScript(node.getParentNode()) || isStyle(node.getParentNode()));
  }
  
  public static boolean isComment(FuzzyXMLNode node) {
    return unwrap(node) instanceof FuzzyXMLComment;
  }
  
  public static String getValue(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node);
    if (_node instanceof FuzzyXMLText)
      return ((FuzzyXMLText)_node).getValue();
    if (_node instanceof FuzzyXMLComment)
      return ((FuzzyXMLComment)_node).getValue();
    return "";
  }
  
  public static String getName(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    if (_node instanceof FuzzyXMLElement)
      return ((FuzzyXMLElement)_node).getName();
    return "";
  }
  
  public static boolean isWOTag(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    return (_node instanceof FuzzyXMLElement && WodHtmlUtils.isWOTag(getName(_node).toLowerCase()));
  }
  
  public static boolean isDocumentRoot(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    return (_node instanceof FuzzyXMLElement && _node.getParentNode() == null);
  }
  
  public static boolean hasChildren(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    return (_node instanceof FuzzyXMLElement && ((FuzzyXMLElement)_node).hasChildren());
  }
    
  public static FuzzyXMLNode[] getChildren(FuzzyXMLNode node) {
    FuzzyXMLNode _node = unwrap(node); 
    if (_node instanceof FuzzyXMLElement)
      return ((FuzzyXMLElement)_node).getChildren();
    return null;
  }
  
  public static boolean isStyle(FuzzyXMLNode node) {
    return "style".equalsIgnoreCase(getName(node));
  }

  public static boolean isScript(FuzzyXMLNode node) {
    return "script".equalsIgnoreCase(getName(node));
  }

  private static FuzzyXMLNode unwrap(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return ((FuzzyXMLFormatComposite)node).getDelegate();
    return node;
  }
  
  private static FuzzyXMLFormatComposite wrap(FuzzyXMLNode node) {
    if (node instanceof FuzzyXMLFormatComposite)
      return (FuzzyXMLFormatComposite)node;
    return new FuzzyXMLFormatComposite(node);
  }
}
