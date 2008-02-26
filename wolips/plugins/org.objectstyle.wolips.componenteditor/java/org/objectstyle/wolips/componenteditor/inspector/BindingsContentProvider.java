package org.objectstyle.wolips.componenteditor.inspector;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.TypeCache;

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
			if (wodElement == null) {
				wodBindings = new IWodBinding[0];
			} else {
				boolean apiFound = false;
				if (_cache != null) {
					try {
						_api = wodElement.getApi(_javaProject, _cache);
						if (_api != null) {
							apiFound = true;
							List<IApiBinding> visibleBindings = new LinkedList<IApiBinding>();
							List<Binding> apiBindings = _api.getBindings();
							visibleBindings.addAll(apiBindings);
							for (IWodBinding wodBinding : wodElement.getBindings()) {
								String bindingName = wodBinding.getName();
								boolean wodBindingDefinedInApi = false; 
								for (IApiBinding apiBinding : apiBindings) {
									if (apiBinding.getName().equals(bindingName)) {
										wodBindingDefinedInApi = true;
										break;
									}
								}
								if (!wodBindingDefinedInApi) {
									visibleBindings.add(wodBinding);
								}
							}
							wodBindings = visibleBindings.toArray();
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
