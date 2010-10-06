package jp.aonir.fuzzyxml;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;


public interface FuzzyXMLElement extends FuzzyXMLNode, FuzzyXMLFormat {
	
	public String getName();
	
  public int getNameOffset();
  
  public int getNameLength();

  public int getOpenTagLength();

  public boolean hasCloseTag();
  
  public int getCloseTagOffset();

  public int getCloseTagLength();

  public int getCloseNameOffset();

  public int getCloseNameLength();
  
  public FuzzyXMLNode getChild(int index);

  public FuzzyXMLElement getChildElement(int index);

	public FuzzyXMLNode[] getChildren();
	
	public boolean hasChildren();
	
	public boolean isEmpty();
	
	public void appendChild(FuzzyXMLNode node);
	
	public void insertBefore(FuzzyXMLNode newChild,FuzzyXMLNode refChild);
	
	public void insertAfter(FuzzyXMLNode newChild, FuzzyXMLNode refChild);
	
	public void replaceChild(FuzzyXMLNode newChild,FuzzyXMLNode refChild);
	
	public void removeChild(FuzzyXMLNode oldChild);
	
	
	public FuzzyXMLAttribute[] getAttributes();
	
	public void setAttribute(FuzzyXMLAttribute attr);
	
	public boolean hasAttribute(String name);
	
	public FuzzyXMLAttribute getAttributeNode(String name);
	
	public String getAttributeValue(String name);
	
	public void removeAttributeNode(FuzzyXMLAttribute attr);
	
	public void setAttribute(String name, String namespace, String value);
	
	public void removeAttribute(String name);
	
	public String getValue();
	
	public void removeAllChildren();

	public IRegion getRegionAtOffset(int offset, IDocument doc, boolean regionForInsert) throws BadLocationException;
	
	public boolean isSelfClosing();
  
  public boolean isForbiddenFromHavingChildren();
  
  public void setSynthetic(boolean synthetic);
}
