package jp.aonir.fuzzyxml.xpath;

import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;


public class FuzzyXMLAttrIterator implements NodeIterator {

    private NodePointer parent;
//    private QName name;
    private List attributes;
    private int position = 0;
    
    public FuzzyXMLAttrIterator(NodePointer parent, QName name) {
        this.parent = parent;
//        this.name = name;
        if (parent.getNode() instanceof FuzzyXMLElement) {
            FuzzyXMLElement element = (FuzzyXMLElement) parent.getNode();
            
            String prefix = name.getPrefix();
            String lname  = name.getName();
            if(prefix!=null && !prefix.equals("")){
                lname = prefix + ":" + lname;
            }
            if (!lname.equals("*")) {
                attributes = new ArrayList();
                FuzzyXMLAttribute[] allAttributes = element.getAttributes();
                for (int i = 0; i < allAttributes.length; i++) {
                    if(allAttributes[i].getName().equals(lname)){
                        attributes.add(allAttributes[i]);
                        break;
                    }
                }
            } else {
                attributes = new ArrayList();
                FuzzyXMLAttribute[] allAttributes = element.getAttributes();
                for (int i = 0; i < allAttributes.length; i++) {
                    attributes.add(allAttributes[i]);
                }
            }
        }
    }

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
        if (attributes == null) {
            return false;
        }
        this.position = position;
        return position >= 1 && position <= attributes.size();
    }

    public NodePointer getNodePointer() {
        if (position == 0) {
            if (!setPosition(1)) {
                return null;
            }
            position = 0;
        }
        int index = position - 1;
        if (index < 0) {
            index = 0;
        }
        return new FuzzyXMLNodePointer(parent,(FuzzyXMLAttribute) attributes.get(index));
    }

}
