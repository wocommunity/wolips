package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;

public interface IConnectionDictionaryOwner {
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String URL = "URL";
  public static final String DRIVER = "driver";
  public static final String PLUGIN = "plugin";
  public static final String CONNECTION_DICTIONARY = "connectionDictionary";

  public void setUsername(String _userName);

  public String getUsername();

  public void setPassword(String _password);

  public String getPassword();

  public void setPlugin(String _plugin);

  public String getPlugin();

  public void setDriver(String _driver);

  public String getDriver();

  public void setURL(String _url);

  public String getURL();

  public Map getConnectionDictionary();
  
  public void setConnectionDictionary(Map _connectionDictionary);

  public void setConnectionDictionary(Map _connectionDictionary, boolean _fireEvents);
}
