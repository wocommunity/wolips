package tk.eclipse.plugin.htmleditor;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;

/**
 * The preference page for the <code>XMLEditor</code>.
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class XMLPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private IWorkbench _workbench;
	private Button _enableClassName;
	private Button spacesAroundEquals;
	private Button _stickyWOTags;
	private List _classNameAttrs;
	private Button _addClassName;
	private Button _removeClassName;
	
	public XMLPreferencePage() {
		super(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.XML"));
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
		//setDescription(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.XML"));
	}
	
	/**
	 * Creates contents of the preference page.
	 * 
	 * @param parent the parent <code>Composite</code>
	 * @retrun the created <code>Control</code> which contains contents.
	 */
	@Override
  protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		
		// checkbox to toggle spaces around equals
	      boolean spacesAroundEqualsValue =Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SPACES_AROUND_EQUALS);
		spacesAroundEquals = new Button(composite, SWT.CHECK);
		spacesAroundEquals.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.SpacesAroundEquals"));
		spacesAroundEquals.setSelection(spacesAroundEqualsValue);
		spacesAroundEquals.addSelectionListener(new SelectionAdapter(){
			@Override
     	public void widgetSelected(SelectionEvent e){
				Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.SPACES_AROUND_EQUALS, spacesAroundEquals.getSelection());
			}
		});
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		spacesAroundEquals.setLayoutData(gd);
		
    // checkbox to toggle spaces around equals
		boolean stickyWoTagsValue = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.STICKY_WOTAGS);
		_stickyWOTags = new Button(composite, SWT.CHECK);
		_stickyWOTags.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.StickyWOTags"));
		_stickyWOTags.setSelection(stickyWoTagsValue);
		_stickyWOTags.addSelectionListener(new SelectionAdapter(){
		  @Override
		  public void widgetSelected(SelectionEvent e){
		    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.STICKY_WOTAGS, _stickyWOTags.getSelection());
		  }
		});
		gd = new GridData();
		gd.horizontalSpan = 2;
		_stickyWOTags.setLayoutData(gd);

		
		
		// checkbox to toggle the classname support
		_enableClassName = new Button(composite, SWT.CHECK);
		_enableClassName.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.EnableClassName"));
		_enableClassName.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent e){
				updateControls();
			}
		});
		gd.horizontalSpan = 2;
		
		_enableClassName.setLayoutData(gd);
		
		
		// listbox
		_classNameAttrs = new List(composite, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		_classNameAttrs.setLayoutData(new GridData(GridData.FILL_BOTH));
		_classNameAttrs.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent e){
				updateControls();
			}
		});
		
		Composite buttons = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		_addClassName = new Button(buttons, SWT.PUSH);
		_addClassName.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.SpacesAroundEquals"));
		_addClassName.setLayoutData(createButtonGridData());
		_addClassName.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent e){
				InputDialog dialog = new InputDialog(
						_workbench.getActiveWorkbenchWindow().getShell(), 
						HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.Title"), 
						HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.Message"), 
						"",
						new IInputValidator(){
							public String isValid(String newText) {
								return newText.length()==0 ? 
										HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Dialog.Error") : null;
							}
				});
				if(dialog.open()==InputDialog.OK){
					_classNameAttrs.add(dialog.getValue());
				}
			}
		});
		
		_removeClassName = new Button(buttons, SWT.PUSH);
		_removeClassName.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.RemoveAttribute"));
		_removeClassName.setLayoutData(createButtonGridData());
		_removeClassName.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent e){
				_classNameAttrs.remove(_classNameAttrs.getSelectionIndices());
			}
		});
		
		// fill initial values
		IPreferenceStore store = getPreferenceStore();
		_enableClassName.setSelection(
				store.getBoolean(HTMLPlugin.PREF_ENABLE_CLASSNAME));
		String[] values = StringConverter.asArray(
				store.getString(HTMLPlugin.PREF_CLASSNAME_ATTRS));
		for(int i=0;i<values.length;i++){
			_classNameAttrs.add(values[i]);
		}
		
		updateControls();
		return composite;
	}
	
	/**
	 * Updates controls status.
	 */
	private void updateControls(){
		boolean enableClassName = this._enableClassName.getSelection();
		_classNameAttrs.setEnabled(enableClassName);
		_addClassName.setEnabled(enableClassName);
		_removeClassName.setEnabled(enableClassName);
		if(enableClassName){
			_removeClassName.setEnabled(_classNameAttrs.getSelectionCount()>0);
		}
	}
	
	/**
	 * Creates the <code>GridData</code> for buttons.
	 * 
	 * @return the <code>GridData</code> which is configured for buttons
	 */
	private static GridData createButtonGridData(){
		GridData gd = new GridData();
		gd.widthHint = 120;
		return gd;
	}

	/**
	 * Initializes the preference page.
	 * 
	 * @param workbench the <code>IWorkbench</code> instance
	 */
	public void init(IWorkbench workbench) {
		this._workbench = workbench;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
  protected void performDefaults() {
		IPreferenceStore store = getPreferenceStore();
		_enableClassName.setSelection(
				store.getDefaultBoolean(HTMLPlugin.PREF_ENABLE_CLASSNAME));
		String[] values = StringConverter.asArray(
				store.getDefaultString(HTMLPlugin.PREF_CLASSNAME_ATTRS));
		_classNameAttrs.removeAll();
		for(int i=0;i<values.length;i++){
			_classNameAttrs.add(values[i]);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
  public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(HTMLPlugin.PREF_ENABLE_CLASSNAME, _enableClassName.getSelection());
		
		String[] items = _classNameAttrs.getItems();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<items.length;i++){
			if(i!=0){
				sb.append(" ");
			}
			sb.append(items[i]);
		}
		store.setValue(HTMLPlugin.PREF_CLASSNAME_ATTRS, sb.toString());
		
		return true;
	}

}
