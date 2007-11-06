package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.objectstyle.wolips.jdt.ui.tags.ITaggedComponentsContentProvider;
import org.objectstyle.wolips.jdt.ui.tags.TaggedComponentsContentProvider;

public class WOPackageExplorerContentProvider extends PackageExplorerContentProvider {
	public WOPackageExplorerContentProvider(boolean provideMembers) {
		super(provideMembers);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ITaggedComponentsContentProvider) {
			ITaggedComponentsContentProvider  taggedComponentsContentProvider = (ITaggedComponentsContentProvider)parentElement;
			return taggedComponentsContentProvider.getChildren();
		}
		Object[] children = super.getChildren(parentElement);
		if(parentElement instanceof IJavaProject) {
			if(children.length > 2) {
				Object[] newChildren = new Object[children.length + 1];
				IJavaProject javaProject = (IJavaProject)parentElement;
				IProject project = javaProject.getProject();
				newChildren[children.length] = new TaggedComponentsContentProvider(project);
				for (int i = 0; i < children.length; i++) {
					Object object = children[i];
					newChildren[i] = object;
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
		if(element instanceof ITaggedComponentsContentProvider) {
			ITaggedComponentsContentProvider taggedComponentsContentProvider = (ITaggedComponentsContentProvider)element;
			return taggedComponentsContentProvider.hasChildren();
		}
		return super.hasChildren(element);
	}

	public static boolean isBundle(IFolder folder) {
		String folderName = folder.getName();
		boolean bundle = folderName.endsWith(".eomodeld") || folderName.endsWith(".wo");
		return bundle;
	}
}
