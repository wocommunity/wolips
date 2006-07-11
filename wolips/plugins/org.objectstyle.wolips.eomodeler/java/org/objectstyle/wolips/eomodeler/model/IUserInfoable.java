package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;

import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public interface IUserInfoable {
  public void setUserInfo(Map _userInfo);
  
  public NotificationMap getUserInfo();
}
