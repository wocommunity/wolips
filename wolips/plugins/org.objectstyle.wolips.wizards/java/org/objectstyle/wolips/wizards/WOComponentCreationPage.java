/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.wizards;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonStatusDialogField;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author mnolte
 * @author uli
 */
public class WOComponentCreationPage extends WizardNewWOResourcePage {
	// widgets
	private static final String BODY_CHECKBOX_KEY = "WOComponentCreationWizardSection.bodyCheckbox";

	private static final String WOO_CHECKBOX_KEY = "WOComponentCreationWizardSection.wooCheckbox";

	private static final String API_CHECKBOX_KEY = "WOComponentCreationWizardSection.apiCheckbox";

	private static final String HTML_DOCTYPE_KEY = "WOComponentCreationWizardSection.htmlDocType";

	private static final String NSSTRING_ENCODING_KEY = "WOComponentCreationWizardSection.encoding";

	private Button bodyCheckbox;

	private Combo htmlCombo;

	private Combo encodingCombo;

	private Button wooCheckbox;

	private Button apiCheckbox;

	private IResource[] resourcesToReveal;

	StringButtonStatusDialogField myPackageDialogField;

	enum HTML {
		STRICT_401("HTML 4.0.1 Strict", "4.0.1 strict doctype", 0),
		TRANSITIONAL_401("HTML 4.0.1 Transitional", "4.0.1 transitional doctype", 1),
		STRICT_XHTML10("XHTML 1.0 Strict", "XHTML 1.0 strict doctype", 2),
		TRANSITIONAL_XHTML10("XHTML 1.0 Transitional", "XHTML 1.0 transitional doctype", 3),
		FRAMESET_XHTML10("XHTML 1.0 Frameset", "XHTML 1.0 frameset doctype",4),
		XHTML11("XHTML 1.1", "XHTML 1.1 doctype", 5);


		private final String _displayString;
		private final String _html;
		private final int _templateIndex;

		//template index is just to make things easier in velocity engine
		HTML (String display, String html, int templateIndex) {
			_displayString = display;
			_html = html;
			_templateIndex = templateIndex;
		}

		String getDisplayString() {
			return _displayString;
		}

		String getHTML() {
			return _html;
		}

		int getTemplateIndex() {
			return _templateIndex;
		}

		String getDefaultDocType() {
			return TRANSITIONAL_XHTML10.getDisplayString();
		}
	}

	enum NSSTRINGENCODING {
		NSUTF8StringEncoding("NSUTF8StringEncoding"),
		NSMacOSRomanStringEncoding("NSMacOSRomanStringEncoding"),
		NSASCIIStringEncoding("NSASCIIStringEncoding"),
		NSNEXTSTEPStringEncoding("NSNEXTSTEPStringEncoding"),
		NSJapaneseEUCStringEncoding ("NSJapaneseEUCStringEncoding"),
		NSISOLatin1StringEncoding("NSISOLatin1StringEncoding"),
		NSSymbolStringEncoding("NSSymbolStringEncoding"),
		NSNonLossyASCIIStringEncoding("NSNonLossyASCIIStringEncoding"),
		NSShiftJISStringEncoding("NSShiftJISStringEncoding"),
		NSISOLatin2StringEncoding("NSISOLatin2StringEncoding"),
		NSUnicodeStringEncoding("NSUnicodeStringEncoding"),
		NSWindowsCP1251StringEncoding("NSWindowsCP1251StringEncoding"),
		NSWindowsCP1252StringEncoding("NSWindowsCP1252StringEncoding"),
		NSWindowsCP1253StringEncoding("NSWindowsCP1253StringEncoding"),
		NSWindowsCP1254StringEncoding("NSWindowsCP1254StringEncoding"),
		NSWindowsCP1250StringEncoding("NSWindowsCP1250StringEncoding"),
		NSISO2022JPStringEncoding("NSISO2022JPStringEncoding"),
		NSProprietaryStringEncoding("NSProprietaryStringEncoding");

		private final String _encoding;

		NSSTRINGENCODING (String encoding) {
			_encoding = encoding;
		}

		String getDisplayString() {
			return _encoding;
		}

		String getDefaultEncoding() {
			return NSSTRINGENCODING.NSUTF8StringEncoding.getDisplayString();
		}
	}

	/**
	 * Creates the page for the wocomponent creation wizard.
	 *
	 * @param workbench
	 *            the workbench on which the page should be created
	 * @param selection
	 *            the current selection
	 */
	public WOComponentCreationPage(IStructuredSelection selection) {
		super("createWOComponentPage1", selection);
		this.setTitle(Messages.getString("WOComponentCreationPage.title"));
		this.setDescription(Messages.getString("WOComponentCreationPage.description"));
	}

