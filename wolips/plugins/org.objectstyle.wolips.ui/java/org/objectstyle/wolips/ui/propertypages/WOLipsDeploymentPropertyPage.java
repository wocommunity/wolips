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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.woenvironment.frameworks.Root;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.ProjectFrameworkAdapter;
import org.objectstyle.wolips.ui.UIPlugin;
import org.objectstyle.wolips.variables.BuildProperties;

public class WOLipsDeploymentPropertyPage extends WOLipsPropertyPage {
	private Button _servletDeploymentCheck;

	private Button _generateWebXMLCheck;

	private Map<Root, Button> _embedButtons;

	private Text _customInfoPListText;

	private Text _customWebXMLText;

	private Button _javaClientButton;

	private Button _javaWebStartButton;

	private void _addEmbedSettingsSection(Composite parent, ProjectAdapter project) {
		Composite embedGroup = _createGroupWithLabel(parent, "Embed Frameworks");
		embedGroup.setLayout(new GridLayout(1, false));

		_embedButtons = new HashMap<Root, Button>();
		for (Root root : JdtPlugin.getDefault().getFrameworkModel(project.getUnderlyingProject()).getRoots()) {
			Button embedButton = new Button(embedGroup, SWT.CHECK | SWT.LEFT);
			embedButton.setText(root.getName());
			embedButton.setEnabled(true);
			embedButton.setSelection(getBuildProperties().isEmbed(root));
			embedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			_embedButtons.put(root, embedButton);
		}
	}

	private void _addCustomInfoPListSettingsSection(Composite parent, ProjectAdapter project) {
		Composite customInfoPListGroup = _createGroupWithLabel(parent, "Custom Info.plist");
		customInfoPListGroup.setLayout(new GridLayout(2, false));

		_customInfoPListText = _addTextArea(customInfoPListGroup, null);
		if (project != null) {
			_customInfoPListText.setText(getBuildProperties().getCustomInfoPListContent(true));
		}
	}

	private void _addJavaClientSection(Composite parent) {
		Composite javaClientGroup = _createGroupWithLabel(parent, "Java Client");
		javaClientGroup.setLayout(new GridLayout(1, false));

		_javaClientButton = new Button(javaClientGroup, SWT.CHECK | SWT.LEFT);
		_javaClientButton.setText("Java Client");
		_javaClientButton.setEnabled(true);
		_javaClientButton.setSelection(getBuildProperties().isJavaClient());
		_javaClientButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WOLipsDeploymentPropertyPage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		_javaWebStartButton = new Button(javaClientGroup, SWT.CHECK | SWT.LEFT);
		_javaWebStartButton.setText("Java WebStart");
		_javaWebStartButton.setEnabled(true);
		_javaWebStartButton.setSelection(getBuildProperties().isJavaWebStart());
	}

	private void _addServletDeploymentSection(Composite parent) {
		Composite group = _createGroupWithLabel(parent, "Servlet Deployment");
		group.setLayout(new GridLayout(2, false));

		_servletDeploymentCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		GridData servletDeploymentData = new GridData();
		servletDeploymentData.horizontalSpan = 2;
		_servletDeploymentCheck.setLayoutData(servletDeploymentData);
		_servletDeploymentCheck.setText("Servlet Deployment");
		_servletDeploymentCheck.setEnabled(true);
		_servletDeploymentCheck.setSelection(getBuildProperties().isServletDeployment());
		_servletDeploymentCheck.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WOLipsDeploymentPropertyPage.this.enableWidgets();
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
		_generateWebXMLCheck.setSelection(getBuildProperties().getWebXML());
		_generateWebXMLCheck.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WOLipsDeploymentPropertyPage.this.enableWidgets();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		_customWebXMLText = _addTextArea(group, "Custom web.xml");
		_customWebXMLText.setText(getBuildProperties().getWebXML_CustomContent(true));
	}

	protected void enableWidgets() {
		_customInfoPListText.setEnabled(true);

		boolean isApplication = getProjectAdapter().isApplication();
		_servletDeploymentCheck.setEnabled(isApplication);
		_generateWebXMLCheck.setEnabled(isApplication && _servletDeploymentCheck.getSelection());
		_customWebXMLText.setEnabled(isApplication && _servletDeploymentCheck.getSelection() && !_generateWebXMLCheck.getSelection());

		for (Button embedButton : _embedButtons.values()) {
			embedButton.setEnabled(isApplication && !_servletDeploymentCheck.getSelection());
		}

		_javaWebStartButton.setEnabled(_javaClientButton.getSelection());
	}

	@Override
	protected void _createContents(Composite parent, ProjectAdapter projectAdapter, boolean isWOProject) {
		_addCustomInfoPListSettingsSection(parent, projectAdapter);
		_addServletDeploymentSection(parent);
		_addJavaClientSection(parent);
		_addEmbedSettingsSection(parent, projectAdapter);
		enableWidgets();
	}

	protected void setDefaults(ProjectAdapter project) {
		String customInfoPListContent = getBuildProperties().getCustomInfoPListContent(true);
		if (customInfoPListContent != null) {
			_customInfoPListText.setText(customInfoPListContent);
		}

		_customWebXMLText.setText("");
		_servletDeploymentCheck.setSelection(false);
		_generateWebXMLCheck.setSelection(false);
	}

	public boolean performOk() {
		// store the value in the owner text field
		try {
			ProjectAdapter projectAdapter = getProjectAdapter();
			if (projectAdapter != null) {
				BuildProperties buildProperties = getBuildProperties();
				buildProperties.setCustomInfoPListContent(_customInfoPListText.getText());
				buildProperties.setServletDeployment(_servletDeploymentCheck.getSelection());
				buildProperties.setWebXML(_generateWebXMLCheck.getSelection());
				buildProperties.setWebXML_CustomContent(_customWebXMLText.getText());

				ProjectFrameworkAdapter projectFrameworkAdapter = getProjectFrameworkAdapter();
				if (buildProperties.isServletDeployment()) {
					projectFrameworkAdapter.addFrameworkNamed("JavaWOJSPServlet");
				} else {
					projectFrameworkAdapter.removeFrameworkNamed("JavaWOJSPServlet");
				}

				for (Root root : _embedButtons.keySet()) {
					Button embedButton = _embedButtons.get(root);
					boolean embed = buildProperties.isServletDeployment() || (embedButton.isEnabled() && embedButton.getSelection());
					buildProperties.setEmbed(root, embed);
				}

				buildProperties.setJavaClient(_javaClientButton.getSelection());
				buildProperties.setJavaWebStart(_javaClientButton.getSelection() && _javaWebStartButton.getSelection());
				buildProperties.save();
			}
		} catch (Exception up) {
			UIPlugin.getDefault().log(up);
			return false;
		}
		return true;
	}
}