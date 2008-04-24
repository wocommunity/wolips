package jp.aonir.fuzzyxml.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

public class WOHTMLRenderDelegate implements RenderDelegate {

  private FuzzyXMLFormatComposite lastNode = null;
  private final boolean useStickyWOTags;
  //TODO Make this a user option??
  private final boolean anyTagIsSticky;
  private static final Set<String> STICKY_TAGS;
  
  private static final String[] SPACE_EFFECTED_TAGS = {
    "a", "img", "u"
  };
  
  static {
    STICKY_TAGS = new HashSet<String>();
    STICKY_TAGS.addAll(Arrays.asList(SPACE_EFFECTED_TAGS));
  }
  
  public WOHTMLRenderDelegate() {
    this(true);
  }
  
  public WOHTMLRenderDelegate(boolean _useStickyWOTags) {
    useStickyWOTags = _useStickyWOTags;
    // Q: Hard code this to true for the moment
    anyTagIsSticky = true;
  }
  
  public void afterCloseTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    if (_node.isDocumentRoot() || _node.getParentNode().isDocumentRoot()) {
      lastNode = null;
    } 
    else {
      lastNode = _node;
    }
  }

  public void afterOpenTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    if (renderContext.shouldFormat()) {
      renderContext.indent();
    }
    lastNode = _node;
  }

  public void afterRender(RenderContext renderContext, StringBuffer xmlBuffer) {
  }

  public void beforeCloseTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
//    System.out.println("beforeCloseTag: " + node + " " + lastNodeWasHiddenText());

    if (renderContext.shouldFormat()) {
      renderContext.outdent();
      if (renderContext.isShowNewlines() ) {
        /* 
         * Not a <pre> tag block
         * and
         * Breaking or non sticky wo tag following element or whitespace
         * or 
         * Any tag following preexisting whitespace 
         */
        if ( !isPreBlock(_node) && 
            ( ((hasWhiteSpaceEnd(xmlBuffer) || hasTagEnd(xmlBuffer)) && 
              (_node.isBreaking() && !isStickyWOTag(_node)) ) || 
              lastNodeWasHiddenText() ) ) {
          if (!hasNewLineEnd(xmlBuffer)) {
            xmlBuffer.append("\n");
          }
          renderContext.appendIndent(xmlBuffer);
        }
      }
    }
  }

  public boolean beforeOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
