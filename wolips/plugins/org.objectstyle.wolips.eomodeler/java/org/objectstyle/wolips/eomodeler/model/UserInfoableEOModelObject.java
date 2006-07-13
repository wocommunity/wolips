package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;

import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public class UserInfoableEOModelObject extends EOModelObject implements IUserInfoable {
  public static final String USER_INFO = "userInfo"; //$NON-NLS-1$

  private PropertyChangeRepeater myUserInfoRepeater;
  private NotificationMap myUserInfo;

  public UserInfoableEOModelObject() {
    myUserInfoRepeater = new PropertyChangeRepeater(UserInfoableEOModelObject.USER_INFO);
    setUserInfo(new NotificationMap(), false);
  }

  public NotificationMap getUserInfo() {
    return myUserInfo;
  }

  public void setUserInfo(Map _userInfo) {
    setUserInfo(_userInfo, true);
  }

  public void setUserInfo(Map _userInfo, boolean _fireEvents) {
    myUserInfo = mapChanged(myUserInfo, _userInfo, myUserInfoRepeater, _fireEvents);
  }
}
