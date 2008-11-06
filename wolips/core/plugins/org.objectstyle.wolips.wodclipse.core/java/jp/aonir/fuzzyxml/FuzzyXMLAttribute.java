package jp.aonir.fuzzyxml;

public interface FuzzyXMLAttribute extends FuzzyXMLNode {
  public String getNamespaceName();
  
	public String getNamespace();
	
	public String getName();
  
	public int getNamespaceOffset();
	
  public int getNamespaceLength();
  
  public int getNameOffset();
  
  public int getNameLength();
  
  public int getValueOffset();
  
  public int getValueLength();
	
  public int getValueDataOffset();
  
  public int getValueDataLength();
  
	public void setValue(String value);
	
	public String getRawValue();
	
	public String getValue();
	
  public boolean isQuoted();
  
	public void setQuoteCharacter(char c);
	
	public char getQuoteCharacter();
	
	public void setEscape(boolean escape);
	
	public boolean isEscape();
}
