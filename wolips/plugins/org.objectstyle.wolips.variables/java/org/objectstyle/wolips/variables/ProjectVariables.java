package org.objectstyle.wolips.variables;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.woenvironment.env.WOVariables;

public class ProjectVariables {
	private WOEnvironment _environment;

	public ProjectVariables(WOEnvironment environment) {
		_environment = environment;
	}

	public WOVariables getWOVariables() {
		return _environment.getWOVariables();
	}

	private IPath fixMissingSeparatorAfterDevice(String string) {
		IPath path;
		if (string != null && string.length() > 1 && string.charAt(1) == ':') {
			path = new Path(string.substring(2)).setDevice(string.substring(0, 2));
		} else if (string != null) {
			path = new Path(string);
		} else {
			path = null;
		}
		return path;
	}

	/**
	 * @return the path to the local root
	 */
	public IPath getLocalRoot() {
		return this.fixMissingSeparatorAfterDevice(_environment.getWOVariables().localRoot());
	}

	/**
	 * @return the path to the local root
	 */
	public IPath getLocalFrameworkPatb() {
		return this.fixMissingSeparatorAfterDevice(_environment.getWOVariables().localFrameworkPath());
	}

	/**
	 * @return the path to the system root
	 */
	public IPath getSystemRoot() {
		return this.fixMissingSeparatorAfterDevice(_environment.getWOVariables().systemRoot());
	}

	/**
	 * @return the path to the system root
	 */
	public IPath getSystemFrameworkPath() {
		return this.fixMissingSeparatorAfterDevice(_environment.getWOVariables().systemFrameworkPath());
	}

	/**
	 * @return the path to the network root
	 */
	public IPath getNetworkRoot() {
		return this.fixMissingSeparatorAfterDevice(_environment.getWOVariables().networkRoot());
	}

	/**
	 * @return the path to the network root
	 */
	public IPath getNetworkFrameworkPath() {
		return this.fixMissingSeparatorAfterDevice(_environment.getWOVariables().networkFrameworkPath());
	}

	/**
	 * @return the path to the user home
	 */
	public IPath getUserRoot() {
		return this.fixMissingSeparatorAfterDevice(this.getWOVariables().userRoot());
	}

	/**
	 * @return the path to the user home
	 */
	public IPath getUserFrameworkPath() {
		return this.fixMissingSeparatorAfterDevice(this.getWOVariables().userFrameworkPath());
	}

	/**
	 * @return the path to the reference api
	 */
	public IPath getReferenceApi() {
		String referenceApi = this.getWOVariables().referenceApi();
		if (referenceApi == null) {
			return null;
		}
		return this.fixMissingSeparatorAfterDevice(referenceApi);
	}

	/**
	 * @return the path to the reference api
	 */
	public String getReferenceApiAsJavaDocCompatibleString() {
		IPath referenceApi = this.getReferenceApi();
		if (referenceApi == null) {
			return null;
		}
		String referenceApiString = referenceApi.toOSString();
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") >= 0) {
			referenceApiString = referenceApiString.replace('\\', '/');
		}
		referenceApiString = "file://" + referenceApiString;
		return referenceApiString;
	}

	/**
	 * @return the path to external build root
	 */
	public IPath getExternalBuildRoot() {
		String root = this.getWOVariables().externalBuildRoot();
		if (root != null) {
			IPath result = this.fixMissingSeparatorAfterDevice(root);
			return result;
		}
		return null;
	}

	/**
	 * @return the path to external build root
	 */
	public IPath getExternalBuildFrameworkPath() {
		String root = this.getWOVariables().externalBuildFrameworkPath();
		if (root != null) {
			IPath result = this.fixMissingSeparatorAfterDevice(root);
			return result;
		}
		return null;
	}

	public String getProperty(String key) {
		return getWOVariables().getProperty(key);
	}

}
