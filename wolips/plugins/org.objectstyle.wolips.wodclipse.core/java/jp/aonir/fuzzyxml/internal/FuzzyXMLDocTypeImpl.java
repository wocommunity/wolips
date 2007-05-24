package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLDocTypeImpl extends AbstractFuzzyXMLNode implements FuzzyXMLDocType {
	
	private String _name;
	private String _publicId;
	private String _systemId;
	private String _internalSubset;
	
	public FuzzyXMLDocTypeImpl(FuzzyXMLNode parent,String name,String publicId,String systemId,String internalSubset,
			                   int offset,int length){
        super(parent,offset,length);
		this._name     = name;
		this._publicId = publicId;
		this._systemId = systemId;
		this._internalSubset = internalSubset;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getPublicId() {
		return _publicId;
	}

	public String getSystemId() {
		return _systemId;
	}
	
	public String getInternalSubset(){
		return _internalSubset;
	}
	
	public String toXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE ").append(_name);
		if(_publicId!=null && !_publicId.equals("")){
			sb.append(" PUBLIC ");
			sb.append("\"").append(_publicId).append("\"");
			if(_systemId!=null && !_systemId.equals("")){
				sb.append(" \"").append(_systemId).append("\"");
			}
		} else if(_systemId!=null && !_systemId.equals("")){
			sb.append(" SYSTEM ");
			sb.append(" \"").append(_systemId).append("\"");
		}
		
		if(_internalSubset!=null && !_internalSubset.equals("")){
			sb.append("[").append(_internalSubset).append("]");
		}
		sb.append(">");
		return sb.toString();
	}
	
	@Override
  public String toString(){
		return "DOCTYPE: " + _name;
	}

}
