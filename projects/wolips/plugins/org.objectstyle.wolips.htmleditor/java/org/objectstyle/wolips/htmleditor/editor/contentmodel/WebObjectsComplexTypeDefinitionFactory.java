package org.objectstyle.wolips.htmleditor.editor.contentmodel;

import org.eclipse.wst.html.core.internal.contentmodel.CMContentImpl;
import org.eclipse.wst.html.core.internal.contentmodel.CMGroupImpl;
import org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinition;
import org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinitionFactory;
import org.eclipse.wst.html.core.internal.contentmodel.ElementCollection;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Custom implementation of ComplexTypeDefinitionFactory that hooks into each CTD 
 * and adds support for it to contain 1 or more webobject tags.
 * 
 * @author mschrag
 */
public class WebObjectsComplexTypeDefinitionFactory extends ComplexTypeDefinitionFactory {
  public WebObjectsComplexTypeDefinitionFactory() {
  }

  protected ComplexTypeDefinition createUncachedTypeDefinition(String _definitionName, ElementCollection _elementCollection) {
    ComplexTypeDefinition def = super.createUncachedTypeDefinition(_definitionName, _elementCollection);
    CMGroupImpl content = (CMGroupImpl) def.getContent();
    CMGroupImpl wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, CMContentImpl.UNBOUNDED);
    content.appendChild(wrap);
    CMNode dec = _elementCollection.getNamedItem(WebObjectsHTML40Namespace.ElementName.WEBOBJECT);
    if (dec != null) {
      wrap.appendChild(dec);
    }
    return def;
  }
}
