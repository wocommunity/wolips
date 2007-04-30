package jp.aonir.fuzzyxml;

public interface FuzzyXMLAttribute extends FuzzyXMLNode {
	
	public String getName();
	
	public void setValue(String value);
	
	public String getValue();
	
	public void setQuoteCharacter(char c);
	
	public char getQuoteCharacter();
	
	public void setEscape(boolean escape);
	
	public boolean isEscape();
}
