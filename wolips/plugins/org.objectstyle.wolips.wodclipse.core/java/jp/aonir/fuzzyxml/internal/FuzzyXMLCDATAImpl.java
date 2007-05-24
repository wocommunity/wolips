package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLCDATA;
import jp.aonir.fuzzyxml.FuzzyXMLNode;


public class FuzzyXMLCDATAImpl extends FuzzyXMLElementImpl implements FuzzyXMLCDATA {
    
    private String _value;
    
    public FuzzyXMLCDATAImpl(String value) {
        this(null,value,-1,-1);
    }

    public FuzzyXMLCDATAImpl(FuzzyXMLNode parent, String value, int offset, int length) {
        super(parent, "", offset, length, -1);
        this._value = value;
    }
    
    @Override
    public String getValue(){
        return this._value;
    }
    
    @Override
    public String toXMLString() {
        return "<![CDATA[" + FuzzyXMLUtil.escapeCDATA(getValue()) + "]]>";
    }
    
    @Override
    public String toString(){
        return "CDATA: " + getValue();
    }

}
