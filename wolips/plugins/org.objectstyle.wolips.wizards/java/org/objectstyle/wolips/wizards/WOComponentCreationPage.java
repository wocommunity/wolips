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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.core.resources.types.TypeNameCollector;
import org.objectstyle.wolips.eomodeler.utils.StringLabelProvider;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;

/**
 * @author mnolte
 * @author uli
 */
public class WOComponentCreationPage extends WizardNewWOResourcePage {
	// widgets
	private static final String BODY_CHECKBOX_KEY = "WOComponentCreationWizardSection.bodyCheckbox";

	private static final String API_CHECKBOX_KEY = "WOComponentCreationWizardSection.apiCheckbox";

	private static final String HTML_DOCTYPE_KEY = "WOComponentCreationWizardSection.htmlDocType";

	private static final String NSSTRING_ENCODING_KEY = "WOComponentCreationWizardSection.encoding";

	private static final String SUPERCLASS_KEY = "WOComponentCreationWizardSection.superclass";

	private Button _bodyCheckbox;

	private Combo _htmlCombo;

	private Combo _encodingCombo;

	private Button _apiCheckbox;

	private IResource[] _resourcesToReveal;

	private StringButtonStatusDialogField _packageDialogField;

	private StringButtonStatusDialogField _superclassDialogField;

	private Object _currentSelection;

	enum HTML {
		STRICT_401("HTML 4.0.1 Strict", "4.0.1 strict doctype", 0), 
		TRANSITIONAL_401("HTML 4.0.1 Transitional", "4.0.1 transitional doctype", 1), 
		STRICT_XHTML10("XHTML 1.0 Strict", "XHTML 1.0 strict doctype", 2), 
		TRANSITIONAL_XHTML10("XHTML 1.0 Transitional", "XHTML 1.0 transitional doctype", 3), 
		FRAMESET_XHTML10("XHTML 1.0 Frameset", "XHTML 1.0 frameset doctype", 4), 
		XHTML11("XHTML 1.1", "XHTML 1.1 doctype", 5), 
		LAZY_OLD("Lazy Old HTML", "Lazy Old HTML", 6);

		private final String _displayString;

		private final String _html;

		private final int _templateIndex;

