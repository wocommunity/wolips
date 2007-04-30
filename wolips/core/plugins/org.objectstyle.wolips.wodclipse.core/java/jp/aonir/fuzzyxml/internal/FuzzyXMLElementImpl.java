package jp.aonir.fuzzyxml.internal;

import java.util.ArrayList;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLException;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.FuzzyXMLText;


public class FuzzyXMLElementImpl extends AbstractFuzzyXMLNode implements FuzzyXMLElement {
	
	private ArrayList children   = new ArrayList();
	private ArrayList attributes = new ArrayList();
	private String name;
	//	private HashMap namespace = new HashMap();
	
	public FuzzyXMLElementImpl(String name) {
		this(null,name,-1,-1);
	}
	
	public FuzzyXMLElementImpl(FuzzyXMLNode parent,String name,int offset,int length) {
		super(parent,offset,length);
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * XMLの断片テキストから子ノード群を追加します。
	 * <p>
	 * 通常の<code>appendChild()</code>で子ノードを追加した場合、
	 * リスナには<code>FuzzyXMLNode#toXMLString()</code>の結果が新しいテキストとして通知されますが、
	 * このメソッドを用いて子ノードを追加した場合、引数で渡したテキストが新しいテキストとして渡されます。
	 * 不正なXMLをパースし、元のテキスト情報を保持する必要がある場合に使用してください。
	 * </p>
	 * @param text 追加する子要素を含んだXMLの断片。
	 */
	public void appendChildrenFromText(String text){
		if(text.length()==0){
			return;
		}
		// 一度エレメントを挿入してオフセットを取得
		FuzzyXMLElement test = new FuzzyXMLElementImpl("test");
		appendChild(test);
		int offset = test.getOffset();
		// オフセットを取得したらすぐ削除
		removeChild(test);
		
		String parseText = "<root>" + text + "</root>";
		
		FuzzyXMLElement root = new FuzzyXMLParser().parse(parseText).getDocumentElement();
		((AbstractFuzzyXMLNode)root).appendOffset(root, 0, -6);
		((AbstractFuzzyXMLNode)root).appendOffset(root, 0, offset);
		FuzzyXMLNode[] nodes = ((FuzzyXMLElement)root.getChildren()[0]).getChildren();
		
		appendOffset(this, offset, text.length());
		
		for(int i=0;i<nodes.length;i++){
			appendChild(nodes[i], false, false);
		}
		
		fireModifyEvent(text, offset, 0);
	}
	
	/**
	 * このエレメントに子ノードを追加します。
	 * 以下の場合はノードを追加することはできません（FuzzyXMLExceptionが発生します）。
	 * 
	 * <ul>
	 *   <li>エレメントが他のツリーに属している場合（親エレメントからremoveすれば追加できます）</li>
	 *   <li>エレメントが子ノードを持っている場合</li>
	 * </ul>
	 * 
	 * @param node 追加するノード。
	 *   エレメントの場合、子を持たないエレメントを指定してください。
	 *   すでに子要素を構築済みのエレメントを渡すと内部で保持している位置情報が同期されません。
	 *   
	 * @exception jp.aonir.fuzzyxml.FuzzyXMLException ノードを追加できない場合
	 */
	public void appendChild(FuzzyXMLNode node) {
		appendChild(node, true, true);
	}
	
	/**
	 * パース時に<code>appendChild()</code>メソッドの代わりに使用します。
	 */
	public void appendChildWithNoCheck(FuzzyXMLNode node){
		appendChild(node, true, false);
	}
	
	/**
	 * このエレメントに子ノードを追加。
	 * 
	 * @param node 追加するノード。
	 *   エレメントの場合、子を持たないエレメントを指定してください。
	 *   すでに子要素を構築済みのエレメントを渡すと内部で保持している位置情報が同期されません。
	 * @param fireEvent イベントを発火するかどうか。
	 *   falseを指定した場合、ノードが持っている位置情報の同期処理も行いません。
	 * @param check 追加するノードの検証を行うかどうか。
	 *   trueを指定した場合、以下のに該当する場合FuzzyXMLExceptionをthrowします。
	 *   <ul>
	 *     <li>ノードが他のツリーに属している場合</li>
	 *     <li>エレメントがすでに子供を持っている場合</li>
	 *   </ul>
	 *   パース時など、検証を行いたくない場合はfalseを指定します。
	 *   
	 * @exception jp.aonir.fuzzyxml.FuzzyXMLException ノードを追加できない場合
	 */
	private void appendChild(FuzzyXMLNode node, boolean fireEvent, boolean check) {
		if(check){
			if(((AbstractFuzzyXMLNode)node).getDocument()!=null){
				throw new FuzzyXMLException("Appended node already has a parent.");
			}
			
			if(node instanceof FuzzyXMLElement){
				if(((FuzzyXMLElement)node).getChildren().length != 0){
					throw new FuzzyXMLException("Appended node has chidlren.");
				}
			}
		}
		
		AbstractFuzzyXMLNode nodeImpl = (AbstractFuzzyXMLNode)node;
		nodeImpl.setParentNode(this);
		nodeImpl.setDocument(getDocument());
		if(node instanceof FuzzyXMLAttribute){
			setAttribute((FuzzyXMLAttribute)node);
		} else {
			if(children.contains(node)){
				return;
			}
			if(getDocument()==null){
				children.add(node);
				return;
			}
			// 追加するノードの位置(最後)を計算
			FuzzyXMLNode[] nodes = getChildren();
			int offset = 0;
			if(nodes.length==0){
				int length = getLength();
				FuzzyXMLAttribute[] attrs = getAttributes();
				offset = getOffset() + getName().length();
				for(int i=0;i<attrs.length;i++){
					offset = offset + attrs[i].toXMLString().length();
				}
				// ここ微妙？
				offset = offset + 2;
				
				nodeImpl.setOffset(offset);
				if(fireEvent){
					nodeImpl.setLength(node.toXMLString().length());
				}
				
				children.add(node);
				String xml = toXMLString();
				children.remove(node);
				
				// イベントの発火
				if(fireEvent){
					fireModifyEvent(xml,getOffset(),getLength());
					// 位置情報の更新
					appendOffset(this,offset,xml.length() - length);
				}
				
				children.add(node);
				
			} else {
				for(int i=0;i<nodes.length;i++){
					offset = nodes[i].getOffset() + nodes[i].getLength();
				}
				// イベントの発火
				if(fireEvent){
					fireModifyEvent(nodeImpl.toXMLString(),offset,0);
					// 位置情報の更新
					appendOffset(this,offset,node.toXMLString().length());
				}
				
				// 最後に追加
				nodeImpl.setOffset(offset);
				if(fireEvent){
					nodeImpl.setLength(node.toXMLString().length());
				}
				
				children.add(node);
			}
		}
	}
	
	public FuzzyXMLAttribute[] getAttributes() {
		return (FuzzyXMLAttribute[])attributes.toArray(new FuzzyXMLAttribute[attributes.size()]);
	}
	
	public FuzzyXMLNode[] getChildren() {
		// アトリビュートは含まない？
		return (FuzzyXMLNode[])children.toArray(new FuzzyXMLNode[children.size()]);
	}
	
	public boolean hasChildren() {
		if(children.size()==0){
			return false;
		} else {
			return true;
		}
	}
	
	public void insertAfter(FuzzyXMLNode newChild, FuzzyXMLNode refChild) {
		// アトリビュートの場合はなにもしない
		if(newChild instanceof FuzzyXMLAttribute || refChild instanceof FuzzyXMLAttribute){
			return;
		}
		// 挿入する位置を探す
		FuzzyXMLNode[] children = getChildren();
		FuzzyXMLNode targetNode = null;
		boolean flag = false;
		for(int i=0;i<children.length;i++){
			if(flag){
				targetNode = children[i];
			}
			if(children[i]==refChild){
				flag = true;
			}
		}
		if(targetNode==null && flag){
			appendChild(newChild);
		} else {
			insertBefore(newChild, targetNode);
		}
	}
	
	public void insertBefore(FuzzyXMLNode newChild, FuzzyXMLNode refChild) {
		// アトリビュートの場合はなにもしない
		if(newChild instanceof FuzzyXMLAttribute || refChild instanceof FuzzyXMLAttribute){
			return;
		}
		// 挿入する位置を探す
		FuzzyXMLNode target = null;
		int index = -1;
		FuzzyXMLNode[] children = getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i]==refChild){
				target = children[i];
				index  = i;
				break;
			}
		}
		if(target==null){
			return;
		}
		int offset = target.getOffset();
		// イベントの発火
		fireModifyEvent(newChild.toXMLString(),offset,0);
		
		AbstractFuzzyXMLNode nodeImpl = (AbstractFuzzyXMLNode)newChild;
		nodeImpl.setParentNode(this);
		nodeImpl.setDocument(getDocument());
		nodeImpl.setOffset(offset);
		nodeImpl.setLength(newChild.toXMLString().length());
		
		// 位置情報の更新
		appendOffset(this,offset,nodeImpl.toXMLString().length());
		
		// 最後に追加
		this.children.add(index,nodeImpl);
	}
	
	public void replaceChild(FuzzyXMLNode newChild, FuzzyXMLNode refChild) {
		// アトリビュートの場合はなにもしない
		if(newChild instanceof FuzzyXMLAttribute || refChild instanceof FuzzyXMLAttribute){
			return;
		}
		// 置換するノードのインデックスを取得
		int index = -1;
		for(int i=0;i<children.size();i++){
			if(refChild == children.get(i)){
				index = i;
				break;
			}
		}
		// ノードが見つからなかったらなにもしない
		if(index==-1){
			return;
		}
		children.remove(index);
		
		AbstractFuzzyXMLNode nodeImpl = (AbstractFuzzyXMLNode)newChild;
		nodeImpl.setParentNode(this);
		nodeImpl.setDocument(getDocument());
		nodeImpl.setOffset(refChild.getOffset());
		nodeImpl.setLength(newChild.toXMLString().length());
		
		// イベントの発火
		fireModifyEvent(newChild.toXMLString(),refChild.getOffset(),refChild.getLength());
		// 位置情報の更新
		appendOffset(this,refChild.getOffset(),newChild.getLength() - refChild.getLength());
		
		children.add(index,newChild);
	}
	
	public void removeChild(FuzzyXMLNode oldChild){
		if(oldChild instanceof FuzzyXMLAttribute){
			removeAttributeNode((FuzzyXMLAttribute)oldChild);
			return;
		}
		if(children.contains(oldChild)){
			// デタッチ
			((AbstractFuzzyXMLNode)oldChild).setParentNode(null);
			((AbstractFuzzyXMLNode)oldChild).setDocument(null);
			// リストから削除
			children.remove(oldChild);
			// イベントの発火
			fireModifyEvent("",oldChild.getOffset(),oldChild.getLength());
			// 位置情報の更新
			appendOffset(this,oldChild.getOffset(),oldChild.getLength() * -1);
		}
	}
	
	public void setAttribute(FuzzyXMLAttribute attr){
		FuzzyXMLAttribute attrNode = getAttributeNode(attr.getName());
		if(attrNode==null){
			if(attributes.contains(attr)){
				return;
			}
			if(getDocument()==null){
				attributes.add(attr);
				return;
			}
			FuzzyXMLAttributeImpl attrImpl = (FuzzyXMLAttributeImpl)attr;
			attrImpl.setDocument(getDocument());
			attrImpl.setParentNode(this);
			// 追加するアトリビュートの位置を検索
			FuzzyXMLAttribute[] attrs = getAttributes();
			int offset = getOffset() + getName().length() + 1;
			for(int i=0;i<attrs.length;i++){
				offset = offset + attrs[i].toXMLString().length();
			}
			// 更新イベントを発火
			fireModifyEvent(attr.toXMLString(),offset,0);
			// 位置情報の更新
			appendOffset(this,offset,attr.toXMLString().length());
			// 最後に追加
			attrImpl.setOffset(offset);
			attrImpl.setLength(attrImpl.toXMLString().length());
			attributes.add(attrImpl);
		} else {
			// この場合はアトリビュートのsetValueメソッド内でイベント発火
			FuzzyXMLAttributeImpl attrImpl = (FuzzyXMLAttributeImpl)attrNode;
			attrImpl.setValue(attr.getValue());
		}
	}
	
	public FuzzyXMLAttribute getAttributeNode(String name) {
		FuzzyXMLAttribute[] attrs = getAttributes();
		for(int i=0;i<attrs.length;i++){
			if(attrs[i].getName().equalsIgnoreCase(name)){
				return attrs[i];
			}
		}
		return null;
	}
	
	public boolean hasAttribute(String name) {
		return getAttributeNode(name)!=null;
	}
	
	public void removeAttributeNode(FuzzyXMLAttribute attr){
		if(attributes.contains(attr)){
			// デタッチ
			((AbstractFuzzyXMLNode)attr).setParentNode(null);
			((AbstractFuzzyXMLNode)attr).setDocument(null);
			// リストから削除
			attributes.remove(attr);
			// イベントの発火
			fireModifyEvent("",attr.getOffset(),attr.getLength());
			// 位置情報の更新
			appendOffset(this,attr.getOffset(),attr.getLength() * -1);
		}
	}
	
	public String getValue(){
		StringBuffer sb = new StringBuffer();
		FuzzyXMLNode[] children = getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof FuzzyXMLText){
				sb.append(((FuzzyXMLText)children[i]).getValue());
			}
		}
		return sb.toString();
	}
	
	public String toXMLString(){
		boolean isHTML = false;
		if(getDocument()!=null){
			isHTML = getDocument().isHTML();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("<").append(FuzzyXMLUtil.escape(getName(), isHTML));
		FuzzyXMLAttribute[] attrs = getAttributes();
		for(int i=0;i<attrs.length;i++){
			sb.append(attrs[i].toXMLString());
		}
		FuzzyXMLNode[] children = getChildren();
		if(children.length==0){
			sb.append("/>");
		} else {
			sb.append(">");
			for(int i=0;i<children.length;i++){
				sb.append(children[i].toXMLString());
			}
			sb.append("</").append(FuzzyXMLUtil.escape(getName(), isHTML)).append(">");
		}
		return sb.toString();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof FuzzyXMLElement){
			FuzzyXMLElement element = (FuzzyXMLElement)obj;
			
			// タグの名前が違ったらfalse
			if(!element.getName().equals(getName())){
				return false;
			}
			
			// 親が両方ともnullだったらtrue
			FuzzyXMLNode parent = element.getParentNode();
			if(parent==null){
				if(getParentNode()==null){
					return true;
				}
				return false;
			}
			
			// 開始オフセットが同じだったらtrue
			if(element.getOffset()==getOffset()){
				return true;
			}
			
		}
		return false;
	}
	
	public String getAttributeValue(String name){
		FuzzyXMLAttribute attr = getAttributeNode(name);
		if(attr!=null){
			return attr.getValue();
		}
		return null;
	}
	
	public void setAttribute(String name, String value){
		FuzzyXMLAttribute attr = new FuzzyXMLAttributeImpl(name, value);
		setAttribute(attr);
	}
	
	public void removeAttribute(String name){
		FuzzyXMLAttribute attr = getAttributeNode(name);
		if(attr!=null){
			removeAttributeNode(attr);
		}
	}
	
	public void setDocument(FuzzyXMLDocumentImpl doc){
		super.setDocument(doc);
		FuzzyXMLNode[] nodes = getChildren();
		for(int i=0;i<nodes.length;i++){
			((AbstractFuzzyXMLNode)nodes[i]).setDocument(doc);
		}
		FuzzyXMLAttribute[] attrs = getAttributes();
		for(int i=0;i<attrs.length;i++){
			((AbstractFuzzyXMLNode)attrs[i]).setDocument(doc);
		}
	}
	
	public String toString(){
		return "element: " + getName();
	}
	
	public void removeAllChildren(){
		FuzzyXMLNode[] children = getChildren();
		for(int i=0;i<children.length;i++){
			removeChild(children[i]);
		}
	}
}
