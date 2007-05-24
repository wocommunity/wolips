package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public abstract class AbstractFuzzyXMLNode implements FuzzyXMLNode {
	
	private int _offset = -1;
	private int _length = -1;
	private FuzzyXMLNode _parent;
	private FuzzyXMLDocumentImpl _doc;
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
		this._length = length;
	}
	
	public int getLength() {
		return _length;
	}
	
	public int getOffset() {
		return _offset;
	}
	
	public FuzzyXMLNode getParentNode() {
		return _parent;
	}
	
	public void setOffset(int offset){
		this._offset = offset;
	}
	
	public void setParentNode(FuzzyXMLNode parent){
		this._parent = parent;
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
	    this._doc = doc;
	}
	
	public FuzzyXMLDocumentImpl getDocument(){
	    return _doc;
	}
}
