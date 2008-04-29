package jp.aonir.fuzzyxml.internal;


public class CSSStyleSheetFormatter {

//  private final CSSStyleSheet _styleSheet;
//  private final FuzzyXMLElement _style;
//  
//  public CSSStyleSheetFormatter(CSSStyleSheet css) {
//    _styleSheet = css;
//    
//  }
  
//  public CSSStyleSheetFormatter(String data) {
//    this(parseCSS(data));
//    _stylesheet = data;
//    System.out.println("Stylesheet = " + data);
//  }
//  
//  public CSSStyleSheetFormatter(FuzzyXMLElement style) {
//    _style = style;
//  }
//  
//  private static CSSStyleSheet parseCSS(String data) throws IOException {
//    CSSStyleSheet styleSheet = new CSSStyleSheetImpl();
//    CSSOMParser parser = new CSSOMParser();
//    InputSource is = new InputSource(new StringReader(data));
//    styleSheet = parser.parseStyleSheet(is);
//    return styleSheet;
//  }
//  
//  public String toCSSString() {
//    StringBuffer stringbuffer = new StringBuffer();
//    RenderContext renderContext = new RenderContext(true);
//    renderContext.setIndent(1);
//    renderContext.setIndentSize(2);
//    _style.toXMLString(renderContext, stringbuffer);
//    return stringbuffer.toString();
//  }
//
//  public void toCSSString(RenderContext renderContext, StringBuffer cssBuffer) {
//    StringBuffer stringbuffer = new StringBuffer();
//    _style.toXMLString(renderContext, stringbuffer);
//    String value = stringbuffer.toString().replaceFirst("^[\r\t ]*\n", "").replaceAll("\n[\r\t ]*$", "\n").replaceAll("\t","    ");
//    StringBuffer indent = new StringBuffer();
//    renderContext.appendIndent(indent);
//
//    Pattern pattern = Pattern.compile("^[^\\s]", Pattern.MULTILINE);
//    while (value.length() > 0 && !pattern.matcher(value).find()) {
//      value = value.replaceAll("^ ", "").replaceAll("\n ", "\n");
//    }
//    value = value.replaceAll("(\n)(.+)", "$1" + indent + "$2");
//    cssBuffer.append(value);
//  }
//
//  
//public toCSSString(RenderContext renderContext, StringBuffer cssBuffer) {
//  CSSRuleList rules = _styleSheet.getCssRules();
//  for(int i = 0; i < rules.getLength(); i++) {
//    CSSStyleRuleImpl rule = (CSSStyleRuleImpl)rules.item(i);
//    CSSStyleDeclaration cssDecl = rule.getStyle();
//    int length = cssDecl.getLength();
//    
//    if (i > 0) {
//      cssBuffer.append("\n");
//      renderContext.appendIndent(cssBuffer);
//    }
//    
//    cssBuffer.append(rule.getSelectorText()).append(" {");
//    if (length > 1) {
//      cssBuffer.append("\n");
//    }
//    renderContext.indent();
//    for (int j = 0; j < cssDecl.getLength(); j++) {
//      String item = cssDecl.item(j);
//      
//      if (length > 1)
//        renderContext.appendIndent(cssBuffer);
//      else
//        cssBuffer.append(" ");
//      cssBuffer.append(item).append(": ").append(cssDecl.getPropertyValue(item));
//      /* Q: The last line doesn't really need a semicolon
//       *    but the syntax highlighting expects it
//       */
//      if (true || j < length - 1)
//        cssBuffer.append(";");
//      if (length > 1)
//        cssBuffer.append("\n");
//      else
//        cssBuffer.append(" ");
//    }
//    renderContext.outdent();
//    if (length > 1) {
//      renderContext.appendIndent(cssBuffer);
//    }
//    cssBuffer.append("}");
//    cssBuffer.append("\n");
//  }      
//  return cssBuffer.toString();
//}


}
