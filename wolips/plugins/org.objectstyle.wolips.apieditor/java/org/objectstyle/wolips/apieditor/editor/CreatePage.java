/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 - 2006 The ObjectStyle Group,
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
package org.objectstyle.wolips.apieditor.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.apieditor.ApieditorPlugin;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.bindings.api.ApiModel;
import org.objectstyle.wolips.bindings.api.ApiModelException;

public class CreatePage extends ApiFormPage {

	public static String PAGE_ID = "org.objectstyle.wolips.wodclipse.api.CreatePage";

	public CreatePage(ApiEditor apiEditor, String title) {
		super(apiEditor, PAGE_ID, title);
	}

	protected void createFormContent(final IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		form.getBody().setLayout(layout);
		SashForm sashForm = new SashForm(form.getBody(), SWT.NULL);
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(form.getBody().getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		Section section = toolkit.createSection(sashForm, Section.DESCRIPTION);
		section.setText("Create Api");
		section.marginWidth = 10;
		section.marginHeight = 5;
		toolkit.createCompositeSeparator(section);
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;

		final ApiEditor apiEditor = (ApiEditor) this.getEditor();
		boolean brokenApiFile = false;
		String brokenMessage = null;
		ApiModel apiModel = null;
		try {
			apiModel = apiEditor.getModel();
		} catch (Throwable throwable) {
			brokenApiFile = true;
			ApieditorPlugin.getDefault().debug(throwable);
			brokenMessage = StringUtils.getErrorMessage(throwable);
		}
		if (apiModel == null) {
			if (brokenApiFile) {
				Label brokenLabel = new Label(client, SWT.WRAP);
				brokenLabel.setBackground(client.getBackground());
				brokenLabel.setText(brokenMessage);
				GridData labelData = new GridData(GridData.FILL_HORIZONTAL);
				labelData.horizontalSpan = 2;
				brokenLabel.setLayoutData(labelData);
			}
			Button createApiFileButton;
			if (brokenApiFile) {
				createApiFileButton = toolkit.createButton(client, "Recreate Api File", SWT.PUSH);
			}
			else {
				createApiFileButton = toolkit.createButton(client, "Create Api File", SWT.PUSH);
			}
			gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			createApiFileButton.setLayoutData(gd);
			createApiFileButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					FileEditorInput fileEditorInput = (FileEditorInput) apiEditor.getEditorInput();
					try {
						IFile file = fileEditorInput.getFile();
						if (file.exists()) {
							file.delete(false, null);
						}
						new ApiModel(file);
					} catch (ApiModelException coreException) {
						throw new RuntimeException("Failed to create .api file.", coreException);
					} catch (CoreException coreException) {
						throw new RuntimeException("Failed to delete existing .api file.", coreException);
					}
					apiEditor.removePage(0);
					apiEditor.addPages();
					apiEditor.activateFirstPage();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// nothing to do
				}
			});
		}
		section.setClient(client);
		form.updateToolBar();
	}
}