//    System.out.println("beforeOpenTag: " + node + " " + lastNodeWasHiddenText());

    if (renderContext.isShowNewlines() && renderContext.shouldFormat()) {
      /* 
       * Element block following element and not a sticky wo tag 
       * or
       * Self closing element following newline
       * or
       * Self closing element following element with preexisting whitespace
       * 
       */
      if ( (hasTagEnd(xmlBuffer) && _node.hasCloseTag() && !isStickyWOTag(_node) && _node.hasChildren()) || 
           (hasNewLineEnd(xmlBuffer) && _node.hasCloseTag()) ||
           (hasTagEnd(xmlBuffer) && !_node.hasCloseTag() && lastNodeWasHiddenText()) ) {
        if (!hasNewLineEnd(xmlBuffer)) {
          xmlBuffer.append("\n");
        }
        renderContext.appendIndent(xmlBuffer);
      } 
    }
    return true;
  }
  
  public void beforeRender(RenderContext renderContext, StringBuffer xmlBuffer) {
  }

  public boolean renderNode(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
//    System.out.println("renderNode: " + _node + _node.isHidden());
    if (_node.isHidden()) {
      lastNode = _node;
      return false;
    }
    if (renderContext.shouldFormat() && renderContext.isShowNewlines()) {
      // Element following element or WO tag element following hidden text
      if (hasTagEnd(xmlBuffer) && _node.isElement() && !isStickyWOTag(_node) && _node.parentNode().isBreaking()) {
        if (!hasNewLineEnd(xmlBuffer)) {
          xmlBuffer.append("\n");
        }
        renderContext.appendIndent(xmlBuffer); 
      } else 
      // Any breaking node or text node with breaking start or any node following a breaking end
      if ((_node.isText() && !_node.isCode()) && _node.hasBreakingStart() && (_node.parentNode().isBreaking() || lineIsTooLong(xmlBuffer))) {
        if (!hasNewLineEnd(xmlBuffer)) {
          xmlBuffer.append("\n");
        }
        renderContext.appendIndent(xmlBuffer); 
      } else 
      // A text node with a breaking start or following a breaking end
      if ((_node.isText() || _node.isCode()) && _node.parentNode().isBreaking() && 
          (_node.hasBreakingStart() || lastNode == null || lastNode.isHidden() || lastNode.hasBreakingEnd()) ) {
        if (!hasNewLineEnd(xmlBuffer)) {
          xmlBuffer.append("\n");
        }
        renderContext.appendIndent(xmlBuffer);

        if (_node.isText() && _node.hasLineBreaks()) {  
          if (_node.isCode()) {
            renderCodeBlock((FuzzyXMLText)node, renderContext, xmlBuffer);            
          } else {
            renderTextBlock((FuzzyXMLText)node, renderContext, xmlBuffer);
          }
          lastNode = _node;
          return false;
        }
      } else
      // A multiline text block that isn't empty
      if ((_node.isText() || _node.isCode()) && _node.hasLineBreaks() && !_node.isHidden()) {
        if (_node.isCode()) {
          renderCodeBlock((FuzzyXMLText)node, renderContext, xmlBuffer);            
        } else {
          renderTextBlock((FuzzyXMLText)node, renderContext, xmlBuffer);
        }
        lastNode = _node;
        return false;
      }
    }
    return true;
  }
  
  private void renderTextBlock(FuzzyXMLText node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    String value = node.getValue().replaceFirst("^[\r\t ]*\n", "").replaceAll("\n[\r\t ]*$", "\n").replaceAll("\t","    ");
    StringBuffer indent = new StringBuffer();
    renderContext.appendIndent(indent);
    value = value.replaceAll("(^|\n) +", "$1");
    if (_node.hasNonBreakingStart())
      value = value.replaceAll("^ *", "");
    
    value = value.replaceAll("(\n)(.+)", "$1" + indent + "$2");
    xmlBuffer.append(value);
  }

  private void renderCodeBlock(FuzzyXMLText node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    if (_node.getParentNode().getName().equals("style")) {
      try {
        CSSStyleSheetFormatter cssFormatter = new CSSStyleSheetFormatter(_node.getValue().toString());
        cssFormatter.toString(renderContext, xmlBuffer);
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (_node.hasNonBreakingStart()) {
      xmlBuffer.append(_node.getValue().replaceAll("\n[\r\t ]*$", "\n"));
      return;
    }
    
    /* XXX Q: This is a hack
     *        It should really be replaced by a proper JS formatter
     *        and moved into FuzzyXMLScriptImpl
     */       
    String value = node.getValue().replaceFirst("^[\r\t ]*\n", "").replaceAll("\n[\r\t ]*$", "\n").replaceAll("\t","    ");
    StringBuffer indent = new StringBuffer();
    renderContext.appendIndent(indent);

    Pattern pattern = Pattern.compile("^[^\\s]", Pattern.MULTILINE);
    while (value.length() > 0 && !pattern.matcher(value).find()) {
      value = value.replaceAll("^ ", "").replaceAll("\n ", "\n");
    }
    if (_node.hasNonBreakingStart()) {
      value = value.replaceAll("^ *", "");
    }
    value = value.replaceAll("(\n)(.+)", "$1" + indent + "$2");
   
    xmlBuffer.append(value);
  }

  private boolean hasTagEnd(StringBuffer xmlBuffer) {
    if (xmlBuffer.length() == 0)
      return false;
    return xmlBuffer.charAt(xmlBuffer.length() -1) == '>';
  }

  private boolean hasNewLineEnd(StringBuffer xmlBuffer) {
    if (xmlBuffer.length() == 0)
      return false;
    return xmlBuffer.charAt(xmlBuffer.length() -1) == '\n';
  }

  private boolean hasWhiteSpaceEnd(StringBuffer xmlBuffer) {
    if (xmlBuffer.length() == 0)
      return false;
    return Character.isWhitespace(xmlBuffer.charAt(xmlBuffer.length() - 1));
  }
 
  private boolean lastNodeWasHiddenText() {
    return lastNode == null || (lastNode.isText() && lastNode.isHidden());
  }

  private boolean isStickyTag(FuzzyXMLFormatComposite node) {
    return STICKY_TAGS.contains(node.getName().toLowerCase()) || node.isWOTag();
  }
  
  private boolean isStickyWOTag(FuzzyXMLFormatComposite node) {
    if (anyTagIsSticky)
      return useStickyWOTags && !lastNodeWasHiddenText() && (isStickyTag(node) || isStickyTag(lastNode));
    return useStickyWOTags && !lastNodeWasHiddenText() && isStickyTag(node) && isStickyTag(lastNode);
  }
  
  private boolean isPreBlock(FuzzyXMLFormatComposite node) {
    return "pre".equals(node.getName().toLowerCase());
  }
  
  private boolean lineIsTooLong(StringBuffer xmlBuffer) {
    int lastLineStart = xmlBuffer.lastIndexOf("\n");
    String line = xmlBuffer.substring(lastLineStart).trim();
    if (line.length() > 80)
      return true;
    return false;
  }
}
