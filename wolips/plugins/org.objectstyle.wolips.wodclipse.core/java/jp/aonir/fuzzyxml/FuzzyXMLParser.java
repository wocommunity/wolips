package jp.aonir.fuzzyxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.event.FuzzyXMLErrorEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorListener;
import jp.aonir.fuzzyxml.internal.FuzzyXMLAttributeImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLCDATAImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLCommentImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLDocTypeImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLDocumentImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLElementImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLProcessingInstructionImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLTextImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLUtil;
import jp.aonir.fuzzyxml.resources.Messages;

import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class FuzzyXMLParser {

  private Stack<FuzzyXMLNode> stack = new Stack<FuzzyXMLNode>();
  private String originalSource;
  private List<FuzzyXMLElementImpl> roots;
  private FuzzyXMLDocType docType;

  private List<FuzzyXMLErrorListener> listeners = new ArrayList<FuzzyXMLErrorListener>();
  private List<FuzzyXMLElement> nonCloseElements = new ArrayList<FuzzyXMLElement>();
  private List<String> looseNamespaces = new ArrayList<String>();
  private List<String> looseTags = new ArrayList<String>();

  private boolean isHTML = false;

  // パースに使用する正規表現
  private Pattern tag = Pattern.compile("<((|/)([^<>]*))([^<]|>)");
  //	private Pattern attr = Pattern.compile("([\\w:]+?)\\s*=(\"|')([^\"]*?)\\2");
  private Pattern docTypeName = Pattern.compile("^<!DOCTYPE[ \r\n\t]+([\\w\\-_]*)");
  private Pattern docTypePublic = Pattern.compile("PUBLIC[ \r\n\t]+\"(.*?)\"[ \r\n\t]+\"(.*?)\"");
  private Pattern docTypeSystem = Pattern.compile("SYSTEM[ \r\n\t]+\"(.*?)\"");
  private Pattern docTypeSubset = Pattern.compile("\\[([^\\]]*)\\]>");

  public FuzzyXMLParser() {
    this(false);
  }

  public FuzzyXMLParser(boolean isHTML) {
    super();
    this.roots = new LinkedList<FuzzyXMLElementImpl>();
    this.isHTML = isHTML;
    // MS: Hardcoded that "wo" is a loose namespace
    addLooseNamespace("wo");
    addLooseNamespace("webobject");
    addLooseNamespace("webobjects");
    addLooseTag("img");
    addLooseTag("br");
    addLooseTag("p");
    addLooseTag("hr");
    addLooseTag("li");
    addLooseTag("meta");
    addLooseTag("link");
    addLooseTag("input");
    addLooseTag("spacer");
  }

  /**
   * A "loose" tag is like br or p where you allow close tags to not exist.
   * 
   * @param looseTag the name of the tag to make loose
   */
  public void addLooseTag(String looseTag) {
    looseTags.add(looseTag);
  }

  /**
   * A "loose" namespace is like the wo: namespace.  We don't actually require that
   * wo:if have a corresponding wo:if close tag -- it actually just needs a 
   * wo close tag.
   * 
   * @param namespace the name of the namespace to make loose
   */
  public void addLooseNamespace(String namespace) {
    looseNamespaces.add(namespace);
  }

  /**
   * エラーハンドリング用のリスナを追加します。
   * 
   * @param listener リスナ
   */
  public void addErrorListener(FuzzyXMLErrorListener listener) {
    this.listeners.add(listener);
  }

  private void fireErrorEvent(int offset, int length, String message, FuzzyXMLNode node) {
    FuzzyXMLErrorEvent evt = new FuzzyXMLErrorEvent(offset, length, message, node);
    for (FuzzyXMLErrorListener listener : listeners) {
      listener.error(evt);
    }
  }

  /**
   * 入力ストリームからXMLドキュメントをパースします。
   * 文字コードはXML宣言にしたがって判別されます。
   * 
   * @param in 入力ストリーム
   * @return パース結果
   * @throws IOException
   */
  public FuzzyXMLDocument parse(InputStream in) throws IOException {
    byte[] bytes = FuzzyXMLUtil.readStream(in);
    String encode = FuzzyXMLUtil.getEncoding(bytes);
    if (encode == null) {
      return parse(new String(bytes));
    }
    return parse(new String(bytes, encode));
  }

  /**
   * ファイルからXMLドキュメントをパースします。
   * 文字コードはXML宣言にしたがって判別されます。
   * 
   * @param file ファイル
   * @return パース結果
   * @throws IOException
   */
  public FuzzyXMLDocument parse(File file) throws IOException {
    byte[] bytes = FuzzyXMLUtil.readStream(new FileInputStream(file));
    String encode = FuzzyXMLUtil.getEncoding(bytes);
    if (encode == null) {
      return parse(new String(bytes));
    }
    return parse(new String(bytes, encode));
  }

  protected int _parse(String source, int initialOffset, boolean woOnly) {
    // パースを開始
    Matcher matcher = tag.matcher(source);
    int lastIndex = initialOffset - 1;
    while (matcher.find()) {
      int start = matcher.start() + initialOffset;
      int end = matcher.end() + initialOffset;
      if (lastIndex != (initialOffset - 1) && lastIndex < start) {
        handleText(lastIndex, start, true);
      }
      String text = matcher.group(1).trim();
      // 閉じタグ
      if (!woOnly && text.startsWith("%")) {
        // ignore
        handleText(start, end, false);
      }
      else if (!woOnly && text.startsWith("?")) {
        handleDeclaration(start, end);
      }
      else if (!woOnly && (text.startsWith("!DOCTYPE") || text.startsWith("!doctype"))) {
        handleDoctype(start, end, text);
      }
      else if (!woOnly && text.startsWith("![CDATA[")) {
        handleCDATA(start, end, originalSource.substring(start, end));
      }
      else if (text.startsWith("/") && (!woOnly || WodHtmlUtils.isWOTag(text.substring(1)))) {
        handleCloseTag(start, end, text);
      }
      else if (!woOnly && text.startsWith("!--")) {
        handleComment(start, end, originalSource.substring(start, end));
      }
      else if (text.endsWith("/") && (!woOnly || WodHtmlUtils.isWOTag(text))) {
        handleEmptyTag(start, end);
      }
      else if (!woOnly || WodHtmlUtils.isWOTag(text)) {
        handleStartTag(start, end);
      }
      lastIndex = end;
    }
    return lastIndex;
  }
  
  /**
   * 引数として渡されたXMLソースをパースしてFuzzyXMLDocumentオブジェクトを返却します。
   * 
   * @param source XMLソース
   * @return パース結果のFuzzyXMLDocumentオブジェクト
   */
  public FuzzyXMLDocument parse(String source) {
    // オリジナルのソースを保存しておく
    originalSource = source;
    // コメント、CDATA、DOCTYPE部分を除去
    source = FuzzyXMLUtil.comment2space(source, true);
    source = FuzzyXMLUtil.escapeScript(source);
    source = FuzzyXMLUtil.scriptlet2space(source, true);
    source = FuzzyXMLUtil.cdata2space(source, true);
    source = FuzzyXMLUtil.doctype2space(source, true);
    source = FuzzyXMLUtil.processing2space(source, true);
    source = FuzzyXMLUtil.escapeString(source);

    int lastIndex = _parse(source, 0, false);

    if (stack.size() > 0 && nonCloseElements.size() > 0) {
      FuzzyXMLElementImpl lastElement = (FuzzyXMLElementImpl) nonCloseElements.get(nonCloseElements.size() - 1);
      String lowercaseLastElementName = lastElement.getName().toLowerCase();
      if (!looseTags.contains(lowercaseLastElementName)) {
        fireErrorEvent(lastElement.getOffset(), lastElement.getLength(), Messages.getMessage("error.noCloseTag", lastElement.getName()), null);
      }

      for (FuzzyXMLNode openNode : stack) {
        if (openNode instanceof FuzzyXMLElementImpl) {
          FuzzyXMLElementImpl openElement = (FuzzyXMLElementImpl) openNode;
          openElement.setLength(lastIndex - openElement.getOffset());
          if (openElement.getParentNode() == null) {
            roots.add(openElement);
          }
          else {
            ((FuzzyXMLElementImpl) openElement.getParentNode()).appendChildWithNoCheck(openElement);
          }
        }
      }
    }

    FuzzyXMLElement docElement = null;
    if (roots.size() == 0) {
      docElement = new FuzzyXMLElementImpl(null, "document", 0, originalSource.length(), 0);
      //docElement.appendChild(root);
    }
    else {
      FuzzyXMLElementImpl firstRoot = roots.get(0);
      FuzzyXMLElementImpl lastRoot = roots.get(roots.size() - 1);
      docElement = new FuzzyXMLElementImpl(null, "document", firstRoot.getOffset(), lastRoot.getOffset() + lastRoot.getLength() - firstRoot.getOffset(), 0);
      for (FuzzyXMLElementImpl root : roots) {
        ((FuzzyXMLElementImpl) docElement).appendChildWithNoCheck(root);
      }
    }
    FuzzyXMLDocumentImpl doc = new FuzzyXMLDocumentImpl(docElement, docType);
    doc.setHTML(this.isHTML);
    return doc;
  }

  /** CDATAノードを処理します。 */
  private void handleCDATA(int offset, int end, String text) {
    if (getParent() != null) {
      text = text.replaceFirst("<!\\[CDATA\\[", "");
      text = text.replaceFirst("\\]\\]>", "");
      FuzzyXMLCDATAImpl cdata = new FuzzyXMLCDATAImpl(getParent(), text, offset, end - offset);
      ((FuzzyXMLElement) getParent()).appendChild(cdata);
      
      stack.push(cdata);
      _parse(text, offset + "<![CDATA[".length(), true);
      FuzzyXMLNode poppedNode = stack.pop();
      if (poppedNode != cdata) {
        stack.push(poppedNode);
      }
    }
  }

  /** テキストノードを処理します。 */
  private void handleText(int offset, int end, boolean escape) {
    String text = originalSource.substring(offset, end);
    if (getParent() != null) {
      FuzzyXMLTextImpl textNode = new FuzzyXMLTextImpl(getParent(), FuzzyXMLUtil.decode(text, isHTML), offset, end - offset);
      textNode.setEscape(escape);
      ((FuzzyXMLElement) getParent()).appendChild(textNode);
    }
  }

  /** XML宣言（処理命令）を処理します。 */
  private void handleDeclaration(int offset, int end) {
    if (getParent() != null) {
      // 余計な部分を削る
      String text = originalSource.substring(offset, end);
      text = text.replaceFirst("^<\\?", "");
      text = text.replaceFirst("\\?>$", "");
      text = text.trim();

      String[] dim = text.split("[ \r\n\t]+");
      String name = dim[0];
      String data = text.substring(name.length()).trim();

      FuzzyXMLProcessingInstructionImpl pi = new FuzzyXMLProcessingInstructionImpl(null, name, data, offset, end - offset);
      ((FuzzyXMLElement) getParent()).appendChild(pi);
    }
  }

  /** DOCTYPE宣言を処理します。 */
  private void handleDoctype(int offset, int end, String text) {
    if (docType == null) {
      String name = "";
      String publicId = "";
      String systemId = "";
      String internalSubset = "";

      text = originalSource.substring(offset, end);
      Matcher matcher = docTypeName.matcher(text);
      if (matcher.find()) {
        name = matcher.group(1);
      }
      matcher = docTypePublic.matcher(text);
      if (matcher.find()) {
        publicId = matcher.group(1);
        systemId = matcher.group(2);
      }
      else {
        matcher = docTypeSystem.matcher(text);
        if (matcher.find()) {
          systemId = matcher.group(1);
        }
      }
      matcher = docTypeSubset.matcher(text);
      if (matcher.find()) {
        internalSubset = matcher.group(1);
      }
      docType = new FuzzyXMLDocTypeImpl(null, name, publicId, systemId, internalSubset, offset, end - offset);
    }
  }

  /** 閉じタグを処理します。 */
  private void handleCloseTag(int offset, int end, String text) {
    handleCloseTag(offset, end, text, true);
  }

  private void handleCloseTag(int offset, int end, String text, boolean showMismatchError) {
    if (stack.size() == 0) {
      return;
    }
    String tagName = text.substring(1).trim();

    // MS: Chuck does close tags like </webobject closing something else> 
    int chuckIndex = tagName.indexOf(' ');
    if (chuckIndex != -1) {
      String chuckWord = tagName.substring(0, chuckIndex);
      if (WodHtmlUtils.isWOTag(chuckWord)) {
        tagName = chuckWord;
      }
    }

    FuzzyXMLElementImpl lastOpenElement = (FuzzyXMLElementImpl) stack.pop();
    String lowercaseLastOpenElementName = lastOpenElement.getName().toLowerCase();
    String lowercaseCloseTagName = tagName.toLowerCase();
    
    boolean closeTagMatches = lowercaseLastOpenElementName.equals(lowercaseCloseTagName);
    //System.out.println("FuzzyXMLParser.handleCloseTag: lastOpen = " + lowercaseLastOpenElementName + ", close = " + lowercaseCloseTagName);
    if (!closeTagMatches) {
      // Allow </wo> to close </wo:if>
      boolean looseNamespace = false;
      int colonIndex = lowercaseLastOpenElementName.indexOf(':');
      if (colonIndex != -1) {
        String elementNamespace = lowercaseLastOpenElementName.substring(0, colonIndex);
        if (lowercaseCloseTagName.equals(elementNamespace) && looseNamespaces.contains(elementNamespace)) {
          tagName = lastOpenElement.getName();
          lowercaseCloseTagName = lowercaseLastOpenElementName;
          looseNamespace = true;
        }
      }

      if (!looseNamespace) {
        boolean looseTag = false;
        if (looseTags.contains(lowercaseLastOpenElementName)) {
          looseTag = true;
        }

        if (looseTag) {
          while (lowercaseLastOpenElementName != null && !lowercaseLastOpenElementName.equals(lowercaseCloseTagName) && looseTags.contains(lowercaseLastOpenElementName)) {
            int lastOpenElementEndOffset = end;
            //int lastOpenElementEndOffset = lastOpenElement.getOffset() + lastOpenElement.getLength();
            stack.push(lastOpenElement);
            handleCloseTag(lastOpenElementEndOffset, lastOpenElementEndOffset, "/" + lastOpenElement.getName(), false);
            
            /*
            FuzzyXMLElement looseElement = lastOpenElement;
            FuzzyXMLNode[] looseElementChildren = lastOpenElement.getChildren();
            FuzzyXMLElement looseElementParent = (FuzzyXMLElement) lastOpenElement.getParentNode();
            for (FuzzyXMLNode looseElementChild : looseElementChildren) {
              looseElement.removeChild(looseElementChild);
              looseElementParent.insertAfter(looseElementChild, looseElement);
              //((AbstractFuzzyXMLNode) looseElementChild).setOffset(looseElementChild.getOffset() + 1);
            }
            */
            
            if (stack.size() == 0) {
              lastOpenElement = null;
              lowercaseLastOpenElementName = null;
            }
            else {
              lastOpenElement = (FuzzyXMLElementImpl) stack.pop();
              lowercaseLastOpenElementName = lastOpenElement.getName().toLowerCase();
            }
          }
        }
        else {
          FuzzyXMLElement matchingOpenElement = null;
          for (FuzzyXMLElement nonCloseElement : nonCloseElements) {
            if (nonCloseElement.getName().equalsIgnoreCase(lowercaseCloseTagName)) {
              matchingOpenElement = nonCloseElement;
            }
          }
          if (matchingOpenElement == null) {
            if (showMismatchError) {
              fireErrorEvent(offset, end - offset, Messages.getMessage("error.noStartTag", tagName), null);
            }
            stack.push(lastOpenElement);
            return;
          }

          //System.out.println("FuzzyXMLParser.handleCloseTag: mismatched close " + lastOpenElement.getName());
          if (showMismatchError) {
            fireErrorEvent(offset, end - offset, "</" + tagName + "> occurred before </" + lastOpenElement.getName() + ">", null);
            fireErrorEvent(lastOpenElement.getOffset(), lastOpenElement.getLength(), "</" + tagName + "> occurred before </" + lastOpenElement.getName() + ">", null);
          }
          stack.push(lastOpenElement);
          handleCloseTag(offset, offset, "/" + lastOpenElement.getName(), false);
          lastOpenElement = (FuzzyXMLElementImpl) stack.pop();
          lowercaseLastOpenElementName = lastOpenElement.getName().toLowerCase();
        }
        /*
         boolean matchesOpenElement = false;
         if (looseTag) {
         for (FuzzyXMLElement nonCloseElement : nonCloseElements) {
         if (nonCloseElement.getName().equalsIgnoreCase(lowercaseCloseTagName)) {
         matchesOpenElement = true;
         }
         }
         if (matchesOpenElement) {
         nonCloseElements.remove(lastOpenElement);
         }
         }

         if (lastOpenElement.getParentNode() != null) {
         ((FuzzyXMLElementImpl) lastOpenElement.getParentNode()).appendChildWithNoCheck(lastOpenElement);
         FuzzyXMLNode[] nodes = lastOpenElement.getChildren();
         for (int i = 0; i < nodes.length; i++) {
         ((AbstractFuzzyXMLNode) nodes[i]).setParentNode(lastOpenElement.getParentNode());
         lastOpenElement.removeChild(nodes[i]);
         ((FuzzyXMLElementImpl) lastOpenElement.getParentNode()).appendChildWithNoCheck(nodes[i]);
         }
         }
         else {
         //System.out.println(tagName + "の開始タグが見つかりません。");
         fireErrorEvent(offset, end - offset, Messages.getMessage("error.noStartTag", tagName), null);
         }
         if (matchesOpenElement) {
         handleCloseTag(offset, end, text);
         }
         //			stack.push(element);
         return;
         */
      }
    }

    if (lastOpenElement != null) {
      // 空タグの場合は空のテキストノードを追加しておく
      if (lastOpenElement.getChildren().length == 0) {
        lastOpenElement.appendChild(new FuzzyXMLTextImpl(getParent(), "", offset, 0));
      }
      lastOpenElement.setLength(end - lastOpenElement.getOffset());
      if (closeTagMatches) {
        lastOpenElement.setCloseTagOffset(offset);
        lastOpenElement.setCloseTagLength(end - offset);
        lastOpenElement.setCloseNameOffset(text.indexOf(tagName));
      }
      nonCloseElements.remove(lastOpenElement);
      if (lastOpenElement.getParentNode() == null) {
        roots.add(lastOpenElement);
        for (FuzzyXMLElement error : nonCloseElements) {
          //System.out.println(error.getName() + "は閉じていません。");
          if (showMismatchError) {
            fireErrorEvent(error.getOffset(), error.getLength(), Messages.getMessage("error.noCloseTag", error.getName()), error);
          }
        }
      }
      else {
        ((FuzzyXMLElementImpl) lastOpenElement.getParentNode()).appendChildWithNoCheck(lastOpenElement);
      }
    }
  }

  /** 空タグを処理します。 */
  private void handleEmptyTag(int offset, int end) {
    TagInfo info = parseTagContents(originalSource.substring(offset + 1, end - 1));
    FuzzyXMLNode parent = getParent();
    FuzzyXMLElementImpl element = new FuzzyXMLElementImpl(parent, info.name, offset, end - offset, info.nameOffset);
    if (parent == null) {
      roots.add(element);
    }
    else {
      ((FuzzyXMLElement) parent).appendChild(element);
    }
    // 属性を追加
    AttrInfo[] attrs = info.getAttrs();
    for (int i = 0; i < attrs.length; i++) {
      FuzzyXMLAttributeImpl attr = new FuzzyXMLAttributeImpl(element, attrs[i].name, attrs[i].value, attrs[i].offset + offset, attrs[i].end - attrs[i].offset + 1, attrs[i].valueOffset);
      attr.setQuoteCharacter(attrs[i].quote);
      if (attrs[i].value.indexOf('"') >= 0 || attrs[i].value.indexOf('\'') >= 0 || attrs[i].value.indexOf('<') >= 0 || attrs[i].value.indexOf('>') >= 0 || attrs[i].value.indexOf('&') >= 0) {
        attr.setEscape(false);
      }
      element.appendChild(attr);
    }

    
    for (FuzzyXMLAttribute attr : element.getAttributes()) {
      stack.push(element);
      _parse(attr.getValue(), element.getOffset() + attr.getValueDataOffset() + 1, true);
      FuzzyXMLNode poppedNode = stack.pop();
      if (poppedNode != element) {
        stack.push(poppedNode);
      }
    }
  }

  /** コメントを処理します。 */
  private void handleComment(int offset, int end, String text) {
    if (getParent() != null) {
      text = text.replaceFirst("<!--", "");
      text = text.replaceFirst("-->", "");
      FuzzyXMLCommentImpl comment = new FuzzyXMLCommentImpl(getParent(), text, offset, end - offset);
      ((FuzzyXMLElement) getParent()).appendChild(comment);
    }
  }

  /** 開始タグを処理します。 */
  private void handleStartTag(int offset, int end) {
    TagInfo info = parseTagContents(originalSource.substring(offset + 1, end - 1));
    FuzzyXMLElementImpl element = new FuzzyXMLElementImpl(getParent(), info.name, offset, end - offset, info.nameOffset);
    // 属性を追加
    AttrInfo[] attrs = info.getAttrs();
    for (int i = 0; i < attrs.length; i++) {
      //			// 名前空間のサポート
      //			if(attrs[i].name.startsWith("xmlns")){
      //				String uri    = attrs[i].value;
      //				String prefix = null;
      //				String[] dim = attrs[i].name.split(":");
      //				if(dim.length > 1){
      //					prefix = dim[1];
      //				}
      //				element.addNamespaceURI(prefix,uri);
      //			}
      FuzzyXMLAttributeImpl attr = new FuzzyXMLAttributeImpl(element, attrs[i].name, attrs[i].value, attrs[i].offset + offset, attrs[i].end - attrs[i].offset + 1, attrs[i].valueOffset);
      attr.setQuoteCharacter(attrs[i].quote);
      if (attrs[i].value.indexOf('"') >= 0 || attrs[i].value.indexOf('\'') >= 0 || attrs[i].value.indexOf('<') >= 0 || attrs[i].value.indexOf('>') >= 0 || attrs[i].value.indexOf('&') >= 0) {
        attr.setEscape(false);
      }
      element.appendChild(attr);
    }
    stack.push(element);
    nonCloseElements.add(element);

    for (FuzzyXMLAttribute attr : element.getAttributes()) {
      stack.push(element);
      _parse(attr.getValue(), element.getOffset() + attr.getValueDataOffset() + 1, true);
      FuzzyXMLNode poppedNode = stack.pop();
      if (poppedNode != element) {
        stack.push(poppedNode);
      }
    }
  }

  /** スタックの最後の要素を取得します(スタックからは削除しません)。 */
  private FuzzyXMLNode getParent() {
    if (stack.size() == 0) {
      return null;
    }
    return stack.get(stack.size() - 1);
  }

  /** タグ部分をパースします。 */
  private TagInfo parseTagContents(String text) {
    // トリム
    Range trimmedRange = Range.trimmedRange(text);
    text = trimmedRange.trim(text);
    // 閉じタグだったら最後のスラッシュを削除
    if (text.endsWith("/")) {
      text = text.substring(0, text.length() - 1);
    }
    // 最初のスペースまでがタグ名
    TagInfo info = new TagInfo();
    if (FuzzyXMLUtil.getSpaceIndex(text) != -1) {
      info.name = text.substring(0, FuzzyXMLUtil.getSpaceIndex(text)).trim();
      info.nameOffset = trimmedRange.getOffset();
      parseAttributeContents(info, text);
    }
    else {
      info.name = text;
    }
    return info;
  }

  /** アトリビュート部分をパースします。 */
  private void parseAttributeContents(TagInfo info, String text) {

    int state = 0;
    StringBuffer sb = new StringBuffer();
    String name = null;
    char quote = 0;
    int start = -1;
    int valueOffset = -1;
    boolean escape = false;

    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (state == 0 && FuzzyXMLUtil.isWhitespace(c)) {
        state = 1;
      }
      else if (state == 1 && !FuzzyXMLUtil.isWhitespace(c)) {
        if (start == -1) {
          start = i;
        }
        state = 2;
        sb.append(c);
      }
      else if (state == 2) {
        if (c == '=') {
          state = 3;
          name = sb.toString().trim();
          sb.setLength(0);
          valueOffset = -1;
        }
        else {
          sb.append(c);
        }
      }
      else if (state == 3 && !FuzzyXMLUtil.isWhitespace(c)) {
        if (valueOffset == -1) {
          valueOffset = i;
        }
        if (c == '\'' || c == '\"') {
          quote = c;
        }
        else {
          quote = 0;
          sb.append(c);
        }
        state = 4;
      }
      else if (state == 4) {
        if (c == quote && escape == true) {
          sb.append(c);
          escape = false;
        }
        else if (c == quote || (quote == 0 && FuzzyXMLUtil.isWhitespace(c))) {
          // add an attribute
          AttrInfo attr = new AttrInfo();
          attr.name = name;
          attr.value = FuzzyXMLUtil.decode(sb.toString(), isHTML);
          attr.valueOffset = valueOffset;
          attr.offset = start;
          attr.end = i + 1;
          attr.quote = quote;
          info.addAttr(attr);

          // reset
          sb.setLength(0);
          state = 1;
          start = -1;
        }
        else if (c == '\\') {
          if (escape == true) {
            sb.append(c);
          }
          else {
            // MS: I took out escaping .. This is potentially a really sketchy thing to do, but it
            // was breaking attributes like   numberformat = "\$#,##0.00"
            sb.append(c);
            escape = true;
          }
        }
        else {
          sb.append(c);
          escape = false;
        }
      }
    }
    if (state == 4 && quote == 0) {
      AttrInfo attr = new AttrInfo();
      attr.name = name;
      attr.value = FuzzyXMLUtil.decode(sb.toString(), isHTML);
      attr.valueOffset = valueOffset;
      attr.offset = start;
      attr.end = text.length();
      attr.quote = quote;
      info.addAttr(attr);
    }
    //		Matcher matcher = attr.matcher(text);
    //		while(matcher.find()){
    //			AttrInfo attr = new AttrInfo();
    //			attr.name   = matcher.group(1);
    //			attr.value  = FuzzyXMLUtil.decode(matcher.group(3));
    //			attr.offset = matcher.start();
    //			attr.end    = matcher.end();
    //			info.addAttr(attr);
    //		}
  }

  private class TagInfo {
    private String name;
    private int nameOffset;
    private ArrayList<AttrInfo> attrs = new ArrayList<AttrInfo>();

    public void addAttr(AttrInfo attr) {
      // 同じものがあっても追加しない
      AttrInfo[] info = getAttrs();
      for (int i = 0; i < info.length; i++) {
        if (info[i].name.equals(attr.name)) {
          return;
        }
      }
      attrs.add(attr);
    }

    public AttrInfo[] getAttrs() {
      return attrs.toArray(new AttrInfo[attrs.size()]);
    }
  }

  private class AttrInfo {
    private String name;
    private String value;
    private int offset;
    private int valueOffset;
    private int end;
    private char quote;
  }

  public static class Range {
    private int _offset;
    private int _length;

    public Range() {
    }

    public int getOffset() {
      return _offset;
    }

    public int getLength() {
      return _length;
    }
    
    public String trim(String str) {
      return str.substring(_offset, _offset + _length);
    }

    public static Range trimmedRange(String str) {
      int i = 0;
      int length = str.length();
      Range r = new Range();
      for (i = 0; i < length && str.charAt(i) <= ' '; i++) {
        // DO NOTHING
      }
      r._offset = i;

      for (i = length - 1; i > r._offset && str.charAt(i) <= ' '; i--) {
        // DO NOTHING
      }
      r._length = (i - r._offset + 1);
      return r;
    }
  }

}
