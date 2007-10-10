package org.objectstyle.wolips.componenteditor.inspector;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.refactoring.RenameElementsRefactoring;

public class RefactoringElementModel {
	public static final String ELEMENT_NAME = "elementName";

	private PropertyChangeSupport _propertyChange;

	private String _elementName;

	private WodParserCache _cache;

	public RefactoringElementModel(IWodElement element, WodParserCache cache) {
		_cache = cache;
		_elementName = element.getElementName();
		_propertyChange = new PropertyChangeSupport(this);
	}

	public void setElementName(String elementName) {
		String oldElementName = _elementName;
		_elementName = elementName;
		try {
			RenameElementsRefactoring.run(oldElementName, elementName, _cache, new NullProgressMonitor());
			_propertyChange.firePropertyChange(RefactoringElementModel.ELEMENT_NAME, oldElementName, elementName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getElementName() {
		return _elementName;
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
