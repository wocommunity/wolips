package org.objectstyle.wolips.eomodeler.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public class EODatabaseConfig extends EOModelObject {
	public static final String USERNAME = "username";

	public static final String PASSWORD = "password";

	public static final String URL = "URL";

	public static final String DRIVER = "driver";

	public static final String PLUGIN = "plugin";

	public static final String CONNECTION_DICTIONARY = "connectionDictionary";

	public static final String NAME = "name";

	public static final String PROTOTYPE = "prototype";

	private EOModel myModel;

	private String myName;

	private String myPrototypeName;

	private EOEntity myCachedPrototype;

	private NotificationMap myConnectionDictionary;

	private PropertyChangeRepeater myConnectionDictionaryRepeater;

	private EOModelMap myDatabaseConfigMap;

	public EODatabaseConfig() {
		myConnectionDictionaryRepeater = new PropertyChangeRepeater(EODatabaseConfig.CONNECTION_DICTIONARY);
		myDatabaseConfigMap = new EOModelMap();
		setConnectionDictionary(new NotificationMap(), false);
	}

	public EODatabaseConfig(String _name) {
		this();
		myName = _name;
	}

	public boolean isActive() {
		return (myModel != null && myModel.getActiveDatabaseConfig() == this);
	}
	
	public void setActive() {
		if (myModel != null) {
			myModel.setActiveDatabaseConfig(this);
		}
	}
	
	public boolean equals(Object _obj) {
		return (_obj instanceof EODatabaseConfig && ComparisonUtils.equals(myName, ((EODatabaseConfig) _obj).myName));
	}

	public int hashCode() {
		return (myName == null) ? super.hashCode() : myName.hashCode();
	}
	
	public boolean isEquivalent(EODatabaseConfig _config) {
		boolean equivalent = false;
		if (_config != null) {
			equivalent = (myConnectionDictionary != null && _config.myConnectionDictionary != null);
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("URL"), _config.myConnectionDictionary.get("URL"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("username"), _config.myConnectionDictionary.get("username"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("password"), _config.myConnectionDictionary.get("password"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("plugin"), _config.myConnectionDictionary.get("plugin"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("driver"), _config.myConnectionDictionary.get("driver"));
			}
			if (equivalent) {
				equivalent = myPrototypeName == null || _config.myPrototypeName == null || myPrototypeName.length() == 0 || _config.myPrototypeName.length() == 0 || ComparisonUtils.equals(myPrototypeName, _config.myPrototypeName);
			}
		}
		return equivalent;
	}

	public EODatabaseConfig cloneDatabaseConfig() {
		EODatabaseConfig databaseConfig = new EODatabaseConfig(myName);
		databaseConfig.myPrototypeName = myPrototypeName;
		databaseConfig.setConnectionDictionary(new HashMap(myConnectionDictionary));
		return databaseConfig;
	}

	public void pasted() {
		// DO NOTHING
	}

	public void _setModel(EOModel _model) {
		myModel = _model;
	}

	public EOModel getModel() {
		return myModel;
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (myModel != null) {
			myModel._databaseConfigChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	public String getName() {
		return myName;
	}

	public void setName(String _name) throws DuplicateDatabaseConfigNameException {
		setName(_name, true);
	}

	public void setName(String _name, boolean _fireEvents) throws DuplicateDatabaseConfigNameException {
		if (_name == null) {
			throw new NullPointerException(Messages.getString("EODatabaseConfig.noBlankDatabaseConfigNames"));
		}
		String oldName = myName;
		if (myModel != null) {
			myModel._checkForDuplicateDatabaseConfigName(this, _name, null);
		}
		myName = _name;
		if (_fireEvents) {
			firePropertyChange(EODatabaseConfig.NAME, oldName, myName);
		}
	}

	public EOEntity getPrototype() {
		if (myCachedPrototype == null && myModel != null) {
			myCachedPrototype = myModel.getModelGroup().getEntityNamed(myPrototypeName);
		}
		return myCachedPrototype;
	}

	public void setPrototype(EOEntity _prototype) {
		EOEntity oldPrototype = getPrototype();
		if (_prototype == null) {
			myPrototypeName = null;
			myCachedPrototype = null;
		} else {
			myPrototypeName = _prototype.getName();
			myCachedPrototype = null;
		}
		EOEntity newPrototype = getPrototype();
		firePropertyChange(EODatabaseConfig.PROTOTYPE, oldPrototype, newPrototype);
	}

	public void setUsername(String _userName) {
		getConnectionDictionary().put(EODatabaseConfig.USERNAME, _userName);
	}

	public String getUsername() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.USERNAME);
	}

	public void setPassword(String _password) {
		getConnectionDictionary().put(EODatabaseConfig.PASSWORD, _password);
	}

	public String getPassword() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.PASSWORD);
	}

	public void setPlugin(String _plugin) {
		getConnectionDictionary().put(EODatabaseConfig.PLUGIN, _plugin);
	}

	public String getPlugin() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.PLUGIN);
	}

	public void setDriver(String _driver) {
		getConnectionDictionary().put(EODatabaseConfig.DRIVER, _driver);
	}

	public String getDriver() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.DRIVER);
	}

	public void setURL(String _url) {
		getConnectionDictionary().put(EODatabaseConfig.URL, _url);
	}

	public String getURL() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.URL);
	}

	public void setConnectionDictionary(Map _connectionDictionary) {
		setConnectionDictionary(_connectionDictionary, true);
	}

	public void setConnectionDictionary(Map _connectionDictionary, boolean _fireEvents) {
		Map oldConnectionDictionary = myConnectionDictionary;
		myConnectionDictionary = mapChanged(myConnectionDictionary, _connectionDictionary, myConnectionDictionaryRepeater, false);
		if (_fireEvents) {
			firePropertyChange(myConnectionDictionaryRepeater.getPropertyName(), oldConnectionDictionary, myConnectionDictionary);
		}
	}

	public Map getConnectionDictionary() {
		return myConnectionDictionary;
	}

	public String getFullyQualifiedName() {
		return ((myModel == null) ? "?" : myModel.getFullyQualifiedName()) + ", dbconfig: " + myName;
	}

	public void resolve(Set _failures) {
		// DO NOTHING
	}

	public void verify(Set _failures) {
		// DO NOTHING
	}

	public Set getReferenceFailures() {
		Set referenceFailures = new HashSet();
		return referenceFailures;
	}

	public void loadFromMap(EOModelMap _map, Set _failures) {
		myDatabaseConfigMap = _map;
		myPrototypeName = _map.getString("prototypeEntityName", true);
		setConnectionDictionary(_map.getMap("connectionDictionary", true), false);
	}

	public EOModelMap toMap() {
		EOModelMap modelMap = myDatabaseConfigMap.cloneModelMap();
		modelMap.setString("prototypeEntityName", myPrototypeName, true);
		modelMap.setMap("connectionDictionary", myConnectionDictionary, true);
		return modelMap;
	}
}
