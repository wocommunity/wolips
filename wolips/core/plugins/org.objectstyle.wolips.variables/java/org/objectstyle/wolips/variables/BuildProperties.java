package org.objectstyle.wolips.variables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.woenvironment.frameworks.Root;
import org.objectstyle.woenvironment.frameworks.Version;
import org.objectstyle.woenvironment.plist.ToHellWithProperties;

public class BuildProperties {
	private IProject _project;

	private Properties _properties;

	private boolean _dirty;

	private long _version;

	public BuildProperties(IProject project) {
		_project = project;
		_version = -1;
		load();
	}

	public boolean isDirty() {
		return _dirty;
	}

	public IProject getProject() {
		return _project;
	}

	public IFile getBuildPropertiesEclipseFile() {
		IFile file = _project.getFile("build.properties");
		return file;
	}

	public File getBuildPropertiesFile() {
		File file = getBuildPropertiesEclipseFile().getLocation().toFile();
		return file;
	}

	public long getModificationStamp() {
		File file = getBuildPropertiesFile();
		if (_version == -1 && file.exists()) {
			_version = file.lastModified();
		}
		return _version;
	}

	public synchronized void setProperties(Properties properties) {
		if (!_properties.equals(properties)) {
			_properties = properties;
			_dirty = true;
		}
	}

	public synchronized Properties getProperties() {
		Properties cloneProperties = new Properties();
		cloneProperties.putAll(_properties);
		return cloneProperties;
	}

	public synchronized boolean getBoolean(String key, boolean defaultValue) {
		String strValue = get(key);
		boolean value;
		if (strValue == null) {
			value = defaultValue;
		}
		else {
			value = "true".equalsIgnoreCase(strValue);
		}
		return value;
	}

	public synchronized String get(String key) {
		return get(key, null);
	}

	public synchronized String get(String key, String defaultValue) {
		String value = _properties.getProperty(key, defaultValue);
		return value;
	}

	public synchronized void remove(String key) {
		put(key, null);
	}

	public synchronized void put(String key, boolean value) {
		put(key, Boolean.valueOf(value).toString());
	}

	public synchronized void put(String key, String value) {
		if (value == null) {
			if (_properties.containsKey(key)) {
				_properties.remove(key);
				_dirty = true;
			}
		}
		else {
			String oldValue = get(key);
			if (!value.equals(oldValue)) {
				_properties.setProperty(key, value);
				_dirty = true;
			}
		}
	}

