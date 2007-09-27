package org.objectstyle.wolips.wizards;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewLinkPage;


/**
 * Used by WOLips project creation wizards to assign default packages for the template
 * Java classes.
 * @see NewWOProjectWizard
 */
/*
 * Subclasses WizardNewLinkPage due to the fact it's the easiest to
 * override UI elements.  All we need is to display a text field for configuring
 * a java package.
 */
public class PackageSpecifierWizardPage extends WizardNewLinkPage {
	static String textData;

	public static final String OPTIONS_STRING = Messages.getString("PackageSpecifierWizardPage.options.text");
	public static final String DEFAULT_PACKAGE_STRING = Messages.getString("PackageSpecifierWizardPage.options.defaultPackage");

	public PackageSpecifierWizardPage(String pageName, int type) {
		super(pageName, type);
	}

	public String getTextData() {
		return (textData != null) ? textData : "";
	}

	//translate '.' to file spearators
	public String getConvertedPath () {
		String convertedPackagePath = "";
		if (getTextData().trim().length() > 0) {
			 convertedPackagePath = getTextData().trim().replace(".", File.separator);
		}
		return convertedPackagePath;
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();
		initializeDialogUnits(parent);

		// top level group
		Composite topLevel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(font);

        Group optionGroup = new Group(topLevel, SWT.SHADOW_IN);
        GridLayout clientlayout = new GridLayout();
        optionGroup.setLayout(clientlayout);
        optionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
        optionGroup.setText(OPTIONS_STRING);
        optionGroup.setFont(topLevel.getFont());

        Text text = new Text(optionGroup, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL));
		text.setText(DEFAULT_PACKAGE_STRING);
		textData = DEFAULT_PACKAGE_STRING;
		text.addModifyListener(new TextFieldModifyListener());

		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}

	public void updatePageComplete() {
		setPageComplete(false);

		//TODO: we could validate against more errors
		if (textData.endsWith(".") || textData.contains("/")) {
			setMessage(null);
			setErrorMessage("Invalid package name: "+textData);
			return;
		}

		setPageComplete(true);
		setMessage(null);
		setErrorMessage(null);
	}

	class TextFieldModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent event) {

			if (event.getSource() instanceof Text) {
				textData = ((Text)event.getSource()).getText();
				updatePageComplete();
			}
		}

	}


}
