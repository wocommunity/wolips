package jp.aonir.fuzzyxml;

public interface FuzzyXMLText extends FuzzyXMLNode, FuzzyXMLFormat {
	
	public String getValue();
	
	public void setEscape(boolean escape);
	
	public boolean isEscape();
	
	public boolean hasLineBreaks();
	
}
