/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002, 2004 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.ui.propertypages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.preferences.formatter.BlankLinesTabPage;
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
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;
import org.objectstyle.wolips.ui.UIPlugin;

/**
 * @author ulrich
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProjectNaturePage extends PropertyPage implements IAdaptable {
	private static final String BUILD_STYLE_TITLE = " Build style";

	private static final String BUILD_PARAMS_TITLE = " Build parameters";

	private static final String PROJECT_KIND_TITLE = " Project kind";

	private static final String PROJECT_KIND_NOTE_TITLE = "Note: ";

	private static final String PROJECT_KIND_NOTE = "Setting will only affect the incremental build style.";

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
	 * @param project
	 * @throws CoreException
	 */
	private void _addFirstSection(Composite parent, Project project)
			throws CoreException {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this._woNatureCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		this._woNatureCheck.setText(WO_NATURE_TITLE);
		this._woNatureCheck.setEnabled(true);
		this._woNatureCheck.setSelection(project.hasWOLipsNature());
	}

	/**
	 * @param parent
	 * @param project
	 * @throws CoreException
	 */
	private void _addBuildStyleSection(Composite parent, Project project)
			throws CoreException {
		Composite group = _createLabelledComposite(parent, BUILD_STYLE_TITLE);
		// project kind field (is framework?)
		this._woIsIncrementalButton = new Button(group, SWT.RADIO | SWT.LEFT);
		this._woIsIncrementalButton.setText(WO_USE_INCREMENTAL_TITLE);
		this._woIsIncrementalButton.setEnabled(true);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		this._woIsIncrementalButton.setLayoutData(fd);
		Button antButton = new Button(group, SWT.RADIO | SWT.LEFT);
		antButton.setText(WO_USE_ANT_TITLE);
		antButton.setEnabled(true);
		fd = new FormData();
		fd.left = new FormAttachment(this._woIsIncrementalButton, 0);
		antButton.setLayoutData(fd);
		boolean isIncremental = project.isIncremental();
		this._woIsIncrementalButton.setSelection(isIncremental);
		antButton.setSelection(!isIncremental);
	}

	/**
	 * @param parent
	 * @param project
	 * @throws CoreException
	 */
	private void _addProjectKindSection(Composite parent, Project project)
			throws CoreException {
		Composite group = _createLabelledComposite(parent, PROJECT_KIND_TITLE);
		// project kind field (is framework?)
		this._woIsApplicationButton = new Button(group, SWT.RADIO | SWT.LEFT);
		this._woIsApplicationButton.setText(WO_IS_APP_TITLE);
		this._woIsApplicationButton.setEnabled(true);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		this._woIsApplicationButton.setLayoutData(fd);
		this._woIsFrameworkButton = new Button(group, SWT.RADIO | SWT.LEFT);
		this._woIsFrameworkButton.setText(WO_IS_FRAMEWORK_TITLE);
		this._woIsFrameworkButton.setEnabled(true);
		fd = new FormData();
		fd.left = new FormAttachment(this._woIsApplicationButton, 0);
		this._woIsFrameworkButton.setLayoutData(fd);
		boolean isFramework = project.isFramework();
		this._woIsFrameworkButton.setSelection(isFramework);
		this._woIsApplicationButton.setSelection(!isFramework);
		Label noteTitle = new Label(group, SWT.BOLD);
		noteTitle.setText(PROJECT_KIND_NOTE_TITLE);
		noteTitle.setFont(JFaceResources.getBannerFont());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(this._woIsApplicationButton, 5);
		noteTitle.setLayoutData(fd);
		Label note = new Label(group, SWT.NULL);
		note.setText(PROJECT_KIND_NOTE);
		fd = new FormData();
		fd.left = new FormAttachment(noteTitle, 0);
		fd.top = new FormAttachment(this._woIsApplicationButton, 5);
		note.setLayoutData(fd);
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

	private Text _addTextArea(Composite parent, String label) {
		GridData gd = new GridData();
		Label textLabel = new Label(parent, SWT.NONE);
		textLabel.setText(label);
		textLabel.setLayoutData(gd);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 70;
		// Owner text field
		//Scrollable scrollable = new Scrollable(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
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
	private void _addPatternSection(Composite parent, Project project) {
		Composite group = _createLabelledComposite(parent, BUILD_PARAMS_TITLE);
		group.setLayout(new GridLayout(2, false));
		this.principalClass = _addTextField(group, "Principal Class");
		this.principalClass.setText(project.getPrincipalClass());
		this.eoAdaptorClassName = _addTextField(group, "EOAdaptorClassName");
		this.eoAdaptorClassName.setText(project.getEOAdaptorClassName());
		this.customInfoPListContent = _addTextArea(group,
				"Custom Info.plist content");
		this.customInfoPListContent.setText(project.getCustomInfoPListContent());
	}

	/**
	 * @param parent
	 * @param project
	 */
	private void _addTargetBuilderSection(Composite parent, Project project) {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this._woTargetBuilderCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		this._woTargetBuilderCheck.setText(WO_USE_TARGET_BUILDET_TITLE);
		this._woTargetBuilderCheck.setEnabled(true);
		this._woTargetBuilderCheck.setSelection(project
				.isTargetBuilderInstalled());
	}

	/**
	 * @param parent
	 * @param project
	 */
	private void addWebXMLSection(Composite parent, Project project) {
		Composite group = _createLabelledComposite(parent, "web.xml");
		group.setLayout(new GridLayout(2, false));
		this._woWebXMLCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		this._woWebXMLCheck.setText("Generate web.xml");
		this._woWebXMLCheck.setEnabled(true);
		this._woWebXMLCheck.setSelection(project.getWebXML());
		Label textLabel = new Label(group, SWT.NONE);
		textLabel.setText("");
		this.webXMLCustomContent = _addTextArea(group,
		"Custom web.xml content");
		this.webXMLCustomContent.setText(project.getWebXML_CustomContent());
	}

	void enableWidgets(boolean enabled) {
		this._woWebXMLCheck.setEnabled(enabled);
		this._woTargetBuilderCheck.setEnabled(enabled);
		this._woIsFrameworkButton.setEnabled(enabled);
		this._woIsApplicationButton.setEnabled(enabled);
		this.principalClass.setEnabled(true);
		this.customInfoPListContent.setEnabled(true);
		this.eoAdaptorClassName.setEnabled(true);
		this.webXMLCustomContent.setEnabled(true);
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
			Project project = this.getProject();
			_addFirstSection(composite, project);
			// --
			_addBuildStyleSection(composite, project);
			// --
			_addProjectKindSection(composite, project);
			// --
			_addPatternSection(composite, project);
			_addTargetBuilderSection(composite, project);
			this._woIsIncrementalButton
					.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							enableWidgets(ProjectNaturePage.this._woIsIncrementalButton
									.getSelection());
						}

						public void widgetDefaultSelected(SelectionEvent e) {
							enableWidgets(ProjectNaturePage.this._woIsIncrementalButton
									.getSelection());
						}
					});
			addWebXMLSection(composite, project);
			//setDefaults(project);
			enableWidgets(this._woIsIncrementalButton.getSelection());
		} catch (CoreException exception) {
			UIPlugin.getDefault().getPluginLogger().log(exception);
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
		setDefaults(getProject());
	}

	/**
	 * @param project
	 *  
	 */
	private void setDefaults(Project project) {
		Map args = project.getBuilderArgs();
		String string = _getArg(args, ProjectBuildPlugin.NS_PRINCIPAL_CLASS, "");
		if (string == null || string.length() == 0) {
			string = project.getPrincipalClass();
		}
		if (string != null) {
			this.principalClass.setText(string);
		}
		string = project.getCustomInfoPListContent();

		if (string != null) {
			this.customInfoPListContent.setText(string);
		}
		string = project.getEOAdaptorClassName();

		if (string != null) {
			this.eoAdaptorClassName.setText(string);
		}
		this.webXMLCustomContent.setText("");
		this._woWebXMLCheck.setSelection(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		// store the value in the owner text field
		Project project;
		try {
			project = this.getProject();
			if (this._woNatureCheck.getSelection()) {
				project.setPrincipalClass(this.principalClass.getText());
				project.setCustomInfoPListContent(this.customInfoPListContent
						.getText());
				project.setWebXML_CustomContent(this.webXMLCustomContent
						.getText());
				project.setWebXML(this._woWebXMLCheck.getSelection());
				project
						.setEOAdaptorClassName(this.eoAdaptorClassName
								.getText());
				if (this._woIsIncrementalButton.getSelection()) {
					Map args = new HashMap();
					project.setIncrementalNature(this._woIsFrameworkButton
							.getSelection(), args);
				} else {
					project.setAntNature(this._woIsFrameworkButton
							.getSelection());
				}
			} else {
				project.removeWOLipsNatures();
			}
			boolean selection = this._woTargetBuilderCheck.getSelection();
			project.useTargetBuilder(selection);
		} catch (CoreException up) {
			UIPlugin.getDefault().getPluginLogger().log(up);
			return false;
		} finally {
			project = null;
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
	 * @return Project
	 */
	public Project getProject() {
		return (Project) (this._getProject()).getAdapter(Project.class);
	}

	private Button _woWebXMLCheck;

	private Button _woTargetBuilderCheck;

	private Button _woNatureCheck;

	Button _woIsIncrementalButton;

	private Button _woIsFrameworkButton;

	private Button _woIsApplicationButton;

	private Text principalClass;

	private Text customInfoPListContent;

	private Text webXMLCustomContent;

	private Text eoAdaptorClassName;
}