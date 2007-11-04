package org.objectstyle.wolips.jdt.ui;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.workingsets.ViewActionGroup;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class WOPackageExplorerPart extends PackageExplorerPart {
	@Override
	public PackageExplorerContentProvider createContentProvider() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		boolean showCUChildren = store.getBoolean(PreferenceConstants.SHOW_CU_CHILDREN);
		if (getRootMode() == ViewActionGroup.SHOW_PROJECTS) {
			return new WOPackageExplorerContentProvider(showCUChildren);
		}
		return new WOWorkingSetAwareContentProvider(showCUChildren, getWorkingSetModel());
	}
}
