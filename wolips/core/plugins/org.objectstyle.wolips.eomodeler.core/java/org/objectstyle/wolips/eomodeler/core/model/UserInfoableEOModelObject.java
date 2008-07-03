package org.objectstyle.wolips.eomodeler.core.model;

import java.util.HashMap;
import java.util.Map;

import org.objectstyle.wolips.eomodeler.core.utils.NotificationMap;

import com.uwyn.rife.tools.ObjectUtils;

public abstract class UserInfoableEOModelObject<T> extends EOModelObject<T> implements IUserInfoable, Cloneable {
	public static final String ENTITY_MODELER_KEY = "_EntityModeler";

	public static final String DOCUMENTATION_KEY = "documentation";

	public static final String USER_INFO = "userInfo";

	private PropertyChangeRepeater _userInfoRepeater;

	private NotificationMap<Object, Object> _userInfo;

	public UserInfoableEOModelObject() {
		_userInfoRepeater = new PropertyChangeRepeater(UserInfoableEOModelObject.USER_INFO);
		setUserInfo(new NotificationMap<Object, Object>(), false);
	}

	public NotificationMap<Object, Object> getUserInfo() {
		return _userInfo;
	}

	public void setUserInfo(Map<Object, Object> userInfo) {
		setUserInfo(userInfo, true);
	}

	public void setUserInfo(Map<Object, Object> userInfo, boolean fireEvents) {
		_userInfo = mapChanged(_userInfo, userInfo, _userInfoRepeater, fireEvents);
	}
	
	public void userInfoChanged(String path, Object oldValue, Object newValue) {
		firePropertyChange(UserInfoableEOModelObject.USER_INFO + "." + path, oldValue, newValue);
	}

	public EOModelMap getEntityModelerMap(boolean readWrite) {  
		NotificationMap<Object, Object> userInfo = getUserInfo();
		Map entityModelerMap = (Map) userInfo.get(UserInfoableEOModelObject.ENTITY_MODELER_KEY);
		if (entityModelerMap == null) {
			entityModelerMap = new HashMap<Object, Object>();
			if (readWrite) {
				userInfo.put(UserInfoableEOModelObject.ENTITY_MODELER_KEY, entityModelerMap);
			}
		}
		return new EOModelMap(entityModelerMap);
	}
	
	/**
	 * Sets the documentation field on this object.  documentation writes
	 * into userInfo=>_EntityModeler=>Documentation.
	 * 
	 * @param documentation the documentation for this object
	 */
	public void setDocumentation(String documentation) {
		String oldDocumentation = getDocumentation();
		EOModelMap entityModelerMap = getEntityModelerMap(true);
		if (documentation == null) {
			entityModelerMap.remove(UserInfoableEOModelObject.DOCUMENTATION_KEY);
		} else {
			entityModelerMap.put(UserInfoableEOModelObject.DOCUMENTATION_KEY, documentation);
		}
		firePropertyChange(UserInfoableEOModelObject.DOCUMENTATION_KEY, oldDocumentation, documentation);
	}

	/**
	 * Returns the documentation for this object.
	 * 
	 * @return the documentation for this object
	 */
	public String getDocumentation() {
		EOModelMap entityModelerMap = getEntityModelerMap(false);
		String documentation = (String) entityModelerMap.get(UserInfoableEOModelObject.DOCUMENTATION_KEY);
		return documentation;
	}

	protected void writeUserInfo(EOModelMap modelMap) {
		EOModelMap entityModelerMap = getEntityModelerMap(false);
		if (entityModelerMap.isEmpty()) {
			getUserInfo().remove(UserInfoableEOModelObject.ENTITY_MODELER_KEY);
		} else {
			getUserInfo().put(UserInfoableEOModelObject.ENTITY_MODELER_KEY, entityModelerMap);
		}

		modelMap.setMap("userInfo", _userInfo, true);
		modelMap.remove("userDictionary");
	}

	protected void loadUserInfo(EOModelMap modelMap) {
		if (modelMap.containsKey("userDictionary")) {
			setUserInfo(modelMap.getMap("userDictionary", true), false);
		} else {
			setUserInfo(modelMap.getMap("userInfo", true), false);
		}
	}

	protected void _cloneUserInfoInto(UserInfoableEOModelObject<T> obj) {
		try {
			obj.setUserInfo(ObjectUtils.deepClone(_userInfo));
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Failed to clone user info: " + obj + ".", e);
		}
	}
}