	/**
	 * (non-Javadoc) Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {
		// inherit default container and name specification widgets
		super.createControl(parent);

		Composite composite = (Composite) getControl();
		// WorkbenchHelp.setHelp(composite,
		// IReadmeConstants.CREATION_WIZARD_PAGE_CONTEXT);
		this.setFileName(Messages.getString("WOComponentCreationPage.newComponent.defaultName"));

		new Label(composite, SWT.NONE); // vertical spacer

		Group packageGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		packageGroup.setLayout(layout);
		packageGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		PackageButtonAdapter adapter = new PackageButtonAdapter();
		myPackageDialogField = new StringButtonStatusDialogField(adapter);
		myPackageDialogField.setDialogFieldListener(adapter);
		myPackageDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label);
		myPackageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		myPackageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);
		myPackageDialogField.doFillIntoGrid(packageGroup, 4);
		Text text = myPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(text);
		// JavaPackageCompletionProcessor packageCompletionProcessor= new
		// JavaPackageCompletionProcessor();
		// ControlContentAssistHelper.createTextContentAssistant(text,
		// packageCompletionProcessor);

		/*
		 * HTML body generation options
		 */
		Group bodygroup = new Group(composite, SWT.NONE);
		bodygroup.setLayout(new GridLayout());
		bodygroup.setText(Messages.getString("WOComponentCreationPage.creationOptions.bodyTag.group"));
		bodygroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		ButtonSelectionAdaptor listener = new ButtonSelectionAdaptor();
		bodyCheckbox = new Button(bodygroup, SWT.CHECK);
		bodyCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.bodyTag.button"));
		bodyCheckbox.setSelection(this.getDialogSettings().getBoolean(BODY_CHECKBOX_KEY));
		bodyCheckbox.setAlignment(SWT.CENTER);
		bodyCheckbox.addListener(SWT.Selection, this);
		bodyCheckbox.addSelectionListener(listener);

		Composite bodyrow = new Composite(bodygroup, SWT.NONE);
		GridLayout rowLayout = new GridLayout();
		rowLayout.numColumns = 3;
		bodyrow.setLayout(rowLayout);

		Label htmllabel = new Label(bodyrow, SWT.NONE);
		htmllabel.setText(Messages.getString("WOComponentCreationPage.creationOptions.bodyTag.label"));
		htmllabel.setAlignment(SWT.CENTER);
		htmlCombo = new Combo(bodyrow, SWT.DROP_DOWN);
		populateHTMLCombo(htmlCombo);
		refreshButtonSettings(bodyCheckbox);

		/*
		 * WOO generation options
		 */
		Group woogroup = new Group(composite, SWT.NONE);
		woogroup.setLayout(new GridLayout());
		woogroup.setText(Messages.getString("WOComponentCreationPage.creationOptions.wooFile.group"));
		woogroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		wooCheckbox = new Button(woogroup, SWT.CHECK);
		wooCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.wooFile.button"));
		wooCheckbox.setSelection(this.getDialogSettings().getBoolean(WOO_CHECKBOX_KEY));
		wooCheckbox.addListener(SWT.Selection, this);
		wooCheckbox.addSelectionListener(listener);

		Composite woorow = new Composite(woogroup, SWT.NONE);
		GridLayout woorowLayout = new GridLayout();
		woorowLayout.numColumns = 3;
		woorow.setLayout(woorowLayout);

		Label encodingLabel = new Label(woorow, SWT.NONE);
		encodingLabel.setText(Messages.getString("WOComponentCreationPage.creationOptions.wooFile.label"));
		encodingLabel.setAlignment(SWT.CENTER);
		encodingCombo = new Combo(woorow, SWT.DROP_DOWN);
		populateStringEncodingCombo(encodingCombo);
		refreshButtonSettings(wooCheckbox);

		/*
		 * API generation options
		 */
		Group apigroup = new Group(composite, SWT.NONE);
		apigroup.setLayout(new GridLayout());
		apigroup.setText(Messages.getString("WOComponentCreationPage.creationOptions.apiFile.group"));
		apigroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Composite apirow = new Composite(apigroup, SWT.NONE);
		RowLayout apirowLayout = new RowLayout();
		apirow.setLayout(apirowLayout);
		apiCheckbox = new Button(apirow, SWT.CHECK);
		apiCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.apiFile.button"));
		apiCheckbox.setSelection(this.getDialogSettings().getBoolean(API_CHECKBOX_KEY));
		apiCheckbox.addListener(SWT.Selection, this);
		apiCheckbox.addSelectionListener(listener);

