/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2004 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.projectbuild.natures;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.objectstyle.wolips.datasets.project.INaturesAccessor;
import org.objectstyle.wolips.datasets.project.IWOLipsProject;
import org.objectstyle.wolips.datasets.project.WOLipsCore;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProjectNaturePage extends PropertyPage
		implements
			IAdaptable {
	private static final String BUILD_STYLE_TITLE = " Build style";
	private static final String BUILD_PARAMS_TITLE = " Build parameters";
	private static final String PROJECT_KIND_TITLE = " Project kind";
	private static final String PROJECT_KIND_NOTE_TITLE = "Note: ";
	private static final String PROJECT_KIND_NOTE = "The settings below will only affect the incremental build style.";
	private static final String WO_NATURE_TITLE = "Is a WebObjects Project (options below apply only if this is checked)";
	private static final String WO_IS_FRAMEWORK_TITLE = "Framework";
	private static final String WO_IS_APP_TITLE = "Application";
	private static final String WO_USE_INCREMENTAL_TITLE = "Incremental";
	private static final String WO_USE_ANT_TITLE = "Use Ant (build.xml)";
	private static final String WO_USE_TARGET_BUILDET_TITLE = "Use TargetBuilder";
	/**
	 * Constructor for WOLipsProjectNaturePage.
	 */
	public ProjectNaturePage() {
		super();
	}
	/**
	 * @param parent
	 * @param woLipsProject
	 * @throws CoreException
	 */
	private void _addFirstSection(Composite parent, IWOLipsProject woLipsProject)
			throws CoreException {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_woNatureCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		_woNatureCheck.setText(WO_NATURE_TITLE);
		_woNatureCheck.setEnabled(true);
		_woNatureCheck.setSelection(woLipsProject.getNaturesAccessor()
				.hasWOLipsNature());
	}
	/**
	 * @param parent
	 * @param woLipsProject
	 * @throws CoreException
	 */
	private void _addBuildStyleSection(Composite parent,
			IWOLipsProject woLipsProject) throws CoreException {
		Composite group = _createLabelledComposite(parent, BUILD_STYLE_TITLE);
		// project kind field (is framework?)
		_woIsIncrementalButton = new Button(group, SWT.RADIO | SWT.LEFT);
		_woIsIncrementalButton.setText(WO_USE_INCREMENTAL_TITLE);
		_woIsIncrementalButton.setEnabled(true);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		_woIsIncrementalButton.setLayoutData(fd);
		Button antButton = new Button(group, SWT.RADIO | SWT.LEFT);
		antButton.setText(WO_USE_ANT_TITLE);
		antButton.setEnabled(true);
		fd = new FormData();
		fd.left = new FormAttachment(_woIsIncrementalButton, 0);
		antButton.setLayoutData(fd);
		boolean isIncremental = woLipsProject.getNaturesAccessor()
				.isIncremental();
		_woIsIncrementalButton.setSelection(isIncremental);
		antButton.setSelection(!isIncremental);
		Label noteTitle = new Label(group, SWT.BOLD);
		noteTitle.setText(PROJECT_KIND_NOTE_TITLE);
		noteTitle.setFont(JFaceResources.getBannerFont());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(_woIsIncrementalButton, 5);
		noteTitle.setLayoutData(fd);
		Label note = new Label(group, SWT.NULL);
		note.setText(PROJECT_KIND_NOTE);
		fd = new FormData();
		fd.left = new FormAttachment(noteTitle, 0);
		fd.top = new FormAttachment(_woIsIncrementalButton, 5);
		note.setLayoutData(fd);
	}
	/**
	 * @param parent
	 * @param woLipsProject
	 * @throws CoreException
	 */
	private void _addProjectKindSection(Composite parent,
			IWOLipsProject woLipsProject) throws CoreException {
		Composite group = _createLabelledComposite(parent, PROJECT_KIND_TITLE);
		// project kind field (is framework?)
		_woIsApplicationButton = new Button(group, SWT.RADIO | SWT.LEFT);
		_woIsApplicationButton.setText(WO_IS_APP_TITLE);
		_woIsApplicationButton.setEnabled(true);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		_woIsApplicationButton.setLayoutData(fd);
		_woIsFrameworkButton = new Button(group, SWT.RADIO | SWT.LEFT);
		_woIsFrameworkButton.setText(WO_IS_FRAMEWORK_TITLE);
		_woIsFrameworkButton.setEnabled(true);
		fd = new FormData();
		fd.left = new FormAttachment(_woIsApplicationButton, 0);
		_woIsFrameworkButton.setLayoutData(fd);
		boolean isFramework = woLipsProject.getNaturesAccessor().isFramework();
		_woIsFrameworkButton.setSelection(isFramework);
		_woIsApplicationButton.setSelection(!isFramework);
	}
	private Text _addTextField(Composite parent, String label) {
		GridData gd = new GridData();
		Label textLabel = new Label(parent, SWT.NONE);
		textLabel.setText(label);
		textLabel.setLayoutData(gd);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		// Owner text field
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(gd);
		return (text);
	}
	private static String _getArg(Map values, String key, String defVal) {
		String result = null;
		try {
			result = (String) values.get(key);
		} catch (Exception up) {
			// hmm, how did that get there?
		}
		if (null == result)
			result = defVal;
		return result;
	}
	/**
	 * @param parent
	 */
	private void _addPatternSection(Composite parent) {
		Composite group = _createLabelledComposite(parent, BUILD_PARAMS_TITLE);
		group.setLayout(new GridLayout(2, false));
		_principalClass = _addTextField(group, "Principal Class");
	}
	/**
	 * @param parent
	 * @param woLipsProject
	 * @throws CoreException
	 */
	private void _addTargetBuilderSection(Composite parent,
			IWOLipsProject woLipsProject) throws CoreException {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_woTargetBuilderCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		_woTargetBuilderCheck.setText(WO_USE_TARGET_BUILDET_TITLE);
		_woTargetBuilderCheck.setEnabled(true);
		_woTargetBuilderCheck.setSelection(woLipsProject.getNaturesAccessor()
				.isTargetBuilderInstalled());
	}
	private void enableWidgets(boolean enabled) {
		_woTargetBuilderCheck.setEnabled(enabled);
		_woIsFrameworkButton.setEnabled(enabled);
		_woIsApplicationButton.setEnabled(enabled);
		_principalClass.setEnabled(enabled);
	}
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		try {
			IWOLipsProject woLipsProject = this.getWOLipsProject();
			_addFirstSection(composite, woLipsProject);
			// --
			_addBuildStyleSection(composite, woLipsProject);
			// --
			_addProjectKindSection(composite, woLipsProject);
			// --
			_addPatternSection(composite);
			_addTargetBuilderSection(composite, woLipsProject);
			_woIsIncrementalButton
					.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							enableWidgets(_woIsIncrementalButton.getSelection());
						}
						public void widgetDefaultSelected(SelectionEvent e) {
							enableWidgets(_woIsIncrementalButton.getSelection());
						}
					});
			setDefaults(woLipsProject);
			enableWidgets(_woIsIncrementalButton.getSelection());
		} catch (CoreException exception) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(exception);
		}
		return composite;
	}
	private Composite _createLabelledComposite(Composite parent, String label) {
		Group composite = new Group(parent, SWT.NULL);
		FormLayout layout = new FormLayout();
		//layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		composite.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		if (null != label) {
			composite.setText(label);
		}
		return composite;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		try {
			setDefaults(getWOLipsProject());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	/**
	 *  
	 */
	private void setDefaults(IWOLipsProject woLipsProject) {
		Map args = woLipsProject.getBuilderAccessor().getBuilderArgs();
		_principalClass.setText(_getArg(args, ProjectBuildPlugin.NS_PRINCIPAL_CLASS, ""));
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		// store the value in the owner text field
		IWOLipsProject woLipsProject;
		try {
			woLipsProject = this.getWOLipsProject();
			INaturesAccessor naturesAccessor = woLipsProject
					.getNaturesAccessor();
			if (_woNatureCheck.getSelection()) {
				if (_woIsIncrementalButton.getSelection()) {
					Map args = new HashMap();
					args.put(ProjectBuildPlugin.NS_PRINCIPAL_CLASS, _principalClass.getText());
					naturesAccessor.setIncrementalNature(_woIsFrameworkButton
							.getSelection(), args);
				} else {
					naturesAccessor.setAntNature(_woIsFrameworkButton
							.getSelection());
				}
			} else {
				naturesAccessor.removeWOLipsNatures();
			}
			boolean selection = _woTargetBuilderCheck.getSelection();
			naturesAccessor.useTargetBuilder(selection);
		} catch (CoreException up) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(up);
			return false;
		} finally {
			woLipsProject = null;
		}
		return true;
	}
	/**
	 * @return IJavaProject
	 * @throws CoreException
	 */
	public IJavaProject _getJavaProject() throws CoreException {
		IProject project = (IProject) (this.getElement()
				.getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}
	/**
	 * @return IProject
	 * @throws CoreException
	 */
	public IProject _getProject() {
		IProject project = (IProject) (this.getElement()
				.getAdapter(IProject.class));
		return (project);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class theClass) {
		return (Platform.getAdapterManager().getAdapter(this, theClass));
	}
	/**
	 * @return WOLipsProject
	 * @throws CoreException
	 */
	public IWOLipsProject getWOLipsProject() throws CoreException {
		return WOLipsCore.createProject(this._getProject());
	}
	private Button _woTargetBuilderCheck;
	private Button _woNatureCheck;
	private Button _woIsIncrementalButton;
	private Button _woIsFrameworkButton;
	private Button _woIsApplicationButton;
	private Text _principalClass;
}