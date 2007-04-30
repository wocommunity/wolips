package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLProcessingInstruction;

public class FuzzyXMLProcessingInstructionImpl extends AbstractFuzzyXMLNode implements FuzzyXMLProcessingInstruction {

	private String name;
	private String data;
	
	public FuzzyXMLProcessingInstructionImpl(String name,String data){
		super();
		this.name = name;
		this.data = data;
	}
	
	public FuzzyXMLProcessingInstructionImpl(FuzzyXMLElement parent,String name,String data,int offset,int length){
		super(parent,offset,length);
		this.name = name;
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public String getName() {
		return name;
	}

	public void setData(String data) {
		int length = this.data.length();
		this.data = data;
		// 更新イベントを発火
		fireModifyEvent(toXMLString(),getOffset(),getLength());
		// 位置情報を更新
		appendOffset((FuzzyXMLElement)getParentNode(),getOffset(),data.length() - length);
	}
	
	public String toString(){
		return "PI: " + name;
	}
	
	public String toXMLString() {
		return "<?" + name + " " + data + "?>";
	}

}
