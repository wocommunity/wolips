package jp.aonir.fuzzyxml;

public interface FuzzyXMLText extends FuzzyXMLNode {
	
	public String getValue();
	
	public void setEscape(boolean escape);
	
	public boolean isEscape();
	
}
