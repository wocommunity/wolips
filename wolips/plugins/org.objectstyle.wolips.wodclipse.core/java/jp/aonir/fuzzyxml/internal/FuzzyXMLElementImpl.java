package jp.aonir.fuzzyxml.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLException;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.FuzzyXMLText;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class FuzzyXMLElementImpl extends AbstractFuzzyXMLNode implements FuzzyXMLElement {

  private List<FuzzyXMLNode> _children = new ArrayList<FuzzyXMLNode>();
  private List<FuzzyXMLAttribute> _attributes = new ArrayList<FuzzyXMLAttribute>();
  private String _name;
  private int _nameOffset;

  private int _openTagLength;
  private int _closeTagOffset;
  private int _closeTagLength;
  private int _closeNameOffset;

  //	private HashMap namespace = new HashMap();

  public FuzzyXMLElementImpl(String name) {
    this(null, name, -1, -1, -1);
  }

  public FuzzyXMLElementImpl(FuzzyXMLNode parent, String name, int offset, int length, int nameOffset) {
    super(parent, offset, length);
    this._name = name;
    _nameOffset = nameOffset;
    _closeTagOffset = -1;
    _closeNameOffset = -1;
    _openTagLength = length - 2;
  }

  public int getOpenTagLength() {
    return _openTagLength;
  }

  public int getNameOffset() {
    return _nameOffset;
  }

  public int getNameLength() {
    return _name != null ? _name.length() : 0;
  }

  public boolean hasCloseTag() {
    return _closeTagOffset != -1 && _closeTagLength > 0;
  }

  public void setCloseTagOffset(int closeTagOffset) {
    _closeTagOffset = closeTagOffset;
  }

  public int getCloseTagOffset() {
    return _closeTagOffset;
  }

  public void setCloseTagLength(int closeTagLength) {
    _closeTagLength = closeTagLength;
  }

  public int getCloseTagLength() {
    return _closeTagLength;
  }

  public void setCloseNameOffset(int closeNameOffset) {
    _closeNameOffset = closeNameOffset;
  }

  public int getCloseNameOffset() {
    return _closeNameOffset;
  }

  public int getCloseNameLength() {
    return getNameLength();
  }

  public String getName() {
    return _name;
  }

  /**
   * XMLの断片テキストから子ノード群を追加します。
   * <p>
   * 通常の<code>appendChild()</code>で子ノードを追加した場合、
   * リスナには<code>FuzzyXMLNode#toXMLString()</code>の結果が新しいテキストとして通知されますが、
   * このメソッドを用いて子ノードを追加した場合、引数で渡したテキストが新しいテキストとして渡されます。
   * 不正なXMLをパースし、元のテキスト情報を保持する必要がある場合に使用してください。
   * </p>
   * @param text 追加する子要素を含んだXMLの断片。
   */
  public void appendChildrenFromText(String text, boolean wo54) {
    if (text.length() == 0) {
      return;
    }
    // 一度エレメントを挿入してオフセットを取得
    FuzzyXMLElement test = new FuzzyXMLElementImpl("test");
    appendChild(test);
    int offset = test.getOffset();
    // オフセットを取得したらすぐ削除
    removeChild(test);

    String parseText = "<root>" + text + "</root>";

    FuzzyXMLElement root = new FuzzyXMLParser(wo54).parse(parseText).getDocumentElement();
    ((AbstractFuzzyXMLNode) root).appendOffset(root, 0, -6);
    ((AbstractFuzzyXMLNode) root).appendOffset(root, 0, offset);
    FuzzyXMLNode[] nodes = ((FuzzyXMLElement) root.getChildren()[0]).getChildren();

    appendOffset(this, offset, text.length());

    for (int i = 0; i < nodes.length; i++) {
      appendChild(nodes[i], false, false);
    }

    fireModifyEvent(text, offset, 0);
  }

  /**
   * このエレメントに子ノードを追加します。
   * 以下の場合はノードを追加することはできません（FuzzyXMLExceptionが発生します）。
   * 
   * <ul>
   *   <li>エレメントが他のツリーに属している場合（親エレメントからremoveすれば追加できます）</li>
   *   <li>エレメントが子ノードを持っている場合</li>
   * </ul>
   * 
   * @param node 追加するノード。
   *   エレメントの場合、子を持たないエレメントを指定してください。
   *   すでに子要素を構築済みのエレメントを渡すと内部で保持している位置情報が同期されません。
   *   
   * @exception jp.aonir.fuzzyxml.FuzzyXMLException ノードを追加できない場合
   */
  public void appendChild(FuzzyXMLNode node) {
    appendChild(node, true, true);
  }

  /**
   * パース時に<code>appendChild()</code>メソッドの代わりに使用します。
   */
  public void appendChildWithNoCheck(FuzzyXMLNode node) {
    appendChild(node, true, false);
  }

  /**
   * このエレメントに子ノードを追加。
   * 
   * @param node 追加するノード。
   *   エレメントの場合、子を持たないエレメントを指定してください。
   *   すでに子要素を構築済みのエレメントを渡すと内部で保持している位置情報が同期されません。
   * @param fireEvent イベントを発火するかどうか。
   *   falseを指定した場合、ノードが持っている位置情報の同期処理も行いません。
   * @param check 追加するノードの検証を行うかどうか。
   *   trueを指定した場合、以下のに該当する場合FuzzyXMLExceptionをthrowします。
   *   <ul>
   *     <li>ノードが他のツリーに属している場合</li>
   *     <li>エレメントがすでに子供を持っている場合</li>
   *   </ul>
   *   パース時など、検証を行いたくない場合はfalseを指定します。
   *   
   * @exception jp.aonir.fuzzyxml.FuzzyXMLException ノードを追加できない場合
   */
  private void appendChild(FuzzyXMLNode node, boolean fireEvent, boolean check) {
    if (check) {
      if (((AbstractFuzzyXMLNode) node).getDocument() != null) {
        throw new FuzzyXMLException("Appended node already has a parent.");
      }

      if (node instanceof FuzzyXMLElement) {
        if (((FuzzyXMLElement) node).getChildren().length != 0) {
          throw new FuzzyXMLException("Appended node has chidlren.");
        }
      }
    }

    AbstractFuzzyXMLNode nodeImpl = (AbstractFuzzyXMLNode) node;
    nodeImpl.setParentNode(this);
    nodeImpl.setDocument(getDocument());
    if (node instanceof FuzzyXMLAttribute) {
      setAttribute((FuzzyXMLAttribute) node);
    }
    else {
      if (_children.contains(node)) {
        return;
      }
      if (getDocument() == null) {
        _children.add(node);
        return;
      }
      // 追加するノードの位置(最後)を計算
      FuzzyXMLNode[] nodes = getChildren();
      int offset = 0;
      if (nodes.length == 0) {
        int length = getLength();
        FuzzyXMLAttribute[] attrs = getAttributes();
        offset = getOffset() + getName().length();
        for (int i = 0; i < attrs.length; i++) {
          offset = offset + attrs[i].toXMLString(new RenderContext(getDocument().isHTML())).length();
        }
        // ここ微妙？
        offset = offset + 2;

        nodeImpl.setOffset(offset);
        if (fireEvent) {
          nodeImpl.setLength(node.toXMLString(new RenderContext(getDocument().isHTML())).length());
        }

        _children.add(node);
        String xml = toXMLString(new RenderContext(getDocument().isHTML()));
        _children.remove(node);

        // イベントの発火
        if (fireEvent) {
          fireModifyEvent(xml, getOffset(), getLength());
          // 位置情報の更新
          appendOffset(this, offset, xml.length() - length);
        }

        _children.add(node);

      }
      else {
        for (int i = 0; i < nodes.length; i++) {
          offset = nodes[i].getOffset() + nodes[i].getLength();
        }
        // イベントの発火
        if (fireEvent) {
          fireModifyEvent(nodeImpl.toXMLString(new RenderContext(getDocument().isHTML())), offset, 0);
          // 位置情報の更新
          appendOffset(this, offset, node.toXMLString(new RenderContext(getDocument().isHTML())).length());
        }

        // 最後に追加
        nodeImpl.setOffset(offset);
        if (fireEvent) {
          nodeImpl.setLength(node.toXMLString(new RenderContext(getDocument().isHTML())).length());
        }

        _children.add(node);
      }
    }
  }

  public FuzzyXMLAttribute[] getAttributes() {
    return _attributes.toArray(new FuzzyXMLAttribute[_attributes.size()]);
  }

  public FuzzyXMLNode[] getChildren() {
    // アトリビュートは含まない？
    return _children.toArray(new FuzzyXMLNode[_children.size()]);
  }

  public boolean hasChildren() {
    return _children.size() > 0;
  }

  public boolean isEmpty() {
    boolean empty = !hasChildren();
    if (!empty) {
      empty = true;
      for (FuzzyXMLNode child : _children) {
        if (child instanceof FuzzyXMLText) {
          FuzzyXMLText text = (FuzzyXMLText) child;
          String textValue = text.getValue();
          if (textValue != null && textValue.trim().length() > 0) {
            empty = false;
            break;
          }
        }
        else {
          empty = false;
          break;
        }
      }
    }
    return empty;
  }

  public void insertAfter(FuzzyXMLNode newChild, FuzzyXMLNode refChild) {
    // アトリビュートの場合はなにもしない
    if (newChild instanceof FuzzyXMLAttribute || refChild instanceof FuzzyXMLAttribute) {
      return;
    }
    // 挿入する位置を探す
    FuzzyXMLNode[] children = getChildren();
    FuzzyXMLNode targetNode = null;
    boolean flag = false;
    for (int i = 0; i < children.length; i++) {
      if (flag) {
        targetNode = children[i];
      }
      if (children[i] == refChild) {
        flag = true;
      }
    }
    if (targetNode == null && flag) {
      appendChild(newChild);
    }
    else {
      insertBefore(newChild, targetNode);
    }
  }

  public void insertBefore(FuzzyXMLNode newChild, FuzzyXMLNode refChild) {
    // アトリビュートの場合はなにもしない
    if (newChild instanceof FuzzyXMLAttribute || refChild instanceof FuzzyXMLAttribute) {
      return;
    }
    // 挿入する位置を探す
    FuzzyXMLNode target = null;
    int index = -1;
    FuzzyXMLNode[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i] == refChild) {
        target = children[i];
        index = i;
        break;
      }
    }
    if (target == null) {
      return;
    }
    int offset = target.getOffset();
    // イベントの発火
    fireModifyEvent(newChild.toXMLString(new RenderContext(getDocument().isHTML())), offset, 0);

    AbstractFuzzyXMLNode nodeImpl = (AbstractFuzzyXMLNode) newChild;
    nodeImpl.setParentNode(this);
    nodeImpl.setDocument(getDocument());
    nodeImpl.setOffset(offset);
    nodeImpl.setLength(newChild.toXMLString(new RenderContext(getDocument().isHTML())).length());

    // 位置情報の更新
    appendOffset(this, offset, nodeImpl.toXMLString(new RenderContext(getDocument().isHTML())).length());

    // 最後に追加
    this._children.add(index, nodeImpl);
  }

  public void replaceChild(FuzzyXMLNode newChild, FuzzyXMLNode refChild) {
    // アトリビュートの場合はなにもしない
    if (newChild instanceof FuzzyXMLAttribute || refChild instanceof FuzzyXMLAttribute) {
      return;
    }
    // 置換するノードのインデックスを取得
    int index = -1;
    for (int i = 0; i < _children.size(); i++) {
      if (refChild == _children.get(i)) {
        index = i;
        break;
      }
    }
    // ノードが見つからなかったらなにもしない
    if (index == -1) {
      return;
    }
    _children.remove(index);

    AbstractFuzzyXMLNode nodeImpl = (AbstractFuzzyXMLNode) newChild;
    nodeImpl.setParentNode(this);
    nodeImpl.setDocument(getDocument());
    nodeImpl.setOffset(refChild.getOffset());
    nodeImpl.setLength(newChild.toXMLString(new RenderContext(getDocument().isHTML())).length());

    // イベントの発火
    fireModifyEvent(newChild.toXMLString(new RenderContext(getDocument().isHTML())), refChild.getOffset(), refChild.getLength());
    // 位置情報の更新
    appendOffset(this, refChild.getOffset(), newChild.getLength() - refChild.getLength());

    _children.add(index, newChild);
  }

  public void removeChild(FuzzyXMLNode oldChild) {
    if (oldChild instanceof FuzzyXMLAttribute) {
      removeAttributeNode((FuzzyXMLAttribute) oldChild);
      return;
    }
    if (_children.contains(oldChild)) {
      // デタッチ
      ((AbstractFuzzyXMLNode) oldChild).setParentNode(null);
      ((AbstractFuzzyXMLNode) oldChild).setDocument(null);
      // リストから削除
      _children.remove(oldChild);
      // イベントの発火
      fireModifyEvent("", oldChild.getOffset(), oldChild.getLength());
      // 位置情報の更新
      appendOffset(this, oldChild.getOffset(), oldChild.getLength() * -1);
    }
  }

  public void setAttribute(FuzzyXMLAttribute attr) {
    FuzzyXMLAttribute attrNode = getAttributeNode(attr.getName());
    if (attrNode == null) {
      if (_attributes.contains(attr)) {
        return;
      }
      if (getDocument() == null) {
        _attributes.add(attr);
        return;
      }
      FuzzyXMLAttributeImpl attrImpl = (FuzzyXMLAttributeImpl) attr;
      attrImpl.setDocument(getDocument());
      attrImpl.setParentNode(this);
      // 追加するアトリビュートの位置を検索
      FuzzyXMLAttribute[] attrs = getAttributes();
      int offset = getOffset() + getName().length() + 1;
      for (int i = 0; i < attrs.length; i++) {
        offset = offset + attrs[i].toXMLString(new RenderContext(getDocument().isHTML())).length();
      }
      // 更新イベントを発火
      fireModifyEvent(attr.toXMLString(new RenderContext(getDocument().isHTML())), offset, 0);
      // 位置情報の更新
      appendOffset(this, offset, attr.toXMLString(new RenderContext(getDocument().isHTML())).length());
      // 最後に追加
      attrImpl.setOffset(offset);
      attrImpl.setLength(attrImpl.toXMLString(new RenderContext(getDocument().isHTML())).length());
      _attributes.add(attrImpl);
    }
    else {
      // この場合はアトリビュートのsetValueメソッド内でイベント発火
      FuzzyXMLAttributeImpl attrImpl = (FuzzyXMLAttributeImpl) attrNode;
      attrImpl.setValue(attr.getValue());
    }
  }

  public FuzzyXMLAttribute getAttributeNode(String name) {
    FuzzyXMLAttribute[] attrs = getAttributes();
    for (int i = 0; i < attrs.length; i++) {
      if (attrs[i].getName().equalsIgnoreCase(name)) {
        return attrs[i];
      }
    }
    return null;
  }

  public boolean hasAttribute(String name) {
    return getAttributeNode(name) != null;
  }

  public void removeAttributeNode(FuzzyXMLAttribute attr) {
    if (_attributes.contains(attr)) {
      // デタッチ
      ((AbstractFuzzyXMLNode) attr).setParentNode(null);
      ((AbstractFuzzyXMLNode) attr).setDocument(null);
      // リストから削除
      _attributes.remove(attr);
      // イベントの発火
      fireModifyEvent("", attr.getOffset(), attr.getLength());
      // 位置情報の更新
      appendOffset(this, attr.getOffset(), attr.getLength() * -1);
    }
  }

  public String getValue() {
    StringBuffer sb = new StringBuffer();
    FuzzyXMLNode[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i] instanceof FuzzyXMLText) {
        sb.append(((FuzzyXMLText) children[i]).getValue());
      }
    }
    return sb.toString();
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    boolean isHTML = renderContext.isHtml();

    boolean renderSurroundingTags = true;
    RenderDelegate delegate = renderContext.getDelegate();
    if (delegate != null) {
      renderSurroundingTags = delegate.beforeOpenTag(this, renderContext, xmlBuffer);
    }
    try {
      boolean shouldFormat = renderContext.shouldFormat();

      String tagName = FuzzyXMLUtil.escape(getName(), isHTML);
      if (renderContext.isLowercaseTags() && FuzzyXMLUtil.isAllUppercase(tagName)) {
        tagName = tagName.toLowerCase();
      }

      if (renderSurroundingTags) {
        if (shouldFormat) {
          renderContext.appendIndent(xmlBuffer);
        }
        xmlBuffer.append("<").append(tagName);
        FuzzyXMLAttribute[] attrs = getAttributes();
        for (int i = 0; i < attrs.length; i++) {
          attrs[i].toXMLString(renderContext, xmlBuffer);
        }
      }

      boolean forbiddenSelfClosing = ("a".equalsIgnoreCase(tagName) || "div".equalsIgnoreCase(tagName) || "script".equalsIgnoreCase(tagName));
      FuzzyXMLNode[] children = getChildren();
      if ((children.length == 0 || (children.length == 1 && children[0].getLength() == 0)) 
          && !forbiddenSelfClosing) {
        if (renderSurroundingTags) {
          if (renderContext.isSpaceInEmptyTags()) {
            xmlBuffer.append(" ");
          }
          xmlBuffer.append("/>");
        }
      }
      else {
        if (renderSurroundingTags) {
          xmlBuffer.append(">");
        }

        boolean isScript = "script".equalsIgnoreCase(getName());
        if (isScript) {
          shouldFormat = false;
          renderContext.setShouldFormat(false);
        }
        Set<FuzzyXMLText> hiddenTextNodes = new HashSet<FuzzyXMLText>();
        int textBlocks = 0;
        boolean newlines = false;
        if (shouldFormat) {
          if (renderContext.isShowNewlines()) {
            for (int i = 0; i < children.length; i++) {
              if (children[i] instanceof FuzzyXMLElement) {
                newlines = true;
              }
              else if (children[i] instanceof FuzzyXMLText) {
                FuzzyXMLText text = (FuzzyXMLText) children[i];
                if (renderContext.isTrim()) {
                  String value = text.getValue().trim();
                  if (value.length() == 0) {
                    hiddenTextNodes.add(text);
                  }
                  else {
                    textBlocks++;
                    if (value.indexOf('\n') >= 0) {
                      textBlocks++;
                    }
                  }
                }
              }
            }
            if (textBlocks > 1) {
              newlines = true;
            }
          }

          if (renderContext.isShowNewlines() && newlines) {
            xmlBuffer.append("\n");
          }
          renderContext.indent();
        }

        if (delegate != null) {
          delegate.afterOpenTag(this, renderContext, xmlBuffer);
        }

        boolean lastNodeWasText = false;
        for (int i = 0; i < children.length; i++) {
          if (shouldFormat && renderContext.isShowNewlines() && lastNodeWasText && children[i] instanceof FuzzyXMLElement) {
            xmlBuffer.append("\n");
          }

          boolean isText = children[i] instanceof FuzzyXMLText;
          boolean wasTextEscaped = false;
          boolean oldTrim = renderContext.isTrim();
          if (shouldFormat && isText) {
            FuzzyXMLText text = (FuzzyXMLText) children[i];
            wasTextEscaped = text.isEscape();
            if (!hiddenTextNodes.contains(children[i])) {
              if (!lastNodeWasText && newlines) {
                renderContext.appendIndent(xmlBuffer);
              }
              lastNodeWasText = true;
            }
          }
          else {
            lastNodeWasText = false;
          }

          if (isText && isScript) {
            ((FuzzyXMLText) children[i]).setEscape(false);
            renderContext.setTrim(false);
          }

          if (delegate == null || delegate.renderNode(children[i], renderContext, xmlBuffer)) {
            children[i].toXMLString(renderContext, xmlBuffer);
          }

          if (isText && isScript) {
            ((FuzzyXMLText) children[i]).setEscape(wasTextEscaped);
            renderContext.setTrim(oldTrim);
          }
        }

        if (shouldFormat && renderContext.isShowNewlines() && lastNodeWasText && textBlocks > 1) {
          xmlBuffer.append("\n");
        }

        if (delegate != null) {
          delegate.beforeCloseTag(this, renderContext, xmlBuffer);
        }

        if (shouldFormat) {
          renderContext.outdent();
        }
        if (renderSurroundingTags) {
          if (shouldFormat) {
            if (newlines || (lastNodeWasText && textBlocks > 1)) {
              renderContext.appendIndent(xmlBuffer);
            }
          }

          xmlBuffer.append("</").append(tagName).append(">");
        }

        if (isScript) {
          shouldFormat = true;
          renderContext.setShouldFormat(true);
        }
      }

      if (shouldFormat && renderContext.isShowNewlines()) {
        xmlBuffer.append("\n");
      }
    }
    finally {
      if (delegate != null) {
        delegate.afterCloseTag(this, renderContext, xmlBuffer);
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FuzzyXMLElement) {
      FuzzyXMLElement element = (FuzzyXMLElement) obj;

      // タグの名前が違ったらfalse
      if (!element.getName().equals(getName())) {
        return false;
      }

      // 親が両方ともnullだったらtrue
      FuzzyXMLNode parent = element.getParentNode();
      if (parent == null) {
        if (getParentNode() == null) {
          return true;
        }
        return false;
      }

      // 開始オフセットが同じだったらtrue
      if (element.getOffset() == getOffset()) {
        return true;
      }

    }
    return false;
  }

  public String getAttributeValue(String name) {
    FuzzyXMLAttribute attr = getAttributeNode(name);
    if (attr != null) {
      return attr.getValue();
    }
    return null;
  }

  public void setAttribute(String name, String value) {
    FuzzyXMLAttribute attr = new FuzzyXMLAttributeImpl(name, value);
    setAttribute(attr);
  }

  public void removeAttribute(String name) {
    FuzzyXMLAttribute attr = getAttributeNode(name);
    if (attr != null) {
      removeAttributeNode(attr);
    }
  }

  @Override
  public void setDocument(FuzzyXMLDocumentImpl doc) {
    super.setDocument(doc);
    FuzzyXMLNode[] nodes = getChildren();
    for (int i = 0; i < nodes.length; i++) {
      ((AbstractFuzzyXMLNode) nodes[i]).setDocument(doc);
    }
    FuzzyXMLAttribute[] attrs = getAttributes();
    for (int i = 0; i < attrs.length; i++) {
      ((AbstractFuzzyXMLNode) attrs[i]).setDocument(doc);
    }
  }

  @Override
  public String toString() {
    return "element: " + getName() + "; attributes = " + _attributes;
  }

  public void removeAllChildren() {
    FuzzyXMLNode[] children = getChildren();
    for (int i = 0; i < children.length; i++) {
      removeChild(children[i]);
    }
  }

  public Region getRegionAtOffset(int offset, IDocument doc, boolean regionForInsert) throws BadLocationException {
    Region region;
    int openTagOffset = getOffset();
    int openTagLength = getOpenTagLength() + 2;
    int openTagEndOffset = openTagOffset + openTagLength;
    if (hasCloseTag()) {
      int closeTagOffset = getCloseTagOffset();
      int closeTagEndOffset = closeTagOffset + getCloseTagLength();
      //if (modelOffset > openTagEndOffset && modelOffset < getCloseTagOffset()) {
      if (!regionForInsert) {
        region = new Region(openTagOffset, closeTagOffset - openTagOffset + getCloseTagLength() + 2);
      }
      else if ((offset >= openTagOffset && offset < openTagEndOffset) || (offset >= closeTagOffset && offset < closeTagEndOffset)) {
        if (doc != null) {
          IRegion lineRegion = doc.getLineInformationOfOffset(openTagEndOffset);
          int lineEndOffset = lineRegion.getOffset() + lineRegion.getLength();
          if (openTagEndOffset == lineEndOffset) {
            openTagEndOffset++;
            openTagLength++;
          }
        }
        region = new Region(openTagOffset, openTagLength);
      }
      else {
        region = new Region(offset, 0);
      }
    }
    else {
      region = new Region(getOffset(), getLength());
    }
    return region;
  }
}
