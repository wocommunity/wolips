package org.objectstyle.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.Messages;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.utils.NotificationMap;

public class EODatabaseConfig extends EOModelObject<EOModel> {
	public static final String JDBC_ADAPTOR_NAME = "JDBC";
	
	public static final String JNDI_ADAPTOR_NAME = "JNDI";
	
	public static final String NONE_AUTHENTICATION_METHOD = "None";

	public static final String SIMPLE_AUTHENTICATION_METHOD = "Simple";

	public static final String OBJECT_SCOPE = "Object";
	
	public static final String SUBTREE_SCOPE = "Subtree";
	
	public static final String ONE_LEVEL_SCOPE = "One Level";
	
	public static final String USERNAME = "username";

	public static final String PASSWORD = "password";

	public static final String URL = "URL";

	public static final String DRIVER = "driver";

	public static final String PLUGIN = "plugin";

	public static final String CONNECTION_DICTIONARY = "connectionDictionary";

	public static final String NAME = "name";

	public static final String PROTOTYPE = "prototype";

	public static final String ADAPTOR_NAME = "adaptorName";

	public static final String AUTHENTICATION_METHOD = "authenticationMethod";

	public static final String INITIAL_CONTEXT_FACTORY = "initialContextFactory";

	public static final String SCOPE = "scope";

	public static final String TIMEOUT = "timeout";

	private EOModel myModel;

	private String myAdaptorName;

	private String myName;

	private String myPrototypeName;

	private EOEntity myCachedPrototype;

	private NotificationMap<Object, Object> myConnectionDictionary;

	private PropertyChangeRepeater myConnectionDictionaryRepeater;

	private EOModelMap myDatabaseConfigMap;

	public EODatabaseConfig() {
		myConnectionDictionaryRepeater = new PropertyChangeRepeater(EODatabaseConfig.CONNECTION_DICTIONARY);
		myDatabaseConfigMap = new EOModelMap();
		myAdaptorName = EODatabaseConfig.JDBC_ADAPTOR_NAME;
		setConnectionDictionary(new NotificationMap<Object, Object>(), false);
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

	public boolean isEquivalent(EODatabaseConfig config, boolean comparePrototypes) {
		boolean equivalent = false;
		if (config != null) {
			equivalent = (myConnectionDictionary != null && config.myConnectionDictionary != null);
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myAdaptorName, config.myAdaptorName);
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("URL"), config.myConnectionDictionary.get("URL"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("username"), config.myConnectionDictionary.get("username"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("password"), config.myConnectionDictionary.get("password"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("plugin"), config.myConnectionDictionary.get("plugin"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("driver"), config.myConnectionDictionary.get("driver"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("serverUrl"), config.myConnectionDictionary.get("serverUrl"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("initialContextFactory"), config.myConnectionDictionary.get("initialContextFactory"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("authenticationMethod"), config.myConnectionDictionary.get("authenticationMethod"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("plugInClassName"), config.myConnectionDictionary.get("plugInClassName"));
			}
			if (equivalent) {
				equivalent = ComparisonUtils.equals(myConnectionDictionary.get("scope"), config.myConnectionDictionary.get("scope"));
			}
			if (equivalent && comparePrototypes) {
				equivalent = myPrototypeName == null || config.myPrototypeName == null || myPrototypeName.length() == 0 || config.myPrototypeName.length() == 0 || ComparisonUtils.equals(myPrototypeName, config.myPrototypeName);
			}
		}
		return equivalent;
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

	public void setAdaptorName(String _adaptorName) {
		String oldAdaptorName = myAdaptorName;
		myAdaptorName = _adaptorName;
		if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(myAdaptorName)) {
			if (getPlugin() == null || getPlugin().length() == 0) {
				setPlugin("com.webobjects.jndiadaptor.LDAPPlugIn");
			}
			if (getInitialContextFactory() == null || getInitialContextFactory().length() == 0) {
				setInitialContextFactory("com.sun.jndi.ldap.LdapCtxFactory");
			}
			if (getTimeout() == null) {
				setTimeout(new Integer(3600));
			}
			if (getScope() == null || getScope().length() ==0) {
				setScope(EODatabaseConfig.SUBTREE_SCOPE);
			}
			if (getAuthenticationMethod() == null || getAuthenticationMethod().length() ==0) {
				setAuthenticationMethod(EODatabaseConfig.NONE_AUTHENTICATION_METHOD);
			}
		}
		firePropertyChange(EODatabaseConfig.ADAPTOR_NAME, oldAdaptorName, myAdaptorName);
	}

	public String getAdaptorName() {
		return myAdaptorName;
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
		if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(myAdaptorName)) {
			getConnectionDictionary().put("plugInClassName", _plugin);
		}
		else {
			getConnectionDictionary().put(EODatabaseConfig.PLUGIN, _plugin);
		}
	}

	public String getPlugin() {
		String plugin;
		if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(myAdaptorName)) {
			plugin = (String) getConnectionDictionary().get("plugInClassName");
		}
		else {
			plugin = (String) getConnectionDictionary().get(EODatabaseConfig.PLUGIN);
		}
		return plugin;
	}

	public void setDriver(String _driver) {
		getConnectionDictionary().put(EODatabaseConfig.DRIVER, _driver);
	}

	public String getDriver() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.DRIVER);
	}

	public void setURL(String _url) {
		if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(myAdaptorName)) {
			getConnectionDictionary().put("serverUrl", _url);
		}
		else {
			getConnectionDictionary().put(EODatabaseConfig.URL, _url);
		}
	}

	public String getURL() {
		String url;
		if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(myAdaptorName)) {
			url = (String) getConnectionDictionary().get("serverUrl");
		}
		else {
			url = (String) getConnectionDictionary().get(EODatabaseConfig.URL);
		}
		return url;
	}

