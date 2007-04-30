package jp.aonir.fuzzyxml;

/**
 * DOCTYPE宣言を示すノード。DOCTYPE宣言の編集はサポートしません。
 * このノードは親を持たず、FuzzyXMLDocumentオブジェクトから直接取得します
 * （getParentメソッドは常にnullを返します）。
 * また、FuzzyXMLElementの子要素として追加することはできません。
 */
public interface FuzzyXMLDocType extends FuzzyXMLNode {

	public String getName();
	
	public String getPublicId();
	
	public String getSystemId();
	
	public String getInternalSubset();
}
