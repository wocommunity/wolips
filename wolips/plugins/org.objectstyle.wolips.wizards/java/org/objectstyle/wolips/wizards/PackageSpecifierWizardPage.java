package org.objectstyle.wolips.wizards;

import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardResourceImportPage;

/**
 * Used by WOLips project creation wizards to assign default packages for the template
 * Java classes.
 * @see NewWOProjectWizard
 */
/*
 * Subclasses WizardResourceImportPage due to the fact it's the easiest to
 * override UI elements.  All we need is to display a text field for configuring
 * a java package.
 */
public class PackageSpecifierWizardPage extends WizardResourceImportPage {
	StringButtonDialogField myPackageDialogField;
	Button _resetButton;
	
	protected PackageSpecifierWizardPage(String name, IStructuredSelection selection) {
		super(name, selection);
	}

	@Override
	protected void createSourceGroup(Composite parent) {
		Group packageGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		packageGroup.setLayout(layout);
		packageGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		StringButtonStatusFieldListener listener = new StringButtonStatusFieldListener();
		myPackageDialogField = new StringButtonDialogField(new ResetButtonAdaptor());
		myPackageDialogField.setDialogFieldListener(listener);
		myPackageDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label);
		myPackageDialogField.setButtonLabel("Clear");
//		myPackageDialogField.enableButton(false);
		_resetButton = myPackageDialogField.getChangeControl(parent);
		myPackageDialogField.doFillIntoGrid(packageGroup, 4);
		Text text = myPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(text);
	}

	@Override
	protected ITreeContentProvider getFileProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ITreeContentProvider getFolderProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Check for:
	 * 1. periods at end of package name
	 * 2. spaces in package name
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardDataTransferPage#validateSourceGroup()
	 */
//    protected boolean validateSourceGroup() {
//    	String currText = myPackageDialogField.getText();
//    	if (currText != null) {
//			char[] ca = currText.toCharArray();
//			for (char aChar : ca) {
//				if (Character.isWhitespace(aChar)) {
//					return false;
//				}
//			}
//			if (!Character.isLetterOrDigit(ca[ca.length - 1])) {
//				return false;
//			}
//		}
//		return true;
//    }
	
    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
	@Override
	public void createControl(Composite parent) {

        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);

//        createOptionsGroup(composite);
        
        restoreWidgetValues();
        updateWidgetEnablements();
//        setPageComplete(determinePageCompletion());
        setPageComplete(false);
        setErrorMessage(null);	// should not initially have error message

        setControl(composite);
    }
	
	
//    //No workspace projects are necessary
//    protected boolean determinePageCompletion() {
////        boolean complete = validateSourceGroup() && validateOptionsGroup();
//        boolean complete = validateSourceGroup();
//        
//        // Avoid draw flicker by not clearing the error
//        // message unless all is valid.
//        if (complete) {
//			setErrorMessage(null);
//		}
//
//        return complete;
//    }
//	
//    /**
//     * Determine if the page is complete and update the page appropriately. 
//     */
//    protected void updatePageCompletion() {
//    	System.out.println("updatePageCompletion called");
//        boolean pageComplete = determinePageCompletion();
//        setPageComplete(pageComplete);
//        if (pageComplete) {
//            setMessage(null);
//        }
//    }
//    
    class StringButtonStatusFieldListener implements IDialogFieldListener {

		public void dialogFieldChanged(DialogField field) {
			// TODO Auto-generated method stub
			System.out.println("Field Changed: "+myPackageDialogField.getText());
			myPackageDialogField.enableButton(true);
			//updatePageCompletion();
		}
    }

	class ResetButtonAdaptor implements IStringButtonAdapter {

		public void changeControlPressed(DialogField field) {
			System.out.println("ResetButtonListener.widgetSelected()");
			myPackageDialogField.setText("");
			myPackageDialogField.refresh();
		}
	}
	
    
}
