package org.objectstyle.wolips.templateeditor;

public interface ITemplateValidationMarkerCreator {
  public void addMarker( int severity, int offset, int length, String message);
}
