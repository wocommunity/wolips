package org.objectstyle.wolips.variables;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.woenvironment.frameworks.Root;

public class BuildProperties {
	private IProject _project;

	private Properties _properties;

	private boolean _dirty;

	public BuildProperties(IProject project) {
		_project = project;
	}

	public IFile getBuildPropertiesFile() {
		IFile file = _project.getFile("build.properties");
		return file;
	}

	public synchronized long getModificationStamp() {
		long version;
		IFile file = getBuildPropertiesFile();
		if (file.exists()) {
			version = file.getModificationStamp();
		}
		else {
			version = -1;
		}
		return version;
	}

	public synchronized void setProperties(Properties properties) {
		ensureLoaded();
		if (!_properties.equals(properties)) {
			_properties = properties;
			_dirty = true;
		}
	}

	public synchronized Properties getProperties() {
		ensureLoaded();
		Properties cloneProperties = new Properties();
		cloneProperties.putAll(_properties);
		return cloneProperties;
	}

	public synchronized boolean getBoolean(String key, boolean defaultValue) {
		String strValue = get(key);
		boolean value;
		if (strValue == null) {
			value = defaultValue;
		} else {
			value = "true".equalsIgnoreCase(strValue);
		}
		return value;
	}

	public synchronized String get(String key) {
		return get(key, null);
	}

	public synchronized String get(String key, String defaultValue) {
		ensureLoaded();
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
		ensureLoaded();
		if (value == null) {
			if (_properties.containsKey(key)) {
				_properties.remove(key);
				_dirty = true;
			}
		} else {
			String oldValue = get(key);
			if (!value.equals(oldValue)) {
				_properties.setProperty(key, value);
				_dirty = true;
			}
		}
	}

	public synchronized void revert() {
		try {
			Properties properties = new Properties();
			IFile file = getBuildPropertiesFile();
			if (file.exists()) {
				file.refreshLocal(IResource.DEPTH_INFINITE, null);
				InputStream inputStream = file.getContents();
				try {
					properties.load(inputStream);
				} finally {
					inputStream.close();
				}
				_dirty = false;
			} else {
				_dirty = true;
			}
			_properties = properties;
		} catch (Exception e) {
			throw new RuntimeException("Failed to load the build properties for the project '" + _project + "'.", e);
		}
	}

	protected synchronized void ensureLoaded() {
		if (_properties == null) {
			revert();
		}
	}

	public synchronized void save() throws CoreException, IOException {
		if (!_dirty) {
			return;
		}

		ByteArrayOutputStream propertiesOutputStream = new ByteArrayOutputStream();
		_properties.store(propertiesOutputStream, null);
		ByteArrayInputStream propertiesInputStream = new ByteArrayInputStream(propertiesOutputStream.toByteArray());

		IFile file = getBuildPropertiesFile();
		if (file.exists()) {
			file.refreshLocal(IResource.DEPTH_INFINITE, null);
			file.setContents(propertiesInputStream, true, true, new NullProgressMonitor());
		} else {
			file.create(propertiesInputStream, false, null);
		}

		_dirty = false;
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
		} else {
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
		} else {
			put("embed." + shortName, embed);
		}
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
		} else {
			remove("javaClient");
		}
	}

	public boolean isJavaClient() {
		return getBoolean("javaClient", false);
	}

	public void setJavaWebStart(boolean javaWebStart) {
		if (javaWebStart) {
			put("javaWebStart", javaWebStart);
		} else {
			remove("javaWebStart");
		}
	}

	public boolean isJavaWebStart() {
		return getBoolean("javaWebStart", false);
	}
}
