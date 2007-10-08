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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelParserDataStructureFactory;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.PropertyListMap;
import org.objectstyle.wolips.eomodeler.core.utils.IPropertyChangeSource;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.eomodeler.eclipse.EclipseEOModelGroupFactory;
import org.objectstyle.wolips.locate.LocatePlugin;

public class WooModel implements IPropertyChangeSource {
	public static final String IS_DIRTY = "IS_DIRTY";

	public static final String DISPLAY_GROUP_NAME = "DISPLAY_GROUP_NAME";

	private IFile myFile;

	private boolean myIsDirty;

	private EOModelGroup myModelGroup;

	private String myEncoding = "NSMacOSRomanStringEncoding";

	private String myWoRelease = "WebObjects 5.0";

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

	public WooModel(final IFile file) throws WooModelException {
		myFile = file;
		if (!file.exists()) {
			String javaFileName = LocatePlugin.getDefault()
					.fileNameWithoutExtension(file);
			try {
				file.create(new ByteArrayInputStream(WooModel.blankContent(
						javaFileName).getBytes()), true,
						new NullProgressMonitor());
			} catch (CoreException e) {
				throw new WooModelException("Failed to create blank WOO file.",
						e);
			}
		}
		try {
			loadModelFromFile(file.getLocation().toFile());
		} catch (WooModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WooModel(final URL url) throws WooModelException {
		// TODO: Fix me
	}

	public WooModel(IEditorInput editorInput) {
		myFile = ((FileEditorInput) editorInput).getFile();
	}

	public static String blankContent(final String name) {
		// XXX Should use components default encoding charset
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		sb.append("    \"WebObjects Release\" =\"WebObjects 5.0\"\n");
		sb.append("     encoding = NSMacOSRomanStringEncoding;");
		sb.append("}\n");
		return sb.toString();
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

	public EOModelGroup getModelGroup() {
		if (myModelGroup == null) {
			myModelGroup = new EOModelGroup();
			EclipseEOModelGroupFactory eogroupFactory = new EclipseEOModelGroupFactory();
			Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
			try {
				eogroupFactory.loadModelGroup(myFile
						.getProject(),myModelGroup, failures, true,
						new NullProgressMonitor());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return myModelGroup;
	}

	private void loadModelFromFile(final File file) throws WooModelException {
		try {
			myModelMap = new EOModelMap((Map<?, ?>) PropertyListSerialization
					.propertyListFromFile(file,
							new EOModelParserDataStructureFactory()));
			parse();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new WooModelException("File not found "
					+ myFile.getProjectRelativePath());
		}
	}

	public void loadModelFromStream(final InputStream input) {
		myModelMap = new EOModelMap((Map<?, ?>) PropertyListSerialization
				.propertyListFromStream(input,
						new EOModelParserDataStructureFactory()));
		parse();
	}

	@SuppressWarnings("unchecked")
	private void parse() {
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();

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

	public void doSave() throws WooModelException {
		if (myFile == null) {
			throw new WooModelException(
					"You can not saveChanges to a WooModel that is not "
							+ "backed by a file.");
		}
		try {
			// XXX Validate data first
			File file = myFile.getLocation().toFile();
			FileOutputStream writer = new FileOutputStream(file);
			try {
				doSave(writer);
				myIsDirty = false;
			} finally {
				writer.close();
			}
			if (myFile != null) {
				try {
					myFile.refreshLocal(IResource.DEPTH_INFINITE,
							new NullProgressMonitor());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ioe) {
			throw new WooModelException("Failed to save changes to WOO file.",
					ioe);
		}
	}

	public void doSave(final OutputStream writer) throws WooModelException {
		// XXX Need to validate model before saving
		EOModelMap modelMap = toModelMap();
		PropertyListSerialization.propertyListToStream(writer, modelMap);
	}

	public void doRevertToSaved() throws WooModelException {
		loadModelFromFile(myFile.getLocation().toFile());
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
		displayGroup.setName(name);
		myDisplayGroups.add(displayGroup);
	}

	public void removeDisplayGroup(final DisplayGroup selection) {
		selection.removePropertyChangeListener(displayGroupListener);
		myDisplayGroups.remove(selection);
	}

	public String toString() {
		OutputStream modelStream = new ByteArrayOutputStream();
		try {
			this.doSave(modelStream);
		} catch (WooModelException e) {
			return null;
		}
		return modelStream.toString();
	}
}
