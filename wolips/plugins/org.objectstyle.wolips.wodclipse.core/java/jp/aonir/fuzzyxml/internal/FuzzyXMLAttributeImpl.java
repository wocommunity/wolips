package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLAttributeImpl extends AbstractFuzzyXMLNode implements FuzzyXMLAttribute {
	
	private char quote = '"';
	private boolean escape = true;
	private String name;
	private String value;
	
	public FuzzyXMLAttributeImpl(String name) {
		this(null,name,null,-1,-1);
	}
	
	public FuzzyXMLAttributeImpl(String name, String value){
		this(null,name,null,-1,-1);
		setValue(value);
	}
	
	public FuzzyXMLAttributeImpl(FuzzyXMLNode parent,String name,String value,int offset,int length){
		super(parent,offset,length);
		this.name  = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
	    if(this.value==null){
	        this.value = "";
	    }
	    
	    int length = this.value.length();
		this.value = value;
	    
	    // 更新イベントを発火
		fireModifyEvent(toXMLString(),getOffset(),getLength());
		// 位置情報を更新
		appendOffset((FuzzyXMLElement)getParentNode(),getOffset(),value.length() - length);
	}
	
	public String getValue() {
		return value;
	}
	
	public char getQuoteCharacter() {
		return quote;
	}

	public void setQuoteCharacter(char c) {
		quote = c;
	}

	public void setEscape(boolean escape){
		this.escape = escape;
	}
	
	public boolean isEscape(){
		return this.escape;
	}
	
	public String toXMLString(){
		boolean isHTML = false;
		if(getDocument()!=null){
			isHTML = getDocument().isHTML();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(" ");
		sb.append(FuzzyXMLUtil.escape(getName(), isHTML));
		sb.append("=");
		sb.append(quote);
		if(escape){
			sb.append(FuzzyXMLUtil.escape(getValue(), isHTML));
		} else {
			String value = getValue();
			for(int i=0;i<value.length();i++){
				char c = value.charAt(i);
				if(quote == c){
					sb.append('\\');
				}
				sb.append(c);
			}
		}
		sb.append(quote);
	    return sb.toString();
	}
	
	public String toString(){
	    return "attr: " + getName() + "=" + getValue();
	}
}
