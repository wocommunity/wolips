package jp.aonir.fuzzyxml.xpath;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLCDATA;
import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class FuzzyXMLNodePointer extends NodePointer {

  private static final long serialVersionUID = 7543442288342946073L;

  private Object _node;

  public FuzzyXMLNodePointer(NodePointer parent, Object node) {
    super(null);
    this._node = node;
  }

  public FuzzyXMLNodePointer(Object node, Locale locale) {
    super(null, locale);
    this._node = node;
  }

  @Override
  public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
    return new FuzzyXMLNodeIterator(this, test, reverse, startWith);
  }

  @Override
  public NodeIterator attributeIterator(QName name) {
    return new FuzzyXMLAttrIterator(this, name);
  }

  @Override
  public boolean isLeaf() {
    if (_node instanceof FuzzyXMLElement) {
      return ((FuzzyXMLElement) _node).getChildren().length == 0;
    }
    else if (_node instanceof FuzzyXMLDocument) {
      return ((FuzzyXMLDocument) _node).getDocumentElement() == null;
    }
    return true;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public int getLength() {
    return 1;
  }

  @Override
  public QName getName() {
    //        String ns = null;
    String ln = null;
    if (_node instanceof FuzzyXMLElement) {
      ln = ((FuzzyXMLElement) _node).getName();
    }
    return new QName(null, ln);

  }

  @Override
  public Object getBaseValue() {
    return _node;
  }

  @Override
  public Object getImmediateNode() {
    return _node;
  }

  @Override
  public Object getValue() {
    if (_node instanceof FuzzyXMLElement) {
      return ((FuzzyXMLElement) _node).getValue().trim();
    }
    else if (_node instanceof FuzzyXMLComment) {
      return ((FuzzyXMLComment) _node).getValue();
    }
    else if (_node instanceof FuzzyXMLText) {
      return ((FuzzyXMLText) _node).getValue();
    }
    else if (_node instanceof FuzzyXMLCDATA) {
      return ((FuzzyXMLCDATA) _node).getValue();
    }
    else if (_node instanceof FuzzyXMLAttribute) {
      return ((FuzzyXMLAttribute) _node).getValue();
    }
    return null;
  }

  @Override
  public void setValue(Object value) {
  }

  @Override
  public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2) {
    Object node1 = pointer1.getBaseValue();
    Object node2 = pointer2.getBaseValue();
    if (node1 == node2) {
      return 0;
    }

    if ((node1 instanceof FuzzyXMLAttribute) && !(node2 instanceof FuzzyXMLAttribute)) {
      return -1;
    }
    else if (!(node1 instanceof FuzzyXMLAttribute) && (node2 instanceof FuzzyXMLAttribute)) {
      return 1;
    }
    else if ((node1 instanceof FuzzyXMLAttribute) && (node2 instanceof FuzzyXMLAttribute)) {
      List<FuzzyXMLAttribute> list = Arrays.asList(((FuzzyXMLElement) getNode()).getAttributes());
      int length = list.size();
      for (int i = 0; i < length; i++) {
        Object n = list.get(i);
        if (n == node1) {
          return -1;
        }
        else if (n == node2) {
          return 1;
        }
      }
      return 0; // Should not happen
    }

    if (!(_node instanceof FuzzyXMLElement)) {
      throw new RuntimeException("JXPath internal error: " + "compareChildNodes called for " + _node);
    }

    List<FuzzyXMLNode> children = Arrays.asList(((FuzzyXMLElement) _node).getChildren());
    int length = children.size();
    for (int i = 0; i < length; i++) {
      Object n = children.get(i);
      if (n == node1) {
        return -1;
      }
      else if (n == node2) {
        return 1;
      }
    }

    return 0;
  }

  public static boolean testNode(NodePointer pointer, Object node, NodeTest test) {
    if (test == null) {
      return true;
    }
    else if (test instanceof NodeNameTest) {
      if (!(node instanceof FuzzyXMLElement)) {
        return false;
      }

      NodeNameTest nodeNameTest = (NodeNameTest) test;
      QName testName = nodeNameTest.getNodeName();
      //            String namespaceURI = nodeNameTest.getNamespaceURI();
      boolean wildcard = nodeNameTest.isWildcard();
      String testPrefix = testName.getPrefix();
      if (wildcard && testPrefix == null) {
        return true;
      }

      if (wildcard || testName.getName().equals(((FuzzyXMLElement) node).getName())) {
        return true;
      }

    }
    else if (test instanceof NodeTypeTest) {
      switch (((NodeTypeTest) test).getNodeType()) {
      case Compiler.NODE_TYPE_NODE:
        return node instanceof FuzzyXMLElement;
      case Compiler.NODE_TYPE_TEXT:
        return (node instanceof FuzzyXMLText) || (node instanceof FuzzyXMLCDATA);
      case Compiler.NODE_TYPE_COMMENT:
        return node instanceof FuzzyXMLComment;
        //                case Compiler.NODE_TYPE_PI :
        //                    return node instanceof ProcessingInstruction;
      }
      return false;
    }
    //        else if (test instanceof ProcessingInstructionTest) {
    //            if (node instanceof ProcessingInstruction) {
    //                String testPI = ((ProcessingInstructionTest) test).getTarget();
    //                String nodePI = ((ProcessingInstruction) node).getTarget();
    //                return testPI.equals(nodePI);
    //            }
    //        }

    return false;
  }

}
