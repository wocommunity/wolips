package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLNode;


public class FuzzyXMLCommentImpl extends AbstractFuzzyXMLNode implements FuzzyXMLComment {
    
    private String _value;
    
    public FuzzyXMLCommentImpl(String value) {
        super();
        this._value = value;
    }
    
    public FuzzyXMLCommentImpl(FuzzyXMLNode parent,String value,int offset,int length) {
        super(parent,offset,length);
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
    
    public String toXMLString(){
		boolean isHTML = false;
		if(getDocument()!=null){
			isHTML = getDocument().isHTML();
		}
    	
        return "<!-- " + FuzzyXMLUtil.escape(getValue(), isHTML) + " -->";
    }
    
    @Override
    public String toString(){
        return "comment: " + getValue();
    }
}
