package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Map;

import org.objectstyle.wolips.eomodeler.core.utils.NotificationMap;

public interface IUserInfoable {
	public void setUserInfo(Map<Object, Object> _userInfo);

	public void setUserInfo(Map<Object, Object> _userInfo, boolean _fireEvents);

	public NotificationMap<Object, Object> getUserInfo();
}
