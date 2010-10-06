package org.objectstyle.wolips.variables;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;

import er.extensions.foundation.ERXValueUtilities;

public class ProjectVariables implements IPersistentPreferenceStore {
	private WOVariables _variables;
	private boolean _dirty;

	public ProjectVariables(WOVariables variables) {
		_variables = variables;
	}

	public WOVariables getWOVariables() {
		return _variables;
	}

	private IPath fixMissingSeparatorAfterDevice(String string) {
		IPath path;
		if (string != null && string.length() > 1 && string.charAt(1) == ':') {
			path = new Path(string.substring(2)).setDevice(string.substring(0, 2));
		} else if (string != null) {
			path = new Path(string);
		} else {
			path = null;
		}
		return path;
	}

	/**
	 * @return the path to the local root
	 */
	public IPath getLocalRoot() {
		return this.fixMissingSeparatorAfterDevice(_variables.localRoot());
	}

	/**
	 * @return the path to the local root
	 */
	public IPath getLocalFrameworkPatb() {
		return this.fixMissingSeparatorAfterDevice(_variables.localFrameworkPath());
	}

	/**
	 * @return the path to the system root
	 */
	public IPath getSystemRoot() {
		return this.fixMissingSeparatorAfterDevice(_variables.systemRoot());
	}

	/**
	 * @return the path to the system root
	 */
	public IPath getSystemFrameworkPath() {
		return this.fixMissingSeparatorAfterDevice(_variables.systemFrameworkPath());
	}

	/**
	 * @return the path to the network root
	 */
	public IPath getNetworkRoot() {
		return this.fixMissingSeparatorAfterDevice(_variables.networkRoot());
	}

	/**
	 * @return the path to the network root
	 */
	public IPath getNetworkFrameworkPath() {
		return this.fixMissingSeparatorAfterDevice(_variables.networkFrameworkPath());
	}

	/**
	 * @return the path to the user home
	 */
	public IPath getUserRoot() {
		return this.fixMissingSeparatorAfterDevice(_variables.userRoot());
	}

	/**
	 * @return the path to the user home
	 */
	public IPath getUserFrameworkPath() {
		return this.fixMissingSeparatorAfterDevice(_variables.userFrameworkPath());
	}

	/**
	 * @return the path to the reference api
	 */
	public IPath getReferenceApi() {
		String referenceApi = _variables.referenceApi();
		if (referenceApi == null) {
			return null;
		}
		return this.fixMissingSeparatorAfterDevice(referenceApi);
	}

	/**
	 * @return the path to the reference api
	 */
	public String getReferenceApiAsJavaDocCompatibleString() {
		IPath referenceApi = this.getReferenceApi();
		if (referenceApi == null) {
			return null;
		}
		String referenceApiString = referenceApi.toOSString();
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") >= 0) {
			referenceApiString = referenceApiString.replace('\\', '/');
		}
		referenceApiString = "file://" + referenceApiString;
		return referenceApiString;
	}

	/**
	 * @return the path to external build root
	 */
	public IPath getExternalBuildRoot() {
		String root = _variables.externalBuildRoot();
		if (root != null) {
			IPath result = this.fixMissingSeparatorAfterDevice(root);
			return result;
		}
		return null;
	}

	/**
	 * @return the path to external build root
	 */
	public IPath getExternalBuildFrameworkPath() {
		String root = _variables.externalBuildFrameworkPath();
		if (root != null) {
			IPath result = this.fixMissingSeparatorAfterDevice(root);
			return result;
		}
		return null;
	}

