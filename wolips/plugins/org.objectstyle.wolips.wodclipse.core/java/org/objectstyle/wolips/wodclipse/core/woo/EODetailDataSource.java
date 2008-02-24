package org.objectstyle.wolips.wodclipse.core.woo;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EODetailDataSource extends EODataSource {
  private String _detailKey;
  private String _masterClass;

  public EODetailDataSource(final EOModelGroup modelGroup) {
    super(modelGroup);
  }

  public String getDetailKey() {
    return _detailKey;
  }

  public void setDetailKey(final String detailKey) {
    _detailKey = detailKey;
  }

  public String getMasterClass() {
    return _masterClass;
  }

  public void setMasterClass(final String masterClass) {
    _masterClass = masterClass;
  }

  @Override
  public void loadFromMap(final EOModelMap map, final Set<EOModelVerificationFailure> failures) {
    String className = map.getString("class", true);
    if ("EODetailDataSource".equals(className)) {
      _detailKey = map.getString("detailKey", true);
      _masterClass = map.getString("masterClassDescription", true);
    }
    else {
      throw new IllegalArgumentException("Unmatched className '" + className + "' for class EODetailDataSource.");
    }
  }

  @Override
  public EOModelMap toMap() {
    EOModelMap modelMap = new EOModelMap();
    modelMap.setString("class", "EODetailDataSource", true);
    modelMap.setString("detailKey", _detailKey, true);
    modelMap.setString("masterClassDescription", _masterClass, true);
    return modelMap;
  }

}
