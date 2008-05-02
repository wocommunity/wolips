package jp.aonir.fuzzyxml.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

public class WOHTMLRenderDelegate implements RenderDelegate {

  private FuzzyXMLFormatComposite lastNode = null;
  private final boolean useStickyWOTags;
  // Should this be a user option??
  private static final Set<String> STICKY_TAGS;
  // TODO: This should be a user option
  private static final int LINE_WRAP_LENGTH = 120;
  
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
  }
  
  public void afterCloseTag(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    if (_node.isComment() && hasNewLineEnd(xmlBuffer)) {
      renderContext.appendIndent(xmlBuffer);
    }
    if (_node.getParentNode() == null) {
      xmlBuffer.append("\n");
    }
    
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
//    System.out.println("beforeCloseTag: " + node + " " + isSticky(_node));

    if (renderContext.shouldFormat()) {
      renderContext.outdent();
      if (renderContext.isShowNewlines() ) {
        boolean append_newline = false;
        boolean append_space = false;
        boolean bufferHasBreakingEnd = (hasWhiteSpaceEnd(xmlBuffer) || hasTagEnd(xmlBuffer));

        if (isPreBlock(_node)) {
          // Do nothing
        } else 
        if (isSticky(_node)) {
          // Do nothing
        } else
        if (_node.isBreaking() && bufferHasBreakingEnd) {
          if (_node.getParentNode().isBreaking())
            append_newline = true;
        } 
        if (lastNodeWasHiddenText()) {
            append_space = true;
        }

        if (append_newline) {
          if (!hasNewLineEnd(xmlBuffer))
            xmlBuffer.append("\n");
          renderContext.appendIndent(xmlBuffer);
        } else
        if (append_space) {
          xmlBuffer.append(" ");
        }
      }
    }
  }

  public boolean beforeOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
//    System.out.println("beforeOpenTag: " + node + " " + node.getParentNode());

    if (renderContext.isShowNewlines() && renderContext.shouldFormat()) {
      boolean append_newline = false;
      boolean append_space = false;

      if (isSticky(_node.getParentNode()) && !lastNodeWasHiddenText()) {
        // Do nothing
      } else
      if (hasTagEnd(xmlBuffer)) {
        if (_node.getParentNode().isBreaking())
          append_newline = true;
        if (lastNodeWasHiddenText()) {
          if (_node.isBreaking())
            append_newline = true;
          else
            append_space = true;
        }
        if (_node.getParentNode().isDocumentRoot())
          append_newline = true;
      }
      if (isText(lastNode) && !lastNodeWasHiddenText()) {
        if (_node.getParentNode().isBreaking() && hasWhiteSpaceEnd(xmlBuffer))
          append_newline = true;
      }
      
      if (append_newline) {
        if (!hasNewLineEnd(xmlBuffer))
          xmlBuffer.append("\n");
        renderContext.appendIndent(xmlBuffer);
      } else
      if (append_space) {
        xmlBuffer.append(" ");
      }
    }
    return true;
  }
  
  public void beforeRender(RenderContext renderContext, StringBuffer xmlBuffer) {
  }

  public boolean renderNode(FuzzyXMLNode node, RenderContext renderContext,
      StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    
    boolean append_newline = false;
    boolean append_space = false;
    boolean bufferHasBreakingEnd = (hasWhiteSpaceEnd(xmlBuffer) || hasTagEnd(xmlBuffer));

//    System.out.println("renderNode: " + node + node.getParentNode());
    if (_node.isHidden()) {
      lastNode = _node;
      return false;
    }
    if (renderContext.shouldFormat() && renderContext.isShowNewlines()) {
      if (isSticky(lastNode) && _node.isElement()) {
        // Do Nothing
      } else
      if (_node.isText()) {
        if (!_node.hasBreakingStart() && !lastNodeWasHiddenText()) {
          // Do Nothing
        } else
        if (_node.parentNode().isBreaking() || lineIsTooLong(xmlBuffer)) {
          append_newline = true;
        }
      } else
      if (bufferHasBreakingEnd && _node.parentNode().isBreaking()) {
        if (lastNode.isHidden()) {
          append_newline = true;
        }
      }
      if (append_newline) {
        if (!hasNewLineEnd(xmlBuffer))
          xmlBuffer.append("\n");
        renderContext.appendIndent(xmlBuffer);
      } else
      if (append_space) {
        xmlBuffer.append(" ");
      }
      
      if (_node.isText() && _node.hasLineBreaks()) {  
        renderTextBlock((FuzzyXMLText)node, renderContext, xmlBuffer);
        lastNode = _node;
        return false;
      }
    }
    if (!_node.isElement())
      lastNode = _node;
    return true;
  }

  private void renderTextBlock(FuzzyXMLText node, RenderContext renderContext, StringBuffer xmlBuffer) {
    FuzzyXMLFormatComposite _node = new FuzzyXMLFormatComposite(node);
    StringBuffer indent = new StringBuffer();
    renderContext.appendIndent(indent);
    xmlBuffer.append(node.getValue().trim().replaceAll("\n\\s*", "\n" + indent.toString()));
    if (_node.hasBreakingEnd()) {
      if (_node.hasLineBreaks()) {
        xmlBuffer.append("\n");
      } else {
        xmlBuffer.append(" ");
      }
    }
  }
  
  private static boolean hasTagEnd(StringBuffer xmlBuffer) {
    if (xmlBuffer.length() == 0)
      return false;
    return xmlBuffer.charAt(xmlBuffer.length() -1) == '>';
  }

  private static boolean hasNewLineEnd(StringBuffer xmlBuffer) {
    if (xmlBuffer.length() == 0)
      return false;
    return xmlBuffer.charAt(xmlBuffer.length() -1) == '\n';
  }

  private static boolean hasWhiteSpaceEnd(StringBuffer xmlBuffer) {
    if (xmlBuffer.length() == 0)
      return false;
    return Character.isWhitespace(xmlBuffer.charAt(xmlBuffer.length() - 1));
  }
 
  private boolean lastNodeWasHiddenText() {
    return lastNode == null || (lastNode.isText() && lastNode.isHidden());
  }

  private boolean _isStickyTag(FuzzyXMLFormatComposite node) {
    return STICKY_TAGS.contains(node.getName().toLowerCase()) || (useStickyWOTags && node.isWOTag());
  }
  
  private boolean isSticky(FuzzyXMLFormatComposite node) {
    return !lastNodeWasHiddenText() && _isStickyTag(node);
  }
  
  private static boolean isPreBlock(FuzzyXMLFormatComposite node) {
    return "pre".equalsIgnoreCase(node.getName());
  }
  
  private static boolean lineIsTooLong(StringBuffer xmlBuffer) {
    int lastLineStart = xmlBuffer.lastIndexOf("\n");
    String line;
    if (lastLineStart == -1)
      line = xmlBuffer.toString();
    else
      line = xmlBuffer.substring(lastLineStart);
    
    return (line.length() > LINE_WRAP_LENGTH);
  }

  private static boolean isText(FuzzyXMLFormatComposite node) {
    if (node == null)
      return false;
    return node.isText();
  }
}
