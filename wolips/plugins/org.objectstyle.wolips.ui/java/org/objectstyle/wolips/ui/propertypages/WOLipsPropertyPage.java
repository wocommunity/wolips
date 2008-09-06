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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.jdt.ProjectFrameworkAdapter;
import org.objectstyle.wolips.variables.BuildProperties;

/**
 * @author mschrag
 */
public abstract class WOLipsPropertyPage extends PropertyPage implements IAdaptable {
	private BuildProperties _buildProperties;

	protected BuildProperties getBuildProperties() {
		if (_buildProperties == null) {
			ProjectAdapter projectAdapter = getProjectAdapter();
			if (projectAdapter != null) {
				_buildProperties = projectAdapter.getBuildProperties();
			}
		}
		return _buildProperties;
	}

	protected Text _addTextField(Composite parent, String label) {
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

	protected Text _addTextArea(Composite parent, String label) {
		if (label != null) {
			Label textLabel = new Label(parent, SWT.NONE);
			textLabel.setText(label);
			GridData labelData = new GridData();
			labelData.horizontalSpan = 2;
			labelData.verticalAlignment = SWT.BEGINNING;
			labelData.verticalIndent = 7;
			textLabel.setLayoutData(labelData);
		}

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

	protected static String _getArg(Map values, String key, String defVal) {
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

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = 0;
		layout.marginRight = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		IProject project = getProject();
		if (project != null) {
			ProjectAdapter projectAdapter = getProjectAdapter();
			boolean isWOProject = (projectAdapter != null);
			if (!isWOProject) {
				projectAdapter = new ProjectAdapter(getProject(), false);
			}

			_createContents(composite, projectAdapter, isWOProject);
		}

		return composite;
	}

	protected abstract void _createContents(Composite parent, ProjectAdapter projectAdapter, boolean isWOProject);

	protected Composite _createGroupWithLabel(Composite parent, String label) {
		Group composite = new Group(parent, SWT.NULL);
		FormLayout layout = new FormLayout();
		// layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(data);
		if (null != label) {
			composite.setText(label);
		}
		return composite;
	}

	protected void performDefaults() {
		ProjectAdapter projectAdapter = getProjectAdapter();
		if (projectAdapter != null) {
			setDefaults(projectAdapter);
		}
	}

	protected abstract void setDefaults(ProjectAdapter project);

	public IJavaProject getJavaProject() {
		return JavaCore.create(getProject());
	}

	public IProject getProject() {
		IProject project;
		IAdaptable element = getElement();
		if (element instanceof IResource) {
			project = ((IResource) element).getProject();
		} else {
			project = (IProject) getElement().getAdapter(IProject.class);
		}
		return project;
	}

	public ProjectAdapter getProjectAdapter() {
		IProject project = getProject();
		ProjectAdapter projectAdapter = null;
		if (project != null) {
			projectAdapter = (ProjectAdapter) project.getAdapter(IProjectAdapter.class);
		}
		return projectAdapter;
	}

	public ProjectFrameworkAdapter getProjectFrameworkAdapter() {
		IProject project = getProject();
		ProjectFrameworkAdapter projectFrameworkAdapter = null;
		if (project != null) {
			projectFrameworkAdapter = (ProjectFrameworkAdapter) project.getAdapter(ProjectFrameworkAdapter.class);
		}
		return projectFrameworkAdapter;
	}

	public Object getAdapter(Class theClass) {
		return Platform.getAdapterManager().getAdapter(this, theClass);
	}
}