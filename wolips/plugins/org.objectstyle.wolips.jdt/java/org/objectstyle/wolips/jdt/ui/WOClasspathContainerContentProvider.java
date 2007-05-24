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

import java.io.File;
import java.io.FilenameFilter;
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
import org.objectstyle.wolips.jdt.PluginImages;
import org.objectstyle.wolips.jdt.classpath.WOClasspathContainer;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WOClasspathContainerContentProvider implements ITreeContentProvider, ILabelProvider {
	private WOClasspathContainerRoot[] roots;

	private CheckboxTreeViewer checkboxTreeViewer;

	protected List<String> allEntries = new ArrayList<String>();

	private boolean isExported = false;

	/**
	 * @param containerEntry
	 */
	public WOClasspathContainerContentProvider(IClasspathEntry containerEntry) {
		super();
		IPath path = null;
		if (containerEntry == null || containerEntry.getPath() == null) {
			path = new Path(WOClasspathContainer.WOLIPS_CLASSPATH_CONTAINER_IDENTITY);
			for (int i = 0; i < WOClasspathContainer.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS.length; i++) {
				path = path.append("/" + WOClasspathContainer.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS[i]);
			}
		} else {
			this.isExported = containerEntry.isExported();
			path = containerEntry.getPath();
		}
		String[] rootsNames = VariablesPlugin.getDefault().getFrameworkRootsNames();
		IPath[] rootsPaths = VariablesPlugin.getDefault().getFrameworkRoots();
		this.roots = new WOClasspathContainerRoot[rootsNames.length];
		for (int i = 0; i < rootsNames.length; i++) {
			this.roots[i] = new WOClasspathContainerRoot(rootsNames[i], rootsPaths[i], path);
		}
	}

	/**
	 * @author ulrich
	 * 
	 * TODO To change the template for this generated type comment go to Window -
	 * Preferences - Java - Code Style - Code Templates
	 */
	public final class WOClasspathContainerRoot {
		private String root;

		private IPath rootPath;

		private IPath containerPath;

		private WOClasspathContainerEntry[] entries;

		protected WOClasspathContainerRoot(String root, IPath rootPath, IPath containerPath) {
			this.root = root;
			this.rootPath = rootPath;
			this.containerPath = containerPath;
			File fwBase = new File(rootPath.toOSString());
			if (fwBase.exists() && fwBase.isDirectory()) {
				File frameworks[] = fwBase.listFiles(new WOFWFilenameFilter());
				this.entries = new WOClasspathContainerEntry[frameworks.length];
				for (int i = 0; i < frameworks.length; i++) {
					WOClasspathContainerEntry entry = new WOClasspathContainerEntry(this, containerPath, frameworks[i]);
					this.entries[i] = entry;
				}
			}
		}

		/**
		 * @return
		 */
		public WOClasspathContainerEntry[] getEntries() {
			return this.entries;
		}

		protected String getRoot() {
			return this.root;
		}

		protected IPath getContainerPath() {
			return this.containerPath;
		}

		protected IPath getRootPath() {
			return this.rootPath;
		}

	}

	/**
	 * @author ulrich
	 * 
	 * TODO To change the template for this generated type comment go to Window -
	 * Preferences - Java - Code Style - Code Templates
	 */
	public final class WOClasspathContainerEntry {
		private WOClasspathContainerRoot root;

		private boolean checked = false;

		private String name;

		protected WOClasspathContainerEntry(WOClasspathContainerRoot root, IPath path, File framework) {
			this.root = root;
			if (path != null) {
				String[] segments = path.segments();
				this.name = framework.getName();
				// cut off the .framework
				this.name = this.name.substring(0, this.name.length() - 10);
				for (int i = 0; i < segments.length; i++) {
					this.checked = (i > 0 && !WOClasspathContainerContentProvider.this.allEntries.contains(this.name) && segments[i].equals(this.name) && this.exists(segments[i], (this.getRoot().getRootPath())));
					if (this.checked) {
						i = segments.length;
						WOClasspathContainerContentProvider.this.allEntries.add(this.name);
					}

				}
			}
		}

		private boolean exists(String framework, IPath rootPath) {
			IPath frameworkPath = rootPath.append(framework + ".framework").append("Resources").append("Java");
			File frameworkFile = new File(frameworkPath.toOSString());
			return frameworkFile.isDirectory();
		}

		/**
		 * @return
		 */
		public boolean isChecked() {
			return this.checked;
		}

		/**
		 * @param b
		 */
		protected void setChecked(boolean b) {
			this.checked = b;
		}

		protected String getName() {
			return this.name;
		}

		protected WOClasspathContainerRoot getRoot() {
			return this.root;
		}

	}

	private static final class WildcardFilenameFilter implements FilenameFilter {
		WildcardFilenameFilter(String prefix, String suffix) {
			this._prefix = prefix;
			this._suffix = suffix;
		}

		public boolean accept(File file, String name) {

			String lowerName = name.toLowerCase();

			return (((null == this._prefix) || lowerName.startsWith(this._prefix)) && ((null == this._suffix) || lowerName.endsWith(this._suffix)));
		}

		String _prefix;

		String _suffix;
	}

	static final class WOFWFilenameFilter implements FilenameFilter {
		public boolean accept(File file, String name) {
			/* name.startsWith("Java") && */
			boolean candidate = name.endsWith(".framework");

			boolean result = false;

			if (candidate) {
				File resDir = new File(file, name + "/Resources/Java");
				if (resDir.exists()) {

					String jarFiles[] = resDir.list(new WildcardFilenameFilter(null, ".jar"));
					String zipFiles[] = resDir.list(new WildcardFilenameFilter(null, ".zip"));

					result = (0 != jarFiles.length) || (0 != zipFiles.length);

				}
			}

			return (result);
		}
	}

	/**
	 * @return
	 */
	public IClasspathEntry getClasspathEntry() {
		IPath path = null;
		path = new Path(WOClasspathContainer.WOLIPS_CLASSPATH_CONTAINER_IDENTITY);
		if (this.getRoots() != null)
			for (int i = 0; i < this.getRoots().length; i++) {
				WOClasspathContainerRoot root = this.getRoots()[i];
				if (root.getEntries() != null)
					for (int j = 0; j < root.getEntries().length; j++) {
						if (root.getEntries() != null && this.checkboxTreeViewer.getChecked(root.getEntries()[j])) {
							// path = path.append(root.root);
							path = path.append(root.getEntries()[j].getName());
						}
					}
			}
		return JavaCore.newContainerEntry(path, this.isExported);
	}

	/**
	 * @return
	 */
	public WOClasspathContainerRoot[] getRoots() {
		return this.roots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof WOClasspathContainerRoot) {
			WOClasspathContainerEntry[] entries = ((WOClasspathContainerRoot) parentElement).getEntries();
			/*
			 * for (int i = 0; i < entries.length; i++) {
			 * viewer.setChecked(entries[i], entries[i].isChecked()); }
			 */
			return entries;
		}
		if (parentElement instanceof WOClasspathContainerContentProvider) {
			WOClasspathContainerRoot[] currentRoots = ((WOClasspathContainerContentProvider) parentElement).getRoots();
			/*
			 * for (int i = 0; i < roots.length; i++) { viewer.add(roots[i],
			 * this.getChildren(roots[i])); }
			 */
			return currentRoots;
		}
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof WOClasspathContainerRoot)
			return this;
		if (element instanceof WOClasspathContainerEntry)
			return ((WOClasspathContainerEntry) element).getRoot();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof WOClasspathContainerContentProvider)
			return ((WOClasspathContainerContentProvider) element).getRoots() != null && ((WOClasspathContainerContentProvider) element).getRoots().length > 0;
		if (element instanceof WOClasspathContainerRoot)
			return ((WOClasspathContainerRoot) element).getEntries() != null && ((WOClasspathContainerRoot) element).getEntries().length > 0;
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
		if (element instanceof WOClasspathContainerRoot)
			return PluginImages.WOFRAMEWORK_ROOT_IMAGE();
		if (element instanceof WOClasspathContainerEntry) {
			for (int i = 0; i < WOClasspathContainer.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS.length; i++) {
				if (WOClasspathContainer.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS[i].equals(((WOClasspathContainerEntry) element).getName()))
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
		if (element instanceof WOClasspathContainerRoot)
			return ((WOClasspathContainerRoot) element).getRoot();
		if (element instanceof WOClasspathContainerEntry)
			return ((WOClasspathContainerEntry) element).getName();

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

}