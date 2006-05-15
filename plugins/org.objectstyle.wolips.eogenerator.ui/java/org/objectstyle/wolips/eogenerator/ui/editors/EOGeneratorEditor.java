package org.objectstyle.wolips.eogenerator.ui.editors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGeneratorEditor extends FormEditor {
  private EOGeneratorModel myModel;

  public EOGeneratorEditor() {
  }

  public static EOGeneratorModel createEOGeneratorModel(IFile _file) throws ParseException, CoreException, IOException {
    _file.refreshLocal(IFile.DEPTH_INFINITE, null);
    InputStream eogenFileStream = _file.getContents();
    try {
      StringBuffer sb = new StringBuffer();
      BufferedReader br = new BufferedReader(new InputStreamReader(eogenFileStream));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      EOGeneratorModel model = new EOGeneratorModel(sb.toString());
      model.setEOGeneratorPath(Preferences.getEOGeneratorPath());
      return model;
    }
    finally {
      eogenFileStream.close();
    }
  }

  protected void setInput(IEditorInput _input) {
    super.setInput(_input);
    try {
      FileEditorInput editorInput = (FileEditorInput) _input;
      IFile eogenFile = editorInput.getFile();
      myModel = EOGeneratorEditor.createEOGeneratorModel(eogenFile);
    }
    catch (Throwable e) {
      throw new RuntimeException("Failed to read EOGen file.", e);
    }
  }

  protected void addPages() {
    try {
      addPage(new EOGeneratorFormPage(this, myModel));
    }
    catch (PartInitException e) {
      ErrorDialog.openError(getSite().getShell(), "Error creating form pages.", null, e.getStatus());
    }
  }

  public void doSave(IProgressMonitor _monitor) {
    try {
      String eogenFileContents = myModel.writeToString(Preferences.getEOGeneratorPath(), Preferences.getEOGeneratorTemplateDir(), Preferences.getEOGeneratorJavaTemplate(), Preferences.getEOGeneratorSubclassJavaTemplate());
      byte[] bytes = eogenFileContents.getBytes("UTF-8");
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      FileEditorInput editorInput = (FileEditorInput) getEditorInput();
      IFile eogenFile = editorInput.getFile();
      if (!eogenFile.exists()) {
        eogenFile.create(bais, true, _monitor);
      }
      else {
        eogenFile.setContents(bais, true, true, _monitor);
      }
      myModel.setDirty(false);
      editorDirtyStateChanged();
    }
    catch (Throwable e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to write EOGen file.", e);
    }
  }

  public void doSaveAs() {
  }

  public boolean isSaveAsAllowed() {
    return true;
  }

  public boolean isDirty() {
    return myModel.isDirty() || super.isDirty();
  }

}
