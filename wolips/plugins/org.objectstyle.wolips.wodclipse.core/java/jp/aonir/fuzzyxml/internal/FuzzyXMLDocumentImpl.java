package jp.aonir.fuzzyxml.internal;

import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLCDATA;
import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLProcessingInstruction;
import jp.aonir.fuzzyxml.FuzzyXMLText;
import jp.aonir.fuzzyxml.event.FuzzyXMLModifyEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLModifyListener;

public class FuzzyXMLDocumentImpl implements FuzzyXMLDocument {

  private boolean _isHTML = false;
  private FuzzyXMLElement _root;
  private FuzzyXMLDocType _docType;
  private List<FuzzyXMLModifyListener> _listeners = new ArrayList<FuzzyXMLModifyListener>();

  public FuzzyXMLDocumentImpl(FuzzyXMLElement root, FuzzyXMLDocType docType) {
    super();
    this._root = root;
    this._docType = docType;

    // ドキュメントオブジェクトをセット
    if (this._root != null) {
      ((FuzzyXMLElementImpl) this._root).setDocument(this);
      setDocument(this._root);
    }
    if (this._docType != null) {
      ((AbstractFuzzyXMLNode) this._docType).setDocument(this);
    }
  }

  public void setHTML(boolean isHTML) {
    this._isHTML = isHTML;
  }

  public boolean isHTML() {
    return this._isHTML;
  }

  private void setDocument(FuzzyXMLElement element) {
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      ((AbstractFuzzyXMLNode) children[i]).setDocument(this);
      if (children[i] instanceof FuzzyXMLElement) {
        setDocument((FuzzyXMLElement) children[i]);
      }
    }
    FuzzyXMLAttribute[] attr = element.getAttributes();
    for (int i = 0; i < attr.length; i++) {
      ((AbstractFuzzyXMLNode) attr[i]).setDocument(this);
    }
  }

  public FuzzyXMLComment createComment(String value) {
    return new FuzzyXMLCommentImpl(value);
  }

  public FuzzyXMLElement createElement(String name) {
    return new FuzzyXMLElementImpl(name);
  }

  public FuzzyXMLAttribute createAttribute(String namespace, String name) {
    return new FuzzyXMLAttributeImpl(namespace, name, null, null);
  }

  public FuzzyXMLText createText(String value) {
    return new FuzzyXMLTextImpl(value);
  }

  public FuzzyXMLCDATA createCDATASection(String value) {
    return new FuzzyXMLCDATAImpl(value);
  }

  public FuzzyXMLProcessingInstruction createProcessingInstruction(String name, String data) {
    return new FuzzyXMLProcessingInstructionImpl(name, data);
  }

  public FuzzyXMLElement getDocumentElement() {
    return _root;
  }

  public FuzzyXMLDocType getDocumentType() {
    return _docType;
  }

  public FuzzyXMLElement getElementByOffset(int offset) {
    if (_root == null) {
      return null;
    }
    List<FuzzyXMLElement> matches = new ArrayList<FuzzyXMLElement>();
    matches.add(_root);
    matchOffsetElement(_root, matches, offset);

    // 区間が最も短いものを選ぶ
    FuzzyXMLElement find = null;
    for (int i = 0; i < matches.size(); i++) {
      FuzzyXMLElement element = matches.get(i);
      if (find == null || find.getLength() >= element.getLength()) {
        find = element;
      }
    }
    return find;
  }

  private void matchOffsetElement(FuzzyXMLElement element, List<FuzzyXMLElement> matches, int offset) {
    FuzzyXMLNode[] nodes = element.getChildren();
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i] instanceof FuzzyXMLElement) {
        FuzzyXMLElement e = (FuzzyXMLElement) nodes[i];
        if (e.getOffset() <= offset && offset <= e.getOffset() + e.getLength()) {
          matches.add(e);
          matchOffsetElement(e, matches, offset);
        }
      }
    }
  }

  public void addModifyListener(FuzzyXMLModifyListener listener) {
    _listeners.add(listener);
  }

  public void removeModifyListener(FuzzyXMLModifyListener listener) {
    _listeners.remove(listener);
  }

  /**
   * 更新イベントを発火します。
   * 
   * @param newText
   * @param offset
   * @param length
   */
  public void fireModifyEvent(String newText, int offset, int length) {
    FuzzyXMLModifyEvent evt = new FuzzyXMLModifyEvent(newText, offset, length);
    for (int i = 0; i < _listeners.size(); i++) {
      FuzzyXMLModifyListener listener = _listeners.get(i);
      listener.modified(evt);
    }
  }

  /**
   * オフセット値を更新します。
   * 
   * @param offset
   * @param append
   */
  public void appendOffset(FuzzyXMLElement parent, int offset, int append) {
    appendOffsetForElement((FuzzyXMLElementImpl) _root, offset, append);
    appendLengthForParent((FuzzyXMLElementImpl) parent, append);
  }

  private void appendOffsetForElement(FuzzyXMLElementImpl element, int offset, int append) {
    FuzzyXMLNode[] children = element.getChildren();
    for (int i = 0; i < children.length; i++) {
      if (children[i].getOffset() >= offset) {
        ((AbstractFuzzyXMLNode) children[i]).setOffset(children[i].getOffset() + append);
      }
      if (children[i] instanceof FuzzyXMLElementImpl) {
        appendOffsetForElement((FuzzyXMLElementImpl) children[i], offset, append);
      }
    }
    FuzzyXMLAttribute[] attr = element.getAttributes();
    for (int i = 0; i < attr.length; i++) {
      if (attr[i].getOffset() >= offset) {
        ((AbstractFuzzyXMLNode) attr[i]).setOffset(attr[i].getOffset() + append);
      }
    }
  }

  private void appendLengthForParent(FuzzyXMLElementImpl parent, int appendLength) {
    parent.setLength(parent.getLength() + appendLength);
    if (parent.getParentNode() != null) {
      appendLengthForParent((FuzzyXMLElementImpl) parent.getParentNode(), appendLength);
    }
  }

}
