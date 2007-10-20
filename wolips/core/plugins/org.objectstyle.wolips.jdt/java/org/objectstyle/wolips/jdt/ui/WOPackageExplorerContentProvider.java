package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;

public class WOPackageExplorerContentProvider extends PackageExplorerContentProvider {
	public WOPackageExplorerContentProvider(boolean provideMembers) {
		super(provideMembers);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return super.getChildren(parentElement);
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
		return super.hasChildren(element);
	}

	public static boolean isBundle(IFolder folder) {
		String folderName = folder.getName();
		boolean bundle = folderName.endsWith(".eomodeld") || folderName.endsWith(".wo");
		return bundle;
	}
}
