/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.html.ui.views.contentoutline.HTMLContentOutlineConfiguration;
import org.objectstyle.wolips.htmleditor.HtmleditorPlugin;

public class ContentOutlineConfigurationWO extends
		HTMLContentOutlineConfiguration {
	Image image;

	WebObjectTagLabelProvider webObjectTagLabelProvider;

	public ContentOutlineConfigurationWO() {
		super();
	}

	/**
	 * @param viewer
	 * @return the ILabelProvider for items within the viewer
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (webObjectTagLabelProvider != null) {
			return webObjectTagLabelProvider;
		}
		ILabelProvider labelProvider = super.getLabelProvider(viewer);
		if (!(labelProvider instanceof WebObjectTagLabelProvider)) {
			webObjectTagLabelProvider = new WebObjectTagLabelProvider(
					labelProvider);
			return webObjectTagLabelProvider;
		}
		return labelProvider;
	}

	private class WebObjectTagLabelProvider implements ILabelProvider {

		private ILabelProvider baseLabelProvider;

		public WebObjectTagLabelProvider(ILabelProvider baseLabelProvider) {
			super();
			this.baseLabelProvider = baseLabelProvider;
		}

		public void addListener(ILabelProviderListener listener) {
			baseLabelProvider.addListener(listener);
		}

		public void dispose() {
			baseLabelProvider.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return baseLabelProvider.isLabelProperty(element, property);
		}

		public void removeListener(ILabelProviderListener listener) {
			baseLabelProvider.removeListener(listener);
		}

		public Image getImage(Object element) {
			if (element instanceof ElementStyleImpl) {
				ElementStyleImpl elementStyleImpl = (ElementStyleImpl) element;
				String tagName = elementStyleImpl.getTagName();
				if (tagName != null && "webobject".equalsIgnoreCase(tagName)) {
					if (image == null) {
						ImageDescriptor desc = HtmleditorPlugin
								.getImageDescriptor("icons/BindingOutline.gif");
						if (desc != null) {
							image = desc.createImage();
						}
					}
					if (image != null) {
						return image;
					}

				}
			}
			return baseLabelProvider.getImage(element);
		}

		public String getText(Object element) {
			if (element instanceof ElementStyleImpl) {
				ElementStyleImpl elementStyleImpl = (ElementStyleImpl) element;
				String tagName = elementStyleImpl.getTagName();
				if (tagName != null && "webobject".equalsIgnoreCase(tagName)) {
					String nameAttributeValue = elementStyleImpl
							.getAttribute("name");
					if (nameAttributeValue != null
							&& nameAttributeValue.length() > 0) {
						return nameAttributeValue;
					}
				}
			}
			return baseLabelProvider.getText(element);
		}

	}
}
