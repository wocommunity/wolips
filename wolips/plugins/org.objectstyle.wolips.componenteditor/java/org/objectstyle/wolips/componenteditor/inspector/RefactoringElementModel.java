package org.objectstyle.wolips.componenteditor.inspector;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.ChangeElementTypeRefactoring;
import org.objectstyle.wolips.wodclipse.core.refactoring.RenameElementsRefactoring;

public class RefactoringElementModel {
	public static final String ELEMENT_NAME = "elementName";

	public static final String ELEMENT_TYPE = "elementType";

	private PropertyChangeSupport _propertyChange;

	private SimpleWodElement _wodElement;

	private WodParserCache _cache;

	public RefactoringElementModel(IWodElement element, WodParserCache cache) {
		_cache = cache;
		_wodElement = new SimpleWodElement(element);
		_propertyChange = new PropertyChangeSupport(this);
	}
	
	public SimpleWodElement getWodElement() {
		return _wodElement;
	}

	public void setElementName(String elementName) throws CoreException, InvocationTargetException, InterruptedException {
		String oldElementName = _wodElement.getElementName();
		if (!_wodElement.isTemporary()) {
			RenameElementsRefactoring.run(oldElementName, elementName, _cache, new NullProgressMonitor());
		}
		_wodElement.setElementName(elementName);
		_propertyChange.firePropertyChange(RefactoringElementModel.ELEMENT_NAME, oldElementName, elementName);
	}

	public String getElementName() {
		return _wodElement.getElementName();
	}

	public void setElementType(String elementType) throws CoreException, InvocationTargetException, InterruptedException {
		String oldElementType = _wodElement.getElementType();
		ChangeElementTypeRefactoring.run(elementType, _wodElement, _cache, new NullProgressMonitor());
		_wodElement.setElementType(elementType);
		_propertyChange.firePropertyChange(RefactoringElementModel.ELEMENT_TYPE, oldElementType, elementType);
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
}
