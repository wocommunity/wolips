package jp.aonir.fuzzyxml;

import jp.aonir.fuzzyxml.event.FuzzyXMLModifyListener;

public interface FuzzyXMLDocument {
	
    public FuzzyXMLComment createComment(String value);
    
	public FuzzyXMLElement createElement(String name);
	
	public FuzzyXMLAttribute createAttribute(String namespace, String name);
	
	public FuzzyXMLText createText(String value);
	
	public FuzzyXMLCDATA createCDATASection(String value);
	
	public FuzzyXMLProcessingInstruction createProcessingInstruction(String name,String data);
	
	public FuzzyXMLElement getDocumentElement();
	
	public FuzzyXMLDocType getDocumentType();
	
	public FuzzyXMLElement getElementByOffset(int offset);
	
	/**
	 * FuzzyXMLModifyListener
	 * 
	 * @param listener
	 */
	public void addModifyListener(FuzzyXMLModifyListener listener);
	
	/**
	 * FuzzyXMLModifyListener
	 * 
	 * @param listener
	 */
	public void removeModifyListener(FuzzyXMLModifyListener listener);
	
	public boolean isHTML();

}
