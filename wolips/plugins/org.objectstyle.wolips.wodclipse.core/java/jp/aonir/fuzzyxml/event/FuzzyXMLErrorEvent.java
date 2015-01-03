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
	 * @return length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return offset
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * @return node
	 */
	public FuzzyXMLNode getNode() {
		return node;
	}
}