//	public String getProperty(String key) {
//		return _variables.getProperty(key);
//	}
//
//	public String getProperty(String key, String defaultValue) {
//		String value =  _variables.getProperty(key);
//		if (value == null) {
//			value = defaultValue;
//		}
//		return value;
//	}
//	
//	public void setProperty(String key, String value) {
//		_variables.setProperty(key, value);
//	}
//	
//	public void setDefaultProperty(String key, String value) {
//		_variables.setProperty(key, value);
//	}
//
//	public boolean getBooleanProperty(String key) {
//		return getBooleanProperty(key, false);
//	}
//
//	public boolean getBooleanProperty(String key, boolean defaultValue) {
//		return "true".equals(getProperty(key, String.valueOf(defaultValue)));
//	}

	private List<IPropertyChangeListener> _listeners = new LinkedList<IPropertyChangeListener>();
	
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		_listeners.add(listener);
	}

	public boolean contains(String name) {
		return getString(name) != null;
	}

	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(this, name, oldValue, newValue);
		for (IPropertyChangeListener listener : _listeners) {
			listener.propertyChange(event);
		}
	}

	public boolean getBoolean(String name) {
		return ERXValueUtilities.booleanValue(getString(name));
	}

	public boolean getBoolean(String name, boolean defaultValue) {
		return ERXValueUtilities.booleanValueWithDefault(getString(name), defaultValue);
	}

	public boolean getDefaultBoolean(String name) {
		return ERXValueUtilities.booleanValue(getDefaultString(name));
	}

	public double getDefaultDouble(String name) {
		return ERXValueUtilities.doubleValue(getDefaultString(name));
	}

	public float getDefaultFloat(String name) {
		return ERXValueUtilities.floatValue(getDefaultString(name));
	}

	public int getDefaultInt(String name) {
		return ERXValueUtilities.intValue(getDefaultString(name));
	}

	public long getDefaultLong(String name) {
		return ERXValueUtilities.longValue(getDefaultString(name));
	}

	public String getDefaultString(String name) {
		return _variables.getDefault(name);
	}

	public double getDouble(String name) {
		return ERXValueUtilities.doubleValue(getString(name));
	}

	public float getFloat(String name) {
		return ERXValueUtilities.floatValue(getString(name));
	}

	public int getInt(String name) {
		return ERXValueUtilities.intValue(getString(name));
	}

	public long getLong(String name) {
		return ERXValueUtilities.longValue(getString(name));
	}

	public String getString(String name, String defaultValue) {
		String value = getString(name);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	public String getString(String name) {
		return _variables.getProperty(name);
	}

	public boolean isDefault(String name) {
		return ComparisonUtils.equals(getDefaultString(name), getString(name), false);
	}

	public boolean needsSaving() {
		return _dirty;
	}

	public void putValue(String name, String value) {
		_variables.setProperty(name, value);
		_dirty = true;
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		_listeners.remove(listener);
	}

	public void setDefault(String name, double value) {
		setDefault(name, String.valueOf(value));
	}

	public void setDefault(String name, float value) {
		setDefault(name, String.valueOf(value));
	}

	public void setDefault(String name, int value) {
		setDefault(name, String.valueOf(value));
	}

	public void setDefault(String name, long value) {
		setDefault(name, String.valueOf(value));
	}

	public void setDefault(String name, String defaultObject) {
		_variables.setDefault(name, defaultObject);
		_dirty = true;
	}

	public void setDefault(String name, boolean value) {
		setDefault(name, String.valueOf(value));
	}

	public void setToDefault(String name) {
		setValue(name, getDefaultString(name));
	}

	public void setValue(String name, double value) {
		setValue(name, String.valueOf(value));
	}

	public void setValue(String name, float value) {
		setValue(name, String.valueOf(value));
	}

	public void setValue(String name, int value) {
		setValue(name, String.valueOf(value));
	}

	public void setValue(String name, long value) {
		setValue(name, String.valueOf(value));
	}

	public void setValue(String name, String value) {
		_variables.setProperty(name, value);
		_dirty = true;
	}

	public void setValue(String name, boolean value) {
		setValue(name, String.valueOf(value));
	}

	public void save() throws IOException {
		_variables.save();
		_dirty = false;
	}
}
