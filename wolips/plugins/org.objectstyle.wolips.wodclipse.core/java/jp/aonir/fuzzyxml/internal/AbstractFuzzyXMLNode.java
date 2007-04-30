package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public abstract class AbstractFuzzyXMLNode implements FuzzyXMLNode {
	
	private int offset = -1;
	private int length = -1;
	private FuzzyXMLNode parent;
	private FuzzyXMLDocumentImpl doc;
//	private String namespaceURI;
//	private String prefix;
	
	public AbstractFuzzyXMLNode() {
		super();
	}
	
	public AbstractFuzzyXMLNode(FuzzyXMLNode parent,int offset,int length){
	    super();
	    setParentNode(parent);
	    setOffset(offset);
	    setLength(length);
	}
	
//	public void setNamespaceURI(String namespaceURI){
//		this.namespaceURI = namespaceURI;
//	}
//	
//	public String getNamespaceURI(){
//		return this.namespaceURI;
//	}
//	
//	public void setPrefix(String prefix){
//		this.prefix = prefix;
//	}
//	
//	public String getPrefix(){
//		return this.prefix;
//	}
	
	public void setLength(int length){
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public FuzzyXMLNode getParentNode() {
		return parent;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public void setParentNode(FuzzyXMLNode parent){
		this.parent = parent;
	}
	
	/**
	 * ツリーの更新イベントを発火します。
	 * 
	 * @param newText
	 * @param offset
	 * @param length
	 */
	protected void fireModifyEvent(String newText,int offset,int length){
	    // まだノードがツリーに追加されていない場合はなにもしない
	    FuzzyXMLDocumentImpl doc = getDocument();
	    if(doc==null){
	        return;
	    }
	    doc.fireModifyEvent(newText,offset,length);
	}
	
	/**
	 * ノードの位置情報を更新します。ツリーの変更前に呼び出します。
	 * 
	 * @param parent
	 * @param offset
	 * @param append
	 */
	protected void appendOffset(FuzzyXMLElement parent,int offset,int append){
	    // まだノードがツリーに追加されていない場合はなにもしない
	    FuzzyXMLDocumentImpl doc = getDocument();
	    if(doc==null){
	        return;
	    }
	    doc.appendOffset(parent,offset,append);
	}
	
	public void setDocument(FuzzyXMLDocumentImpl doc){
	    this.doc = doc;
	}
	
	public FuzzyXMLDocumentImpl getDocument(){
	    return doc;
	}
}
