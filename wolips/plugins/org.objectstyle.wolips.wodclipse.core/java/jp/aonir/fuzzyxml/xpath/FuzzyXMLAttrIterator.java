package jp.aonir.fuzzyxml.xpath;

import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class FuzzyXMLAttrIterator implements NodeIterator {

  private NodePointer _parent;
  //    private QName name;
  private List<FuzzyXMLAttribute> _attributes;
  private int _position = 0;

  public FuzzyXMLAttrIterator(NodePointer parent, QName name) {
    this._parent = parent;
    //        this.name = name;
    if (parent.getNode() instanceof FuzzyXMLElement) {
      FuzzyXMLElement element = (FuzzyXMLElement) parent.getNode();

      String prefix = name.getPrefix();
      String lname = name.getName();
      if (prefix != null && !prefix.equals("")) {
        lname = prefix + ":" + lname;
      }
      if (!lname.equals("*")) {
        _attributes = new ArrayList<FuzzyXMLAttribute>();
        FuzzyXMLAttribute[] allAttributes = element.getAttributes();
        for (int i = 0; i < allAttributes.length; i++) {
          if (allAttributes[i].getName().equals(lname)) {
            _attributes.add(allAttributes[i]);
            break;
          }
        }
      }
      else {
        _attributes = new ArrayList<FuzzyXMLAttribute>();
        FuzzyXMLAttribute[] allAttributes = element.getAttributes();
        for (int i = 0; i < allAttributes.length; i++) {
          _attributes.add(allAttributes[i]);
        }
      }
    }
  }

  public int getPosition() {
    return _position;
  }

  public boolean setPosition(int position) {
    if (_attributes == null) {
      return false;
    }
    this._position = position;
    return position >= 1 && position <= _attributes.size();
  }

  public NodePointer getNodePointer() {
    if (_position == 0) {
      if (!setPosition(1)) {
        return null;
      }
      _position = 0;
    }
    int index = _position - 1;
    if (index < 0) {
      index = 0;
    }
    return new FuzzyXMLNodePointer(_parent, _attributes.get(index));
  }

}
