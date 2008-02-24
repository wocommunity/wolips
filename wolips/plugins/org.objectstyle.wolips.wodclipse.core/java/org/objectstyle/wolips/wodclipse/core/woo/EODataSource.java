package org.objectstyle.wolips.wodclipse.core.woo;

import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public abstract class EODataSource {
  private EOModelGroup _modelGroup;

  public EODataSource(final EOModelGroup modelGroup) {
    _modelGroup = modelGroup;
  }

  public abstract void loadFromMap(EOModelMap dataSource, Set<EOModelVerificationFailure> failures);

  public EOModelGroup getModelGroup() {
    return _modelGroup;
  }

  public void setmodelGroup(final EOModelGroup modelGroup) {
    _modelGroup = modelGroup;
  }

  public abstract EOModelMap toMap();

}
