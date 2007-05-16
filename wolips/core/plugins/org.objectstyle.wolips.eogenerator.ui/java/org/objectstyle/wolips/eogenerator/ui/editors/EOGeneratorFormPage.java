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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.pde.internal.ui.parts.FormEntry;
import org.eclipse.pde.internal.ui.parts.IFormEntryListener;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel.Define;

public class EOGeneratorFormPage extends FormPage {
	EOGeneratorModel _model;

	private FormEntry _destinationEntry;

	private FormEntry _subclassDestinationEntry;

	private FormEntry _templatesFolderEntry;

	private FormEntry _templateEntry;

	private FormEntry _subclassTemplateEntry;

	private FormEntry _prefixEntry;

	private FormEntry _filenameTemplateEntry;

	// private Button myVerboseButton;
	Button _javaButton;

	Button _javaClientButton;

	Button _packageDirsButton;

	TableViewer _modelsTableViewer;

	TableViewer _refModelsTableViewer;

	TableViewer _definesTableViewer;

	private boolean _modelGroupEditor;

	public EOGeneratorFormPage(FormEditor editor, EOGeneratorModel model, boolean modelGroupEditor) {
		super(editor, "EOGeneratorForm", (modelGroupEditor) ? "EOModelGroup Form" : "EOGenerator Form");
		_model = model;
		_modelGroupEditor = modelGroupEditor;
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);
	}

	protected String getString(String name) {
		return name;
	}

	protected void createFormContent(IManagedForm managedForm) {
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

		form.reflow(true);
	}

	private TableViewer createModelsSection(String title, String description, FormToolkit toolkit, Composite parent, IStructuredContentProvider contentProvider, ITableLabelProvider labelProvider, SelectionListener addListener, SelectionListener removeListener) {
		Composite modelsSection = createSection(toolkit, parent, title, description, 1, 2);
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

		_filenameTemplateEntry = new FormEntry(namingSection, toolkit, "Filename Template", SWT.NONE);
		_filenameTemplateEntry.setValue(_model.getFilenameTemplate());
		_filenameTemplateEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setFilenameTemplate(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setFilenameTemplate(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}
		});

		_prefixEntry = new FormEntry(namingSection, toolkit, "Prefix", SWT.NONE);
		_prefixEntry.setValue(_model.getPrefix());
		_prefixEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setPrefix(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setPrefix(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}
		});
	}

	protected void createPathsSection(FormToolkit toolkit, Composite parent) {
		FileEditorInput editorInput = (FileEditorInput) getEditorInput();
		IFile eogenFile = editorInput.getFile();
		final IProject project = eogenFile.getProject();

		Composite pathsSection = createSection(toolkit, parent, "Destination Paths", "These paths specify where generated files will be written and are project-relative.", 1, 3);
		GridLayout pathsSectionLayout = (GridLayout) pathsSection.getLayout();
		pathsSectionLayout.horizontalSpacing = 10;

		Label packageDirsLabel = toolkit.createLabel(pathsSection, "Create Packages?");
		packageDirsLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		_packageDirsButton = toolkit.createButton(pathsSection, "", SWT.CHECK);
		GridData packageDirsButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		packageDirsButtonGridData.horizontalSpan = 2;
		_packageDirsButton.setLayoutData(packageDirsButtonGridData);
		_packageDirsButton.setSelection(_model.isPackageDirs() != null && _model.isPackageDirs().booleanValue());
		_packageDirsButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent _e) {
				widgetSelected(_e);
			}

			public void widgetSelected(SelectionEvent _e) {
				EOGeneratorFormPage.this.getModel().setPackageDirs(Boolean.valueOf(_packageDirsButton.getSelection()));
				getEditor().editorDirtyStateChanged();
			}
		});

		_destinationEntry = new FormEntry(pathsSection, toolkit, "Destination", "Browse...", false);
		_destinationEntry.setValue(_model.getDestination());
		_destinationEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setDestination(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setDestination(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}

			public void browseButtonSelected(FormEntry entry) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder to write autogenerated files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					entry.setValue(projectRelativePath.toPortableString());
				}
				entry.getText().forceFocus();
			}
		});

		_subclassDestinationEntry = new FormEntry(pathsSection, toolkit, "Subclass Destination", "Browse...", false);
		_subclassDestinationEntry.setValue(_model.getSubclassDestination());
		_subclassDestinationEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setSubclassDestination(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setSubclassDestination(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}

			public void browseButtonSelected(FormEntry entry) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder to generate customizable files into.");
				containerDialog.open();
				Object[] selectedContainers = containerDialog.getResult();
				if (selectedContainers != null && selectedContainers.length > 0) {
					IPath selectedPath = (IPath) selectedContainers[0];
					IFolder selectedFolder = project.getParent().getFolder(selectedPath);
					IPath projectRelativePath = selectedFolder.getProjectRelativePath();
					entry.setValue(projectRelativePath.toPortableString());
				}
				entry.getText().forceFocus();
			}
		});

		Composite templatesSection = createSection(toolkit, parent, "Templates", "These paths specify the templates that will be used to generate files.  If left blank, the defaults from the EOGenerator preference page will be used.", 1, 3);
		GridLayout templatesSectionLayout = (GridLayout) templatesSection.getLayout();
		templatesSectionLayout.horizontalSpacing = 10;

		Label javaLabel = toolkit.createLabel(templatesSection, "Java?");
		javaLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		_javaButton = toolkit.createButton(templatesSection, "", SWT.CHECK);
		GridData javaButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		javaButtonGridData.horizontalSpan = 2;
		_javaButton.setLayoutData(packageDirsButtonGridData);
		_javaButton.setSelection(_model.isJava() != null && _model.isJava().booleanValue());
		_javaButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent _e) {
				widgetSelected(_e);
			}

			public void widgetSelected(SelectionEvent _e) {
				EOGeneratorFormPage.this.getModel().setJava(Boolean.valueOf(_javaButton.getSelection()));
				getEditor().editorDirtyStateChanged();
			}
		});

		Label javaClientLabel = toolkit.createLabel(templatesSection, "Java Client?");
		javaClientLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));

		_javaClientButton = toolkit.createButton(templatesSection, "", SWT.CHECK);
		GridData javaClientButtonGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		javaClientButtonGridData.horizontalSpan = 2;
		_javaClientButton.setLayoutData(packageDirsButtonGridData);
		_javaClientButton.setSelection(_model.isJavaClient() != null && _model.isJavaClient().booleanValue());
		_javaClientButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent _e) {
				widgetSelected(_e);
			}

			public void widgetSelected(SelectionEvent _e) {
				EOGeneratorFormPage.this.getModel().setJavaClient(Boolean.valueOf(_javaClientButton.getSelection()));
				getEditor().editorDirtyStateChanged();
			}
		});

		_templatesFolderEntry = new FormEntry(templatesSection, toolkit, "Templates Folder", "Browse...", false);
		_templatesFolderEntry.setValue(_model.getTemplateDir(null));
		_templatesFolderEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setTemplateDir(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setTemplateDir(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}

			public void browseButtonSelected(FormEntry entry) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getEditorSite().getShell());
				directoryDialog.setMessage("Select the folder that contains your EOGenerator templates.");
				directoryDialog.setFilterPath(entry.getValue());
				String selectedDirectory = directoryDialog.open();
				if (selectedDirectory != null) {
					entry.setValue(selectedDirectory);
				}
				entry.getText().forceFocus();
			}
		});

		_templateEntry = new FormEntry(templatesSection, toolkit, "Template", "Browse...", false);
		_templateEntry.setValue(_model.getJavaTemplate(null));
		_templateEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setJavaTemplate(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setJavaTemplate(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}

			public void browseButtonSelected(FormEntry entry) {
				EOGeneratorFormPage.this.selectTemplate("Select the superclass template.", entry);
				entry.getText().forceFocus();
			}
		});

		_subclassTemplateEntry = new FormEntry(templatesSection, toolkit, "Subclass Template", "Browse...", false);
		_subclassTemplateEntry.setValue(_model.getSubclassJavaTemplate(null));
		_subclassTemplateEntry.setFormEntryListener(new EOFormEntryAdapter() {
			public void textValueChanged(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setSubclassJavaTemplate(entry.getValue());
				getEditor().editorDirtyStateChanged();
			}

			public void textDirty(FormEntry entry) {
				EOGeneratorFormPage.this.getModel().setSubclassJavaTemplate(entry.getText().getText());
				getEditor().editorDirtyStateChanged();
			}

			public void browseButtonSelected(FormEntry entry) {
				EOGeneratorFormPage.this.selectTemplate("Select the subclass template.", entry);
				entry.getText().forceFocus();
			}
		});
	}

	protected void selectTemplate(String text, FormEntry entry) {
		FileDialog templateDialog = new FileDialog(getEditorSite().getShell());
		templateDialog.setFileName(_templatesFolderEntry.getValue());
		templateDialog.setText(text);
		templateDialog.setFilterExtensions(new String[] { "*.eotemplate" });
		String templateDir = _templatesFolderEntry.getValue();
		if (templateDir != null) {
			templateDialog.setFilterPath(templateDir);
		}
		String selectedTemplate = templateDialog.open();
		if (selectedTemplate != null) {
			if (templateDir != null && selectedTemplate.startsWith(templateDir)) {
				int templateDirLength = templateDir.length();
				if (!templateDir.endsWith(File.separator)) {
					templateDirLength += File.separator.length();
				}
				selectedTemplate = selectedTemplate.substring(templateDirLength);
			}
			entry.setValue(selectedTemplate);
		}
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
			_destinationEntry.setValue(_model.getDestination());
			_subclassDestinationEntry.setValue(_model.getSubclassDestination());
			_subclassTemplateEntry.setValue(_model.getSubclassJavaTemplate(null));
			_templateEntry.setValue(_model.getJavaTemplate(null));
			_templatesFolderEntry.setValue(_model.getTemplateDir(null));
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
						getEditor().editorDirtyStateChanged();
					}
				}
			}
		}
	}

	protected class ModelRemoveModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			IStructuredSelection selection = (IStructuredSelection) _modelsTableViewer.getSelection();
			if (!selection.isEmpty()) {
				List<EOModelReference> models = EOGeneratorFormPage.this.getModel().getModels();
				List<EOModelReference> newModels = new LinkedList<EOModelReference>(models);
				Object[] selections = selection.toArray();
				for (int i = 0; i < selections.length; i++) {
					newModels.remove(selections[i]);
				}
				EOGeneratorFormPage.this.getModel().setModels(newModels);
				_modelsTableViewer.refresh();
				getEditor().editorDirtyStateChanged();
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
				_modelsTableViewer.refresh();
				getEditor().editorDirtyStateChanged();
			}
		}
	}

	protected class RefModelRemoveModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			IStructuredSelection selection = (IStructuredSelection) _refModelsTableViewer.getSelection();
			if (!selection.isEmpty()) {
				List<EOModelReference> refModels = EOGeneratorFormPage.this.getModel().getRefModels();
				List<EOModelReference> newRefModels = new LinkedList<EOModelReference>(refModels);
				Object[] selections = selection.toArray();
				for (int i = 0; i < selections.length; i++) {
					newRefModels.remove(selections[i]);
				}
				EOGeneratorFormPage.this.getModel().setRefModels(newRefModels);
				_refModelsTableViewer.refresh();
				getEditor().editorDirtyStateChanged();
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
				_refModelsTableViewer.refresh();
				getEditor().editorDirtyStateChanged();
			}
		}
	}

	protected class DefinesRemoveModelListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			IStructuredSelection selection = (IStructuredSelection) _definesTableViewer.getSelection();
			if (!selection.isEmpty()) {
				List<Define> defines = EOGeneratorFormPage.this.getModel().getDefines();
				List<Define> newDefines = new LinkedList<Define>(defines);
				Object[] selections = selection.toArray();
				for (int i = 0; i < selections.length; i++) {
					newDefines.remove(selections[i]);
				}
				EOGeneratorFormPage.this.getModel().setDefines(newDefines);
				_definesTableViewer.refresh();
				getEditor().editorDirtyStateChanged();
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
			IStructuredSelection selection = (IStructuredSelection) _definesTableViewer.getSelection();
			if (!selection.isEmpty()) {
				EOGeneratorModel.Define define = (EOGeneratorModel.Define) selection.getFirstElement();
				if (define != null) {
					EOGeneratorFormPage.this.addDefine(define.getName(), define.getValue());
				}
			}
		}
	}

	protected class EOFormEntryAdapter implements IFormEntryListener {
		public void browseButtonSelected(FormEntry entry) {
			// do nothing
		}

		public void focusGained(FormEntry entry) {
			// do nothing
		}

		public void selectionChanged(FormEntry entry) {
			// do nothing
		}

		public void textDirty(FormEntry entry) {
			// do nothing
		}

		public void textValueChanged(FormEntry entry) {
			// do nothing
		}

		public void linkActivated(HyperlinkEvent event) {
			// do nothing
		}

		public void linkEntered(HyperlinkEvent event) {
			// do nothing
		}

		public void linkExited(HyperlinkEvent event) {
			// do nothing
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
			String name = model.getPath(_model.getProject());
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
