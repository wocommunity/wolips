package jp.aonir.fuzzyxml.event;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLErrorEvent {
	
	private int offset;
	private int length;
	private String message;
	private FuzzyXMLNode node;
	
	public FuzzyXMLErrorEvent(int offset,int length,String message,FuzzyXMLNode node){
		this.offset  = offset;
		this.length  = length;
		this.message = message;
		this.node    = node;
	}
	
	/**
	 * @return length を戻します。
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @return message を戻します。
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return offset を戻します。
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * @return node を戻します。
	 */
	public FuzzyXMLNode getNode() {
		return node;
	}
}
