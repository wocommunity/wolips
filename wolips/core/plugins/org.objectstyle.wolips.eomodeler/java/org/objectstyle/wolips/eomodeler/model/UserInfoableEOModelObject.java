package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;

import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public abstract class UserInfoableEOModelObject extends EOModelObject implements IUserInfoable {
  public static final String USER_INFO = "userInfo";

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

  protected void writeUserInfo(EOModelMap _modelMap) {
    _modelMap.setMap("userInfo", myUserInfo, true);
    _modelMap.remove("userDictionary");
  }

  protected void loadUserInfo(EOModelMap _modelMap) {
    if (_modelMap.containsKey("userDictionary")) {
      setUserInfo(_modelMap.getMap("userDictionary", true), false);
    }
    else {
      setUserInfo(_modelMap.getMap("userInfo", true), false);
    }
  }
}