		setPageComplete(validatePage());

	}

	/**
	 * Creates a new file resource as requested by the user. If everything is OK
	 * then answer true. If not, false will cause the dialog to stay open and
	 * the appropriate error message is shown
	 *
	 * @return whether creation was successful
	 * @see WOComponentCreationWizard#performFinish()
	 */
	public boolean createComponent() {
		WOComponentCreator componentCreator;
		String componentName = getFileName();
		String packageName = myPackageDialogField.getText();
		IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
		switch (getContainerFullPath().segmentCount()) {
		case 0:
			// not possible ( see validatePage() )
			setErrorMessage("unknown error");
			return false;
		case 1:
			componentCreator = new WOComponentCreator(actualProject, componentName, packageName, bodyCheckbox.getSelection(), apiCheckbox.getSelection(), wooCheckbox.getSelection(), this);
			break;
		default:
			// determine parent resource for component creator by removing
			// first element (workspace) from full path
			IFolder subprojectFolder = actualProject.getFolder(getContainerFullPath().removeFirstSegments(1));
			componentCreator = new WOComponentCreator(subprojectFolder, componentName, packageName, bodyCheckbox.getSelection(), apiCheckbox.getSelection(), wooCheckbox.getSelection(), this);
			break;
		}
		this.getDialogSettings().put(BODY_CHECKBOX_KEY, bodyCheckbox.getSelection());
		this.getDialogSettings().put(HTML_DOCTYPE_KEY, htmlCombo.getText());
		this.getDialogSettings().put(WOO_CHECKBOX_KEY, wooCheckbox.getSelection());
		this.getDialogSettings().put(NSSTRING_ENCODING_KEY, encodingCombo.getText());
		this.getDialogSettings().put(API_CHECKBOX_KEY, apiCheckbox.getSelection());

		logPreferences();

		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(componentCreator);
		return createResourceOperation(op);
	}

	/*
	 * Debugging
	 */
	public void logPreferences () {
		System.out.println("BODY_CHECKBOX_KEY: "+this.getDialogSettings().get(BODY_CHECKBOX_KEY));
		System.out.println("HTML_DOCTYPE_KEY: "+this.getDialogSettings().get(HTML_DOCTYPE_KEY));
		System.out.println("WOO_CHECKBOX_KEY: "+this.getDialogSettings().get(WOO_CHECKBOX_KEY));
		System.out.println("NSSTRING_ENCODING_KEY: "+this.getDialogSettings().get(NSSTRING_ENCODING_KEY));
		System.out.println("API_CHECKBOX_KEY: "+this.getDialogSettings().get(API_CHECKBOX_KEY));
	}

	/**
	 * Populate a SWT Combo with HTML doctypes
	 * @param c
	 */
	public void populateHTMLCombo(Combo c) {

		for (HTML entry : HTML.values()) {
			c.add(entry.getDisplayString());
		}

		selectHTMLDocTypePreference(c);
	}

	/**
	 * Pick the previous encoding preference else default to HTML.TRANSITIONAL_XHTML10
	 * @param c
	 */
	public void selectHTMLDocTypePreference(Combo c) {
		String previousDocType = this.getDialogSettings().get(HTML_DOCTYPE_KEY);

		if (previousDocType != null && previousDocType.length() > 0) {
			int i = 0;
			for (HTML entry : HTML.values()) {
				if (previousDocType.equals(entry.getDisplayString())) {
					c.select(i);
					return;
				}
				i++;
			}
		}
		//default
		c.select(3);
	}

	/**
	 * Return the HTML for the selected html doc type
	 * @return defaults to HTML.TRANSITIONAL_XHTML10
	 */
	public HTML getSelectedHTMLDocType() {
		if (bodyCheckbox.getSelection()) {
			return getHTMLForDisplayString(htmlCombo.getText());
		}

		return HTML.TRANSITIONAL_XHTML10;
	}

	/**
	 * Return HTML to insert for selected html/xhtml doc type
	 * @param displayString
	 * @return selected doc type or HTML.TRANSITIONAL_XHTML10
	 */
	public HTML getHTMLForDisplayString(String displayString) {
		for (HTML entry : HTML.values()) {

			if (displayString.equals(entry.getDisplayString())) {
				return entry;
			}
		}

		return HTML.TRANSITIONAL_XHTML10;
	}

	/**
	 * Populate a SWT Combo with NSStringEncoding doctypes (See NSString.h)
	 * @param c
	 * @see NSString.h
	 */
	public void populateStringEncodingCombo(Combo c) {

		for (NSSTRINGENCODING entry : NSSTRINGENCODING.values()) {
			c.add(entry.getDisplayString());
		}

		selectNSStringEncodingPreference(c);
	}

	/**
	 * Pick the previous encoding preference else default to NSSTRINGENCODING.NSUTF8StringEncoding
	 * @param c
	 */
	public void selectNSStringEncodingPreference(Combo c) {
		String previousEncoding = this.getDialogSettings().get(NSSTRING_ENCODING_KEY);

		if (previousEncoding != null && previousEncoding.length() > 0) {
			int i = 0;
			for (NSSTRINGENCODING entry : NSSTRINGENCODING.values()) {
				if (previousEncoding.equals(entry.getDisplayString())) {
					c.select(i);
					return;
				}
				i++;
			}
		}
		//default
		c.select(0);
	}

	/**
	 * Return current selected encoding
	 * @return defaults to NSUTF8StringEncoding
	 */
	public String getSelectedEncoding() {
		if (wooCheckbox.getSelection()) {
			return getEncodingForDisplayString(encodingCombo.getText());
		}

		return NSSTRINGENCODING.NSUTF8StringEncoding.getDisplayString();
	}

	/**
	 * Return the encoding value to insert into the .woo file
	 * @param displayString
	 * @return selected encoding or NSUTF8StringEncoding if not set
	 */
	public String getEncodingForDisplayString(String displayString) {
		for (NSSTRINGENCODING entry : NSSTRINGENCODING.values()) {

			if (displayString.equals(entry.getDisplayString())) {
				return displayString;
			}
		}

		return NSSTRINGENCODING.NSUTF8StringEncoding.getDefaultEncoding();
	}

	/**
	 * (non-Javadoc) Method declared on WizardNewFileCreationPage.
	 */
	protected String getNewFileLabel() {
		return Messages.getString("WOComponentCreationPage.newComponent.label");
	}

	public IResource[] getResourcesToReveal() {
		return resourcesToReveal;
	}

	public void setResourcesToReveal(IResource[] resources) {
		this.resourcesToReveal = resources;
	}

	IPackageFragment choosePackage() {
		List<IJavaElement> packagesList = new LinkedList<IJavaElement>();
		try {
			IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
			IJavaProject javaProject = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject(actualProject);
			IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
			for (int k = 0; k < roots.length; k++) {
				if (roots[k].getKind() == IPackageFragmentRoot.K_SOURCE) {
					IJavaElement[] children = roots[k].getChildren();
					for (int i = 0; i < children.length; i++) {
						packagesList.add(children[i]);
					}
				}
			}
		} catch (JavaModelException e) {
			// JTourBusPlugin.log(e);
			e.printStackTrace();
		}
		IJavaElement[] packages = packagesList.toArray(new IJavaElement[packagesList.size()]);

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description);
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty);
		dialog.setFilter(myPackageDialogField.getText());
		dialog.setElements(packages);
		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}

	protected void refreshButtonSettings (Button button) {
		if (button.equals(bodyCheckbox)) {
			if (bodyCheckbox.getSelection()) {
				htmlCombo.setEnabled(true);
			} else {
				htmlCombo.setEnabled(false);
			}
		}

		if (button.equals(apiCheckbox)) {
			if (apiCheckbox.getSelection()) {
				setPageComplete(false);
			}
		}

		if (button.equals(wooCheckbox)) {
			if (wooCheckbox.getSelection()) {
				encodingCombo.setEnabled(true);
			} else {
				encodingCombo.setEnabled(false);
			}
		}
	}

	protected void handleSelectionEvent(SelectionEvent event) {
		System.out.println("ButtonSelectionEvent: event:"+event);
		Widget w = event.widget;
		if (w instanceof Button) {
			refreshButtonSettings((Button)w);
		}
	}

	protected class PackageButtonAdapter implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField _field) {
			IPackageFragment pack = choosePackage();
			if (pack != null) {
				myPackageDialogField.setText(pack.getElementName());
			}
		}

		public void dialogFieldChanged(DialogField _field) {
			// fPackageStatus= packageChanged();
			// updatePackageStatusLabel();
		}
	}

	protected class ButtonSelectionAdaptor implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent event) {
			handleSelectionEvent(event);
		}

		public void widgetSelected(SelectionEvent event) {
			handleSelectionEvent(event);
		}

	}
}
