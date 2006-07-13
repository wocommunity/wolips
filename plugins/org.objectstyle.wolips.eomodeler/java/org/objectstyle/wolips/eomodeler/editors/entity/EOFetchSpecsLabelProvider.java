package org.objectstyle.wolips.eomodeler.editors.entity;

import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOFetchSpecsLabelProvider extends TablePropertyLabelProvider {
  public EOFetchSpecsLabelProvider(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Image getColumnImage(Object _element, String _property) {
    EOFetchSpecification fetchSpec = (EOFetchSpecification) _element;
    Image image;
    if (_property == EOFetchSpecification.SHARES_OBJECTS) {
      image = yesNoImage(fetchSpec.isSharesObjects(), Activator.getDefault().getImageRegistry().get(Activator.CHECK_ICON), null, null);
    }
    else {
      image = super.getColumnImage(_element, _property);
    }
    return image;
  }

  public String getColumnText(Object _element, String _property) {
    String text;
    if (_property == EOFetchSpecification.SHARES_OBJECTS) {
      text = null;
    }
    else {
      text = super.getColumnText(_element, _property);
    }
    return text;
  }

}
