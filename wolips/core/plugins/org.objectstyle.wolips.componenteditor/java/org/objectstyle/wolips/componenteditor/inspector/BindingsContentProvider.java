package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.TypeCache;

public class BindingsContentProvider implements IStructuredContentProvider {
	private IJavaProject _javaProject;

	private TypeCache _cache;

	public void setContext(IJavaProject javaProject, TypeCache cache) {
		_javaProject = javaProject;
		_cache = cache;
	}

	public Object[] getElements(Object inputElement) {
		Object[] wodBindings = null;
		if (inputElement instanceof IWodElement) {
			IWodElement wodElement = (IWodElement) inputElement;
			if (wodElement == null) {
				wodBindings = new IWodBinding[0];
			} else {
				boolean apiFound = false;
				if (_cache != null) {
					try {
						Wo api = wodElement.getApi(_javaProject, _cache);
						if (api != null) {
							apiFound = true;
							wodBindings = api.getBindings();
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
				if (!apiFound) {
					wodBindings = wodElement.getBindings().toArray();
				}
			}
		} else {
			wodBindings = new IWodBinding[0];
		}
		return wodBindings;
	}

	public void dispose() {
		// DO NOTHING
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// DO NOTHING
	}
}
