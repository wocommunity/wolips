package org.objectstyle.wolips.eomodeler.editors.relationship;

import org.objectstyle.wolips.eomodeler.model.EOJoin;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;

public class EOJoinsViewerSorter extends TablePropertyViewerSorter {
  public EOJoinsViewerSorter(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Object getComparisonValue(Object _obj, String _property) {
    EOJoin join = (EOJoin) _obj;
    Object value = null;
    if (_property == EOJoin.SOURCE_ATTRIBUTE) {
      value = join.getSourceAttribute().getName();
    }
    else if (_property == EOJoin.DESTINATION_ATTRIBUTE) {
      value = join.getDestinationAttribute().getName();
    }
    return value;
  }

}
