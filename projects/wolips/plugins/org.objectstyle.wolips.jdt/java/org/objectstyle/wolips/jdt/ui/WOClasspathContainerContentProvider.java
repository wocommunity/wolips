/*
 * Created on 01.03.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.jdt.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

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
	protected ArrayList allEntries = new ArrayList();
	private boolean isExported = false;

	/**
	 * @param containerEntry
	 */
	public WOClasspathContainerContentProvider(IClasspathEntry containerEntry) {
		super();
		IPath path = null;
		if (containerEntry == null || containerEntry.getPath() == null) {
			path =
				new Path(
					WOClasspathContainer.WOLIPS_CLASSPATH_CONTAINER_IDENTITY);
			for (int i = 0;
				i
					< WOClasspathContainer
						.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS
						.length;
				i++) {
				path =
					path.append(
						"/"
							+ WOClasspathContainer
								.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS[i]);
			}
		} else {
			isExported = containerEntry.isExported();
			path = containerEntry.getPath();
		}
		String[] rootsNames = VariablesPlugin.getDefault().getFrameworkRootsNames();
		IPath[] rootsPaths = VariablesPlugin.getDefault().getFrameworkRoots();
		roots = new WOClasspathContainerRoot[rootsNames.length];
		for (int i = 0; i < rootsNames.length; i++) {
			roots[i] =
				new WOClasspathContainerRoot(rootsNames[i], rootsPaths[i], path);
		}
	}

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
				entries = new WOClasspathContainerEntry[frameworks.length];
				for (int i = 0; i < frameworks.length; i++) {
					WOClasspathContainerEntry entry =
						new WOClasspathContainerEntry(
							this,
							containerPath,
							frameworks[i]);
					entries[i] = entry;
				}
			}
		}

		/**
		 * @return
		 */
		public WOClasspathContainerEntry[] getEntries() {
			return entries;
		}

		protected String getRoot() {
			return root;
		}
		
		protected IPath getContainerPath() {
			return containerPath;
		}

	}

	public final class WOClasspathContainerEntry {
		private WOClasspathContainerRoot root;
		private boolean checked = false;
		private String name;
		protected WOClasspathContainerEntry(
			WOClasspathContainerRoot root,
			IPath path,
			File framework) {
			this.root = root;
			if (path != null) {
				String[] segments = path.segments();
				name = framework.getName();
				//				cut off the .framework
				name = name.substring(0, name.length() - 10);
				for (int i = 0; i < segments.length; i++) {
					checked =
						(i > 0
							&& !allEntries.contains(name)
							&& segments[i].equals(name)
							&& this.exists(
								segments[i],
								(this.getRoot().getContainerPath())));
					if (checked) {
						i = segments.length;
						allEntries.add(name);
					}

				}
			}
		}

		private boolean exists(String framework, IPath rootPath) {
			File frameworkFile =
					new File(
							rootPath.toOSString(),
						framework + ".framework/Resources/Java");
				return frameworkFile.isDirectory();
		}
		/**
		 * @return
		 */
		public boolean isChecked() {
			return checked;
		}

		/**
		 * @param b
		 */
		protected void setChecked(boolean b) {
			checked = b;
		}

		protected String getName() {
			return name;
		}

		protected WOClasspathContainerRoot getRoot() {
			return root;
		}

	}

	private static final class WildcardFilenameFilter
		implements FilenameFilter {
		WildcardFilenameFilter(String prefix, String suffix) {
			_prefix = prefix;
			_suffix = suffix;
		}

		public boolean accept(File file, String name) {

			String lowerName = name.toLowerCase();

			return (
				((null == _prefix) || lowerName.startsWith(_prefix))
					&& ((null == _suffix) || lowerName.endsWith(_suffix)));
		}

		String _prefix;
		String _suffix;
	}

	private static final class WOFWFilenameFilter implements FilenameFilter {
		public boolean accept(File file, String name) {
			/*name.startsWith("Java") &&*/
			boolean candidate = name.endsWith(".framework");

			boolean result = false;

			if (candidate) {
				File resDir = new File(file, name + "/Resources/Java");
				if (resDir.exists()) {

					String jarFiles[] =
						resDir.list(new WildcardFilenameFilter(null, ".jar"));
					String zipFiles[] =
						resDir.list(new WildcardFilenameFilter(null, ".zip"));

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
		path =
			new Path(WOClasspathContainer.WOLIPS_CLASSPATH_CONTAINER_IDENTITY);
		if (this.getRoots() != null)
			for (int i = 0; i < this.getRoots().length; i++) {
				WOClasspathContainerRoot root = this.getRoots()[i];
				if (root.getEntries() != null)
					for (int j = 0; j < root.getEntries().length; j++) {
						if (root.getEntries() != null
							&& checkboxTreeViewer.getChecked(root.getEntries()[j])) {
							//path = path.append(root.root);
							path = path.append(root.getEntries()[j].getName());
						}
					}
			}
		return JavaCore.newContainerEntry(path, isExported);
	}

	/**
	 * @return
	 */
	public WOClasspathContainerRoot[] getRoots() {
		return roots;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof WOClasspathContainerRoot) {
			WOClasspathContainerEntry[] entries =
				((WOClasspathContainerRoot) parentElement).getEntries();
			/*
			for (int i = 0; i < entries.length; i++) {
			viewer.setChecked(entries[i], entries[i].isChecked());
			}*/
			return entries;
		}
		if (parentElement instanceof WOClasspathContainerContentProvider) {
			WOClasspathContainerRoot[] currentRoots =
				((WOClasspathContainerContentProvider) parentElement).getRoots();
			/*
			for (int i = 0; i < roots.length; i++) {
			viewer.add(roots[i], this.getChildren(roots[i]));
			}*/
			return currentRoots;
		}
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof WOClasspathContainerRoot)
			return this;
		if (element instanceof WOClasspathContainerEntry)
			return ((WOClasspathContainerEntry) element).getRoot();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof WOClasspathContainerContentProvider)
			return ((WOClasspathContainerContentProvider) element).getRoots() != null
				&& ((WOClasspathContainerContentProvider) element).getRoots().length > 0;
		if (element instanceof WOClasspathContainerRoot)
			return ((WOClasspathContainerRoot) element).getEntries() != null
				&& ((WOClasspathContainerRoot) element).getEntries().length > 0;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return this.getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.checkboxTreeViewer = (CheckboxTreeViewer) viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof WOClasspathContainerRoot)
			return PluginImages.WOFRAMEWORK_ROOT_IMAGE();
		if (element instanceof WOClasspathContainerEntry) {
			for (int i = 0;
				i
					< WOClasspathContainer
						.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS
						.length;
				i++) {
				if (WOClasspathContainer
					.WOLIPS_CLASSPATH_STANDARD_FRAMEWORKS[i]
					.equals(((WOClasspathContainerEntry) element).getName()))
					return PluginImages.WOSTANDARD_FRAMEWORK_IMAGE();
			}
			return PluginImages.WOFRAMEWORK_IMAGE();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof WOClasspathContainerRoot)
			return ((WOClasspathContainerRoot) element).getRoot();
		if (element instanceof WOClasspathContainerEntry)
			return ((WOClasspathContainerEntry) element).getName();

		return element.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

}
