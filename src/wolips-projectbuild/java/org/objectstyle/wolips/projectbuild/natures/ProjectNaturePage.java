/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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

package org.objectstyle.wolips.projectbuild.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.objectstyle.wolips.core.plugin.logging.WOLipsLog;
import org.objectstyle.wolips.core.project.WOLipsProject;

public class ProjectNaturePage extends PropertyPage implements IAdaptable {

	private static final String PATH_TITLE = "Path:";

	private static final String WO_NATURE_TITLE = "Is a WebObjects Project";

	private static final String WO_IS_FRAMEWORK_TITLE = "Is a framework";

	private static final String WO_IS_INCREMENTAL_TITLE = "Use incremental builder";

	private static final int TEXT_FIELD_WIDTH = 50;
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
	private void _addFirstSection(
		Composite parent,
		WOLipsProject woLipsProject)
		throws CoreException {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_woNatureCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		_woNatureCheck.setText(WO_NATURE_TITLE);
		_woNatureCheck.setEnabled(true);

		_woNatureCheck.setSelection(
			woLipsProject.getNaturesAccessor().hasWOLipsNature());
	}

	/**
	 * @param parent
	 */
	private void _addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

  /**
   * @param parent
   * @param woLipsProject
   * @throws CoreException
   */
  private void _addSecondSection(
    Composite parent,
    WOLipsProject woLipsProject)
    throws CoreException {
    Composite group = _createDefaultComposite(parent);

    // project kind field (is framework?)
    _woIsFrameworkCheck = new Button(group, SWT.CHECK | SWT.LEFT);
    _woIsFrameworkCheck.setText(WO_IS_FRAMEWORK_TITLE);
    _woIsFrameworkCheck.setEnabled(true);

    _woIsFrameworkCheck.setSelection(
      woLipsProject.getNaturesAccessor().isFramework());
  }

	/**
	 * @param parent
	 * @param woLipsProject
	 * @throws CoreException
	 */
	private void _addThirdSection (
		Composite parent,
		WOLipsProject woLipsProject)
		throws CoreException {
		Composite group = _createDefaultComposite(parent);

		// project kind field (is framework?)
		_woIsIncrementalCheck = new Button(group, SWT.CHECK | SWT.LEFT);
		_woIsIncrementalCheck.setText(WO_IS_INCREMENTAL_TITLE);
		_woIsIncrementalCheck.setEnabled(true);

		_woIsIncrementalCheck.setSelection(
			woLipsProject.getNaturesAccessor().isIncremental());
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
			WOLipsProject woLipsProject = this.getWOLipsProject();
			_addFirstSection(composite, woLipsProject);
			_addSeparator(composite);
			_addSecondSection(composite, woLipsProject);
			_addSeparator(composite);
			_addThirdSection(composite, woLipsProject);
		} catch (CoreException exception) {
			WOLipsLog.log(exception);
		}
		return composite;
	}

	/**
	 * @param parent
	 * @return Composite
	 */
	private Composite _createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		// store the value in the owner text field
		WOLipsProject woLipsProject;
		try {
			woLipsProject = this.getWOLipsProject();
			if (_woNatureCheck.getSelection()) {
				if (_woIsIncrementalCheck.getSelection()) {
					woLipsProject.getNaturesAccessor().setIncrementalNature(_woIsFrameworkCheck.getSelection(), false);
				} else {
					woLipsProject.getNaturesAccessor().setAntNature(_woIsFrameworkCheck.getSelection(), false);
				}
			} else {
        woLipsProject.getNaturesAccessor().removeWOLipsNatures();
			}

		} catch (CoreException up) {
			WOLipsLog.log(up);

			return false;
		}
		finally {
			woLipsProject = null;
		}

		return true;
	}
	/**
	 * @return IJavaProject
	 * @throws CoreException
	 */

	public IJavaProject _getJavaProject() throws CoreException {
		IProject project =
			(IProject) (this.getElement().getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}
	/**
	 * @return IProject
	 * @throws CoreException
	 */
	public IProject _getProject() throws CoreException {
		IProject project =
			(IProject) (this.getElement().getAdapter(IProject.class));
		return (project);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class theClass) {
		return (Platform.getAdapterManager().getAdapter(this, theClass));
	}
	/**
	 * @return WOLipsProject
	 * @throws CoreException
	 */
	public WOLipsProject getWOLipsProject() throws CoreException {
		return new WOLipsProject(this._getProject());
	}
	private Button _woNatureCheck;
	private Button _woIsIncrementalCheck;
	private Button _woIsFrameworkCheck;
}