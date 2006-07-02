package org.objectstyle.wolips.eomodeler.editors.relationships;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.editors.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOJoin;
import org.objectstyle.wolips.eomodeler.model.EORelationship;

public class EORelationshipsLabelProvider extends TablePropertyLabelProvider implements ITableColorProvider, ITableFontProvider {
  public EORelationshipsLabelProvider(String[] _columnProperties) {
    super(_columnProperties);
  }

  public Image getColumnImage(Object _element, String _property) {
    EORelationship relationship = (EORelationship) _element;
    Image image = null;
    if (_property == EORelationshipsConstants.TO_MANY) {
      image = yesNoImage(relationship.isToMany(), Activator.getDefault().getImageRegistry().get(Activator.TO_MANY_ICON), Activator.getDefault().getImageRegistry().get(Activator.TO_ONE_ICON), Activator.getDefault().getImageRegistry().get(Activator.TO_ONE_ICON));
    }
    else if (_property == EORelationshipsConstants.CLASS_PROPERTY) {
      image = yesNoImage(relationship.isClassProperty(), Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON), null, null);
    }
    return image;
  }

  public String getColumnText(Object _element, String _property) {
    EORelationship relationship = (EORelationship) _element;
    String text = null;
    if (_property == EORelationshipsConstants.TO_MANY) {
      text = null;
    }
    else if (_property == EORelationshipsConstants.CLASS_PROPERTY) {
      text = null;
    }
    else if (_property == EORelationshipsConstants.NAME) {
      text = relationship.getName();
    }
    else if (_property == EORelationshipsConstants.DESTINATION) {
      EOEntity destination = relationship.getDestination();
      if (destination != null) {
        text = destination.getName();
      }
    }
    else if (_property == EORelationshipsConstants.SOURCE_ATTRIBUTE) {
      EOJoin firstJoin = relationship.getFirstJoin();
      if (firstJoin != null) {
        text = firstJoin.getSourceAttribute().getName();
      }
    }
    else if (_property == EORelationshipsConstants.DESTINATION_ATTRIBUTE) {
      EOJoin firstJoin = relationship.getFirstJoin();
      if (firstJoin != null) {
        text = firstJoin.getDestinationAttribute().getName();
      }
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return text;
  }

  public Font getFont(Object _element, int _columnIndex) {
    EORelationship relationship = (EORelationship) _element;
    return null;
  }

  public Color getBackground(Object _element, int _columnIndex) {
    EORelationship relationship = (EORelationship) _element;
    return null;
  }

  public Color getForeground(Object _element, int _columnIndex) {
    EORelationship relationship = (EORelationship) _element;
    return null;
  }

  public void addListener(ILabelProviderListener _listener) {
  }

  public void dispose() {
  }

  public boolean isLabelProperty(Object _element, String _property) {
    return true;
  }

  public void removeListener(ILabelProviderListener _listener) {
  }
}
