package jp.aonir.fuzzyxml.util;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class ElementFilter implements NodeFilter {
	
	private String name;
	
	public ElementFilter(){
		this(null);
	}
	
	public ElementFilter(String name){
		this.name = name;
	}
	
	public boolean filter(FuzzyXMLNode node) {
		if(node instanceof FuzzyXMLElement){
			FuzzyXMLElement element = (FuzzyXMLElement)node;
			if(name!=null && !name.equals(element.getName())){
				return false;
			}
			return true;
		}
		return false;
	}

}
