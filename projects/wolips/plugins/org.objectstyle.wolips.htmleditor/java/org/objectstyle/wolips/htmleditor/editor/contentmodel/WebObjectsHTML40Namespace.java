package org.objectstyle.wolips.htmleditor.editor.contentmodel;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;

/**
 * Adds a constant for the WebObject tag name.
 * 
 * @author mschrag
 */
public interface WebObjectsHTML40Namespace extends HTML40Namespace {
  public static interface ElementName extends HTML40Namespace.ElementName {
    public static final String WEBOBJECT = "WEBOBJECT"; //$NON-NLS-1$
  }

}
