package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;

public class BindingsContentProvider implements IStructuredContentProvider {
	private IJavaProject _javaProject;

	private TypeCache _cache;

	private Wo _api;

	public void setContext(IJavaProject javaProject, TypeCache cache) {
		_javaProject = javaProject;
		_cache = cache;
	}

	public Wo getApi() {
		return _api;
	}

	public Object[] getElements(Object inputElement) {
		Object[] wodBindings = null;
		_api = null;
		if (inputElement instanceof IWodElement) {
			IWodElement wodElement = (IWodElement) inputElement;
			if (_cache != null && _api == null) {
				try {
					_api = wodElement.getApi(_javaProject, _cache);
				} catch (Exception e) {
					_api = null;
					ComponenteditorPlugin.getDefault().log("Failed to load API for WO.", e);
				}
			}
			wodBindings = wodElement.getApiBindings(_api);
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
