package org.objectstyle.wolips.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ClasspathContainerPage  extends NewElementWizardPage implements IClasspathContainerPage {

	private StringDialogField fEntryField;

	/**
	 * Constructor for ClasspathContainerDefaultPage.
	 * @param pageName
	 */
	public ClasspathContainerPage() {
		super("ClasspathContainerPage"); //$NON-NLS-1$
		setTitle(NewWizardMessages.getString("ClasspathContainerDefaultPage.title")); //$NON-NLS-1$
		setDescription(NewWizardMessages.getString("ClasspathContainerDefaultPage.description")); //$NON-NLS-1$
		
		fEntryField= new StringDialogField();
		fEntryField.setLabelText(NewWizardMessages.getString("ClasspathContainerDefaultPage.path.label")); //$NON-NLS-1$
		fEntryField.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				validatePath();
			}
		});
		validatePath();
	}

	private void validatePath() {
		StatusInfo status= new StatusInfo();
		String str= fEntryField.getText();
		if (str.length() == 0) {
			status.setError(NewWizardMessages.getString("ClasspathContainerDefaultPage.path.error.enterpath")); //$NON-NLS-1$
		} else if (!Path.ROOT.isValidPath(str)) {
			status.setError(NewWizardMessages.getString("ClasspathContainerDefaultPage.path.error.invalidpath")); //$NON-NLS-1$
		} else {
			IPath path= new Path(str);
			if (path.segmentCount() == 0) {
				status.setError(NewWizardMessages.getString("ClasspathContainerDefaultPage.path.error.needssegment")); //$NON-NLS-1$
			}
		}
		updateStatus(status);
	}

	/* (non-Javadoc)
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		
		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fEntryField }, true);
		LayoutUtil.setHorizontalGrabbing(fEntryField.getTextControl(null));
		
		fEntryField.setFocus();
		
		setControl(composite);
		WorkbenchHelp.setHelp(composite, IJavaHelpContextIds.CLASSPATH_CONTAINER_DEFAULT_PAGE);
	}

	/* (non-Javadoc)
	 * @see IClasspathContainerPage#finish()
	 */
	public boolean finish() {
		return true;
	}

	/* (non-Javadoc)
	 * @see IClasspathContainerPage#getSelection()
	 */
	public IClasspathEntry getSelection() {
		return JavaCore.newContainerEntry(new Path(fEntryField.getText()));
	}

	/* (non-Javadoc)
	 * @see IClasspathContainerPage#setSelection(IClasspathEntry)
	 */
	public void setSelection(IClasspathEntry containerEntry) {
		if (containerEntry != null) {
			fEntryField.setText(containerEntry.getPath().toString());
		} else {
			fEntryField.setText(""); //$NON-NLS-1$
		}
	}

}
