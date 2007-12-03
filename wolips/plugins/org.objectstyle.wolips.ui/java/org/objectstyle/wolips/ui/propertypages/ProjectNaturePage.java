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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.internal.build.Nature;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.ui.UIPlugin;

/**
 * @author ulrich
 * @author mschrag
 */
public class ProjectNaturePage extends PropertyPage implements IAdaptable {
	private static final String BUILD_STYLE_TITLE = "Build Style";

	private static final String PROJECT_KIND_TITLE = "Bundle Settings";

	private static final String WO_NATURE_TITLE = "WebObjects Project";

	private static final String WO_IS_FRAMEWORK_TITLE = "Framework";

	private static final String WO_IS_APP_TITLE = "Application";

	private static final String WO_USE_INCREMENTAL_TITLE = "Incremental";

	private static final String WO_USE_ANT_TITLE = "Ant (build.xml)";

	private static final String WO_USE_TARGET_BUILDER_TITLE = "Use Target Builder for JavaClient";

	private Button _servletDeploymentCheck;

	private Button _generateWebXMLCheck;

	private Button _useTargetBuilderCheck;

	private Button _webObjectsProjectCheck;

	private Button _buildStyleAntButton;

	private Button _buildStyleIncrementalButton;

	private Button _bundleTypeFrameworkButton;

	private Button _bundleTypeApplicationButton;

	private Text _principalClassText;

	private Text _customInfoPListText;

	private Text _customWebXMLText;

	private Text _eoAdaptorClassText;

	/**
	 * Constructor for WOLipsProjectNaturePage.
	 */
	public ProjectNaturePage() {
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
				ProjectNaturePage.this.enableWidgets();
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
				ProjectNaturePage.this.enableWidgets();
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
				ProjectNaturePage.this.enableWidgets();
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
			_principalClassText.setText(project.getPrincipalClass(true));
		}

		_eoAdaptorClassText = _addTextField(textSettingsGroup, "EOAdaptor Class");
		if (project != null) {
			_eoAdaptorClassText.setText(project.getEOAdaptorClassName(true));
		}

		_customInfoPListText = _addTextArea(textSettingsGroup, "Custom Info.plist");
		if (project != null) {
			_customInfoPListText.setText(project.getCustomInfoPListContent(true));
		}
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
		Label textLabel = new Label(parent, SWT.NONE);
		textLabel.setText(label);
		GridData labelData = new GridData();
		labelData.horizontalSpan = 2;
		labelData.verticalAlignment = SWT.BEGINNING;
		labelData.verticalIndent = 7;
		textLabel.setLayoutData(labelData);

