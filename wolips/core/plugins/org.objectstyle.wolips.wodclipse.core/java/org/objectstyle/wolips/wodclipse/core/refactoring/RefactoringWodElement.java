package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.SimpleWodBinding;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class RefactoringWodElement {
	public static final String ELEMENT_NAME = "elementName";

	public static final String ELEMENT_TYPE = "elementType";

	private PropertyChangeSupport _propertyChange;

	private SimpleWodElement _wodElement;

	private WodParserCache _cache;

	public RefactoringWodElement(IWodElement element, WodParserCache cache) {
		_cache = cache;
		_wodElement = new SimpleWodElement(element);
		_propertyChange = new PropertyChangeSupport(this);
	}

	public SimpleWodElement getWodElement() {
		return _wodElement;
	}

	public RefactoringWodBinding setValueForBinding(String value, String name) throws CoreException, InvocationTargetException, InterruptedException {
		RefactoringWodBinding wodBinding = null;
		if (value == null || value.trim().length() == 0) {
			removeBindingNamed(name);
		} else {
			wodBinding = getBindingNamed(name);
			if (wodBinding != null) {
				wodBinding.setValue(value);
			} else {
				wodBinding = addBindingValueNamed(value, null, name);
			}
		}
		return wodBinding;
	}

	public void removeBindingNamed(String name) throws CoreException, InvocationTargetException, InterruptedException {
		IWodBinding existingBinding = _wodElement.getBindingNamed(name);
		if (existingBinding == null) {
			// IGNORE
		} else {
			RemoveBindingRefactoring.run(_wodElement, existingBinding, _cache, new NullProgressMonitor());
			_wodElement.removeBinding(existingBinding);
		}
	}

	public RefactoringWodBinding addBindingValueNamed(String value, String namespace, String name) throws CoreException, InvocationTargetException, InterruptedException {
		SimpleWodBinding binding = new SimpleWodBinding(namespace, name, value);
		_wodElement.addBinding(binding);
		RefactoringWodBinding refactoringBinding = new RefactoringWodBinding(_wodElement, binding, _cache);
		refactoringBinding._setValue(value);
		return refactoringBinding;
	}

	public RefactoringWodBinding getBindingNamed(String name) {
		IWodBinding binding = _wodElement.getBindingNamed(name);
		RefactoringWodBinding refactoringBinding = null;
		if (binding != null) {
			refactoringBinding = new RefactoringWodBinding(_wodElement, binding, _cache);
		}
		return refactoringBinding;
	}

	public void setElementName(String elementName) throws CoreException, InvocationTargetException, InterruptedException {
		String oldElementName = _wodElement.getElementName();
		if (!_wodElement.isInline()) {
			RenameElementsRefactoring.run(oldElementName, elementName, _cache, new NullProgressMonitor());
		}
		_wodElement.setElementName(elementName);
		_propertyChange.firePropertyChange(RefactoringWodElement.ELEMENT_NAME, oldElementName, elementName);
	}

	public String getElementName() {
		return _wodElement.getElementName();
	}

	public void setElementType(String elementType) throws CoreException, InvocationTargetException, InterruptedException {
		String oldElementType = _wodElement.getElementType();
		ChangeElementTypeRefactoring.run(elementType, _wodElement, _cache, new NullProgressMonitor());
		_wodElement.setElementType(elementType);
		_propertyChange.firePropertyChange(RefactoringWodElement.ELEMENT_TYPE, oldElementType, elementType);
	}

	public String getElementType() {
		return _wodElement.getElementType();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChange.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		_propertyChange.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_propertyChange.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		_propertyChange.removePropertyChangeListener(propertyName, listener);
	}
	
	public static String findUnusedBindingName(IWodElement element, String baseName) {
	  return StringUtils.findUnusedName("newBinding", element, "getBindingNamed");
	}
}
