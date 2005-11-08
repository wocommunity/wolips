package org.objectstyle.wolips.htmleditor.editor.contentmodel;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.html.core.internal.contentmodel.CMContentImpl;
import org.eclipse.wst.html.core.internal.contentmodel.CMGroupImpl;
import org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinition;
import org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinitionFactory;
import org.eclipse.wst.html.core.internal.contentmodel.ElementCollection;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class WebObjectsComplexTypeDefinitionFactory extends ComplexTypeDefinitionFactory {
  private Set myAlreadyConvertedDefs;

  public WebObjectsComplexTypeDefinitionFactory() {
    myAlreadyConvertedDefs = new HashSet();
  }

  protected ComplexTypeDefinition createUncachedTypeDefinition(String _definitionName, ElementCollection _elementCollection) {
    ComplexTypeDefinition def = super.createUncachedTypeDefinition(_definitionName, _elementCollection);
    if (!myAlreadyConvertedDefs.contains(_definitionName)) {
      CMGroupImpl content = (CMGroupImpl) def.getContent();
      CMGroupImpl wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, CMContentImpl.UNBOUNDED);
      content.appendChild(wrap);
      CMNode dec = _elementCollection.getNamedItem(WebObjectsHTML40Namespace.ElementName.WEBOBJECT);
      if (dec != null) {
        wrap.appendChild(dec);
      }
    }
    return def;
  }
}
