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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;

public class EOGeneratorFormPage extends FormPage {
  private EOGeneratorModel myModel;
  private FormEntry myDestinationEntry;
  private FormEntry mySubclassDestinationEntry;
  private FormEntry myTemplatesFolderEntry;
  private FormEntry myTemplateEntry;
  private FormEntry mySubclassTemplateEntry;
  private TableViewer myModelsTableViewer;
  private TableViewer myRefModelsTableViewer;
  private TableViewer myDefinesTableViewer;

  public EOGeneratorFormPage(FormEditor _editor, EOGeneratorModel _model) {
    super(_editor, "EOGeneratorForm", "EOGenerator Form");
    myModel = _model;
  }

  protected void setInput(IEditorInput _input) {
    super.setInput(_input);
  }

  protected String getString(String _name) {
    return _name;
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
    myModelsTableViewer = createModelsSection("Models", "These models will have Java files generated for all of their entities.", toolkit, body, modelsModel, modelsModel, new ModelAddModelListener(), new ModelRemoveModelListener());

    RefModelsTableContentProvider refModelsModel = new RefModelsTableContentProvider();
    myRefModelsTableViewer = createModelsSection("Referenced Models", "These models are used to resolve type references from models listed in the first section.  No Java files will be generated for these models.", toolkit, body, refModelsModel, refModelsModel, new RefModelAddModelListener(), new RefModelRemoveModelListener());

    createPathsSection(toolkit, body);

    createDefinesSection(toolkit, body);

    updateViewsFromModel();

    form.reflow(true);
  }

  private TableViewer createModelsSection(String _title, String _description, FormToolkit _toolkit, Composite _parent, IStructuredContentProvider _contentProvider, ITableLabelProvider _labelProvider, SelectionListener _addListener, SelectionListener _removeListener) {
    Composite modelsSection = createSection(_toolkit, _parent, _title, _description, 1, 2);
    Table modelsTable = _toolkit.createTable(modelsSection, SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);

    GridData modelsTableGridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
    modelsTableGridData.heightHint = 75;
    modelsTable.setLayoutData(modelsTableGridData);

    TableViewer modelsTableViewer = new TableViewer(modelsTable);
    modelsTableViewer.setContentProvider(_contentProvider);
    modelsTableViewer.setLabelProvider(_labelProvider);

    Composite modelsButtonsComposite = _toolkit.createComposite(modelsSection);
    modelsButtonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    GridLayout modelsButtonsLayout = new GridLayout();
    modelsButtonsLayout.marginTop = 0;
    modelsButtonsLayout.marginBottom = 0;
    modelsButtonsLayout.verticalSpacing = 0;
    modelsButtonsLayout.horizontalSpacing = 0;
    modelsButtonsLayout.numColumns = 1;
    modelsButtonsComposite.setLayout(modelsButtonsLayout);

    Button modelsAddButton = _toolkit.createButton(modelsButtonsComposite, "Add...", SWT.PUSH);
    GridData modelsAddButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    modelsAddButton.setLayoutData(modelsAddButtonGridData);
    modelsAddButton.addSelectionListener(_addListener);

    Button modelsRemoveButton = _toolkit.createButton(modelsButtonsComposite, "Remove", SWT.PUSH);
    GridData modelsRemoveButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    modelsRemoveButton.setLayoutData(modelsRemoveButtonGridData);
    modelsRemoveButton.addSelectionListener(_removeListener);

    return modelsTableViewer;
  }

