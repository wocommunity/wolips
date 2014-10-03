package jp.aonir.fuzzyxml;

/**
 * DOCTYPE
 * AFuzzyXMLElement
 */
public interface FuzzyXMLDocType extends FuzzyXMLNode {

	public String getName();
	
	public String getPublicId();
	
	public String getSystemId();
	
	public String getInternalSubset();
}
