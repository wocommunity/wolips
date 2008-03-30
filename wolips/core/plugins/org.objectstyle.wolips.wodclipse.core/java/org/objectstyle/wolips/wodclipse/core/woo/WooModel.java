package org.objectstyle.wolips.wodclipse.core.woo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelParserDataStructureFactory;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.PropertyListMap;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.EOModelGroupCache;

public class WooModel {
  public static final String IS_DIRTY = "IS_DIRTY";

  public static final String DISPLAY_GROUP_NAME = "DISPLAY_GROUP_NAME";

  public static final String ENCODING = "encoding";

  public static final String DEFAULT_ENCODING = "UTF-8";

  public static final String DEFAULT_WO_RELEASE = "WebObjects 5.0";

  private IFile _file;

  private boolean _isDirty;

  private EOModelGroup _modelGroup;

  private String _encoding;

  private String _woRelease = DEFAULT_WO_RELEASE;

  private EOModelMap _modelMap;

  private PropertyListMap<Object, Object> _variables;

  private List<DisplayGroup> _displayGroups;

  private PropertyChangeSupport _changes = new PropertyChangeSupport(this);

  private PropertyChangeListener _displayGroupListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
      if (DisplayGroup.NAME.equals(evt.getPropertyName())) {
        PropertyChangeEvent newEvent = new PropertyChangeEvent(evt.getSource(), DISPLAY_GROUP_NAME, evt.getOldValue(), evt.getNewValue());
        _changes.firePropertyChange(newEvent);
      }
    }

  };

  public WooModel(final IFile file) {
    _file = file;
    try {
      init();
    }
    catch (Throwable e) {
    }
  }

  public WooModel(final URL url) {
    // TODO: Fix me
  }

  public WooModel(final String contents) throws WooModelException {
    InputStream input = new ByteArrayInputStream(contents.getBytes());
    try {
      loadModelFromStream(input);
    }
    catch (Throwable e) {
      throw new WooModelException(e.getMessage());
    }
  }

  public WooModel(final InputStream input) throws WooModelException {
    try {
      loadModelFromStream(input);
    }
    catch (Throwable e) {
      throw new WooModelException(e.getMessage());
    }
  }

  public WooModel(IEditorInput editorInput) {
    if (editorInput instanceof IFileEditorInput) {
      _file = ((IFileEditorInput) editorInput).getFile();
    }
    try {
      init();
    }
    catch (Throwable e) {
    }
  }

  private void init() throws IOException, PropertyListParserException {
    if (_file == null || !_file.exists()) {
      loadModelFromStream(new ByteArrayInputStream(blankContent().getBytes()));

    }
    else {
      loadModelFromFile(_file.getLocation().toFile());
    }
  }

  public String blankContent() {
    // XXX Should use components default encoding charset
    StringBuffer sb = new StringBuffer();
    sb.append("{\n");
    sb.append("    \"WebObjects Release\" = \"WebObjects 5.0\";\n");
    sb.append("     encoding = \"" + getEncoding() + "\";\n");
    sb.append("}\n");
    return sb.toString();
  }

  private void resetModel() {
    _encoding = null;
    _woRelease = DEFAULT_WO_RELEASE;
    _variables = null;
    _modelMap = null;
    _displayGroups = null;
  }

  public String getLocation() {
    String location;
    if (_file != null) {
      location = _file.getFullPath().toString();
    }
    else {
      location = null;
    }
    return location;
  }

  public DisplayGroup[] getDisplayGroups() {
    if (_displayGroups != null) {
      return _displayGroups.toArray(new DisplayGroup[] {});
    }
    return new DisplayGroup[0];
  }

  public String getEncoding() {
    if (_encoding == null) {
      if (_modelMap != null && _modelMap.containsKey("encoding")) {
        _encoding = _modelMap.getString("encoding", true);
      }
      else if (_file != null && _file.getParent().exists()) {
        try {
          _encoding = _file.getParent().getDefaultCharset();
        }
        catch (CoreException e) {
        }
      }
    }
    if (_encoding == null)
      return DEFAULT_ENCODING;
    return _encoding;
  }

  public EOModelGroup getModelGroup() {
    if (_modelGroup == null) {
      _modelGroup = WodParserCache.getModelGroupCache().getModelGroup(_file.getProject());
    }
    return _modelGroup;
  }

  public void setEncoding(String encoding) {
    String oldEncoding = _encoding;
    _encoding = encoding;
    _changes.firePropertyChange(ENCODING, oldEncoding, _encoding);
  }

  private void loadModelFromFile(final File file) throws IOException, PropertyListParserException {
    _modelMap = new EOModelMap((Map<?, ?>) PropertyListSerialization.propertyListFromFile(file, new EOModelParserDataStructureFactory()));
  }

  public void loadModelFromStream(final InputStream input) throws IOException, PropertyListParserException {
    _modelMap = new EOModelMap((Map<?, ?>) PropertyListSerialization.propertyListFromStream(input, new EOModelParserDataStructureFactory()));
  }

  @SuppressWarnings("unchecked")
  public void parseModel() {
    Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();

    if (_modelMap == null)
      return;

    if (_modelMap.containsKey("encoding")) {
      _encoding = _modelMap.getString("encoding", true);
    }
    if (_modelMap.containsKey("WebObjects Release")) {
      _woRelease = _modelMap.getString("WebObjects Release", true);
    }

    _variables = new PropertyListMap<Object, Object>();
    _displayGroups = new ArrayList<DisplayGroup>();

    Map<?, ?> variables = _modelMap.getMap("variables");
    if (variables != null) {
      EOModelMap variableMap = new EOModelMap(variables);
      Set<Map.Entry<String, Object>> variableEntries = variableMap.entrySet();
      for (Map.Entry<String, Object> entry : variableEntries) {
        if (entry.getValue() instanceof Map) {
          EOModelMap entryMap = new EOModelMap((Map<?, ?>) entry.getValue());
          String className = entryMap.getString("class", true);
          if ("WODisplayGroup".equals(className)) {
            DisplayGroup displayGroup = new DisplayGroup(this);
            displayGroup.setName(entry.getKey());
            displayGroup.loadFromMap(entryMap, failures);
            _displayGroups.add(displayGroup);
            displayGroup.addPropertyChangeListener(_displayGroupListener);
            continue;
          }
        }
        _variables.put(entry.getKey(), entry.getValue());
      }
    }

    _isDirty = false;
  }

  public void doSave() throws IOException {
    if (_file == null) {
      throw new IOException("You can not save changes to a WooModel that is not " + "backed by a file.");
    }
    File file = _file.getLocation().toFile();
    FileOutputStream writer = new FileOutputStream(file);
    try {
      doSave(writer);
      _isDirty = false;
      _file.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    }
    catch (CoreException e) {
      e.printStackTrace();
    }
    finally {
      writer.close();
    }

  }

  public void doSave(final OutputStream writer) throws IOException {
    // XXX Need to validate model before saving
    EOModelMap modelMap = toModelMap();
    try {
      PropertyListSerialization.propertyListToStream(writer, modelMap);
    }
    catch (PropertyListParserException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void doRevertToSaved() throws IOException, PropertyListParserException {
    resetModel();
    loadModelFromFile(_file.getLocation().toFile());
    parseModel();
  }

  public EOModelMap toModelMap() {
    EOModelMap modelMap = _modelMap.cloneModelMap();
    modelMap.setString("WebObjects Release", _woRelease, true);
    modelMap.setString("encoding", _encoding, true);
    EOModelMap variableMap = new EOModelMap();
    if (_variables != null) {
      variableMap.putAll(_variables);
    }
    for (DisplayGroup displayGroup : _displayGroups) {
      String displayGroupName = displayGroup.getName();
      EOModelMap displayGroupMap = displayGroup.toMap();
      variableMap.setMap(displayGroupName, displayGroupMap, true);
    }
    modelMap.setMap("variables", variableMap, true);
    return modelMap;
  }

  public boolean isDirty() {
    return _isDirty;
  }

  public void markAsDirty() {
    boolean oldIsDirty = _isDirty;
    _isDirty = true;
    _changes.firePropertyChange(IS_DIRTY, oldIsDirty, _isDirty);
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    _changes.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(final String name, final PropertyChangeListener listener) {
    _changes.addPropertyChangeListener(name, listener);
  }

  public void removePropertyChangeListener(final PropertyChangeListener listener) {
    _changes.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(final String name, final PropertyChangeListener listener) {
    _changes.removePropertyChangeListener(name, listener);
  }

  public void createDisplayGroup(final String name) {
    DisplayGroup displayGroup = new DisplayGroup(this);
    displayGroup.addPropertyChangeListener(_displayGroupListener);
    displayGroup.setName(name);
    _displayGroups.add(displayGroup);
    markAsDirty();
  }

  public void removeDisplayGroup(final DisplayGroup selection) {
    selection.removePropertyChangeListener(_displayGroupListener);
    _displayGroups.remove(selection);
    markAsDirty();
  }

  @Override
  public String toString() {
    OutputStream modelStream = new ByteArrayOutputStream();
    try {
      this.doSave(modelStream);
    }
    catch (Exception e) {
      return null;
    }
    return modelStream.toString();
  }

  public List<WodProblem> getProblems(IJavaProject javaProject, IType type, TypeCache typeCache, EOModelGroupCache modelCache) {
    final List<WodProblem> problems = new ArrayList<WodProblem>();

    // This was causing models to load when opening any component
    // even if your woo file is empty, and it doesn't APPEAR to actually
    // use this during this process.

//		EOModelGroupCache _modelCache = (EOModelGroupCache)modelCache;
//		EOModelGroup modelGroup = _modelCache.getModelGroup(javaProject);
//		if (modelGroup != null ) {
//			this.setModelGroup(modelGroup);
//		} else {
//			_modelCache.setModelGroup(javaProject, getModelGroup());
//		}

    try {
      this.parseModel();
    }
    catch (Throwable e) {
      problems.add(new WodProblem(e.getMessage(), null, 0, true));
      return problems;
    }
    if (_file == null) {
    	return problems;
    }
    try {
      String componentCharset = _file.getParent().getDefaultCharset();
      String encoding = WooUtils.encodingNameFromObjectiveC(this.getEncoding());
      if (!(encoding.equals(componentCharset))) {
        problems.add(new WodProblem("WOO Encoding type " + encoding + " doesn't match component " + componentCharset, null, 0, true));
      }

      if (_file.getParent().exists()) {
        for (IResource element : _file.getParent().members()) {
          if (element.getType() == IResource.FILE) {
            IFile file = (IFile) element;
            if (file.getFileExtension().matches("(xml|html|xhtml|wod)") && !file.getCharset().equals(encoding)) {
              problems.add(new WodProblem("WOO Encoding type " + encoding + " doesn't match " + file.getName() + " of " + file.getCharset(), null, 0, true));
            }
          }
        }
      }

    }
    catch (CoreException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    if (type == null) {
      if (getDisplayGroups().length != 0) {
        problems.add(new WodProblem("Display groups are defined for component " + _file.getParent().getName() + " but class was not found", null, 0, false));
      }
      return problems;
    }

    for (DisplayGroup displayGroup : getDisplayGroups()) {
      try {
        if (type != null) {

          // Validate WODisplayGroup variable is declared. 
          BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(displayGroup.getName(), type, type.getJavaProject(), WodParserCache.getTypeCache());
          if (!(bindingValueKeyPath.isValid() && !bindingValueKeyPath.isAmbiguous())) {
            //XXX Walk type hierarchy and check that is a WODisplayGroup
            problems.add(new WodProblem("WODisplayGroup " + displayGroup.getName() + " is configured but not declared in class", null, 0, false));
          }

          // Validate editing context
          bindingValueKeyPath = new BindingValueKeyPath(displayGroup.getEditingContext(), type, type.getJavaProject(), WodParserCache.getTypeCache());
          if (!(bindingValueKeyPath.isValid() && !bindingValueKeyPath.isAmbiguous())) {
            problems.add(new WodProblem("Editing context for display group " + displayGroup.getName() + " not found", null, 0, false));
          }
        }
      }
      catch (JavaModelException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    return problems;
  }

  public String getName() {
    return _file.getName();
  }

  public static void updateEncoding(IFile file, String charset) {
    WooModel model = new WooModel(file);
    String encoding = WooUtils.encodingNameFromObjectiveC(model.getEncoding());
    if (!encoding.equals(charset)) {
      try {
        model._modelMap.setString("encoding", charset, true);
        File _file = file.getLocation().toFile();
        try {
          FileOutputStream writer = new FileOutputStream(_file);
          PropertyListSerialization.propertyListToStream(writer, model._modelMap);
        }
        catch (PropertyListParserException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      catch (Throwable e) {
      }
    }
  }

  public IFile getFile() {
	return _file;
  }

  public void setFile(IFile file) {
	this._file = file;
  }

}