  protected void createPathsSection(FormToolkit _toolkit, Composite _parent) {
    FileEditorInput editorInput = (FileEditorInput) getEditorInput();
    IFile eogenFile = editorInput.getFile();
    final IProject project = eogenFile.getProject();

    Composite pathsSection = createSection(_toolkit, _parent, "Java Destination Paths", "These paths specify where generated Java files will be written and are project-relative.", 1, 3);
    GridLayout pathsSectionLayout = (GridLayout) pathsSection.getLayout();
    pathsSectionLayout.horizontalSpacing = 10;

    myDestinationEntry = new FormEntry(pathsSection, _toolkit, "Destination", "Browse...", false);
    myDestinationEntry.setValue(myModel.getDestination());
    myDestinationEntry.setFormEntryListener(new EOFormEntryAdapter() {
      public void textValueChanged(FormEntry _entry) {
        myModel.setDestination(_entry.getValue());
        getEditor().editorDirtyStateChanged();
      }

      public void textDirty(FormEntry _entry) {
        myModel.setDestination(_entry.getText().getText());
        getEditor().editorDirtyStateChanged();
      }

      public void browseButtonSelected(FormEntry _entry) {
        ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder to write autogenerated Java files into.");
        containerDialog.open();
        Object[] selectedContainers = containerDialog.getResult();
        if (selectedContainers != null && selectedContainers.length > 0) {
          IPath selectedPath = (IPath) selectedContainers[0];
          IFolder selectedFolder = project.getParent().getFolder(selectedPath);
          IPath projectRelativePath = selectedFolder.getProjectRelativePath();
          _entry.setValue(projectRelativePath.toPortableString());
        }
        _entry.getText().forceFocus();
      }
    });

    mySubclassDestinationEntry = new FormEntry(pathsSection, _toolkit, "Subclass Destination", "Browse...", false);
    mySubclassDestinationEntry.setValue(myModel.getSubclassDestination());
    mySubclassDestinationEntry.setFormEntryListener(new EOFormEntryAdapter() {
      public void textValueChanged(FormEntry _entry) {
        myModel.setSubclassDestination(_entry.getValue());
        getEditor().editorDirtyStateChanged();
      }

      public void textDirty(FormEntry _entry) {
        myModel.setSubclassDestination(_entry.getText().getText());
        getEditor().editorDirtyStateChanged();
      }

      public void browseButtonSelected(FormEntry _entry) {
        ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the folder to generate customizable Java files into.");
        containerDialog.open();
        Object[] selectedContainers = containerDialog.getResult();
        if (selectedContainers != null && selectedContainers.length > 0) {
          IPath selectedPath = (IPath) selectedContainers[0];
          IFolder selectedFolder = project.getParent().getFolder(selectedPath);
          IPath projectRelativePath = selectedFolder.getProjectRelativePath();
          _entry.setValue(projectRelativePath.toPortableString());
        }
        _entry.getText().forceFocus();
      }
    });

    Composite templatesSection = createSection(_toolkit, _parent, "Templates", "These paths specify the templates that will be used to generate Java files.  If left blank, the defaults from the EOGenerator preference page will be used.", 1, 3);
    GridLayout templatesSectionLayout = (GridLayout) templatesSection.getLayout();
    templatesSectionLayout.horizontalSpacing = 10;

    myTemplatesFolderEntry = new FormEntry(templatesSection, _toolkit, "Templates Folder", "Browse...", false);
    myTemplatesFolderEntry.setValue(myModel.getTemplateDir(null));
    myTemplatesFolderEntry.setFormEntryListener(new EOFormEntryAdapter() {
      public void textValueChanged(FormEntry _entry) {
        myModel.setTemplateDir(_entry.getValue());
        getEditor().editorDirtyStateChanged();
      }

      public void textDirty(FormEntry _entry) {
        myModel.setTemplateDir(_entry.getText().getText());
        getEditor().editorDirtyStateChanged();
      }

      public void browseButtonSelected(FormEntry _entry) {
        DirectoryDialog directoryDialog = new DirectoryDialog(getEditorSite().getShell());
        directoryDialog.setMessage("Select the folder that contains your EOGenerator templates.");
        directoryDialog.setFilterPath(_entry.getValue());
        String selectedDirectory = directoryDialog.open();
        if (selectedDirectory != null) {
          _entry.setValue(selectedDirectory);
        }
        _entry.getText().forceFocus();
      }
    });

    myTemplateEntry = new FormEntry(templatesSection, _toolkit, "Template", "Browse...", false);
    myTemplateEntry.setValue(myModel.getJavaTemplate(null));
    myTemplateEntry.setFormEntryListener(new EOFormEntryAdapter() {
      public void textValueChanged(FormEntry _entry) {
        myModel.setJavaTemplate(_entry.getValue());
        getEditor().editorDirtyStateChanged();
      }

      public void textDirty(FormEntry _entry) {
        myModel.setJavaTemplate(_entry.getText().getText());
        getEditor().editorDirtyStateChanged();
      }

      public void browseButtonSelected(FormEntry _entry) {
        EOGeneratorFormPage.this.selectTemplate("Select the superclass template.", _entry);
        _entry.getText().forceFocus();
      }
    });

    mySubclassTemplateEntry = new FormEntry(templatesSection, _toolkit, "Subclass Template", "Browse...", false);
    mySubclassTemplateEntry.setValue(myModel.getSubclassJavaTemplate(null));
    mySubclassTemplateEntry.setFormEntryListener(new EOFormEntryAdapter() {
      public void textValueChanged(FormEntry _entry) {
        myModel.setSubclassJavaTemplate(_entry.getValue());
        getEditor().editorDirtyStateChanged();
      }

      public void textDirty(FormEntry _entry) {
        myModel.setSubclassJavaTemplate(_entry.getText().getText());
        getEditor().editorDirtyStateChanged();
      }

      public void browseButtonSelected(FormEntry _entry) {
        EOGeneratorFormPage.this.selectTemplate("Select the subclass template.", _entry);
        _entry.getText().forceFocus();
      }
    });
  }

