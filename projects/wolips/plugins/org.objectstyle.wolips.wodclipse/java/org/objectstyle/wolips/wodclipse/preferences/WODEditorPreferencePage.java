package org.objectstyle.wolips.wodclipse.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;

public class WODEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public WODEditorPreferencePage() {
    super(GRID);
    setPreferenceStore(WodclipsePlugin.getDefault().getPreferenceStore());
    setDescription("WOD Editor Preferences");
  }

  public void createFieldEditors() {
    addField(new ColorFieldEditor(PreferenceConstants.COMPONENT_NAME, "Component Name Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.COMPONENT_TYPE, "Component Type Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.ASSOCIATION_NAME, "Association Name Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.ASSOCIATION_VALUE, "Association Value Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.CONSTANT_ASSOCIATION_VALUE, "Constant Association Value Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.OPERATOR, "Operator Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.UNKNOWN, "Unknown Color", getFieldEditorParent()));
  }

  public void init(IWorkbench workbench) {
  }

}