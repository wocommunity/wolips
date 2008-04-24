package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLDocTypeImpl extends AbstractFuzzyXMLNode implements FuzzyXMLDocType {

  private String _name;
  private String _publicId;
  private String _systemId;
  private String _internalSubset;

  public FuzzyXMLDocTypeImpl(FuzzyXMLNode parent, String name, String publicId, String systemId, String internalSubset, int offset, int length) {
    super(parent, offset, length);
    this._name = name;
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

  public String getInternalSubset() {
    return _internalSubset;
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {   
    xmlBuffer.append("<!DOCTYPE ").append(_name);
    if (_publicId != null && !_publicId.equals("")) {
      xmlBuffer.append(" PUBLIC ");
      xmlBuffer.append("\"").append(_publicId).append("\"");
      if (_systemId != null && !_systemId.equals("")) {
        xmlBuffer.append(" \"").append(_systemId).append("\"");
      }
    }
    else if (_systemId != null && !_systemId.equals("")) {
      xmlBuffer.append(" SYSTEM ");
      xmlBuffer.append(" \"").append(_systemId).append("\"");
    }

    if (_internalSubset != null && !_internalSubset.equals("")) {
      xmlBuffer.append("[").append(_internalSubset).append("]");
    }
    xmlBuffer.append(">");
    if (renderContext.isShowNewlines()) {
      xmlBuffer.append("\n");
    }
  }

  @Override
  public String toString() {
    return "DOCTYPE: " + _name;
  }

}