	protected void load() {
		try {
			boolean dirty;
			
			Properties properties = new Properties();
			File file = getBuildPropertiesFile();
			if (file.exists()) {
				InputStream inputStream = new FileInputStream(file);
				try {
					properties.load(inputStream);
				}
				finally {
					inputStream.close();
				}
				dirty = false;
			}
			else {
				dirty = true;
			}
			
			synchronized (this) {
				_dirty = dirty;
				_properties = properties;
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to load the build properties for the project '" + _project + "'.", e);
		}
	}

	public synchronized void save() throws CoreException, IOException {
		Properties properties = new ToHellWithProperties();
		properties.putAll(_properties);
		
		if (!_dirty) {
			return;
		}

		File file = getBuildPropertiesFile();
		FileOutputStream fos = new FileOutputStream(file);
		try {
			properties.store(fos, null);
		}
		finally {
			fos.close();
		}

		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				getBuildPropertiesEclipseFile().refreshLocal(IResource.DEPTH_ONE, monitor);
			}
		}, null);
		
		_dirty = false;
	}

	public String getName() {
		String projectName = get("project.name");
		// MS: compatibility with old build.properties
		if (projectName == null || projectName.length() == 0) {
			projectName = get("framework.name");
		}
		if (projectName == null || projectName.length() == 0) {
			projectName = _project.getName();
		}
		return projectName;
	}

	public void setName(String name) {
		put("project.name", name);
		put("project.name.lowercase", name.toLowerCase());
	}

	public boolean getWebXML() {
		return getBoolean("webXML", false);
	}

	public void setWebXML(boolean webXML) {
		put("webXML", webXML);
	}

	public boolean isServletDeployment() {
		return getBoolean("servletDeployment", false);
	}

	public void setServletDeployment(boolean servletDeployment) {
		if (servletDeployment) {
			put("servletDeployment", servletDeployment);
		}
		else {
			remove("servletDeployment");
		}
	}

	public String getWebXML_CustomContent(boolean convertNullValueToEmptyString) {
		return get("webXML_CustomContent", convertNullValueToEmptyString ? "" : null);
	}

	/**
	 * @param webXML_CustomContent
	 *            webxml custom content
	 */
	public void setWebXML_CustomContent(String webXML_CustomContent) {
		put("webXML_CustomContent", webXML_CustomContent);
	}

	public String getEOGeneratorArgs(boolean convertNullValueToEmptyString) {
		return get("eogeneratorArgs", convertNullValueToEmptyString ? "" : null);
	}

	public void setEOGeneratorArgs(String eogeneratorArgs) {
		put("eogeneratorArgs", eogeneratorArgs);
	}

	public String getPrincipalClass() {
		return getPrincipalClass(false);
	}
	
	/**
	 * @param convertNullValueToEmptyString
	 * @return principalClass.
	 */
	public String getPrincipalClass(boolean convertNullValueToEmptyString) {
		return get("principalClass", convertNullValueToEmptyString ? "" : null);
	}

	/**
	 * @param principalClass
	 *            the principalClass for the Info.plist
	 */
	public void setPrincipalClass(String principalClass) {
		put("principalClass", (principalClass == null) ? "" : principalClass);
	}

	public boolean isEmbed(Root root) {
		String shortName = root.getShortName();
		return getBoolean("embed." + shortName, false);
	}

	public void setEmbed(Root root, boolean embed) {
		String shortName = root.getShortName();
		if (!embed) {
			remove("embed." + shortName);
		}
		else {
			put("embed." + shortName, embed);
		}
	}

	public String getCustomInfoPListContent() {
		return getCustomInfoPListContent(false);
	}

	/**
	 * @param convertNullValueToEmptyString
	 * @return The CustomContent for the Info.plist
	 */
	public String getCustomInfoPListContent(boolean convertNullValueToEmptyString) {
		return get("customInfoPListContent", convertNullValueToEmptyString ? "" : null);
	}

	/**
	 * @param customInfoPListContent
	 *            The CustomContent for the Info.plist
	 */
	public void setCustomInfoPListContent(String customInfoPListContent) {
		put("customInfoPListContent", (customInfoPListContent == null) ? "" : customInfoPListContent);
	}

	public String getEOAdaptorClassName() {
		return getEOAdaptorClassName(false);
	}
	
	/**
	 * @param convertNullValueToEmptyString
	 * @return The EOAdaptorClassName for the Info.plist
	 */
	public String getEOAdaptorClassName(boolean convertNullValueToEmptyString) {
		return get("eoAdaptorClassName", convertNullValueToEmptyString ? "" : null);
	}

	/**
	 * @param eoAdaptorClassName
	 *            the eoadaptorclassname for the Info.plist
	 */
	public void setEOAdaptorClassName(String eoAdaptorClassName) {
		put("eoAdaptorClassName", (eoAdaptorClassName == null) ? "" : eoAdaptorClassName);
	}

	public String getProjectFrameworkFolder() {
		return get("projectFrameworkFolder");
	}

	public void setProjectFrameworkFolder(String projectFrameworkFolder) {
		put("projectFrameworkFolder", projectFrameworkFolder);
	}

	public void setJavaClient(boolean javaClient) {
		if (javaClient) {
			put("javaClient", javaClient);
		}
		else {
			remove("javaClient");
		}
	}

	public boolean isJavaClient() {
		return getBoolean("javaClient", false);
	}

	public void setJavaWebStart(boolean javaWebStart) {
		if (javaWebStart) {
			put("javaWebStart", javaWebStart);
		}
		else {
			remove("javaWebStart");
		}
	}

	public boolean isJavaWebStart() {
		return getBoolean("javaWebStart", false);
	}

	public boolean hasValidProjectType() {
		String projectType = get("project.type");
		return "application".equals(projectType) || "framework".equals(projectType);
	}

	public boolean isFramework() {
		boolean isFramework = false;
		String projectType = get("project.type");
		if (projectType != null) {
			isFramework = "framework".equals(projectType);
		}
		else {
			// MS: compatibility with old build.properties
			String frameworkName = get("framework.name");
			if (frameworkName != null) {
				isFramework = true;
			}
		}
		return isFramework;
	}

	public void setFramework(boolean framework) {
		if (framework) {
			put("project.type", "framework");
		}
		else {
			put("project.type", "application");
		}
	}
	
	public String getBundleType() {
		return isFramework() ? "FMWK" : "APPL";
	}
	
	private boolean _defaultsInitialized;

	private Version _woVersionDefault;

	private Version _versionDefault;

	private String _inlineBindingPrefixDefault;

	private String _inlineBindingSuffixDefault;

	private boolean _wellFormedTemplateRequiredDefault;

	public void _copyDefaultsFrom(BuildProperties props) {
		if (props._defaultsInitialized) {
			_woVersionDefault = props._woVersionDefault;
			_inlineBindingPrefixDefault = props._inlineBindingPrefixDefault;
			_inlineBindingSuffixDefault = props._inlineBindingSuffixDefault;
			_wellFormedTemplateRequiredDefault = props._wellFormedTemplateRequiredDefault;
			_defaultsInitialized = true;
		}
	}

	protected synchronized void ensureDefaultsInitialized() {
		if (!_defaultsInitialized) {
			_defaultsInitialized = true;
			_woVersionDefault = new Version(VariablesPlugin.getDefault().getGlobalVariables().getString("wo.version", "5.3.3"));
			_inlineBindingPrefixDefault = VariablesPlugin.getDefault().getGlobalVariables().getString("component.inlineBindingPrefix", "[");
			_inlineBindingSuffixDefault = VariablesPlugin.getDefault().getGlobalVariables().getString("component.inlineBindingSuffix", "]");
			// MS: This is pretty hacky -- Technically this plugin doesn't depend on the Bindings preference plugin, and I don't want to add it because it brings in a bunch
			// of JDT dependencies that we don't want here, but I can't move the preferences out of the plugin easily without changing their key name. 
			_wellFormedTemplateRequiredDefault = VariablesPlugin.getDefault().getGlobalVariables().getBoolean("component.wellFormedTemplateRequired", "yes".equals(Platform.getPreferencesService().getString("org.objectstyle.wolips.bindings", "WellFormedTemplate", null, null)));
			BuildPropertiesAdapterFactory.initializeBuildPropertiesDefaults(this);
		}
	}

	public void setWOVersionDefault(Version woVersionDefault) {
		_woVersionDefault = woVersionDefault;
	}

	public Version getWOVersionDefault() {
		ensureDefaultsInitialized();
		return _woVersionDefault;
	}

	public void setVersionDefault(Version versionDefault) {
		_versionDefault = versionDefault;
	}
	
	public Version getVersionDefault() {
		ensureDefaultsInitialized();
		return _versionDefault;
	}
	
	public void setVersion(Version version) {
		if (version != null) {
			put("version", version.getVersionStr());
		}
		else {
			remove("version");
		}
	}

	public Version getVersion() {
		Version versionDefault = getVersionDefault();
		return new Version(get("version", versionDefault == null ? null : versionDefault.getVersionStr()));
	}
	
	public void setWOVersion(Version woVersion) {
		if (woVersion != null) {
			put("wo.version", woVersion.getVersionStr());
		}
		else {
			remove("wo.version");
		}
	}

	public Version getWOVersion() {
		Version woVersionDefault = getWOVersionDefault();
		return new Version(get("wo.version", woVersionDefault == null ? null : woVersionDefault.getVersionStr()));
	}

	public void setInlineBindingPrefixDefault(String inlineBindingPrefixDefault) {
		_inlineBindingPrefixDefault = inlineBindingPrefixDefault;
	}

	public String getInlineBindingPrefixDefault() {
		ensureDefaultsInitialized();
		return _inlineBindingPrefixDefault;
	}

	public void setInlineBindingPrefix(String inlineBindingPrefix) {
		if (inlineBindingPrefix != null) {
			put("component.inlineBindingPrefix", inlineBindingPrefix);
		}
		else {
			remove("component.inlineBindingPrefix");
		}
	}

	public String getInlineBindingPrefix() {
		return get("component.inlineBindingPrefix", getInlineBindingPrefixDefault());
	}

	public void setInlineBindingSuffixDefault(String inlineBindingSuffixDefault) {
		_inlineBindingSuffixDefault = inlineBindingSuffixDefault;
	}

	public String getInlineBindingSuffixDefault() {
		ensureDefaultsInitialized();
		return _inlineBindingSuffixDefault;
	}

	public void setInlineBindingSuffix(String inlineBindingSuffix) {
		if (inlineBindingSuffix != null) {
			put("component.inlineBindingSuffix", inlineBindingSuffix);
		}
		else {
			remove("component.inlineBindingSuffix");
		}
	}

	public String getInlineBindingSuffix() {
		return get("component.inlineBindingSuffix", getInlineBindingSuffixDefault());
	}

	public void setWellFormedTemplateRequiredDefault(boolean wellFormedTemplateRequiredDefault) {
		_wellFormedTemplateRequiredDefault = wellFormedTemplateRequiredDefault;
	}

	public boolean getWellFormedTemplateRequiredDefault() {
		ensureDefaultsInitialized();
		return _wellFormedTemplateRequiredDefault;
	}

	public void setWellFormedTemplateRequired(Boolean wellFormedTemplateRequired) {
		if (wellFormedTemplateRequired == null) {
			remove("component.wellFormedTemplateRequired");
		}
		else {
			put("component.wellFormedTemplateRequired", wellFormedTemplateRequired.booleanValue());
		}
	}

	public boolean isWellFormedTemplateRequired() {
		boolean wellFormedTemplateRequired = getBoolean("component.wellFormedTemplateRequired", getWellFormedTemplateRequiredDefault());
		return wellFormedTemplateRequired;
	}

	public boolean isBuildFolderRequired() {
		return getWOVersion().isAtLeastVersion(5, 6);
	}
}
