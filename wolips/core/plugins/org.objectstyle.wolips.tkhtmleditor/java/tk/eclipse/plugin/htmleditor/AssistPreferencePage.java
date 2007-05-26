package tk.eclipse.plugin.htmleditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page to configure content assistant settings.
 * 
 * @author Naoki Takezoe
 */
public class AssistPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button checkCloseTag;
	private Button checkEnableAutoActivation;
	private Text   textAutoActivationChars;
	private Text   textAutoActivationDelay;
	
	public AssistPreferencePage() {
		super(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.CodeAssist")); //$NON-NLS-1$
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
  protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		
		checkCloseTag = new Button(composite,SWT.CHECK);
		checkCloseTag.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AssistCloseTag"));
		checkCloseTag.setLayoutData(createGridData(2));
		
		checkEnableAutoActivation = new Button(composite,SWT.CHECK);
		checkEnableAutoActivation.setText(HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AutoActivation"));
		checkEnableAutoActivation.setLayoutData(createGridData(2));
		checkEnableAutoActivation.addSelectionListener(
				new SelectionAdapter(){
					@Override
          public void widgetSelected(SelectionEvent evt){
						if(checkEnableAutoActivation.getSelection()){
							textAutoActivationChars.setEnabled(true);
							textAutoActivationDelay.setEnabled(true);
						} else {
							textAutoActivationChars.setEnabled(false);
							textAutoActivationDelay.setEnabled(false);
						}
						setValid(doValidate());
					}
				}
		);
		
		createLabel(composite,HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AutoActivationTrigger"));
		textAutoActivationChars = new Text(composite,SWT.BORDER);
		textAutoActivationChars.setLayoutData(createTextGridData());
		
		createLabel(composite,HTMLPlugin.getResourceString("HTMLEditorPreferencePage.AutoActivationDelay"));
		textAutoActivationDelay = new Text(composite,SWT.BORDER);
		textAutoActivationDelay.setLayoutData(createTextGridData());
		textAutoActivationDelay.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e){
					setValid(doValidate());
				}
		});
		
		// set initial values
		performDefaults();
		
		return composite;
	}
	
	private boolean doValidate(){
		if(checkEnableAutoActivation.getSelection()){
			try {
				Integer.parseInt(textAutoActivationDelay.getText());
			} catch(Exception ex){
				setErrorMessage(HTMLPlugin.createMessage(
						HTMLPlugin.getResourceString("Error.Numeric"),
						new String[]{
								HTMLPlugin.getResourceString("HTMLEditorPreferencePage.Message.AutoActivationDelay")
						}));
				return false;
			}
		}
		setErrorMessage(null);
		return true;
	}
	
	private GridData createTextGridData(){
		GridData gd = new GridData();
		gd.widthHint = 50;
		return gd;
	}
	
	private GridData createGridData(int span){
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		return gd;
	}
	
	private void createLabel(Composite parent,String text){
		Label label = new Label(parent,SWT.NULL);
		label.setText(text);
	}
	
	public void init(IWorkbench workbench) {
	}
	
	@Override
  public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(HTMLPlugin.PREF_ASSIST_CLOSE ,checkCloseTag.getSelection());
		store.setValue(HTMLPlugin.PREF_ASSIST_AUTO  ,checkEnableAutoActivation.getSelection());
		store.setValue(HTMLPlugin.PREF_ASSIST_CHARS ,textAutoActivationChars.getText());
		store.setValue(HTMLPlugin.PREF_ASSIST_TIMES ,textAutoActivationDelay.getText());
		return true;
	}
	
	@Override
  protected void performDefaults() {
		IPreferenceStore store = getPreferenceStore();
		checkCloseTag.setSelection(store.getBoolean(HTMLPlugin.PREF_ASSIST_CLOSE));
		checkEnableAutoActivation.setSelection(store.getBoolean(HTMLPlugin.PREF_ASSIST_AUTO));
		textAutoActivationChars.setText(store.getString(HTMLPlugin.PREF_ASSIST_CHARS));
		textAutoActivationDelay.setText(store.getString(HTMLPlugin.PREF_ASSIST_TIMES));
		
		if(checkEnableAutoActivation.getSelection()){
			textAutoActivationChars.setEnabled(true);
			textAutoActivationDelay.setEnabled(true);
		} else {
			textAutoActivationChars.setEnabled(false);
			textAutoActivationDelay.setEnabled(false);
		}
	}

}
