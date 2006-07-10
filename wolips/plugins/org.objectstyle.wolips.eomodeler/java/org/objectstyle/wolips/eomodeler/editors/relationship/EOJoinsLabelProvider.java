package org.objectstyle.wolips.eomodeler.editors.relationship;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.model.EOJoin;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOJoinsLabelProvider extends TablePropertyLabelProvider {
  public EOJoinsLabelProvider(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Image getColumnImage(Object _element, String _property) {
    return null;
  }

  public String getColumnText(Object _element, String _property) {
    EOJoin join = (EOJoin) _element;
    String text = null;
    if (_property == EOJoin.SOURCE_ATTRIBUTE_NAME) {
      text = join.getSourceAttributeName();
    }
    else if (_property == EOJoin.DESTINATION_ATTRIBUTE_NAME) {
      text = join.getDestinationAttributeName();
    }
    if (text == null) {
      text = Messages.getString("EOJoin.selectAttribute"); //$NON-NLS-1$
    }
    return text;
  }

  public void addListener(ILabelProviderListener _listener) {
    // DO NOTHING
  }

  public void dispose() {
    // DO NOTHING
  }

  public boolean isLabelProperty(Object _element, String _property) {
    return true;
  }

  public void removeListener(ILabelProviderListener _listener) {
    // DO NOTHING
  }
}
