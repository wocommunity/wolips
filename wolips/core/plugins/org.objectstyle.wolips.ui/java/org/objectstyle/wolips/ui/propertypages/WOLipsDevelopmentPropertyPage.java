/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2006 The ObjectStyle Group and individual authors of the
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

import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.internal.build.Nature;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.ui.UIPlugin;
import org.objectstyle.wolips.variables.BuildProperties;

/**
 * @author ulrich
 * @author mschrag
 */
public class WOLipsDevelopmentPropertyPage extends WOLipsPropertyPage {
	private static final String BUILD_STYLE_TITLE = "Build Style";

	private static final String PROJECT_KIND_TITLE = "Bundle Settings";

	private static final String WO_NATURE_TITLE = "WebObjects Project";

	private static final String WO_IS_FRAMEWORK_TITLE = "Framework";

	private static final String WO_IS_APP_TITLE = "Application";

	private static final String WO_USE_INCREMENTAL_TITLE = "Incremental";

	private static final String WO_USE_ANT_TITLE = "Ant (build.xml)";

	private static final String WO_USE_TARGET_BUILDER_TITLE = "Use Target Builder for JavaClient";

	private Button _useTargetBuilderCheck;

	private Button _webObjectsProjectCheck;

	private Button _buildStyleAntButton;

	private Button _buildStyleIncrementalButton;

	private Button _bundleTypeFrameworkButton;

	private Button _bundleTypeApplicationButton;

	private Text _principalClassText;

	private Text _eoAdaptorClassText;

	private Text _projectFrameworkFolderText;

	/**
	 * Constructor for WOLipsProjectNaturePage.
	 */
	public WOLipsDevelopmentPropertyPage() {
		super();
	}

