package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.packageview.WorkingSetAwareContentProvider;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetModel;

public class WOWorkingSetAwareContentProvider extends WorkingSetAwareContentProvider {

	public WOWorkingSetAwareContentProvider(boolean provideMembers, WorkingSetModel model) {
		super(provideMembers, model);
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

}
