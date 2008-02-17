package org.objectstyle.wolips.wooeditor.model;

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
import org.objectstyle.wolips.bindings.woo.IEOModelGroupCache;
import org.objectstyle.wolips.bindings.woo.IWooModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelParserDataStructureFactory;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.model.PropertyListMap;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.wooeditor.utils.WooUtils;

public class WooModel implements IWooModel {
	public static final String IS_DIRTY = "IS_DIRTY";

	public static final String DISPLAY_GROUP_NAME = "DISPLAY_GROUP_NAME";
		
	public static final String ENCODING = "encoding";
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public static final String DEFAULT_WO_RELEASE = "WebObjects 5.0";

	private IFile myFile;

	private boolean myIsDirty;

	private EOModelGroup myModelGroup;

	private String myEncoding;

	private String myWoRelease = DEFAULT_WO_RELEASE;
	
	private EOModelMap myModelMap;

	private PropertyListMap<Object, Object> myVariables;

	private List<DisplayGroup> myDisplayGroups;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	private PropertyChangeListener displayGroupListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (DisplayGroup.NAME.equals(evt.getPropertyName())) {
				PropertyChangeEvent newEvent = new PropertyChangeEvent(evt
						.getSource(), DISPLAY_GROUP_NAME, evt.getOldValue(),
						evt.getNewValue());
				changes.firePropertyChange(newEvent);
			}
		}

	};
	
	public WooModel(final IFile file) {
		myFile = file;
		try {
			init();
		} catch (Throwable e) {}
	}

	public WooModel(final URL url) {
		// TODO: Fix me
	}
	
	public WooModel(final String contents) throws WooModelException {
		InputStream input = new ByteArrayInputStream(contents.getBytes());
		try {
			loadModelFromStream(input);
		} catch (Throwable e) {
			throw new WooModelException(e.getMessage());
		}
	}

	public WooModel(final InputStream input) throws WooModelException {
		try {
			loadModelFromStream(input);
		} catch (Throwable e) {
			throw new WooModelException(e.getMessage());
		}
	}

	
	public WooModel(IEditorInput editorInput) {
		if (editorInput instanceof IFileEditorInput) {
			myFile = ((IFileEditorInput)editorInput).getFile();
		}
		try {
			init();
		} catch (Throwable e) {}
	}
	
	private void init() throws IOException, PropertyListParserException {
		if (myFile == null || !myFile.exists()) {
			loadModelFromStream(new ByteArrayInputStream(blankContent().getBytes()));

		} else {
			loadModelFromFile(myFile.getLocation().toFile());
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
		myEncoding = null;
		myWoRelease = DEFAULT_WO_RELEASE;
		myVariables = null;
		myModelMap = null;
		myDisplayGroups = null;
	}
	
	public String getLocation() {
		String location;
		if (myFile != null) {
			location = myFile.getFullPath().toString();
		} else {
			location = null;
		}
		return location;
	}

	public DisplayGroup[] getDisplayGroups() {
		if (myDisplayGroups != null) {
			return myDisplayGroups.toArray(new DisplayGroup[] {});
		}
		return new DisplayGroup[0];
	}

	public String getEncoding() {
		if (myEncoding == null) {
			if (myModelMap != null && myModelMap.containsKey("encoding")) {
				myEncoding = myModelMap.getString("encoding", true);
			} else if (myFile != null && myFile.getParent().exists()) {
				try {
					myEncoding = myFile.getParent().getDefaultCharset();
				} catch (CoreException e) { }
			}
		}
		if (myEncoding == null) return DEFAULT_ENCODING;
		return myEncoding;
	}
	
	public EOModelGroup getModelGroup() {
		if (myModelGroup == null) {
			myModelGroup = new EOModelGroup();
			if (myFile == null)
				return myModelGroup;
			
			Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			try {
				IEOModelGroupFactory.Utility.loadModelGroup(
						myFile.getProject(), myModelGroup, failures, true,
						new URL("file://"), new NullProgressMonitor());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return myModelGroup;
	}
	
	public void setEncoding(String encoding) {
		String oldEncoding = myEncoding;
		myEncoding = encoding;
		changes.firePropertyChange(ENCODING, oldEncoding, myEncoding);
	}

	private void loadModelFromFile(final File file) throws IOException, PropertyListParserException {
		myModelMap = new EOModelMap((Map<?, ?>) PropertyListSerialization
				.propertyListFromFile(file,
						new EOModelParserDataStructureFactory()));
	}

	public void loadModelFromStream(final InputStream input) throws IOException, PropertyListParserException {
		myModelMap = new EOModelMap((Map<?, ?>) PropertyListSerialization
				.propertyListFromStream(input,
						new EOModelParserDataStructureFactory()));
	}

	@SuppressWarnings("unchecked")
	public void parseModel() {
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();

		if (myModelMap == null)
			return;
		
		if (myModelMap.containsKey("encoding")) {
			myEncoding = myModelMap.getString("encoding", true);
		}
		if (myModelMap.containsKey("WebObjects Release")) {
			myWoRelease = myModelMap.getString("WebObjects Release", true);
		}

		myVariables = new PropertyListMap<Object, Object>();
		myDisplayGroups = new ArrayList<DisplayGroup>();

		Map<?, ?> variables = myModelMap.getMap("variables");
		if (variables != null) {
			EOModelMap variableMap = new EOModelMap(variables);
			Set<Map.Entry<String, Object>> variableEntries = variableMap
					.entrySet();
			for (Map.Entry<String, Object> entry : variableEntries) {
				if (entry.getValue() instanceof Map) {
					EOModelMap entryMap = new EOModelMap((Map<?, ?>) entry
							.getValue());
					String className = entryMap.getString("class", true);
					if ("WODisplayGroup".equals(className)) {
						DisplayGroup displayGroup = new DisplayGroup(this);
						displayGroup.setName(entry.getKey());
						displayGroup.loadFromMap(entryMap, failures);
						myDisplayGroups.add(displayGroup);
						displayGroup
								.addPropertyChangeListener(displayGroupListener);
						continue;
					}
				}
				myVariables.put(entry.getKey(), entry.getValue());
			}
		}

		myIsDirty = false;
	}

	public void doSave() throws IOException {
		if (myFile == null) {
			throw new IOException(
					"You can not save changes to a WooModel that is not "
							+ "backed by a file.");
		}
		File file = myFile.getLocation().toFile();
		FileOutputStream writer = new FileOutputStream(file);
		try {
			doSave(writer);
			myIsDirty = false;
			myFile.refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}

	}

	public void doSave(final OutputStream writer) throws IOException {
		// XXX Need to validate model before saving
		EOModelMap modelMap = toModelMap();
		try {
			PropertyListSerialization.propertyListToStream(writer, modelMap);
		} catch (PropertyListParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doRevertToSaved() throws IOException, PropertyListParserException {
		resetModel();
		loadModelFromFile(myFile.getLocation().toFile());
		parseModel();
	}

	public EOModelMap toModelMap() {
		EOModelMap modelMap = myModelMap.cloneModelMap();
		modelMap.setString("WebObjects Release", myWoRelease, true);
		modelMap.setString("encoding", myEncoding, true);
		EOModelMap variableMap = new EOModelMap();
		if (myVariables != null) {
			variableMap.putAll(myVariables);
		}
		for (DisplayGroup displayGroup : myDisplayGroups) {
			String displayGroupName = displayGroup.getName();
			EOModelMap displayGroupMap = displayGroup.toMap();
			variableMap.setMap(displayGroupName, displayGroupMap, true);
		}
		modelMap.setMap("variables", variableMap, true);
		return modelMap;
	}

	public boolean isDirty() {
		return myIsDirty;
	}

	public void markAsDirty() {
		boolean oldIsDirty = myIsDirty;
		myIsDirty = true;
		changes.firePropertyChange(IS_DIRTY, oldIsDirty, myIsDirty);
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(final String name,
			final PropertyChangeListener listener) {
		changes.addPropertyChangeListener(name, listener);
	}

	public void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(final String name,
			final PropertyChangeListener listener) {
		changes.removePropertyChangeListener(name, listener);
	}

	public void createDisplayGroup(final String name) {
		DisplayGroup displayGroup = new DisplayGroup(this);
		displayGroup.addPropertyChangeListener(displayGroupListener);
		displayGroup.setName(name);
		myDisplayGroups.add(displayGroup);
		markAsDirty();
	}

	public void removeDisplayGroup(final DisplayGroup selection) {
		selection.removePropertyChangeListener(displayGroupListener);
		myDisplayGroups.remove(selection);
		markAsDirty();
	}

	@Override
  public String toString() {
		OutputStream modelStream = new ByteArrayOutputStream();
		try {
			this.doSave(modelStream);
		} catch (Exception e) {
			return null;
		}
		return modelStream.toString();
	}

	public List<WodProblem> getProblems(IJavaProject javaProject, IType type,
			TypeCache typeCache, IEOModelGroupCache modelCache) {
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
		} catch (Throwable e) {
			problems.add(new WodProblem(e.getMessage(), null, 0, true));
			return problems;
		}
		try {
			String componentCharset = myFile.getParent().getDefaultCharset();
			String encoding = WooUtils.encodingNameFromObjectiveC(this.getEncoding());
			if (!(encoding.equals(componentCharset))) {
				problems.add(new WodProblem("WOO Encoding type " +
						encoding + " doesn't match component " +
						componentCharset, null, 0, true));
			}
			
			if (myFile.getParent().exists()) {
				for(IResource element : myFile.getParent().members()) {
					if (element.getType() == IResource.FILE) {
						IFile file = (IFile) element;
						if (file.getFileExtension().matches("(xml|html|xhtml|wod)")
								&& !file.getCharset().equals(encoding)) {
							problems.add(new WodProblem("WOO Encoding type " +
									encoding + " doesn't match " + file.getName() 
									+ " of "+ file.getCharset(), null, 0, true));
						}
					}
				}
			}
			
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (type == null) {
			if (getDisplayGroups().length != 0) {
				problems.add(new WodProblem("Display groups are defined for component " 
						+ myFile.getParent().getName()
						+ " but class was not found", null, 0, false));
			}
			return problems;
		}
		
		for (DisplayGroup displayGroup : getDisplayGroups()) {
			try {
				if (type != null) {
					
					// Validate WODisplayGroup variable is declared. 
					BindingValueKeyPath bindingValueKeyPath = new BindingValueKeyPath(displayGroup.getName(), type);
					if (!(bindingValueKeyPath.isValid() && !bindingValueKeyPath.isAmbiguous())) {
						//XXX Walk type hierarchy and check that is a WODisplayGroup
						problems.add(new WodProblem("WODisplayGroup " + displayGroup.getName() 
								+ " is configured but not declared in class", null, 0, false));
					}
					
					// Validate editing context
					bindingValueKeyPath = new BindingValueKeyPath(displayGroup.getEditingContext(), type);
					if (!(bindingValueKeyPath.isValid() && !bindingValueKeyPath.isAmbiguous())) {
						problems.add(new WodProblem("Editing context for display group " 
								+ displayGroup.getName() + " not found", null, 0, false));
					}
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		return problems;
	}

	private void setModelGroup(EOModelGroup modelGroup) {
		myModelGroup = modelGroup;
	}

	public String getName() {
		return myFile.getName();
	}

	public static void updateEncoding(IFile file, String charset) {
		WooModel model = new WooModel(file);
		String encoding = WooUtils.encodingNameFromObjectiveC(model.getEncoding());
		if (!encoding.equals(charset)) {
			try {
				model.myModelMap.setString("encoding", charset, true);
				File _file = file.getLocation().toFile();
				try {
					FileOutputStream writer = new FileOutputStream(_file);
					PropertyListSerialization.propertyListToStream(writer, model.myModelMap);
				} catch (PropertyListParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Throwable e) { }
		}
	}

}
