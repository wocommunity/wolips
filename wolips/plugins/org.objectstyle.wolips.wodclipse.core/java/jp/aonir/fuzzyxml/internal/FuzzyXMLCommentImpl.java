package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLNode;


public class FuzzyXMLCommentImpl extends AbstractFuzzyXMLNode implements FuzzyXMLComment {
    
    private String value;
    
    public FuzzyXMLCommentImpl(String value) {
        super();
        this.value = value;
    }
    
    public FuzzyXMLCommentImpl(FuzzyXMLNode parent,String value,int offset,int length) {
        super(parent,offset,length);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public String toXMLString(){
		boolean isHTML = false;
		if(getDocument()!=null){
			isHTML = getDocument().isHTML();
		}
    	
        return "<!-- " + FuzzyXMLUtil.escape(getValue(), isHTML) + " -->";
    }
    
    public String toString(){
        return "comment: " + getValue();
    }
}