	public void setTimeout(Integer _timeout) {
		getConnectionDictionary().put(EODatabaseConfig.TIMEOUT, _timeout);
	}

	public Integer getTimeout() {
		return (Integer) getConnectionDictionary().get(EODatabaseConfig.TIMEOUT);
	}

	public void setInitialContextFactory(String _initialContextFactory) {
		getConnectionDictionary().put(EODatabaseConfig.INITIAL_CONTEXT_FACTORY, _initialContextFactory);
	}

	public String getInitialContextFactory() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.INITIAL_CONTEXT_FACTORY);
	}

	public void setAuthenticationMethod(String _authenticationMethod) {
		getConnectionDictionary().put(EODatabaseConfig.AUTHENTICATION_METHOD, _authenticationMethod);
	}

	public String getAuthenticationMethod() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.AUTHENTICATION_METHOD);
	}

	public void setScope(String _scope) {
		getConnectionDictionary().put(EODatabaseConfig.SCOPE, _scope);
	}

	public String getScope() {
		return (String) getConnectionDictionary().get(EODatabaseConfig.SCOPE);
	}

	public void setConnectionDictionary(Map<Object, Object> _connectionDictionary) {
		setConnectionDictionary(_connectionDictionary, true);
	}

	public void setConnectionDictionary(Map<Object, Object> _connectionDictionary, boolean _fireEvents) {
		Map oldConnectionDictionary = myConnectionDictionary;
		if (_connectionDictionary != null) {
			Object password = _connectionDictionary.get(EODatabaseConfig.PASSWORD);
			if (password != null && !(password instanceof String)) {
				_connectionDictionary.put(EODatabaseConfig.PASSWORD, String.valueOf(password));
			}
		}
		myConnectionDictionary = mapChanged(myConnectionDictionary, _connectionDictionary, myConnectionDictionaryRepeater, false);
		if (_fireEvents) {
			firePropertyChange(myConnectionDictionaryRepeater.getPropertyName(), oldConnectionDictionary, myConnectionDictionary);
		}
	}

	public Map<Object, Object> getConnectionDictionary() {
		return myConnectionDictionary;
	}

	public String getFullyQualifiedName() {
		return ((myModel == null) ? "?" : myModel.getFullyQualifiedName()) + "/dbconfig: " + myName;
	}

	@SuppressWarnings("unused")
	public void resolve(Set<EOModelVerificationFailure> _failures) {
		// DO NOTHING
	}

	@SuppressWarnings("unused")
	public void verify(Set<EOModelVerificationFailure> _failures) {
		// DO NOTHING
	}
	
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	@SuppressWarnings("unused")
	public void loadFromMap(EOModelMap _map, Set<EOModelVerificationFailure> _failures) {
		myDatabaseConfigMap = _map;
		myAdaptorName = _map.getString("adaptorName", true);
		if (myAdaptorName == null) {
			myAdaptorName = EODatabaseConfig.JDBC_ADAPTOR_NAME;
		}
		myPrototypeName = _map.getString("prototypeEntityName", true);
		setConnectionDictionary(_map.getMap("connectionDictionary", true), false);
	}

	public EOModelMap toMap() {
		EOModelMap modelMap = myDatabaseConfigMap.cloneModelMap();
		if (myAdaptorName == null) {
			modelMap.setString("adaptorName", EODatabaseConfig.JDBC_ADAPTOR_NAME, true);
		} else {
			modelMap.setString("adaptorName", myAdaptorName, true);
		}
		modelMap.setString("prototypeEntityName", myPrototypeName, true);
		Map connectionDictionary = new HashMap(getConnectionDictionary());
		if (EODatabaseConfig.JNDI_ADAPTOR_NAME.equals(myAdaptorName)) {
			connectionDictionary.remove("URL");
			connectionDictionary.remove("plugin");
			connectionDictionary.remove("driver");
		}
		else if (EODatabaseConfig.JDBC_ADAPTOR_NAME.equals(myAdaptorName) || myAdaptorName == null) {
			connectionDictionary.remove("serverUrl");
			connectionDictionary.remove("plugInClassName");
			connectionDictionary.remove("initialContextFactory");
			connectionDictionary.remove("authenticationMethod");
			connectionDictionary.remove("scope");
			connectionDictionary.remove("timeout");
		}
		modelMap.setMap("connectionDictionary", connectionDictionary, true);
		return modelMap;
	}

	@Override
	public EODatabaseConfig _cloneModelObject() {
		EODatabaseConfig databaseConfig = new EODatabaseConfig(myName);
		databaseConfig.myAdaptorName = myAdaptorName;
		databaseConfig.myPrototypeName = myPrototypeName;
		databaseConfig.setConnectionDictionary(new HashMap<Object, Object>(myConnectionDictionary));
		return databaseConfig;
	}

	@Override
	public Class<EOModel> _getModelParentType() {
		return EOModel.class;
	}
	
	public EOModel _getModelParent() {
		return getModel();
	}
	
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getModel().removeDatabaseConfig(this);
	}
	
	public void _addToModelParent(EOModel modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedDatabaseConfigName(getName()));
		}
		modelParent.addDatabaseConfig(this);
	}
}
