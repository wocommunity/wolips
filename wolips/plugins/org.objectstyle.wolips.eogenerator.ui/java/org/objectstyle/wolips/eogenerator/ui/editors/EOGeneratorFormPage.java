/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.eogenerator.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel.Define;

public class EOGeneratorFormPage extends FormPage {
	private EOGeneratorModel _model;

	private TableViewer _modelsTableViewer;

	private TableViewer _refModelsTableViewer;

	private TableViewer _definesTableViewer;

	private boolean _modelGroupEditor;

	private DirtyModelListener _dirtyModelListener;

	private DataBindingContext _bindingContext;

	public EOGeneratorFormPage(FormEditor editor, EOGeneratorModel model, boolean modelGroupEditor) {
		super(editor, "EOGeneratorForm", (modelGroupEditor) ? "EOModelGroup Form" : "EOGenerator Form");
		_model = model;
		_dirtyModelListener = new DirtyModelListener();
		_model.addPropertyChangeListener(EOGeneratorModel.DIRTY, _dirtyModelListener);
		_modelGroupEditor = modelGroupEditor;
	}

	@Override
	public void dispose() {
		_bindingContext.dispose();
		_model.removePropertyChangeListener(EOGeneratorModel.DIRTY, _dirtyModelListener);
		super.dispose();
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);
	}

	protected String getString(String name) {
		return name;
	}

	protected void createFormContent(IManagedForm managedForm) {
		_bindingContext = new DataBindingContext();

		ScrolledForm form = managedForm.getForm();
		form.setText("EOGenerator");

		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = form.getBody();

		GridLayout bodyLayout = new GridLayout();
		bodyLayout.numColumns = 1;
		bodyLayout.makeColumnsEqualWidth = true;
		bodyLayout.marginWidth = 10;
		bodyLayout.verticalSpacing = 20;
		bodyLayout.horizontalSpacing = 10;
		body.setLayout(bodyLayout);

		ModelsTableContentProvider modelsModel = new ModelsTableContentProvider();
		String modelsDescription;
		if (_modelGroupEditor) {
			modelsDescription = "For editing a model group, there should only be one entry in the model list.  If there are multiple models, they will all be loaded into the group, but the first entry will be the model that is opened by entity modeler.";
		} else {
			modelsDescription = "These models will have Java files generated for all of their entities.";
		}
		_modelsTableViewer = createModelsSection("Models", modelsDescription, toolkit, body, modelsModel, modelsModel, new ModelAddModelListener(), new ModelRemoveModelListener());

		RefModelsTableContentProvider refModelsModel = new RefModelsTableContentProvider();
		String refModelDescription;
		if (_modelGroupEditor) {
			refModelDescription = "Add all addition models that should be a model of this model group into the list below.";
		} else {
			refModelDescription = "These models are used to resolve type references from models listed in the first section.  No Java files will be generated for these models.";
		}
		_refModelsTableViewer = createModelsSection("Referenced Models", refModelDescription, toolkit, body, refModelsModel, refModelsModel, new RefModelAddModelListener(), new RefModelRemoveModelListener());

		if (!_modelGroupEditor) {
			createNamingSection(toolkit, body);

			createPathsSection(toolkit, body);

			createDefinesSection(toolkit, body);
		}

		updateViewsFromModel();

		form.pack();
		form.reflow(true);
	}

	private TableViewer createModelsSection(String title, String description, FormToolkit toolkit, Composite parent, IStructuredContentProvider contentProvider, ITableLabelProvider labelProvider, SelectionListener addListener, SelectionListener removeListener) {
		Composite modelsSection = createSection(toolkit, parent, title, description, 1, 2);

		if (contentProvider instanceof RefModelsTableContentProvider) {
			Button loadModelGroupButton = toolkit.createButton(modelsSection, "", SWT.CHECK);
			loadModelGroupButton.setText("Load Model Group");
			GridData loadModelGroupButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
			loadModelGroupButtonGridData.horizontalSpan = 2;
			loadModelGroupButtonGridData.verticalIndent = 5;
			loadModelGroupButton.setLayoutData(loadModelGroupButtonGridData);
			_bindingContext.bindValue(
					//SWTObservables.observeSelection(loadModelGroupButton), 
					WidgetProperties.buttonSelection().observe(loadModelGroupButton),
					//BeansObservables.observeValue(_model, EOGeneratorModel.LOAD_MODEL_GROUP),
					BeanProperties.value(EOGeneratorModel.LOAD_MODEL_GROUP).observe(_model),
					null, null);
		}

		Table modelsTable = toolkit.createTable(modelsSection, SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);

		GridData modelsTableGridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		modelsTableGridData.heightHint = 75;
		modelsTable.setLayoutData(modelsTableGridData);

		TableViewer modelsTableViewer = new TableViewer(modelsTable);
		modelsTableViewer.setContentProvider(contentProvider);
		modelsTableViewer.setLabelProvider(labelProvider);

		Composite modelsButtonsComposite = toolkit.createComposite(modelsSection);
		modelsButtonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridLayout modelsButtonsLayout = new GridLayout();
		modelsButtonsLayout.marginTop = 0;
		modelsButtonsLayout.marginBottom = 0;
		modelsButtonsLayout.verticalSpacing = 0;
		modelsButtonsLayout.horizontalSpacing = 0;
		modelsButtonsLayout.numColumns = 1;
		modelsButtonsComposite.setLayout(modelsButtonsLayout);

		Button modelsAddButton = toolkit.createButton(modelsButtonsComposite, "Add...", SWT.PUSH);
		GridData modelsAddButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		modelsAddButton.setLayoutData(modelsAddButtonGridData);
		modelsAddButton.addSelectionListener(addListener);

		Button modelsRemoveButton = toolkit.createButton(modelsButtonsComposite, "Remove", SWT.PUSH);
		GridData modelsRemoveButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		modelsRemoveButton.setLayoutData(modelsRemoveButtonGridData);
		modelsRemoveButton.addSelectionListener(removeListener);

		return modelsTableViewer;
	}

	protected EOGeneratorModel getModel() {
		return _model;
	}

	protected void createNamingSection(FormToolkit toolkit, Composite parent) {
		Composite namingSection = createSection(toolkit, parent, "File Names", "These settings control the names of the produced files.", 1, 2);
		GridLayout namingSectionLayout = (GridLayout) namingSection.getLayout();
		namingSectionLayout.horizontalSpacing = 10;

		Label filenameTemplateLabel = toolkit.createLabel(namingSection, "Filename Template:");
		filenameTemplateLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		Text filenameTemplate = toolkit.createText(namingSection, null);
		filenameTemplate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(filenameTemplate, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(filenameTemplate),
				//BeansObservables.observeValue(_model, EOGeneratorModel.FILENAME_TEMPLATE),
				BeanProperties.value(EOGeneratorModel.FILENAME_TEMPLATE).observe(_model),
				null, null);

		Label prefixLabel = toolkit.createLabel(namingSection, "Prefix:");
		prefixLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		Text prefixEntry = toolkit.createText(namingSection, null);
		prefixEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(prefixEntry, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(prefixEntry),
				//BeansObservables.observeValue(_model, EOGeneratorModel.PREFIX),
				BeanProperties.value(EOGeneratorModel.PREFIX).observe(_model),
				null, null);

		Label extensionLabel = toolkit.createLabel(namingSection, "Extension:");
		extensionLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		Text extensionEntry = toolkit.createText(namingSection, null);
		extensionEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(extensionEntry, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(extensionEntry),
				//BeansObservables.observeValue(_model, EOGeneratorModel.EXTENSION),
				BeanProperties.value(EOGeneratorModel.EXTENSION).observe(_model),
				null, null);
	}

	protected void createPathsSection(FormToolkit toolkit, Composite parent) {
		FileEditorInput editorInput = (FileEditorInput) getEditorInput();
		IFile eogenFile = editorInput.getFile();
		final IProject project = eogenFile.getProject();

		Composite pathsSection = createSection(toolkit, parent, "Destination Paths", "These paths specify where generated files will be written and are project-relative.", 1, 3);
		GridLayout pathsSectionLayout = (GridLayout) pathsSection.getLayout();
		pathsSectionLayout.horizontalSpacing = 10;

		Label superclassPackageLabel = toolkit.createLabel(pathsSection, "Superclass Package (e.g. \"base\"):");
		superclassPackageLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text superclassPackage = toolkit.createText(pathsSection, null);
		superclassPackage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(superclassPackage, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(superclassPackage),
				//BeansObservables.observeValue(_model, EOGeneratorModel.SUPERCLASS_PACKAGE),
				BeanProperties.value(EOGeneratorModel.SUPERCLASS_PACKAGE).observe(_model),
				null, null);
		toolkit.createLabel(pathsSection, "");

		Label destinationLabel = toolkit.createLabel(pathsSection, "Destination:");
		destinationLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text destination = toolkit.createText(pathsSection, null);
		destination.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(destination, SWT.Modify), 
				WidgetProperties.text(SWT.Modify).observe(destination),
				//BeansObservables.observeValue(_model, EOGeneratorModel.DESTINATION),
				BeanProperties.value(EOGeneratorModel.DESTINATION).observe(_model),
				null, null);

		Button destinationBrowse = toolkit.createButton(pathsSection, "Browse...", SWT.PUSH);
		destinationBrowse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder to write autogenerated files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					EOGeneratorFormPage.this.getModel().setDestination(projectRelativePath.toPortableString());
				}
				destination.forceFocus();
			}
		});

		Label subclassDestinationLabel = toolkit.createLabel(pathsSection, "Subclass Destination:");
		subclassDestinationLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text subclassDestination = toolkit.createText(pathsSection, null);
		subclassDestination.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(subclassDestination, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(subclassDestination),
				//BeansObservables.observeValue(_model, EOGeneratorModel.SUBCLASS_DESTINATION),
				BeanProperties.value(EOGeneratorModel.SUBCLASS_DESTINATION).observe(_model),
				null, null);

		Button subclassDestinationBrowse = toolkit.createButton(pathsSection, "Browse...", SWT.PUSH);
		subclassDestinationBrowse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder to generate customizable files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					EOGeneratorFormPage.this.getModel().setSubclassDestination(projectRelativePath.toPortableString());
				}
				subclassDestination.forceFocus();
			}
		});

		Composite templatesSection = createSection(toolkit, parent, "Templates", "These paths specify the templates that will be used to generate files.  If left blank, the defaults from the EOGenerator preference page will be used.", 1, 3);
		GridLayout templatesSectionLayout = (GridLayout) templatesSection.getLayout();
		templatesSectionLayout.horizontalSpacing = 10;

		Label templatesFolderLabel = toolkit.createLabel(templatesSection, "Templates Folder:");
		templatesFolderLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text templatesFolder = toolkit.createText(templatesSection, null);
		templatesFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(templatesFolder, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(templatesFolder),
				//BeansObservables.observeValue(_model, EOGeneratorModel.TEMPLATE_DIR),
				BeanProperties.value(EOGeneratorModel.TEMPLATE_DIR).observe(_model),
				null, null);

		Button templatesFolderBrowse = toolkit.createButton(templatesSection, "Browse...", SWT.PUSH);
		templatesFolderBrowse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getEditorSite().getShell());
				directoryDialog.setMessage("Select the folder that contains your EOGenerator templates.");
				directoryDialog.setFilterPath(EOGeneratorFormPage.this.getModel().getTemplateDir());
				String selectedDirectory = directoryDialog.open();
				if (selectedDirectory != null) {
					EOGeneratorFormPage.this.getModel().setTemplateDir(selectedDirectory);
				}
				templatesFolder.forceFocus();
			}
		});

		Label templateLabel = toolkit.createLabel(templatesSection, "Template:");
		templateLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text template = toolkit.createText(templatesSection, null);
		template.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(template, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(template),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA_TEMPLATE),
				BeanProperties.value(EOGeneratorModel.JAVA_TEMPLATE).observe(_model),
				null, null);

		Button templateBrowse = toolkit.createButton(templatesSection, "Browse...", SWT.PUSH);
		templateBrowse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				String selectedTemplate = EOGeneratorFormPage.this.selectTemplate("Select the superclass template.");
				if (selectedTemplate != null) {
					EOGeneratorFormPage.this.getModel().setJavaTemplate(selectedTemplate);
				}
				template.forceFocus();
			}
		});

		Label subclassTemplateLabel = toolkit.createLabel(templatesSection, "Subclass Template:");
		subclassTemplateLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text subclassTemplate = toolkit.createText(templatesSection, null);
		subclassTemplate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(subclassTemplate, SWT.Modify), 
				WidgetProperties.text(SWT.Modify).observe(subclassTemplate),
				//BeansObservables.observeValue(_model, EOGeneratorModel.SUBCLASS_JAVA_TEMPLATE), 
				BeanProperties.value(EOGeneratorModel.SUBCLASS_JAVA_TEMPLATE).observe(_model),
				null, null);

		Button subclassTemplateBrowse = toolkit.createButton(templatesSection, "Browse...", SWT.PUSH);
		subclassTemplateBrowse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				String selectedTemplate = EOGeneratorFormPage.this.selectTemplate("Select the subclass template.");
				if (selectedTemplate != null) {
					EOGeneratorFormPage.this.getModel().setSubclassJavaTemplate(selectedTemplate);
				}
				subclassTemplate.forceFocus();
			}
		});
		
		String furtherTemplatesDescription = "It's possible to generate Java-Files from up to three additional Templates in the 'Templates-Folder'.\n"
			+ "The name of each Java-File will be based on the generated class-name according to the Template.\n"
			+ "The generated Java-Files for 'Desination2', 'Destination3' and 'Destination4' will always be overwritten. (Same behaviour as with 'Desination')";
		
		Composite furtherTemplatesSection = createSection(toolkit, parent, "Generate additional Java-Files", furtherTemplatesDescription, 1, 3);
		GridLayout furtherTemplatesLayout = (GridLayout) furtherTemplatesSection.getLayout();
		furtherTemplatesLayout.horizontalSpacing = 10;
		
		Label destination2Label = toolkit.createLabel(furtherTemplatesSection, "Destination2:");
		destination2Label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text destination2 = toolkit.createText(furtherTemplatesSection, null);
		destination2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(destination2, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(destination2),
				//BeansObservables.observeValue(_model, EOGeneratorModel.DESTINATION2),
				BeanProperties.value(EOGeneratorModel.DESTINATION2).observe(_model),
				null, null);

		
		Button destination2Browse = toolkit.createButton(furtherTemplatesSection, "Browse...", SWT.PUSH);
		destination2Browse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder2 to write autogenerated files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					EOGeneratorFormPage.this.getModel().setDestination2(projectRelativePath.toPortableString());
				}
				destination2.forceFocus();
			}
		});
		
		Label template2Label = toolkit.createLabel(furtherTemplatesSection, "Template2:");
		template2Label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text template2 = toolkit.createText(furtherTemplatesSection, null);
		template2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(template2, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(template2),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA_TEMPLATE2),
				BeanProperties.value(EOGeneratorModel.JAVA_TEMPLATE2).observe(_model),
				null, null);

		Button templateBrowse2 = toolkit.createButton(furtherTemplatesSection, "Browse...", SWT.PUSH);
		templateBrowse2.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				String selectedTemplate = EOGeneratorFormPage.this.selectTemplate("Select the template2.");
				if (selectedTemplate != null) {
					EOGeneratorFormPage.this.getModel().setJavaTemplate2(selectedTemplate);
				}
				template2.forceFocus();
			}
		});

		Label destination3Label = toolkit.createLabel(furtherTemplatesSection, "Destination3:");
		destination3Label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text destination3 = toolkit.createText(furtherTemplatesSection, null);
		destination3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(destination3, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(destination3),
				//BeansObservables.observeValue(_model, EOGeneratorModel.DESTINATION3),
				BeanProperties.value(EOGeneratorModel.DESTINATION3).observe(_model),
				null, null);

		
		Button destination3Browse = toolkit.createButton(furtherTemplatesSection, "Browse...", SWT.PUSH);
		destination3Browse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder3 to write autogenerated files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					EOGeneratorFormPage.this.getModel().setDestination3(projectRelativePath.toPortableString());
				}
				destination3.forceFocus();
			}
		});
		
		Label template3Label = toolkit.createLabel(furtherTemplatesSection, "Template3:");
		template3Label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text template3 = toolkit.createText(furtherTemplatesSection, null);
		template3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(template3, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(template3),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA_TEMPLATE3), 
				BeanProperties.value(EOGeneratorModel.JAVA_TEMPLATE3).observe(_model),
				null, null);

		Button templateBrowse3 = toolkit.createButton(furtherTemplatesSection, "Browse...", SWT.PUSH);
		templateBrowse3.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				String selectedTemplate = EOGeneratorFormPage.this.selectTemplate("Select the template3.");
				if (selectedTemplate != null) {
					EOGeneratorFormPage.this.getModel().setJavaTemplate3(selectedTemplate);
				}
				template3.forceFocus();
			}
		});
		
		Label destination4Label = toolkit.createLabel(furtherTemplatesSection, "Destination4:");
		destination4Label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text destination4 = toolkit.createText(furtherTemplatesSection, null);
		destination4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(destination4, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(destination4),
				//BeansObservables.observeValue(_model, EOGeneratorModel.DESTINATION4), 
				BeanProperties.value(EOGeneratorModel.DESTINATION4).observe(_model),
				null, null);

		
		Button destination4Browse = toolkit.createButton(furtherTemplatesSection, "Browse...", SWT.PUSH);
		destination4Browse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder4 to write autogenerated files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					EOGeneratorFormPage.this.getModel().setDestination4(projectRelativePath.toPortableString());
				}
				destination4.forceFocus();
			}
		});
		
		Label template4Label = toolkit.createLabel(furtherTemplatesSection, "Template4:");
		template4Label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		final Text template4 = toolkit.createText(furtherTemplatesSection, null);
		template4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_bindingContext.bindValue(
				//SWTObservables.observeText(template4, SWT.Modify),
				WidgetProperties.text(SWT.Modify).observe(template4),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA_TEMPLATE4),
				BeanProperties.value(EOGeneratorModel.JAVA_TEMPLATE4).observe(_model),
				null, null);

		Button templateBrowse4 = toolkit.createButton(furtherTemplatesSection, "Browse...", SWT.PUSH);
		templateBrowse4.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
				widgetSelected(selectionevent);
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				String selectedTemplate = EOGeneratorFormPage.this.selectTemplate("Select the template4.");
				if (selectedTemplate != null) {
					EOGeneratorFormPage.this.getModel().setJavaTemplate4(selectedTemplate);
				}
				template4.forceFocus();
			}
		});

		Composite outputOptionsSection = createSection(toolkit, parent, "Options", "These flags control various output options for generated source.", 1, 3);
		GridLayout outputOptionsLayout = (GridLayout) outputOptionsSection.getLayout();
		outputOptionsLayout.horizontalSpacing = 10;
		outputOptionsLayout.verticalSpacing = 5;

		// Label packageDirsLabel = toolkit.createLabel(pathsSection, "Create
		// Packages?");
		// packageDirsLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		Button packageDirsButton = toolkit.createButton(outputOptionsSection, "", SWT.CHECK);
		packageDirsButton.setText("Create Packages");
		GridData packageDirsButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		packageDirsButtonGridData.horizontalSpan = 3;
		packageDirsButton.setLayoutData(packageDirsButtonGridData);
		_bindingContext.bindValue(
				//SWTObservables.observeSelection(packageDirsButton), 
				WidgetProperties.buttonSelection().observe(packageDirsButton),
				//BeansObservables.observeValue(_model, EOGeneratorModel.PACKAGE_DIRS), 
				BeanProperties.value(EOGeneratorModel.PACKAGE_DIRS).observe(_model),
				null, null);

		Button javaButton = toolkit.createButton(outputOptionsSection, "", SWT.CHECK);
		GridData javaButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		javaButtonGridData.horizontalSpan = 3;
		javaButton.setLayoutData(javaButtonGridData);
		javaButton.setText("Java");
		_bindingContext.bindValue(
				//SWTObservables.observeSelection(javaButton),
				WidgetProperties.buttonSelection().observe(javaButton),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA), 
				BeanProperties.value(EOGeneratorModel.JAVA).observe(_model),
				null, null);

		Button javaClientButton = toolkit.createButton(outputOptionsSection, "", SWT.CHECK);
		javaClientButton.setText("Java Client");
		GridData javaClientButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		javaClientButtonGridData.horizontalSpan = 3;
		javaClientButton.setLayoutData(javaClientButtonGridData);
		_bindingContext.bindValue(
				//SWTObservables.observeSelection(javaClientButton),
				WidgetProperties.buttonSelection().observe(javaClientButton),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA_CLIENT),
				BeanProperties.value(EOGeneratorModel.JAVA_CLIENT).observe(_model),
				null, null);

		Button javaClientCommonButton = toolkit.createButton(outputOptionsSection, "", SWT.CHECK);
		javaClientCommonButton.setText("Java Client Common");
		GridData javaClientCommonButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		javaClientCommonButtonGridData.horizontalSpan = 3;
		javaClientCommonButton.setLayoutData(javaClientCommonButtonGridData);
		_bindingContext.bindValue(
				//SWTObservables.observeSelection(javaClientCommonButton),
				WidgetProperties.buttonSelection().observe(javaClientCommonButton),
				//BeansObservables.observeValue(_model, EOGeneratorModel.JAVA_CLIENT_COMMON),
				BeanProperties.value(EOGeneratorModel.JAVA_CLIENT_COMMON).observe(_model),
				null, null);
	}

	protected String selectTemplate(String text) {
		String selectedTemplate = null;
		FileDialog templateDialog = new FileDialog(getEditorSite().getShell());
		templateDialog.setFileName(_model.getTemplateDir());
		templateDialog.setText(text);
		templateDialog.setFilterExtensions(new String[] { "*.eotemplate" });
		String templateDir = _model.getTemplateDir();
		if (templateDir != null) {
			templateDialog.setFilterPath(templateDir);
		}
		selectedTemplate = templateDialog.open();
		if (selectedTemplate != null) {
			if (templateDir != null && selectedTemplate.startsWith(templateDir)) {
				int templateDirLength = templateDir.length();
				if (!templateDir.endsWith(File.separator)) {
					templateDirLength += File.separator.length();
				}
				selectedTemplate = selectedTemplate.substring(templateDirLength);
			}
		}
		return selectedTemplate;
	}

	protected void createDefinesSection(FormToolkit toolkit, Composite parent) {
		Composite definesSection = createSection(toolkit, parent, "Defines", "These variables will turn into EOGenerator -define-Xxx parameters that will be accessible in your templates (i.e. EOGenericRecord, etc)", 1, 2);
		Table definesTable = toolkit.createTable(definesSection, SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);

		definesTable.setLinesVisible(true);
		definesTable.setHeaderVisible(true);

		TableColumn defineNameTableColumn = new TableColumn(definesTable, SWT.NONE);
		defineNameTableColumn.setWidth(175);
		defineNameTableColumn.setText("Name");

		TableColumn defineValueTableColumn = new TableColumn(definesTable, SWT.NONE);
		defineValueTableColumn.setWidth(400);
		defineValueTableColumn.setText("Value");

		_definesTableViewer = new TableViewer(definesTable);
		DefinesTableContentProvider definesModel = new DefinesTableContentProvider();
		_definesTableViewer.setContentProvider(definesModel);
		_definesTableViewer.setLabelProvider(definesModel);
		_definesTableViewer.addDoubleClickListener(new DefinesDoubleClickListener());

		GridData definesTableGridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		definesTableGridData.heightHint = 125;
		definesTable.setLayoutData(definesTableGridData);
		Composite definesButtonsComposite = toolkit.createComposite(definesSection);
		definesButtonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridLayout definesButtonsLayout = new GridLayout();
		definesButtonsLayout.marginTop = 0;
		definesButtonsLayout.marginBottom = 0;
		definesButtonsLayout.verticalSpacing = 0;
		definesButtonsLayout.horizontalSpacing = 0;
		definesButtonsLayout.numColumns = 1;
		definesButtonsComposite.setLayout(definesButtonsLayout);

		Button definesAddButton = toolkit.createButton(definesButtonsComposite, "Add...", SWT.PUSH);
		GridData definesAddButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		definesAddButton.setLayoutData(definesAddButtonGridData);
		definesAddButton.addSelectionListener(new DefineAddModelListener());

		Button definesRemoveButton = toolkit.createButton(definesButtonsComposite, "Remove", SWT.PUSH);
		GridData definesRemoveButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		definesRemoveButton.setLayoutData(definesRemoveButtonGridData);
		definesRemoveButton.addSelectionListener(new DefinesRemoveModelListener());
	}

	protected void updateViewsFromModel() {
		_modelsTableViewer.setInput(_model);
		_refModelsTableViewer.setInput(_model);
		if (!_modelGroupEditor) {
			_definesTableViewer.setInput(_model);
			// _destinationEntry.setValue(_model.getDestination());
			// _subclassDestinationEntry.setValue(_model.getSubclassDestination());
			// _subclassTemplateEntry.setValue(_model.getSubclassJavaTemplate(null));
			// _templateEntry.setValue(_model.getJavaTemplate(null));
			// _templatesFolderEntry.setValue(_model.getTemplateDir(null));
		}
	}

	protected Composite createSection(FormToolkit toolkit, Composite parent, String title, String description, int spanColumns, int sectionColumns) {
		int style = (description == null) ? ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED : ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | Section.DESCRIPTION;
		Section section = toolkit.createSection(parent, style);
		GridLayout sectionLayout = new GridLayout();
		section.setLayout(sectionLayout);
		section.setText(title);
		if (description != null) {
			section.setDescription(description);
		}
		GridData sectionGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		sectionGridData.horizontalSpan = spanColumns;
		section.setLayoutData(sectionGridData);

		Composite sectionClient = toolkit.createComposite(section, SWT.NONE);
		GridData sectionClientGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		sectionClient.setLayoutData(sectionClientGridData);
		GridLayout sectionClientLayout = new GridLayout();
		sectionClientLayout.marginHeight = 0;
		sectionClientLayout.marginWidth = 0;
		sectionClientLayout.verticalSpacing = 0;
		sectionClientLayout.horizontalSpacing = 0;
		sectionClientLayout.numColumns = sectionColumns;
		sectionClient.setLayout(sectionClientLayout);
		section.setClient(sectionClient);

		return sectionClient;
	}

	protected void addDefine(String name, String value) {
		InputDialog nameDialog = new InputDialog(getEditorSite().getShell(), "Enter Name", "Enter the name of this variable.", name, null);
		int nameRetval = nameDialog.open();
		if (nameRetval == Window.OK) {
			String dialogName = nameDialog.getValue();
			if (dialogName != null && dialogName.trim().length() > 0) {
				InputDialog valueDialog = new InputDialog(getEditorSite().getShell(), "Enter Value", "Enter the value of this variable.", value, null);
				int valueRetval = valueDialog.open();
				if (valueRetval == Window.OK) {
					String dialogValue = valueDialog.getValue();
					if (dialogValue != null && dialogValue.trim().length() > 0) {
						EOGeneratorModel.Define define = new EOGeneratorModel.Define(dialogName, dialogValue);
						List<Define> defines = _model.getDefines();
						List<Define> newDefines = new LinkedList<Define>(defines);
						if (name != null && name.trim().length() > 0) {
							EOGeneratorModel.Define oldDefine = new EOGeneratorModel.Define(name, value);
							newDefines.remove(oldDefine);
						}
						newDefines.remove(define);
						newDefines.add(define);
						_model.setDefines(newDefines);
						_definesTableViewer.refresh();
					}
				}
			}
		}
	}

	public TableViewer getModelsTableViewer() {
		return _modelsTableViewer;
	}

	public TableViewer getRefModelsTableViewer() {
		return _refModelsTableViewer;
	}

	public TableViewer getDefinesTableViewer() {
		return _definesTableViewer;
	}

	protected class DirtyModelListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (EOGeneratorModel.DIRTY.equals(event.getPropertyName())) {
				EOGeneratorFormPage.this.getEditor().editorDirtyStateChanged();
			}
		}
	}

	protected class ModelRemoveModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			IStructuredSelection selection = (IStructuredSelection) EOGeneratorFormPage.this.getModelsTableViewer().getSelection();
			if (!selection.isEmpty()) {
				List<EOModelReference> models = EOGeneratorFormPage.this.getModel().getModels();
				List<EOModelReference> newModels = new LinkedList<EOModelReference>(models);
				Object[] selections = selection.toArray();
				for (int i = 0; i < selections.length; i++) {
					newModels.remove(selections[i]);
				}
				EOGeneratorFormPage.this.getModel().setModels(newModels);
				EOGeneratorFormPage.this.getModelsTableViewer().refresh();
			}
		}
	}

	protected class ModelAddModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			FileEditorInput editorInput = (FileEditorInput) getEditorInput();
			IFile eogenFile = editorInput.getFile();
			IProject project = eogenFile.getProject();
			ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the EOModel to add.");
			containerDialog.open();
			Object[] selectedContainers = containerDialog.getResult();
			if (selectedContainers != null && selectedContainers.length > 0) {
				IPath selectedPath = (IPath) selectedContainers[0];
				IFolder selectedFolder = project.getParent().getFolder(selectedPath);
				IPath modelPath = selectedFolder.getLocation();
				EOModelReference eoModel = new EOModelReference(modelPath);
				addModel(eoModel);
			}
		}

		protected void addModel(EOModelReference eoModel) {
			List<EOModelReference> models = EOGeneratorFormPage.this.getModel().getModels();
			if (!models.contains(eoModel)) {
				List<EOModelReference> newModels = new LinkedList<EOModelReference>(models);
				newModels.add(eoModel);
				EOGeneratorFormPage.this.getModel().setModels(newModels);
				EOGeneratorFormPage.this.getModelsTableViewer().refresh();
			}
		}
	}

	protected class RefModelRemoveModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			IStructuredSelection selection = (IStructuredSelection) EOGeneratorFormPage.this.getRefModelsTableViewer().getSelection();
			if (!selection.isEmpty()) {
				List<EOModelReference> refModels = EOGeneratorFormPage.this.getModel().getRefModels();
				List<EOModelReference> newRefModels = new LinkedList<EOModelReference>(refModels);
				Object[] selections = selection.toArray();
				for (int i = 0; i < selections.length; i++) {
					newRefModels.remove(selections[i]);
				}
				EOGeneratorFormPage.this.getModel().setRefModels(newRefModels);
				EOGeneratorFormPage.this.getRefModelsTableViewer().refresh();
			}
		}
	}

	protected class RefModelAddModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			// FileEditorInput editorInput = (FileEditorInput) getEditorInput();
			// IFile eogenFile = editorInput.getFile();
			DirectoryDialog directoryDialog = new DirectoryDialog(getEditorSite().getShell());
			directoryDialog.setMessage("Select the Reference EOModel to add.");
			String selectedDirectory = directoryDialog.open();
			if (selectedDirectory != null) {
				EOModelReference eoModel = new EOModelReference(new Path(selectedDirectory));
				addModel(eoModel);
			}
		}

		protected void addModel(EOModelReference eoModel) {
			List<EOModelReference> refModels = EOGeneratorFormPage.this.getModel().getRefModels();
			if (!refModels.contains(eoModel)) {
				List<EOModelReference> newRefModels = new LinkedList<EOModelReference>(refModels);
				newRefModels.add(eoModel);
				EOGeneratorFormPage.this.getModel().setRefModels(newRefModels);
				EOGeneratorFormPage.this.getRefModelsTableViewer().refresh();
			}
		}
	}

	protected class DefinesRemoveModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			IStructuredSelection selection = (IStructuredSelection) EOGeneratorFormPage.this.getDefinesTableViewer().getSelection();
			if (!selection.isEmpty()) {
				List<Define> defines = EOGeneratorFormPage.this.getModel().getDefines();
				List<Define> newDefines = new LinkedList<Define>(defines);
				Object[] selections = selection.toArray();
				for (int i = 0; i < selections.length; i++) {
					newDefines.remove(selections[i]);
				}
				EOGeneratorFormPage.this.getModel().setDefines(newDefines);
				EOGeneratorFormPage.this.getDefinesTableViewer().refresh();
			}
		}
	}

	protected class DefineAddModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			EOGeneratorFormPage.this.addDefine("", "");
		}
	}

	protected class DefinesDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent event) {
			IStructuredSelection selection = (IStructuredSelection) EOGeneratorFormPage.this.getDefinesTableViewer().getSelection();
			if (!selection.isEmpty()) {
				EOGeneratorModel.Define define = (EOGeneratorModel.Define) selection.getFirstElement();
				if (define != null) {
					EOGeneratorFormPage.this.addDefine(define.getName(), define.getValue());
				}
			}
		}
	}

	protected class DefinesTableContentProvider implements IStructuredContentProvider, ITableLabelProvider {
		public Object[] getElements(Object inputElement) {
			Object[] models = EOGeneratorFormPage.this.getModel().getDefines().toArray();
			return models;
		}

		public void dispose() {
			// do nothing
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			EOGeneratorModel.Define define = (EOGeneratorModel.Define) element;
			String text;
			if (columnIndex == 0) {
				text = define.getName();
			} else if (columnIndex == 1) {
				text = define.getValue();
			} else {
				text = "";
			}
			return text;
		}

		public void addListener(ILabelProviderListener listener) {
			// do nothing
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// do nothing
		}
	}

	protected abstract class AbstractModelsTableContentProvider implements IStructuredContentProvider, ITableLabelProvider {
		public void dispose() {
			// do nothing
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			EOModelReference model = (EOModelReference) element;
			String name = model.getPath(EOGeneratorFormPage.this.getModel().getProjectPath());
			return name;
		}

		public void addListener(ILabelProviderListener listener) {
			// do nothing
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// do nothing
		}
	}

	protected class ModelsTableContentProvider extends AbstractModelsTableContentProvider {
		public Object[] getElements(Object inputElement) {
			Object[] models = EOGeneratorFormPage.this.getModel().getModels().toArray();
			return models;
		}
	}

	protected class RefModelsTableContentProvider extends AbstractModelsTableContentProvider {
		public Object[] getElements(Object inputElement) {
			Object[] models = EOGeneratorFormPage.this.getModel().getRefModels().toArray();
			return models;
		}
	}
}
