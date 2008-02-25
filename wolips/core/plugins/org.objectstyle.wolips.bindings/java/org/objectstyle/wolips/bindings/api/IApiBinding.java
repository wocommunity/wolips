package org.objectstyle.wolips.bindings.api;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.wod.TypeCache;

public interface IApiBinding {
  public static final String ACTIONS_DEFAULT = "Actions";
  public static final String[] ALL_DEFAULTS = new String[] { "Undefined", IApiBinding.ACTIONS_DEFAULT, "Boolean", "YES/NO", "Date Format Strings", "Number Format Strings", "MIME Types", "Direct Actions", "Direct Action Classes", "Page Names", "Frameworks", "Resources" };

  public String getName();

  public int getSelectedDefaults();

  public String getDefaults();

  public boolean isRequired();

  public boolean isWillSet();

  public String[] getValidValues(String partialValue, IJavaProject javaProject, IType componentType, TypeCache typeCache) throws JavaModelException;
}
