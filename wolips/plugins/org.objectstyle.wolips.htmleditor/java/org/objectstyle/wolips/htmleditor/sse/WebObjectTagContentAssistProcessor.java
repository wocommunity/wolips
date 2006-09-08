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
package org.objectstyle.wolips.htmleditor.sse;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.objectstyle.wolips.htmleditor.HtmleditorPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class WebObjectTagContentAssistProcessor extends HTMLContentAssistProcessor {

	/**
	 * Return a list of proposed code completions based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text-editor control.
	 * 
	 * @param documentPosition
	 *            a location within the document
	 * @return an array of code-assist items
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentPosition) {

		IndexedRegion indexedNode = ContentAssistUtils.getNodeAt((StructuredTextViewer) viewer, documentPosition);
		IDOMNode xNode = null;

		if (indexedNode instanceof IDOMNode) {
			xNode = (IDOMNode) indexedNode;
		}
		if (xNode != null && xNode.getNodeName().equalsIgnoreCase("webobject")) {
			NamedNodeMap namedNodeMap = xNode.getAttributes();
			Node node = namedNodeMap.getNamedItem("name");
			Attr attr = (Attr) node;
			String value = attr.getNodeValue();
			if (value != null) {
				String[] names = HtmleditorPlugin.getDefault().getWebObjectsTagNames();
				ICompletionProposal[] completionProposals = new ICompletionProposal[names.length];
				for (int i = 0; i < names.length; i++) {
					ICompletionProposal completionProposal = new WebObjectTagNameAttributeCompletionProposal(node, names[i]);
					completionProposals[i] = completionProposal;
				}
				return completionProposals;
			}
		}

		return super.computeCompletionProposals(viewer, documentPosition);
	}

	private class WebObjectTagNameAttributeCompletionProposal implements ICompletionProposal {
		private Node node;

		private String proposal;

		public WebObjectTagNameAttributeCompletionProposal(Node node, String proposal) {
			super();
			this.node = node;
			this.proposal = proposal;
		}

		public void apply(IDocument document) {
			node.setNodeValue(proposal);
		}

		public Point getSelection(IDocument document) {
			return null;
		}

		public String getAdditionalProposalInfo() {
			return null;
		}

		public String getDisplayString() {
			return proposal;
		}

		public Image getImage() {
			return null;
		}

		public IContextInformation getContextInformation() {
			return null;
		}

	}
}