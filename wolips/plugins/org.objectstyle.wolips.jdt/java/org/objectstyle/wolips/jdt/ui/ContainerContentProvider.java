/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.PluginImages;
import org.objectstyle.wolips.jdt.classpath.Container;
import org.objectstyle.wolips.jdt.classpath.ContainerEntries;
import org.objectstyle.wolips.jdt.classpath.ContainerEntry;
import org.objectstyle.wolips.jdt.classpath.PathCoderException;
import org.objectstyle.wolips.jdt.classpath.model.Framework;
import org.objectstyle.wolips.jdt.classpath.model.Root;

/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ContainerContentProvider implements ITreeContentProvider, ILabelProvider {
	private CheckboxTreeViewer checkboxTreeViewer;

	private Container container;

	/**
	 * 
	 */
	public ContainerContentProvider() {
		super();
		ContainerEntries containerEntries = null;
		try {
			containerEntries = ContainerEntries.initWithPath(new Path(Container.DEFAULT_PATH));
		} catch (PathCoderException e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
		}
		this.container = new Container(containerEntries);
	}

	/**
	 * @param containerEntry
	 */
	public ContainerContentProvider(IClasspathEntry containerEntry) {
		super();
		ContainerEntries containerEntries = null;
		try {
			IPath path = containerEntry.getPath();
			if (Container.CONTAINER_IDENTITY.equals(path.segment(0))) {
				path = path.removeFirstSegments(1);
			}
			containerEntries = ContainerEntries.initWithPath(path);
			this.pull(containerEntries);
		} catch (PathCoderException e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
		}
		this.container = new Container(containerEntries);
	}

	private void pull(ContainerEntries containerEntries) {
		Root[] roots = JdtPlugin.getDefault().getClasspathModel().getRoots();
		for (int i = 0; i < roots.length; i++) {
			Framework[] frameworks = roots[i].getEntries();
			if (frameworks != null)
				for (int j = 0; j < frameworks.length; j++) {
					ContainerEntry containerEntry = containerEntries.getEntry(frameworks[j]);
					if (containerEntry != null) {
						containerEntry.pull(frameworks[j]);
					}
				}
		}
	}

	private void setCheckedElements() {
		List<Framework> checked = new ArrayList<Framework>();
		Root[] roots = JdtPlugin.getDefault().getClasspathModel().getRoots();
		for (int i = 0; i < roots.length; i++) {
			Framework[] frameworks = roots[i].getEntries();
			if (frameworks != null)
				for (int j = 0; j < frameworks.length; j++) {
					if (this.container.contains(frameworks[j]))
						checked.add(frameworks[j]);
				}
		}
		this.checkboxTreeViewer.setCheckedElements(checked.toArray());

	}

	/**
	 * @return
	 */
	public IClasspathEntry getClasspathEntry() {
		List<Framework> checked = new ArrayList<Framework>();
		Root[] roots = JdtPlugin.getDefault().getClasspathModel().getRoots();
		for (int i = 0; i < roots.length; i++) {
			Framework[] frameworks = roots[i].getEntries();
			if (frameworks != null)
				for (int j = 0; j < frameworks.length; j++) {
					if (roots[i].getEntries() != null && this.checkboxTreeViewer.getChecked(roots[i].getEntries()[j])) {
						checked.add(roots[i].getEntries()[j]);
					}
				}
		}
		this.container.setContent(checked.toArray(new Framework[checked.size()]));
		return JavaCore.newContainerEntry(this.container.getPath(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Root) {
			Framework[] frameworks = ((Root) parentElement).getEntries();
			return frameworks;
		}
		if (parentElement instanceof ContainerContentProvider) {
			return JdtPlugin.getDefault().getClasspathModel().getRoots();
		}
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof Root)
			return this;
		if (element instanceof Framework)
			return ((Framework) element).getRoot();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof ContainerContentProvider)
			return JdtPlugin.getDefault().getClasspathModel().getRoots().length > 0;
		if (element instanceof Root)
			return ((Root) element).getEntries().length > 0;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return this.getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.checkboxTreeViewer = (CheckboxTreeViewer) viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof Root)
			return PluginImages.WOFRAMEWORK_ROOT_IMAGE();
		if (element instanceof Framework) {
			for (int i = 0; i < Container.STANDARD_FRAMEWORK_NAMES.length; i++) {
				if (Container.STANDARD_FRAMEWORK_NAMES[i].equals(((Framework) element).getName()))
					return PluginImages.WOSTANDARD_FRAMEWORK_IMAGE();
			}
			return PluginImages.WOFRAMEWORK_IMAGE();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof Root)
			return ((Root) element).getName();
		if (element instanceof Framework)
			return ((Framework) element).getName();
		return element.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		return;
	}

	/**
	 * @param checkboxTreeViewer
	 *            The checkboxTreeViewer to set.
	 */
	protected void setCheckboxTreeViewer(CheckboxTreeViewer checkboxTreeViewer) {
		this.checkboxTreeViewer = checkboxTreeViewer;
		this.setCheckedElements();
	}
}