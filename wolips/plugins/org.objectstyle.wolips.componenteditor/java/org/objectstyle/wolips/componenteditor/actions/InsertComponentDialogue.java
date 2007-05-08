package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import org.eclipse.jface.dialogs.*;

/**
 * <P>This dialogue box is used to get the name of a
 * component that will be inserted both into the
 * template as well as the wod file.</P>
 */

public class InsertComponentDialogue extends org.eclipse.jface.dialogs.Dialog
{
	
	public final static int RESULT_CREATE = 1;
	public final static int RESULT_CANCEL = 2;
	
	// no time to localise :-(
	public final static String LABEL_COMPONENT_NAME = "Supply a name for your component:";

// user interface elements which the user interacts with.
	
	protected Text componentInstanceNameText = null;
	protected Label componentInstanceLabel = null;
	protected Button componentInstanceCreateButton = null;
	protected Button componentInstanceCancelButton = null;
	
	protected InsertComponentSpecification insertComponentSpecification = null;
	
	public InsertComponentDialogue(Shell parentShell,InsertComponentSpecification insertComponentSpecification)
	{
		super(parentShell);
		this.insertComponentSpecification = insertComponentSpecification;
	}
	
	protected Control createDialogArea(Composite parent)
	{
		Composite control = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1,true);
		
		control.setLayout(layout);
		
		componentInstanceLabel = new Label(control,0);
		componentInstanceLabel.setText(LABEL_COMPONENT_NAME);

		GridData componentInstanceNameData = new GridData(GridData.FILL_HORIZONTAL);
		componentInstanceNameText = new Text(control, SWT.BORDER);
		componentInstanceNameText.setLayoutData(componentInstanceNameData);
		componentInstanceNameText.addVerifyListener(new ComponentInstanceNameVerifyListener());
		
		if(null!=insertComponentSpecification.getComponentInstanceName())
			componentInstanceNameText.setText(insertComponentSpecification.getComponentInstanceName());
		else
		{
			if(null!=insertComponentSpecification.getComponentInstanceNameSuffix())
				componentInstanceNameText.setText(insertComponentSpecification.getComponentInstanceNameSuffix());
		}
		
		componentInstanceNameText.setSelection(0);
		
		return control;
	}
	
// these two methods handle buttons getting pressed.
	
	public void cancelPressed()
	{
		insertComponentSpecification.setComponentInstanceName(null);
		super.cancelPressed();
	}
	
	public void okPressed()
	{
		insertComponentSpecification.setComponentInstanceName(componentInstanceNameText.getText());
		super.okPressed();
	}
	
}
