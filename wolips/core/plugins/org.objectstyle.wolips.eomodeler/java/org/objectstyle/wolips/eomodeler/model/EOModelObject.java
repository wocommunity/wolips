/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.objectstyle.wolips.eomodeler.utils.IPropertyChangeSource;
import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public abstract class EOModelObject<T> implements IAdaptable, IPropertyChangeSource {
	private PropertyChangeSupport myPropertyChangeSupport = new PropertyChangeSupport(this);

	public EOModelObject() {
		myPropertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener _listener) {
		myPropertyChangeSupport.addPropertyChangeListener(_listener);
	}

	public void addPropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
		myPropertyChangeSupport.addPropertyChangeListener(_propertyName, _listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener _listener) {
		myPropertyChangeSupport.removePropertyChangeListener(_listener);
	}

	public void removePropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
		myPropertyChangeSupport.removePropertyChangeListener(_propertyName, _listener);
	}

	protected void firePropertyChange(String _propertyName, Object _oldValue, Object _newValue) {
		if (_oldValue == null || _newValue == null || !_oldValue.equals(_newValue)) {
			myPropertyChangeSupport.firePropertyChange(_propertyName, _oldValue, _newValue);
			_propertyChanged(_propertyName, _oldValue, _newValue);
		}
	}

	public abstract Set<EOModelVerificationFailure> getReferenceFailures();

	protected abstract void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue);

	public Object getAdapter(Class _adapter) {
		return null;
	}

	public abstract String getFullyQualifiedName();

	public String _findUnusedName(String newName, String getMethodName) {
		try {
			Method getMethod = getClass().getMethod(getMethodName, String.class);
			int cutoffLength;
			for (cutoffLength = newName.length(); cutoffLength > 0; cutoffLength --) {
				if (!Character.isDigit(newName.charAt(cutoffLength - 1))) {
					break;
				}
			}
			String newWithoutTrailingNumber = newName.substring(0, cutoffLength);
			boolean unusedNameFound = (getMethod.invoke(this, newWithoutTrailingNumber) == null);
			String unusedName = newWithoutTrailingNumber;
			for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
				unusedName = newWithoutTrailingNumber + dupeNameNum;
				Object existingObject = getMethod.invoke(this, unusedName);
				unusedNameFound = (existingObject == null);
			}
			return unusedName;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to find unused name for '" + newName + "' with method '" + getMethodName + "'.", t);
		}
	}

	public abstract String getName();
	
	public abstract EOModelObject<T> _cloneModelObject();

	public abstract Class<T> _getModelParentType();

	public abstract T _getModelParent();

	public abstract void _removeFromModelParent(Set<EOModelVerificationFailure> failures) throws EOModelException;

	public abstract void _addToModelParent(T modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException;

	protected NotificationMap<Object, Object> mapChanged(NotificationMap<Object, Object> _oldMap, Map<Object, Object> _newMap, PropertyChangeRepeater _propertyChangeRepeater, boolean _fireEvents) {
		NotificationMap<Object, Object> newMap;
		if (_oldMap != null) {
			_oldMap.removePropertyChangeListener(_propertyChangeRepeater);
		}
		if (_newMap instanceof NotificationMap) {
			newMap = (NotificationMap<Object, Object>) _newMap;
		} else {
			newMap = new NotificationMap<Object, Object>(_newMap);
		}
		newMap.addPropertyChangeListener(_propertyChangeRepeater);
		if (_fireEvents) {
			firePropertyChange(_propertyChangeRepeater.getPropertyName(), _oldMap, newMap);
		}
		return newMap;
	}

	protected class PropertyChangeRepeater implements PropertyChangeListener {
		private String myPropertyName;

		public PropertyChangeRepeater(String _propertyName) {
			myPropertyName = _propertyName;
		}

		public String getPropertyName() {
			return myPropertyName;
		}

		public void propertyChange(PropertyChangeEvent _event) {
			EOModelObject.this.firePropertyChange(myPropertyName, null, null);
		}
	}

}
