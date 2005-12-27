package org.objectstyle.wolips.htmleditor.editor.contentmodel;

import java.util.Collection;

import org.eclipse.wst.html.core.internal.contentmodel.AttributeCollection;
import org.eclipse.wst.html.core.internal.contentmodel.ElementCollection;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * ElementCollection subclass that adds WebObjects as a known block tag.
 *  
 * @author mschrag
 */
public class WebObjectsElementCollection extends ElementCollection implements WebObjectsHTML40Namespace.ElementName {
  private static String[] fNamesWithWebObject;
  
  static {
    String[] names = ElementCollection.getNames();
    fNamesWithWebObject = new String[names.length + 1];
    System.arraycopy(names, 0, fNamesWithWebObject, 0, names.length);
    fNamesWithWebObject[fNamesWithWebObject.length - 1] = WEBOBJECT;
  }
  
  public WebObjectsElementCollection(AttributeCollection collection) {
    super(fNamesWithWebObject, collection);
  }
  
  protected CMNode create(String elementName) {
    CMNode edec = super.create(elementName);
    if (edec == null && elementName.equalsIgnoreCase(WEBOBJECT)) {
      edec = new HedWEBOBJECT(this);
    }
    return edec;
  }

  public Collection getNamesOfBlock() {
    Collection namesOfBlock = super.getNamesOfBlock();
    namesOfBlock.add(WEBOBJECT);
    return namesOfBlock;
  }
}
