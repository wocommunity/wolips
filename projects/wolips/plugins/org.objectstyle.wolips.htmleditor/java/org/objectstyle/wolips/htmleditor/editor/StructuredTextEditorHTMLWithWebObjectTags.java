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
package org.objectstyle.wolips.htmleditor.html;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.html.ui.internal.provisional.StructuredTextEditorHTML;
import org.eclipse.wst.sse.ui.internal.contentoutline.StructuredTextEditorContentOutlinePage;
import org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline.ContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.internal.view.events.NodeSelectionChangedEvent;
import org.objectstyle.wolips.htmleditor.HtmleditorPlugin;

public class StructuredTextEditorHTMLWithWebObjectTags extends
		StructuredTextEditorHTML {
	
	Image image;
	
	public StructuredTextEditorHTMLWithWebObjectTags() {
		super();
	}

	public Object getAdapter(Class required) {
		Object object = super.getAdapter(required);
		if (IContentOutlinePage.class.equals(required)) {
			StructuredTextEditorContentOutlinePage structuredTextEditorContentOutlinePage = (StructuredTextEditorContentOutlinePage) object;
			ContentOutlineConfiguration contentOutlineConfiguration = structuredTextEditorContentOutlinePage.getConfiguration();
			if(!(contentOutlineConfiguration instanceof ContentOutlineConfigurationWrapper)) {
				structuredTextEditorContentOutlinePage.setConfiguration(new ContentOutlineConfigurationWrapper(contentOutlineConfiguration));
			}
		}
		return object;
	}

	private class ContentOutlineConfigurationWrapper extends ContentOutlineConfiguration {
		private ContentOutlineConfiguration contentOutlineConfiguration;
		
		private WebObjectTagLabelProvider webObjectTagLabelProvider;
		
		public ContentOutlineConfigurationWrapper(ContentOutlineConfiguration contentOutlineConfiguration) {
			super();
			this.contentOutlineConfiguration = contentOutlineConfiguration;
		}
		

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			return contentOutlineConfiguration.getAdapter(adapter);
		}

		/**
		 * @param viewer
		 * @return the ITreeContentProvider to use with this viewer
		 */
		public IContentProvider getContentProvider(TreeViewer viewer) {
			return contentOutlineConfiguration.getContentProvider(viewer);
		}

		/**
		 * @return Returns the declaringID, useful for remembering settings.
		 */
		public String getDeclaringID() {
			return contentOutlineConfiguration.getDeclaringID();
		}

		/**
		 * @param viewer
		 * @return the IDoubleClickListener to be notified when the viewer
		 *         receives a double click event
		 */
		public IDoubleClickListener getDoubleClickListener(TreeViewer viewer) {
			return contentOutlineConfiguration.getDoubleClickListener(viewer);
		}

		/**
		 * 
		 * @param viewer
		 * @return an array of KeyListeners to attach to the TreeViewer's Control.
		 *         The listeners should adhere to the KeyEvent.doit field to
		 *         ensure proper behaviors. Ordering of the event notifications is
		 *         dependent on the Control in the TreeViewer.
		 */
		public KeyListener[] getKeyListeners(TreeViewer viewer) {
			return contentOutlineConfiguration.getKeyListeners(viewer);
		}

		/**
		 * @param viewer
		 * @return the ILabelProvider for items within the viewer
		 */
		public ILabelProvider getLabelProvider(TreeViewer viewer) {
			if(webObjectTagLabelProvider != null) {
				return webObjectTagLabelProvider;
			}
			ILabelProvider labelProvider = contentOutlineConfiguration.getLabelProvider(viewer);
			if(!(labelProvider instanceof WebObjectTagLabelProvider)) {
				webObjectTagLabelProvider = new WebObjectTagLabelProvider(labelProvider);
				return webObjectTagLabelProvider;
			}
			return labelProvider;
		}

		/**
		 * @param viewer
		 * @return IContributionItem[] for the local menu
		 */
		public IContributionItem[] getMenuContributions(TreeViewer viewer) {
			return contentOutlineConfiguration.getMenuContributions(viewer);
		}

		/**
		 * @param viewer
		 * @return the IMenuListener to notify when the viewer's context menu is
		 *         about to be show
		 */
		public IMenuListener getMenuListener(TreeViewer viewer) {
			return null;
		}

		/**
		 * @param nodes
		 * @return The list of nodes from this List that should be seen in the
		 *         Outline. Possible uses include programmatic selection setting.
		 */
		public List getNodes(List nodes) {
			return contentOutlineConfiguration.getNodes(nodes);
		}

		/**
		 * @param event
		 * @return The (filtered) list of selected nodes from this event. Uses
		 *         include mapping model selection onto elements provided by the
		 *         content provider. Should only return elements that will be
		 *         shown in the Tree Control.
		 */
		public List getSelectedNodes(NodeSelectionChangedEvent event) {
			return contentOutlineConfiguration.getSelectedNodes(event);
		}

		/**
		 * @param viewer
		 * @return the ISelectionChangedListener to notify when the viewer's
		 *         selection changes
		 */
		public ISelectionChangedListener getSelectionChangedListener(TreeViewer viewer) {
			return contentOutlineConfiguration.getSelectionChangedListener(viewer);
		}

		/**
		 * @param viewer
		 * @return IContributionItem[] for the local toolbar
		 */
		public IContributionItem[] getToolbarContributions(TreeViewer viewer) {
			return contentOutlineConfiguration.getToolbarContributions(viewer);
		}

		/**
		 * Adopted since you can't easily removeDragSupport from StructuredViewers
		 * 
		 * @param treeViewer
		 * @return
		 */
		public TransferDragSourceListener[] getTransferDragSourceListeners(TreeViewer treeViewer) {
			return contentOutlineConfiguration.getTransferDragSourceListeners(treeViewer);
		}

		/**
		 * Adopted since you can't easily removeDropSupport from StructuredViewers
		 * 
		 * @param treeViewer
		 * @return
		 */
		public TransferDropTargetListener[] getTransferDropTargetListeners(TreeViewer treeViewer) {
			return contentOutlineConfiguration.getTransferDropTargetListeners(treeViewer);
		}

		/**
		 * Should node selection changes affect selection in the TreeViewer?
		 * 
		 * @return
		 */
		public boolean isLinkedWithEditor(TreeViewer treeViewer) {
			return contentOutlineConfiguration.isLinkedWithEditor(treeViewer);
		}

		/**
		 * @param declaringID
		 *            The declaringID to set.
		 */
		public void setDeclaringID(String declaringID) {
			contentOutlineConfiguration.setDeclaringID(declaringID);
		}

		/**
		 * General hook for resource releasing and listener removal when
		 * configurations change or the viewer is disposed of
		 * 
		 * @param viewer
		 */
		public void unconfigure(TreeViewer viewer) {
			contentOutlineConfiguration.unconfigure(viewer);
		}
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
				          ImageDescriptor desc = HtmleditorPlugin.getImageDescriptor("icons/BindingOutline.gif");
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
					String nameAttributeValue = elementStyleImpl.getAttribute("name");
					if(nameAttributeValue != null && nameAttributeValue.length() > 0) {
						return nameAttributeValue;
					}
				}
			}
			return baseLabelProvider.getText(element);
		}

	}
}
