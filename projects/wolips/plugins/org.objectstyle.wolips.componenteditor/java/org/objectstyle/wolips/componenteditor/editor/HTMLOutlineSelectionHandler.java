/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.componenteditor.editor;

import java.util.List;

import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.sse.ui.internal.contentoutline.StructuredTextEditorContentOutlinePage;
import org.eclipse.wst.sse.ui.internal.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.internal.view.events.NodeSelectionChangedEvent;
import org.objectstyle.wolips.wodclipse.wod.WodEditor;

public class HTMLOutlineSelectionHandler implements INodeSelectionListener {
	private WodEditor wodEditor;

	public HTMLOutlineSelectionHandler(
			StructuredTextEditorContentOutlinePage contentOutlinePage,
			WodEditor wodEditor) {
		super();
		this.wodEditor = wodEditor;
		contentOutlinePage.getViewerSelectionManager()
				.addNodeSelectionListener(this);
	}

	public void nodeSelectionChanged(NodeSelectionChangedEvent event) {

		List nodes = event.getSelectedNodes();
		if (nodes != null && nodes.size() == 1) {
			Object object = nodes.get(0);
			if (object instanceof ElementStyleImpl) {
				ElementStyleImpl elementStyleImpl = (ElementStyleImpl) object;
				String tagName = elementStyleImpl.getTagName();
				if (tagName != null && "webobject".equalsIgnoreCase(tagName)) {
					this.changeWodSelection(elementStyleImpl);

				}
			}
		}
	}

	private void changeWodSelection(ElementStyleImpl elementStyleImpl) {
		String webobjectTagName = elementStyleImpl.getAttribute("name");
		if (webobjectTagName != null && webobjectTagName.length() > 0) {
			wodEditor.selectTagNamed(webobjectTagName);
		}
	}
}