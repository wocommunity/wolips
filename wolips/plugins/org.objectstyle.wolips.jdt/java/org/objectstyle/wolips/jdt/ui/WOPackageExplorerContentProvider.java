/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.jdt.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.objectstyle.wolips.jdt.ui.tags.ITaggedComponentsContentProvider;
import org.objectstyle.wolips.jdt.ui.tags.TaggedComponentsContentProvider;
import org.objectstyle.wolips.jdt.ui.tags.WOTagLibResourceChangeListener;

public class WOPackageExplorerContentProvider extends PackageExplorerContentProvider {

	private IResourceChangeListener resourceChangeListener;

	WOPackageExplorerPart woPackageExplorerPart;

	Map<IProject, TaggedComponentsContentProvider> taggedComponentsContentProviders = new HashMap<IProject, TaggedComponentsContentProvider>();

	public WOPackageExplorerContentProvider(boolean provideMembers, WOPackageExplorerPart woPackageExplorerPart) {
		super(provideMembers);
		this.woPackageExplorerPart = woPackageExplorerPart;
		this.resourceChangeListener = new WOTagLibResourceChangeListener() {

			@Override
			public void update(IProject project) {
				TaggedComponentsContentProvider taggedComponentsContentProvider = WOPackageExplorerContentProvider.this.taggedComponentsContentProviders.get(project);
				if (taggedComponentsContentProvider != null) {
					taggedComponentsContentProvider.forgetTagLib();
					WOPackageExplorerContentProvider.this.woPackageExplorerPart.getTreeViewer().refresh(taggedComponentsContentProvider);
				}
			}

		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ITaggedComponentsContentProvider) {
			ITaggedComponentsContentProvider taggedComponentsContentProvider = (ITaggedComponentsContentProvider) parentElement;
			return taggedComponentsContentProvider.getChildren();
		}
		Object[] children = super.getChildren(parentElement);
		if (parentElement instanceof IJavaProject) {
			if (children.length > 2) {
				Object[] newChildren = new Object[children.length + 1];
				IJavaProject javaProject = (IJavaProject) parentElement;
				IProject project = javaProject.getProject();
				TaggedComponentsContentProvider taggedComponentsContentProvider = taggedComponentsContentProviders.get(project);
				if (taggedComponentsContentProvider == null) {
					taggedComponentsContentProvider = new TaggedComponentsContentProvider(project);
					taggedComponentsContentProviders.put(project, taggedComponentsContentProvider);
				}
				Object[] tags = taggedComponentsContentProvider.getChildren();
				if (tags != null && tags.length > 0) {
					newChildren[0] = taggedComponentsContentProvider;
					for (int i = 0; i < children.length; i++) {
						Object object = children[i];
						newChildren[i + 1] = object;
					}
				}
				else {
					newChildren = children;
				}
				return newChildren;
			}
		}
		return children;
	}

	@Override
	protected Object[] getFolderContent(IFolder folder) throws CoreException {
		Object[] contents;
		if (WOPackageExplorerContentProvider.isBundle(folder)) {
			contents = NO_CHILDREN;
		} else {
			contents = super.getFolderContent(folder);
		}
		return contents;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ITaggedComponentsContentProvider) {
			ITaggedComponentsContentProvider taggedComponentsContentProvider = (ITaggedComponentsContentProvider) element;
			return taggedComponentsContentProvider.hasChildren();
		}
		return super.hasChildren(element);
	}

	public static boolean isBundle(IFolder folder) {
		String folderName = folder.getName();
		boolean bundle = folderName.endsWith(".eomodeld") || folderName.endsWith(".wo");
		return bundle;
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.resourceChangeListener);
		super.dispose();
	}
}
