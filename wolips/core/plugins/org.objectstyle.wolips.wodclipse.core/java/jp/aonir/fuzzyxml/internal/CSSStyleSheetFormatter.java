package jp.aonir.fuzzyxml.internal;

import java.io.IOException;
import java.io.StringReader;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.parser.CSSOMParser;

public class CSSStyleSheetFormatter {

  private final CSSStyleSheet _styleSheet;
  
  public CSSStyleSheetFormatter(CSSStyleSheet css) {
    _styleSheet = css;
    
  }
  
  public CSSStyleSheetFormatter(String data) throws IOException {
    this(parseCSS(data));
  }
  
  private static CSSStyleSheet parseCSS(String data) throws IOException {
    CSSStyleSheet styleSheet = new CSSStyleSheetImpl();
    CSSOMParser parser = new CSSOMParser();
    InputSource is = new InputSource(new StringReader(data));
    styleSheet = parser.parseStyleSheet(is);
    return styleSheet;
  }
  
  @Override
  public String toString() {
    StringBuffer stringbuffer = new StringBuffer();
    RenderContext renderContext = new RenderContext(true);
    renderContext.setIndent(1);
    renderContext.setIndentSize(2);
    return toString(renderContext, stringbuffer);
  }
  
  public String toString(RenderContext renderContext, StringBuffer cssBuffer) {
    CSSRuleList rules = _styleSheet.getCssRules();
    for(int i = 0; i < rules.getLength(); i++) {
      CSSStyleRuleImpl rule = (CSSStyleRuleImpl)rules.item(i);
      CSSStyleDeclaration cssDecl = rule.getStyle();
      int length = cssDecl.getLength();
      
      if (i > 0) {
        cssBuffer.append("\n");
        renderContext.appendIndent(cssBuffer);
      }
      
      cssBuffer.append(rule.getSelectorText()).append(" {");
      if (length > 1) {
        cssBuffer.append("\n");
      }
      renderContext.indent();
      for (int j = 0; j < cssDecl.getLength(); j++) {
        String item = cssDecl.item(j);
        
        if (length > 1)
          renderContext.appendIndent(cssBuffer);
        else
          cssBuffer.append(" ");
        cssBuffer.append(item).append(": ").append(cssDecl.getPropertyValue(item));
        /* Q: The last line doesn't really need a semicolon
         *    but the syntax highlighting expects it
         */
        if (true || j < length - 1)
          cssBuffer.append(";");
        if (length > 1)
          cssBuffer.append("\n");
        else
          cssBuffer.append(" ");
      }
      renderContext.outdent();
      if (length > 1) {
        renderContext.appendIndent(cssBuffer);
      }
      cssBuffer.append("}");
      cssBuffer.append("\n");
    }      
    return cssBuffer.toString();
  }
}
