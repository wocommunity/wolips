package jp.aonir.fuzzyxml.internal;

import java.util.ArrayList;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLCDATA;
import jp.aonir.fuzzyxml.FuzzyXMLComment;
import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLProcessingInstruction;
import jp.aonir.fuzzyxml.FuzzyXMLText;
import jp.aonir.fuzzyxml.event.FuzzyXMLModifyEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLModifyListener;


public class FuzzyXMLDocumentImpl implements FuzzyXMLDocument {
	
	private boolean isHTML = false;
	private FuzzyXMLElement root;
	private FuzzyXMLDocType docType;
	private ArrayList listeners = new ArrayList();
	
	public FuzzyXMLDocumentImpl(FuzzyXMLElement root,FuzzyXMLDocType docType) {
		super();
		this.root    = root;
		this.docType = docType;
		
		// ドキュメントオブジェクトをセット
		if(this.root!=null){
			((FuzzyXMLElementImpl)this.root).setDocument(this);
			setDocument((FuzzyXMLElement)this.root);
		}
		if(this.docType!=null){
			((AbstractFuzzyXMLNode)this.docType).setDocument(this);
		}
	}
	
	public void setHTML(boolean isHTML){
		this.isHTML = isHTML;
	}
	
	public boolean isHTML(){
		return this.isHTML;
	}
	
	private void setDocument(FuzzyXMLElement element){
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
		    ((AbstractFuzzyXMLNode)children[i]).setDocument(this);
		    if(children[i] instanceof FuzzyXMLElement){
		        setDocument((FuzzyXMLElement)children[i]);
		    }
		}
		FuzzyXMLAttribute[] attr = element.getAttributes();
		for(int i=0;i<attr.length;i++){
		    ((AbstractFuzzyXMLNode)attr[i]).setDocument(this);
		}
	}
		
	public FuzzyXMLComment createComment(String value){
	    return new FuzzyXMLCommentImpl(value);
	}
	
	public FuzzyXMLElement createElement(String name) {
		return new FuzzyXMLElementImpl(name);
	}

	public FuzzyXMLAttribute createAttribute(String name) {
		return new FuzzyXMLAttributeImpl(name);
	}

	public FuzzyXMLText createText(String value) {
		return new FuzzyXMLTextImpl(value);
	}
	
	public FuzzyXMLCDATA createCDATASection(String value){
	    return new FuzzyXMLCDATAImpl(value);
	}
	
	public FuzzyXMLProcessingInstruction createProcessingInstruction(String name,String data){
		return new FuzzyXMLProcessingInstructionImpl(name,data);
	}
	
	public FuzzyXMLElement getDocumentElement() {
		return root;
	}
	
	public FuzzyXMLDocType getDocumentType(){
		return docType;
	}
	
	public FuzzyXMLElement getElementByOffset(int offset){
		if(root==null){
			return null;
		}
		ArrayList matches = new ArrayList();
		matches.add(root);
		matchOffsetElement(root,matches,offset);
		
		// 区間が最も短いものを選ぶ
		FuzzyXMLElement find = null;
		for(int i=0;i<matches.size();i++){
			FuzzyXMLElement element = (FuzzyXMLElement)matches.get(i);
			if(find==null || find.getLength() >= element.getLength()){
				find = element;
			}
		}
		return find;
	}
	
	private void matchOffsetElement(FuzzyXMLElement element,ArrayList matches,int offset){
		FuzzyXMLNode[] nodes = element.getChildren();
		for(int i=0;i<nodes.length;i++){
			if(nodes[i] instanceof FuzzyXMLElement){
				FuzzyXMLElement e = (FuzzyXMLElement)nodes[i];
				if(e.getOffset() <= offset && offset <= e.getOffset()+e.getLength()){
					matches.add(nodes[i]);
					matchOffsetElement(e,matches,offset);
				}
			}
		}
	}
	
	public void addModifyListener(FuzzyXMLModifyListener listener){
	    listeners.add(listener);
	}
	
	public void removeModifyListener(FuzzyXMLModifyListener listener){
	    listeners.remove(listener);
	}
	
	/**
	 * 更新イベントを発火します。
	 * 
	 * @param newText
	 * @param offset
	 * @param length
	 */
	public void fireModifyEvent(String newText,int offset,int length){
	    FuzzyXMLModifyEvent evt = new FuzzyXMLModifyEvent(newText,offset,length);
	    for(int i=0;i<listeners.size();i++){
	        FuzzyXMLModifyListener listener = (FuzzyXMLModifyListener)listeners.get(i);
	        listener.modified(evt);
	    }
	}
	
	/**
	 * オフセット値を更新します。
	 * 
	 * @param offset
	 * @param append
	 */
	public void appendOffset(FuzzyXMLElement parent,int offset,int append){
	    appendOffsetForElement((FuzzyXMLElementImpl)root,offset,append);
	    appendLengthForParent((FuzzyXMLElementImpl)parent,append);
	}
	
	private void appendOffsetForElement(FuzzyXMLElementImpl element,int offset,int append){
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
		    if(children[i].getOffset() >= offset){
		        ((AbstractFuzzyXMLNode)children[i]).setOffset(children[i].getOffset() + append);
		    }
		    if(children[i] instanceof FuzzyXMLElementImpl){
		        appendOffsetForElement((FuzzyXMLElementImpl)children[i],offset,append);
		    }
		}
		FuzzyXMLAttribute[] attr = element.getAttributes();
		for(int i=0;i<attr.length;i++){
		    if(attr[i].getOffset() >= offset){
		        ((AbstractFuzzyXMLNode)attr[i]).setOffset(attr[i].getOffset() + append);
		    }
		}
	}
	
	private void appendLengthForParent(FuzzyXMLElementImpl parent,int appendLength){
	    parent.setLength(parent.getLength() + appendLength);
	    if(parent.getParentNode()!=null){
	        appendLengthForParent((FuzzyXMLElementImpl)parent.getParentNode(),appendLength);
	    }
	}

}
