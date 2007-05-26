package tk.eclipse.plugin.htmleditor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public abstract class AbstractValidationDialog extends Dialog {
	
	private CLabel errorLabel;
	private static Image errorImage
		= HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_ERROR);
	
	protected AbstractValidationDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle()|SWT.RESIZE);
	}
	
	@Override
  protected Control createContents(Composite parent) {
		// create the top level composite for the dialog
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createDialogArea(composite);
		errorLabel = new CLabel(composite, SWT.NULL);
		errorLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonBar = createButtonBar(composite);
		
		validate();
		
		return composite;
	}
	
	protected abstract void validate();
	
	protected void add(Text text){
		text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent evt) {
				validate();
			}
		});
	}
	
	protected void setErrorMessage(String message){
		if(message==null || message.length()==0){
			errorLabel.setImage(null);
			errorLabel.setText("");
			getButton(OK).setEnabled(true);
		} else {
			errorLabel.setImage(errorImage);
			errorLabel.setText(message);
			getButton(OK).setEnabled(false);
		}
	}


}
