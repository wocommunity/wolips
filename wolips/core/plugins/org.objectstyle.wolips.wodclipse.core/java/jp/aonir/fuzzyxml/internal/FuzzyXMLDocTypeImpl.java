package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLDocTypeImpl extends AbstractFuzzyXMLNode implements FuzzyXMLDocType {
	
	private String name;
	private String publicId;
	private String systemId;
	private String internalSubset;
	
	public FuzzyXMLDocTypeImpl(FuzzyXMLNode parent,String name,String publicId,String systemId,String internalSubset,
			                   int offset,int length){
        super(parent,offset,length);
		this.name     = name;
		this.publicId = publicId;
		this.systemId = systemId;
		this.internalSubset = internalSubset;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPublicId() {
		return publicId;
	}

	public String getSystemId() {
		return systemId;
	}
	
	public String getInternalSubset(){
		return internalSubset;
	}
	
	public String toXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE ").append(name);
		if(publicId!=null && !publicId.equals("")){
			sb.append(" PUBLIC ");
			sb.append("\"").append(publicId).append("\"");
			if(systemId!=null && !systemId.equals("")){
				sb.append(" \"").append(systemId).append("\"");
			}
		} else if(systemId!=null && !systemId.equals("")){
			sb.append(" SYSTEM ");
			sb.append(" \"").append(systemId).append("\"");
		}
		
		if(internalSubset!=null && !internalSubset.equals("")){
			sb.append("[").append(internalSubset).append("]");
		}
		sb.append(">");
		return sb.toString();
	}
	
	public String toString(){
		return "DOCTYPE: " + name;
	}

}
