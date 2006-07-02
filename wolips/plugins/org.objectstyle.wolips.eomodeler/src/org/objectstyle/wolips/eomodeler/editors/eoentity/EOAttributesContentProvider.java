package org.objectstyle.wolips.eomodeler.editors.eoentity;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOAttributesContentProvider implements IStructuredContentProvider {
  public Object[] getElements(Object _inputElement) {
    EOEntity entity = (EOEntity) _inputElement;
    List attributesList = entity.getAttributes();
    EOAttribute[] attributes = (EOAttribute[]) attributesList.toArray(new EOAttribute[attributesList.size()]);
    return attributes;
  }

  public void dispose() {
  }

  public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
  }
}
