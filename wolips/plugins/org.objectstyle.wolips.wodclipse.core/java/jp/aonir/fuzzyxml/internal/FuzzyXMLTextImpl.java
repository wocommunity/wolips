package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;

public class FuzzyXMLTextImpl extends AbstractFuzzyXMLNode implements FuzzyXMLText {
    
    private String value;
    private boolean escape = true;
    
    public FuzzyXMLTextImpl(String value) {
        this(null,value,-1,-1);
    }
    
    public FuzzyXMLTextImpl(FuzzyXMLNode parent,String value,int offset,int length) {
        super(parent,offset,length);
        this.value  = value;
    }

    public String getValue() {
    	if(getDocument()==null){
            return FuzzyXMLUtil.decode(value, false);
    	}
        return FuzzyXMLUtil.decode(value, getDocument().isHTML());
    }
    
    public String toXMLString(){
    	if(escape){
    		boolean isHTML = false;
    		if(getDocument()!=null){
    			isHTML = getDocument().isHTML();
    		}
    		return FuzzyXMLUtil.escape(getValue(), isHTML);
    	} else {
    		return getValue();
    	}
    }
    
    public String toString(){
        return "text: " + getValue();
    }
    
    public void setEscape(boolean escape){
    	this.escape = escape;
    }
    
    public boolean isEscape(){
    	return this.escape;
    }

}