		GridData textData = new GridData();
		textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.horizontalSpan = 2;
		textData.horizontalIndent = 0;
		textData.grabExcessHorizontalSpace = true;
		textData.grabExcessVerticalSpace = true;
		textData.heightHint = 70;
		// Owner text field
		// Scrollable scrollable = new Scrollable(parent, SWT.H_SCROLL |
		// SWT.V_SCROLL);
		Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setLayoutData(textData);
		return (text);
	}

	private static String _getArg(Map values, String key, String defVal) {
		String result = null;
		try {
			result = (String) values.get(key);
		} catch (Exception up) {
			// hmm, how did that get there?
		}
		if (null == result) {
			result = defVal;
		}
		return result;
	}

	private void addServletDeploymentSection(Composite parent, ProjectAdapter project) {
		Composite group = _createGroupWithLabel(parent, "Servlet Deployment");
		group.setLayout(new GridLayout(2, false));

		_servletDeploymentCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		GridData servletDeploymentData = new GridData();
		servletDeploymentData.horizontalSpan = 2;
		_servletDeploymentCheck.setLayoutData(servletDeploymentData);
		_servletDeploymentCheck.setText("Servlet Deployment");
		_servletDeploymentCheck.setEnabled(true);
		_servletDeploymentCheck.setSelection(project != null && project.isServletDeployment());
		_servletDeploymentCheck.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ProjectNaturePage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		_generateWebXMLCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		GridData generateWebXmlData = new GridData();
		generateWebXmlData.horizontalSpan = 2;
		_generateWebXMLCheck.setLayoutData(generateWebXmlData);
		_generateWebXMLCheck.setText("Autogenerate web.xml");
		_generateWebXMLCheck.setEnabled(true);
		_generateWebXMLCheck.setSelection(project != null && project.getWebXML());
		_generateWebXMLCheck.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ProjectNaturePage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		_customWebXMLText = _addTextArea(group, "Custom web.xml");
		if (project != null) {
			_customWebXMLText.setText(project.getWebXML_CustomContent(true));
		}
	}

	protected void enableWidgets() {
		_useTargetBuilderCheck.setEnabled(_buildStyleIncrementalButton.getSelection());
		
		_principalClassText.setEnabled(true);
		_eoAdaptorClassText.setEnabled(!_bundleTypeApplicationButton.getSelection());
		_customInfoPListText.setEnabled(true);
		
		_servletDeploymentCheck.setEnabled(_bundleTypeApplicationButton.getSelection());
		_generateWebXMLCheck.setEnabled(_bundleTypeApplicationButton.getSelection() && _servletDeploymentCheck.getSelection());
		_customWebXMLText.setEnabled(_bundleTypeApplicationButton.getSelection() && _servletDeploymentCheck.getSelection() && !_generateWebXMLCheck.getSelection());
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = 0;
		layout.marginRight = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		ProjectAdapter project = getProjectAdaptor();
		boolean isWOProject = (project != null);
		if (!isWOProject) {
			project = new ProjectAdapter(getProject(), false);
		}
		_addWOProjectSection(composite, isWOProject);
		_addBuildStyleSection(composite, project);
		_addBundleSettingsSection(composite, project);
		addServletDeploymentSection(composite, project);

		enableWidgets();

		return composite;
	}

	protected Composite _createGroupWithLabel(Composite parent, String label) {
		Group composite = new Group(parent, SWT.NULL);
		FormLayout layout = new FormLayout();
		// layout.numColumns = 2;
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

	protected void performDefaults() {
		setDefaults(getProjectAdaptor());
	}

	private void setDefaults(ProjectAdapter project) {
		Map args = project.getBuilderArgs();
		String string = _getArg(args, BuilderPlugin.NS_PRINCIPAL_CLASS, "");
		if (string == null || string.length() == 0) {
			string = project.getPrincipalClass(true);
		}
		if (string != null) {
			_principalClassText.setText(string);
		}

		string = project.getCustomInfoPListContent(true);
		if (string != null) {
			_customInfoPListText.setText(string);
		}

		string = project.getEOAdaptorClassName(true);
		if (string != null) {
			_eoAdaptorClassText.setText(string);
		}

		_customWebXMLText.setText("");
		_servletDeploymentCheck.setSelection(false);
		_generateWebXMLCheck.setSelection(false);
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
				ProjectAdapter project = getProjectAdaptor();
				project.setPrincipalClass(_principalClassText.getText());
				project.setCustomInfoPListContent(_customInfoPListText.getText());
				project.setServletDeployment(_servletDeploymentCheck.getSelection());
				project.setWebXML(_generateWebXMLCheck.getSelection());
				project.setWebXML_CustomContent(_customWebXMLText.getText());
				project.setEOAdaptorClassName(_eoAdaptorClassText.getText());
			} else {
				Nature.removeNaturesFromProject(getProject(), new NullProgressMonitor());
			}
		} catch (CoreException up) {
			UIPlugin.getDefault().log(up);
			return false;
		}
		return true;
	}

	public IJavaProject getJavaProject() {
		return JavaCore.create((IProject) getElement().getAdapter(IProject.class));
	}

	public IProject getProject() {
		return (IProject) getElement().getAdapter(IProject.class);
	}

	public ProjectAdapter getProjectAdaptor() {
		return (ProjectAdapter) getProject().getAdapter(IProjectAdapter.class);
	}

	public Object getAdapter(Class theClass) {
		return Platform.getAdapterManager().getAdapter(this, theClass);
	}
}