  protected void selectTemplate(String _text, FormEntry _entry) {
    FileDialog templateDialog = new FileDialog(getEditorSite().getShell());
    templateDialog.setFileName(myTemplatesFolderEntry.getValue());
    templateDialog.setText(_text);
    templateDialog.setFilterExtensions(new String[] { "*.eotemplate" });
    String templateDir = myTemplatesFolderEntry.getValue();
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
      _entry.setValue(selectedTemplate);
    }
  }

  protected void createDefinesSection(FormToolkit _toolkit, Composite _parent) {
    Composite definesSection = createSection(_toolkit, _parent, "Defines", "These variables will turn into EOGenerator -define-Xxx parameters that will be accessible in your templates (i.e. EOGenericRecord, etc)", 1, 2);
    Table definesTable = _toolkit.createTable(definesSection, SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);

    definesTable.setLinesVisible(true);
    definesTable.setHeaderVisible(true);

    TableColumn defineNameTableColumn = new TableColumn(definesTable, SWT.NONE);
    defineNameTableColumn.setWidth(175);
    defineNameTableColumn.setText("Name");

    TableColumn defineValueTableColumn = new TableColumn(definesTable, SWT.NONE);
    defineValueTableColumn.setWidth(400);
    defineValueTableColumn.setText("Value");

    myDefinesTableViewer = new TableViewer(definesTable);
    DefinesTableContentProvider definesModel = new DefinesTableContentProvider();
    myDefinesTableViewer.setContentProvider(definesModel);
    myDefinesTableViewer.setLabelProvider(definesModel);
    myDefinesTableViewer.addDoubleClickListener(new DefinesDoubleClickListener());

    GridData definesTableGridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
    definesTableGridData.heightHint = 125;
    definesTable.setLayoutData(definesTableGridData);
    Composite definesButtonsComposite = _toolkit.createComposite(definesSection);
    definesButtonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    GridLayout definesButtonsLayout = new GridLayout();
    definesButtonsLayout.marginTop = 0;
    definesButtonsLayout.marginBottom = 0;
    definesButtonsLayout.verticalSpacing = 0;
    definesButtonsLayout.horizontalSpacing = 0;
    definesButtonsLayout.numColumns = 1;
    definesButtonsComposite.setLayout(definesButtonsLayout);

    Button definesAddButton = _toolkit.createButton(definesButtonsComposite, "Add...", SWT.PUSH);
    GridData definesAddButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    definesAddButton.setLayoutData(definesAddButtonGridData);
    definesAddButton.addSelectionListener(new DefineAddModelListener());

    Button definesRemoveButton = _toolkit.createButton(definesButtonsComposite, "Remove", SWT.PUSH);
    GridData definesRemoveButtonGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    definesRemoveButton.setLayoutData(definesRemoveButtonGridData);
    definesRemoveButton.addSelectionListener(new DefinesRemoveModelListener());
  }

  protected void updateViewsFromModel() {
    myModelsTableViewer.setInput(myModel);
    myRefModelsTableViewer.setInput(myModel);
    myDefinesTableViewer.setInput(myModel);
    myDestinationEntry.setValue(myModel.getDestination());
    mySubclassDestinationEntry.setValue(myModel.getSubclassDestination());
    mySubclassTemplateEntry.setValue(myModel.getSubclassJavaTemplate(null));
    myTemplateEntry.setValue(myModel.getJavaTemplate(null));
    myTemplatesFolderEntry.setValue(myModel.getTemplateDir(null));
  }

  protected Composite createSection(FormToolkit _toolkit, Composite _parent, String _title, String _description, int _spanColumns, int _sectionColumns) {
    int style = (_description == null) ? Section.TITLE_BAR | Section.EXPANDED : Section.TITLE_BAR | Section.EXPANDED | Section.DESCRIPTION;
    Section section = _toolkit.createSection(_parent, style);
    GridLayout sectionLayout = new GridLayout();
    section.setLayout(sectionLayout);
    section.setText(_title);
    if (_description != null) {
      section.setDescription(_description);
    }
    GridData sectionGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    sectionGridData.horizontalSpan = _spanColumns;
    section.setLayoutData(sectionGridData);

    Composite sectionClient = _toolkit.createComposite(section, SWT.NONE);
    GridData sectionClientGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    sectionClient.setLayoutData(sectionClientGridData);
    GridLayout sectionClientLayout = new GridLayout();
    sectionClientLayout.marginHeight = 0;
    sectionClientLayout.marginWidth = 0;
    sectionClientLayout.verticalSpacing = 0;
    sectionClientLayout.horizontalSpacing = 0;
    sectionClientLayout.numColumns = _sectionColumns;
    sectionClient.setLayout(sectionClientLayout);
    section.setClient(sectionClient);

    return sectionClient;
  }

  protected void addDefine(String _name, String _value) {
    InputDialog nameDialog = new InputDialog(getEditorSite().getShell(), "Enter Name", "Enter the name of this variable.", _name, null);
    int nameRetval = nameDialog.open();
    if (nameRetval == InputDialog.OK) {
      String name = nameDialog.getValue();
      if (name != null && name.trim().length() > 0) {
        InputDialog valueDialog = new InputDialog(getEditorSite().getShell(), "Enter Value", "Enter the value of this variable.", _value, null);
        int valueRetval = valueDialog.open();
        if (valueRetval == InputDialog.OK) {
          String value = valueDialog.getValue();
          if (value != null && value.trim().length() > 0) {
            EOGeneratorModel.Define define = new EOGeneratorModel.Define(name, value);
            List defines = myModel.getDefines();
            LinkedList newDefines = new LinkedList(defines);
            if (_name != null && _name.trim().length() > 0) {
              EOGeneratorModel.Define oldDefine = new EOGeneratorModel.Define(_name, _value);
              newDefines.remove(oldDefine);
            }
            newDefines.remove(define);
            newDefines.add(define);
            myModel.setDefines(newDefines);
            myDefinesTableViewer.refresh();
            getEditor().editorDirtyStateChanged();
          }
        }
      }
    }
  }

  protected class ModelRemoveModelListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      IStructuredSelection selection = (IStructuredSelection) myModelsTableViewer.getSelection();
      if (!selection.isEmpty()) {
        List models = myModel.getModels();
        LinkedList newModels = new LinkedList(models);
        Object[] selections = selection.toArray();
        for (int i = 0; i < selections.length; i++) {
          newModels.remove(selections[i]);
        }
        myModel.setModels(newModels);
        myModelsTableViewer.refresh();
        getEditor().editorDirtyStateChanged();
      }
    }
  }

  protected class ModelAddModelListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      FileEditorInput editorInput = (FileEditorInput) getEditorInput();
      IFile eogenFile = editorInput.getFile();
      IProject project = eogenFile.getProject();
      ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getEditorSite().getShell(), project, false, "Select the EOModel to add.");
      containerDialog.open();
      Object[] selectedContainers = containerDialog.getResult();
      if (selectedContainers != null && selectedContainers.length > 0) {
        IPath selectedPath = (IPath) selectedContainers[0];
        IFolder selectedFolder = project.getParent().getFolder(selectedPath);
        IPath projectRelativePath = selectedFolder.getProjectRelativePath();
        EOModelReference eoModel = new EOModelReference(projectRelativePath.toPortableString());
        addModel(eoModel);
      }
    }

    protected void addModel(EOModelReference _eoModel) {
      List models = myModel.getModels();
      if (!models.contains(_eoModel)) {
        LinkedList newModels = new LinkedList(models);
        newModels.add(_eoModel);
        myModel.setModels(newModels);
        myModelsTableViewer.refresh();
        getEditor().editorDirtyStateChanged();
      }
    }
  }

  protected class RefModelRemoveModelListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      IStructuredSelection selection = (IStructuredSelection) myRefModelsTableViewer.getSelection();
      if (!selection.isEmpty()) {
        List refModels = myModel.getRefModels();
        LinkedList newRefModels = new LinkedList(refModels);
        Object[] selections = selection.toArray();
        for (int i = 0; i < selections.length; i++) {
          newRefModels.remove(selections[i]);
        }
        myModel.setRefModels(newRefModels);
        myRefModelsTableViewer.refresh();
        getEditor().editorDirtyStateChanged();
      }
    }
  }

  protected class RefModelAddModelListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      FileEditorInput editorInput = (FileEditorInput) getEditorInput();
      IFile eogenFile = editorInput.getFile();
      IProject project = eogenFile.getProject();
      DirectoryDialog directoryDialog = new DirectoryDialog(getEditorSite().getShell());
      directoryDialog.setMessage("Select the Reference EOModel to add.");
      String selectedDirectory = directoryDialog.open();
      if (selectedDirectory != null) {
        EOModelReference eoModel = new EOModelReference(selectedDirectory);
        addModel(eoModel);
      }
    }

    protected void addModel(EOModelReference _eoModel) {
      List refModels = myModel.getRefModels();
      if (!refModels.contains(_eoModel)) {
        LinkedList newRefModels = new LinkedList(refModels);
        newRefModels.add(_eoModel);
        myModel.setRefModels(newRefModels);
        myRefModelsTableViewer.refresh();
        getEditor().editorDirtyStateChanged();
      }
    }
  }

  protected class DefinesRemoveModelListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      IStructuredSelection selection = (IStructuredSelection) myDefinesTableViewer.getSelection();
      if (!selection.isEmpty()) {
        List defines = myModel.getDefines();
        LinkedList newDefines = new LinkedList(defines);
        Object[] selections = selection.toArray();
        for (int i = 0; i < selections.length; i++) {
          newDefines.remove(selections[i]);
        }
        myModel.setDefines(newDefines);
        myDefinesTableViewer.refresh();
        getEditor().editorDirtyStateChanged();
      }
    }
  }

  protected class DefineAddModelListener implements SelectionListener {
    public void widgetDefaultSelected(SelectionEvent _e) {
      widgetSelected(_e);
    }

    public void widgetSelected(SelectionEvent _e) {
      EOGeneratorFormPage.this.addDefine("", "");
    }
  }

  protected class DefinesDoubleClickListener implements IDoubleClickListener {
    public void doubleClick(DoubleClickEvent _event) {
      IStructuredSelection selection = (IStructuredSelection) myDefinesTableViewer.getSelection();
      if (!selection.isEmpty()) {
        EOGeneratorModel.Define define = (EOGeneratorModel.Define) selection.getFirstElement();
        if (define != null) {
          EOGeneratorFormPage.this.addDefine(define.getName(), define.getValue());
        }
      }
    }
  }

  protected class EOFormEntryAdapter implements IFormEntryListener {
    public void browseButtonSelected(FormEntry _entry) {
    }

    public void focusGained(FormEntry _entry) {
    }

    public void selectionChanged(FormEntry _entry) {
    }

    public void textDirty(FormEntry _entry) {
    }

    public void textValueChanged(FormEntry _entry) {
    }

    public void linkActivated(HyperlinkEvent _e) {
    }

    public void linkEntered(HyperlinkEvent _e) {
    }

    public void linkExited(HyperlinkEvent _e) {
    }
  }

  protected class DefinesTableContentProvider implements IStructuredContentProvider, ITableLabelProvider {
    public DefinesTableContentProvider() {
    }

    public Object[] getElements(Object _inputElement) {
      Object[] models = myModel.getDefines().toArray();
      return models;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
    }

    public Image getColumnImage(Object _element, int _columnIndex) {
      return null;
    }

    public String getColumnText(Object _element, int _columnIndex) {
      EOGeneratorModel.Define define = (EOGeneratorModel.Define) _element;
      String text;
      if (_columnIndex == 0) {
        text = define.getName();
      }
      else if (_columnIndex == 1) {
        text = define.getValue();
      }
      else {
        text = "";
      }
      return text;
    }

    public void addListener(ILabelProviderListener _listener) {
    }

    public boolean isLabelProperty(Object _element, String _property) {
      return false;
    }

    public void removeListener(ILabelProviderListener _listener) {
    }
  }

  protected abstract class AbstractModelsTableContentProvider implements IStructuredContentProvider, ITableLabelProvider {
    public AbstractModelsTableContentProvider() {
    }

    public void dispose() {
    }

    public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
    }

    public Image getColumnImage(Object _element, int _columnIndex) {
      return null;
    }

    public String getColumnText(Object _element, int _columnIndex) {
      EOModelReference model = (EOModelReference) _element;
      String name = model.getPath();
      return name;
    }

    public void addListener(ILabelProviderListener _listener) {
    }

    public boolean isLabelProperty(Object _element, String _property) {
      return false;
    }

    public void removeListener(ILabelProviderListener _listener) {
    }
  }

  protected class ModelsTableContentProvider extends AbstractModelsTableContentProvider {
    public ModelsTableContentProvider() {
    }

    public Object[] getElements(Object _inputElement) {
      Object[] models = myModel.getModels().toArray();
      return models;
    }
  }

  protected class RefModelsTableContentProvider extends AbstractModelsTableContentProvider {
    public RefModelsTableContentProvider() {
    }

    public Object[] getElements(Object _inputElement) {
      Object[] models = myModel.getRefModels().toArray();
      return models;
    }
  }
}
