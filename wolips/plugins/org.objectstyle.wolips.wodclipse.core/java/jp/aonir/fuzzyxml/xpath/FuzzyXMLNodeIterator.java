package jp.aonir.fuzzyxml.xpath;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class FuzzyXMLNodeIterator implements NodeIterator {

  private NodePointer _parent;
  private NodeTest _nodeTest;

  private boolean _reverse;
  private int _position = 0;
  private int _index = 0;
  private List _children;
  private Object _child;

  /**
   * 
   */
  public FuzzyXMLNodeIterator(NodePointer parent, NodeTest nodeTest, boolean reverse, NodePointer startWith) {
    this._parent = parent;
    if (startWith != null) {
      this._child = startWith.getNode();
    }
    // TBD: optimize me for different node tests
    Object node = parent.getNode();
    if (node instanceof FuzzyXMLDocument) {
      this._children = Arrays.asList(new Object[] { ((FuzzyXMLDocument) node).getDocumentElement() });
    }
    else if (node instanceof FuzzyXMLElement) {
      this._children = Arrays.asList(((FuzzyXMLElement) node).getChildren());
    }
    else {
      this._children = Collections.EMPTY_LIST;
    }
    this._nodeTest = nodeTest;
    this._reverse = reverse;
  }

  public int getPosition() {
    return _position;
  }

  public boolean setPosition(int position) {
    while (this._position < position) {
      if (!next()) {
        return false;
      }
    }
    while (this._position > position) {
      if (!previous()) {
        return false;
      }
    }
    return true;
  }

  public NodePointer getNodePointer() {
    if (_child == null) {
      if (!setPosition(1)) {
        return null;
      }
      _position = 0;
    }

    return new FuzzyXMLNodePointer(_parent, _child);
  }

  /**
   * This is actually never invoked during the normal evaluation
   * of xpaths - an iterator is always going forward, never backwards.
   * So, this is implemented only for completeness and perhaps for
   * those who use these iterators outside of XPath evaluation.
   */
  private boolean previous() {
    _position--;
    if (!_reverse) {
      while (--_index >= 0) {
        _child = _children.get(_index);
        if (testChild()) {
          return true;
        }
      }
    }
    else {
      for (; _index < _children.size(); _index++) {
        _child = _children.get(_index);
        if (testChild()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean next() {
    _position++;
    if (!_reverse) {
      if (_position == 1) {
        _index = 0;
        if (_child != null) {
          _index = _children.indexOf(_child) + 1;
        }
      }
      else {
        _index++;
      }
      for (; _index < _children.size(); _index++) {
        _child = _children.get(_index);
        if (testChild()) {
          return true;
        }
      }
      return false;
    }
    if (_position == 1) {
      _index = _children.size() - 1;
      if (_child != null) {
        _index = _children.indexOf(_child) - 1;
      }
    }
    else {
      _index--;
    }
    for (; _index >= 0; _index--) {
      _child = _children.get(_index);
      if (testChild()) {
        return true;
      }
    }
    return false;
  }

  private boolean testChild() {
    return FuzzyXMLNodePointer.testNode(_parent, _child, _nodeTest);
  }

}