		// template index is just to make things easier in velocity engine
		HTML(String display, String html, int templateIndex) {
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
		NSJapaneseEUCStringEncoding("NSJapaneseEUCStringEncoding"), 
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

		NSSTRINGENCODING(String encoding) {
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
		super("createWOComponentPage1", WOComponentCreationPage.processSelection(selection));
		this.setTitle(Messages.getString("WOComponentCreationPage.title"));
		this.setDescription(Messages.getString("WOComponentCreationPage.description"));

		if (selection != null) {
			Object selectedObject = selection.getFirstElement();
			if (selectedObject instanceof IFolder) {
				IJavaElement parentJavaElement = JavaCore.create((IFolder) selectedObject);
				if (parentJavaElement instanceof IPackageFragment) {
					_currentSelection = parentJavaElement;
					this.setContainerFullPath(componentPathForPackage((IPackageFragment)_currentSelection));
				}
			}
		}
	}

	public static IStructuredSelection processSelection(IStructuredSelection selection) {
		IStructuredSelection processedSelection = null;
		if (selection != null) {
			Object selectedObject = selection.getFirstElement();
			if (selectedObject instanceof IFile) {
				selectedObject = ((IFile)selectedObject).getParent();
				processedSelection = null;
			}
			if (selectedObject instanceof IFolder) {
				IFolder currentFolder = (IFolder) selectedObject;
				IJavaElement parentJavaElement = JavaCore.create(currentFolder);
				if (parentJavaElement instanceof IPackageFragment) {
					// Don't let you put WO's in a package
					processedSelection = new StructuredSelection(currentFolder.getProject());
				} else if (parentJavaElement instanceof IPackageFragmentRoot) {
					// Don't let you put WO's in a source folder
					processedSelection = new StructuredSelection(currentFolder.getProject());
				} else if (currentFolder.getName().endsWith(".wo")) {
					// Don't let you put WO's inside of WO's by accident
					processedSelection = new StructuredSelection(currentFolder.getParent());
				}
			}
		}
		return processedSelection;
	}

	@Override
	protected void initialPopulateContainerNameField() {
		super.initialPopulateContainerNameField();
	}

	@Override
	protected void createAdvancedControls(Composite parent) {
		// super.createAdvancedControls(parent);
	}

	@Override
	protected IStatus validateLinkedResource() {
		return Status.OK_STATUS;
	}
	
	@Override
	protected boolean validatePage() {
		IStatus status = JavaConventions.validateCompilationUnitName(this.getFileName() + ".java",
				CompilerOptions.VERSION_1_3, CompilerOptions.VERSION_1_3);
		if (!status.isOK()) {
			setErrorMessage(status.getMessage());
			return false;
		}
		return super.validatePage();
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

		// new Label(composite, SWT.NONE); // vertical spacer

		Group javaGroup = new Group(composite, SWT.NONE);
		javaGroup.setText(Messages.getString("WOComponentCreationPage.creationOptions.javaFile.group"));
		GridLayout javaLayout = new GridLayout();
		javaLayout.numColumns = 4;
		javaGroup.setLayout(javaLayout);
		javaGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		PackageButtonAdapter packageButtonAdapter = new PackageButtonAdapter();
		_packageDialogField = new StringButtonStatusDialogField(packageButtonAdapter);
		_packageDialogField.setDialogFieldListener(packageButtonAdapter);
		_packageDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label);
		_packageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		_packageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);
		_packageDialogField.doFillIntoGrid(javaGroup, 4);
		Text packageText = _packageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(packageText, convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(packageText);
		// JavaPackageCompletionProcessor packageCompletionProcessor= new
		// JavaPackageCompletionProcessor();
		// ControlContentAssistHelper.createTextContentAssistant(text,
		// packageCompletionProcessor);

		if (_currentSelection instanceof IPackageFragment) {
			_packageDialogField.setText(((IPackageFragment) _currentSelection).getElementName());
		} else {
			IFolder _path = (IFolder)ResourcesPlugin.getWorkspace().getRoot().findMember(this.getContainerFullPath());
			String _package = packageNameForComponentFolder(_path);
			if (_package == null && (_package = packageNameForComponent("Main")) == null) {
				_package = "";
			}
			_packageDialogField.setText(_package);
		}

		SuperclassButtonAdapter superclassButtonAdapter = new SuperclassButtonAdapter();
		_superclassDialogField = new StringButtonStatusDialogField(superclassButtonAdapter);
		_superclassDialogField.setDialogFieldListener(superclassButtonAdapter);
		_superclassDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_superclass_label);
		_superclassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button);
		_superclassDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);
		_superclassDialogField.doFillIntoGrid(javaGroup, 4);
		String superclass = this.getDialogSettings().get(WOComponentCreationPage.SUPERCLASS_KEY);
		if (superclass == null || superclass.length() == 0) {
			_superclassDialogField.setText("com.webobjects.appserver.WOComponent");
		}
		else {
			_superclassDialogField.setText(superclass);
		}
		Text superclassText = _superclassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(superclassText, convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(superclassText);

		new Label(composite, SWT.NONE); // vertical spacer

		/*
		 * HTML body generation options
		 */
		Group optionalFilesGroup = new Group(composite, SWT.NONE);
		optionalFilesGroup.setLayout(new GridLayout(3, false));
		optionalFilesGroup.setText(Messages.getString("WOComponentCreationPage.creationOptions.group"));
		optionalFilesGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		ButtonSelectionAdaptor listener = new ButtonSelectionAdaptor();
		_bodyCheckbox = new Button(optionalFilesGroup, SWT.CHECK);
		_bodyCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.bodyTag.button"));
		_bodyCheckbox.setSelection(this.getDialogSettings().getBoolean(BODY_CHECKBOX_KEY));
		_bodyCheckbox.setAlignment(SWT.CENTER);
		_bodyCheckbox.addListener(SWT.Selection, this);
		_bodyCheckbox.addSelectionListener(listener);

		Label htmlLabel = new Label(optionalFilesGroup, SWT.RIGHT);
		htmlLabel.setText(Messages.getString("WOComponentCreationPage.creationOptions.bodyTag.label"));
		htmlLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_htmlCombo = new Combo(optionalFilesGroup, SWT.DROP_DOWN);
		_htmlCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		populateHTMLCombo(_htmlCombo);
		refreshButtonSettings(_bodyCheckbox);

		_apiCheckbox = new Button(optionalFilesGroup, SWT.CHECK);
		GridData apiLayoutData = new GridData();
//		apiLayoutData.horizontalSpan = 3;
		_apiCheckbox.setLayoutData(apiLayoutData);
		_apiCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.apiFile.button"));
		_apiCheckbox.setSelection(this.getDialogSettings().getBoolean(API_CHECKBOX_KEY));
		_apiCheckbox.addListener(SWT.Selection, this);
		_apiCheckbox.addSelectionListener(listener);

		Label encodingLabel = new Label(optionalFilesGroup, SWT.RIGHT);
		encodingLabel.setText(Messages.getString("WOComponentCreationPage.creationOptions.wooFile.label"));
		encodingLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_encodingCombo = new Combo(optionalFilesGroup, SWT.DROP_DOWN);
		_encodingCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		populateStringEncodingCombo(_encodingCombo);

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
		String packageName = _packageDialogField.getText();
		String superclassName = _superclassDialogField.getText();
		IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
		switch (getContainerFullPath().segmentCount()) {
		case 0:
			// not possible ( see validatePage() )
			setErrorMessage("unknown error");
			return false;
		default:
			// determine parent resource for component creator by removing
			// first element (workspace) from full path
			IFolder subprojectFolder = actualProject.getFolder(getContainerFullPath().removeFirstSegments(1));
			componentCreator = new WOComponentCreator(subprojectFolder, componentName, packageName, superclassName, _bodyCheckbox.getSelection(), _apiCheckbox.getSelection(), this);
			break;
		}
		this.getDialogSettings().put(WOComponentCreationPage.SUPERCLASS_KEY, _superclassDialogField.getText());
		this.getDialogSettings().put(WOComponentCreationPage.BODY_CHECKBOX_KEY, _bodyCheckbox.getSelection());
		this.getDialogSettings().put(WOComponentCreationPage.HTML_DOCTYPE_KEY, _htmlCombo.getText());
		this.getDialogSettings().put(WOComponentCreationPage.NSSTRING_ENCODING_KEY, _encodingCombo.getText());
		this.getDialogSettings().put(WOComponentCreationPage.API_CHECKBOX_KEY, _apiCheckbox.getSelection());

		// logPreferences();

		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(componentCreator);
		return createResourceOperation(op);
	}

	/*
	 * Debugging
	 */
	public void logPreferences() {
		System.out.println("BODY_CHECKBOX_KEY: " + this.getDialogSettings().get(BODY_CHECKBOX_KEY));
		System.out.println("HTML_DOCTYPE_KEY: " + this.getDialogSettings().get(HTML_DOCTYPE_KEY));
		System.out.println("NSSTRING_ENCODING_KEY: " + this.getDialogSettings().get(NSSTRING_ENCODING_KEY));
		System.out.println("API_CHECKBOX_KEY: " + this.getDialogSettings().get(API_CHECKBOX_KEY));
		System.out.println("SUPERCLASS_KEY: " + this.getDialogSettings().get(WOComponentCreationPage.SUPERCLASS_KEY));
	}

	/**
	 * Populate a SWT Combo with HTML doctypes
	 * 
	 * @param c
	 */
	public void populateHTMLCombo(Combo c) {

		for (HTML entry : HTML.values()) {
			c.add(entry.getDisplayString());
		}

		selectHTMLDocTypePreference(c);
	}

	/**
	 * Pick the previous encoding preference else default to
	 * HTML.TRANSITIONAL_XHTML10
	 * 
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
		// default
		c.select(3);
	}

	/**
	 * Return the HTML for the selected html doc type
	 * 
	 * @return defaults to HTML.TRANSITIONAL_XHTML10
	 */
	public HTML getSelectedHTMLDocType() {
		if (_bodyCheckbox.getSelection()) {
			return getHTMLForDisplayString(_htmlCombo.getText());
		}

		return HTML.TRANSITIONAL_XHTML10;
	}

	/**
	 * Return HTML to insert for selected html/xhtml doc type
	 * 
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
	 * 
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
	 * Pick the previous encoding preference else default to
	 * NSSTRINGENCODING.NSUTF8StringEncoding
	 * 
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
		// default
		c.select(0);
	}

	/**
	 * Return current selected encoding
	 * 
	 * @return defaults to NSUTF8StringEncoding
	 */
	public String getSelectedEncoding() {
		return getEncodingForDisplayString(_encodingCombo.getText());

//		return NSSTRINGENCODING.NSUTF8StringEncoding.getDisplayString();
	}

	/**
	 * Return the encoding value to insert into the .woo file
	 * 
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
		return _resourcesToReveal;
	}

	public void setResourcesToReveal(IResource[] resources) {
		this._resourcesToReveal = resources;
	}

	protected String packageNameForComponent(String componentName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
		try {
			LocalizedComponentsLocateResult result = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(project, componentName);
			IType javaType;
			if (result != null && (javaType = result.getDotJavaType()) != null) {
				return javaType.getPackageFragment().getElementName();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (LocateException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String packageNameForComponentFolder(IFolder folder) {
		try {
			for(IResource resource : folder.members()) {
				if ("wo".equals(resource.getLocation().getFileExtension())) {
					return packageNameForComponent(resource.getLocation().removeFileExtension().lastSegment());
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected IPath componentPathForPackage(IPackageFragment _selection) {
		try {
			LocatePlugin locate = LocatePlugin.getDefault();
			for (IJavaElement element : _selection.getChildren()) {
				String componentName = locate.fileNameWithoutExtension(element.getElementName());
				LocalizedComponentsLocateResult result = locate.getLocalizedComponentsLocateResult(
						_selection.getJavaProject().getProject(), componentName);
				IFolder[] components = result.getComponents();
				if (components.length > 0) {
					IContainer selectionPath = components[0].getParent();
					return selectionPath.getFullPath();
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LocateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected IPackageFragment choosePackage() {
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
		dialog.setFilter(_packageDialogField.getText());
		dialog.setElements(packages);
		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}

	protected String chooseSuperclass() {
		Set<String> superclasses = new HashSet<String>();
		try {
			IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
			IJavaProject javaProject = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject(actualProject);

			TypeNameCollector typeNameCollector = new TypeNameCollector(javaProject, false);
			BindingReflectionUtils.findMatchingElementClassNames("", SearchPattern.R_PREFIX_MATCH, typeNameCollector, new NullProgressMonitor());
			for (String typeName : typeNameCollector.getTypeNames()) {
				// int dotIndex = typeName.lastIndexOf('.');
				// if (dotIndex != -1) {
				// typeName = typeName.substring(dotIndex + 1);
				// }
				// validValues.add("\"" + typeName + "\"");
				superclasses.add(typeName);
			}
		} catch (JavaModelException e) {
			// JTourBusPlugin.log(e);
			e.printStackTrace();
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new StringLabelProvider());
		dialog.setIgnoreCase(true);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_message);
		// dialog.setEmptyListMessage(NewWizardMessages.NewTypeWiz);
		dialog.setFilter(_superclassDialogField.getText());
		dialog.setElements(superclasses.toArray());
		if (dialog.open() == Window.OK) {
			return (String) dialog.getFirstResult();
		}
		return null;
	}

	protected void refreshButtonSettings(Button button) {
		if (button.equals(_bodyCheckbox)) {
			if (_bodyCheckbox.getSelection()) {
				_htmlCombo.setEnabled(true);
			} else {
				_htmlCombo.setEnabled(false);
			}
		}

		if (button.equals(_apiCheckbox)) {
			// if (_apiCheckbox.getSelection()) {
			// setPageComplete(false);
			// }
		}

	}

	protected void handleSelectionEvent(SelectionEvent event) {
		Widget w = event.widget;
		if (w instanceof Button) {
			refreshButtonSettings((Button) w);
		}
	}

	public StringButtonStatusDialogField getPackageDialogField() {
		return _packageDialogField;
	}

	public StringButtonStatusDialogField getSuperclassDialogField() {
		return _superclassDialogField;
	}

	protected class PackageButtonAdapter implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField _field) {
			IPackageFragment pack = choosePackage();
			if (pack != null) {
				getPackageDialogField().setText(pack.getElementName());
			}
		}

		public void dialogFieldChanged(DialogField _field) {
			// fPackageStatus= packageChanged();
			// updatePackageStatusLabel();
		}
	}

	protected class SuperclassButtonAdapter implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField _field) {
			String superclass = chooseSuperclass();
			if (superclass != null) {
				getSuperclassDialogField().setText(superclass);
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
