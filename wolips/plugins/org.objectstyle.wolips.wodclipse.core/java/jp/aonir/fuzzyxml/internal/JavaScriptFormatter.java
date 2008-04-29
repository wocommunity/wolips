package jp.aonir.fuzzyxml.internal;

public class JavaScriptFormatter {
//  private final String _source;
//  private final boolean _incomment;
//  private Scriptable _scope;
//  private Context _context;
//
//  private static final String COMMENT_PREFIX = "__ZZ__";
//  private static final String BALANCED_QUOTES_REGEX = "\"(?:[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|'(?:[^'\\\\]*(?:\\\\.[^'\\\\]*)*)'";
//  private static final String BLOCK_COMMENTS_REGEX = "/\\*(?:[^*]*(?:\\*[^/])*)*\\*/";
//  private static final String INLINE_COMMENTS_REGEX = "//[^\\n]*";
//  private static final String LEADING_CODE_REGEX = "(?:[^\\n\\'\"/]|" + BALANCED_QUOTES_REGEX + ")*";
//  private static final String COMMENTS_REGEX = "((?:^|\\n)(" + LEADING_CODE_REGEX + ")(?:(" + INLINE_COMMENTS_REGEX + "|" + BLOCK_COMMENTS_REGEX + ")))";
//  private static final String ESCAPED_COMMENTS_REGEX = "((\\s*)" + COMMENT_PREFIX + " = \"(.*)\";\\n?)";
//  private static final String FUNCTION_WRAPPER_REGEX = "^\\s*function\\s*\\(\\)\\s*\\{\\s*|\\s*\\}\\s*$";
//  
//  private static final Pattern COMMENTS_PATTERN = Pattern.compile(COMMENTS_REGEX);
//  private static final Pattern ESCAPED_COMMENTS_PATTERN = Pattern.compile(ESCAPED_COMMENTS_REGEX);
//  
//  public JavaScriptFormatter(String data) {
//    _incomment = data.trim().startsWith("<!--");
//    _context = ContextFactory.getGlobal().enterContext();
//    _scope = _context.initStandardObjects();
//    _source = parseSource(data);
//  }
//
//  @Override
//  protected void finalize() throws Throwable {
//    _context.exit();
//    super.finalize();
//  }
//  
//  private String parseSource(String source) {
//
//    try {
//      
//      // Convert comments to inline code
//      String data = source.replace(COMMENT_PREFIX, "__" + COMMENT_PREFIX);
//
//      Matcher matcher = COMMENTS_PATTERN.matcher(source); // get a matcher object
//      StringBuffer sb = new StringBuffer();
//      while (matcher.find()) {
//        String comment = matcher.group(3);
//        if (!"".equals(matcher.group(2).trim())) {
//          comment = "+" + comment;
//        }
//        comment = StringEscapeUtils.escapeJavaScript(comment); 
//        
//        String replacement;
//        if (comment.trim().endsWith("-->")) {
//          replacement = "";
//        } else {
////          String code = matcher.group(1);
////          if (code.trim().endsWith(","))
////            replacement = "$1";
////          else
//            replacement = "$1\n" + Matcher.quoteReplacement(COMMENT_PREFIX) + " = \'" + Matcher.quoteReplacement(comment) + "\';\n";
//        }
//        matcher.appendReplacement(sb, replacement);
//      }
//      matcher.appendTail(sb);
//      data = sb.toString();
//
//      String script = "$$ = function () {" + data + "\n}; $$.toString()";
//      System.out.println(script);
//      String result = _context.evaluateString(_scope, script, "<cmd>", 1, null).toString();
////      System.out.println(result);
//      result = result.replaceAll(FUNCTION_WRAPPER_REGEX, "").replace("\n    ", "\n");
//      
//      sb = new StringBuffer();
//      matcher = ESCAPED_COMMENTS_PATTERN.matcher(result);
//      while (matcher.find()) {
//        String comment = matcher.group(3);
//        comment = StringEscapeUtils.unescapeJavaScript(comment).replaceAll("\n\\s*", "\n ");
//        if (comment.startsWith("+")) {
//          comment = comment.replaceFirst("\\+", " ");
//          String replacement = Matcher.quoteReplacement(comment+"\n");
//          matcher.appendReplacement(sb, replacement);
//        } else {
//          String replacement = "$2" +  Matcher.quoteReplacement(comment+"\n");
//          matcher.appendReplacement(sb, replacement);
//        }
//      }
//      matcher.appendTail(sb);
//      result = sb.toString().replace("__"+COMMENT_PREFIX, COMMENT_PREFIX);
//
//      return result;
//    } catch (Throwable e) {
//      e.printStackTrace();
//      throw new RuntimeException(e);
//    } finally {
////      _context.exit();
//    }
//  }
//
//  @Override
//  public String toString() {
//    String result = _source;
//    if (_incomment) {
//      result = "<!--\n" + _source + "\n// -->";
//    }
//    return result;
//  }
//
//  public String toString(RenderContext renderContext, StringBuffer jsBuffer) {
//    StringBuffer indent = new StringBuffer();
//    int indentCount = renderContext.getIndent();
//
//    renderContext.setIndent(1);
//    renderContext.appendIndent(indent);
//    renderContext.setIndent(indentCount);
//
//    String result = toString();
//    String[] lines = result.split("\n");
//    for (int i = 0; i < lines.length; i++) {
//      String leadin = lines[i].replaceFirst("^(\\s*).*$", "$1").replaceAll("    ", indent.toString());
//      String line = leadin + lines[i].replaceFirst("^\\s*", "");
//      if (i > 0)
//        renderContext.appendIndent(jsBuffer);
//      jsBuffer.append(line).append("\n");
//    }
//    return result;
//  }
}
