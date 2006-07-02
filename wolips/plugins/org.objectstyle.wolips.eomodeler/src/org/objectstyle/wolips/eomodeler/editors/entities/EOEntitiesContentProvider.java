package org.objectstyle.wolips.eomodeler.editors.entities;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class EOEntitiesContentProvider implements IStructuredContentProvider {
  public Object[] getElements(Object _inputElement) {
    EOModel model = (EOModel) _inputElement;
    List entitiesList = model.getEntities();
    EOEntity[] entities = (EOEntity[]) entitiesList.toArray(new EOEntity[entitiesList.size()]);
    return entities;
  }

  public void dispose() {
  }

  public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
  }
}
