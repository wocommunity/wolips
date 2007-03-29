package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;

import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public interface IUserInfoable {
	public void setUserInfo(Map<Object, Object> _userInfo);

	public void setUserInfo(Map<Object, Object> _userInfo, boolean _fireEvents);

	public NotificationMap<Object, Object> getUserInfo();
}
