package org.objectstyle.wolips.jdt.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.jdt.PluginImages;

public class WOPackageExplorerLabelProvider extends PackageExplorerLabelProvider {
	private Image _eomodelImage;

	private Image _wocomponentImage;

	public WOPackageExplorerLabelProvider(PackageExplorerContentProvider cp) {
		super(cp);
	}

	@Override
	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			if (WOPackageExplorerContentProvider.isBundle(folder)) {
				String name = folder.getName();
				if (name.endsWith(".eomodeld")) {
					if (_eomodelImage == null) {
						_eomodelImage = PluginImages.createImageDescriptor("images/EOModelBundle.png").createImage(false);
					}
					image = _eomodelImage;
				} else if (name.endsWith(".wo")) {
					if (_wocomponentImage == null) {
						_wocomponentImage = PluginImages.createImageDescriptor("images/WOComponentBundle.png").createImage(false);
					}
					image = _wocomponentImage;
				}
			}
		}
		if (image == null) {
			image = super.getImage(element);
		}
		return image;
	}
}