	private void _addWOProjectSection(Composite parent, boolean isWOProject) {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_webObjectsProjectCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		_webObjectsProjectCheck.setText(WO_NATURE_TITLE);
		_webObjectsProjectCheck.setEnabled(true);
		_webObjectsProjectCheck.setSelection(isWOProject);

		_webObjectsProjectCheck.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WOLipsDevelopmentPropertyPage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void _addBuildStyleSection(Composite parent, ProjectAdapter project) {
		Composite buildStyleGroup = _createGroupWithLabel(parent, BUILD_STYLE_TITLE);

		// project kind field (is framework?)
		_buildStyleIncrementalButton = new Button(buildStyleGroup, SWT.RADIO | SWT.LEFT);
		_buildStyleIncrementalButton.setText(WO_USE_INCREMENTAL_TITLE);
		_buildStyleIncrementalButton.setEnabled(true);
		FormData incrementalLayoutData = new FormData();
		incrementalLayoutData.left = new FormAttachment(0, 0);
		_buildStyleIncrementalButton.setLayoutData(incrementalLayoutData);
		_buildStyleIncrementalButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WOLipsDevelopmentPropertyPage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		_buildStyleAntButton = new Button(buildStyleGroup, SWT.RADIO | SWT.LEFT);
		_buildStyleAntButton.setText(WO_USE_ANT_TITLE);
		_buildStyleAntButton.setEnabled(true);
		FormData antLayoutData = new FormData();
		antLayoutData.top = new FormAttachment(_buildStyleIncrementalButton, 5);
		_buildStyleAntButton.setLayoutData(antLayoutData);

		boolean isIncremental = (project == null || project.isIncrementalBuilderInstalled());
		if (isIncremental) {
			_buildStyleIncrementalButton.setSelection(true);
		} else {
			_buildStyleAntButton.setSelection(true);
		}

		_useTargetBuilderCheck = new Button(buildStyleGroup, SWT.CHECK | SWT.LEFT);
		_useTargetBuilderCheck.setText(WO_USE_TARGET_BUILDER_TITLE);
		_useTargetBuilderCheck.setEnabled(true);
		_useTargetBuilderCheck.setSelection(project != null && project.isTargetBuilderInstalled());
		FormData targetBuilderData = new FormData();
		targetBuilderData.top = new FormAttachment(_buildStyleAntButton, 15);
		_useTargetBuilderCheck.setLayoutData(targetBuilderData);

		Composite textSettingsGroup = new Composite(buildStyleGroup, SWT.NONE);
		GridLayout textSettingsLayout = new GridLayout(2, false);
		textSettingsGroup.setLayout(textSettingsLayout);
		FormData textSettingsLayoutData = new FormData();
		textSettingsLayoutData.top = new FormAttachment(_useTargetBuilderCheck, 10);
		textSettingsLayoutData.left = new FormAttachment(0, 0);
		textSettingsLayoutData.right = new FormAttachment(100, 0);
		textSettingsGroup.setLayoutData(textSettingsLayoutData);

		_projectFrameworkFolderText = _addTextField(textSettingsGroup, "Framework Folder");
		if (project != null) {
			String projectFrameworkFolder = getBuildProperties().getProjectFrameworkFolder();
			if (projectFrameworkFolder == null) {
				projectFrameworkFolder = "";
			}
			_projectFrameworkFolderText.setText(projectFrameworkFolder);
		}
	}

	private void _addBundleSettingsSection(Composite parent, ProjectAdapter project) {
		Composite bundleTypeGroup = _createGroupWithLabel(parent, PROJECT_KIND_TITLE);

		_bundleTypeApplicationButton = new Button(bundleTypeGroup, SWT.RADIO | SWT.LEFT);
		_bundleTypeApplicationButton.setText(WO_IS_APP_TITLE);
		_bundleTypeApplicationButton.setEnabled(true);
		FormData applicationLayoutData = new FormData();
		applicationLayoutData.left = new FormAttachment(0, 0);
		_bundleTypeApplicationButton.setLayoutData(applicationLayoutData);
		_bundleTypeApplicationButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WOLipsDevelopmentPropertyPage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		_bundleTypeFrameworkButton = new Button(bundleTypeGroup, SWT.RADIO | SWT.LEFT);
		_bundleTypeFrameworkButton.setText(WO_IS_FRAMEWORK_TITLE);
		_bundleTypeFrameworkButton.setEnabled(true);
		FormData frameworkLayoutData = new FormData();
		frameworkLayoutData.top = new FormAttachment(_bundleTypeApplicationButton, 5);
		_bundleTypeFrameworkButton.setLayoutData(frameworkLayoutData);

		boolean isFramework = project == null || project.isFramework();
		if (isFramework) {
			_bundleTypeFrameworkButton.setSelection(true);
		} else {
			_bundleTypeApplicationButton.setSelection(true);
		}

		Composite textSettingsGroup = new Composite(bundleTypeGroup, SWT.NONE);
		GridLayout textSettingsLayout = new GridLayout(2, false);
		textSettingsGroup.setLayout(textSettingsLayout);
		FormData textSettingsLayoutData = new FormData();
		textSettingsLayoutData.top = new FormAttachment(_bundleTypeFrameworkButton, 10);
		textSettingsLayoutData.left = new FormAttachment(0, 0);
		textSettingsLayoutData.right = new FormAttachment(100, 0);
		textSettingsGroup.setLayoutData(textSettingsLayoutData);
		_principalClassText = _addTextField(textSettingsGroup, "Principal Class");
		if (project != null) {
			_principalClassText.setText(project.getBuildProperties().getPrincipalClass(true));
		}

		_eoAdaptorClassText = _addTextField(textSettingsGroup, "EOAdaptor Class");
		if (project != null) {
			_eoAdaptorClassText.setText(project.getBuildProperties().getEOAdaptorClassName(true));
		}
	}

	protected void enableWidgets() {
		_useTargetBuilderCheck.setEnabled(_buildStyleIncrementalButton.getSelection());
		_principalClassText.setEnabled(true);
		_eoAdaptorClassText.setEnabled(!_bundleTypeApplicationButton.getSelection());
	}

	@Override
	protected void _createContents(Composite parent, ProjectAdapter projectAdapter, boolean isWOProject) {
		_addWOProjectSection(parent, isWOProject);
		_addBuildStyleSection(parent, projectAdapter);
		_addBundleSettingsSection(parent, projectAdapter);

		enableWidgets();
	}

	@Override
	protected void setDefaults(ProjectAdapter project) {
		Map args = project.getBuilderArgs();
		String string = _getArg(args, BuilderPlugin.NS_PRINCIPAL_CLASS, "");
		if (string == null || string.length() == 0) {
			string = project.getBuildProperties().getPrincipalClass(true);
		}
		if (string != null) {
			_principalClassText.setText(string);
		}

		string = project.getBuildProperties().getEOAdaptorClassName(true);
		if (string != null) {
			_eoAdaptorClassText.setText(string);
		}
	}

	public boolean performOk() {
		// store the value in the owner text field
		try {
			boolean useTargetBuilder = _useTargetBuilderCheck.getSelection();
			if (_webObjectsProjectCheck.getSelection()) {
				if (_bundleTypeFrameworkButton.getSelection()) {
					if (_buildStyleIncrementalButton.getSelection()) {
						Nature.setNatureForProject(Nature.INCREMENTAL_FRAMEWORK_ID, useTargetBuilder, getProject(), new NullProgressMonitor());
					} else if (_buildStyleAntButton.getSelection()) {
						Nature.setNatureForProject(Nature.ANT_FRAMEWORK_ID, useTargetBuilder, getProject(), new NullProgressMonitor());
					}
				} else if (_bundleTypeApplicationButton.getSelection()) {
					if (_buildStyleIncrementalButton.getSelection()) {
						Nature.setNatureForProject(Nature.INCREMENTAL_APPLICATION_ID, useTargetBuilder, getProject(), new NullProgressMonitor());
					} else if (_buildStyleAntButton.getSelection()) {
						Nature.setNatureForProject(Nature.ANT_APPLICATION_ID, useTargetBuilder, getProject(), new NullProgressMonitor());
					}
				}
				ProjectAdapter project = getProjectAdapter();
				if (project != null) {
					BuildProperties buildProperties = getBuildProperties();
					buildProperties.setPrincipalClass(_principalClassText.getText());
					buildProperties.setEOAdaptorClassName(_eoAdaptorClassText.getText());
					String projectFrameworkFolderText = _projectFrameworkFolderText.getText();
					if (projectFrameworkFolderText.length() == 0) {
						buildProperties.setProjectFrameworkFolder(null);
					} else {
						buildProperties.setProjectFrameworkFolder(projectFrameworkFolderText);
					}
					buildProperties.setFramework(_bundleTypeFrameworkButton.getSelection());
					buildProperties.save();
				}
			} else {
				Nature.removeNaturesFromProject(getProject(), new NullProgressMonitor());
			}
		} catch (Exception up) {
			UIPlugin.getDefault().log(up);
			return false;
		}
		return true;
	}
}