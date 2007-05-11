package org.objectstyle.wolips.wodclipse.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.preferences.TableViewerSupport;
import org.objectstyle.wolips.wodclipse.core.Activator;

/**
 * The preference page to add / edit / remove BindingValidationRules.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.htmleditor.tasktag.IBindingValidationRuleDetector
 * @see tk.eclipse.plugin.htmleditor.tasktag.BindingValidationRule
 * @see tk.eclipse.plugin.htmleditor.HTMLProjectBuilder
 */
public class BindingValidationRulePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  private TableViewer _viewer;
  private List<BindingValidationRule> _model = new ArrayList<BindingValidationRule>();
  private List<BindingValidationRule> _oldModel = new ArrayList<BindingValidationRule>();

  public BindingValidationRulePreferencePage() {
    setTitle("Binding Validation Rules");
    setDescription("Binding validation rules let you specify the regex for components and the regexes for valid binding values on those components.  For instance, component .*\\.MDT.* and binding value ^localizer\\..* would say that any component with MDT in its name that uses a binding value starting with 'localizer\\.' automatically validates.");
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore() {
    return Activator.getDefault().getPreferenceStore();
  }

  @Override
  protected Control createContents(Composite parent) {
    TableViewerSupport support = new TableViewerSupport(_model, parent) {

      @Override
      protected void initTableViewer(TableViewer viewer) {
        Table table = viewer.getTable();

        TableColumn col1 = new TableColumn(table, SWT.LEFT);
        col1.setText("Component Regex");
        col1.setWidth(150);

        TableColumn col2 = new TableColumn(table, SWT.LEFT);
        col2.setText("Valid Binding Regex");
        col2.setWidth(150);
      }

      @Override
      protected Object doAdd() {
        BindingValidationRuleDialog dialog = new BindingValidationRuleDialog(getShell());
        if (dialog.open() == Dialog.OK) {
          return dialog.getBindingValidationRule();
        }
        return null;
      }

      @Override
      protected void doEdit(Object obj) {
        BindingValidationRule bindingValidationRule = (BindingValidationRule) obj;
        BindingValidationRuleDialog dialog = new BindingValidationRuleDialog(getShell(), bindingValidationRule);
        if (dialog.open() == Dialog.OK) {
          BindingValidationRule newElement = dialog.getBindingValidationRule();
          bindingValidationRule.setTypeRegex(newElement.getTypeRegex());
          bindingValidationRule.setValidBindingRegex(newElement.getValidBindingRegex());
        }
      }

      @Override
      protected ITableLabelProvider createLabelProvider() {
        return new ITableLabelProvider() {

          public Image getColumnImage(Object _tagShortcut, int columnIndex) {
            return null;
          }

          public String getColumnText(Object bindingValidationRule, int columnIndex) {
            switch (columnIndex) {
            case 0:
              return ((BindingValidationRule) bindingValidationRule).getTypeRegex();
            case 1:
              return ((BindingValidationRule) bindingValidationRule).getValidBindingRegex();
            default:
              return bindingValidationRule.toString();
            }
          }

          public void addListener(ILabelProviderListener listener) {
          }

          public void dispose() {
          }

          public boolean isLabelProperty(Object bindingValidationRule, String property) {
            return false;
          }

          public void removeListener(ILabelProviderListener listener) {
          }
        };
      }

    };

    _viewer = support.getTableViewer();
    _model.addAll(BindingValidationRule.fromPreferenceString(getPreferenceStore().getString(PreferenceConstants.BINDING_VALIDATION_RULES_KEY)));
    syncModels();
    _viewer.refresh();

    return support.getControl();
  }

  @Override
  protected void performDefaults() {
    _model.clear();
    _model.addAll(BindingValidationRule.fromPreferenceString(getPreferenceStore().getDefaultString(PreferenceConstants.BINDING_VALIDATION_RULES_KEY)));
    _viewer.refresh();
    processChange();
  }

  @Override
  public boolean performOk() {
    getPreferenceStore().setValue(PreferenceConstants.BINDING_VALIDATION_RULES_KEY, BindingValidationRule.toPreferenceString(_model));
    processChange();
    Activator.getDefault().savePluginPreferences();
    return true;
  }

  private void syncModels() {
    try {
      _oldModel.clear();
      for (int i = 0; i < _model.size(); i++) {
        _oldModel.add(_model.get(i).clone());
      }
    }
    catch (Exception ex) {
      Activator.getDefault().log(ex);
    }
  }

  public void init(IWorkbench workbench) {
  }

  private void processChange() {
    if (BindingValidationRule.hasChange(_oldModel, _model)) {
      syncModels();
      //      try {
      //        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      //        IProject[] projects = root.getProjects();
      //        for (int i = 0; i < projects.length; i++) {
      //          IProjectAdapter wolipsProjectAdaptor = (IProjectAdapter) projects[i].getAdapter(IProjectAdapter.class);
      //          if (wolipsProjectAdaptor != null) {
      //            projects[i].build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
      //          }
      //        }
      //      }
      //      catch (Exception ex) {
      //        Activator.getDefault().log(ex);
      //      }
    }
  }

  /**
   * The dialog to add / edit BindingValidationRules.
   */
  private class BindingValidationRuleDialog extends Dialog {
    private Text _typeRegex;
    private Text _validBindingRegex;
    private BindingValidationRule _bindingValidationRule;

    public BindingValidationRuleDialog(Shell parentShell) {
      super(parentShell);
      setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    public BindingValidationRuleDialog(Shell parentShell, BindingValidationRule bindingValidationRule) {
      super(parentShell);
      _bindingValidationRule = bindingValidationRule;
    }

    @Override
    protected Point getInitialSize() {
      Point size = super.getInitialSize();
      size.x = 300;
      return size;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      getShell().setText("Tag Shortcut");

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));
      composite.setLayout(new GridLayout(2, false));

      Label label = new Label(composite, SWT.NULL);
      label.setText("Component Regex");

      _typeRegex = new Text(composite, SWT.BORDER);
      if (_bindingValidationRule != null) {
        _typeRegex.setText(_bindingValidationRule.getTypeRegex());
      }
      _typeRegex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      label = new Label(composite, SWT.NULL);
      label.setText("Valid Binding Regex");

      _validBindingRegex = new Text(composite, SWT.BORDER);
      if (_bindingValidationRule != null) {
        _validBindingRegex.setText(_bindingValidationRule.getValidBindingRegex());
      }
      _validBindingRegex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      return composite;
    }

    @Override
    protected void okPressed() {
      if (_typeRegex.getText().length() == 0) {
        MessageDialog.openError(getParentShell(), "Component Regex Required", "You must set a component regex.");
        return;
      }
      if (_validBindingRegex.getText().length() == 0) {
        MessageDialog.openError(getParentShell(), "Valid Binding Regex Required", "You must set a valid binding regex.");
        return;
      }

      String typeRegex = _typeRegex.getText();
      String validBindingRegex = _validBindingRegex.getText();
      _bindingValidationRule = new BindingValidationRule(typeRegex, validBindingRegex);

      super.okPressed();
    }

    public BindingValidationRule getBindingValidationRule() {
      return _bindingValidationRule;
    }
  }

}
