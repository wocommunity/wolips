/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.objectstyle.wolips.htmleditor.editor.contentmodel;



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.contentmodel.CMNamedNodeMapImpl;
import org.eclipse.wst.html.core.internal.contentmodel.ElementCollection;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLAttrDeclImpl;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDataType;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDataTypeImpl;
import org.eclipse.wst.html.core.internal.contentmodel.HedFlowContainer;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * WEBOBJECT.
 */
public class HedWEBOBJECT extends HedFlowContainer {

	/**
	 */
	public HedWEBOBJECT(ElementCollection collection) {
		super(WebObjectsHTML40Namespace.ElementName.WEBOBJECT, collection);
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * %attrs;
	 * %align;
	 * %reserved;
	 */
	protected void createAttributeDeclarations() {
    if (attributes != null)
      return; // already created.
    if (attributeCollection == null)
      return; // fatal

    attributes = new CMNamedNodeMapImpl();

    String[] names = { HTML40Namespace.ATTR_NAME_NAME };
    attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

    // (type %ContentType; #REQUIRED) ... should be defined locally.
    HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.NAME);
    HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_NAME, atype, CMAttributeDeclaration.REQUIRED);
    attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}