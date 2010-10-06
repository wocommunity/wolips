package org.objectstyle.woenvironment.frameworks;

/**
 * Encapsulates a version number along with some convenient methods of testing
 * the major/minor/etc. This supports version of the format x.y.z-something
 * 
 * @author mschrag
 */
public class Version {
	private String _versionStr;

	/**
	 * Constructs a new Version.
	 * 
	 * @param versionStr the string form of the version
	 */
	public Version(String versionStr) {
		_versionStr = versionStr;
		if (_versionStr == null) {
			_versionStr = "";
		}
	}

	/**
	 * Returns true if this version is not specified.
	 * 
	 * @return true if this version is not specified
	 */
	public boolean isUndefined() {
		return _versionStr == null || _versionStr.length() == 0;
	}
	
	/**
	 * Returns the original version string.
	 * 
	 * @return the original version string
	 */
	public String getVersionStr() {
		return _versionStr;
	}

	/**
	 * Returns the version split on dots.
	 * 
	 * @return the version split on dots
	 */
	public String[] getVersionComponents() {
		String[] versionComponents = _versionStr.split("[.-]");
		return versionComponents;
	}

	/**
	 * Returns true if the version is at least minimumMajor.minimumMinor
	 * 
	 * @param minimumMajor the minimum major version required
	 * @param minimumMinor the minimum minor version required
	 * @return true if the version is at least the specified value
	 */
	public boolean isAtLeastVersion(int minimumMajor, int minimumMinor) {
		int major = getMajorVersion();
		int minor = getMinorVersion();
		boolean ok;
		if (major > minimumMajor) {
			ok = true;
		}
		else if (major == minimumMajor) {
			if (minor >= minimumMinor) {
				ok = true;
			}
			else {
				ok = false;
			}
		}
		else {
			ok = false;
		}
		return ok;
	}

	/**
	 * Returns x from x.y.z-something.
	 * 
	 * @return x from x.y.z-something
	 */
	public int getMajorVersion() {
		int majorVersion = 0;
		String[] versionComponents = getVersionComponents();
		if (versionComponents.length >= 1) {
			String majorVersionStr = versionComponents[0];
			if (majorVersionStr != null && majorVersionStr.length() > 0) {
				try {
					majorVersion = Integer.parseInt(versionComponents[0]);
				}
				catch (NumberFormatException e) {
					majorVersion = 0;
				}
			}
		}
		return majorVersion;
	}

	/**
	 * Returns y from x.y.z-something.
	 * 
	 * @return y from x.y.z-something
	 */
	public int getMinorVersion() {
		String[] versionComponents = getVersionComponents();
		int minorVersion = 0;
		if (versionComponents.length >= 2) {
			try {
				minorVersion = Integer.parseInt(versionComponents[1]);
			}
			catch (NumberFormatException e) {
				minorVersion = 0;
			}
		}
		return minorVersion;
	}

	/**
	 * Returns y-something from x.y.z-something.
	 * 
	 * @return y-something from x.y.z-something
	 */
	public String getPatchVersion() {
		String patchVersion;
		String[] versionComponents = getVersionComponents();
		if (versionComponents.length >= 3) {
			patchVersion = versionComponents[2];
		}
		else if (getMajorVersion() == 0) {
			patchVersion = _versionStr;
		}
		else {
			patchVersion = "0";
		}
		return patchVersion;
	}
	
	public String getBundleVersion() {
		return getVersionStr();
	}
	
	public String getBundleShortVersionString() {
		return getMajorVersion() + "." + getMinorVersion();
	}

	public String toString() {
		return "[Version: " + _versionStr + "]";
	}
}
